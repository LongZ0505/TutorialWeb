package com.tutor.project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String password;
    String email;
    String fullName;
    String phone;
    String avatar;
    @ManyToMany
    @JoinTable(name = "user_role",
    joinColumns =@JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name= "role_id"))
    Set<Role> roles=new HashSet<>();
}
