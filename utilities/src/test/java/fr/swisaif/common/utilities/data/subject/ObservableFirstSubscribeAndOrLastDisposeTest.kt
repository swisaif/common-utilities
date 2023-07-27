package fr.swisaif.common.utilities.data.subject

import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservableFirstSubscribeAndOrLastDisposeTest {

    @Test
    fun testDoOnFirstSubscribe() {
        var i = 0
        val subject = PublishSubject.create<Any>()
        val observable = subject.doOnFirstSubscribe { i++ }

        assertEquals(0, i)
        val subscribe1 = observable.subscribe()

        assertEquals(1, i)

        val subscribe2 = observable.subscribe()
        assertEquals(1, i)

        subscribe1.dispose()
        subscribe2.dispose()
        assertEquals(1, i)
    }

    @Test
    fun testDoOnLastDispose() {
        var i = 0
        val subject = PublishSubject.create<Any>()
        val observable = subject.doOnLastDispose { i++ }

        assertEquals(0, i)
        val subscribe1 = observable.subscribe()

        assertEquals(0, i)
        val subscribe2 = observable.subscribe()

        assertEquals(0, i)

        subscribe1.dispose()
        assertEquals(0, i)

        subscribe2.dispose()
        assertEquals(1, i)
    }

    @Test
    fun testDoOnFirstSubscribeAndLastDispose() {
        var i = 0
        val subject = PublishSubject.create<Any>()
        val observable = subject.doOnFirstSubscribeAndLastDispose(
            onFirstSubscribe = { i++ },
            onLastDispose = { i-- }
        )

        assertEquals(0, i)
        val subscribe1 = observable.subscribe()

        assertEquals(1, i)
        val subscribe2 = observable.subscribe()

        assertEquals(1, i)

        subscribe1.dispose()
        assertEquals(1, i)

        subscribe2.dispose()
        assertEquals(0, i)
    }
}