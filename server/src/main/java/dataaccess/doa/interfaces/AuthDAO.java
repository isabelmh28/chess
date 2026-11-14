/**
 * AuthDAO Interface
 * Auth DataObject Interface
 * Author: Isabel Hinton
 */

package dataaccess.doa.interfaces;

import dataaccess.exceptions.DataAccessException;

import model.AuthData;

public interface AuthDAO {
    // Interface Functions
    void createAuthData(AuthData auth) throws DataAccessException;
    AuthData getAuthData(String authToken) throws DataAccessException;
    void removeAuthData(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}