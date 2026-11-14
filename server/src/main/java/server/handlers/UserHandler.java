/**
 * UserHandler Module
 * Handler for Server User endpoints
 * Author: Isabel Hinton
 */

package server.handlers;

import com.google.gson.JsonSyntaxException;
import model.transactions.request.LoginRequest;
import model.transactions.request.RegisterRequest;
import model.transactions.result.LoginResult;
import model.transactions.result.RegisterResult;

import dataaccess.exceptions.*;

import service.UserService;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class UserHandler {
    // Private Members
    private final UserService userService;
    private final Gson serializer = new Gson();
    // Constructor
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    // Endpoints
    /**
     * Register User
     * @param ctx information from javalin in Server
     */
    public void register(Context ctx) {
        try {
            // Check Parameters
            if(ctx.body().isEmpty()) {
                ctx.status(400);
                ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
                return;
            }

            RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);

            // Success
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (JsonSyntaxException | InvalidRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
        }
        catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(new ErrorResponse("Error: already taken")));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }
    /**
     * Log in User
     * @param ctx information from javalin in Server
     */
    public void login(Context ctx) {
        try {
            // Check Parameters
            if(ctx.body().isEmpty()) {
                ctx.status(400);
                ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
                return;
            }

            LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);

            // Success
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (JsonSyntaxException | InvalidRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
        }
        catch (UnauthorizedUserException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }
    /**
     * Log out User
     * @param ctx information from javalin in Server
     */
    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if((authToken == null) || (authToken.isEmpty())) {
                ctx.status(401);
                ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
            }

            userService.logout(authToken);

            // Success
            ctx.status(200);
            ctx.result("{}");
        }
        catch (TokenNotFoundException e) {
        ctx.status(401);
        ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
        }
        catch (DataAccessException e) {
        ctx.status(500);
        ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }
}
