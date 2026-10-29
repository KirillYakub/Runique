package com.kiras.auth.presentation.intro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.auth.presentation.R
import com.kiras.auth.presentation.intro.components.SplitlineLogoVertical
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineActionButton
import com.kiras.core.presentation.designsystem.components.SplitlineOutlinedActionButton
import com.kiras.core.presentation.designsystem.components.SplitlineScaffold

@Composable
fun IntroScreenRoot(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    IntroScreen(
        onAction = { action ->
            when(action) {
                IntroAction.OnSignInClick -> onSignInClick()
                IntroAction.OnSignUpClick -> onSignUpClick()
            }
        }
    )
}

@Composable
private fun IntroScreen(onAction: (IntroAction) -> Unit) {
    SplitlineScaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                SplitlineLogoVertical()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 48.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome_to_splitline),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.splitline_description),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(32.dp))
                SplitlineOutlinedActionButton(
                    text = stringResource(R.string.sign_in),
                    isLoading = false,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onAction(IntroAction.OnSignInClick) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                SplitlineActionButton(
                    text = stringResource(R.string.sign_up),
                    isLoading = false,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onAction(IntroAction.OnSignUpClick) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun IntroScreenPreview() {
    SplitlineTheme {
        IntroScreen {  }
    }
}
