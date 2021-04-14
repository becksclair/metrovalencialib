package com.heliasar.metrovalencialib.ui;

import java.util.Calendar;

import com.heliasar.UIToolkit.ConfirmDialog;
import com.heliasar.UIToolkit.UIToolkitUtils;
import com.heliasar.UIToolkit.QuickAction.ActionItem;
import com.heliasar.UIToolkit.QuickAction.QuickAction;
import com.heliasar.UIToolkit.QuickAction.QuickAction.OnActionItemClickListener;
import com.heliasar.metrovalencialib.MetroData;
import com.heliasar.metrovalencialib.R;
import com.heliasar.metrovalencialib.ResultsActivity;
import com.heliasar.metrovalencialib.data.Bookmark;
import com.heliasar.metrovalencialib.data.BookmarksData;
import com.heliasar.metrovalencialib.data.BookmarksDbHelper;
import com.heliasar.metrovalencialib.data.BookmarksProvider;
import com.heliasar.tools.Utils;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.ads.AdView;

public class BookmarksFragment extends SherlockListFragment implements
LoaderManager.LoaderCallbacks<Cursor> {

	private static final int BOOKMARKS_LOADER = 0x100;
	
	private BookmarksData bookmarksData;
	private SimpleCursorAdapter bookmarksAdapter;
	
	private QuickAction quickActions;
	private static final int IDA_EDIT = 1;
	private static final int IDA_REMOVE = 2;
	
	private int selectedItem;
	private long selectedId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		inflater.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return inflater.inflate(R.layout.view_bookmarks, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		AdView adView = (AdView) view.findViewById(R.id.adView);
		Utils.loadAds(adView);
		UIToolkitUtils.animateListView(getListView());
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		bookmarksData = new BookmarksData(getActivity().getApplicationContext());

		String[] bindFrom = { BookmarksDbHelper.NAME };
		int[] bindTo = { R.id.title };
		getLoaderManager().initLoader(BOOKMARKS_LOADER, null, this);
		bookmarksAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.view_list_row, null,
				bindFrom, bindTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		bookmarksAdapter.setViewBinder(VIEW_BINDER);
		setListAdapter(bookmarksAdapter);
		
		// Initialize Context menus
		quickActions = new QuickAction(getActivity());
		quickActions.setAnimStyle(QuickAction.ANIM_AUTO);

		ActionItem editItem = new ActionItem(IDA_EDIT, "Edit", getResources().getDrawable(R.drawable.ic_menu_compose_holo_light));
		ActionItem removeItem = new ActionItem(IDA_REMOVE, "Remove", getResources().getDrawable(R.drawable.ic_launcher_trashcan_normal_holo));

		quickActions.addActionItem(editItem);
		quickActions.addActionItem(removeItem);
	}
	
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), BookmarksProvider.CONTENT_URI,
				Bookmark.projection, null, null, BookmarksDbHelper.ROWID + " DESC");
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		bookmarksAdapter.swapCursor(cursor);
	}
	
	public void onLoaderReset(Loader<Cursor> loader) {
		bookmarksAdapter.swapCursor(null);
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		Bookmark b = new Bookmark(bookmarksData.get(id));
		
		String fromCode = b.getDeparture();
		String toCode = b.getArrival();
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		String dHour = "00:00";
		String aHour = "23:59";
		
		String query = "?res=0&key=0&calcular=1&origen=" +
				fromCode + "&destino=" + toCode + "&fecha=" + day + "/" + month + "/" + "/" + year +
				"&hini=" + dHour + "&hfin=" + aHour;
		
		Intent i = new Intent();
		i.setClass(getActivity(), ResultsActivity.class);
		i.putExtra("queryStr", query);
		i.putExtra("title", b.getName());
		i.putExtra("from", fromCode);
		i.putExtra("to", toCode);
		startActivity(i);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
		
		AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
		selectedItem = aMenuInfo.position;
		selectedId = aMenuInfo.id;

		quickActions.show(aMenuInfo.targetView);
		quickActions.setOnActionItemClickListener(quickActionsEventHandler);
	}
	
	private OnActionItemClickListener quickActionsEventHandler = new OnActionItemClickListener() {
		public void onItemClick(QuickAction source, int pos, int actionId) {
			switch (actionId) {
			case IDA_EDIT:
				showEdit(selectedItem, selectedId);
				break;

			case IDA_REMOVE:
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag("confirm");
				if (prev != null) {
					ft.remove(prev);
				}

				ConfirmDialog confirm = ConfirmDialog.newInstance(
						(FragmentActivity) getActivity(), R.string.app_name,
						R.drawable.ic_launcher, R.string.dialog_delete_message,
						confirmOkAction, confirmCancelAction);
				confirm.show(ft, "confirm");
				break;
			}
		}
	};
	
	private DialogInterface.OnClickListener confirmOkAction = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			UIToolkitUtils.animateListViewRow(getSherlockActivity(), getListView(), selectedItem);
			
			new Handler().postDelayed(new Runnable() {
				public void run() {
					bookmarksData.delete(new Bookmark(bookmarksData.get(selectedId)));
					bookmarksAdapter.notifyDataSetChanged();
				}
			}, 500);
		}
	};

	private DialogInterface.OnClickListener confirmCancelAction = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {

		}
	};
	
	private void showEdit(int pos, long index) {
		Bookmark b = new Bookmark(bookmarksData.get(index));
		DialogFragment dlg = NewBookmark.newInstance(new MetroData(getSherlockActivity()), b);
		dlg.show(getFragmentManager(), "newBookmark");
	}
	
	static final ViewBinder VIEW_BINDER = new ViewBinder() {

		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (cursor.getColumnIndex(BookmarksDbHelper.NAME) == columnIndex) {
				String title = cursor.getString(columnIndex);

				if (view.getId() == R.id.title) {
					((TextView) view).setText(title);
				}
				return true;
			}
			return false;
		}
	};
}
