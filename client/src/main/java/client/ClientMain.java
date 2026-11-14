package client;

import chess.*;
import model.transactions.request.*;
import model.transactions.result.*;
import client.REPL;

import java.net.URI;
import com.google.gson.Gson;
import ui.EscapeSequences;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println(
                        EscapeSequences.SET_TEXT_COLOR_YELLOW + "♕" +
                        EscapeSequences.SET_TEXT_COLOR_MAGENTA + " 240 Chess Client: Welcome!\n" +
                        EscapeSequences.RESET_TEXT_COLOR);

        int port = 8080;
        if(args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        REPL repl = new REPL(port);
        repl.run();
    }

    /**
     * Manages the Scanner input loop, tracks current user state,
     * processes user commands, and prints user output
     */

    /**
     * Handles HttpClient logic for /user, /session, /game endpoints.
     * Manages sending auth tokens via headers and deserializing responses.
     */

    public static class ServerFacade {
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

        public void logout(String authToken) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/session"))
                    .header("authorization", authToken)
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

        public void joinGame(String authToken, JoinGameRequest join) throws Exception {
            String postBody = serializer.toJson(join);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/game"))
                    .header("Content-Type", "application/json") // for the POST operation
                    .header("authorization", authToken)               // Specs
                    .PUT(HttpRequest.BodyPublishers.ofString(postBody))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != SUCCESS) {
                throw new Exception("Error joining game: " + response.body());
            }
        }
    }
}
