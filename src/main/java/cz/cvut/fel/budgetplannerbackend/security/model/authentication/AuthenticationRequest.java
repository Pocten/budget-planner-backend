package cz.cvut.fel.budgetplannerbackend.security.model.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class AuthenticationRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String userPassword;

}
