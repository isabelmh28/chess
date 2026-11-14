/**
 * AuthData Module
 * Contains authentication information, including a
 * username's username and their authToken if they log in.
 * Author: Isabel Hinton
 */

package model;

public record AuthData(
        String authToken,
        String username
) { }
