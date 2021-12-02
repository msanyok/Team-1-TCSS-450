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
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.group1project.R;

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
        ViewPager2 viewPager = getView().findViewById(R.id.view_pager);
        List<Fragment> frags = new ArrayList<>();
        frags.add(new ContactsFragment());
        frags.add(new ContactRequestsFragment());
        frags.add(new NewContactRequestFragment());
        ContactFragmentAdapter pagerAdapter =
                new ContactFragmentAdapter(getChildFragmentManager(), getLifecycle(), frags);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = getView().findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All Contacts");
                    break;
                case 1:
                    tab.setText("Requests");
                    break;
                case 2:
                    tab.setText("New Request");
                    break;
            }
        }).attach();
    }
}
