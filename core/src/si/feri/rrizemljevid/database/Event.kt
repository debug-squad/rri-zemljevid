package si.feri.rrizemljevid.database

import si.feri.rrizemljevid.utils.Geolocation
import java.time.LocalDateTime
import java.util.*

class Event @JvmOverloads constructor(
    var id: UUID =
        UUID.randomUUID(), var name: String? =
        "", var description: String? =
        "", var location: Geolocation =
        Geolocation(), var eventCreator: UUID =
        UUID.randomUUID(), var createdAt: LocalDateTime =
        LocalDateTime.now(),  // Other
    var invitedList: String? =
        ""
)