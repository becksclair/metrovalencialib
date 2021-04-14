package com.heliasar.metrovalencialib.data;

import android.content.ContentValues;
import android.database.Cursor;

public class Bookmark {

	public static final String[] projection = { BookmarksDbHelper.ROWID, BookmarksDbHelper.NAME,
			BookmarksDbHelper.DEPARTURE, BookmarksDbHelper.ARRIVAL };

	public long rowId;
	public String name = "";
	public String departure = "";
	public String arrival = "";

	public Bookmark() {
	}

	public Bookmark(String name, String departure, String arrival) {
		this.name = name;
		this.departure = departure;
		this.arrival = arrival;
	}

	public Bookmark(Cursor cursor) {
		rowId = cursor.getLong(cursor.getColumnIndexOrThrow(BookmarksDbHelper.ROWID));
		name = cursor.getString(cursor.getColumnIndexOrThrow(BookmarksDbHelper.NAME));
		departure = cursor.getString(cursor.getColumnIndexOrThrow(BookmarksDbHelper.DEPARTURE));
		arrival = cursor.getString(cursor.getColumnIndexOrThrow(BookmarksDbHelper.ARRIVAL));
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public ContentValues getContentValues() {
		ContentValues obj = new ContentValues();

		obj.put(BookmarksDbHelper.NAME, name);
		obj.put(BookmarksDbHelper.DEPARTURE, departure);
		obj.put(BookmarksDbHelper.ARRIVAL, arrival);

		return obj;
	}

}
