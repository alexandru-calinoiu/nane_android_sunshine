package nonandroid.nanodegree.sunshine;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import nonandroid.nanodegree.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

  private ForecastAdapter forecastAdapter;

  public ForecastFragment() {
  }

  @Override
  public void onStart() {
    super.onStart();
    fetchForecast();
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
      fetchForecast();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, container, false);


    final String locationSetting = Utility.getPreferredLocation(getActivity());
    final String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());
    Cursor cursor = getActivity().getContentResolver().query(weatherForLocationUri,
        null, null, null, sortOrder);

    forecastAdapter = new ForecastAdapter(getActivity(), cursor, 0);

    final ListView forecastListView = (ListView) view.findViewById(R.id.listView_forecast);
    forecastListView.setAdapter(forecastAdapter);

    return view;
  }

  private void fetchForecast() {
    new FetchWeatherTask(getActivity()).execute(getPostalCode());
  }

  private String getPostalCode() {
    return SettingsActivity.getLocation(getActivity());
  }
}
