package com.heliasar.metrovalencialib.ui;

import java.util.ArrayList;

import com.heliasar.metrovalencialib.MetroData;
import com.heliasar.metrovalencialib.R;
import com.heliasar.metrovalencialib.data.Bookmark;
import com.heliasar.metrovalencialib.data.BookmarksData;
import com.heliasar.metrovalencialib.data.MetroStop;
import com.heliasar.metrovalencialib.data.MetroStopData;
import com.heliasar.tools.Utils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class NewBookmark extends SherlockDialogFragment {

	private EditText nameField;
	private Spinner nDeparture;
	private Spinner nArrival;
	
	private MetroData metroData;
	private MetroStopData md;
	private Bookmark current;
	
	public static NewBookmark newInstance(MetroData metroData, Bookmark b) {
		NewBookmark newBookmarkDialog = new NewBookmark();
		newBookmarkDialog.metroData = metroData;
		newBookmarkDialog.current = b;
		return newBookmarkDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder;
		
		if (!Utils.isAboveHoneycomb()) {
			builder = new AlertDialog.Builder(getSherlockActivity().getSupportActionBar().getThemedContext());
		} else {
			builder = new AlertDialog.Builder(getActivity());
		}
		
		View v = getSherlockActivity().getLayoutInflater().inflate(R.layout.view_new_bookmark, null);

		nameField = (EditText) v.findViewById(R.id.nameField);
		
		nDeparture = (Spinner) v.findViewById(R.id.nDeparture);
		nArrival = (Spinner) v.findViewById(R.id.nArrival);
		nDeparture.setAdapter(metroData.ndAdapter);
		nArrival.setAdapter(metroData.naAdapter);
		
		if (current != null) {
			if (!metroData.isCacheLoaded()) {
				metroData.restoreStopsCache();
			}
			
			md = new MetroStopData(getActivity().getApplicationContext());
			MetroStop dStop = new MetroStop(md.getByCode(current.getDeparture()));
			MetroStop aStop = new MetroStop(md.getByCode(current.getArrival()));
			nameField.setText(current.getName());
			
			ArrayList<MetroStop> stops = md.getIndex();
			int i = 0;
			for (MetroStop metroStop : stops) {
				if (metroStop.getName().contentEquals(dStop.getName())) {
					nDeparture.setSelection(i);
				}
				if (metroStop.getName().contentEquals(aStop.getName())) {
					nArrival.setSelection(i);
				}
				i++;
			}
		}
		
		builder.setInverseBackgroundForced(true);
		builder.setView(v);
		builder.setTitle(R.string.bookmark_title);
		
		builder.setPositiveButton(R.string.bookmark_save, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				BookmarksData bData = new BookmarksData(getActivity().getApplicationContext());
				
				if (current != null) {
					current.setName(nameField.getText().toString());
					current.setDeparture(((MetroStop) nDeparture.getSelectedItem()).getId());
					current.setArrival(((MetroStop) nArrival.getSelectedItem()).getId());
					
					bData.update(current);
					dialog.dismiss();
					return;
				}
				
				String name = nameField.getText().toString();
				String fromCode = ((MetroStop) nDeparture.getSelectedItem()).getId();
				String toCode = ((MetroStop) nArrival.getSelectedItem()).getId();
				
				Bookmark b = new Bookmark(name, fromCode, toCode);
				bData.create(b);
				
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton(R.string.bookmark_cancel, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}

}
