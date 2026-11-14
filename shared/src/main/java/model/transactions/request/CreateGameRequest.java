package model.transactions.request;
/**
 * CreateGameRequest. Contains the information needed for a user to create a game.
 * Users must provide a name for their game.
 */
public record CreateGameRequest(String gameName) { }
