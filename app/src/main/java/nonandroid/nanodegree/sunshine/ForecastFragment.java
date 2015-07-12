package nonandroid.nanodegree.sunshine;

import android.content.Intent;
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

  public static final int FORECAST_CURSOR_LOADER_ID = 42;

  private ForecastAdapter forecastAdapter;

  public ForecastFragment() {
  }

  @Override
  public void onStart() {
    super.onStart();
    updateWeather();
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
    getLoaderManager().initLoader(FORECAST_CURSOR_LOADER_ID, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, container, false);

    forecastAdapter = new ForecastAdapter(getActivity(), null, 0);

    final ListView forecastListView = (ListView) view.findViewById(R.id.listView_forecast);
    forecastListView.setAdapter(forecastAdapter);

    forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);

        if (cursor != null) {
          Intent intent = DetailActivity.getIntent(getActivity(), cursor.getLong(Utility.COL_WEATHER_DATE));
          startActivity(intent);
        }
      }
    });

    return view;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    final String location = Utility.getPreferredLocation(getActivity());
    final String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, System.currentTimeMillis());

    return new CursorLoader(getActivity(), weatherForLocationUri, Utility.FORECAST_COLUMNS, null, null, sortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    forecastAdapter.swapCursor(cursor);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    forecastAdapter.swapCursor(null);
  }

  private void updateWeather() {
    FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
    String location = Utility.getPreferredLocation(getActivity());
    weatherTask.execute(location);
  }
}
