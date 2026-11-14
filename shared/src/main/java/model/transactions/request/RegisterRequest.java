package model.transactions.request;
/**
 * RegisterRequest. Contains the information needed to register a user.
 * Users must provide a username, password, and email.
 */
public record RegisterRequest (
        String username,
        String password,
        String email
) { }
