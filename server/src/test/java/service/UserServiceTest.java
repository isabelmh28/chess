package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import transactions.request.*;
import transactions.result.*;


import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        service = new UserService(userDAO, authDAO);
        // When moving to SQL, will need to clear as well
    }

    // Register Endpoint Testing
    // Success
    @Test
    void registerSuccess() throws Exception{
        RegisterRequest request = new RegisterRequest("testUser", "testpw23#6^{/", "testemail@testing.com");
        RegisterResult result = service.register(request);

        assertNotNull(result, "RegisterResult should not be null");
        assertEquals("testUser", result.username(), "Result username should match request");
        assertNotNull(result.authToken(), "RegisterResult authToken should be generated");
    }
    // Invalid Request
    @Test
    void registerNullUsernameInvalidRequest() throws Exception{
        RegisterRequest request = new RegisterRequest(null,"testpw23#6^{/", "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Null username should cause InvalidRequestException");
    }
    @Test
    void registerEmptyUsernameInvalidRequest() throws Exception{
        RegisterRequest request = new RegisterRequest("","testpw23#6^{/", "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Empty/Blank username should cause InvalidRequestException");
    }
    @Test
    void registerNullPasswordInvalidRequest() throws Exception{
        RegisterRequest request = new RegisterRequest("testUser",null, "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Null password should cause InvalidRequestException");
    }
    @Test
    void registerEmptyPasswordInvalidRequest() throws Exception{
        RegisterRequest request = new RegisterRequest("testUser","", "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Empty/Blank password should cause InvalidRequestException");
    }
    @Test
    void registerNullEmailInvalidRequest() throws Exception{
        RegisterRequest request = new RegisterRequest("testUser","testpw23#6^{/", null);

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Null email should cause InvalidRequestException");
    }
    @Test
    void registerEmptyEmailInvalidRequest() throws Exception{
        RegisterRequest request = new RegisterRequest("testUser","testpw23#6^{/", "");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Empty/Blank email should cause InvalidRequestException");
    }
    // Already Taken
    @Test
    void registerAlreadyTaken() throws Exception{
        RegisterRequest request = new RegisterRequest("testUser","password", "testemail@testing.com");
        service.register(request);

        assertThrows(AlreadyTakenException.class, ()-> service.register(request),
                "Request with an existing username should cause AlreadyTakenException");
    }


    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}