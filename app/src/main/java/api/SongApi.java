package api;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import database.Song;
import utils.HttpHandler;

public class SongApi {
    private static final String BASE_URL = "http://ec2-51-44-167-78.eu-west-3.compute.amazonaws.com/maranburu011/WEB/api.php";

    public static boolean addSong(Song song) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "add_song");
        jsonBody.put("titulo", song.getTitulo());
        jsonBody.put("artista", song.getArtista());
        jsonBody.put("album", song.getAlbum());
        jsonBody.put("fecha", song.getFecha());
        jsonBody.put("duracion", song.getDuracion());
        jsonBody.put("genero", song.getGenero());
        jsonBody.put("userId", song.getUserId());

        String response = HttpHandler.makeRequest(BASE_URL, "POST", jsonBody.toString());
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getBoolean("success");
    }

    public static List<Song> getSongs(int userId) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "get_songs");
        jsonBody.put("user_id", userId);

        String response = HttpHandler.makeRequest(BASE_URL, "POST", jsonBody.toString());
        JSONObject jsonResponse = new JSONObject(response);
        List<Song> songs = new ArrayList<>();
        if (jsonResponse.getBoolean("success")) {
            JSONArray jsonArray = jsonResponse.getJSONArray("data"); // Access the `data` array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Song song = new Song();
                song.setId(jsonObject.getInt("id"));
                song.setTitulo(jsonObject.getString("titulo"));
                song.setArtista(jsonObject.getString("artista"));
                song.setAlbum(jsonObject.optString("album", null));
                song.setFecha(jsonObject.optString("fecha", null));
                song.setDuracion(jsonObject.optString("duracion", null));
                song.setGenero(jsonObject.optString("genero", null));
                song.setUserId(jsonObject.getInt("userId"));
                songs.add(song);
            }
        } else {
            throw new Exception(jsonResponse.getString("message"));
        }
        return songs;
    }

    public static boolean deleteSong(int songId) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "delete_song");
        jsonBody.put("song_id", songId);

        String response = HttpHandler.makeRequest(BASE_URL, "POST", jsonBody.toString());
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getBoolean("success");
    }

    public static boolean deleteAllSongs(int userId) throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("action", "delete_all_songs");
        jsonBody.put("user_id", userId);

        String response = HttpHandler.makeRequest(BASE_URL, "POST", jsonBody.toString());
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getBoolean("success");
    }
}