package nonandroid.nanodegree.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nonandroid.nanodegree.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

  public static String convertCursorRowToUXFormat(Context context, Cursor cursor) {
    String highAndLow = formatHighLows(
        context,
        cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
        cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

    return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
        " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
        " - " + highAndLow;
  }

  private static String formatHighLows(Context context, double high, double low) {
    boolean isMetric = Utility.isMetric(context);
    return Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
  }

  public ForecastAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  /*
    Remember that these views are reused as needed.
   */
  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
  }

  /*
    This is where we fill-in the views with the contents of the cursor.
   */
  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    // our view is pretty simple here --- just a text view
    // we'll keep the UI functional with a simple (and slow!) binding.

    TextView tv = (TextView)view;
    tv.setText(convertCursorRowToUXFormat(mContext, cursor));
  }
}
