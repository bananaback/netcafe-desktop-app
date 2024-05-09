package dev.hideftbanana.netcafejavafxapp.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String phoneNumber;
    private String email;
    private String identityNumber;
    private Double balance;

}
