package model.transactions.request;
/**
 * LoginRequest. Contains the information needed to log in a user.
 * Users must provide the username and password they used to register.
 */
public record LoginRequest(
        String username,
        String password
){ }
