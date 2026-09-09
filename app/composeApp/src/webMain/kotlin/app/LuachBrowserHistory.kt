package app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.domain.LuachSection
import app.ui.sectionOfSlug
import app.ui.slug
import com.github.terrakok.navigation3.browser.ChronologicalBrowserNavigation
import com.github.terrakok.navigation3.browser.buildBrowserHistoryFragment
import com.github.terrakok.navigation3.browser.getBrowserHistoryFragmentName

/**
 * Mirrors the back stack into the browser's URL and history.
 *
 * Chronological, not hierarchical: the sections are peers reached from a rail, so `#month`
 * should be a bookmarkable address and Back should return to the section you came from —
 * which is what the address bar buys, and what hierarchical navigation gives up.
 */
@Composable
fun LuachBrowserHistory(backStack: SnapshotStateList<LuachSection>) {
    ChronologicalBrowserNavigation(
        backStack = backStack,
        saveKey = { buildBrowserHistoryFragment(it.slug) },
        restoreKey = { fragment -> sectionOfSlug(getBrowserHistoryFragmentName(fragment)) },
    )
}
