package spready.ui

import javafx.embed.swing.JFXPanel
import org.junit.jupiter.api.TestInstance
import spready.ui.script.ScriptModel
import spready.ui.status.Err
import spready.ui.status.Ok
import spready.ui.status.StatusEvent
import tornadofx.ViewModel
import tornadofx.getValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ScriptModelTest : ViewModel() {
    private val panel = JFXPanel()

    private val spreadModel: SpreadModel by inject()
    private val spread by spreadModel.spreadProperty

    @Test
    fun `normal current input`() {
        spread.setScript("script1", "123")

        val model = ScriptModel("script1")

        assertEquals(model.currentInputProperty.value, "123")
    }

    @Test
    fun `fail to initialize`() {
        assertFailsWith<IllegalArgumentException> {
            ScriptModel("script2")
        }
    }

    @Test
    fun `run script normal`() {
        var event: StatusEvent? = null

        spread.setScript("script3", "")

        val model = ScriptModel("script3")

        model.subscribe<StatusEvent> {
            event = it
        }

        model.currentInputProperty.value = "(val x 123)"
        model.runScript()

        Thread.sleep(50)
        assertEquals("(val x 123)", spread.allScripts["script3"])
        assertNotNull(event)
        assertEquals(Ok, event?.result)
    }

    @Test
    fun `run script fail`() {
        var event: StatusEvent? = null

        spread.setScript("script4", "")

        val model = ScriptModel("script4")

        model.subscribe<StatusEvent> {
            event = it
        }

        model.currentInputProperty.value = "test"
        model.runScript()

        Thread.sleep(50)
        assertNotNull(event)
        assertEquals(Err("Can't find variable test"), event?.result)
    }
}
