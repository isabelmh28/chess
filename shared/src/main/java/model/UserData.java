/**
 * UserData Module
 * Holds a user's username, password, and email.
 * Author: Isabel Hinton
 */

package model;

public record UserData(
        String username,
        String password,
        String email
) { }