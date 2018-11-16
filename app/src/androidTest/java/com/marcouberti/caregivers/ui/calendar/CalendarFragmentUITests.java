package com.marcouberti.caregivers.ui.calendar;


import com.marcouberti.caregivers.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    private void drain() throws TimeoutException, InterruptedException {
        mCountingTaskExecutorRule.drainTasks(1, TimeUnit.MINUTES);
    }
}