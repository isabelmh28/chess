package server;

import service.*;
import dataaccess.*;
import dataaccess.doa.interfaces.*;
import dataaccess.doa.memory.*;
import dataaccess.doa.sql.*;
import dataaccess.exceptions.*;
import server.handlers.*;

import io.javalin.*;
import java.sql.SQLException;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;

    public Server() {
        // Initialization
        // Data Access Objects
        UserDAO user = new SQLUserDAO();
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

    private void dbSetup() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            // Initialize the Database with User, Auth, and Game tables
            String[] createChessDB = {
                    """
                    CREATE TABLE IF NOT EXISTS user (
                        username VARCHAR(255) PRIMARY KEY,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL
                    )
                    """,
                    """
                    CREATE TABLE IF NOT EXISTS auth (
                        authToken VARCHAR(255) PRIMARY KEY,
                        username VARCHAR(255) NOT NULL
                    )
                    """,
                    """
                    CREATE TABLE IF NOT EXISTS game (
                        gameID INT PRIMARY KEY,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255) NOT NULL,
                        gameJson TEXT NOT NULL
                    )
                    """
            };

            for(String table : createChessDB) {
                try (var createTable = connection.prepareStatement(table)) {
                    createTable.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to set up the database", e);
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        try {
            dbSetup();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initalize database during run", e);
        }
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
