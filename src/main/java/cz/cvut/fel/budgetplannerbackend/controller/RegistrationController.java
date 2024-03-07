package cz.cvut.fel.budgetplannerbackend.controller;

import cz.cvut.fel.budgetplannerbackend.dto.UserDto;
import cz.cvut.fel.budgetplannerbackend.exceptions.UserAlreadyExistsException;
import cz.cvut.fel.budgetplannerbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registrationForm() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@RequestParam("username") String username,
                                      @RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      Model model) {
        // Логика создания пользователя...
        UserDto newUser = new UserDto(null, username, email, password, null, Collections.singleton("ROLE_USER"));
        try {
            userService.createUser(newUser);
            return "redirect:/login";
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("error", true);
            return "registration";
        }
    }
}
