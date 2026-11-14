/**
 * LoginRequest Module
 * Contains the information needed to log in a user.
 * Users must provide the username and password they used to register.
 * Author: Isabel Hinton
 */

package model.transactions.request;

public record LoginRequest(
        String username,
        String password
){ }
