package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import dataaccess.doa.interfaces.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

/**
 * Stores users (Primary Key is username) in SQL
 */
public class SQLUserDAO implements UserDAO {
    // Interface Functions
    @Override
    public void createUser(UserData data) throws DataAccessException {
        // Hashed Password
        String encryptedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                "INSERT INTO user (username, password, email)" +
                        "VALUES (?, ?, ?)")) {
            statement.setString(1, data.username());
            statement.setString(2, encryptedPassword);
            statement.setString(3, data.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to update database in User: createUser. %s", e.getMessage()));
        }
    }
    @Override
    public UserData getUserData(String username) throws DataAccessException {
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
                    String.format("Unable to retrieve UserData in User: getUserData. %s", e.getMessage()));
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
                    String.format("Unable to clear UserData in User: clear. %s", e.getMessage()));
        }
    }
}
