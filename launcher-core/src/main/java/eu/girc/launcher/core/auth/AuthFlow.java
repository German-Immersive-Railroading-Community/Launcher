package eu.girc.launcher.core.auth;

import eu.girc.launcher.core.models.auth.DeviceCodeResponse;
import eu.girc.launcher.core.utils.Constants;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AuthFlow {
    private static final HttpClient client = HttpClient.newHttpClient();

    public boolean authenticationFilePresent() {
        return Path.of("").toFile().exists();
    }

    public Optional<DeviceCodeResponse> init() throws ExecutionException, InterruptedException {
        return Optional.ofNullable(CompletableFuture.supplyAsync(() -> {
            try {
                final String params = "client_id=" + AuthConstants.MICROSOFT_CLIENT_ID + "&scope=" + AuthConstants.MICROSOFT_OAUTH_SCOPES.replace(" ", "%20");

                final HttpRequest req = HttpRequest.newBuilder(AuthConstants.MICROSOFT_OAUTH_DEVICECODE_URI).POST(HttpRequest.BodyPublishers.ofString(params)).setHeader("Content-Type", "application/x-www-form-urlencoded").build();
                final HttpResponse<String> res;
                res = client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).get();

                if (res.statusCode() >= 300) {
                    System.out.println(res.body());
                    throw new RuntimeException("Non-Success Status Code: " + res.statusCode());
                }

                return Constants.GSON.fromJson(res.body(), DeviceCodeResponse.class);
            } catch (final ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).get());
    }
}
