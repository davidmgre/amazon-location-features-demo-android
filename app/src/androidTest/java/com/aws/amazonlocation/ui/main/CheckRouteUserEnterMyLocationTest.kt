package com.aws.amazonlocation.ui.main

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.aws.amazonlocation.AMAZON_MAP_READY
import com.aws.amazonlocation.BaseTestMainActivity
import com.aws.amazonlocation.BuildConfig
import com.aws.amazonlocation.DELAY_15000
import com.aws.amazonlocation.DELAY_2000
import com.aws.amazonlocation.DELAY_20000
import com.aws.amazonlocation.DELAY_3000
import com.aws.amazonlocation.DELAY_5000
import com.aws.amazonlocation.MY_LOCATION
import com.aws.amazonlocation.R
import com.aws.amazonlocation.TEST_FAILED
import com.aws.amazonlocation.TEST_FAILED_CARD_DRIVE_GO
import com.aws.amazonlocation.TEST_FAILED_DIRECTION_CARD
import com.aws.amazonlocation.TEST_FAILED_ORIGIN_TEXT_NOT_MY_LOCATION
import com.aws.amazonlocation.TEST_FAILED_SEARCH_DIRECTION
import com.aws.amazonlocation.TEST_WORD_2
import com.aws.amazonlocation.di.AppModule
import com.aws.amazonlocation.enableGPS
import com.aws.amazonlocation.failTest
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert
import org.junit.Test

@UninstallModules(AppModule::class)
@HiltAndroidTest
class CheckRouteUserEnterMyLocationTest : BaseTestMainActivity() {

    private val uiDevice = UiDevice.getInstance(getInstrumentation())

    @Test
    fun showRouteUserEnterMyLocationTest() {
        try {
            enableGPS(ApplicationProvider.getApplicationContext())
            uiDevice.wait(Until.hasObject(By.desc(AMAZON_MAP_READY)), DELAY_15000)
            Thread.sleep(DELAY_2000)
            val cardDirection =
                mActivityRule.activity.findViewById<MaterialCardView>(R.id.card_direction)
            if (cardDirection.visibility == View.VISIBLE) {
                val cardDirectionTest =
                    onView(withId(R.id.card_direction)).check(matches(isDisplayed()))
                cardDirectionTest.perform(click())
                uiDevice.wait(
                    Until.hasObject(By.res("${BuildConfig.APPLICATION_ID}:id/edt_search_direction")),
                    DELAY_5000,
                )
                val edtSearchDirection =
                    mActivityRule.activity.findViewById<TextInputEditText>(R.id.edt_search_direction)
                if (edtSearchDirection.visibility == View.VISIBLE) {
                    val clMyLocation =
                        onView(withId(R.id.cl_my_location)).check(matches(isDisplayed()))
                    clMyLocation.perform(click())
                    Thread.sleep(DELAY_2000)
                    Assert.assertTrue(TEST_FAILED_ORIGIN_TEXT_NOT_MY_LOCATION, edtSearchDirection.text.toString().trim() == MY_LOCATION)
                    onView(withId(R.id.edt_search_dest)).perform(ViewActions.replaceText(TEST_WORD_2))
                    Thread.sleep(DELAY_3000)
                    uiDevice.wait(
                        Until.hasObject(By.res("${BuildConfig.APPLICATION_ID}:id/rv_search_places_suggestion_direction")),
                        DELAY_20000,
                    )
                    Thread.sleep(DELAY_3000)
                    onView(withId(R.id.rv_search_places_suggestion_direction)).check(
                        matches(
                            hasMinimumChildCount(1),
                        ),
                    )
                    Thread.sleep(DELAY_2000)
                    onView(withId(R.id.rv_search_places_suggestion_direction)).perform(
                        RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            0,
                            click(),
                        ),
                    )
                    uiDevice.wait(
                        Until.hasObject(By.res("${BuildConfig.APPLICATION_ID}:id/card_drive_go")),
                        DELAY_20000,
                    )
                    Thread.sleep(DELAY_5000)

                    val cardDriveGo =
                        mActivityRule.activity.findViewById<MaterialCardView>(R.id.card_drive_go)
                    Assert.assertTrue(TEST_FAILED_CARD_DRIVE_GO, cardDriveGo.isVisible)
                } else {
                    Assert.fail(TEST_FAILED_SEARCH_DIRECTION)
                }
            } else {
                Assert.fail(TEST_FAILED_DIRECTION_CARD)
            }
        } catch (e: Exception) {
            failTest(121, e)
            Assert.fail(TEST_FAILED)
        }
    }
}
