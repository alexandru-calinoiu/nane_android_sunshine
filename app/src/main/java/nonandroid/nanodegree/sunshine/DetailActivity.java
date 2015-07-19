package nonandroid.nanodegree.sunshine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import nonandroid.nanodegree.sunshine.data.WeatherContract;

public class DetailActivity extends AppCompatActivity {
  public static Intent getIntent(Context context, Long date) {
    String locationSetting = Utility.getPreferredLocation(context);
    Uri forecastUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, date);

    Intent intent = new Intent(context, DetailActivity.class);
    intent.setData(forecastUri);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    if (savedInstanceState == null) {
      DetailActivityFragment.addToLayout(getSupportFragmentManager());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.detail, menu);
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
    }

    return super.onOptionsItemSelected(item);
  }
}
