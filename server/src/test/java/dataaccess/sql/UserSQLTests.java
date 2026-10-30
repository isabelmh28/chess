package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import model.UserData;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import static org.junit.jupiter.api.Assertions.*;

public class UserSQLTests {
    private final SQLUserDAO userDAO = new SQLUserDAO();

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO.clear();
    }

    // Create User
    @Test
    void createUserSuccess() throws DataAccessException {
        UserData data = new UserData("name", "myPassword12@3", "myEmail@emails.com");
        userDAO.createUser(data);

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT username, password, email FROM user WHERE username = ?")
            ) {
            statement.setString(1, data.username());
            var ret = statement.executeQuery();
            assertTrue(ret.next(), "User Row should exist in the database");
            assertEquals(data.username(), ret.getString("username"),
                    "Created user should have input username");
            assertTrue(BCrypt.checkpw(data.password(), ret.getString("password")),
                    "Created user should have password hashed from input password");
            assertEquals(data.email(), ret.getString("email"),
                    "Created user should have input email");
        } catch (SQLException e) {
            throw new DataAccessException(String.format("createUser was unsuccessful: %s", e.getMessage()));
        }
    }
    @Test
    void createDuplicateUsernameDataAccessException() throws DataAccessException {
        UserData data1 = new UserData("name", "myPassword12@3", "myEmail@emails.com");
        UserData data2 = new UserData("name", "difPassword", "difEmail@dif.com");
        userDAO.createUser(data1);

        assertThrows(DataAccessException.class, ()-> userDAO.createUser(data2),
                "Creating a User with a duplicate username should throw a DataAccessException");
    }

    // Get User Datas
    @Test
    void getUserDataSuccess() throws DataAccessException {
        UserData data1 = new UserData("Isa", "myPassword12@3", "myEmail@emails.com");
        UserData data2 = new UserData("Jeff", "jeffNotGeoff", "jeffreyJeffsEmail@emails.com");
        userDAO.createUser(data1);
        userDAO.createUser(data2);

        UserData get = userDAO.getUserData(data1.username());

        assertNotNull(get, "getUserData output should not be null");
        assertEquals(data1.username(), get.username(), "Retrieved username should be the same as the original");
        assertTrue(BCrypt.checkpw(data1.password(), get.password()), "Retrieved password should hash to original");
        assertEquals(data1.email(), get.email(), "Retrieved email should be the same as the original");
    }
    @Test
    void getUserDataNotFoundNull() throws DataAccessException {
        UserData notFound = userDAO.getUserData("Bad Username");
        assertNull(notFound, "Searching for a user not in the database should return Null");
    }

    // Clear
    @Test
    void clearSuccess() throws DataAccessException {
        UserData data1 = new UserData("Isa", "myPassword12@3", "myEmail@emails.com");
        UserData data2 = new UserData("Jeff", "jeffNotGeoff", "jeffreyJeffsEmail@emails.com");
        userDAO.createUser(data1);
        userDAO.createUser(data2);

        UserData found1 = userDAO.getUserData(data1.username());
        UserData found2 = userDAO.getUserData(data2.username());
        assertEquals(found1.username(), data1.username(),
                "Before Clear, the returned username should match");
        assertTrue(BCrypt.checkpw(data1.password(), found1.password()),
                "Before Clear, the returned password should hash to the original");
        assertEquals(found1.email(), data1.email(),
                "Before Clear, the returned email should match");
        assertEquals(found2.username(), data2.username(),
                "Before Clear, the returned username should match");
        assertTrue(BCrypt.checkpw(data2.password(), found2.password()),
                "Before Clear, the returned password should hash to the original");
        assertEquals(found2.email(), data2.email(),
                "Before Clear, the returned email should match");

        userDAO.clear();
        UserData postClear1 = userDAO.getUserData(data1.username());
        UserData postClear2 = userDAO.getUserData(data2.username());
        assertNull(postClear1, "After Clear you should no longer find the first user created previously");
        assertNull(postClear2, "After Clear you should no longer find the second user created previously");
    }
}