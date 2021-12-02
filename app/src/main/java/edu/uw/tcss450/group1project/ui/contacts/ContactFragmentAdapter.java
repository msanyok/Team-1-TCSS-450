/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * ContactFragmentPagerAdapter is a class for containing all contacts-related fragments
 * for dynamic swipe display.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ContactFragmentAdapter extends FragmentStateAdapter {

    /** The fragment list to be displayed */
    private List<Fragment> mFrags;

    /**
     * Creates a new ContactFragmentPagerAdapter with the provided fragment manager and list
     * of fragments
     *
     * @param theManager the fragment manager
     * @param theLifeCycle the life cycle to be assigned
     * @param theFrags the fragment list to be displayed
     */
    public ContactFragmentAdapter(@NonNull final FragmentManager theManager,
                                  final Lifecycle theLifeCycle,
                                  final List<Fragment> theFrags) {
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
