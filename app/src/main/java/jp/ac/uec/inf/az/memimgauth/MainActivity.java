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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * This is the main class with the start screen
 */
public class MainActivity extends AppCompatActivity {

    Button selectPassImagesButton;
    Button showPassImages;
    int userId = 0;
    private static final int PICK_IMAGE = 100;
    ArrayList<String> imageList;
    private DatabaseConnection dbConnection;
    private boolean passImagesSelected = false;
    //int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.MANAGE_DOCUMENTS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userId = getIntent().getIntExtra("userId", 0);
        dbConnection = new DatabaseConnection(this);
        dbConnection.open();

        final TextView username = (TextView) findViewById(R.id.userName);
        if(userId > 0){
            username.append(dbConnection.getUserForId(userId).getName());
        }
        else {
            username.setText("You are not logged in!");
        }
        //did the user already select some pass images?
        passImagesSelected = dbConnection.getPassImagesForUser(userId).size() > 0;
        selectPassImagesButton = (Button)findViewById(R.id.selectPassImagesButton);
        selectPassImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId > 0) {
                    openGallery();
                }
            }
        });

        showPassImages = (Button)findViewById(R.id.showPassImagesButton);
        showPassImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbConnection.getPassImagesForUser(userId).size() > 0) {
                    Intent intent = new Intent(getApplicationContext(), showPassImages.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
                else {
                    Snackbar.make(findViewById(R.id.activity_main),"Please select Pass Images first!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        Button authenticate_20 = (Button)findViewById(R.id.authenticateButton_20);
        authenticate_20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openAuthenticationScreen(findViewById(R.id.activity_main), 20);
                openAuthenticationScreen(v, 16);
            }
        });

        Button authenticate_60 = (Button)findViewById(R.id.authenticateButton_60);
        authenticate_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openAuthenticationScreen(findViewById(R.id.activity_main), 20);
                openAuthenticationScreen(v, 56);
            }
        });

        Button authenticate_100 = (Button)findViewById(R.id.authenticateButton_100);
        authenticate_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openAuthenticationScreen(findViewById(R.id.activity_main), 20);
                openAuthenticationScreen(v, 96);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.actions, menu);
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
        /*Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image*//*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(gallery, PICK_IMAGE);*/
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE);
    }

    /**
     * Handler for image selection
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                if (clipData.getItemCount() == 4) {
                    imageList = new ArrayList<>();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        imageList.add(clipData.getItemAt(i).getUri().toString());
                    }
                    setPassImagesForUser(imageList);
                    if(dbConnection.getPassImagesForUser(userId).size() > 0) {
                        Intent intent = new Intent(getApplicationContext(), showPassImages.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                }
                else {
                    Snackbar.make(findViewById(R.id.activity_main), clipData.getItemCount() + " Images selected. Please select 4 Pass Images!", Snackbar.LENGTH_LONG).show();
                }
            }
            else {
                Snackbar.make(findViewById(R.id.activity_main), "Please select 4 Pass Images!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Returns the real file path for a given URI.
     * @param contentUri URI of the file
     * @return File path
     * @deprecated
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
     * @deprecated
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
     * set the pass images for user
     * @param values
     * @return
     */
    private boolean setPassImagesForUser(ArrayList<String> values){
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i).toString());
        }
        return dbConnection.setPassImagesForUser(userId, a.toString());
    }

    /**
     * get string array from SharedPref
     * @param context
     * @param key
     * @return
     * @deprecated
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


    @Override
    protected void onResume() {
        passImagesSelected = dbConnection.getPassImagesForUser(userId).size() > 0;
        super.onResume();
    }

    @Override
    protected void onPause() {
        //dbConnection.close();
        super.onPause();
    }

    /**
     * Opens the Auth. Screen
     * @param view
     * @param numberOfDecoyImages
     */
    public void openAuthenticationScreen(View view, int numberOfDecoyImages){
        if(userId > 0 && passImagesSelected && !Uri.EMPTY.equals(dbConnection.getKeyPassImageForUser(userId))) {
            Intent intent = new Intent(getApplicationContext(), authenticate.class);
            intent.putExtra("userId", userId);
            intent.putExtra("numberOfDecoyImages", numberOfDecoyImages);
            startActivity(intent);
        }
        else{
            Snackbar.make(view,"Please select Pass Images correctly!", Snackbar.LENGTH_LONG).show();
        }
    }
}
