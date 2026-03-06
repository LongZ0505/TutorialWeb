package com.tutor.project.configuration;

import com.nimbusds.jose.Algorithm;
import com.tutor.project.dto.request.IntrospectRequest;
import com.tutor.project.dto.response.IntrospectResponse;
import com.tutor.project.exception.AppException;
import com.tutor.project.exception.ErrorCode;
import com.tutor.project.service.AuthenticationService;
import jakarta.websocket.Decoder;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Configuration
public class CustomDecoder implements JwtDecoder {
    @NonFinal
    @Value("${jwt.signerKey}")
    String SECRETKEY;
    @Autowired
    AuthenticationService authenticationService;
    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        var introspectResponse = authenticationService
                .introspect(IntrospectRequest.builder().token(token).build());
        if (!introspectResponse.isValid()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRETKEY.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
