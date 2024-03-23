package cz.cvut.fel.budgetplannerbackend.security.model.authentication;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthenticationRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String userPassword;
}
