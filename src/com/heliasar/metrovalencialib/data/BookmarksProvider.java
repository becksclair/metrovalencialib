package com.heliasar.metrovalencialib.data;

import com.heliasar.tools.Utils;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class BookmarksProvider extends ContentProvider {

	private static final int BOOKMARKS = 100;
	private static final int BOOKMARK = 110;

	private static final String AUTHORITY = "com.heliasar.metrovalencialib.data.BookmarksProvider";
	private static final String BOOKMARKS_BASE_PATH = "bookmarks";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BOOKMARKS_BASE_PATH);
	public static final Uri BOOKMARK_URK = Uri.parse("content://" + AUTHORITY + "/" + BOOKMARKS_BASE_PATH + "/#");

	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/bookmarks";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/bookmarks";

	private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, BOOKMARKS_BASE_PATH, BOOKMARKS);
		uriMatcher.addURI(AUTHORITY, BOOKMARKS_BASE_PATH + "/#", BOOKMARK);
	}

	private BookmarksDbHelper dbh;

	@Override
	public synchronized boolean onCreate() {
		dbh = new BookmarksDbHelper(getContext());
		return true;
	}

	@Override
	public synchronized Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(BookmarksDbHelper.DB_TABLE);

		switch (uriMatcher.match(uri)) {
		case BOOKMARKS:
			// No filter
			break;
		case BOOKMARK:
			qb.appendWhere(BookmarksDbHelper.ROWID + "=" + uri.getLastPathSegment());

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		Cursor c = qb.query(dbh.getReadableDatabase(),
							projection,
							selection,
							selectionArgs,
							null, null,
							sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public synchronized Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase sqlDB;

		sqlDB = dbh.getWritableDatabase();
		try {
			long rowId = sqlDB.insertOrThrow(BookmarksDbHelper.DB_TABLE, null, values);

			if (rowId > 0) {
				Uri newUri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(uri, null);
				return newUri;
			} else {
				throw new SQLException("Failed to insert row into " + uri);
			}
		} catch (SQLiteConstraintException e) {
			Utils.l("Ignoring constraint failure.");
		}

		return null;
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbh.getWritableDatabase();
		int count;

		switch (uriMatcher.match(uri)) {
		case BOOKMARKS:
			count = db.update(BookmarksDbHelper.DB_TABLE, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public synchronized int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbh.getWritableDatabase();
		int count;
		
		switch (uriMatcher.match(uri)) {
		case BOOKMARKS:
			count = db.delete(BookmarksDbHelper.DB_TABLE, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public synchronized String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case BOOKMARKS:
			return CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

}
