package net.seip.botwatch;

        import android.os.Bundle;
        import android.preference.ListPreference;
        import android.preference.Preference;
        import android.preference.PreferenceFragment;

public class PrefFragment extends PreferenceFragment {

    ListPreference listPreferenceMAC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        listPreferenceMAC = (ListPreference) findPreference("pref_mac");
        listPreferenceMAC.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                // Tools.showToast(context, "BlaBla");
                getMacText();
                return true;
            }

        });
    }

    public void getMacText() {
        CharSequence currText = listPreferenceMAC.getEntry();
        String currValue = listPreferenceMAC.getValue();
        listPreferenceMAC.setSummary(currText);
    }
}