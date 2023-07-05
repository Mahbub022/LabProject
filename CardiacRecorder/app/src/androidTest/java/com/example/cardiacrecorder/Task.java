package com.example.cardiacrecorder;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.Matcher;


public class Task {
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            // Override getConstraints() method
            // This method specifies the constraints on the view that is targeted by the action
            @Override
            public Matcher<View> getConstraints() {
                return null; // No constraints are specified, returning null
            }

            // Override getDescription() method
            // This method provides a description of the action
            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            // Override perform() method
            // This method performs the action on the target view
            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id); // Find the child view with the specified ID
                v.performClick(); // Perform a click on the child view
            }
        };
    }
}
