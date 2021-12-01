/**
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * WeatherFragmentPagerAdatper is a class for displaying individual weather fragments.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherFragmentPagerAdapter extends FragmentStateAdapter {

    /** The fragments to be displayed */
    private final List<WeatherFragment> mFrags;

    /**
     * Constructs a new WeatherFragmentPagerAdapter with the provided fragment manager and list
     * of fragments to be displayed
     *
     * @param theManager the fragment manager
     * @param theFrags the list of fragments to be displayed
     */
    public WeatherFragmentPagerAdapter(@NonNull final FragmentManager theManager,
                                       final Lifecycle theLifeCycle,
                                       final List<WeatherFragment> theFrags) {
        super(theManager, theLifeCycle);
        mFrags = theFrags;
    }

//    @NonNull
//    @Override
//    public Fragment getItem(final int thePosition) {
//        return mFrags.get(thePosition);
//    }
//
//    @Override
//    public int getCount() {
//        return mFrags.size();
//    }

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
