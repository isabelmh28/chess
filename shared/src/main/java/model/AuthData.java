package model;
/**
 * AuthData record. Contains authentication information, including a
 * username's username and their authToken if they log in.
 */
public record AuthData(
        String authToken,
        String username
) { }
