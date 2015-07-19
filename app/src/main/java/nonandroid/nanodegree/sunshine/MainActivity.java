package nonandroid.nanodegree.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

  private static final String DETAILS_TAG = "DETAILS";

  private boolean twoPanel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (findViewById(R.id.weather_detail_container) != null) {
      twoPanel = true;

      if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
            .add(R.id.weather_detail_container, new DetailActivityFragment(), DETAILS_TAG)
            .commit();
      } else {
        twoPanel = false;

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
          supportActionBar.setElevation(0);
        }
      }
    }

    ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
    forecastFragment.setSinglePane(!twoPanel);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    twoPanel = findViewById(R.id.weather_detail_container) != null;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      startActivity(SettingsActivity.getIntent(this));
      return true;
    } else if (id == R.id.action_show_location) {
      Uri mapsUri = Uri.parse("geo:0,0?")
          .buildUpon()
          .appendQueryParameter("q", SettingsActivity.getLocation(this))
          .build();

      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(mapsUri);
      if (intent.resolveActivity(getPackageManager()) != null) {
        startActivity(intent);
      }
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onItemSelected(final Uri forecastUri) {
    if (twoPanel) {
      DetailActivityFragment fragment = new DetailActivityFragment();
      Bundle bundle = new Bundle();
      bundle.putParcelable(DetailActivityFragment.FORECAST_URI_KEY, forecastUri);
      fragment.setArguments(bundle);

      getSupportFragmentManager().beginTransaction()
          .replace(R.id.weather_detail_container, fragment, DETAILS_TAG)
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .commit();

    } else {
      Intent intent = DetailActivity.getIntent(this, forecastUri);
      startActivity(intent);
    }
  }
}
