package com.example.cardiacrecorder;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class UI_Test {

    @Rule
    public ActivityScenarioRule<Login> loginRule = new ActivityScenarioRule<>(Login.class);

//    @Rule
//    public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAll() {
        testLogin();
        testAddData();
        testUpdateData();
        testDeleteData();
        testLogout();
    }

    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }

    public static ViewAction waitFor(final Matcher<View> viewMatcher) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(View.class);
            }

            @Override
            public String getDescription() {
                return "Wait until view is " + viewMatcher.toString();
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + 5000; // Adjust the timeout as needed
                while (System.currentTimeMillis() < endTime) {
                    if (viewMatcher.matches(view)) {
                        return;
                    }
                    uiController.loopMainThreadForAtLeast(50);
                }
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    @Test
    public void testLogin(){
        try {
            Thread.sleep(2000); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create a CountDownLatch with a count of 1
        final CountDownLatch latch = new CountDownLatch(1);



        onView(withId(R.id.loginEmail)).perform(ViewActions.typeText("d@gmail.com"));
        onView(withId(R.id.loginPassword)).perform(ViewActions.typeText("123456"));
        closeSoftKeyboard();
        onView(withId(R.id.logInBtn)).perform(click());

        // Wait for the RecyclerView action to complete
        try {
            latch.await(5, TimeUnit.SECONDS); // Adjust the timeout as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.buttonLogOut)).perform(waitFor(isDisplayed()));
        onView(withId(R.id.floatingActionAdd)).perform(waitFor(isDisplayed()));
    }


    @Test
    public void testLogout() {

        testLogin();
        try {
            Thread.sleep(2000); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.buttonLogOut)).perform(click());
        onView(withId(R.id.login)).check(matches(withText("LOG IN")));
    }

    @Test
    public void testAddData(){
        testLogin();
        try {
            Thread.sleep(3000); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.floatingActionAdd)).perform(click());
        onView(withId(R.id.editTextSystolic)).perform(ViewActions.typeText("120"));
        onView(withId(R.id.editTextDiastolic)).perform(ViewActions.typeText("50"));
        onView(withId(R.id.editTextHeart)).perform(ViewActions.typeText("67"));
        onView(withId(R.id.editTextComment)).perform(ViewActions.typeText("dummy"));
        onView(withId(R.id.editTextDate)).perform(ViewActions.typeText("07-07-2023"));
        onView(withId(R.id.editTextTime)).perform(ViewActions.typeText("12:45"));
        closeSoftKeyboard();

        onView(withId(R.id.floatingActionSave)).perform(click());

        onView(withId(R.id.recordItems)).check(matches(isDisplayed()));
        onView(withText("dummy")).check(matches(isDisplayed()));
    }

    @Test
    public void testUpdateData(){
        testLogin();
        try {
            Thread.sleep(3000); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(withId(R.id.recordItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, Task.clickChildViewWithId(R.id.floatingActionEdit)
                )
        );

        Espresso.onView(ViewMatchers.withId(R.id.editTextSystolic)).perform(clearText());
        Espresso.onView(ViewMatchers.withId(R.id.editTextDiastolic)).perform(clearText());
        Espresso.onView(ViewMatchers.withId(R.id.editTextHeart)).perform(clearText());

        onView(withId(R.id.editTextSystolic)).perform(ViewActions.typeText("80"));
        onView(withId(R.id.editTextDiastolic)).perform(ViewActions.typeText("65"));
        onView(withId(R.id.editTextHeart)).perform(ViewActions.typeText("71"));
        closeSoftKeyboard();
        onView(withId(R.id.floatingActionSave)).perform(click());

        onView(withText("80mm-Hg")).check(matches(isDisplayed()));
        onView(withText("71beat/min")).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteData(){

        testLogin();
        try {
            Thread.sleep(3000); // Adjust the delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create a CountDownLatch with a count of 1
        final CountDownLatch latch = new CountDownLatch(1);

        // Perform action on the RecyclerView item
        Espresso.onView(ViewMatchers.withId(R.id.recordItems))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, Task.clickChildViewWithId(R.id.floatingActionDelete)));

        // Wait for the RecyclerView action to complete
        try {
            latch.await(5, TimeUnit.SECONDS); // Adjust the timeout as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Perform assertion after the RecyclerView action is completed
        Espresso.onView(withText("dummy")).check(doesNotExist());
    }

}
