/**
 * GameService Module
 * Services Game endpoints:
 *    CreateGame
 *    ListGames
 *    JoinGame
 * Author: Isabel Hinton
 */

package service;

import chess.ChessGame;

import dataaccess.doa.interfaces.AuthDAO;
import dataaccess.doa.interfaces.GameDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidRequestException;
import dataaccess.exceptions.TokenNotFoundException;

import model.AuthData;
import model.GameData;
import model.transactions.request.CreateGameRequest;
import model.transactions.request.JoinGameRequest;
import model.transactions.result.CreateGameResult;
import model.transactions.result.ListGamesResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class GameService {
    // Private Members
    private final AuthDAO auth;
    private final GameDAO game;
    // Constructor
    public GameService(AuthDAO auth, GameDAO game) {
        this.auth = auth;
        this.game = game;
    }

    // Endpoints
    /**
     * Creates a game given a request.
     * @param request The service request to create a new game.
     * @return CreateGameResult.
     * See transactions package for more information on requests and results.
     */
    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws DataAccessException, InvalidRequestException, TokenNotFoundException {
        // Check for bad request
        if((request == null) || (request.gameName() == null) || (request.gameName().isBlank())) {
            throw new InvalidRequestException("Error: bad request");
        }
        // Find authData from authToken
        if(authToken == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }
        if(auth.getAuthData(authToken) == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }

        // Create new Game
        // Generate a random, positive gameID
        int gameID = UUID.randomUUID().hashCode() & 0x7FFFFFFF;
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(
                gameID, null, null, request.gameName(), chessGame);
        game.createGame(newGame);

        return new CreateGameResult(newGame.gameID());
    }
    /**
     * Creates a game given a request.
     * @param authToken There is no request body, only an authToken header.
     * @return ListGamesResult.
     * See transactions package for more information on requests and results.
     */
    public ListGamesResult listGames(String authToken)
            throws DataAccessException, TokenNotFoundException {
        // Find authData from authToken
        if((authToken == null) || (auth.getAuthData(authToken) == null)) {
            throw new TokenNotFoundException("Error: unauthorized");
        }

        // Get list of Games
        Collection<GameData> gameList = new ArrayList<>(game.getGameList());

        return new ListGamesResult(gameList);
    }
    /**
     * Joins a game given a request.
     * @param authToken authToken header.
     * @param request   The service request for joining a game.
     * See transactions package for more information on requests and results.
     */
    public void joinGame(String authToken, JoinGameRequest request)
            throws DataAccessException, TokenNotFoundException, InvalidRequestException,
            AlreadyTakenException {
        // Get game from gameID
        if((request == null) || (request.gameID() < 0) || (request.playerColor() == null)) {
            throw new InvalidRequestException("Error: bad request");
        }
        // Find authData from authToken
        if(authToken == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }
        AuthData checkAuth = auth.getAuthData(authToken);
        if(checkAuth == null) {
            throw new TokenNotFoundException("Error: unauthorized");
        }
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
        if (ChessGame.TeamColor.WHITE.name().equals(playerColor)) {
            if((whiteUser != null) && (!whiteUser.isBlank())) {
                throw new AlreadyTakenException("Error: already taken");
            }
            whiteUser = username;
        }
        else if (ChessGame.TeamColor.BLACK.name().equals(playerColor)) {
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
