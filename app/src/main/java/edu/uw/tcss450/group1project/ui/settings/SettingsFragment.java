/*
 * TCSS 450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentSettingsBinding;

/**
 * SettingsFragment is a fragment for settings display including theme changing.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class SettingsFragment extends Fragment {

    /**
     * Required empty constructor. Has no functionality.
     */
    public SettingsFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_settings, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        FragmentSettingsBinding binding = FragmentSettingsBinding.bind(getView());
        binding.buttonTheme1.setOnClickListener(button -> {
            ((MainActivity) getActivity()).changeTheme(R.style.AppTheme1);
            getActivity().recreate();
        });
        binding.buttonTheme2.setOnClickListener(button -> {
            ((MainActivity) getActivity()).changeTheme(R.style.AppTheme2);
            getActivity().recreate();
        });
        binding.buttonTheme3.setOnClickListener(button -> {
            ((MainActivity) getActivity()).changeTheme(R.style.AppTheme3);
            getActivity().recreate();
        });
        binding.buttonTheme4.setOnClickListener(button -> {
            ((MainActivity) getActivity()).changeTheme(R.style.AppTheme4);
            getActivity().recreate();
        });
        binding.buttonResetPasswordSetting.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                SettingsFragmentDirections.actionNavigationSettingsToNavigationPasswordReset())
        );
    }
}
