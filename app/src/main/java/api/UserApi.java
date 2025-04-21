package api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import utils.HttpHandler;

public class UserApi {
    private static final String BASE_URL = "http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/maranburu011/WEB/api.php";

    public static boolean addUser(String username, String password, String email) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "add_user");
        jsonBody.put("username", username);
        jsonBody.put("password", password);
        jsonBody.put("email", email);

        String response = HttpHandler.makeRequest(BASE_URL, "POST", jsonBody.toString());
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getBoolean("success");
    }

    public static int loginUser(String username, String password) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "login_user");
        jsonBody.put("username", username);
        jsonBody.put("password", password);

        String response = HttpHandler.makeRequest(BASE_URL, "POST", jsonBody.toString());
        Log.d("UserApi", "Server Response: " + response); // Log para depuración
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.getBoolean("success")) {
            JSONObject data = jsonResponse.getJSONObject("data"); // Extrae el objeto `data`
            return data.getInt("user_id"); // Obtiene el `user_id` desde `data`
        } else {
            throw new Exception(jsonResponse.getString("message"));
        }
    }

    public static Bitmap getProfilePicture(int userId) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "get_profile_picture");
        jsonBody.put("user_id", userId);

        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Leer el contenido binario de la imagen
            try (InputStream inputStream = connection.getInputStream()) {
                return BitmapFactory.decodeStream(inputStream); // Convertir a Bitmap
            }
        } else {
            throw new Exception("HTTP error code: " + responseCode);
        }
    }

    public static String getUsername(int userId) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "get_user_name");
        jsonBody.put("user_id", userId);

        String response = HttpHandler.makeRequest(BASE_URL, "POST", jsonBody.toString());
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.getBoolean("success")) {
            JSONObject data = jsonResponse.getJSONObject("data"); // Accede al objeto `data`
            return data.getString("username"); // Obtén el valor de `username` desde `data`
        } else {
            throw new Exception(jsonResponse.getString("message"));
        }
    }

    public static boolean uploadProfilePictureMultipart(int userId, String fileName, byte[] fileData) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "upload_profile_picture");
        params.put("user_id", String.valueOf(userId));

        String response = HttpHandler.uploadFile(BASE_URL, "image", fileName, fileData, params);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getBoolean("success");
    }
}