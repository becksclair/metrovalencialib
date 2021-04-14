package com.heliasar.metrovalencialib;

import com.heliasar.metrovalencialib.helpers.WebHelper;
import com.heliasar.metrovalencialib.helpers.WebHelper.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;

public class MetroResults {
	
	public static String HTML_HEADER = "<!doctype html>" +
			"<html>" +
			"<head>" +
				"<style type='text/css'>" +
					"table {" +
						"font-size: 24px;" +
					"}" +
					"table tbody tr { line-height: 20px; }" +
					"table tbody td {" +
						"padding-top: 10px;" +
						"padding-bottom: 10px;" +
						"padding-left: 7px;" +
						"padding-right: 7px;" +
					"}" +
				"</style>" +
			"</head><body>";
	public static String HTML_FOOTER = "</body></html>";
	
	public MetroResults(SherlockActivity activity, WebView view, String query) {
		class FetchResults implements Runnable {
			public SherlockActivity activity;
			public WebView webView;
			public String query;
			
			public void run() {
				try {
					Response resp = WebHelper.get(WebHelper.MTV_TIMETABLES_ENDPOINT + query);
					
					if (resp.statusCode == 200) {
						Document doc = Jsoup.parse(resp.resp);

						MetroData.ProcessStops(doc, activity);
						
						// Get the first table
						Elements table = doc.select("table");
						// Remove title row
						table.first().getElementsByTag("tr").first().remove();

						for (Element element : table.select("td")) {
							if (element.text().length() == 2 || element.text().length() == 3) {
								element.remove();
							}
						}
						
						final String html = MetroResults.HTML_HEADER + table.first() + MetroResults.HTML_FOOTER;
						
						activity.runOnUiThread(new Runnable() {
							public void run() {
								webView.loadData(html, "text/html", "utf-8");
							}
						});
												
						table = null;
						doc = null;
					}
					
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
			}
		}
		
		FetchResults thread = new FetchResults();
		thread.activity = activity;
		thread.webView = view;
		thread.query = query;
		new Thread(thread).start();
	}
}
