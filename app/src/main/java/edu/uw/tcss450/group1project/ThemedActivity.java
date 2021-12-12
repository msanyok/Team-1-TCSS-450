/*
 * TCSS 450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project;

import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ThemedActivity is a class providing functionality for dynamic theme changing in app.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ThemedActivity extends AppCompatActivity {

    /** The key used for shared preferences insertion */
    private static final String KEY_THEME = "Theme";

    /** The assigned theme */
    private Integer mTheme;

    @Override
    protected void onCreate(final Bundle theSavedInstanceState) {
        mTheme = PreferenceManager.getDefaultSharedPreferences(this).contains(KEY_THEME) ?
                 (int) PreferenceManager.getDefaultSharedPreferences(this)
                        .getAll().get(KEY_THEME) : R.style.AppTheme1;

        super.onCreate(theSavedInstanceState);
    }

    /**
     * Applies this ThemedActivity's assigned theme
     */
    public void applyTheme() {
        setTheme(mTheme);
    }

    /**
     * Changes this ThemedActivity's theme to the provided theme
     *
     * @param theTheme the theme to be assigned
     */
    public void changeTheme(final int theTheme) {
        mTheme = theTheme;
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putInt(KEY_THEME, mTheme).apply();
    }
}