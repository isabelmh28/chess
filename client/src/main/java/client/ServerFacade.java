package client;

import com.google.gson.Gson;
import model.transactions.request.*;
import model.transactions.result.*;

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

    public static final int SUCCESS = 200;

    // Constructor
    public ServerFacade(int port) {
        this.url = "http://localhost:" + port;
    }

    // Helper Functions
    public void printError(HttpResponse<String> response) throws ResponseException {
        var errMap = serializer.fromJson(response.body(), java.util.Map.class);
        String message = (errMap != null && errMap.containsKey("message")) ? (String) errMap.get("message") : response.body();
        throw new ResponseException(response.statusCode(), message);
    }

    private <T> T sendRequest(HttpRequest request, Class<T> responseClass) throws ResponseException {
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if(statusCode != SUCCESS) {
                printError(response);
            }

            if((responseClass == null) || (response.body() == null) || (response.body().isEmpty())) {
                return null;
            }
            return serializer.fromJson(response.body(), responseClass);
        } catch (java.io.IOException | InterruptedException e) {
            throw new ResponseException(500, "Http send error: " + e.getMessage());
        }
    }

    // Endpoints
    public void clear() throws ResponseException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/db"))
                .DELETE()
                .build();
        sendRequest(request, null);
    }

    public RegisterResult register(RegisterRequest register) throws ResponseException {
        String postBody = serializer.toJson(register);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        return sendRequest(request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest login) throws ResponseException {
        String postBody = serializer.toJson(login);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        return sendRequest(request, LoginResult.class);
    }

    public void logout(LogoutRequest logout) throws ResponseException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/session"))
                .header("authorization", logout.authToken())
                .DELETE()
                .build();
        sendRequest(request, null);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGame) throws ResponseException {
        String postBody = serializer.toJson(createGame);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/game"))
                .header("Content-Type", "application/json") // for the POST operation
                .header("authorization", authToken)               // Specs
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        return sendRequest(request, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/game"))
                .header("authorization", authToken) // Specs
                .GET()
                .build();
        return sendRequest(request, ListGamesResult.class);
    }

    public void joinGame(String authToken, JoinGameRequest join) throws ResponseException {
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
