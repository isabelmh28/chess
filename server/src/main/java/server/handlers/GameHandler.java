/**
 * GameHandler Module
 * Handler for Server Game endpoints
 * Author: Isabel Hinton
 */

package server.handlers;

import com.google.gson.JsonSyntaxException;
import model.transactions.request.CreateGameRequest;
import model.transactions.request.JoinGameRequest;
import model.transactions.result.CreateGameResult;
import model.transactions.result.ListGamesResult;

import dataaccess.exceptions.*;

import service.GameService;

import io.javalin.http.Context;
import com.google.gson.Gson;

public class GameHandler {
    // Private Members
    private final GameService gameService;
    private final Gson serializer = new Gson();
    // Constructor
    public GameHandler(GameService gameService) {this.gameService = gameService;}

    // Game Endpoints
    /**
     * Create a game
     * @param ctx information from javalin in Server
     */
    public void create(Context ctx) {
        try {
            // Check Parameters
            if(ctx.body().isEmpty()) {
                ctx.status(400);
                ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
                return;
            }
            String authToken = ctx.header("authorization");
            if((authToken == null) || (authToken.isEmpty())) {
                ctx.status(401);
                ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
            }

            CreateGameRequest request = serializer.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(authToken, request);

            // Success
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (JsonSyntaxException | InvalidRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
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
    /**
     * List games
     * @param ctx information from javalin in Server
     */
    public void list(Context ctx) {
        try {
            // Check Parameters
            String authToken = ctx.header("authorization");
            if((authToken == null) || (authToken.isEmpty())) {
                ctx.status(401);
                ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
            }

            ListGamesResult result = gameService.listGames(authToken);

            // Success
            ctx.status(200);
            ctx.result(serializer.toJson(result));
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
    /**
     * Join a game
     * @param ctx information from javalin in Server
     */
    public void join(Context ctx) {
        try {
            // Check Parameters
            if(ctx.body().isEmpty()) {
                ctx.status(400);
                ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
                return;
            }
            String authToken = ctx.header("authorization");
            if((authToken == null) || (authToken.isEmpty())) {
                ctx.status(401);
                ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
            }

            JoinGameRequest request = serializer.fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, request);

            // Success
            ctx.status(200);
            ctx.result("{}");
        }
        catch (JsonSyntaxException | InvalidRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(new ErrorResponse("Error: bad request")));
        }
        catch (TokenNotFoundException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(new ErrorResponse("Error: unauthorized")));
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

}
