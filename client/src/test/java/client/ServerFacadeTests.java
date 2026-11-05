package client;

import dataaccess.exceptions.InvalidRequestException;
import dataaccess.exceptions.UnauthorizedUserException;
import model.transactions.request.LoginRequest;
import model.transactions.request.RegisterRequest;
import model.transactions.result.LoginResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import server.Server;
import ui.ServerFacade;


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

    // Clear
    @Test
    void clearSuccess() throws Exception{
        var originalResult = facade.register(new RegisterRequest("user1", "password1", "email1"));
        facade.clear();
        var newResult = facade.register(new RegisterRequest("user1", "password1", "email1"));
        assertNotNull(newResult.authToken(), "Clearing the database should allow re-registration");
        assertEquals(originalResult.username(), newResult.username(),
                "Username should be the same before and after clearing and re-registration");
    }

    // Register
    // Success Case
    @Test
    public void registerSuccess() throws Exception {
        var result = facade.register(new RegisterRequest("user1", "password1", "email1"));

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
        facade.register(new RegisterRequest("user1", "password1", "email1"));

        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest("user1", "password1", "email1"));
        }, "Trying to register with a username that already exists should throw an exception");
    }

    // Login
    // Success
    @Test
    void loginSuccess() throws Exception{
        var register = facade.register(new RegisterRequest("user1", "password1", "email1"));
        var result = facade.login(new LoginRequest("user1", "password1"));

        assertNotNull(result, "Login should return a result");
        assertEquals(register.username(), result.username(), "Login username should match the registered user");
        assertNotNull(result.authToken(), "Login should return an authToken");
    }
    // Invalid Request
    @Test
    void loginNullUsernameException() throws Exception {

    }
    @Test
    void loginEmptyUsernameException() throws Exception {

    }
    @Test
    void loginNullPasswordException() throws Exception {

    }
    @Test
    void loginEmptyPasswordException() throws Exception {

    }
    // Unauthorized User
    @Test
    void loginNoExistingUserException() throws Exception {

    }
    @Test
    void loginMismatchedPasswordException() throws Exception {

    }
}
