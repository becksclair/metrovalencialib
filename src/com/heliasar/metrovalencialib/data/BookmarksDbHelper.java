package com.heliasar.metrovalencialib.data;

import com.heliasar.tools.Utils;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookmarksDbHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "metrovalencia_data.db";
	public static final String DB_TABLE = "bookmarks";
	private static final int DB_VERSION = 1;

	public static final String ROWID = "_id";
	public static final String NAME = "name";
	public static final String DEPARTURE = "departure";
	public static final String ARRIVAL = "arrival";

	public static final String[] columns = { ROWID, NAME, DEPARTURE, ARRIVAL };

	private static final String sql = "CREATE TABLE " + DB_TABLE + " (" + ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
			+ " TEXT NOT NULL, " + DEPARTURE + " TEXT NOT NULL, " + ARRIVAL
			+ " TEXT NOT NULL)";

	public BookmarksDbHelper(Context context) {
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