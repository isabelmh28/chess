/**
 * ServerFacade Module
 * Http Requests for CLI
 * Author: Isabel Hinton
 */

package client;

import model.transactions.result.*;
import model.transactions.request.*;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Handles HttpClient logic for /user, /session, /game endpoints.
 * Manages sending auth tokens via headers and deserializing responses.
 */
public class ServerFacade {
    // Private members
    private final HttpClient http = HttpClient.newHttpClient();
    private final String url;
    private final Gson serializer = new Gson();
    // Status Macro
    public static final int SUCCESS = 200;
    // Constructor
    public ServerFacade(int port) {
        this.url = "http://localhost:" + port;
    }

    // Helper Functions
    /**
     * Prints an error message. If the response has a "message" prefix, remove it so
     * the error response is printed cleanly. Used to make sure user doesn't see raw
     * error output from the pure Json.
     * @param response: response body to be printed
     */
    public void printError(HttpResponse<String> response) throws ResponseException {
        String body = response.body();
        int code    = response.statusCode();
        // Check Parameters
        if((body == null) || body.isEmpty()) {
            throw new ResponseException(code, "Error: server returned status " + code);
        }

        var errMap = serializer.fromJson(body, java.util.Map.class);
        String message =   (errMap != null && errMap.containsKey("message")) ? (String) errMap.get("message")
                         :                                                     body;
        throw new ResponseException(code, message);
    }
    /**
     * Sends an HttpRequest and returns a result if needed by that endpoint.
     * @param request       Http request to be executed.
     * @param responseClass Result Class for the endpoint. EX: RegisterResult.
     * @return Result type specified by the responseClass parameter. Can be null.
     */
    private <T> T sendRequest(HttpRequest request, Class<T> responseClass) throws ResponseException {
        // Check Parameters
        if(request == null) {
            throw new ResponseException(500, "Http Request cannot be null");
        }
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if(statusCode != SUCCESS) {
                printError(response);
            }

            // Some endpoints don't have a return, which is indicated by a null responseClass
            if(responseClass == null) {
                return null;
            }
            // If the endpoint should have returned a response, check for bad body
            if((response.body() == null) || (response.body().isEmpty())) {
                return null;
            }
            return serializer.fromJson(response.body(), responseClass);
        } catch (java.io.IOException | InterruptedException e) {
            throw new ResponseException(500, "Http send error: " + e.getMessage());
        }
    }

    // Endpoints
    /**
     * clear endpoint. Not actually implemented in the CLI so the user doesn't
     * have access to it.
     */
    public void clear() throws ResponseException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/db"))
                .DELETE()
                .build();
        sendRequest(request, null);
    }
    /**
     * register endpoint. Registers a user provided with a RegisterRequest.
     * @param register: provided RegisterRequest to build the HttpRequest
     *        user's desired username, password, and email
     * @return RegisterResult which indicates a successful request.
     *         user's username and authToken generated in registration
     * See transactions package in for more information on Requests and Results.
     */
    public RegisterResult register(RegisterRequest register) throws ResponseException {
        // Check Parameters
        if(register == null) {
            throw new ResponseException(400, "Cannot register with a null request");
        }

        String postBody = serializer.toJson(register);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        return sendRequest(request, RegisterResult.class);
    }
    /**
     * login endpoint. Logs in a user provided with a LoginRequest.
     * @param login: provided LoginRequest to build the HttpRequest
     *        user's username and password from their registration
     * @return LoginResult which indicates a successful request.
     *         user's username and authToken generated in login
     * See transactions package in for more information on Requests and Results.
     */
    public LoginResult login(LoginRequest login) throws ResponseException {
        // Check Parameters
        if(login == null) {
            throw new ResponseException(400, "Cannot login with a null request");
        }

        String postBody = serializer.toJson(login);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        return sendRequest(request, LoginResult.class);
    }
    /**
     * logout endpoint. Logs out a user provided with an authToken
     * @param authToken: provided authToken header to build the HttpRequest
     * Returns no information and has no Request, only an authToken header
     */
    public void logout(String authToken) throws ResponseException {
        if((authToken == null) || authToken.isEmpty()) {
            throw new ResponseException(401, "Bad authToken, cannot logout");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/session"))
                .header("authorization", authToken)
                .DELETE()
                .build();
        sendRequest(request, null);
    }
    /**
     * createGame endpoint. Creates a game using an authToken and a CreateGameRequest
     * @param authToken:  provided authToken header to build the HttpRequest
     * @param createGame: provided CreateGameRequest to build the HttpRequest
     *        gameName for their newly created game
     * @return CreateGameResult which indicates a game was created successfully.
     *         GameID from created game
     * See transactions package in for more information on Requests and Results.
     */
    public CreateGameResult createGame(String authToken, CreateGameRequest createGame) throws ResponseException {
        // Check Parameters
        if((authToken == null) || authToken.isEmpty()) {
            throw new ResponseException(401, "Bad authToken, cannot create game");
        }
        if(createGame == null) {
            throw new ResponseException(400, "Cannot create a game with a null request");
        }

        String postBody = serializer.toJson(createGame);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/game"))
                .header("Content-Type", "application/json") // for the POST operation
                .header("authorization", authToken)               // Specs
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        return sendRequest(request, CreateGameResult.class);
    }
    /**
     * listGames endpoint. Lists the games in the database using an authToken
     * @param authToken:  provided authToken header to build the HttpRequest
     * @return CreateGameResult which indicates a game was created successfully.
     *         Collection of GameData
     * See transactions package in for more information on Requests and Results.
     */
    public ListGamesResult listGames(String authToken) throws ResponseException {
        // Check Parameters
        if((authToken == null) || authToken.isEmpty()) {
            throw new ResponseException(401, "Bad authToken, cannot list games");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/game"))
                .header("authorization", authToken) // Specs
                .GET()
                .build();
        return sendRequest(request, ListGamesResult.class);
    }
    /**
     * joinGame endpoint. Joins a game in the gameList specified by the user
     * @param authToken:  provided authToken header to build the HttpRequest
     * @param join:       provided JoinGameRequest to build the HttpRequest
     *        user's desired player color and the gameID of the game they
     *        wish to join.
     * See transactions package in for more information on Requests and Results.
     */
    public void joinGame(String authToken, JoinGameRequest join) throws ResponseException {
        // Check Parameters
        if((authToken == null) || authToken.isEmpty()) {
            throw new ResponseException(401, "Bad authToken, cannot create game");
        }
        if(join == null) {
            throw new ResponseException(400, "Cannot create a game with a null request");
        }

        String postBody = serializer.toJson(join);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/game"))
                .header("Content-Type", "application/json") // for the POST operation
                .header("authorization", authToken)               // Specs
                .PUT(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        sendRequest(request, null);
    }
}
