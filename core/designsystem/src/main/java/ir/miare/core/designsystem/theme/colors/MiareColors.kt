package ir.miare.core.designsystem.theme.colors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class MiareColors(
    textColor : Color,
    bg: Bg
) {
   var textColor by mutableStateOf(textColor)
        internal set

    var bg by mutableStateOf(bg)
        internal set


    fun copy(
        textColor: Color = this.textColor,
        bg: Bg = this.bg,
    ): MiareColors = MiareColors(
        textColor = textColor,
        bg = bg
    )
}


/**
 * Updates the internal values of the given [MiareColors] with values from the [other] [MiareColors]. This
 * allows efficiently updating a subset of [MiareColors], without recomposing every composable that
 * consumes values from [LocalMiareColors].
 *
 * Because [MiareColors] is very wide-reaching, and used by many expensive composables in the
 * hierarchy, providing a new value to [LocalMiareColors] causes every composable consuming
 * [LocalMiareColors] to recompose, which is prohibitively expensive in cases such as animating one
 * color in the theme. Instead, [MiareColors] is internally backed by [mutableStateOf], and this
 * function mutates the internal state of [this] to match values in [other]. This means that any
 * changes will mutate the internal state of [this], and only cause composables that are reading
 * the specific changed value to recompose.
 */
fun MiareColors.updateColorsFrom(other: MiareColors) {
    textColor = other.textColor
    bg = other.bg
}

val LocalMiareColors = staticCompositionLocalOf { miareDarkColors() }