package nonandroid.nanodegree.sunshine;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
    implements Preference.OnPreferenceChangeListener {

  public static String getLocation(Context context) {
    return getSharedPreferences(context).getString(context.getString(R.string.pref_location), context.getString(R.string.pref_location_default));
  }

  public static String getUnit(Context context) {
    return getSharedPreferences(context).getString(context.getString(R.string.pref_unit), context.getString(R.string.pref_unit_default));
  }

  public static Boolean isMetric(Context context) {
    String metric = context.getString(R.string.pref_unit_default);
    return getSharedPreferences(context).getString(context.getString(R.string.pref_unit), context.getString(R.string.pref_unit_default)).equals(metric);
  }

  public static SharedPreferences getSharedPreferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static Intent getIntent(Context context) {
    return new Intent(context, SettingsActivity.class);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Add 'general' preferences, defined in the XML file
    addPreferencesFromResource(R.xml.pref_general);

    // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
    // updated when the preference changes.
    bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location)));
    bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_unit)));
  }

  /**
   * Attaches a listener so the summary is always updated with the preference value.
   * Also fires the listener once, to initialize the summary (so it shows up before the value
   * is changed.)
   */
  private void bindPreferenceSummaryToValue(Preference preference) {
    // Set the listener to watch for value changes.
    preference.setOnPreferenceChangeListener(this);

    // Trigger the listener immediately with the preference's
    // current value.
    onPreferenceChange(preference,
        PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
  }

  @Override
  public boolean onPreferenceChange(Preference preference, Object value) {
    String stringValue = value.toString();

    if (preference instanceof ListPreference) {
      // For list preferences, look up the correct display value in
      // the preference's 'entries' list (since they have separate labels/values).
      ListPreference listPreference = (ListPreference) preference;
      int prefIndex = listPreference.findIndexOfValue(stringValue);
      if (prefIndex >= 0) {
        preference.setSummary(listPreference.getEntries()[prefIndex]);
      }
    } else {
      // For other preferences, set the summary to the value's simple string representation.
      preference.setSummary(stringValue);
    }
    return true;
  }
}
