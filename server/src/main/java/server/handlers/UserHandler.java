package server.handlers;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import service.UserService;
import transactions.request.*;
import transactions.result.*;
import dataaccess.*;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);

            // Success
            ctx.status(200);
            ctx.json(result);
        }
        catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(new ErrorResponse("Error: already taken"));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
        catch (Exception e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);

            // Success
            ctx.status(200);
            ctx.json(result);
        }
        catch (UnauthorizedUserException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
        catch (Exception e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));
        }
    }

    public void logout(Context ctx) {
        try {

            String authToken = ctx.header("authorization");
            LogoutRequest request = new LogoutRequest(authToken);
            userService.logout(request);

            // Success
            ctx.status(200);
            ctx.json("{}");
        }
        catch (TokenNotFoundException e) {
        ctx.status(401);
        ctx.json(new ErrorResponse("Error: unauthorized"));
        }
        catch (DataAccessException e) {
        ctx.status(500);
        ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
}
