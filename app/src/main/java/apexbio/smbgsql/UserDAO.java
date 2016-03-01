package apexbio.smbgsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A1302 on 2015/7/27.
 */
public class UserDAO {
    public static final String TABLE_NAME = "User_table";

    public static final String Uid_COLUMN = "Uid_title";

    public static final String Account_COLUMN = "Account_COLUMN";
    public static final String Pwd_COLUMN = "Pwd_COLUMN";
    public static final String Uname_COLUMN = "Uname_COLUMN";
    public static final String Gender_COLUMN = "Gender_COLUMN";
    public static final String UBirthday_COLUMN = "UBirthday_COLUMN";
    public static final String Height_COLUMN = "Height_COLUMN";
    public static final String Weight_COLUMN = "Weight_COLUMN";
    public static final String Email_COLUMN = "Email_COLUMN";
    public static final String High_COLUMN = "High_COLUMN";
    public static final String Low_COLUMN = "Low_COLUMN";

    public static final String createUserStatement =
            String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "%s TEXT NOT NULL, " +
                            "%s TEXT NOT NULL," +
                            "%s TEXT," +
                            "%s INTEGER," +
                            "%s date," +
                            "%s double," +
                            "%s double," +
                            "%s TEXT," +
                            "%s INTEGER," +
                            "%s INTEGER)",
                            TABLE_NAME,
                            Uid_COLUMN,
                            Account_COLUMN,
                            Pwd_COLUMN,
                            Uname_COLUMN,
                            Gender_COLUMN,
                            UBirthday_COLUMN,
                            Height_COLUMN,
                            Weight_COLUMN,
                            Email_COLUMN,
                            High_COLUMN,
                            Low_COLUMN);

    private SQLiteDatabase db;

    public UserDAO(Context context) {
        db = SqliteDBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    // 新增使用者資料
    public User insert(User user) {
        ContentValues cv = new ContentValues();

        cv.put(Uid_COLUMN, user.getUid());
        cv.put(Account_COLUMN, user.getAccount());
        cv.put(Pwd_COLUMN, user.getPwd());
        cv.put(Uname_COLUMN, user.getUname());
        cv.put(Gender_COLUMN, user.getGender());
        cv.put(UBirthday_COLUMN, user.getUbirthday());
        cv.put(Height_COLUMN, user.getHeight());
        cv.put(Weight_COLUMN, user.getWeight());
        cv.put(Email_COLUMN, user.getEmail());
        cv.put(High_COLUMN, user.getHigh());
        cv.put(Low_COLUMN, user.getLow());

        long uid = db.insert(TABLE_NAME, null, cv);

        user.setUid(uid);
        return user;
    }

    // 更新使用者資料
    public boolean update(User user) {
        ContentValues cv = new ContentValues();

        //cv.put(Uid_COLUMN, user.getUid());
        cv.put(Account_COLUMN, user.getAccount());
        cv.put(Pwd_COLUMN, user.getPwd());
        cv.put(Uname_COLUMN, user.getUname());
        cv.put(Gender_COLUMN, user.getGender());
        cv.put(UBirthday_COLUMN, user.getUbirthday());
        cv.put(Height_COLUMN, user.getHeight());
        cv.put(Weight_COLUMN, user.getWeight());
        cv.put(Email_COLUMN, user.getEmail());
        cv.put(High_COLUMN, user.getHigh());
        cv.put(Low_COLUMN, user.getLow());

        String where = Uid_COLUMN + "=" + user.getUid();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除全部資料
    public boolean deleteAll(){
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, null , null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(long uid){
        String where = Uid_COLUMN + "=" + uid;
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // 取得指定編號的資料物件
    public User get(long uid) {
        User user = null;
        String where = Uid_COLUMN + "=" + uid;
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            user = getUser(result);
        }
        result.close();
        return user;
    }

    // 登入功能
    public User login(String account, String pwd) {
        User user = null;
        String where = Account_COLUMN + " = '" + account + "' and "
                + Pwd_COLUMN + " = '" + pwd + "'";
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            user = getUser(result);
        }

        result.close();
        return user;
    }

    // 打包使用者資料
    private User getUser(Cursor cursor) {
        User result = new User();

        result.setUid(cursor.getLong(0));
        result.setAccount(cursor.getString(1));
        result.setPwd(cursor.getString(2));
        result.setUname(cursor.getString(3));
        result.setGender(cursor.getInt(4));
        result.setUbirthday(cursor.getString(5));
        result.setHeight(cursor.getDouble(6));
        result.setWeight(cursor.getDouble(7));
        result.setEmail(cursor.getString(8));
        result.setHigh(cursor.getInt(9));
        result.setLow(cursor.getInt(10));

        return result;
    }

    // 取得使用者資料數量
    public int getUserCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    // 檢查使用者帳號是否存在
    public boolean CheckUserAccount(String account) {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME
                + " WHERE " + Account_COLUMN + " = '" + account + "'"
                , null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        if(result > 0){
            return true;
        }else{
            return false;
        }
    }

    // 使用者範例資料
    public void sample(){
        List<User> user = new ArrayList<User>();

        user.add(new User(0, "apexbiotest1", "123456", "Apexbio RD1"
                , 1, "2001-05-06", 175.2, 70.5, "apexbio1@gmail.com", 85, 75));
        user.add(new User(1, "apexbiotest2", "123456", "Apexbio RD2"
                , 1, "2002-06-07", 185.2, 75.5, "apexbio2@gmail.com", 85, 75));
        user.add(new User(2, "apexbiotest3", "123456", "Apexbio RD3"
                , 0, "2003-07-08", 165.2, 52.5, "apexbio3@gmail.com", 85, 75));

        for(int i = 0;i < user.size(); i++) {
            insert(user.get(i));
        }
    }
}
