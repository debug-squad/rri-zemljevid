package si.feri.slidegame.database;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Database {
    public static String fetchString(URL url) throws IOException {
        return fetchString(url.openStream());
    }

    public static String fetchString(InputStream is) throws IOException {
        ByteArrayOutputStream bis = new ByteArrayOutputStream();
        byte[] bytebuff = new byte[4096];
        int n;

        while ((n = is.read(bytebuff)) > 0) {
            bis.write(bytebuff, 0, n);
        }
        return bis.toString();
    }

    public static LinkedHashMap<String, EventFirebase> fetchEvents() throws IOException {
        URL url = new URL("https://imhere-4ade3-default-rtdb.europe-west1.firebasedatabase.app/events.json");
        String jsonString = fetchString(url);

        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, EventFirebase>>(){}.getType();
        return gson.fromJson(jsonString, type);
    }


    public static void addEvent(EventFirebase eventFirebase) throws IOException {
        URL url = new URL("https://imhere-4ade3-default-rtdb.europe-west1.firebasedatabase.app/events.json");
        Gson gson = new Gson();

        //
        //
        //

        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        HashMap<String, EventFirebase> out = new HashMap<>();
        out.put(eventFirebase.id, eventFirebase);
        String payload = gson.toJson(out);

        http.setFixedLengthStreamingMode(payload.length());
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(payload.getBytes());
        }

        //
        //
        //

        fetchString(url);
    }
}
