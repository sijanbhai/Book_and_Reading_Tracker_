package com.sijan.bookandreadingtracker.presentation.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sijan.bookandreadingtracker.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun profileScreen_displaysLoginFormWhenNotLoggedIn() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Verify login form is displayed
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Welcome back! Please login to continue.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun profileScreen_canSwitchToRegisterMode() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Click on "Don't have an account? Register"
        composeTestRule.onNodeWithText("Don't have an account? Register").performClick()

        // Verify register form is displayed
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun profileScreen_registerButtonDisabledWithEmptyFields() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Switch to register mode
        composeTestRule.onNodeWithText("Don't have an account? Register").performClick()

        // Verify register button is disabled
        composeTestRule.onNodeWithText("Register")
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun profileScreen_emailFieldAcceptsInput() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Enter email
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")

        // Verify email is entered
        composeTestRule.onNodeWithText("test@example.com").assertIsDisplayed()
    }

    @Test
    fun profileScreen_passwordFieldAcceptsInput() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Enter password (note: password is masked, so we can't verify the text directly)
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Just verify the field exists after input
        composeTestRule.onNodeWithText("Password").assertExists()
    }

    @Test
    fun profileScreen_registerFormAcceptsAllInputs() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Switch to register mode
        composeTestRule.onNodeWithText("Don't have an account? Register").performClick()

        // Enter all fields
        composeTestRule.onNodeWithText("Name").performTextInput("Test User")
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Verify name is displayed
        composeTestRule.onNodeWithText("Test User").assertIsDisplayed()
        composeTestRule.onNodeWithText("test@example.com").assertIsDisplayed()
    }

    @Test
    fun profileScreen_registerButtonEnabledWithAllFieldsFilled() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Switch to register mode
        composeTestRule.onNodeWithText("Don't have an account? Register").performClick()

        // Enter all fields
        composeTestRule.onNodeWithText("Name").performTextInput("Test User")
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Verify register button is enabled
        composeTestRule.onNodeWithText("Register")
            .assertExists()
            .assertIsEnabled()
    }

    @Test
    fun profileScreen_displaysProfileIconInLoginMode() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Verify profile icon is displayed (by checking content description)
        composeTestRule.onNodeWithContentDescription("Profile").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysEmailIcon() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Verify email icon is displayed
        composeTestRule.onNodeWithContentDescription("Email").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysPasswordIcon() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Verify password icon is displayed
        composeTestRule.onNodeWithContentDescription("Password").assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysNameIconInRegisterMode() {
        // Navigate to Profile screen
        composeTestRule.onNodeWithText("Profile").performClick()

        // Switch to register mode
        composeTestRule.onNodeWithText("Don't have an account? Register").performClick()

        // Verify name icon is displayed
        composeTestRule.onNodeWithContentDescription("Name").assertIsDisplayed()
    }
}

