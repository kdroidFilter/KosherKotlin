import androidx.compose.ui.window.ComposeUIViewController
import sternbach.software.kosherkotlin.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
