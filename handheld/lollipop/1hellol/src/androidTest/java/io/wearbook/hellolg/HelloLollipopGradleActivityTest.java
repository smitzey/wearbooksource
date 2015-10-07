package io.wearbook.hellolg;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class io.wearbook.hellolg.HelloLollipopGradleActivityTest \
 * io.wearbook.hellolg.tests/android.test.InstrumentationTestRunner
 */
public class HelloLollipopGradleActivityTest extends ActivityInstrumentationTestCase2<HelloLollipopGradleActivity> {

    public HelloLollipopGradleActivityTest() {
        super("io.wearbook.hellolg", HelloLollipopGradleActivity.class);
    }

}
