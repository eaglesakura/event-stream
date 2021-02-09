package com.eaglesakura.firearm.event

import androidx.annotation.CheckResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.eaglesakura.armyknife.android.reactivex.toChannel
import com.eaglesakura.armyknife.android.reactivex.toLiveData
import com.eaglesakura.armyknife.android.reactivex.with
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel

/**
 * Support RxJava functions.
 *
 * EventStream use to event or snackbar-data or such one-shot data.
 * Port from: https://github.com/eaglesakura/armyknife-reactivex
 */
class EventStream<T : Event> private constructor(
    private val subject: Subject<T>,
    @Suppress("MemberVisibilityCanBePrivate") val observable: Observable<T>,
    private val validator: ((T) -> Boolean)
) {
    constructor(subject: Subject<T>, validator: (T) -> Boolean) : this(
        subject,
        subject.observeOn(AndroidSchedulers.mainThread()),
        validator
    )

    @Suppress("unused")
    constructor(validator: (T) -> Boolean) : this(PublishSubject.create(), validator)

    /**
     * Post new value.
     * Can run on any-thread.
     */
    fun next(value: T) {
        if (!validator(value)) {
            throw IllegalArgumentException("Value is invalid[$value]")
        }
        subject.onNext(value)
    }

    /**
     * Make LiveData from Observable in RxJava.
     *
     * LiveData calls "dispose()" method at Inactive event.
     * You should not call Disposable.dispose() method.
     */
    @Suppress("unused")
    fun toLiveData(): LiveData<T> {
        return observable.toLiveData()
    }

    /**
     * Make Channel from Observable in RxJava.
     * CAUTION!! Make a promise, You will call "Channel.close()" or "Channel.cancel()" method.
     *
     * Channel calls "dispose()" method at Channel.close() or Channel.cancel().
     * You should not call Disposable.dispose() method.
     *
     * e.g.)
     * stream.toChannel().consume {
     *      // something...
     * }    // Channel.close() on exit.
     */
    @Suppress("unused")
    @CheckResult
    fun toChannel(dispatcher: CoroutineDispatcher = Dispatchers.Main): Channel<T> {
        return observable.toChannel(dispatcher)
    }

    /**
     * Subscribe by reactivex.Observer
     */
    @Suppress("unused")
    fun subscribe(observer: io.reactivex.Observer<T>) {
        return observable.subscribe(observer)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun subscribe(observer: (value: T) -> Unit): Disposable {
        return observable.subscribe {
            observer(it)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun subscribe(lifecycle: Lifecycle, observer: (value: T) -> Unit) {
        subscribe(observer).with(lifecycle)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun subscribe(observer: Observer<T>): Disposable {
        return observable.subscribe {
            observer.onChanged(it)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun subscribe(lifecycle: Lifecycle, observer: Observer<T>) {
        subscribe(observer).with(lifecycle)
    }

    @Suppress("unused")
    fun subscribe(owner: LifecycleOwner, observer: Observer<T>) {
        subscribe(owner.lifecycle, observer)
    }

    @Suppress("unused")
    fun subscribe(owner: LifecycleOwner, observer: (value: T) -> Unit) {
        subscribe(owner.lifecycle, Observer { observer(it) })
    }
}
