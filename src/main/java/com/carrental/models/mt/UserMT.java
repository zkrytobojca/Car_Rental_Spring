package com.carrental.models.mt;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMT {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;

    @Override
    public String toString() {
        return "UserMT{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
