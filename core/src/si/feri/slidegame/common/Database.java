package si.feri.slidegame.common;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import si.feri.slidegame.utils.Geolocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

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

    public static LinkedHashMap<String, Event> fetchEvents() throws IOException {
        URL url = new URL("https://imhere-4ade3-default-rtdb.europe-west1.firebasedatabase.app/events.json");
        String jsonString = fetchString(url);

        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, Event>>(){}.getType();
        return gson.fromJson(jsonString, type);
    }


    public static void addEvent(Event event) throws IOException {
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

        HashMap<String, Event> out = new HashMap<>();
        out.put(event.id, event);
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

    public static class Event {
        String date;
        String description;
        String eventCreator;
        String id;
        String invitedList;
        String latitude;
        String longitude;
        String name;
        String time;

        public Event(Geolocation geo) {
            this.date = "29-12-2022";
            this.description = "description";
            this.eventCreator = "eventCreator";
            this.id = UUID.randomUUID().toString();
            this.invitedList = "invitedList";
            this.latitude = geo.lat +"";
            this.longitude = geo.lng +"";
            this.name = "jan";
            this.time = "23:59";
        }

        public Event(String date, String description, String eventCreator, String id, String invitedList, String latitude, String longitude, String name, String time) {
            this.date = date;
            this.description = description;
            this.eventCreator = eventCreator;
            this.id = id;
            this.invitedList = invitedList;
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "date='" + date + '\'' +
                    ", description='" + description + '\'' +
                    ", eventCreator='" + eventCreator + '\'' +
                    ", id='" + id + '\'' +
                    ", invitedList='" + invitedList + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", name='" + name + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }

        public Geolocation getLocation() {
            return new Geolocation(
                    Double.parseDouble(latitude),
                    Double.parseDouble(longitude),
                    name,description,eventCreator,latitude,longitude,date,time
            );
        }
    }

}
