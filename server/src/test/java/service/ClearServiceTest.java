package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transactions.request.*;
import transactions.result.*;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    ClearService clearService;
    UserService userService;
    GameService gameService;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
    }

    @Test
    void clear() throws Exception{
        RegisterRequest register1 = new RegisterRequest("user1", "password1", "email1");
        RegisterRequest register2 = new RegisterRequest("user2", "password2", "email2");

        userService.register(register1);
        userService.register(register2);

        LoginRequest login1 = new LoginRequest("user1", "password1");
        LoginResult auth1 = userService.login(login1);

        CreateGameRequest game1 = new CreateGameRequest("game1");
        CreateGameRequest game2 = new CreateGameRequest("game2");

        CreateGameResult create1 = gameService.createGame(auth1.authToken(), game1);
        CreateGameResult create2 = gameService.createGame(auth1.authToken(), game2);

        ListGamesRequest list = new ListGamesRequest(auth1.authToken());
        ListGamesResult games = gameService.listGames(list);
        assertFalse(games.games().isEmpty(), "Games list should contain games before clear");

        clearService.clear();

        assertNotNull(userService.register(register1),
                "Clearance should allow user 1 to register again");
        assertNotNull(userService.register(register2),
                "Clearance should allow user 2 to register again");

        LogoutRequest oldLogout = new LogoutRequest(auth1.authToken());
        assertThrows(TokenNotFoundException.class, ()->userService.logout(oldLogout),
                "Old AuthToken should no longer be valid after clear");

        LoginRequest newLogin = new LoginRequest("user1", "password1");
        LoginResult newToken = userService.login(newLogin);

        ListGamesRequest list1 = new ListGamesRequest(newToken.authToken());
        ListGamesResult newGames = gameService.listGames(list1);
        assertTrue(newGames.games().isEmpty(), "Games list should be empty");
    }
}