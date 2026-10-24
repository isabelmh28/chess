package server.handlers;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import service.GameService;
import transactions.request.*;
import transactions.result.*;
import dataaccess.*;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {this.gameService = gameService;}

    public void create(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest request = new Gson().fromJson(ctx.body(), CreateGameRequest.class);

            CreateGameResult result = gameService.createGame(authToken, request);

            // Success
            ctx.status(200);
            ctx.json(result);
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
        catch (TokenNotFoundException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));
        }
        catch (InvalidRequestException e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));
        }
    }

    public void list(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = gameService.listGames(request);

            // Success
            ctx.status(200);
            ctx.json(result);
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

    public void join(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, request);

            // Success
            ctx.status(200);
            ctx.result("{}");
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
        catch (TokenNotFoundException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));
        }
        catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.json(new ErrorResponse("Error: already taken"));
        }
        catch (InvalidRequestException e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));
        }
    }

}
