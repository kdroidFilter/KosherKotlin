package app.ui

import androidx.compose.runtime.mutableStateListOf
import app.domain.LuachSection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NavigationTest {

    @Test
    fun `every section survives the url round trip`() {
        LuachSection.entries.forEach { assertEquals(it, sectionOfSlug(it.slug)) }
    }

    @Test
    fun `unknown fragments restore to nothing`() {
        assertNull(sectionOfSlug(null))
        assertNull(sectionOfSlug("nope"))
    }

    @Test
    fun `sections sit one level above the hero`() {
        val backStack = mutableStateListOf(LuachSection.NOW)

        backStack.goTo(LuachSection.MONTH)
        assertEquals(listOf(LuachSection.NOW, LuachSection.MONTH), backStack.toList())

        // Peers replace each other instead of stacking, so back is always one step.
        backStack.goTo(LuachSection.SHABBAT)
        assertEquals(listOf(LuachSection.NOW, LuachSection.SHABBAT), backStack.toList())

        backStack.goTo(LuachSection.NOW)
        assertEquals(listOf(LuachSection.NOW), backStack.toList())
    }
}
