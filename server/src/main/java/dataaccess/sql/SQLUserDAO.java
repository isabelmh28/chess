/**
 * SQLUserDAO Module
 * Services User Database functions
 * Author: Isabel Hinton
 */

package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import dataaccess.doa.interfaces.UserDAO;
import model.UserData;

import java.sql.SQLException;

/**
 * Stores users (Primary Key is username) in SQL
 */
public class SQLUserDAO implements UserDAO {
    // Interface Functions
    @Override
    public void createUser(UserData data) throws DataAccessException {
        // Check Parameters
        if(        (data.username() == null) || (data.username().isEmpty())
                || (data.password() == null) || (data.password().isEmpty())
                || (data.email() == null)    || (data.email().isEmpty())) {
            throw new DataAccessException("Bad arguments, unable to update database");
        }
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "INSERT INTO user (username, password, email)" +
                        "VALUES (?, ?, ?)")) {
            statement.setString(1, data.username());
            statement.setString(2, data.password());
            statement.setString(3, data.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database: %s", e.getMessage()));
        }
    }
    @Override
    public UserData getUserData(String username) throws DataAccessException {
        // Check Parameters
        if((username == null) || (username.isEmpty())) {
            throw new DataAccessException("Bad arguments, unable to retrieve UserData");
        }
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "SELECT username, password, email FROM user WHERE username = ?")) {
            statement.setString(1, username);
            try (var ret = statement.executeQuery()) {
                if(ret.next()) {
                    return new UserData(
                            ret.getString("username"),
                            ret.getString("password"),
                            ret.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to retrieve UserData: %s", e.getMessage()));
        }
        return null;
    }
    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "DELETE FROM user")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to clear UserData: %s", e.getMessage()));
        }
    }
}
