package edu.uw.tcss450.group1project.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class ContactTabNewCountViewModel extends AndroidViewModel {

    public static final String TOTAL_KEY = "TOTAL";

    private MutableLiveData<Map<String, Integer>> mTabCounts;



    public ContactTabNewCountViewModel(@NonNull Application theApplication) {
        super(theApplication);

        mTabCounts = new MutableLiveData<>();
        mTabCounts.setValue(new HashMap<>());
    }

    public void addNotification(final String theTab) {
        final Map<String, Integer> map = mTabCounts.getValue();
        map.put(theTab, map.getOrDefault(theTab, 0) + 1);
        map.put(TOTAL_KEY, map.getOrDefault(TOTAL_KEY, 0) + 1);

        mTabCounts.setValue(map);
    }

    public void removeTabNotifications(final String theTab) {
        final Map<String, Integer> map = mTabCounts.getValue();

        map.put(TOTAL_KEY, map.getOrDefault(TOTAL_KEY, 0) - map.getOrDefault(theTab, 0));
        map.put(theTab, 0);


        mTabCounts.setValue(map);

    }

    public void addContactNotifObserver(@NonNull final LifecycleOwner theOwner,
                                  @NonNull final Observer<? super Map<String, Integer>> theObserver) {
        mTabCounts.observe(theOwner, theObserver);
    }


}
