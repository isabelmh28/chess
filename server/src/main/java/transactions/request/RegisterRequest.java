package transactions.request;

record RegisterRequest (
        String username,
        String password,
        String email
) { }
