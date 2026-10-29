package service;

import dataaccess.doa.interfaces.AuthDAO;
import dataaccess.doa.interfaces.GameDAO;
import dataaccess.doa.memory.MemoryAuthDAO;
import dataaccess.doa.memory.MemoryGameDAO;
import dataaccess.doa.memory.MemoryUserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.InvalidRequestException;
import dataaccess.exceptions.TokenNotFoundException;
import dataaccess.doa.interfaces.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transactions.request.*;
import transactions.result.*;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
        GameDAO game = new MemoryGameDAO();

        userService = new UserService(user, auth);
        gameService = new GameService(auth, game);
        // When moving to SQL, will need to clear as well
    }

    // CreateGame Endpoint Testing
    // Success
    @Test
    void createGameSuccess() throws Exception{
        RegisterRequest register = new RegisterRequest("user", "password", "email");
        RegisterResult registerResult = userService.register(register);

        CreateGameRequest create = new CreateGameRequest("game");
        CreateGameResult createResult = gameService.createGame(registerResult.authToken(), create);

        assertNotNull(createResult, "CreateGameResult should not be null");
        assertTrue(createResult.gameID() > 0, "GameID should be a random positive number");
    }
    // Invalid Request
    @Test
    void createGameNullInvalidRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "email");
        RegisterResult register = userService.register(registerRequest);

        assertThrows(InvalidRequestException.class, ()->gameService.createGame(register.authToken(), null),
                "Null request object should return InvalidRequestException");
    }
    @Test
    void createGameNullGameNameInvalidRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "email");
        RegisterResult register = userService.register(registerRequest);

        CreateGameRequest request = new CreateGameRequest(null);

        assertThrows(InvalidRequestException.class, ()->gameService.createGame(register.authToken(), request),
                "Null gameName should cause InvalidRequestException");
    }
    @Test
    void createGameEmptyGameNameInvalidRequest() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "email");
        RegisterResult register = userService.register(registerRequest);

        CreateGameRequest request = new CreateGameRequest("");

        assertThrows(InvalidRequestException.class, ()->gameService.createGame(register.authToken(), request),
                "Empty/Blank gameName should cause InvalidRequestException");
    }
    // Token Not Found
    @Test
    void createGameTokenNotFound() throws Exception {
        CreateGameRequest request = new CreateGameRequest("gameName");

        assertThrows(TokenNotFoundException.class, ()->gameService.createGame("bad token", request),
                "Token not found should return TokenNotFoundRequest");
    }

    // ListGames Endpoint
    // Success
    @Test
    void listGamesSuccess() throws Exception {
        RegisterRequest register = new RegisterRequest("user1", "password1", "email1");
        userService.register(register);

        LoginRequest login = new LoginRequest("user1", "password1");
        LoginResult auth1 = userService.login(login);

        CreateGameRequest game1 = new CreateGameRequest("game1");
        CreateGameRequest game2 = new CreateGameRequest("game2");

        CreateGameResult create1 = gameService.createGame(auth1.authToken(), game1);
        CreateGameResult create2 = gameService.createGame(auth1.authToken(), game2);

        ListGamesRequest list = new ListGamesRequest(auth1.authToken());
        ListGamesResult games = gameService.listGames(list);

        assertNotNull(games, "ListGamesResult should not be null");
        assertFalse(games.games().isEmpty(), "Games list should not be empty");
        assertEquals(2, games.games().size(), "Games list should contain 2 games");
    }
    // Token Not Found
    @Test
    void listGamesTokenNotFound() throws Exception {
        ListGamesRequest request = new ListGamesRequest("bad token");

        assertThrows(TokenNotFoundException.class, ()->gameService.listGames(request),
                "Bad token should cause a TokenNotFoundException");
    }

    // JoinGame Endpoint
    // Success
    @Test
    void joinGameWhiteSuccess() throws Exception{
        RegisterRequest register = new RegisterRequest("user", "password", "email");
        RegisterResult registerResult = userService.register(register);

        CreateGameRequest create = new CreateGameRequest("game");
        CreateGameResult createResult = gameService.createGame(registerResult.authToken(), create);

        JoinGameRequest join = new JoinGameRequest("WHITE", createResult.gameID());
        assertDoesNotThrow(()->gameService.joinGame(registerResult.authToken(), join),
                "White user should be able to join a game");
    }
    @Test
    void joinGameBlackSuccess() throws Exception{
        RegisterRequest register = new RegisterRequest("user", "password", "email");
        RegisterResult registerResult = userService.register(register);

        CreateGameRequest create = new CreateGameRequest("game");
        CreateGameResult createResult = gameService.createGame(registerResult.authToken(), create);

        JoinGameRequest join = new JoinGameRequest("BLACK", createResult.gameID());
        assertDoesNotThrow(()->gameService.joinGame(registerResult.authToken(), join),
                "White user should be able to join a game");
    }
    // Token Not Found
    @Test
    void joinGameTokenNotFound() throws Exception {
        JoinGameRequest request = new JoinGameRequest("WHITE", 1234);
        assertThrows(TokenNotFoundException.class, ()->gameService.joinGame("bad token", request),
                "Bad Token should cause TokenNotFoundException");
    }
    // Bad Request
    @Test
    void joinGameInvalidRequest() throws Exception {
        RegisterRequest register = new RegisterRequest("user", "password", "email");
        RegisterResult registerResult = userService.register(register);

        JoinGameRequest request = new JoinGameRequest("WHITE", 1234);
        assertThrows(InvalidRequestException.class, ()->gameService.joinGame(registerResult.authToken(), request),
                "Non-Existant GameID should cause InvalidRequestException");
    }
    @Test
    void joinGameBadColorInvalidRequest() throws Exception{
        RegisterRequest register = new RegisterRequest("user1", "password1", "email1");
        RegisterResult registerResult = userService.register(register);

        CreateGameRequest create = new CreateGameRequest("game");
        CreateGameResult createResult = gameService.createGame(registerResult.authToken(), create);

        JoinGameRequest join1 = new JoinGameRequest("YELLOW", createResult.gameID());
        assertThrows(InvalidRequestException.class, ()->gameService.joinGame(registerResult.authToken(), join1),
                "Player Color that is not WHITE or BLACK should cause InvalidRequestException");
    }
    // Already Taken
    // White already taken
    @Test
    void joinGameWhiteAlreadyTaken() throws Exception{
        RegisterRequest register1 = new RegisterRequest("user1", "password1", "email1");
        RegisterRequest register2 = new RegisterRequest("user2", "password2", "email2");

        RegisterResult registerResult1 = userService.register(register1);
        RegisterResult registerResult2 = userService.register(register2);

        CreateGameRequest create = new CreateGameRequest("game");
        CreateGameResult createResult = gameService.createGame(registerResult1.authToken(), create);

        JoinGameRequest join1 = new JoinGameRequest("WHITE", createResult.gameID());
        gameService.joinGame(registerResult1.authToken(), join1);

        JoinGameRequest join2 = new JoinGameRequest("WHITE", createResult.gameID());
        assertThrows(AlreadyTakenException.class, ()->gameService.joinGame(registerResult2.authToken(), join2),
                "White player attempting to register in game with a White player should cause AlreadyRequestException");
    }
    // Black already taken
    @Test
    void joinGameBlackAlreadyTaken() throws Exception{
        RegisterRequest register1 = new RegisterRequest("user1", "password1", "email1");
        RegisterRequest register2 = new RegisterRequest("user2", "password2", "email2");

        RegisterResult registerResult1 = userService.register(register1);
        RegisterResult registerResult2 = userService.register(register2);

        CreateGameRequest create = new CreateGameRequest("game");
        CreateGameResult createResult = gameService.createGame(registerResult1.authToken(), create);

        JoinGameRequest join1 = new JoinGameRequest("BLACK", createResult.gameID());
        gameService.joinGame(registerResult1.authToken(), join1);

        JoinGameRequest join2 = new JoinGameRequest("BLACK", createResult.gameID());
        assertThrows(AlreadyTakenException.class, ()->gameService.joinGame(registerResult2.authToken(), join2),
                "Black player attempting to register in game with a Black player should cause AlreadyTakenException");
    }


}