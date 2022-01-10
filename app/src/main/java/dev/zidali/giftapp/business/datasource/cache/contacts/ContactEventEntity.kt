package dev.zidali.giftapp.business.datasource.cache.contacts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import dev.zidali.giftapp.business.domain.models.ContactEvent

@Entity(
    tableName = "contact_events",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["contact_name"],
            childColumns = ["contact_name"],
            onDelete = CASCADE
        )
    ]
)
data class ContactEventEntity (

    @PrimaryKey
    @ColumnInfo(name = "contact_event")
    var contact_event: String,

    @ColumnInfo(name = "contact_name")
    var contact_name: String,

    @ColumnInfo(name = "contact_event_reminder")
    var contact_event_reminder: String,

    @ColumnInfo(name = "year")
    var year: Int,

    @ColumnInfo(name = "month")
    var month: Int,

    @ColumnInfo(name = "day")
    var day: Int,

)

fun ContactEvent.toContactEventEntity(): ContactEventEntity {
    return ContactEventEntity(
        contact_event = contact_event,
        contact_name = contact_name,
        contact_event_reminder = contact_event_reminder,
        year = year,
        month = month,
        day = day,
    )
}

fun ContactEventEntity.toContactEvent(): ContactEvent {
    return ContactEvent(
        contact_name = contact_name,
        contact_event = contact_event,
        contact_event_reminder = contact_event_reminder,
        year = year,
        month = month,
        day = day,
    )
}