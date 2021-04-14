package com.heliasar.metrovalencialib.ui;

import com.heliasar.metrovalencialib.MetroValenciaActivity;
import com.heliasar.metrovalencialib.R;
import com.heliasar.metrovalencialib.ResultsActivity;
import com.heliasar.metrovalencialib.data.MetroStop;
import com.heliasar.tools.Utils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.ads.AdView;

public class SearchFragment extends SherlockFragment {
	
	private Spinner departureStops;
	private Spinner arrivalStops;
	private DatePicker datePicker;
	private TimePicker departureTime;
	private TimePicker arrivalTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		inflater.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.view_search, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MetroValenciaActivity activity = (MetroValenciaActivity) getSherlockActivity();
		
		AdView adView = (AdView) view.findViewById(R.id.adView);
		Utils.loadAds(adView);
		
		//--
		// Setup Stop spinners
		departureStops = (Spinner) activity.findViewById(R.id.departure_stops);
		arrivalStops = (Spinner) activity.findViewById(R.id.arrival_stops);
		departureStops.setAdapter(activity.metroData.sdAdapter);
		arrivalStops.setAdapter(activity.metroData.saAdapter);
		//--

		datePicker = (DatePicker) activity.findViewById(R.id.datePicker1);
		setDatePicker();

		departureTime = (TimePicker) activity.findViewById(R.id.departureTime);
		arrivalTime = (TimePicker) activity.findViewById(R.id.arrivalTime);
		departureTime.setCurrentHour(00);
		departureTime.setCurrentMinute(00);
		arrivalTime.setCurrentHour(23);
		arrivalTime.setCurrentMinute(59);
		
		Button searchButton = (Button) activity.findViewById(R.id.searchBtn);
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				performSearch();
			}
		});
	}
	
	private void performSearch() {
		// Build search query
		MetroStop from = (MetroStop) departureStops.getSelectedItem();
		MetroStop to = (MetroStop) arrivalStops.getSelectedItem();
		
		String fromCode = from.getId();
		String toCode = to.getId();
		
		int year = datePicker.getYear();
		int month = datePicker.getMonth();
		int day = datePicker.getDayOfMonth();
		
		int dHour = departureTime.getCurrentHour();
		int dMin = departureTime.getCurrentMinute();
		int aHour = arrivalTime.getCurrentHour();
		int aMin = arrivalTime.getCurrentMinute();
		
		String query = "?res=0&key=0&calcular=1&origen=" +
				fromCode + "&destino=" + toCode + "&fecha=" + day + "/" + month + "/" + "/" + year +
				"&hini=" + dHour + ":" + dMin + "&hfin=" + aHour + ":" + aMin;
		
		Intent i = new Intent();
		i.setClass(getActivity(), ResultsActivity.class);
		i.putExtra("queryStr", query);
		i.putExtra("title", from.getName() + " - " + to.getName());
		i.putExtra("from", fromCode);
		i.putExtra("to", toCode);
		i.putExtra("search", true);
		startActivity(i);
	}
	
	@TargetApi(11)
	private void setDatePicker() {
		if (Utils.isAboveHoneycomb()) {
			datePicker.setCalendarViewShown(false);
		}
	}
}
