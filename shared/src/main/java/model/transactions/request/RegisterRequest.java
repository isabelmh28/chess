package model.transactions.request;

public record RegisterRequest (
        String username,
        String password,
        String email
) { }
