package si.feri.rrizemljevid.database

import si.feri.rrizemljevid.utils.Geolocation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EventFirebase {
    //
    //
    //
    var date: String
    var description: String?
    var eventCreator: String
    var id: String
    var invitedList: String?
    var latitude: String
    var longitude: String
    var name: String?
    var time: String

    constructor(event: Event?) {
        date = event!!.createdAt.format(DATE_FORMATTER)
        time = event.createdAt.format(TIME_FORMATTER)
        name = event.name
        description = event.description
        eventCreator = event.eventCreator.toString()
        id = event.id.toString()
        invitedList = event.invitedList
        latitude = event.location.lat.toString() + ""
        longitude = event.location.lng.toString() + ""
    }

    constructor(geo: Geolocation) {
        date = "29-12-2022"
        description = "description"
        eventCreator = "eventCreator"
        id = UUID.randomUUID().toString()
        invitedList = "invitedList"
        latitude = geo.lat.toString() + ""
        longitude = geo.lng.toString() + ""
        name = "jan"
        time = "23:59"
    }

    constructor(
        date: String,
        description: String?,
        eventCreator: String,
        id: String,
        invitedList: String?,
        latitude: String,
        longitude: String,
        name: String?,
        time: String
    ) {
        this.date = date
        this.description = description
        this.eventCreator = eventCreator
        this.id = id
        this.invitedList = invitedList
        this.latitude = latitude
        this.longitude = longitude
        this.name = name
        this.time = time
    }

    override fun toString(): String {
        return "Event{date='$date', description='$description', eventCreator='$eventCreator', id='$id', invitedList='$invitedList', latitude='$latitude', longitude='$longitude', name='$name', time='$time'}"
    }

    fun parse(): Event {
        val eventCreator: UUID
        eventCreator = try {
            UUID.fromString(this.eventCreator)
        } catch (e: Exception) {
            UUID.randomUUID()
        }
        return Event(
            UUID.fromString(id), name, description, parseLocation(), eventCreator, LocalDateTime.parse(
                "$date $time", DATE_TIME_FORMATTER
            ), invitedList
        )
    }

    fun parseLocation(): Geolocation {
        return Geolocation(latitude.toDouble(), longitude.toDouble())
    }

    companion object {
        val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    }
}