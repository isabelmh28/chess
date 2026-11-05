package model.transactions.request;

public record JoinGameRequest(
        String playerColor,
        int gameID
) { }
