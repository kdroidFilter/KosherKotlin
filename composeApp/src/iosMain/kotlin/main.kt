import androidx.compose.ui.window.ComposeUIViewController
import sternbach.software.kosherkotlin.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
