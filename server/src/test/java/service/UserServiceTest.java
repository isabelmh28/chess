package service;

import dataaccess.doa.interfaces.AuthDAO;
import dataaccess.doa.interfaces.UserDAO;
import dataaccess.doa.memory.MemoryAuthDAO;
import dataaccess.doa.memory.MemoryUserDAO;
import dataaccess.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    // Login Endpoint Testing
    // Success
    @Test
    void loginSuccess() throws Exception{
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "testpw23#6^{/");
        LoginResult result = service.login(loginRequest);

        assertNotNull(result, "LoginResult should not be null");
        assertEquals("testUser", result.username(), "Result username should match request");
        assertNotNull(result.authToken(), "LoginResult authToken should be generated");
    }
    // Invalid Request
    @Test
    void loginNullUsernameInvalidRequest() throws Exception {
        LoginRequest request = new LoginRequest(null,"testpw23#6^{/");

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Null username should cause InvalidRequestException");
    }
    @Test
    void loginEmptyUsernameInvalidRequest() throws Exception {
        LoginRequest request = new LoginRequest("","testpw23#6^{/");

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Empty/Blank username should cause InvalidRequestException");
    }
    @Test
    void loginNullPasswordInvalidRequest() throws Exception {
        LoginRequest request = new LoginRequest("testUser",null);

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Null password should cause InvalidRequestException");
    }
    @Test
    void loginEmptyPasswordInvalidRequest() throws Exception {
        LoginRequest request = new LoginRequest("testUser","");

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Empty/Blank password should cause InvalidRequestException");
    }
    // Unauthorized User
    @Test
    void loginNoExistingUserUnauthorizedUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("newUser", "newPassword");
        assertThrows(UnauthorizedUserException.class, ()-> service.login(loginRequest),
                "Userdata from username not found should cause UnauthorizedUserException");
    }
    @Test
    void loginMismatchedPasswordUnauthorizedUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "newPassword");
        assertThrows(UnauthorizedUserException.class, ()-> service.login(loginRequest),
                "Login password not matching registered password should cause UnauthorizedUserException");
    }

    // Logout Endpoint
    // Success
    @Test
    void logoutSuccess() throws Exception{
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "testpw23#6^{/");
        LoginResult loginResult = service.login(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        service.logout(logoutRequest);

        assertThrows(TokenNotFoundException.class, ()-> service.logout(logoutRequest),
                "Trying to logout again should cause a TokenNotFoundException");
    }
    // Token Not Found
    @Test
    void logoutTokenNotFoundException() throws Exception {
        LogoutRequest request = new LogoutRequest("Bad AuthToken");
        assertThrows(TokenNotFoundException.class, ()-> service.logout(request),
                "Sending the wrong AuthToken should cause a TokenNotFound");
    }
}