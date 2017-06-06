package jp.ac.uec.inf.az.memimgauth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azlabadm on 05.06.2017.
 */

public class DatabaseConnection {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_USER };

    public DatabaseConnection(Context context) {
        dbHelper = MySQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public User createUser(String user) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER, user);
        long insertId = database.insert(MySQLiteHelper.TABLE_USERS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            User newUser = cursorToUser(cursor);
            cursor.close();
            Log.d("Created User ", user + " with id " + insertId);
            return newUser;
        }
        return new User();
    }

    public void deleteUser(User user) {
        long id = user.getId();
        Log.d("User deleted with id: ", ""+ id);
        database.delete(MySQLiteHelper.TABLE_USERS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    public User getUserForId(int insertId) {
        String[] args = { String.valueOf(insertId) };
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = ?", args, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            return cursorToUser(cursor);

        }
        cursor.close();
        return new User();
    }

    public boolean setPassImagesForUser(int userId, String passImagesAsJsonArray){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PASSIMAGES, passImagesAsJsonArray);
        String[] args = { String.valueOf(userId) };
        int count = database.update(MySQLiteHelper.TABLE_USERS,
                values,
                MySQLiteHelper.COLUMN_ID + " = ?",
                args);
        Log.d("Set pass images for uid", ""+ userId);
        return count > 0;
    }

    public ArrayList<Uri> getPassImagesForUser(int userid){
        String[] tables = { MySQLiteHelper.COLUMN_PASSIMAGES };
        String[] args = { String.valueOf(userid) };
        ArrayList<Uri> values = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
                tables, MySQLiteHelper.COLUMN_ID + " = ?", args, null, null, null);
        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            String json = cursor.getString(0);
            if (json != null) {
                try {
                    JSONArray a = new JSONArray(json);
                    for (int i = 0; i < a.length(); i++) {
                        String singleString = a.optString(i);
                        values.add(Uri.parse(singleString));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        cursor.close();
        Log.d("Pass images size", ""+values.size());
        return values;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(0));
        user.setName(cursor.getString(1));
        return user;
    }
}
