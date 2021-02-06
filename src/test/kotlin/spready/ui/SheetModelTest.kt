package spready.ui

import org.junit.jupiter.api.Nested
import spready.lisp.sexpr.Cell
import spready.ui.sheet.SheetModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SheetModelTest {
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
    }
}
