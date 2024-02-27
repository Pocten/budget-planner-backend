package cz.cvut.fel.budgetplannerbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BudgetPlannerBackendApplication {

    @GetMapping("/")
    public String home() {
        return "Welcome to Budget Planner with CI/CD!";
    }
    public static void main(String[] args) {
        SpringApplication.run(BudgetPlannerBackendApplication.class, args);
    }

}
