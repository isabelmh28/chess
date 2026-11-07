package ui;

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

    // Constructors
    public ServerFacade(int port) {
        this.url = "http://localhost:" + port;
    }

    // Endpoints
    public void clear() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/db"))
                .DELETE()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != SUCCESS) {
            throw new Exception("Error clearing database: " + response.body());
        }
    }

    public RegisterResult register(RegisterRequest register) throws Exception {
        String postBody = serializer.toJson(register);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != SUCCESS) {
            throw new Exception("Error registering user: " + response.body());
        }
        return serializer.fromJson(response.body(), RegisterResult.class);
    }

    public LoginResult login(LoginRequest login) throws Exception {
        String postBody = serializer.toJson(login);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != SUCCESS) {
            throw new Exception("Error logging in user: " + response.body());
        }
        return serializer.fromJson(response.body(), LoginResult.class);
    }

    public void logout(LogoutRequest logout) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/session"))
                .header("authorization", logout.authToken())
                .DELETE()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != SUCCESS) {
            throw new Exception("Error logging out user: " + response.body());
        }
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGame) throws Exception {
        String postBody = serializer.toJson(createGame);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/game"))
                .header("Content-Type", "application/json") // for the POST operation
                .header("authorization", authToken)               // Specs
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != SUCCESS) {
            throw new Exception("Error creating game: " + response.body());
        }
        return serializer.fromJson(response.body(), CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/game"))
                .header("authorization", authToken) // Specs
                .GET()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != SUCCESS) {
            throw new Exception("Error listing games: " + response.body());
        }
        return serializer.fromJson(response.body(), ListGamesResult.class);
    }
}
