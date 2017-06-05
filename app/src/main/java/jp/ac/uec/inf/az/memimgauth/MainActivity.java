package jp.ac.uec.inf.az.memimgauth;

import android.content.ClipData;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * This is the main class with the start screen
 */
public class MainActivity extends AppCompatActivity {

    Button button;
    Button showPassImages;
    Button authenticate;
    EditText username;
    private static final int PICK_IMAGE = 100;
    ArrayList<String> imageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText)findViewById(R.id.username);

        button = (Button)findViewById(R.id.selectPassImagesButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Button showUsers = (Button) findViewById(R.id.showUsersButton);
        showUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectUsers.class);
                startActivity(intent);
            }
        });

        showPassImages = (Button)findViewById(R.id.showPassImagesButton);
        showPassImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), showPassImages.class);
                final ArrayList<String> arr = getStringArrayPref(getApplicationContext(), "passImages");
                intent.putExtra("passImages", arr);
                startActivity(intent);
            }
        });

        authenticate = (Button)findViewById(R.id.authenticateButton);
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().length() > 0) {
                    Intent intent = new Intent(getApplicationContext(), authenticate.class);
                    ArrayList<String> selectedPassImages = getStringArrayPref(getApplicationContext(), "passImages");
                    final ArrayList<String> passImagesToDisplay = selectedPassImages;
                    Log.d("pass images len", ""+selectedPassImages.size());
                    intent.putExtra("passImages", passImagesToDisplay);
                    startActivity(intent);
                }
                else{
                    Snackbar.make(findViewById(R.id.activity_main),"Please set a user name!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuItemSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the gallery
     * @return void
     */
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            ClipData clipData = data.getClipData();
            imageList = new ArrayList<>();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                imageList.add(clipData.getItemAt(i).getUri().toString());
            }
            setStringArrayPref(getApplicationContext(), "passImages", imageList);
        }
    }

    /**
     * Returns the real file path for a given URI.
     * @param contentUri URI of the file
     * @return File path
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    /**
     * set string array in SharedPref
     * @param context
     * @param key
     * @return
     */
    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i).toString());
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    /**
     * get string array from SharedPref
     * @param context
     * @param key
     * @return
     */
    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> values = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String singleString = a.optString(i);
                    Log.d("single string", singleString);
                    values.add(singleString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

}
