package com.heliasar.metrovalencialib;

import java.util.List;
import java.util.Vector;

import com.heliasar.UIToolkit.ImageViewer.ViewerActivity;
import com.heliasar.metrovalencialib.ui.BookmarksFragment;
import com.heliasar.metrovalencialib.ui.NewBookmark;
import com.heliasar.metrovalencialib.ui.PageAdapter;
import com.heliasar.metrovalencialib.ui.SearchFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.Window;

public class MetroValenciaActivity extends SherlockFragmentActivity {
	
	private ActionBar ab;
	private ViewPager pager;
	@SuppressWarnings("unused")
	private PageAdapter pagerAdapter;
	
	public MetroData metroData;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.view_phone);
        
        ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setDisplayShowTitleEnabled(true);
		ab.setTitle(getResources().getString(R.string.app_name));
		setSupportProgressBarIndeterminateVisibility(false);
        
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(BookmarksFragment.instantiate(this, BookmarksFragment.class.getName()));
		fragments.add(SearchFragment.instantiate(this, SearchFragment.class.getName()));

		metroData = new MetroData(this);
		
		pager = (ViewPager) findViewById(R.id.contentFrame);
		pagerAdapter = new PageAdapter(getSupportFragmentManager(), ab, pager, fragments, metroData);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem newMenu = menu.add(R.string.new_bookmark);
		newMenu.setIcon(R.drawable.ic_menu_add_field_holo_light);
		newMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		newMenu.setOnMenuItemClickListener(newMenuAction);
		
		MenuItem mapMenu = menu.add(R.string.map_tab);
		mapMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mapMenu.setOnMenuItemClickListener(mapMenuAction);

		return super.onCreateOptionsMenu(menu);
	}
	
	private OnMenuItemClickListener newMenuAction = new OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {
			if (!metroData.isCacheLoaded()) {
				metroData.restoreStopsCache();
			}
			
			DialogFragment dlg = NewBookmark.newInstance(metroData, null);
			dlg.show(getSupportFragmentManager(), "newBookmark");
			return true;
		}
	};

	private OnMenuItemClickListener mapMenuAction = new OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {
			Intent i = new Intent(getApplicationContext(), ViewerActivity.class);
			i.setClass(getApplicationContext(), ViewerActivity.class);
			i.putExtra("asset", "images/map.jpg");
			startActivity(i);
			return true;
		}
	};
	
}