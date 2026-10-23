package transactions.result;

public record RegisterResult(
        String username,
        String authToken
) { }
