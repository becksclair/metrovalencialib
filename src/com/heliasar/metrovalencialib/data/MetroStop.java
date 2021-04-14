package com.heliasar.metrovalencialib.data;

import android.content.ContentValues;
import android.database.Cursor;

public class MetroStop {
	
	public static final String[] projection = { MetroStopDbHelper.ROWID, MetroStopDbHelper.CODE,
		MetroStopDbHelper.NAME };
	
	private String id;
	private String name;

	public MetroStop() {
		
	}
	
	public MetroStop(String _id, String _name) {
		id = _id;
		name = _name;
	}
	
	public MetroStop(Cursor c) {
		id = c.getString(c.getColumnIndexOrThrow(MetroStopDbHelper.CODE));
		name = c.getString(c.getColumnIndexOrThrow(MetroStopDbHelper.NAME));
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	public ContentValues getContentValues() {
		ContentValues obj = new ContentValues();

		obj.put(MetroStopDbHelper.CODE, id);
		obj.put(MetroStopDbHelper.NAME, name);

		return obj;
	}
	
}
