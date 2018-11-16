package com.marcouberti.caregivers.ui.calendar;


import android.widget.TextView;

import com.marcouberti.caregivers.R;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.core.IsNot.not;
import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class CalendarFragmentUITests {

    @Rule
    public ActivityTestRule<CalendarActivity> mActivityRule = new ActivityTestRule<>(
            CalendarActivity.class);

    @Rule
    public CountingTaskExecutorRule mCountingTaskExecutorRule = new CountingTaskExecutorRule();

    @Before
    public void disableRecyclerViewAnimations() {
        // Disable RecyclerView animations
        //EspressoTestUtil.disableAnimations(mActivityRule);
    }

    @Test
    public void clickOnFirstItem_opensSlotDetails() throws Throwable {
        drain();
        // When clicking on the first time slot
        onView(ViewMatchers.withId(R.id.slot_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        drain();
        // Then the second screen with the slot details should appear.
        onView(withId(R.id.room))
                .check(matches(isDisplayed()));
        drain();
        // Then the second screen with the slot details should appear.
        onView(withId(R.id.patient))
                .check(matches(not(withText(""))));

    }

    @Test
    public void clickOnNextDayActionIcon_changeTheDate() throws Throwable {
        drain();
        // When clicking on the first time slot
        onView(ViewMatchers.withId(R.id.action_next_day))
                .perform(click());
        drain();
        // Then the second screen with the slot details should appear.
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(InstrumentationRegistry.
                        getInstrumentation().getTargetContext().getString(R.string.tomorrow))));
        drain();
    }

    @Test
    public void clickOnPrevDayActionIcon_changeTheDate() throws Throwable {
        drain();
        // When clicking on the first time slot
        onView(ViewMatchers.withId(R.id.action_prev_day))
                .perform(click());
        drain();
        // Then the second screen with the slot details should appear.
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(InstrumentationRegistry.
                        getInstrumentation().getTargetContext().getString(R.string.yesterday))));
        drain();
    }


    @Test
    public void clickOnAutoFillButton_callTheRunWorkers() throws Throwable {

        drain();

        CalendarFragment fragment = (CalendarFragment)mActivityRule.getActivity().
                getSupportFragmentManager().getFragments().get(0);

        CalendarViewModel mockViewModel = Mockito.mock(CalendarViewModel.class);
        fragment.mViewModel = mockViewModel;

        // When clicking on the auto fill fab
        onView(ViewMatchers.withId(R.id.fab_auto_fill))
                .perform(click());

        drain();

        // Then the view model is called in order to run the workers
        verify(mockViewModel, times(1)).onAutoFillTap();
    }

    private void drain() throws TimeoutException, InterruptedException {
        mCountingTaskExecutorRule.drainTasks(1, TimeUnit.MINUTES);
    }
}