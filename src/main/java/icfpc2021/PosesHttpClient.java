package icfpc2020;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Interact with https://poses.live API
 */
public class PosesHttpClient {
    private static final Logger log = LoggerFactory.getLogger(PosesHttpClient.class);

    /**
     * Download and store all problems.
     */
    public static void main(String[] args) {
        var serverUrl = "https://poses.live/api";
        var apiToken = "f1b30d61-a94e-4980-bd2d-66f8b9dd8714";
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/hello"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Authorization", "Bearer " + apiToken)
                    .GET()
                    .build();

            var response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            var status = response.statusCode();

            if (status != HttpURLConnection.HTTP_OK) {
                log.error("Unexpected server response:");
                log.error("HTTP code: {}", status);
                log.error("Response body: {}", response.body());
                System.exit(2);
            }
            log.info("Server response: {}", response.body());
        } catch (Exception e) {
            log.error("Unexpected server response", e);
            System.exit(1);
        }
    }
}
