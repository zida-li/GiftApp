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
            parentColumns = ["primary_key"],
            childColumns = ["primary_key"],
            onDelete = CASCADE
        )
    ]
)
data class ContactEventEntity (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_pk")
    var event_pk: Int,

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

    @ColumnInfo(name = "primary_key")
    var contact_pk: Int,

    @ColumnInfo(name = "ymd_format")
    var ymd_format: String,

    @ColumnInfo(name = "expired")
    var expired: Boolean,

    @ColumnInfo(name = "event_owner")
    var event_owner: String,

    ) {

    constructor(): this(
        0,
        "",
        "",
        "",
        0,
        0,
        0,
        0,
        "",
        false,
        "",
    )

}

fun ContactEvent.toContactEventEntity(): ContactEventEntity {
    return ContactEventEntity(
        contact_event = contact_event,
        contact_name = contact_name,
        contact_event_reminder = contact_event_reminder,
        year = year,
        month = month,
        day = day,
        contact_pk = contact_pk,
        ymd_format = ymd_format,
        expired = expired,
        event_pk = event_pk,
        event_owner = event_owner,
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
        contact_pk = contact_pk,
        ymd_format = ymd_format,
        expired = expired,
        event_pk = event_pk,
        event_owner = event_owner,
    )
}