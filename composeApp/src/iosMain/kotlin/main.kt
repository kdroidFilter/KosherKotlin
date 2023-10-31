import androidx.compose.ui.window.ComposeUIViewController
import sternbach.software.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
