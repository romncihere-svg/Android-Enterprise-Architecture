package com.estrano.starter.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.estrano.core.security.SecurityManager
import com.estrano.core.session.SessionManager
import com.estrano.starter.MainActivity
import com.estrano.starter.R
import com.estrano.starter.view.compose.EstranoTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        SecurityManager.applyScreenProtection(this)

        setContent {
            EstranoTheme {
                SplashScreen()
            }
        }

        lifecycleScope.launch {
            delay(1800)
            runIntegrityCheck()
        }
    }

    private fun runIntegrityCheck() {
        SecurityManager.runFullIntegrityCheck(this, object : SecurityManager.SecurityCallback {
            override fun onIntegrityVerified() {
                proceedToApp()
            }

            override fun onThreatDetected(threat: String, level: SecurityManager.ThreatLevel) {
                if (level == SecurityManager.ThreatLevel.CRITICAL) {
                    runOnUiThread {
                        Toast.makeText(this@SplashActivity, "INTEGRITY ERROR: $threat", Toast.LENGTH_LONG).show()
                        finishAffinity()
                    }
                } else {
                    Timber.w("Non-critical threat detected: $threat")
                    proceedToApp()
                }
            }
        })
    }

    private fun proceedToApp() {
        val intent = if (sessionManager.isSignedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthActivity::class.java)
        }
        startActivity(intent)
        finish()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}

@Composable
private fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ESTRANO",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF03DAC5),
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            )
            Text(
                text = "STORES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray,
                letterSpacing = 6.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = Color(0xFF03DAC5),
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
