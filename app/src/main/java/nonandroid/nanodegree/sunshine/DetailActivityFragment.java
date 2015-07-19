package nonandroid.nanodegree.sunshine;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  public static final int CURSOR_LOADER_ID = 42;
  public static final String FORECAST_URI_KEY = "FORECAST_URI_KEY";

  private ShareActionProvider shareActionProvider;
  private TextView dayView;
  private TextView dateView;
  private TextView minView;
  private TextView maxView;
  private TextView forecastView;
  private TextView humidityView;
  private TextView windView;
  private TextView pressureView;
  private ImageView iconView;

  public DetailActivityFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.detailfragment, menu);

    MenuItem shareMenuItem = menu.findItem(R.id.action_share);
    shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);

    dayView = (TextView) view.findViewById(R.id.details_day_textview);
    dateView = (TextView) view.findViewById(R.id.details_date_textview);
    minView = (TextView) view.findViewById(R.id.details_min_textview);
    maxView = (TextView) view.findViewById(R.id.details_max_textview);
    forecastView = (TextView) view.findViewById(R.id.details_forecast_textview);
    humidityView = (TextView) view.findViewById(R.id.details_humidity_textview);
    windView = (TextView) view.findViewById(R.id.details_wind_textview);
    pressureView = (TextView) view.findViewById(R.id.details_pressure_textview);
    iconView = (ImageView) view.findViewById(R.id.details_icon_imageview);

    Bundle bundle = new Bundle();
    bundle.putParcelable(FORECAST_URI_KEY, getForecastUri());
    getLoaderManager().initLoader(CURSOR_LOADER_ID, bundle, this);

    return view;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    if (args.getParcelable(FORECAST_URI_KEY) == null) {
      return null;
    }

    return new CursorLoader(getActivity(), (Uri) args.getParcelable(FORECAST_URI_KEY), ForecastFragment.FORECAST_COLUMNS, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (!data.moveToFirst()) {
      return;
    }

    final Context context = getActivity();

    long date = data.getLong(ForecastFragment.COL_WEATHER_DATE);
    dayView.setText(Utility.getDayName(context, date));
    dateView.setText(Utility.getFormattedMonthDay(context, date));

    final boolean isMetric = SettingsActivity.isMetric(context);

    minView.setText(Utility.formatTemperature(context, data.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric));
    maxView.setText(Utility.formatTemperature(context, data.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric));

    final String forecast = data.getString(ForecastFragment.COL_WEATHER_DESC);
    forecastView.setText(forecast);

    humidityView.setText(context.getString(R.string.format_humidity, data.getFloat(ForecastFragment.COL_HUMIDITY)));
    windView.setText(Utility.getFormattedWind(context, data.getFloat(ForecastFragment.COL_WIND), data.getFloat(ForecastFragment.COL_DEGRESS)));
    pressureView.setText(context.getString(R.string.format_pressure, data.getFloat(ForecastFragment.COL_PRESSURE)));

    iconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));

    setSharedIntent(forecast);
  }

  private void setSharedIntent(String forecast) {
    if (shareActionProvider != null) {
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      String forecastHashTag = " #SunshineApp";
      shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + forecastHashTag);
      shareActionProvider.setShareIntent(shareIntent);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    // ignore
  }

  private Uri getForecastUri() {
    if (getArguments() == null) {
      return null;
    }
    return getArguments().getParcelable(FORECAST_URI_KEY);
  }
}
