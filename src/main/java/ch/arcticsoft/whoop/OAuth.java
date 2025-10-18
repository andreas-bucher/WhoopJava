package ch.arcticsoft.whoop;

/**

import java.net.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.fasterxml.jackson.databind.*;

public class OAuth {
	private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
	private static final ObjectMapper MAPPER = new ObjectMapper();

	

	public static void main(String[] args) throws Exception {
	    // ---- Configure these: ----
	    String tokenEndpoint = "https://api.prod.whoop.com/oauth/oauth2/token";
	    String clientId      = "cdcf5a0c-6022-468d-8041-b4de95d9086b";
	    String clientSecret  = "0ce83e2b4d65a104ed3846273730573687348d1c92191f64856d0acb311ff953";
	    String redirect_uri  = "https://arcticsoft.ch/whoop/callback";
	    String scope         = "read:workout"; // or blank if your IdP doesn’t require scope
	    String apiUrl        = "https://api.example.com/data";

	    //String code = fetchCode2();
	    
	    String accessToken = fetchAccessToken(tokenEndpoint, clientId, clientSecret, scope);
	    String apiResponse = callApi(apiUrl, accessToken);
	    System.out.println(apiResponse);
	}

	
	//
	// https://api.prod.whoop.com/oauth/oauth2/auth?response_type=code&client_id=cdcf5a0c-6022-468d-8041-b4de95d9086b&redirect_uri=https://arcticsoft.ch/whoop/callback&scope=read:workout&state=AB6ZTDH4CD
	//
	static String fetchCode()throws Exception {
		String codeEndpoint = "https://api.prod.whoop.com/oauth/oauth2/auth"
				+ "?response_type=code"
				+ "&client_id=cdcf5a0c-6022-468d-8041-b4de95d9086b"
				+ "&redirect_uri=https://arcticsoft.ch/whoop/callback"
				+ "&scope=read:workout" 
				+ "&state=AB6ZTDH4CD";
		
		
		System.out.println(codeEndpoint);
		
	    HttpRequest req = HttpRequest.newBuilder()
	        .uri(URI.create(codeEndpoint))
	        .header("Content-Type", "application/x-www-form-urlencoded")
	        .build();
	    

	    HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
	    if (res.statusCode() != 200) {
	      throw new RuntimeException("Token request failed: " + res.statusCode() + " → " + res.body());
	    }
	    JsonNode json = MAPPER.readTree(res.body());
	    return json.get("access_token").asText();	

	}
	

	static String fetchCode2()throws Exception {
		String codeEndpoint = "https://api.prod.whoop.com/oauth/oauth2/auth"
				+ "?response_type=code"
				+ "&client_id=cdcf5a0c-6022-468d-8041-b4de95d9086b"
				+ "&redirect_uri=https://arcticsoft.ch/whoop/callback"
				+ "&scope=read:workout"
				+ "&state=AB6ZTDH4CD";
		
		
		System.out.println(codeEndpoint);
		
	    HttpRequest req = HttpRequest.newBuilder()
	        .uri(URI.create(codeEndpoint))
	        .header("Content-Type", "application/x-www-form-urlencoded")
	        .build();
	    

	    HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
	    if (res.statusCode() != 200) {
	      throw new RuntimeException("Token request failed: " + res.statusCode() + " → " + res.body());
	    }
	    JsonNode json = MAPPER.readTree(res.body());
	    return json.get("access_token").asText();	

	}
	

	static String fetchAccessToken(String tokenEndpoint, String clientId, String clientSecret, String scope) throws Exception {

		/**String body = "grant_type=client_credentials"
				+ "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
				+ "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
				+ "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8);
		**/
/**
		String body = "grant_type=authorization_code"
				+ "&redirect_uri=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
				+ "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
				+ "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
				+ "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8);

		
		System.out.println(body);
		
	    HttpRequest req = HttpRequest.newBuilder()
	        .uri(URI.create(tokenEndpoint))
	        .header("Content-Type", "application/x-www-form-urlencoded")
	        //.header("Authorization", basicAuth(clientId, clientSecret))
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
**/
