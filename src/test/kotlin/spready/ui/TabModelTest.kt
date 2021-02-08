package spready.ui

import org.junit.jupiter.api.Test
import spready.ui.status.Err
import spready.ui.status.Ok
import spready.ui.tabs.TabModel
import kotlin.test.assertEquals

class TabModelTest {

    private val model = TabModel()

    @Test
    fun `initialize normal`() {
        assertEquals(Ok, model.initializeScript("tab1"))
    }

    @Test
    fun `initialize fail`() {
        model.initializeScript("tab2")
        assertEquals(Err("Script already exists!"), model.initializeScript("tab2"))
    }
}
