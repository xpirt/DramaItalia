package and.conachegroup.dramaitalia.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import and.conachegroup.dramaitalia.BuildConfig;
import and.conachegroup.dramaitalia.R;
import and.conachegroup.dramaitalia.utils.Constants;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, Constants {

    private SharedPreferences mSharedPreferences;
    private SwitchPreference mLoadDocumentsAtBoot;
    private SwitchPreference mShowAnimations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        addPreferencesFromResource(R.xml.pref_settings);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mLoadDocumentsAtBoot = (SwitchPreference)
                preferenceScreen.findPreference(getString(R.string.key_load_documents_at_boot));
        mLoadDocumentsAtBoot.setDefaultValue(true);

        mShowAnimations = (SwitchPreference)
                preferenceScreen.findPreference(getString(R.string.key_show_animations));
        mShowAnimations.setDefaultValue(true);

        // website
        Preference website = preferenceScreen.findPreference(getString(R.string.key_website));
        website.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(DRAMAITALIA_URL));
                startActivity(browserIntent);
                return false;
            }
        });

        // email
        Preference email = preferenceScreen.findPreference(getString(R.string.key_email));
        email.setSummary(DRAMAITALIA_EMAIL);
        email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/email");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ DRAMAITALIA_EMAIL });
                startActivity(Intent.createChooser(emailIntent,
                        getString(R.string.title_email)));
                return false;
            }
        });

        // author
        Preference author = preferenceScreen.findPreference(getString(R.string.key_author));
        author.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(AUTHOR_LINKEDIN_URL));
                startActivity(browserIntent);
                return false;
            }
        });

        // version
        Preference version = preferenceScreen.findPreference(getString(R.string.key_version));
        version.setSummary(BuildConfig.VERSION_NAME);

        // feedback
        Preference feedback = preferenceScreen.findPreference(getString(R.string.key_feedback));
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final ViewGroup nullParent = null;

                final View dialogView = inflater.inflate(R.layout.send_feedback_dialog, nullParent);
                final EditText text = dialogView.findViewById(R.id.text);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle(getString(R.string.feedback));
                dialogBuilder.setMessage(getString(R.string.feedback_summary));
                dialogBuilder.setPositiveButton(getString(R.string.send),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("text/email");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{
                                        getString(R.string.developer_email)});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                        getString(R.string.feedback_subject));
                                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_intro) +
                                        text.getText().toString());
                                startActivity(Intent.createChooser(emailIntent,
                                        getString(R.string.feedback_send)));
                            }
                        });
                dialogBuilder.setNegativeButton(getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // cancel
                            }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(mLoadDocumentsAtBoot.getKey())) {
            boolean loadDocumentsAtBoot = sharedPreferences.getBoolean(key, true);
            mSharedPreferences.edit().putBoolean(key, loadDocumentsAtBoot).apply();
        } else if (key.equals(mShowAnimations.getKey())) {
            boolean showAnimations = sharedPreferences.getBoolean(key, true);
            mSharedPreferences.edit().putBoolean(key, showAnimations).apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
