package ir.miare.core.designsystem.theme.colors

import androidx.compose.ui.graphics.Color
import ir.miare.core.designsystem.theme.Gray600
import ir.miare.core.designsystem.theme.Gray650
import ir.miare.core.designsystem.theme.Gray700

fun miareDarkColors(
    textColor: Color = Color(0xFFFFFFFF),
    bg: Bg = Bg(
        body = Gray700,
        card = Gray600,
        overlay = Gray650
    )
): MiareColors = MiareColors(
    textColor = textColor,
    bg = bg
)

