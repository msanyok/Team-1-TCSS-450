/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.model.ContactNotificationViewModel;

/**
 * ContactsParentFragment is a class for containing child data fragments via a view pager.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ContactsParentFragment extends Fragment {

    /** The String shown at the top of the contacts tab */
    public static final String ALL_CONTACTS = "All Contacts";

    /** The String shown at the top of the requests tab */
    public static final String REQUESTS = "Requests";

    /** The String shown at the top of the new requests tab */
    public static final String NEW_REQUESTS = "New Request";

    /** The tab layout that stores the tabs */
    private TabLayout mTabs;

    /** The view pager */
    private ViewPager2 mViewPager;

    /** The color for notification badges */
    private int mBadgeColor;

    /** The color for notification badge text */
    private int mBadgeTextColor;

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

        final ContactNotificationViewModel contactNotificationViewModel =
                new ViewModelProvider(getActivity()).get(ContactNotificationViewModel.class);

        contactNotificationViewModel.addContactRequestNotifObserver(getViewLifecycleOwner(),
                this::observeContactRequestNotificationChange);

        contactNotificationViewModel.addContactsNotifObserver(getViewLifecycleOwner(),
                this::observeContactsNotificationChange);

        int[] attr = { R.attr.colorAccent, R.attr.buttonTextColor };
        TypedArray tA = theView.getContext().obtainStyledAttributes(attr);
        mBadgeColor = tA.getResourceId(0, R.color.rose);
        mBadgeTextColor = tA.getResourceId(1, R.color.white);
        tA.recycle();

        ContactsParentFragmentArgs args = ContactsParentFragmentArgs.fromBundle(getArguments());
        mViewPager.setCurrentItem(args.getViewPagerIndex(), false);
    }

    /**
     * Completes the actions required when the contact notification state changes
     *
     * @param theContactsSet the set of contacts that represent the updated state
     */
    private void observeContactsNotificationChange(final Set<String> theContactsSet) {
        int count = theContactsSet.size();
        if (count > 0) {
            BadgeDrawable badge = mTabs.getTabAt(0).getOrCreateBadge();
            badge.setMaxCharacterCount(2);
            badge.setBackgroundColor(
                    getResources().getColor(mBadgeColor, getActivity().getTheme()));
            badge.setBadgeTextColor(
                    getResources().getColor(mBadgeTextColor, getActivity().getTheme()));
            badge.setNumber(count);
        } else {
            mTabs.getTabAt(0).removeBadge();
        }
    }

    /**
     * Completes the actions required when the contact notification state changes
     *
     * @param theContactRequestSet the set of contact requests Strings
     *                             that represent the updated state
     */
    private void observeContactRequestNotificationChange(final Set<String> theContactRequestSet) {
        int count = theContactRequestSet.size();

        if (count > 0) {
            BadgeDrawable badge = mTabs.getTabAt(1).getOrCreateBadge();
            badge.setMaxCharacterCount(2);
            badge.setBackgroundColor(
                    getResources().getColor(mBadgeColor, getActivity().getTheme()));
            badge.setBadgeTextColor(
                    getResources().getColor(mBadgeTextColor, getActivity().getTheme()));
            badge.setNumber(count);
        } else {
            mTabs.getTabAt(1).removeBadge();
        }
    }

    /**
     * Returns the title text of the tab that is currently active.
     *
     * @return the title text of the tab that is currently active.
     */
    public String getCurrentTabString() {
        return mTabs.getTabAt(mViewPager.getCurrentItem()).getText().toString();
    }
}