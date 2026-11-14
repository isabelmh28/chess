/**
 * AuthSQLTest Module
 * Test suite for the Database level
 * Targets Authentication Database functions
 * Author: Isabel Hinton
 */

package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;

import model.AuthData;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthSQLTests {
    // Private Members
    private final SQLAuthDAO authDAO = new SQLAuthDAO();

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO.clear();
    }

    // Create Auth
    @Test
    void createAuthSuccess() throws DataAccessException {
        AuthData data = new AuthData("newToken", "myName");
        authDAO.createAuthData(data);

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT authToken, username FROM auth WHERE authToken = ?")
        ) {
            statement.setString(1, data.authToken());
            var ret = statement.executeQuery();
            assertTrue(ret.next(), "auth Row should exist in the database");
            assertEquals(data.authToken(), ret.getString("authToken"),
                    "Created Auth should have input authToken");
            assertEquals(data.username(), ret.getString("username"),
                    "Created Auth should have input username");
        } catch (SQLException e) {
            throw new DataAccessException(String.format("createAuthData was unsuccessful: %s", e.getMessage()));
        }
    }
    @Test
    void createAuthDataNullAuthDataDataAccessException() throws DataAccessException {
        assertThrows(DataAccessException.class, ()-> authDAO.createAuthData(null),
                "Creating AuthData with null data should throw a DataAccessException");
    }
    @Test
    void createAuthDataNullAuthTokenDataAccessException() throws DataAccessException {
        AuthData data = new AuthData(null, "name");
        assertThrows(DataAccessException.class, ()-> authDAO.createAuthData(data),
                "Creating AuthData with a null authToken should throw a DataAccessException");
    }
    @Test
    void createAuthDataEmptyAuthTokenDataAccessException() throws DataAccessException {
        AuthData data = new AuthData("", "name");
        assertThrows(DataAccessException.class, ()-> authDAO.createAuthData(data),
                "Creating AuthData with an empty authToken should throw a DataAccessException");
    }
    @Test
    void createAuthDataNullUsernameDataAccessException() throws DataAccessException {
        AuthData data = new AuthData("token", null);
        assertThrows(DataAccessException.class, ()-> authDAO.createAuthData(data),
                "Creating AuthData with a null username should throw a DataAccessException");
    }
    @Test
    void createAuthDataEmptyUsernameDataAccessException() throws DataAccessException {
        AuthData data = new AuthData("token", "");
        assertThrows(DataAccessException.class, ()-> authDAO.createAuthData(data),
                "Creating AuthData with an empty username should throw a DataAccessException");
    }
    @Test
    void createDuplicateAuthTokenDataAccessException() throws DataAccessException {
        AuthData data1 = new AuthData("token1", "name1");
        AuthData data2 = new AuthData("token1", "name2");
        authDAO.createAuthData(data1);

        assertThrows(DataAccessException.class, ()-> authDAO.createAuthData(data2),
                "Creating AuthData with a duplicate authToken should throw a DataAccessException");
    }

    // Get Auth Data
    @Test
    void getAuthDataSuccess() throws DataAccessException {
        AuthData data1 = new AuthData("myFirstToken", "Isa");
        AuthData data2 = new AuthData("mySecondToken", "Jeff");
        authDAO.createAuthData(data1);
        authDAO.createAuthData(data2);

        AuthData get = authDAO.getAuthData(data1.authToken());

        assertNotNull(get, "getAuthData output should not be null");
        assertEquals(data1.authToken(), get.authToken(), "Retrieved authToken should be the same as the original");
        assertEquals(data1.username(), get.username(), "Retrieved username should be the same as the original");
    }
    @Test
    void getAuthDataNullAuthTokenDataAccessException() throws DataAccessException {
        assertThrows(DataAccessException.class, ()-> authDAO.getAuthData(null),
                "Retrieving AuthData with a null authToken should throw a DataAccessException");
    }
    @Test
    void getAuthDataEmptyAuthTokenDataAccessException() throws DataAccessException {
        assertThrows(DataAccessException.class, ()-> authDAO.getAuthData(""),
                "Retrieving AuthData with an empty authToken should throw a DataAccessException");
    }
    @Test
    void getAuthDataNotFoundNullDataAccessException() throws DataAccessException {
        AuthData notFound = authDAO.getAuthData("Bad authToken");
        assertNull(notFound, "Searching for AuthData not in the database should return Null");
    }

    // Remove Auth Data
    @Test
    void removeAuthData() throws DataAccessException {
        AuthData data1 = new AuthData("myFirstToken", "Isa");
        AuthData data2 = new AuthData("mySecondToken", "Jeff");
        authDAO.createAuthData(data1);
        authDAO.createAuthData(data2);

        authDAO.removeAuthData(data1.authToken());
        assertNull(authDAO.getAuthData(data1.authToken()),
                "Searching for a removed AuthData should return Null");
        assertNotNull(authDAO.getAuthData(data2.authToken()),
                "Searching for authentication not removed should return AuthData");
    }
    @Test
    void removeAuthDataNullAuthTokenDataAccessException() throws DataAccessException {
        assertThrows(DataAccessException.class, ()-> authDAO.removeAuthData(null),
                "Removing AuthData with a null authToken should throw a DataAccessException");
    }
    @Test
    void removeAuthDataEmptyAuthTokenDataAccessException() throws DataAccessException {
        assertThrows(DataAccessException.class, ()-> authDAO.removeAuthData(""),
                "Removing AuthData with an empty authToken should throw a DataAccessException");
    }
    @Test
    void removeAuthDataNotFoundDataAccessException() {
        assertDoesNotThrow(() -> authDAO.removeAuthData("Bad Token"),
                "Removing a non-existant authToken should not throw an exception");
    }

    // Clear
    @Test
    void clearSuccess() throws DataAccessException {
        AuthData data1 = new AuthData("myFirstToken", "Isa");
        AuthData data2 = new AuthData("mySecondToken", "Jeff");
        authDAO.createAuthData(data1);
        authDAO.createAuthData(data2);

        AuthData found1 = authDAO.getAuthData(data1.authToken());
        AuthData found2 = authDAO.getAuthData(data2.authToken());
        assertEquals(found1.authToken(), data1.authToken(),
                "Before Clear, the returned authToken should match");
        assertEquals(found1.username(), data1.username(),
                "Before Clear, the returned username should match");
        assertEquals(found2.authToken(), data2.authToken(),
                "Before Clear, the returned authToken should match");
        assertEquals(found2.username(), data2.username(),
                "Before Clear, the returned username should match");

        authDAO.clear();
        AuthData postClear1 = authDAO.getAuthData(data1.authToken());
        AuthData postClear2 = authDAO.getAuthData(data2.authToken());
        assertNull(postClear1, "After Clear you should no longer find the first Auth created previously");
        assertNull(postClear2, "After Clear you should no longer find the second Auth created previously");
    }
}