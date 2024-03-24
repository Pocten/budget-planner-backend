package cz.cvut.fel.budgetplannerbackend.security.model.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String userEmail;

    @NotBlank
    private String userPassword;


}
