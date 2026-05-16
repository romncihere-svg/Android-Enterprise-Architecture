package com.estrano.core.security

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.view.WindowManager
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.security.MessageDigest
import java.util.concurrent.Executors

object SecurityManager {

    private const val TAG = "AetherSecurityShield"
    
    // Realistic development fingerprint (example) to replace placeholders
    private val SIGN_P1 = "5E:8F:16:06:2E:A3:CD:2B:4A:0D:54:78:76:BA:A6:F3"
    private val SIGN_P2 = ":8C:AB:F3:E5:05:71:08:C5:8F:EE:EA:7B:5B:DA:21"
    private val TRUSTED_SIGNATURE: String get() = SIGN_P1 + SIGN_P2

    private val ROOT_BINARIES = arrayOf(
        "/system/xbin/su", "/system/bin/su", "/sbin/su",
        "/data/local/xbin/su", "/data/local/bin/su",
        "/system/sd/xbin/su", "/system/bin/failsafe/su",
        "/data/local/su", "/su/bin/su", "/su/xbin/su",
        "/system/app/Superuser.apk", "/system/app/SuperSU.apk",
        "/system/app/superuser.apk", "/data/app/eu.chainfire.supersu.apk",
        "/system/framework/am.jar", "/system/bin/magisk", "/sbin/magisk"
    )

    private val FRIDA_LIBRARIES = arrayOf(
        "frida", "frida-agent", "libfrida", "gum-js-loop", "gmain",
        "linjector", "jniload"
    )

    interface SecurityCallback {
        fun onIntegrityVerified()
        fun onThreatDetected(threat: String, level: ThreatLevel)
    }

    enum class ThreatLevel {
        LOW, MEDIUM, CRITICAL
    }

    fun runFullIntegrityCheck(context: Context, callback: SecurityCallback) {
        Executors.newSingleThreadExecutor().execute {
            Timber.tag(TAG).d(">>> AETHER SECURITY SHIELD ACTIVATED <<<")

            // Layer 1: Signature
            if (!verifyAppSignature(context)) {
                Timber.tag(TAG).e("[CRITICAL] APK Signature Mismatch!")
                callback.onThreatDetected("APK_TAMPERED", ThreatLevel.CRITICAL)
                return@execute
            }

            // Layer 2: Root
            if (isRooted()) {
                Timber.tag(TAG).w("[MEDIUM] Rooted device detected.")
                callback.onThreatDetected("ROOTED_DEVICE", ThreatLevel.MEDIUM)
            }

            // Layer 3: Debugger
            if (isDebuggerAttached()) {
                Timber.tag(TAG).w("[MEDIUM] Debugger attached!")
                callback.onThreatDetected("DEBUGGER_ATTACHED", ThreatLevel.MEDIUM)
            }

            // Layer 4: Emulator
            if (isRunningOnEmulator()) {
                Timber.tag(TAG).i("[LOW] Emulator detected.")
                callback.onThreatDetected("EMULATOR", ThreatLevel.LOW)
            }

            // Layer 5: Hook Frameworks
            if (isHookFrameworkPresent()) {
                Timber.tag(TAG).e("[CRITICAL] Hook framework detected!")
                callback.onThreatDetected("HOOK_FRAMEWORK", ThreatLevel.CRITICAL)
                return@execute
            }

            Timber.tag(TAG).d(">>> INTEGRITY CONFIRMED <<<")
            callback.onIntegrityVerified()
        }
    }

    private fun verifyAppSignature(context: Context): Boolean {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo.signingCertificateHistory
            } else {
                packageInfo.signatures
            }

            signatures.any { signature ->
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signature.toByteArray())
                bytesToHex(md.digest()).lowercase() == TRUSTED_SIGNATURE.lowercase()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Signature verification failed")
            false
        }
    }

    private fun isRooted(): Boolean {
        return ROOT_BINARIES.any { File(it).exists() } ||
               (Build.TAGS?.contains("test-keys") == true) ||
               canExecuteSu()
    }

    private fun canExecuteSu(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            BufferedReader(InputStreamReader(process.inputStream)).use { it.readLine() != null }
        } catch (e: Exception) {
            false
        }
    }

    private fun isDebuggerAttached() = Debug.isDebuggerConnected() || Debug.waitingForDebugger()

    private fun isRunningOnEmulator(): Boolean {
        var score = 0
        if (Build.FINGERPRINT.startsWith("generic")) score++
        if (Build.FINGERPRINT.contains("unknown")) score++
        if (Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator")) score++
        if (Build.MANUFACTURER.contains("Genymotion")) score++
        if (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) score++
        if (Build.PRODUCT.contains("emulator") || Build.PRODUCT.contains("simulator")) score++
        if (Build.HARDWARE.contains("goldfish") || Build.HARDWARE.contains("ranchu")) score++
        return score >= 3
    }

    private fun isHookFrameworkPresent(): Boolean {
        // Proc maps check for Frida
        try {
            val mapsFile = "/proc/self/maps"
            BufferedReader(InputStreamReader(FileInputStream(mapsFile))).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line?.lowercase() ?: continue
                    if (FRIDA_LIBRARIES.any { currentLine.contains(it) }) return true
                }
            }
        } catch (e: Exception) {}

        // Xposed check
        return try {
            Class.forName("de.robv.android.xposed.XC_MethodHook")
            true
        } catch (e: Exception) {
            false
        }
    }

    fun applyScreenProtection(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
