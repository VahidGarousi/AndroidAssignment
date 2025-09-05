package ir.miare.androidcodechallenge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MiareApp(
    modifier: Modifier = Modifier,
    appState: MiareAppState = rememberMiareAppState(),
) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            MiareNavHost(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                MiareBottomBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    currentDestination = appState.currentDestination,
                    destinations = appState.topLevelDestinations,
                    onNavigationSelected = {
                        appState.navigateToTopLevelDestination(it)
                    },
                )
            }
        }
    }
}