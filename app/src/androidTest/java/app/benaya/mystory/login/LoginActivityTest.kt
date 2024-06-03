@file:Suppress("DEPRECATION")
package app.benaya.mystory.login

import androidx.test.runner.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import app.benaya.mystory.settings.EspressoIdlingResource
import app.benaya.mystory.ActivityFragment
import app.benaya.mystory.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    private lateinit var activityScenario: ActivityScenario<LoginActivity>

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
        activityScenario = activityRule.scenario
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loginandologout() {
        onView(withId(R.id.et_Email)).perform(typeText("bena@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_Pass)).perform(typeText("mbakku88"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        ActivityScenario.launch(ActivityFragment::class.java)

        onView(withId(R.id.profile)).perform(click())

        onView(withId(R.id.tv_logout)).perform(click())

        onView(withText(R.string.CONFIRMATION_LOGOUT_TITLE)).check(matches(isDisplayed()))
        onView(withText(R.string.CONFIRMATION_LOGOUT_MESSAGE)).check(matches(isDisplayed()))
        onView(withText(R.string.CONFIRMATION_LOGOUT_YES)).check(matches(isDisplayed()))
        onView(withText(R.string.CONFIRMATION_LOGOUT_CANCEL)).check(matches(isDisplayed()))
        onView(withText(R.string.CONFIRMATION_LOGOUT_YES)).perform(click())
    }
}
