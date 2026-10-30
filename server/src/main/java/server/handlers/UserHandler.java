package server.handlers;

import dataaccess.exceptions.*;
import service.UserService;
import transactions.request.*;
import transactions.result.*;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class UserHandler {
    private final UserService userService;
    private final Gson serializer = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);

            // Success
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(new ErrorResponse("Error: already taken")));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
        catch (InvalidRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
        }
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);

            // Success
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (UnauthorizedUserException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
        catch (InvalidRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            LogoutRequest request = new LogoutRequest(authToken);
            userService.logout(request);

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
