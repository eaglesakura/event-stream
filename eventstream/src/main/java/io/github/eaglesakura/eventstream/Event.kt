package io.github.eaglesakura.eventstream

import android.os.Parcelable

/**
 * Any event(Error, Data Found, or such event) object.
 */
interface Event {
    val id: Any
}

/**
 *  can parcel event.
 */
interface ParcelableEvent : Event, Parcelable

/**
 * Make an Event instance.
 */
fun Event(name: String): Event = ParcelableEventImpl(name)

/**
 * Make an ParcelableEvent instance.
 */
fun ParcelableEvent(name: String): ParcelableEvent = ParcelableEventImpl(name)
