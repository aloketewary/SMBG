package apexbio.smbgsql;

/**
 * Created by A1302 on 2015/7/7.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GdataDAO {
    // 表格名稱
    public static final String TABLE_NAME = "GData_table";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String Uid_COLUMN = "Uid_title";
    public static final String Metername_COLUMN = "Metername_title";
    public static final String Date_COLUMN = "Date_title";
    public static final String Time_COLUMN = "Time_title";
    public static final String Flag_COLUMN = "Flag_title";
    public static final String Gvalue_COLUMN = "Gvalue_title";
    public static final String Note_COLUMN = "Note_content";

    public static final String createStatement =
            String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s INTEGER, " +
                            "%s TEXT NOT NULL," +
                            "%s date," +
                            "%s time," +
                            "%s TEXT," +
                            "%s INTEGER," +
                            "%s TEXT)",
                            TABLE_NAME,
                            KEY_ID,
                            Uid_COLUMN,
                            Metername_COLUMN,
                            Date_COLUMN,
                            Time_COLUMN,
                            Flag_COLUMN,
                            Gvalue_COLUMN,
                            Note_COLUMN);

    // db
    private SQLiteDatabase db;

    // constructor
    public GdataDAO(Context context) {
        db = SqliteDBHelper.getDatabase(context);
    }

    // close db
    public void close() {
        db.close();
    }

    // 輸入血糖資料
    public GData insert(GData gdata) {
        ContentValues cv = new ContentValues();

        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Uid_COLUMN, gdata.getUid());
        cv.put(Metername_COLUMN, gdata.getMetername());
        cv.put(Date_COLUMN, gdata.getGDataDate().toString());
        cv.put(Time_COLUMN, gdata.getGDataTime().toString());
        cv.put(Gvalue_COLUMN, gdata.getGvalue());
        cv.put(Flag_COLUMN, gdata.getGDataFlag());
        cv.put(Note_COLUMN, gdata.getGNote());

        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        gdata.setId(id);
        // 回傳結果
        return gdata;
    }

    // 更新血糖資料庫
    public boolean update(GData gdata) {
        ContentValues cv = new ContentValues();

        cv.put(Uid_COLUMN, gdata.getUid());
        cv.put(Metername_COLUMN, gdata.getMetername());
        cv.put(Date_COLUMN, gdata.getGDataDate().toString());
        cv.put(Time_COLUMN, gdata.getGDataTime().toString());
        cv.put(Flag_COLUMN, gdata.getGDataFlag());
        cv.put(Gvalue_COLUMN, gdata.getGvalue());
        cv.put(Note_COLUMN, gdata.getGNote());

        String where = KEY_ID + "=" + gdata.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除血糖資料
    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // 刪除全部資料
    public boolean deleteAll(){
        return db.delete(TABLE_NAME, null , null) > 0;
    }

    // 讀取所有資料
    public List<GData> getAll() {
        List<GData> result = new ArrayList<GData>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定的血糖資料
    public GData get(long id) {
        GData gdata = null;
        String where = KEY_ID + "=" + id;
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            gdata = getRecord(result);
        }

        result.close();
        return gdata;
    }

    // 取得指定日期內的血糖資料
    public List<GData> getFromTo(String fDate, String tDate, long uid) {
        List<GData> gdata = new ArrayList<GData>();
        String where = Date_COLUMN + " between '" + fDate + "' and '" + tDate +
                "' and " + Uid_COLUMN + " = '" + uid + "'";
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
        while (result.moveToNext()) {
            gdata.add(getRecord(result));
        }
        result.close();
        return gdata;
    }

    //取得指定日期內小於多少的血糖數值
    public  List<GData> getLowValue(int low, String fDate, String tDate, long uid) {
        List<GData> gLowdata = new ArrayList<GData>();
        String where = Gvalue_COLUMN + " < '" + low + "' and "
                + Date_COLUMN + " between '" + fDate + "' and '" + tDate +
                "' and " + Uid_COLUMN + " = '" + uid + "'";
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
        while (result.moveToNext()) {
            gLowdata.add(getRecord(result));
        }
        result.close();
        return gLowdata;
    }

    //取得指定日期內大於多少的數值
    public List<GData> getHighValue(int high, String fDate, String tDate, long uid) {
        List<GData> gHighdata = new ArrayList<GData>();
        String where = Gvalue_COLUMN + " > '" + high + "' and "
                + Date_COLUMN + " between '" + fDate + "' and '" + tDate +
                "' and " + Uid_COLUMN + " = '" + uid + "'";
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
        while (result.moveToNext()) {
            gHighdata.add(getRecord(result));
        }
        result.close();
        return gHighdata;
    }

    //取得指定日期內，多少之間的數值
    public List<GData> getNormalValue(int high, int low, String fDate, String tDate, long uid) {
        List<GData> gNormaldata = new ArrayList<GData>();
        String where = Gvalue_COLUMN + " <= '" + high + "' and "
                + Gvalue_COLUMN + " >= '" + low + "' and "
                +Date_COLUMN + " between '" + fDate + "' and '" + tDate +
                "' and " + Uid_COLUMN + " = '" + uid + "'";
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
        while (result.moveToNext()) {
            gNormaldata.add(getRecord(result));
        }
        result.close();
        return gNormaldata;
    }

    // 把Cursor目前的資料包裝為物件
    public GData getRecord(Cursor cursor) {
        GData result = new GData();
        result.setId(cursor.getLong(0));
        result.setUid(cursor.getLong(1));
        result.setMetername(cursor.getString(2));
        result.setGDataDate(cursor.getString(3));
        result.setGDataTime(cursor.getString(4));
        result.setGDataFlag(cursor.getString(5));
        result.setGvalue(cursor.getInt(6));
        result.setGNote(cursor.getString(7));

        return result;
    }

    // 取得血糖資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    // 取得特定使用者之資料數量
    public int getUserCount(long uid) {
        int result = 0;
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_NAME + " where " + Uid_COLUMN + " = '" + uid + "'"
                , null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    // 建立範例資料
    public void sample(long uid) {
        List<GData> gdata = new ArrayList<GData>();
    	/*
	    	low到high亂數（含high）
			(int) (Math.random() * (high - low + 1) + low)

			low到high亂數（不含high）
			(int) (Math.random() * (high - low) + low)
    	*/
        // 500筆資料
        for(int i = 0; i < 500 ; i++){
            int month = (int) (Math.random()*12 + 1);
            int day = 1;
            if(month == 2){
                day = (int) (Math.random()*28 + 1);
            }else if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 ||
                    month == 10 || month == 12){
                day = (int) (Math.random()*31 + 1);
            }else{
                day = (int) (Math.random()*30 + 1);
            }
            int hour = (int) (Math.random()*25);
            int mins = (int) (Math.random()*61);
            int value = (int) (Math.random()*40 + 60);
            int intflag = (int) (Math.random()*4 + 1);
            String flag = "";
            if(intflag == 1){
                flag = "早上";
            }else if(intflag == 2){
                flag = "飯前";
            }else if(intflag == 3){
                flag = "飯後";
            }else{
                flag = "睡前";
            }

            gdata.add(new GData(i, uid, "G2", "2016-" + String.format("%02d", month) + "-" + String.format("%02d", day)
                    , hour + ":" + mins, flag, value, "for test"));
        }
        for(int i = 0;i < gdata.size(); i++){
            insert(gdata.get(i));
        }
    }
}
