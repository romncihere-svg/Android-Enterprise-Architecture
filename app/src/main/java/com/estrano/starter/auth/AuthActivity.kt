package com.estrano.starter.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.estrano.core.session.SessionManager
import com.estrano.starter.MainActivity
import com.estrano.starter.R
import com.estrano.starter.view.compose.AuthScreen
import com.estrano.starter.view.compose.EstranoTheme
import com.estrano.starter.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    @Inject lateinit var sessionManager: SessionManager
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    state?.let {
                        if (it.isSuccess) {
                            launchMain()
                        }
                    }
                }
            }
        }

        setContent {
            EstranoTheme {
                AuthScreen(
                    onLoginClick = { input ->
                        viewModel.loginWithKey(input)
                    },
                    onDiscordClick = {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://discord.gg/estrano"))
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun launchMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
