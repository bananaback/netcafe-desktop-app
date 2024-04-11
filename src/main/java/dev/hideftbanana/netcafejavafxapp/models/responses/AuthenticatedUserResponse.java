package dev.hideftbanana.netcafejavafxapp.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUserResponse {
    private boolean isSuccess;
    private String accessToken;
    private String refreshToken;
}
