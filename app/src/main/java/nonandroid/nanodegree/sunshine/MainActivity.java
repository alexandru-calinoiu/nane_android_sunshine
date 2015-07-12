package nonandroid.nanodegree.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

  private final String LOG_TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Log.i(LOG_TAG, "onCreate");
  }

  @Override
  protected void onPause() {
    super.onPause();

    Log.i(LOG_TAG, "onPause");
  }

  @Override
  protected void onStop() {
    super.onStop();

    Log.i(LOG_TAG, "onStop");
  }

  @Override
  protected void onResume() {
    super.onResume();

    Log.i(LOG_TAG, "onResume");
  }

  @Override
  protected void onStart() {
    super.onStart();

    Log.i(LOG_TAG, "onStart");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    Log.i(LOG_TAG, "onDestroy");
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
}
