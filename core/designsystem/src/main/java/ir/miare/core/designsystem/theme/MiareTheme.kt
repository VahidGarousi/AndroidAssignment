package ir.miare.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

val MiareDarkColors = miareDarkColors()
val MiareLightColors = miareLightColors()


@Composable
fun MiareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val miareColors = if (darkTheme) {
        MiareDarkColors
    } else {
        MiareLightColors
    }
    MiareTheme(
        miareColors = miareColors,
        content = content
    )
}

@Composable
private fun MiareTheme(
    miareColors: MiareColors,
    content: @Composable () -> Unit
) {
    val rememberedMiareColors = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colors]
        // provided, and overwrite the values set in it.
        miareColors.copy()
    }.apply { updateColorsFrom(miareColors) }

    CompositionLocalProvider(
        LocalMiareColors provides rememberedMiareColors
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object MiareTheme {
    val colors: MiareColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMiareColors.current
}