package cz.cvut.fel.budgetplannerbackend.security.model.registration;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RegistrationRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String userPassword;

    @NotBlank
    private String userEmail;

}
