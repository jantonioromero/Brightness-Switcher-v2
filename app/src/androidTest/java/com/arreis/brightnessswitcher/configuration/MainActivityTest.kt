package com.arreis.brightnessswitcher.configuration

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.arreis.brightnessswitcher.R
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private var resources: Resources = ApplicationProvider.getApplicationContext<Context>().resources

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
        Espresso.onView(ViewMatchers.withText("Level 3"))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun createNewLevel() {
        Espresso.onView(ViewMatchers.withText(resources.getString(R.string.addLevel)))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(resources.getString(R.string.ok)))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText("Level 1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Level 2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Level 3"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    }
}