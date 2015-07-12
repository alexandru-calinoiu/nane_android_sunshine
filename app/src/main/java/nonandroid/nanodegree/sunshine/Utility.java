package nonandroid.nanodegree.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

import nonandroid.nanodegree.sunshine.data.WeatherContract;

public class Utility {
  // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
  // must change.
  static final int COL_WEATHER_ID = 0;
  static final int COL_WEATHER_DATE = 1;
  static final int COL_WEATHER_DESC = 2;
  static final int COL_WEATHER_MAX_TEMP = 3;
  static final int COL_WEATHER_MIN_TEMP = 4;
  static final int COL_LOCATION_SETTING = 5;
  static final int COL_WEATHER_CONDITION_ID = 6;
  static final int COL_COORD_LAT = 7;
  static final int COL_COORD_LONG = 8;

  public static String getPreferredLocation(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getString(context.getString(R.string.pref_location),
        context.getString(R.string.pref_location_default));
  }

  public static boolean isMetric(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getString(context.getString(R.string.pref_unit),
        context.getString(R.string.pref_unit_default))
        .equals(context.getString(R.string.pref_unit_default));
  }

  static String formatTemperature(double temperature, boolean isMetric) {
    double temp;
    if ( !isMetric ) {
      temp = 9*temperature/5+32;
    } else {
      temp = temperature;
    }
    return String.format("%.0f", temp);
  }

  static String formatDate(long dateInMillis) {
    Date date = new Date(dateInMillis);
    return DateFormat.getDateInstance().format(date);
  }

  static final String[] FORECAST_COLUMNS = {
      // In this case the id needs to be fully qualified with a table name, since
      // the content provider joins the location & weather tables in the background
      // (both have an _id column)
      // On the one hand, that's annoying.  On the other, you can search the weather table
      // using the location set by the user, which is only in the Location table.
      // So the convenience is worth it.
      WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
      WeatherContract.WeatherEntry.COLUMN_DATE,
      WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
      WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
      WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
      WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
      WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
      WeatherContract.LocationEntry.COLUMN_COORD_LAT,
      WeatherContract.LocationEntry.COLUMN_COORD_LONG
  };
}
