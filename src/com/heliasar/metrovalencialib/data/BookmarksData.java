package com.heliasar.metrovalencialib.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class BookmarksData {

	private BookmarksDbHelper dbh;
	private Context context;

	public BookmarksData(Context context) {
		this.context = context;
		dbh = new BookmarksDbHelper(context);
	}

	public long getCount() {
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.beginTransaction();
		Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + BookmarksDbHelper.DB_TABLE, null);
		if (c == null) {
			db.close();
			return 0;
		}

		c.moveToFirst();
		String[] cols = c.getColumnNames();
		final long count = c.getLong(c.getColumnIndexOrThrow(cols[0]));
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return count;
	}
	
	public void delete(Bookmark bookmark) {
		Uri uri = BookmarksProvider.CONTENT_URI;
		context.getContentResolver().delete(uri, BookmarksDbHelper.ROWID + "=" + bookmark.rowId, null);
	}

	public ArrayList<Bookmark> getIndex() {
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.beginTransaction();
		ArrayList<Bookmark> notes = new ArrayList<Bookmark>();

		Cursor c = db.query(BookmarksDbHelper.DB_TABLE, BookmarksDbHelper.columns, null, null, null, null, BookmarksDbHelper.ROWID + " DESC");
		if (c == null) {
			db.close();
			return null;
		}

		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			notes.add(new Bookmark(c));
			c.moveToNext();
		}
		c.close();
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return notes;
	}

	public Uri create(Bookmark bookmark) {
		return context.getContentResolver().insert(BookmarksProvider.CONTENT_URI, bookmark.getContentValues());
	}

	public Cursor getAll() {
		SQLiteDatabase db = dbh.getReadableDatabase();
		db.beginTransaction();
		Cursor c = db.query(BookmarksDbHelper.DB_TABLE, BookmarksDbHelper.columns,
				null, null, null, null, BookmarksDbHelper.ROWID + " DESC");
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return c;
	}

	public Cursor get(long bookmarkId) {
		Uri note = BookmarksProvider.CONTENT_URI;
		Cursor c = context.getContentResolver().query(note, Bookmark.projection,
				BookmarksDbHelper.ROWID + "=" + bookmarkId, null, null);

		if (c != null)
			c.moveToFirst();
		return c;
	}

	public int update(Bookmark bookmark) {
		Uri uri = BookmarksProvider.CONTENT_URI;
		return context.getContentResolver().update(uri,
				bookmark.getContentValues(), BookmarksDbHelper.ROWID + "=" + bookmark.rowId, null);
	}

	public void deleteAll() {
		SQLiteDatabase db = dbh.getWritableDatabase();
		db.beginTransaction();
		db.delete(BookmarksDbHelper.DB_TABLE, null, null);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public void close() {
		dbh.close();
	}

}
