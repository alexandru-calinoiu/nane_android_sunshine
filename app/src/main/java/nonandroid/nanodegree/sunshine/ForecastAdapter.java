package nonandroid.nanodegree.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

  private static final int VIEW_TYPE_TODAY = 0;
  private static final int VIEW_TYPE_FUTURE_DAY = 1;
  private static final int VIEW_TYPE_COUNT = 2;


  private static final SparseIntArray layout = new SparseIntArray() {{
    put(VIEW_TYPE_TODAY, R.layout.list_item_forecast_today);
    put(VIEW_TYPE_FUTURE_DAY, R.layout.list_item_forecast);
  }};

  private boolean showToday = true;

  public static class ViewHolder {
    public final ImageView iconView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;

    public ViewHolder(View view) {
      iconView = (ImageView) view.findViewById(R.id.list_item_icon);
      dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
      descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
      highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
      lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
    }
  }

  public ForecastAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  public void setShowToday(boolean showToday) {
    this.showToday = showToday;
  }

  @Override
  public int getItemViewType(int position) {
    return (position == 0 && showToday) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
  }

  @Override
  public int getViewTypeCount() {
    return VIEW_TYPE_COUNT;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    int viewType = getItemViewType(cursor.getPosition());

    View view = LayoutInflater.from(context).inflate(layout.get(viewType), parent, false);
    ViewHolder viewHolder = new ViewHolder(view);
    view.setTag(viewHolder);

    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    // our view is pretty simple here --- just a text view
    // we'll keep the UI functional with a simple (and slow!) binding.

    ViewHolder viewHolder = (ViewHolder) view.getTag();

    viewHolder.dateView.setText(Utility.getFriendlyDayString(context, cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));
    viewHolder.descriptionView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));

    final boolean isMetric = SettingsActivity.isMetric(context);

    viewHolder.highTempView.setText(Utility.formatTemperature(context, cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric));
    viewHolder.lowTempView.setText(Utility.formatTemperature(context, cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric));

    int conditionId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
    int imageResourceId = getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY ? Utility.getArtResourceForWeatherCondition(conditionId) : Utility.getIconResourceForWeatherCondition(conditionId);

    viewHolder.iconView.setImageResource(imageResourceId);
  }
}
