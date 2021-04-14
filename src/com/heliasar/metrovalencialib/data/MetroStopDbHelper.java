package com.heliasar.metrovalencialib.data;

import com.heliasar.tools.Utils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MetroStopDbHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "metrovalencia_stops.db";
	public static final String DB_TABLE = "stops";
	private static final int DB_VERSION = 1;

	public static final String ROWID = "_id";
	public static final String CODE = "code";
	public static final String NAME = "name";

	public static final String[] columns = { ROWID, CODE, NAME };

	private static final String sql = "CREATE TABLE " + DB_TABLE + " (" + ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + CODE
			+ " TEXT NOT NULL, " + NAME + " TEXT NOT NULL)";

	public MetroStopDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Utils.l("onCreate SQL: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Utils.l("Upgrading database from version " + oldVersion + " to version " + newVersion);

		switch (oldVersion) {
		case 1:
			Utils.l("** now upgrading from v1 to v2;");
		default:
			Utils.l("** upgrade steps complete.");
			break;
		}
	}
}
