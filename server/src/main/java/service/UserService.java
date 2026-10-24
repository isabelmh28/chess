package service;

import java.util.UUID;

import dataaccess.*;
import model.UserData;
import model.AuthData;
import transactions.request.*;
import transactions.result.*;

/**
 * Performs Services for the User (registration, login, authentication, etc.)
 */
public class UserService {
    private final UserDAO user;
    private final AuthDAO auth;

    public UserService(UserDAO user, AuthDAO auth) {
        this.user = user;
        this.auth = auth;
    }

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
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        user.createUser(newUser);
        // Generate an authToken
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(request.username(), newAuthToken);
        auth.createAuthData(newAuthData);

        return new RegisterResult(newAuthData.username(), newAuthData.authToken());
    }

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
        else if(!checkUser.password().equals(request.password())) {
            throw new UnauthorizedUserException("Error: unauthorized");
        }
        // Generate an authToken
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(request.username(), newAuthToken);
        auth.createAuthData(newAuthData);

        return new LoginResult(newAuthData.username(), newAuthData.authToken());
    }

    public void logout(LogoutRequest request)
            throws DataAccessException, TokenNotFoundException{
        // Find authData from authToken
        if(auth.getAuthData(request.authToken()) == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }
        auth.removeAuthData(request.authToken());
    }
}
