package com.tutor.project.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tutor.project.dto.request.*;
import com.tutor.project.dto.response.IntrospectResponse;
import com.tutor.project.dto.response.LoginResponse;
import com.tutor.project.entity.InvalidToken;
import com.tutor.project.entity.Role;
import com.tutor.project.entity.User;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.repository.InvalidationToken;
import com.tutor.project.repository.RoleRepository;
import com.tutor.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.valid-duration}")
    long EXPIRETIME;
    @NonFinal
    @Value("${jwt.refresh-duration}")
    long REFRESHTIME;
    @NonFinal
    @Value("${jwt.signerKey}")
    String SECRETKEY;

    InvalidationToken invalidationToken;
    UserRepository userRepository;
    RoleRepository roleRepository;
    public String addRole(RoleCreationRequest request){
        roleRepository.save(Role.builder()
                        .name(request.getName())
                .build());
        return "ok";
    }
    public LoginResponse login(LoginRequest request) throws KeyLengthException {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder= new BCryptPasswordEncoder(8);
        boolean check= passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!check){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return LoginResponse.builder().token(generateToken(user.getId())).build();
    }
    public void logout(LogoutRequest request) {
        try{
            var check=verify(request.getToken(),true);
            invalidationToken.save(InvalidToken.builder()
                    .id(check.getJWTClaimsSet().getJWTID())
                    .expiryTime(check.getJWTClaimsSet().getExpirationTime())
                    .build());
        }catch (ParseException | JOSEException e){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

    }
    public IntrospectResponse introspect(IntrospectRequest request){
        boolean isValid=true;
        try{
            var check=verify(request.getToken(),false);
        }catch (JOSEException | java.text.ParseException  e ){
            isValid=false;
        }
        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }
    public LoginResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signedJWT= verify(request.getToken(),true);
        invalidationToken.save(InvalidToken.builder()
                .id(signedJWT.getJWTClaimsSet().getJWTID())
                .build());
        return LoginResponse.builder()
                .token(generateToken(signedJWT.getJWTClaimsSet().getSubject()))
                .build();
    }
    public SignedJWT verify(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier= new MACVerifier(SECRETKEY.getBytes());
        SignedJWT signedJWT= SignedJWT.parse(token);
        Date expire=isRefresh
                ?new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHTIME,ChronoUnit.SECONDS).toEpochMilli())
                :signedJWT.getJWTClaimsSet().getExpirationTime();
        var check= signedJWT.verify(jwsVerifier);
        if(!expire.after(new Date(Instant.now().toEpochMilli())) || !check){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if(invalidationToken.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }
    public String generateToken(String userId) throws KeyLengthException {
        var user=userRepository.findByIdWithRoles(userId)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        JWSHeader header= new JWSHeader(JWSAlgorithm.HS512);
        log.info("expireTime: {}",EXPIRETIME);
        JWTClaimsSet claimsSet= new JWTClaimsSet.Builder()
                .subject(userId)
                .issuer("tutor.com")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(EXPIRETIME,ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope",buildScope(user))
                .build();
        Payload payload=new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject= new JWSObject(header,payload);
        try {
            jwsObject.sign(new MACSigner(SECRETKEY.getBytes()));
            return jwsObject.serialize();
        }catch(JOSEException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
    public String buildScope(User user ){
        StringJoiner stringJoiner= new StringJoiner(" ");
        log.info("???");
        log.info("size: {}",user.getRoles().size());
        user.getRoles().forEach(role -> stringJoiner.add("ROLE_"+role.getName()));
        return stringJoiner.toString();
    }
}
