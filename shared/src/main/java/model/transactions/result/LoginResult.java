package model.transactions.result;

public record LoginResult (
        String username,
        String authToken
){ }
