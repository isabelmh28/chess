package transactions.request;

public record JoinGameRequest(
        String playerColor,
        int gameID
) { }
