package com.heliasar.metrovalencialib;

import java.util.ArrayList;

import com.heliasar.metrovalencialib.data.MetroStop;
import com.heliasar.metrovalencialib.data.MetroStopData;
import com.heliasar.metrovalencialib.helpers.WebHelper;
import com.heliasar.metrovalencialib.helpers.WebHelper.Response;
import com.heliasar.tools.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MetroData {
	private MetroStopData metroStops;
	private SherlockFragmentActivity activity;
	private boolean cacheLoaded;
	
	public ArrayList<MetroStop> Stops;
	public ArrayAdapter<MetroStop> sdAdapter;
	public ArrayAdapter<MetroStop> saAdapter;
	public ArrayAdapter<MetroStop> ndAdapter;
	public ArrayAdapter<MetroStop> naAdapter;
	
	public MetroData(SherlockFragmentActivity activity) {
		this.activity = activity;
		Context abContext;
		if (!Utils.isAboveHoneycomb()) {
			abContext = activity.getSupportActionBar().getThemedContext();
		} else {
			abContext = activity;
		}
		
		Stops = new ArrayList<MetroStop>();
		
		sdAdapter = new ArrayAdapter<MetroStop>(abContext, android.R.layout.simple_spinner_item, Stops);
		saAdapter = new ArrayAdapter<MetroStop>(abContext, android.R.layout.simple_spinner_item, Stops);
		ndAdapter = new ArrayAdapter<MetroStop>(abContext, android.R.layout.simple_spinner_item, Stops);
		naAdapter = new ArrayAdapter<MetroStop>(abContext, android.R.layout.simple_spinner_item, Stops);
		
		sdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		saAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ndAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		naAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		metroStops = new MetroStopData(activity.getApplicationContext());
	}
	
	public void restoreStopsCache() {
		activity.setSupportProgressBarIndeterminateVisibility(true);
		
		if (metroStops.getCount() == 0) {
			Utils.l("No stops cached, caching now");
			fetchStops();
		} else {
			Utils.l("Stops in cache, restoring now");
			
			class RestoreThread implements Runnable {
				public SherlockFragmentActivity activity;
				public MetroStopData metroData;
				
				public void run() {
					final ArrayList<MetroStop> stops = metroData.getIndex();
					
					activity.runOnUiThread(new Runnable() {
						public void run() {
							for (MetroStop metroStop : stops) {
								sdAdapter.add(metroStop);
							}
							
							sdAdapter.notifyDataSetChanged();
							saAdapter.notifyDataSetChanged();
							ndAdapter.notifyDataSetChanged();
							naAdapter.notifyDataSetChanged();
							
							setCacheLoaded(true);
							activity.setSupportProgressBarIndeterminateVisibility(false);
						}
					});
				}
			}
			
			RestoreThread restoreThread = new RestoreThread();
			restoreThread.activity = activity;
			restoreThread.metroData = metroStops;
			
			new Thread(restoreThread).start(); 
		}
	}
	
	@SuppressWarnings("unchecked")
	private void fetchStops() {
		activity.getSupportActionBar().setSubtitle(R.string.loading_stops);
		new RetreiveStopsTask().execute(Stops);
	}
	
	public boolean isCacheLoaded() {
		return cacheLoaded;
	}

	public void setCacheLoaded(boolean cacheLoaded) {
		this.cacheLoaded = cacheLoaded;
	}

	protected class RetreiveStopsTask extends AsyncTask<ArrayList<MetroStop>, Void, Void> {
		@Override
	    protected Void doInBackground(ArrayList<MetroStop>... params) {
	        try {
	        	ArrayList<MetroStop> stops = params[0];
				Response resp = WebHelper.get(WebHelper.MTV_TIMETABLES_ENDPOINT);
				
				if (resp.statusCode == 200) {
					Document doc = Jsoup.parse(resp.resp);
					
					// Get stop options
					Elements stopOptions = doc.select("select[name=origen] > option");
					stopOptions.remove(0);
					for (Element element : stopOptions) {
						stops.add(new MetroStop(element.attr("value"), element.text()));
					}
					
					stopOptions = null;
					doc = null;
				}
				
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
			return null;
	    }

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			sdAdapter.notifyDataSetChanged();
			saAdapter.notifyDataSetChanged();
			ndAdapter.notifyDataSetChanged();
			naAdapter.notifyDataSetChanged();
			
			metroStops.addIndex(Stops);
			setCacheLoaded(true);
			activity.setSupportProgressBarIndeterminateVisibility(false);
			activity.getSupportActionBar().setSubtitle("");
		}
	 }
	
	public static void ProcessStops(Document doc, SherlockActivity activity) {
		ArrayList<MetroStop> stops = new ArrayList<MetroStop>();
		
		Elements stopOptions = doc.select("select[name=origen] > option");
		stopOptions.remove(0);
		for (Element element : stopOptions) {
			stops.add(new MetroStop(element.attr("value"), element.text()));
		}
		
		MetroStopData stopsData = new MetroStopData(activity.getApplicationContext());
		stopsData.deleteAll();
		stopsData.addIndex(stops);
	}
}
