package model.transactions.request;
/**
 * LogoutRequest. Contains the information needed to log out a user.
 * Users must provide the authToken connected to their account.
 */
public record LogoutRequest (String authToken){ }
