package icfpc2021;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.net.http.*;

class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            var serverUrl = args[0];
            var playerKey = args[1];

            log.info("ServerUrl: {}; PlayerKey: {}", serverUrl, playerKey);

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(playerKey))
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
