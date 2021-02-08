package spready.ui

import javafx.embed.swing.JFXPanel
import spready.ui.status.Err
import spready.ui.status.Ok
import spready.ui.status.StatusEvent
import spready.ui.status.StatusModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusModelTest {
    // Must initialise a panel
    // Otherwise can't use the bus
    private val panel = JFXPanel()

    private var model = StatusModel()

    @BeforeTest
    fun reset() {
        model = StatusModel()
    }

    @Test
    fun `receive Err`() {
        model.fire(StatusEvent(Err("test")))
        Thread.sleep(50)

        assertEquals("test", model.lastErrorProperty.value)
    }

    @Test
    fun `receive Ok`() {
        model.fire(StatusEvent(Ok))
        Thread.sleep(50)

        assertEquals("", model.lastErrorProperty.value)
    }

    @Test
    fun `receive Ok after Err`() {
        model.fire(StatusEvent(Err("test")))
        Thread.sleep(50)

        assertEquals("test", model.lastErrorProperty.value)

        model.fire(StatusEvent(Ok))
        Thread.sleep(50)

        assertEquals("", model.lastErrorProperty.value)
    }
}
