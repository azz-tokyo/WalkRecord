package jp.co.azz.maps;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import jp.co.azz.maps.databases.DatabaseContract;
import jp.co.azz.maps.databases.DatabaseHelper;

/**
 * walkrecordテーブルのレコードをCursorLoaderに提供するクラス
 * （SQLiteのデータベースのデータをCursorLoaderで非同期に取得するときに使用、
 * ContentProviderクラスは別のアプリにアプリ情報を公開するときに利用する）
 */
public class WalkRecordContentProvider extends ContentProvider {
    private DatabaseHelper mDbHelper;

    private static final int WALKRECORD = 10;
    private static final int WALKRECORD_ID = 20;
    private static final String AUTHORITY = "jp.co.azz.maps.WalkRecordContentProvider";

    private static final String BASE_PATH = "walkRecord";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, WALKRECORD);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WALKRECORD_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(DatabaseContract.History.TABLE_NAME);

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case WALKRECORD :
                break;
            case WALKRECORD_ID:
                queryBuilder.appendWhere(DatabaseContract.History._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        return cursor;
    }

    /**
     * walkrecordテーブルにレコードを追加する
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case WALKRECORD:
                id = sqlDB.insert(DatabaseContract.History.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
    @Nullable
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
