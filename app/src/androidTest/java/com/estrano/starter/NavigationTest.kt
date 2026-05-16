package com.estrano.starter

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testBottomNavigationSwitchesFragments() {
        // 1. Verify Home is start destination
        onView(withText("Modern Android showcase")).check(matches(isDisplayed()))

        // 2. Click Services tab
        onView(withId(R.id.nav_services)).perform(click())
        onView(withText("Product UI Systems")).check(matches(isDisplayed()))

        // 3. Click Portfolio tab
        onView(withId(R.id.nav_portfolio)).perform(click())
        onView(withText("Aether Commerce")).check(matches(isDisplayed()))

        // 4. Click Profile tab
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withText("Log out")).check(matches(isDisplayed()))
    }
}
