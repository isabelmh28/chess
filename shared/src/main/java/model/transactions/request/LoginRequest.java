package model.transactions.request;

public record LoginRequest(
        String username,
        String password
){ }
