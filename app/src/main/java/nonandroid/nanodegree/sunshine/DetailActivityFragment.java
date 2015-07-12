package nonandroid.nanodegree.sunshine;

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
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  public static final int CURSOR_LOADER_ID = 42;
  public static final String FORECAST_URI_KEY = "FORECAST_URI_KEY";

  private ShareActionProvider shareActionProvider;
  private TextView textView;

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

    textView = (TextView) view.findViewById(R.id.textview_forecast);


    Bundle bundle = new Bundle();
    bundle.putString(FORECAST_URI_KEY, getActivity().getIntent().getDataString());
    getLoaderManager().initLoader(CURSOR_LOADER_ID, bundle, this);

    return view;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), Uri.parse(args.getString(FORECAST_URI_KEY)), ForecastFragment.FORECAST_COLUMNS, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (!data.moveToFirst()) {
      return;
    }

    String forecast = ForecastAdapter.convertCursorRowToUXFormat(getActivity(), data);
    textView.setText(forecast);

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
    textView.setText("");
  }
}
