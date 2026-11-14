/**
 * RegisterResult Module
 * Contains the information returned by a successful
 * user registration, which is the user's username and created authToken.
 * Author: Isabel Hinton
 */

package model.transactions.result;

public record RegisterResult(
        String username,
        String authToken
) { }
