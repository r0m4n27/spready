package spready.ui

import javafx.embed.swing.JFXPanel
import org.junit.jupiter.api.Nested
import spready.lisp.sexpr.Cell
import spready.ui.sheet.Err
import spready.ui.sheet.EvalStatusEvent
import spready.ui.sheet.Ok
import spready.ui.sheet.SheetModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SheetModelTest {

    // Must initialise a panel
    // Otherwise can't use the bus
    private val panel = JFXPanel()

    private var model = SheetModel()

    @BeforeTest
    fun reset() {
        model = SheetModel()
    }

    @Nested
    inner class Choose {
        @Test
        fun `choose normal`() {
            model.chooseCell(Cell(1, 1))
            model.currentInputProperty.value = "123"
            model.evalInput()

            model.chooseCell(Cell(2, 1))
            assertEquals("", model.currentInputProperty.value)
            model.chooseCell(Cell(1, 1))
            assertEquals("123", model.currentInputProperty.value)
        }

        @Test
        fun `choose not found`() {
            model.chooseCell(Cell(-1, -1))

            assertEquals("", model.currentInputProperty.value)
        }
    }

    @Nested
    inner class Eval {
        @Test
        fun `eval null`() {
            model.evalInput()

            assertEquals(emptySet(), model.changedCellsProperty.value)
        }

        @Test
        fun `eval normal`() {
            model.chooseCell(Cell(1, 1))
            model.currentInputProperty.value = "123"
            model.evalInput()

            assertEquals(setOf(Cell(1, 1)), model.changedCellsProperty.value)

            assertEquals("123", model.allResults[Cell(1, 1)])
        }

        @Test
        fun `eval fire Ok`() {
            var event: EvalStatusEvent? = null

            model.subscribe<EvalStatusEvent> {
                event = it
            }

            model.chooseCell(Cell(1, 1))
            model.currentInputProperty.value = "123"
            model.evalInput()

            // Wait to receive the event
            Thread.sleep(500)
            assertNotNull(event)
            assertEquals(Ok, event?.result)
        }

        @Test
        fun `eval fire Err`() {
            var event: EvalStatusEvent? = null

            model.subscribe<EvalStatusEvent> {
                event = it
            }

            model.chooseCell(Cell(1, 1))
            model.currentInputProperty.value = "test"
            model.evalInput()

            Thread.sleep(500)
            assertNotNull(event)
            assertEquals(Err("Can't find variable test"), event?.result)
        }
    }
}
