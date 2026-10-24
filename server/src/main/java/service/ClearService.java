package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final UserDAO user;
    private final AuthDAO auth;
    private final GameDAO game;

    public ClearService(UserDAO user, AuthDAO auth, GameDAO game) {
        this.user = user;
        this.auth = auth;
        this.game = game;
    }

    public void clear() throws DataAccessException {
        user.clear();
        auth.clear();
        game.clear();
    }
}
