package com.heliasar.metrovalencialib.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MetroStopData {
	private MetroStopDbHelper dbh;
	private Context context;

	public MetroStopData(Context context) {
		this.context = context;
		dbh = new MetroStopDbHelper(context);
	}

	public long getCount() {
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.beginTransaction();
		Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + MetroStopDbHelper.DB_TABLE, null);
		if (c == null) {
			db.close();
			return 0;
		}

		c.moveToFirst();
		String[] cols = c.getColumnNames();
		final long count = c.getLong(c.getColumnIndexOrThrow(cols[0]));
		c.close();
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return count;
	}

	public ArrayList<MetroStop> getIndex() {
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.beginTransaction();
		ArrayList<MetroStop> stops = new ArrayList<MetroStop>();

		Cursor c = db.query(MetroStopDbHelper.DB_TABLE, MetroStopDbHelper.columns, null, null, null, null, MetroStopDbHelper.ROWID + " ASC");
		if (c == null) {
			db.close();
			return null;
		}

		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			stops.add(new MetroStop(c));
			c.moveToNext();
		}
		c.close();
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return stops;
	}

	public void create(MetroStop stop) {
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.beginTransaction();
		db.insert(MetroStopDbHelper.DB_TABLE, null, stop.getContentValues());
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	public void addIndex(ArrayList<MetroStop> stops) {
		for (MetroStop metroStop : stops) {
			create(metroStop);
		}
	}

	public Cursor getAll() {
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.beginTransaction();
		Cursor c = db.query(MetroStopDbHelper.DB_TABLE, MetroStopDbHelper.columns,
				null, null, null, null, MetroStopDbHelper.ROWID + " DESC");
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return c;
	}

	public Cursor get(long stopId) {
		Uri stop = BookmarksProvider.CONTENT_URI;
		Cursor c = context.getContentResolver().query(stop, MetroStop.projection,
				MetroStopDbHelper.ROWID + "=" + stopId, null, null);

		if (c != null)
			c.moveToFirst();
		return c;
	}
	
	public Cursor getByCode(String code) {
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.beginTransaction();
		Cursor c = db.query(MetroStopDbHelper.DB_TABLE, MetroStopDbHelper.columns,
				MetroStopDbHelper.CODE + "=" + code, null, null, null, null);
		
		if (c != null)
			c.moveToFirst();
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return c;
	}

	public void deleteAll() {
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.beginTransaction();
		db.delete(MetroStopDbHelper.DB_TABLE, null, null);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public void close() {
		dbh.close();
	}

}
