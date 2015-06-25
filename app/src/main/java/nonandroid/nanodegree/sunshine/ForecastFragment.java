package nonandroid.nanodegree.sunshine;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

  private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
  private final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
  private final String QUERY_PARAM = "q";
  private final String FORMAT_PARAM = "mode";
  private final String UNITS_PARAM = "units";
  private final String DAYS_PARAM = "cnt";
  private final int NUMBER_OF_DAYS = 7;

  private ArrayAdapter<String> forecastAdapter;

  public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    @Override
    protected String[] doInBackground(String... postalCodes) {
      if (postalCodes.length == 0) {
        return null;
      }

      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;
      Uri baseUri = Uri.parse(FORECAST_BASE_URL)
          .buildUpon()
          .appendQueryParameter(FORMAT_PARAM, "json")
          .appendQueryParameter(UNITS_PARAM, "metric")
          .appendQueryParameter(DAYS_PARAM, Integer.valueOf(NUMBER_OF_DAYS).toString())
          .build();

      // Will contain the raw JSON response as a string.
      String forecastJsonStr = null;

      try {

        URL url = new URL(baseUri.buildUpon().appendQueryParameter(QUERY_PARAM, postalCodes[0]).build().toString());

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        if (inputStream == null) {
          return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line).append("\n");
        }

        if (buffer.length() == 0) {
          return null;
        }

        forecastJsonStr = buffer.toString();

      } catch (IOException e) {
        Log.e(LOG_TAG, "Error ", e);
        return null;
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e("PlaceholderFragment", "Error closing stream", e);
          }
        }
      }

      try {
        return getWeatherDataFromJson(forecastJsonStr, NUMBER_OF_DAYS);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      return null;
    }

    private String getReadableDateString(long time) {
      // Because the API returns a unix timestamp (measured in seconds),
      // it must be converted to milliseconds in order to be converted to valid date.
      SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd", Locale.US);
      return shortenedDateFormat.format(time);
    }

    private String formatHighLows(double high, double low) {
      // For presentation, assume the user doesn't care about tenths of a degree.
      long roundedHigh = Math.round(high);
      long roundedLow = Math.round(low);

      return roundedHigh + "/" + roundedLow;
    }

    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
        throws JSONException {

      // These are the names of the JSON objects that need to be extracted.
      final String OWM_LIST = "list";
      final String OWM_WEATHER = "weather";
      final String OWM_TEMPERATURE = "temp";
      final String OWM_MAX = "max";
      final String OWM_MIN = "min";
      final String OWM_DESCRIPTION = "main";

      JSONObject forecastJson = new JSONObject(forecastJsonStr);
      JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

      // OWM returns daily forecasts based upon the local time of the city that is being
      // asked for, which means that we need to know the GMT offset to translate this data
      // properly.

      // Since this data is also sent in-order and the first day is always the
      // current day, we're going to take advantage of that to get a nice
      // normalized UTC date for all of our weather.

      Time dayTime = new Time();
      dayTime.setToNow();
      // we start at the day returned by local time. Otherwise this is a mess.
      int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

      // now we work exclusively in UTC
      dayTime = new Time();

      String[] resultStrs = new String[numDays];
      for (int i = 0; i < weatherArray.length(); i++) {
        // For now, using the format "Day, description, hi/low"
        String day;
        String description;
        String highAndLow;

        // Get the JSON object representing the day
        JSONObject dayForecast = weatherArray.getJSONObject(i);

        // The date/time is returned as a long.  We need to convert that
        // into something human-readable, since most people won't read "1400356800" as
        // "this saturday".
        long dateTime;
        // Cheating to convert this to UTC time, which is what we want anyhow
        dateTime = dayTime.setJulianDay(julianStartDay + i);
        day = getReadableDateString(dateTime);

        // description is in a child array called "weather", which is 1 element long.
        JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
        description = weatherObject.getString(OWM_DESCRIPTION);

        // Temperatures are in a child object called "temp".  Try not to name variables
        // "temp" when working with temperature.  It confuses everybody.
        JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
        double high = convertTemperature(temperatureObject.getDouble(OWM_MAX));
        double low = convertTemperature(temperatureObject.getDouble(OWM_MIN));

        highAndLow = formatHighLows(high, low);
        resultStrs[i] = day + " - " + description + " - " + highAndLow;
      }

      return resultStrs;
    }

    @Override
    protected void onPostExecute(String[] forecasts) {
      super.onPostExecute(forecasts);

      if (forecasts == null) {
        return;
      }

      forecastAdapter.clear();
      for (String forecast : forecasts) {
        forecastAdapter.add(forecast);
      }

      forecastAdapter.notifyDataSetChanged();
    }
  }

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


    ArrayList<String> forecasts = new ArrayList<>();
    forecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, forecasts);
    forecastAdapter.setNotifyOnChange(false);

    final ListView forecastListView = (ListView) view.findViewById(R.id.listView_forecast);
    forecastListView.setAdapter(forecastAdapter);

    forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String forecast = forecastAdapter.getItem(i);
        startActivity(DetailActivity.getIntent(getActivity(), forecast));
      }
    });

    return view;
  }

  private void fetchForecast() {
    new FetchWeatherTask().execute(getPostalCode());
  }

  private String getPostalCode() {
    SharedPreferences sharedPref = getSharedPreferences();
    return sharedPref.getString(getString(R.string.pref_location), getString(R.string.pref_location_default));
  }

  private double convertTemperature(double celsius) {
    String metric = getString(R.string.pref_unit_default);
    String unit = getSharedPreferences().getString(getString(R.string.pref_unit), metric);

    if (unit.equals(metric)) {
      return celsius;
    }
    else {
      return ((celsius * 9) / 5) + 32;
    }
  }

  private SharedPreferences getSharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(getActivity());
  }
}
