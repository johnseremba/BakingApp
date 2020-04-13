package com.example.bakingapp;

import android.content.res.Resources;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(JUnit4.class)
@LargeTest
public class MainActivityTest {
    private static final int ITEM_BELOW_FOLD = 1;

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivity =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void uiInitializedProperly() {
        Resources res = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        onView(withText(res.getString(R.string.title_baking_time)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.fragment_container))
                .check(matches(isDisplayed()));
    }

    @Test
    public void scrollToItemBelowFold_checkItExists() {
        // Check existence of recycler view
        onView(withId(R.id.recipes_recycler_view))
                .check(matches(isDisplayed()));

        // check if an element called Brownies exists
        onView(withText("Brownies")).check(matches(isDisplayed()));

        String recipeTitle = InstrumentationRegistry.getInstrumentation()
                .getTargetContext()
                .getResources()
                .getString(R.string.title_recipe, "Brownies");

        // click second item
        onView(withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM_BELOW_FOLD, click()));

        // check if the correct title is displayed
        onView(withId(R.id.text_recipe_label))
                .check(matches(withText(recipeTitle)));
    }

    @Test
    public void launchRecipeStepFragment_checkElementsDisplayedCorrectly() {
        onView(withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM_BELOW_FOLD, click()));

        // Check if title is displayed correctly
        onView(withText("Brownies"))
                .check(matches(isDisplayed()));

        // Check if image view exists
        onView(withId(R.id.image_recipe_image))
                .check(matches(isDisplayed()));

        // Check for ingredients label
        onView(withId(R.id.label_ingredients))
                .check(matches(isDisplayed()));

        // Check for ingredients list, count elements and verify that they are exactly 10
        onView(withId(R.id.ingredients_recycler_view))
                .check(matches(isDisplayed()))
                .check(new RecyclerViewCountAssertion(10));

        // Check for Recipes label
        onView(withId(R.id.label_steps))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        // Check for recipe steps list, count elements and verify that they are exactly 10
        onView(withId(R.id.recipe_steps_recycler_view))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .check(new RecyclerViewCountAssertion(10));
    }

    @Test
    public void viewStepDataDisplayedCorrectly() {
        // find recipes_recycler_view
        onView(withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM_BELOW_FOLD, click()));

        // Under the recipes list, click step one, launch view step fragment
        onView(withId(R.id.recipe_steps_recycler_view))
                .perform(scrollTo(), RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check if the view Recipe Step Fragment is displayed properly
        // check if player view is visible
        onView(withId(R.id.player_view))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        // Check recipe description
        onView(withId(R.id.text_step_description))
                .check(matches(withText("Recipe Introduction")));

        // Check if Prev button is disabled on first launch
        onView(withId(R.id.button_prev))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        // Check if Next button exists, click it
        onView(withId(R.id.button_next))
                .check(matches(isDisplayed()))
                .perform(click());

        // After clicking the next button, check if the prev button is enabled
        onView(withId(R.id.button_prev))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));
    }

    public static class RecyclerViewCountAssertion implements ViewAssertion {
        private final int expectedCount;

        RecyclerViewCountAssertion(int expectedCount) {
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
}
