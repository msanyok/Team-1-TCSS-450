/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.ui.home.HomeFragment;
import edu.uw.tcss450.group1project.ui.weather.WeatherFragment;

/**
 * ContactsParentFragment is a class for containing child data fragments via a view pager.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ContactsParentFragment extends Fragment {

    /**
     * Required empty constructor
     */
    public ContactsParentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_contacts_parent, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        ViewPager viewPager = getView().findViewById(R.id.view_pager);
        List<Fragment> frags = new ArrayList<>();
        frags.add(new ContactsFragment());
        frags.add(new ContactRequestsFragment());
        frags.add(new NewContactRequestFragment());
        ContactFragmentPagerAdapter pagerAdapter =
                new ContactFragmentPagerAdapter(getChildFragmentManager(), frags);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

//         THIS IS USEFUL
//        viewPager.setCurrentItem(2);
//        frags.add(new HomeFragment());
//        frags.remove(0);
//        pagerAdapter.notifyDataSetChanged();
    }
}
