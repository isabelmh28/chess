package model.transactions.request;
/**
 * JoinGameRequest. Contains the information needed for a user to join a game.
 * Users must provide the gameID of the game they wish to join as well as the
 * player color or team they wish to join as.
 */
public record JoinGameRequest(
        String playerColor,
        int gameID
) { }
