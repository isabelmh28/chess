package service;

import dataaccess.doa.interfaces.AuthDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.doa.interfaces.GameDAO;
import dataaccess.doa.interfaces.UserDAO;

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
