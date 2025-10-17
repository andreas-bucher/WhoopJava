package ch.arcticsoft.javawhoop;

import java.net.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import com.fasterxml.jackson.databind.*;

public class OAuth {
	private static final HttpClient HTTP = HttpClient.newBuilder()
		      .connectTimeout(Duration.ofSeconds(10)).build();
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static void main(String[] args) throws Exception {
	    // ---- Configure these: ----
	    String tokenEndpoint = "https://YOUR_IDP/oauth2/token";
	    String clientId      = "YOUR_CLIENT_ID";
	    String clientSecret  = "YOUR_CLIENT_SECRET";
	    String scope         = "api.read"; // or blank if your IdP doesn’t require scope
	    String apiUrl        = "https://api.example.com/data";

	    String accessToken = fetchAccessToken(tokenEndpoint, clientId, clientSecret, scope);
	    String apiResponse = callApi(apiUrl, accessToken);
	    System.out.println(apiResponse);
	}

	static String fetchAccessToken(String tokenEndpoint, String clientId, String clientSecret, String scope) throws Exception {
	    String body = "grant_type=client_credentials"
	        + (scope == null || scope.isBlank() ? "" : "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8));

	    HttpRequest req = HttpRequest.newBuilder()
	        .uri(URI.create(tokenEndpoint))
	        .header("Content-Type", "application/x-www-form-urlencoded")
	        .header("Authorization", basicAuth(clientId, clientSecret))
	        .POST(HttpRequest.BodyPublishers.ofString(body))
	        .build();

	    HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
	    if (res.statusCode() != 200) {
	      throw new RuntimeException("Token request failed: " + res.statusCode() + " → " + res.body());
	    }
	    JsonNode json = MAPPER.readTree(res.body());
	    return json.get("access_token").asText();
	}

	static String callApi(String apiUrl, String token) throws Exception {
	    HttpRequest req = HttpRequest.newBuilder()
	        .uri(URI.create(apiUrl))
	        .header("Authorization", "Bearer " + token)
	        .GET()
	        .build();

	    HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
	    if (res.statusCode() / 100 != 2) {
	      throw new RuntimeException("API call failed: " + res.statusCode() + " → " + res.body());
	    }
	    return res.body();
	}

	 static String basicAuth(String user, String pass) {
	    String raw = user + ":" + pass;
	    String b64 = java.util.Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
	    return "Basic " + b64;
}
}
