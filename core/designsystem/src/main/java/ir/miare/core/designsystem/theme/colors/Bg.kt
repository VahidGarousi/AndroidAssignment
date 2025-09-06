package ir.miare.core.designsystem.theme.colors

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Stable
class Bg(
    card: Color,
    body: Color,
    overlay: Color,
) {
    var card by mutableStateOf(card)
        internal set

    var body by mutableStateOf(body)
        internal set
    var overlay by mutableStateOf(overlay)
        internal set

    fun copy(
        card: Color = this.card,
        body: Color = this.body,
        overlay: Color = this.overlay,
    ) = Bg(
        card = card,
        body = body,
        overlay = overlay,
    )
}