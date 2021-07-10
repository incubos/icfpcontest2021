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
import java.nio.file.Paths;

public class SubmissionClient {
    private static final Logger log = LoggerFactory.getLogger(PosesHttpClient.class);

    public static void main(String[] args) throws IOException {
        Files.list(Paths.get(args[0]))
                .forEach(o -> {
                    final String name = o.getFileName().toString();
                    final String problem = name.substring(0, name.indexOf(".json"));
                    try {
                        final String pose = Files.readString(o);
                        log.info("Submitting solution {}", problem);
                        new SubmissionClient().submit(problem, pose);
                        log.info("Submitted solution {}", problem);
                    } catch (IOException e) {
                        log.error("Error when submitting solution {}", problem, e);
                    }
                });
    }

    private static void submit(String problem, String pose) {
        var serverUrl = "https://poses.live/api";
        var apiToken = "f1b30d61-a94e-4980-bd2d-66f8b9dd8714";

        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/problems/%s/solutions", serverUrl, problem)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Authorization", "Bearer " + apiToken)
                    .POST(HttpRequest.BodyPublishers.ofString(pose))
                    .build();

            var response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            var status = response.statusCode();

            if (status != HttpURLConnection.HTTP_OK) {
                log.error("Unexpected server response:");
                log.error("HTTP code: {}", status);
                log.error("Response body: {}", response.body());
            }
        } catch (Exception e) {
            log.error("Unexpected server response", e);
            System.exit(1);
        }
    }
}
