/**
 * UserService Module
 * Services User endpoints:
 *    Register
 *    Login
 *    Logout
 * Author: Isabel Hinton
 */

package service;

import model.UserData;
import model.AuthData;
import model.transactions.request.LoginRequest;
import model.transactions.request.RegisterRequest;
import model.transactions.result.LoginResult;
import model.transactions.result.RegisterResult;

import dataaccess.doa.interfaces.AuthDAO;
import dataaccess.doa.interfaces.UserDAO;
import dataaccess.exceptions.*;

import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    // Private Members
    private final UserDAO user;
    private final AuthDAO auth;
    // Constructor
    public UserService(UserDAO user, AuthDAO auth) {
        this.user = user;
        this.auth = auth;
    }

    // Endpoints
    /**
     * Registers a user given a request.
     * @param request The service request to register a user.
     * @return RegisterResult.
     * See transactions package for more information on requests and results.
     */
    public RegisterResult register(RegisterRequest request)
            throws DataAccessException, InvalidRequestException, AlreadyTakenException {
        // Check for a bad request
        if(        (request == null)
                || (request.username() == null) || (request.username().isBlank())
                || (request.password() == null) || (request.password().isBlank())
                || (request.email()    == null) || (request.email().isBlank())) {
            throw new InvalidRequestException("Error: bad request");
        }
        // Make sure username isn't already taken
        if(user.getUserData(request.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        // Create new user
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData newUser = new UserData(request.username(), hashedPassword, request.email());
        user.createUser(newUser);
        // Generate an authToken
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(newAuthToken, request.username());
        auth.createAuthData(newAuthData);

        return new RegisterResult(newAuthData.username(), newAuthData.authToken());
    }
    /**
     * Logs in a user given a request.
     * @param request The service request to log in a user.
     * @return LoginResult.
     * See transactions package for more information on requests and results.
     */
    public LoginResult login(LoginRequest request)
            throws DataAccessException, InvalidRequestException, UnauthorizedUserException {
        // Check for a bad request
        if(        (request == null)
                || (request.username() == null) || (request.username().isBlank())
                || (request.password() == null) || (request.password().isBlank())) {
            throw new InvalidRequestException("Error: bad request");
        }
        // Validate user login
        UserData checkUser = user.getUserData(request.username());
        if(checkUser == null) {
            throw new UnauthorizedUserException("Error: unauthorized");
        }
        else if(!BCrypt.checkpw(request.password(), checkUser.password())) {
            throw new UnauthorizedUserException("Error: unauthorized");
        }
        // Generate an authToken
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(newAuthToken, request.username());
        auth.createAuthData(newAuthData);

        return new LoginResult(newAuthData.username(), newAuthData.authToken());
    }
    /**
     * Logs out a user based on a request.
     * @param authToken There is no request body, only an authToken header.
     * See transactions package for more information on requests and results.
     */
    public void logout(String authToken)
            throws DataAccessException, TokenNotFoundException {
        // Find authData from authToken
        if(auth.getAuthData(authToken) == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }
        auth.removeAuthData(authToken);
    }
}
