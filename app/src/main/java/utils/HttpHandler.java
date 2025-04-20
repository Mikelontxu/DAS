package utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpHandler {

    private static final String TAG = "HttpHandler";

    public static String makeRequest(String requestUrl, String method, String params) throws Exception {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setConnectTimeout(10000); // 10 seconds
        connection.setReadTimeout(10000); // 10 seconds

        Log.d(TAG, "Request Params (raw): " + params);

        if (method.equals("POST") || method.equals("PUT")) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json"); // Cambiar a JSON
            try (OutputStream os = connection.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
                os.flush();

                Log.d(TAG, "Request URL: " + requestUrl);
                Log.d(TAG, "Request Method: " + method);
                Log.d(TAG, "Request Body: " + params);
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            throw new Exception("HTTP error code: " + responseCode);
        }
    }
}