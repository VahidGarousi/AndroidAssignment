package ir.miare.core.designsystem.theme.colors

import androidx.compose.ui.graphics.Color
import ir.miare.core.designsystem.theme.Gray100
import ir.miare.core.designsystem.theme.Gray50
import ir.miare.core.designsystem.theme.White0


fun miareLightColors(
    textColor: Color = Color(0xFF121212),
    bg: Bg = Bg(
        card = White0,
        body = Gray100,
        overlay = Gray50
    )
): MiareColors = MiareColors(
    textColor = textColor,
    bg = bg
)

