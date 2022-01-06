package com.arreis.brightnessswitcher.configuration

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arreis.brightnessswitcher.R
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
        Espresso.onView(ViewMatchers.withId(R.id.config_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(resources.getString(R.string.showTitleInWidget)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(resources.getString(R.string.addLevel)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun screenShowsListOfLevels() {
        Espresso.onView(ViewMatchers.withText("Level 1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Level 2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}