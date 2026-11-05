package ui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * Handles HttpClient logic for /user, /session, /game endpoints.
 * Manages sending auth tokens via headers and deserializing responses.
 */

public class ServerFacade {
    private final HttpClient http = HttpClient.newHttpClient();
    private final String url;

    public static final int SUCCESS = 200;

    public ServerFacade(int port) {
        this.url = "http://localhost:" + port;
    }
    public ServerFacade(String url) {
        this.url = url;
    }

    // Endpoints
    public void clear() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/db"))
                .DELETE()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != SUCCESS) {
            throw new Exception("Error clearing database: " + response.body());
        }
    }
}
