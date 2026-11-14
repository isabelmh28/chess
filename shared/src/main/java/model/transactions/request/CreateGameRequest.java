/**
 * CreateGameRequest Module
 * Contains the information needed for a user to create a game.
 * Users must provide a name for their game.
 * Author: Isabel Hinton
 */

package model.transactions.request;

public record CreateGameRequest(String gameName) { }
