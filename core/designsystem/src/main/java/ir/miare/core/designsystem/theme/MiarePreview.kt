package ir.miare.core.designsystem.theme

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun MiarePreview(
    direction: LayoutDirection = LayoutDirection.Ltr,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides direction
    ) {
        Surface(
            color = MiareTheme.colors.bg.body
        ) {
            MiareTheme(
                content = content
            )
        }
    }
}