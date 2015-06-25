package nonandroid.nanodegree.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

  private ShareActionProvider shareActionProvider;
  private final String forecastHashTag = " #SunshineApp";

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

    String forecast = getActivity().getIntent().getStringExtra(DetailActivity.FORECAST_TEXT);

    TextView textView = (TextView) view.findViewById(R.id.textview_forecast);
    textView.setText(forecast);

    if (shareActionProvider != null) {
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + forecastHashTag);
      shareActionProvider.setShareIntent(shareIntent);
    }

    return view;
  }
}
