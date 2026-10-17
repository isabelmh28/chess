package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuthData(AuthData auth) throws DataAccessException;
    AuthData getAuthData(String authToken) throws DataAccessException;
    void removeAuthData(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}