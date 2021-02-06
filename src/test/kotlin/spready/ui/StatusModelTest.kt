package spready.ui

import javafx.embed.swing.JFXPanel
import spready.ui.sheet.Err
import spready.ui.sheet.EvalStatusEvent
import spready.ui.sheet.Ok
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
        model.fire(EvalStatusEvent(Err("test")))
        Thread.sleep(500)

        assertEquals("test", model.lastErrorProperty.value)
    }

    @Test
    fun `receive Ok`() {
        model.fire(EvalStatusEvent(Ok))
        Thread.sleep(500)

        assertEquals("", model.lastErrorProperty.value)
    }

    @Test
    fun `receive Ok after Err`() {
        model.fire(EvalStatusEvent(Err("test")))
        Thread.sleep(500)

        assertEquals("test", model.lastErrorProperty.value)

        model.fire(EvalStatusEvent(Ok))
        Thread.sleep(500)

        assertEquals("", model.lastErrorProperty.value)
    }
}
