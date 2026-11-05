package model.transactions.result;

public record RegisterResult(
        String username,
        String authToken
) { }
