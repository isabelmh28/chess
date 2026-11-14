/**
 * LoginResult Module
 * Contains the information returned by a successful user login,
 * which is the user's username and created authToken.
 * Author: Isabel Hinton
 */

package model.transactions.result;

public record LoginResult (
        String username,
        String authToken
){ }
