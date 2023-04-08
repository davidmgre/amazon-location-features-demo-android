package com.aws.amazonlocation.ui.main

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.aws.amazonlocation.ACCESS_COARSE_LOCATION
import com.aws.amazonlocation.ACCESS_FINE_LOCATION
import com.aws.amazonlocation.AMAZON_MAP_READY
import com.aws.amazonlocation.DELAY_1000
import com.aws.amazonlocation.DELAY_10000
import com.aws.amazonlocation.DELAY_15000
import com.aws.amazonlocation.DELAY_2000
import com.aws.amazonlocation.DELAY_5000
import com.aws.amazonlocation.R
import com.aws.amazonlocation.STREETS
import com.aws.amazonlocation.TEST_FAILED_BUTTON_DIRECTION
import com.aws.amazonlocation.TEST_FAILED_CARD_DRIVE_GO
import com.aws.amazonlocation.TEST_FAILED_NO_SEARCH_RESULT
import com.aws.amazonlocation.TEST_WORD_4
import com.aws.amazonlocation.di.AppModule
import com.aws.amazonlocation.enableGPS
import com.aws.amazonlocation.utils.KEY_MAP_NAME
import com.aws.amazonlocation.utils.KEY_MAP_STYLE_NAME
import com.aws.amazonlocation.utils.PreferenceManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@UninstallModules(AppModule::class)
@HiltAndroidTest
class ExploreFragmentMapFunctionWithoutAwsLoginTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )

    @get:Rule
    var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private val uiDevice = UiDevice.getInstance(getInstrumentation())
    private lateinit var preferenceManager: PreferenceManager

    @Before
    fun setUp() {
        preferenceManager = PreferenceManager(ApplicationProvider.getApplicationContext())
        preferenceManager.setValue(KEY_MAP_STYLE_NAME, mActivityRule.activity.getString(R.string.map_light))
        preferenceManager.setValue(KEY_MAP_NAME, mActivityRule.activity.getString(R.string.esri))
    }

    @Test
    fun showMapFunctionWithoutAwsLoginTest() {
        enableGPS(ApplicationProvider.getApplicationContext())
        uiDevice.wait(Until.hasObject(By.desc(AMAZON_MAP_READY)), DELAY_15000)
        Thread.sleep(DELAY_2000)
        val map = uiDevice.findObject(UiSelector().resourceId("com.aws.amazonlocation:id/mapView"))
        map.swipeLeft(50)
        Thread.sleep(DELAY_1000)

        val btnCardMap =
            onView(withId(R.id.card_map)).check(ViewAssertions.matches(isDisplayed()))
        btnCardMap?.perform(click())
        Thread.sleep(DELAY_1000)

        uiDevice.findObject(By.text(STREETS))?.click()

        Thread.sleep(DELAY_2000)
        val cardMapStyleClose =
            onView(withId(R.id.card_map_style_close)).check(ViewAssertions.matches(isDisplayed()))
        cardMapStyleClose?.perform(click())

        Thread.sleep(DELAY_2000)
        val edtSearch =
            onView(withId(R.id.edt_search_places)).check(ViewAssertions.matches(isDisplayed()))
        edtSearch?.perform(click())
        onView(withId(R.id.edt_search_places))?.perform(typeText(TEST_WORD_4))
        uiDevice.wait(
            Until.hasObject(By.res("com.aws.amazonlocation:id/rv_search_places_suggestion")),
            DELAY_10000
        )
        val rvSearchPlaceSuggestion =
            mActivityRule.activity.findViewById<RecyclerView>(R.id.rv_search_places_suggestion)
        rvSearchPlaceSuggestion.adapter?.itemCount?.let {
            if (it > 0) {
                onView(withId(R.id.rv_search_places_suggestion))?.perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        click()
                    )
                )

                uiDevice.wait(
                    Until.hasObject(By.res("com.aws.amazonlocation:id/tv_direction_time")),
                    DELAY_5000
                )

                val btnDirection1 =
                    mActivityRule.activity.findViewById<MaterialCardView>(R.id.btn_direction)
                if (btnDirection1.visibility == View.VISIBLE) {
                    mActivityRule.activity.runOnUiThread {
                        btnDirection1.performClick()
                    }

                    val tvGetLocation =
                        mActivityRule.activity.findViewById<TextInputEditText>(R.id.edt_search_direction)

                    if (tvGetLocation.text.isNullOrEmpty()) {
                        uiDevice.findObject(By.text(mActivityRule.activity.getString(R.string.label_my_location)))
                            ?.click()
                    }

                    uiDevice.wait(
                        Until.hasObject(By.res("com.aws.amazonlocation:id/card_drive_go")),
                        DELAY_5000
                    )

                    val cardDriveGo =
                        mActivityRule.activity.findViewById<MaterialCardView>(R.id.card_drive_go)
                    if (cardDriveGo.visibility == View.VISIBLE) {
                        mActivityRule.activity.runOnUiThread {
                            cardDriveGo.performClick()
                        }

                        uiDevice.wait(
                            Until.hasObject(By.res("com.aws.amazonlocation:id/rv_navigation_list")),
                            DELAY_5000
                        )

                        val btnExit =
                            mActivityRule.activity.findViewById<AppCompatButton>(R.id.btn_exit)
                        Assert.assertTrue(btnExit.visibility == View.VISIBLE)
                    } else {
                        Assert.fail(TEST_FAILED_CARD_DRIVE_GO)
                    }
                } else {
                    Assert.fail(TEST_FAILED_BUTTON_DIRECTION)
                }
            } else {
                Assert.fail(TEST_FAILED_NO_SEARCH_RESULT)
            }
        }
    }
}
