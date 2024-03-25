package cz.cvut.fel.budgetplannerbackend.exceptions.dashboard;

public class DashboardNotFoundException extends RuntimeException{

    public DashboardNotFoundException(Long id) {
        super("Dashboard with id: " + id + " not found");
    }

}
