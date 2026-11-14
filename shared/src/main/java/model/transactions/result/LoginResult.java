package model.transactions.result;
/**
 * LoginResult. Contains the information returned by a successful
 * user login, which is the user's username and created authToken.
 */
public record LoginResult (
        String username,
        String authToken
){ }
