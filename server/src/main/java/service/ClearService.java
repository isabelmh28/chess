/**
 * ClearService Module
 * Services Admin endpoint:
 *    Clear
 * The Clear endpoint is only for testing and is therefore
 * kept separate from the User and Game Services
 * CLEAR SHOULD ONLY BE USED IN TESTING.
 * Exposing to user is not advised.
 * Author: Isabel Hinton
 */

package service;

import dataaccess.doa.interfaces.AuthDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.doa.interfaces.GameDAO;
import dataaccess.doa.interfaces.UserDAO;

public class ClearService {
    // Private Members
    private final UserDAO user;
    private final AuthDAO auth;
    private final GameDAO game;
    // Constructor
    public ClearService(UserDAO user, AuthDAO auth, GameDAO game) {
        this.user = user;
        this.auth = auth;
        this.game = game;
    }

    /**
     * Clears the database of all Data objects:
     *      UserData(username, password, email)
     *      AuthData(username, authToken)
     *      GameData(gameID, player usernames, gameName, game)
     * See transactions package for more information on requests and results.
     */
    public void clear() throws DataAccessException {
        user.clear();
        auth.clear();
        game.clear();
    }
}
