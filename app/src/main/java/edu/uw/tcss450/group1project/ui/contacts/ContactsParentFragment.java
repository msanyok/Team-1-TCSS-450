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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.model.ContactTabNewCountViewModel;

/**
 * ContactsParentFragment is a class for containing child data fragments via a view pager.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ContactsParentFragment extends Fragment {
    /** The String shown at the top of the contacts tab */
    public static final String ALL_CONTACTS = "All Contacts";

    /** The String shown at the top of the requests tab */
    public static final String REQUESTS = "Requests";

    /** The String shown at the top of the new requests tab */
    public static final String NEW_REQUESTS = "New Request";

    private TabLayout mTabs;

    private ViewPager2 mViewPager;


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
        mViewPager = getView().findViewById(R.id.view_pager);
        List<Fragment> frags = new ArrayList<>();
        frags.add(new ContactsFragment());
        frags.add(new ContactRequestsFragment());
        frags.add(new NewContactRequestFragment());
        ContactFragmentAdapter pagerAdapter =
                new ContactFragmentAdapter(getChildFragmentManager(), getLifecycle(), frags);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabs = getView().findViewById(R.id.tab_layout);
        new TabLayoutMediator(mTabs, mViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(ALL_CONTACTS);
                    break;
                case 1:
                    tab.setText(REQUESTS);
                    break;
                case 2:
                    tab.setText(NEW_REQUESTS);
                    break;
            }
        }).attach();


        final ContactTabNewCountViewModel notifViewModel = new ViewModelProvider(getActivity()).
                get(ContactTabNewCountViewModel.class);

        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTab = tab.getText().toString();
                // todo: remove if they go onto this tab
                if (selectedTab.equals(ALL_CONTACTS)) {
                    System.out.println(ALL_CONTACTS);
                    notifViewModel.removeTabNotifications(ALL_CONTACTS);
                    notifViewModel.addNotification(REQUESTS);
                } else if (selectedTab.equals(REQUESTS)) {
                    System.out.println(REQUESTS);
                    notifViewModel.removeTabNotifications(REQUESTS);
                    notifViewModel.addNotification(ALL_CONTACTS);
                } else if (selectedTab.equals(NEW_REQUESTS)) {
                    System.out.println(NEW_REQUESTS);
                    notifViewModel.removeTabNotifications(REQUESTS);
                    notifViewModel.removeTabNotifications(ALL_CONTACTS);
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // unused
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // unused
            }
        });

        notifViewModel.addContactNotifObserver(getViewLifecycleOwner(),
                this::observeNotificationChange);


    }

    private void observeNotificationChange(final Map<String, Integer> theMap) {

        int contactCount = theMap.getOrDefault(ALL_CONTACTS, 0);
        int requestCount = theMap.getOrDefault(REQUESTS, 0);

        if (contactCount > 0) {
            mTabs.getTabAt(0).getOrCreateBadge().setNumber(contactCount);
        } else {
            mTabs.getTabAt(0).removeBadge();
        }

        if (requestCount > 0) {
            mTabs.getTabAt(1).getOrCreateBadge().setNumber(requestCount);
        } else {
            mTabs.getTabAt(1).removeBadge();
        }


    }

    public String getCurrentTabString() {
        return mTabs.getTabAt(mViewPager.getCurrentItem()).getText().toString();
    }


}
