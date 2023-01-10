package si.feri.slidegame.database;

import si.feri.slidegame.utils.Geolocation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EventFirebase {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    //
    //
    //

    public String date;
    public String description;
    public String eventCreator;
    public String id;
    public String invitedList;
    public String latitude;
    public String longitude;
    public String name;
    public String time;

    public EventFirebase(Event event) {
        this.date = event.createdAt.format(DATE_FORMATTER);
        this.time = event.createdAt.format(TIME_FORMATTER);
        this.name = event.name;
        this.description = event.description;
        this.eventCreator = event.eventCreator.toString();
        this.id = event.id.toString();
        this.invitedList = event.invitedList;
        this.latitude = event.location.lat + "";
        this.longitude = event.location.lng + "";
    }

    public EventFirebase(Geolocation geo) {
        this.date = "29-12-2022";
        this.description = "description";
        this.eventCreator = "eventCreator";
        this.id = UUID.randomUUID().toString();
        this.invitedList = "invitedList";
        this.latitude = geo.lat + "";
        this.longitude = geo.lng + "";
        this.name = "jan";
        this.time = "23:59";
    }

    public EventFirebase(String date, String description, String eventCreator, String id, String invitedList, String latitude, String longitude, String name, String time) {
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
        return "Event{" + "date='" + date + '\'' + ", description='" + description + '\'' + ", eventCreator='" + eventCreator + '\'' + ", id='" + id + '\'' + ", invitedList='" + invitedList + '\'' + ", latitude='" + latitude + '\'' + ", longitude='" + longitude + '\'' + ", name='" + name + '\'' + ", time='" + time + '\'' + '}';
    }

    public Event parse() {
        UUID eventCreator;
        try {
            eventCreator = UUID.fromString(this.eventCreator);
        } catch (Exception e) {
            eventCreator = UUID.randomUUID();
        }

        return new Event(UUID.fromString(id), name, description, parseLocation(), eventCreator, LocalDateTime.parse(date + " " + time, DATE_TIME_FORMATTER), invitedList);
    }

    public Geolocation parseLocation() {
        return new Geolocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }
}
