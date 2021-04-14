package com.heliasar.metrovalencialib;

import com.heliasar.metrovalencialib.data.Bookmark;
import com.heliasar.metrovalencialib.data.BookmarksData;
import com.heliasar.tools.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.Window;
import com.google.ads.AdView;

public class ResultsActivity extends SherlockActivity {
	
	private boolean search;
	private String name;
	private String from;
	private String to;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.view_results);
        
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setSubtitle(R.string.loading);
        
        AdView adView = (AdView) findViewById(R.id.adView);
        WebView view = (WebView) findViewById(R.id.resultsWebView);
        String query = getIntent().getStringExtra("queryStr");
        search = getIntent().getBooleanExtra("search", false);
        
        name = getIntent().getStringExtra("title");
        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        
        final String subtitle = name;
        
        final SherlockActivity activity = this;
        
        view.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100)
				activity.setSupportProgressBarIndeterminateVisibility(false);
				activity.getSupportActionBar().setSubtitle(subtitle);
			}
        });
        
        Utils.loadAds(adView);
        new MetroResults(this, view, query);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (search) {
			MenuItem saveMenu = menu.add(R.string.bookmark_save);
			saveMenu.setIcon(R.drawable.ic_menu_add_field_holo_light);
			saveMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			saveMenu.setOnMenuItemClickListener(saveMenuAction);
		}
		return super.onCreateOptionsMenu(menu);
	}

	private OnMenuItemClickListener saveMenuAction = new OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {
			BookmarksData bookmarks = new BookmarksData(getApplicationContext());
			Bookmark b = new Bookmark(name, from, to);
			bookmarks.create(b);
			Toast.makeText(getApplicationContext(), R.string.search_saved, Toast.LENGTH_LONG).show();
			return true;
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MetroValenciaActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
