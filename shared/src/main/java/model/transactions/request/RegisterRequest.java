/**
 * RegisterRequest Module
 * Contains the information needed to register a user.
 * Users must provide a username, password, and email.
 * Author: Isabel Hinton
 */

package model.transactions.request;

public record RegisterRequest (
        String username,
        String password,
        String email
) { }
