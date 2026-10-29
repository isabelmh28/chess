package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import transactions.request.*;
import transactions.result.*;
import chess.ChessGame;


/**
 * Performs Services related to Games (creating, joining, listing games)
 */
public class GameService {
    private final AuthDAO auth;
    private final GameDAO game;

    public GameService(AuthDAO auth, GameDAO game) {
        this.auth = auth;
        this.game = game;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws DataAccessException, InvalidRequestException, TokenNotFoundException {
        // Check for bad request
        if((request == null) || (request.gameName() == null) || (request.gameName().isBlank())) {
            throw new InvalidRequestException("Error: bad request");
        }
        // Find authData from authToken
        if(auth.getAuthData(authToken) == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }
        // Create new Game
        // Generate a random, positive gameID
        int gameID = UUID.randomUUID().hashCode() & 0x7FFFFFFF;
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(gameID, null, null, request.gameName(), chessGame);
        game.createGame(newGame);

        return new CreateGameResult(newGame.gameID());
    }

    public ListGamesResult listGames(ListGamesRequest request)
            throws DataAccessException, TokenNotFoundException {
        // Find authData from authToken
        if(auth.getAuthData(request.authToken()) == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }

        // Get list of Games
        Collection<GameData> gameList = new ArrayList<>(game.getGameList());

        return new ListGamesResult(gameList);
    }

    public void joinGame(String authToken, JoinGameRequest request)
            throws DataAccessException, TokenNotFoundException, InvalidRequestException,
                   AlreadyTakenException {
        // Find authData from authToken
        AuthData checkAuth = auth.getAuthData(authToken);
        if(checkAuth == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }

        // Get game from gameID
        GameData checkGame = game.getGame(request.gameID());
        if(checkGame == null) {
            throw new InvalidRequestException("Error: bad request");
        }

        // Update Game with join info
        String playerColor = request.playerColor();
        String username = checkAuth.username();
        // Check if player color is already taken
        String whiteUser = checkGame.whiteUsername();
        String blackUser = checkGame.blackUsername();
        if ("WHITE".equals(playerColor)) {
            if((whiteUser != null) && (!whiteUser.isBlank())) {
                throw new AlreadyTakenException("Error: already taken");
            }
            whiteUser = username;
        }
        else if ("BLACK".equals(playerColor)) {
            if((blackUser != null) && (!blackUser.isBlank())) {
                throw new AlreadyTakenException("Error: already taken");
            }
            blackUser = username;
        }
        else {throw new InvalidRequestException("Error: bad request");}
        // New Game
        GameData updateGame = new GameData(
                checkGame.gameID(),
                whiteUser,
                blackUser,
                checkGame.gameName(),
                checkGame.game()
        );
        game.updateGame(updateGame);
    }
}
