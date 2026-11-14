package model.transactions.result;
/**
 * RegisterResult. Contains the information returned by a successful
 * user registration, which is the user's username and created authToken.
 */
public record RegisterResult(
        String username,
        String authToken
) { }
