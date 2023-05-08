package com.aws.amazonlocation.ui.main

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.Until.hasObject
import com.amplifyframework.geo.maplibre.view.MapLibreView
import com.aws.amazonlocation.ACCESS_COARSE_LOCATION
import com.aws.amazonlocation.ACCESS_FINE_LOCATION
import com.aws.amazonlocation.AMAZON_MAP_READY
import com.aws.amazonlocation.BaseTest
import com.aws.amazonlocation.BuildConfig
import com.aws.amazonlocation.DELAY_1000
import com.aws.amazonlocation.DELAY_15000
import com.aws.amazonlocation.DELAY_2000
import com.aws.amazonlocation.DELAY_5000
import com.aws.amazonlocation.R
import com.aws.amazonlocation.TEST_FAILED
import com.aws.amazonlocation.TEST_FAILED_LATCH_TIMEOUT
import com.aws.amazonlocation.TEST_FAILED_MAP_NOT_FOUND
import com.aws.amazonlocation.TEST_FAILED_ZOOM_LEVEL
import com.aws.amazonlocation.TEST_FAILED_ZOOM_LEVEL_NOT_CHANGED
import com.aws.amazonlocation.di.AppModule
import com.aws.amazonlocation.enableGPS
import com.aws.amazonlocation.failTest
import com.aws.amazonlocation.utils.MapCameraZoom
import com.mapbox.mapboxsdk.maps.MapboxMap
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@UninstallModules(AppModule::class)
@HiltAndroidTest
class ExploreFragmentMapZoomInOutTest : BaseTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION,
    )

    @get:Rule
    var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private val uiDevice = UiDevice.getInstance(getInstrumentation())

    @Test
    fun testMapZoomIn() {
        try {
            enableGPS(ApplicationProvider.getApplicationContext())
            var mapbox: MapboxMap? = null
            uiDevice.wait(Until.hasObject(By.desc(AMAZON_MAP_READY)), DELAY_15000)
            Thread.sleep(DELAY_1000)

            val mapView = mActivityRule.activity.findViewById<MapLibreView>(R.id.mapView)
            mapView.getMapAsync {
                mapbox = it
            }
            setDefaultZoom(mapbox)
            val beforeZoomLevel: Double? = mapbox?.cameraPosition?.zoom
            uiDevice.wait(hasObject(By.res("${BuildConfig.APPLICATION_ID}:id/mapView")), DELAY_5000)
            val map = uiDevice.findObject(UiSelector().resourceId("${BuildConfig.APPLICATION_ID}:id/mapView"))
            if (map.exists()) {
                map.pinchOut(50, 15)
            } else {
                Assert.fail(TEST_FAILED_MAP_NOT_FOUND)
            }
            Thread.sleep(DELAY_2000)
            if (beforeZoomLevel != null) {
                mapbox?.cameraPosition?.zoom?.let {
                    Assert.assertTrue(TEST_FAILED_ZOOM_LEVEL_NOT_CHANGED, beforeZoomLevel < it)
                }
            } else {
                Assert.fail(TEST_FAILED_ZOOM_LEVEL)
            }
        } catch (e: Exception) {
            failTest(79, e)
            Assert.fail(TEST_FAILED)
        }
    }

    @Test
    fun testMapZoomOut() {
        try {
            var mapbox: MapboxMap? = null
            uiDevice.wait(Until.hasObject(By.desc(AMAZON_MAP_READY)), DELAY_15000)
            Thread.sleep(DELAY_1000)

            val mapView = mActivityRule.activity.findViewById<MapLibreView>(R.id.mapView)
            mapView.getMapAsync {
                mapbox = it
            }
            setDefaultZoom(mapbox)
            val beforeZoomLevel: Double? = mapbox?.cameraPosition?.zoom
            uiDevice.wait(hasObject(By.res("${BuildConfig.APPLICATION_ID}:id/mapView")), DELAY_5000)
            val map = uiDevice.findObject(UiSelector().resourceId("${BuildConfig.APPLICATION_ID}:id/mapView"))
            if (map.exists()) {
                map.pinchIn(50, 15)
            } else {
                Assert.fail(TEST_FAILED_MAP_NOT_FOUND)
            }
            Thread.sleep(DELAY_2000)
            if (beforeZoomLevel != null) {
                mapbox?.cameraPosition?.zoom?.let {
                    Assert.assertTrue(TEST_FAILED_ZOOM_LEVEL_NOT_CHANGED, beforeZoomLevel > it)
                }
            } else {
                Assert.fail(TEST_FAILED_ZOOM_LEVEL)
            }
        } catch (e: Exception) {
            failTest(108, e)
            Assert.fail(TEST_FAILED)
        }
    }

    @Test
    fun testMapZoomDoubleTap() {
        try {
            var mapbox: MapboxMap? = null
            uiDevice.wait(Until.hasObject(By.desc(AMAZON_MAP_READY)), DELAY_15000)
            Thread.sleep(DELAY_1000)

            val mapView = mActivityRule.activity.findViewById<MapLibreView>(R.id.mapView)
            mapView.getMapAsync {
                mapbox = it
            }
            setDefaultZoom(mapbox)
            val beforeZoomLevel: Double? = mapbox?.cameraPosition?.zoom
            uiDevice.wait(hasObject(By.res("${BuildConfig.APPLICATION_ID}:id/mapView")), DELAY_5000)
            val map = uiDevice.findObject(UiSelector().resourceId("${BuildConfig.APPLICATION_ID}:id/mapView"))
            if (map.exists()) {
                onView(withId(R.id.mapView)).perform(ViewActions.doubleClick())
            } else {
                Assert.fail(TEST_FAILED_MAP_NOT_FOUND)
            }
            Thread.sleep(DELAY_2000)
            if (beforeZoomLevel != null) {
                mapbox?.cameraPosition?.zoom?.let {
                    Assert.assertTrue(TEST_FAILED_ZOOM_LEVEL_NOT_CHANGED, beforeZoomLevel < it)
                }
            } else {
                Assert.fail(TEST_FAILED_ZOOM_LEVEL)
            }
        } catch (e: Exception) {
            failTest(138, e)
            Assert.fail(TEST_FAILED)
        }
    }

    fun setDefaultZoom(mapboxMap: MapboxMap?) {
        val latch = CountDownLatch(1)

        getInstrumentation().runOnMainSync {
            mapboxMap?.getStyle {
                mapboxMap.moveCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.zoomTo(MapCameraZoom.DEFAULT_CAMERA_ZOOM))
                latch.countDown()
            }
        }

        val success = latch.await(DELAY_5000, TimeUnit.MILLISECONDS)

        Assert.assertTrue(TEST_FAILED_LATCH_TIMEOUT, success)
    }
}
