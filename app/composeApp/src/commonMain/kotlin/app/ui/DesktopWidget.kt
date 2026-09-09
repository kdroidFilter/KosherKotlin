package app.ui

/**
 * True on the JVM desktop target, where a Nucleus overlay window can host the hero as a
 * below-stacking widget. Hidden from settings everywhere else.
 */
internal expect val DesktopWidgetSupported: Boolean
