package app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import app.domain.LuachSection

/**
 * The Navigation 3 back stack: a plain observable list of [LuachSection], owned by the caller.
 *
 * Saved as names rather than through `rememberNavBackStack`, which would drag in the
 * serialization plugin and a polymorphic `SavedStateConfiguration` to persist an enum.
 */
@Composable
fun rememberLuachBackStack(): SnapshotStateList<LuachSection> = rememberSaveable(
    saver = listSaver(
        save = { it.map(LuachSection::name) },
        restore = { it.map(LuachSection::valueOf).toMutableStateList() },
    ),
) { mutableStateListOf(LuachSection.NOW) }

/**
 * Rail navigation: [LuachSection.NOW] is the root and every other section sits one level above
 * it, so system back — and the browser's back button on the web — always lands on the hero.
 *
 * One snapshot for the whole swap: the browser binding pushes a history entry per emission, and
 * a bare `clear()` then `addAll()` would emit the intermediate stack and leave a phantom entry.
 */
fun SnapshotStateList<LuachSection>.goTo(section: LuachSection) {
    val target =
        if (section == LuachSection.NOW) listOf(LuachSection.NOW)
        else listOf(LuachSection.NOW, section)
    if (toList() == target) return
    Snapshot.withMutableSnapshot {
        clear()
        addAll(target)
    }
}

/** URL fragment name for [section] — `#day`, `#month`, … Also parsed back by the web build. */
val LuachSection.slug: String get() = name.lowercase()

/** Inverse of [slug]; `null` when the fragment names nothing we know. */
fun sectionOfSlug(slug: String?): LuachSection? =
    LuachSection.entries.firstOrNull { it.slug == slug }
