package com.estrano.starter

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.estrano.starter.auth.AuthActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AuthActivity::class.java)

    @Test
    fun testLoginScreenLoadsCorrectly() {
        // Verify branding and buttons are visible
        onView(withText("AETHER")).check(matches(isDisplayed()))
        onView(withText("Sign in with Google")).check(matches(isDisplayed()))
        onView(withText("Continue as Guest")).check(matches(isDisplayed()))
    }
}
