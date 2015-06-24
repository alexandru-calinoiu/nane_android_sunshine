package nonandroid.nanodegree.sunshine;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

  public DetailActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);

    String forecast = getActivity().getIntent().getStringExtra(DetailActivity.FORECAST_TEXT);

    TextView textView = (TextView) view.findViewById(R.id.textview_forecast);
    textView.setText(forecast);

    return view;
  }
}
