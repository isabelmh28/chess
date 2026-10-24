package server;

import dataaccess.*;
import io.javalin.*;
import server.handlers.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;

    public Server() {
        // Initialization
        // Data Access Objects
        UserDAO user = new MemoryUserDAO();
        AuthDAO auth = new MemoryAuthDAO();
        GameDAO game = new MemoryGameDAO();
        // Services
        UserService userService = new UserService(user, auth);
        GameService gameService = new GameService(auth, game);
        clearService = new ClearService(user, auth, game);
        // Handlers
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        // Javalin
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        // Clear Endpoint
        javalin.delete("/db",
                ctx-> {
                                clearService.clear();
                                ctx.status(200);
                                ctx.result("{}");
                              }
        );
        // User Endpoints
        // Register Endpoint
        javalin.post("/user", userHandler::register);
        // Login Endpoint
        javalin.post("/session", userHandler::login);
        // Logout Endpoint
        javalin.delete("/session", userHandler::logout);
        // Game Endpoints
        // CreateGame Endpoint
        javalin.post("/game", gameHandler::create);
        // ListGames Endpoint
        javalin.get("/game", gameHandler::list);
        // JoinGame Endpoint
        javalin.put("/game", gameHandler::join);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
