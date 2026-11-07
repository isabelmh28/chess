package client;

import model.transactions.request.CreateGameRequest;
import model.transactions.request.LoginRequest;
import model.transactions.request.LogoutRequest;
import model.transactions.request.RegisterRequest;
import model.transactions.result.RegisterResult;
import server.Server;
import ui.ServerFacade;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDB() throws Exception {
        facade.clear();
    }

    // Test helper functions to reduce duplication
    private RegisterResult registerTestUser() throws Exception {
        return facade.register(new RegisterRequest("user1", "password1", "email1"));
    }
    
    // Clear
    @Test
    void clearSuccess() throws Exception {
        var originalResult = registerTestUser();
        facade.clear();
        var newResult = registerTestUser();
        assertNotNull(newResult.authToken(), "Clearing the database should allow re-registration");
        assertEquals(originalResult.username(), newResult.username(),
                "Username should be the same before and after clearing and re-registration");
    }

    // Register
    // Success Case
    @Test
    public void registerSuccess() throws Exception {
        var result = registerTestUser();

        assertNotNull(result.authToken(), "Registration should return an authToken.");
        assertEquals("user1", result.username(), "Registration result should have the original username");
    }
    // Failure Cases
    @Test
    public void registerNullUsernameException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest(null, "password1", "email1"));
        }, "Trying to register with a null username should throw an exception");
    }
    @Test
    public void registerEmptyUsernameException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest("", "password1", "email1"));
        }, "Trying to register with an empty username should throw an exception");
    }
    @Test
    public void registerNullPasswordException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest("user1", null, "email1"));
        }, "Trying to register with a null password should throw an exception");
    }
    @Test
    public void registerEmptyPasswordException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest("user1", "", "email1"));
        }, "Trying to register with an empty password should throw an exception");
    }
    @Test
    public void registerNullEmailException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest("user1", "password1", null));
        }, "Trying to register with a null email should throw an exception");
    }
    @Test
    public void registerEmptyEmailException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest("user1", "password1", ""));
        }, "Trying to register with an empty email should throw an exception");
    }
    @Test
    public void registerUserAlreadyTakenException() throws Exception {
        registerTestUser();

        assertThrows(Exception.class, ()-> {
            registerTestUser();
        }, "Trying to register with a username that already exists should throw an exception");
    }

    // Login
    // Success
    @Test
    void loginSuccess() throws Exception{
        var register = registerTestUser();
        var result = facade.login(new LoginRequest("user1", "password1"));

        assertNotNull(result, "Login should return a result");
        assertEquals(register.username(), result.username(), "Login username should match the registered user");
        assertNotNull(result.authToken(), "Login should return an authToken");
    }
    // Invalid Request
    @Test
    void loginNullUsernameException() throws Exception {
        var register = registerTestUser();

        assertThrows(Exception.class, ()-> {
            facade.login(new LoginRequest(null, "password1"));
        }, "Trying to login with a null username should throw an exception");
    }
    @Test
    void loginEmptyUsernameException() throws Exception {
        var register = registerTestUser();

        assertThrows(Exception.class, ()-> {
            facade.login(new LoginRequest("", "password1"));
        }, "Trying to login with an empty username should throw an exception");
    }
    @Test
    void loginNullPasswordException() throws Exception {
        var register = registerTestUser();

        assertThrows(Exception.class, ()-> {
            facade.login(new LoginRequest("user1", null));
        }, "Trying to login with a null password should throw an exception");
    }
    @Test
    void loginEmptyPasswordException() throws Exception {
        var register = registerTestUser();

        assertThrows(Exception.class, ()-> {
            facade.login(new LoginRequest("user1", ""));
        }, "Trying to login with an empty password should throw an exception");
    }
    // Unauthorized User
    @Test
    void loginNoExistingUserException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.login(new LoginRequest("user1", "password1"));
        }, "Trying to login with unregistered credentials should throw an exception");
    }
    @Test
    void loginMismatchedPasswordException() throws Exception {
        var register = registerTestUser();

        assertThrows(Exception.class, ()-> {
            facade.login(new LoginRequest("user1", "wrongPasswword"));
        }, "Logging in with the wrong password should throw an exception");
    }

    //Logout
    //Success
    @Test
    void logoutSuccess() throws Exception{
        var register = registerTestUser();
        facade.logout(new LogoutRequest(register.authToken()));

        assertThrows(Exception.class, ()-> {
            facade.logout(new LogoutRequest(register.authToken()));
        }, "Trying to Logout twice should delete the authToken and throw an Exception");
    }
    // Token Not Found
    @Test
    void logoutTokenNotFoundException() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.logout(new LogoutRequest("badToken"));
        }, "Trying to Logout with an invalid authToken should throw an Exception");
    }

    // CreateGame
    // Success
    @Test
    void createGameSuccess() throws Exception{
        var register = registerTestUser();
        var create = facade.createGame(register.authToken(), new CreateGameRequest("game1"));

        assertNotNull(create, "Created game should not return null");
        assertTrue(create.gameID() > 0, "Creating a game should return a gameID that is a positive integer");

        var newGame = facade.createGame(register.authToken(), new CreateGameRequest("newGame"));

        assertNotNull(create, "Creating a second game should not return null");
        assertTrue(newGame.gameID() > 0, "Creating a  second game should return a gameID that is a positive integer");
        assertNotEquals(create.gameID(), newGame.gameID(), "Two created games should have unique gameIDs");
    }
    // Invalid Request
    @Test
    void createGameNullInvalidRequest() throws Exception {
        var register = registerTestUser();
        assertThrows(Exception.class, ()-> {
            facade.createGame(register.authToken(), null);
        }, "Attempting to create a null game should throw an exception");
    }
    @Test
    void createGameNullGameNameInvalidRequest() throws Exception {
        var register = registerTestUser();
        assertThrows(Exception.class, ()-> {
            facade.createGame(register.authToken(), new CreateGameRequest(null));
        }, "Attempting to create a game with a null gameName should throw an exception");
    }
    @Test
    void createGameEmptyGameNameInvalidRequest() throws Exception {
        var register = registerTestUser();
        assertThrows(Exception.class, ()-> {
            facade.createGame(register.authToken(), new CreateGameRequest(""));
        }, "Attempting to create a game with an empty gameName should throw an exception");
    }
    // Token Not Found
    @Test
    void createGameTokenNotFound() throws Exception {
        assertThrows(Exception.class, ()-> {
            facade.createGame("badToken", new CreateGameRequest("game"));
        }, "Attempting to create a game with a bad authToken should throw an exception");
    }
}
