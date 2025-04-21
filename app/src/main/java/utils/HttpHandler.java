package utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    public static String uploadFile(String requestUrl, String fileField, String fileName, byte[] fileData, Map<String, String> params) throws Exception {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try (OutputStream os = connection.getOutputStream()) {
            // Add form fields
            for (Map.Entry<String, String> entry : params.entrySet()) {
                os.write((twoHyphens + boundary + lineEnd).getBytes());
                os.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd + lineEnd).getBytes());
                os.write((entry.getValue() + lineEnd).getBytes());
            }

            // Add file part
            os.write((twoHyphens + boundary + lineEnd).getBytes());
            os.write(("Content-Disposition: form-data; name=\"" + fileField + "\"; filename=\"" + fileName + "\"" + lineEnd).getBytes());
            os.write(("Content-Type: application/octet-stream" + lineEnd + lineEnd).getBytes());
            os.write(fileData);
            os.write(lineEnd.getBytes());

            // End of multipart
            os.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
            os.flush();
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