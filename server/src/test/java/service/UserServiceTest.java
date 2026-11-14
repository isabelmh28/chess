/**
 * UserServiceTest Module
 * Test suite for the Service level
 * Targets User endpoints:
 *    Register
 *    Login
 *    Logout
 * Author: Isabel Hinton
 */

package service;

import dataaccess.doa.interfaces.*;
import dataaccess.doa.memory.*;
import dataaccess.exceptions.*;

import model.transactions.request.LoginRequest;
import model.transactions.request.RegisterRequest;
import model.transactions.result.LoginResult;
import model.transactions.result.RegisterResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class UserServiceTest {
    // Private Members
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
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testUser", "testpw23#6^{/", "testemail@testing.com");
        RegisterResult result = service.register(request);

        assertNotNull(result, "RegisterResult should not be null");
        assertEquals("testUser", result.username(), "Result username should match request");
        assertNotNull(result.authToken(), "RegisterResult authToken should be generated");
    }
    // Invalid Request
    @Test
    void registerNullRequestInvalidRequestException() throws Exception {
        assertThrows(InvalidRequestException.class, ()->service.register(null),
                "Null RegisterRequest should cause InvalidRequestException");
    }
    @Test
    void registerNullUsernameInvalidRequestException() throws Exception {
        RegisterRequest request = new RegisterRequest(
                null,"testpw23#6^{/", "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Null username should cause InvalidRequestException");
    }
    @Test
    void registerEmptyUsernameInvalidRequestException() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "","testpw23#6^{/", "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Empty/Blank username should cause InvalidRequestException");
    }
    @Test
    void registerNullPasswordInvalidRequestException() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testUser",null, "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Null password should cause InvalidRequestException");
    }
    @Test
    void registerEmptyPasswordInvalidRequestException() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testUser","", "testemail@testing.com");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Empty/Blank password should cause InvalidRequestException");
    }
    @Test
    void registerNullEmailInvalidRequestException() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser","testpw23#6^{/", null);

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Null email should cause InvalidRequestException");
    }
    @Test
    void registerEmptyEmailInvalidRequestException() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser","testpw23#6^{/", "");

        assertThrows(InvalidRequestException.class, ()-> service.register(request),
                "Empty/Blank email should cause InvalidRequestException");
    }
    // Already Taken
    @Test
    void registerAlreadyTakenException() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testUser","password", "testemail@testing.com");
        service.register(request);

        assertThrows(AlreadyTakenException.class, ()-> service.register(request),
                "Request with an existing username should cause AlreadyTakenException");
    }

    // Login Endpoint Testing
    // Success
    @Test
    void loginSuccess() throws Exception{
        RegisterRequest registerRequest = new RegisterRequest(
                "testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "testpw23#6^{/");
        LoginResult result = service.login(loginRequest);

        assertNotNull(result, "LoginResult should not be null");
        assertEquals("testUser", result.username(), "Result username should match request");
        assertNotNull(result.authToken(), "LoginResult authToken should be generated");
    }
    // Invalid Request
    @Test
    void loginNullRequestInvalidRequestException() throws Exception {
        assertThrows(InvalidRequestException.class, ()-> service.login(null),
                "Null LoginRequest should cause InvalidRequestException");
    }
    @Test
    void loginNullUsernameInvalidRequestException() throws Exception {
        LoginRequest request = new LoginRequest(null,"testpw23#6^{/");

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Null username should cause InvalidRequestException");
    }
    @Test
    void loginEmptyUsernameInvalidRequestException() throws Exception {
        LoginRequest request = new LoginRequest("","testpw23#6^{/");

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Empty/Blank username should cause InvalidRequestException");
    }
    @Test
    void loginNullPasswordInvalidRequestException() throws Exception {
        LoginRequest request = new LoginRequest("testUser",null);

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Null password should cause InvalidRequestException");
    }
    @Test
    void loginEmptyPasswordInvalidRequestException() throws Exception {
        LoginRequest request = new LoginRequest("testUser","");

        assertThrows(InvalidRequestException.class, ()-> service.login(request),
                "Empty/Blank password should cause InvalidRequestException");
    }
    // Unauthorized User
    @Test
    void loginNoExistingUserUnauthorizedUserException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("newUser", "newPassword");
        assertThrows(UnauthorizedUserException.class, ()-> service.login(loginRequest),
                "Userdata from username not found should cause UnauthorizedUserException");
    }
    @Test
    void loginMismatchedPasswordUnauthorizedUserException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "newPassword");
        assertThrows(UnauthorizedUserException.class, ()-> service.login(loginRequest),
                "Login password not matching registered password should cause UnauthorizedUserException");
    }

    // Logout Endpoint
    // Success
    @Test
    void logoutSuccess() throws Exception{
        RegisterRequest registerRequest = new RegisterRequest(
                "testUser", "testpw23#6^{/", "testemail@testing.com");
        service.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "testpw23#6^{/");
        LoginResult loginResult = service.login(loginRequest);

        String logoutHeader = loginResult.authToken();
        service.logout(logoutHeader);

        assertThrows(TokenNotFoundException.class, ()-> service.logout(logoutHeader),
                "Trying to logout again should cause a TokenNotFoundException");
    }
    // Token Not Found
    @Test
    void logoutTokenNotFoundException() throws Exception {
        assertThrows(TokenNotFoundException.class, ()-> service.logout("Bad authToken"),
                "Sending the wrong AuthToken should cause a TokenNotFound");
    }
}