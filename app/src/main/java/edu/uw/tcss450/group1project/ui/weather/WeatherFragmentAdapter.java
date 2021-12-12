/**
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * WeatherFragmentPagerAdapter is a class for displaying individual weather fragments.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherFragmentAdapter extends FragmentStateAdapter {

    /** The fragments to be displayed */
    private final List<WeatherFragment> mFrags;

    /**
     * Constructs a new WeatherFragmentPagerAdapter with the provided fragment manager, lifecycle,
     * and list of fragments to be displayed
     *
     * @param theManager the fragment manager
     * @param theLifeCycle the life cycle to be assigned
     * @param theFrags the list of fragments to be displayed
     */
    public WeatherFragmentAdapter(@NonNull final FragmentManager theManager,
                                  final Lifecycle theLifeCycle,
                                  final List<WeatherFragment> theFrags) {
        super(theManager, theLifeCycle);
        mFrags = theFrags;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFrags.get(position);
    }

    @Override
    public int getItemCount() {
        return mFrags.size();
    }
}