package nonandroid.nanodegree.sunshine;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import nonandroid.nanodegree.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final int CURSOR_LOADER_ID = 42;

  private static final String POSITION = "POSITION";

  private static String lastLocation = "";

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
      WeatherContract.LocationEntry.COLUMN_COORD_LONG,
      WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
      WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
      WeatherContract.WeatherEntry.COLUMN_DEGREES,
      WeatherContract.WeatherEntry.COLUMN_PRESSURE,
  };

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
  static final int COL_HUMIDITY = 9;
  static final int COL_WIND = 10;
  static final int COL_DEGRESS = 11;
  static final int COL_PRESSURE = 12;

  private ListView forecastListView;
  private ForecastAdapter forecastAdapter;
  private int lastPosition;

  public interface Callback {
    void onItemSelected(Uri foreCastUri);
  }

  @Override
  public void onStart() {
    super.onStart();

    String currentLocation = Utility.getPreferredLocation(getActivity());

    if (!lastLocation.equals(currentLocation)) {
      lastLocation = currentLocation;
      updateWeather();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(POSITION, lastPosition);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.forecastfragment, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_refresh) {
      updateWeather();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, container, false);

    if (savedInstanceState != null) {
      lastPosition = savedInstanceState.getInt(POSITION, 0);
    }

    forecastAdapter = new ForecastAdapter(getActivity(), null, 0);

    forecastListView = (ListView) view.findViewById(R.id.listView_forecast);
    forecastListView.setAdapter(forecastAdapter);

    forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        lastPosition = position;

        if (cursor != null) {
          String locationSetting = Utility.getPreferredLocation(getActivity());
          long date = cursor.getLong(COL_WEATHER_DATE);
          Uri forecastUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, date);

          ((Callback) getActivity()).onItemSelected(forecastUri);
        }
      }
    });

    return view;
  }

  public void setSinglePane(boolean singlePane) {
    forecastAdapter.setShowToday(singlePane);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    final String location = Utility.getPreferredLocation(getActivity());
    final String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, System.currentTimeMillis());

    return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    forecastAdapter.swapCursor(cursor);
    forecastListView.smoothScrollToPosition(lastPosition);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    forecastAdapter.swapCursor(null);
  }

  private void updateWeather() {
    FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
    weatherTask.execute(lastLocation);
  }
}
