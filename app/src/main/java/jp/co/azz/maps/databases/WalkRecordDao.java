package jp.co.azz.maps.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.DatabaseUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class WalkRecordDao {
    private DatabaseHelper dataBaseHelper;

    public WalkRecordDao(Context context) {
        dataBaseHelper = new DatabaseHelper(context);
    }


    public List<HistoryDto> selectHistory() {
        //history(履歴テーブル)SELECT文

        List<HistoryDto> historyList = new ArrayList<>();

        try(
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(DatabaseContract.History.SELECT_SQL,null)
        ) {
            while(cursor.moveToNext()) {

                historyList.add(new HistoryDto(
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.History._ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.History.COLUMN_START_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.History.COLUMN_END_DATE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.History.COLUMN_NUMBER_OF_STEPS)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseContract.History.COLUMN_DISTANCE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.History.COLUMN_CALOLIE))
                ));

            }
        }
        return historyList;
    }

    /**
     * 履歴取得（id指定）
     * @param historyId
     * @return
     */
    @Nullable
    public HistoryDto selectByIdFromHistory(long historyId) {
        //history(履歴テーブル)SELECT文
        List<HistoryDto> historyList = new ArrayList<>();

        try(
                SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
                Cursor cursor = db.query(
                        DatabaseContract.History.TABLE_NAME,
                        null,
                        DatabaseContract.History._ID + " = ?",
                        new String[]{String.valueOf(historyId)},
                        null,
                        null,
                        null,
                        "1"
                )
        ) {
            if(cursor.moveToFirst()) {
                return new HistoryDto(cursor);
            }
            return null;
        }
    }

    /**
     * 履歴一覧Insert
     *
     * @param start_date
     * @param end_date
     * @param number_of_steps
     * @param distance
     * @param calorie
     * @return
     */
    public long insertHistory(String start_date,
                              String end_date,
                              int number_of_steps,
                              double distance,
                              int calorie){
        //history(履歴テーブル)INSERT文
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.History.COLUMN_START_DATE, start_date);
        cv.put(DatabaseContract.History.COLUMN_END_DATE, end_date);
        cv.put(DatabaseContract.History.COLUMN_NUMBER_OF_STEPS, number_of_steps);
        cv.put(DatabaseContract.History.COLUMN_DISTANCE, distance);
        cv.put(DatabaseContract.History.COLUMN_CALOLIE, calorie);

        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        return db.insert(DatabaseContract.History.TABLE_NAME,null,cv);

    }

    public void updateHistory(long ID,
                              String end_date,
                              int number_of_steps,
                              double distance){
        //history(履歴テーブル)UPDATE文
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.History.COLUMN_END_DATE, end_date);
        cv.put(DatabaseContract.History.COLUMN_NUMBER_OF_STEPS, number_of_steps);
        cv.put(DatabaseContract.History.COLUMN_DISTANCE, distance);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.update(DatabaseContract.History.TABLE_NAME, cv, DatabaseContract.History._ID + " = "+ ID , null);

    }

    public void deleteHistory(long ID){
        //history(履歴テーブル)DELETE文
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.delete(DatabaseContract.History.TABLE_NAME,  "id = "+ ID , null);

    }

    @NonNull
    public CoordinateListDto selectCoordinate(long ID){
        try(
                SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery(DatabaseContract.Coordinate.SELECT_SQL,new String[]{String.valueOf(ID)})
        ) {
            return CoordinateListDto.create(cursor);
        }
    }

    /**
     * 座標テーブルInsert
     * @param number_of_history
     * @param coordinate_x
     * @param coordinate_y
     */
    public void insertCoordinate(long number_of_history,
                                 double coordinate_x,
                                 double coordinate_y){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Coordinate.COLUMN_NUMBER_OF_HISTORY, number_of_history);
        cv.put(DatabaseContract.Coordinate.COLUMN_COORDINATE_X, coordinate_x);
        cv.put(DatabaseContract.Coordinate.COLUMN_COORDINATE_Y, coordinate_y);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.insert(DatabaseContract.Coordinate.TABLE_NAME,null,cv);

    }

    /**
     * 座標テーブルInsert
     * @param historyId
     * @param latLng
     */
    public void insertCoordinate(long historyId,
                                 LatLng latLng){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Coordinate.COLUMN_NUMBER_OF_HISTORY, historyId);
        cv.put(DatabaseContract.Coordinate.COLUMN_COORDINATE_X, latLng.latitude);
        cv.put(DatabaseContract.Coordinate.COLUMN_COORDINATE_Y, latLng.longitude);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.insert(DatabaseContract.Coordinate.TABLE_NAME,null,cv);

    }
    /**
     * 座標テーブルDelete
     * @param ID
     */
    public void deleteCoordinate(long ID){
        //history(履歴テーブル)DELETE文
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.delete(DatabaseContract.Coordinate.TABLE_NAME,  "id = "+ ID , null);

    }
    public long selectCoordinateCount(){
        //history(履歴テーブル)SELECT文
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        long recodeCount = DatabaseUtils.queryNumEntries(db, DatabaseContract.Coordinate.TABLE_NAME);
        return recodeCount;
    }
}