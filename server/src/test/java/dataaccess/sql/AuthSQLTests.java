package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.doa.sql.SQLAuthDAO;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class AuthSQLTests {
    private final SQLAuthDAO AuthDAO = new SQLAuthDAO();

    @BeforeEach
    public void setUp() throws DataAccessException {
        AuthDAO.clear();
    }

    // Create Auth
    @Test
    void createAuthSuccess() throws DataAccessException {
        AuthData data = new AuthData("newToken", "myName");
        AuthDAO.createAuthData(data);

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
    void createDuplicateAuthTokenDataAccessException() throws DataAccessException {
        AuthData data1 = new AuthData("token1", "name1");
        AuthData data2 = new AuthData("token1", "name2");
        AuthDAO.createAuthData(data1);

        assertThrows(DataAccessException.class, ()-> AuthDAO.createAuthData(data2),
                "Creating AuthData with a duplicate authToken should throw a DataAccessException");
    }

    // Get Auth Data
    @Test
    void getAuthDataSuccess() throws DataAccessException {
        AuthData data1 = new AuthData("myFirstToken", "Isa");
        AuthData data2 = new AuthData("mySecondToken", "Jeff");
        AuthDAO.createAuthData(data1);
        AuthDAO.createAuthData(data2);

        AuthData get = AuthDAO.getAuthData(data1.authToken());

        assertNotNull(get, "getAuthData output should not be null");
        assertEquals(data1.authToken(), get.authToken(), "Retrieved authToken should be the same as the original");
        assertEquals(data1.username(), get.username(), "Retrieved username should be the same as the original");
    }
    @Test
    void getAuthDataNotFoundDataAccessException() throws DataAccessException {
        AuthData notFound = AuthDAO.getAuthData("Bad authToken");
        assertNull(notFound, "Searching for AuthData not in the database should return Null");
    }

    // Remove Auth Data
    @Test
    void removeAuthData() throws DataAccessException {
        AuthData data1 = new AuthData("myFirstToken", "Isa");
        AuthData data2 = new AuthData("mySecondToken", "Jeff");
        AuthDAO.createAuthData(data1);
        AuthDAO.createAuthData(data2);

        AuthDAO.removeAuthData(data1.authToken());
        assertNull(AuthDAO.getAuthData(data1.authToken()),
                "Searching for a removed AuthData should return Null");
        assertNotNull(AuthDAO.getAuthData(data2.authToken()),
                "Searching for authentication not removed should return AuthData");
    }
    @Test
    void removeAuthDataNotFoundDataAccessException() throws DataAccessException {
        assertDoesNotThrow(() -> AuthDAO.removeAuthData("Bad Token"),
                "Removing a non-existant authToken should not throw an exception");
    }

    // Clear
    @Test
    void clearSuccess() throws DataAccessException {
        AuthData data1 = new AuthData("myFirstToken", "Isa");
        AuthData data2 = new AuthData("mySecondToken", "Jeff");
        AuthDAO.createAuthData(data1);
        AuthDAO.createAuthData(data2);

        AuthData found1 = AuthDAO.getAuthData(data1.authToken());
        AuthData found2 = AuthDAO.getAuthData(data2.authToken());
        assertEquals(found1.authToken(), data1.authToken(),
                "Before Clear, the returned authToken should match");
        assertEquals(found1.username(), data1.username(),
                "Before Clear, the returned username should match");
        assertEquals(found2.authToken(), data2.authToken(),
                "Before Clear, the returned authToken should match");
        assertEquals(found2.username(), data2.username(),
                "Before Clear, the returned username should match");

        AuthDAO.clear();
        AuthData postClear1 = AuthDAO.getAuthData(data1.authToken());
        AuthData postClear2 = AuthDAO.getAuthData(data2.authToken());
        assertNull(postClear1, "After Clear you should no longer find the first Auth created previously");
        assertNull(postClear2, "After Clear you should no longer find the second Auth created previously");
    }
}