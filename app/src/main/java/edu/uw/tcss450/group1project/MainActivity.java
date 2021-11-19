/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * A {@link AppCompatActivity} subclass that is responsible
 * for the main activities of the app.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @author Chris Ding
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class MainActivity extends ThemedActivity {

    /**
     * The configuration for the bottom navigation displayed
     * on fragments in this activity
     */
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt()))
                        .get(UserInfoViewModel.class);

        applyTheme();
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_contacts,
                R.id.navigation_messages, R.id.navigation_weather)
                .build();
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(
                this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu theMenu) {
        getMenuInflater().inflate(R.menu.toolbar, theMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem theItem) {
        int id = theItem.getItemId();
        if (id == R.id.action_settings) {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.navigation_settings);
            return true;
        }
        return super.onOptionsItemSelected(theItem);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Function of warning for deleting a contact using alert dialog
     *
     * @param theView the view to be assigned
     */
    public void showContactDeleteAlertDialog(final View theView) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Deleting this contact " +
                "will be permanent. Are you sure?</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Delete</font>"),
                (dialog, which) -> {
                    //TODO add delete function in it
                    Toast.makeText(getApplicationContext(),"You have deleted this contact.",
                            Toast.LENGTH_SHORT).show();
        });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }
}
