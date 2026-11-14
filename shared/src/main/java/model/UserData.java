package model;
/**
 * UserData record. Holds a user's username, password, and email.
 */

public record UserData(
        String username,
        String password,
        String email
) { }