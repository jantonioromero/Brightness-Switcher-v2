package com.arreis.brightnessswitcher

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private var resources: Resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Before
    fun setUp() {
    }

    @Test
    fun screenShowsListAndButtons() {
        onView(withId(R.id.config_list)).check(matches(isDisplayed()))
        onView(withText(resources.getString(R.string.showTitleInWidget))).check(matches(isDisplayed()))
        onView(withText(resources.getString(R.string.addLevel))).check(matches(isDisplayed()))
    }

}