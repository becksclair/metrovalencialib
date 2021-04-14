package com.heliasar.metrovalencialib.ui;

import java.util.List;

import com.heliasar.metrovalencialib.MetroData;
import com.heliasar.metrovalencialib.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

public class PageAdapter extends FragmentPagerAdapter implements
		ActionBar.TabListener, ViewPager.OnPageChangeListener {

	private static final int NUM_ITEMS = 2;
	public static final int NOTES_PAGE = 0;
	public static final int PHOTOS_PAGE = 1;

	private ActionBar ab;
	private ViewPager pager;
	public boolean dualPane;

	public Fragment bookmarksFragment;
	public Fragment searchFragment;
	private MetroData metroData;

	public PageAdapter(FragmentManager fm, ActionBar bar, ViewPager viewPager, List<Fragment> fragments, MetroData data) {
		super(fm);

		metroData = data;
		ab = bar;
		pager = viewPager;

		bookmarksFragment = fragments.get(0);
		searchFragment = fragments.get(1);

		addTabs();

		pager.setAdapter(this);
		pager.setOnPageChangeListener(this);
	}

	@Override
	public Fragment getItem(int position) {
		if (position == NOTES_PAGE){
			return bookmarksFragment;
		}

		else if (position == PHOTOS_PAGE) {
			return searchFragment;
		}
		else
			return null;
	}

	@Override
	public int getCount() {
		return NUM_ITEMS;
	}

	public void onPageScrollStateChanged(int position) {
	}

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	public void onPageSelected(int position) {
		ab.setSelectedNavigationItem(position);
	}

	public void addTabs() {
		ActionBar.Tab bookmarksTab = ab.newTab()
								   .setText(R.string.bookmarks_tab)
								   .setTag(Integer.valueOf(0)).setTabListener(this);

		ActionBar.Tab searchTab = ab.newTab()
									.setText(R.string.search_tab)
									.setTag(Integer.valueOf(1)).setTabListener(this);

		ab.addTab(bookmarksTab, true);
		ab.addTab(searchTab);
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getPosition() == 1 && !metroData.isCacheLoaded()) {
			metroData.restoreStopsCache();
		}
		pager.setCurrentItem(tab.getPosition());
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

}
