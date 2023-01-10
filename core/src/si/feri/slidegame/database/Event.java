package si.feri.slidegame.database;

import si.feri.slidegame.utils.Geolocation;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {


    public UUID id;

    public String name;
    public String description;

    public Geolocation location;


    public UUID eventCreator;

    public LocalDateTime createdAt;

    // Other
    public String invitedList;


    public Event() {
        this(
                UUID.randomUUID(),
                "",
                "",
                new Geolocation(),
                UUID.randomUUID(),
                LocalDateTime.now(),
                ""
        );
    }

    public Event(UUID id, String name, String description, Geolocation location, UUID eventCreator, LocalDateTime createdAt, String invitedList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.eventCreator = eventCreator;
        this.createdAt = createdAt;
        this.invitedList = invitedList;
    }
}
