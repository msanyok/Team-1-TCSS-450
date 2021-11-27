package edu.uw.tcss450.group1project.ui.contacts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class ContactFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFrags;

    public ContactFragmentPagerAdapter(@NonNull final FragmentManager theManager,
                                       final List<Fragment> theFrags) {
        super(theManager);
        mFrags = theFrags;
    }

    @NonNull
    @Override
    public Fragment getItem(final int thePosition) {
//        switch (thePosition) {
//            case 0:
//                return new ContactsFragment();
//            case 1:
//                return new ContactRequestsFragment();
//        }
//        return new NewContactRequestFragment();
        return mFrags.get(thePosition);
    }

    @Override
    public int getCount() {
        return mFrags.size();
    }

//    public void decrementViewCount() {
//        mCount--;
//    }
//
//    public void incrementViewCount() {
//        mCount++;
//    }

    @Override
    public CharSequence getPageTitle(final int thePosition) {
        switch (thePosition) {
            case 0:
                return "All Contacts";
            case 1:
                return "Requests";
        }
        return "New Request";
    }

}
