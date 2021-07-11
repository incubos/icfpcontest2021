package icfpc2021;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

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
        var problemsCount = 106;

        try {
            Files.createDirectory(Path.of("problems"));
        } catch (IOException ignored) {}

        for (int i = 0; i < problemsCount; i++) {
            try {
                var request = HttpRequest.newBuilder()
                        .uri(URI.create(String.format("%s/problems/%d", serverUrl, i + 1)))
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
                }
                System.out.print(".");
                System.out.flush();
                var path = Path.of("problems", String.format("%03d.json", i + 1));
                Files.writeString(path, response.body());
            } catch (Exception e) {
                log.error("Unexpected server response", e);
                System.exit(1);
            }
        }
    }
}
