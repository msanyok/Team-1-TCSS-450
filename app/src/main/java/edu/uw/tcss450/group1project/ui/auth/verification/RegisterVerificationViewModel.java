package edu.uw.tcss450.group1project.ui.auth.verification;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONObject;

import java.util.Objects;

import edu.uw.tcss450.group1project.model.UserInfoViewModel;

public class RegisterVerificationViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    public RegisterVerificationViewModel(@NonNull final Application theApplication) {
        super(Objects.requireNonNull(theApplication, "theApplication can not be null."));
    }

    public void addResponseObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super JSONObject> theObserver) {
        Objects.requireNonNull(theOwner, "theOwner can not be null");
        Objects.requireNonNull(theObserver, "theObserver can not be null");
        mResponse.observe(theOwner, theObserver);
    }
}
