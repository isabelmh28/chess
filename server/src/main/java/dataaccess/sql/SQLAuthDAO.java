/**
 * SQLAuthDAO Module
 * Services Authentication Database functions
 * Author: Isabel Hinton
 */

package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import dataaccess.doa.interfaces.AuthDAO;

import model.AuthData;

import java.sql.SQLException;

/**
 * Stores authentication data (Primary Key is authToken) in SQL
 */
public class SQLAuthDAO implements AuthDAO {
    // Interface Functions
    @Override
    public void createAuthData(AuthData auth) throws DataAccessException {
        if(    (auth == null)
            || (auth.authToken() == null) || (auth.authToken().isEmpty())
            || (auth.username() == null)  || (auth.username().isEmpty())) {
            throw new DataAccessException("Bad argument, unable to update database");
        }
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "INSERT INTO auth (authToken, username)" +
                        "VALUES (?, ?)")) {
            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database: %s", e.getMessage()));
        }
    }
    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        if((authToken == null) || (authToken.isEmpty())) {
            throw new DataAccessException("Bad argument, unable to retrieve data");
        }
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "SELECT authToken, username FROM auth WHERE authToken = ?")) {
            statement.setString(1, authToken);
            try (var ret = statement.executeQuery()) {
                if(ret.next()) {
                    return new AuthData(
                            ret.getString("authToken"),
                            ret.getString("username")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to retrieve AuthData: %s", e.getMessage()));
        }
        return null;
    }
    @Override
    public void removeAuthData(String authToken) throws DataAccessException {
        if((authToken == null) || (authToken.isEmpty())) {
            throw new DataAccessException("Bad argument, unable to update database");
        }
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "DELETE FROM auth WHERE authToken = ?")) {
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to delete AuthData: %s", e.getMessage()));
        }
    }
    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "DELETE FROM auth")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to clear AuthData: %s", e.getMessage()));
        }
    }
}