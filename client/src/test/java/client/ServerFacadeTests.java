package client;

import model.transactions.request.RegisterRequest;
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
    @Test
    public void registerSuccess() throws Exception {
        var result = facade.register(new RegisterRequest("user1", "password1", "email1"));

        assertNotNull(result.authToken(), "Registration should return a result.");
    }
    @Test
    public void registerNullUsernameException() throws Exception {
        facade.register(new RegisterRequest("user1", "password1", "email1");

    }
    @Test
    public void registerUserAlreadyTakenException() throws Exception {
        facade.register(new RegisterRequest("user1", "password1", "email1"));

        assertThrows(Exception.class, ()-> {
            facade.register(new RegisterRequest("user1", "password1", "email1"))
        });
    }

}
