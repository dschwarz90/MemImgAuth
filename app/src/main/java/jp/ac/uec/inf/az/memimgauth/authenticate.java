package jp.ac.uec.inf.az.memimgauth;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class authenticate extends AppCompatActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private HashSet<Uri> selectedPassImages;
    private HashSet<Uri> passImages;
    private Statistics statistics = Statistics.getInstance();
    int authenticationTries = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_images);
        gridView = (GridView) findViewById(R.id.gridView);
        selectedPassImages = new HashSet<>();
        ArrayList<String> imageList;
        TimingLogger timings = new TimingLogger("AUTHENTICATE", "onCreate");

        Bundle b = getIntent().getExtras();
        //receive all the pass images
        imageList = b.getStringArrayList("passImages");
        if(imageList.size() > 0){

            ArrayList<Image> thumbnails = new ArrayList<>();
            timings.addSplit("getCameraImages()");
            ArrayList<Uri> cameraImages = getCameraImages(getApplicationContext());
            int numberOfDecoyImagesToDisplay = Integer.parseInt(getValueFromSharedPref("numberOfDecoyImages"));
            int numberOfPassImagesToDisplay = Integer.parseInt(getValueFromSharedPref("numberOfDisplayedPassImages"));
            timings.addSplit("pickRandomElements(cameraImages)");
            List<Uri> decoyImagesToDisplay = pickRandomElements(cameraImages, numberOfDecoyImagesToDisplay);
            ArrayList<Uri> imageListAsUri = new ArrayList<>(imageList.size());
            for (Object object : imageList) {
                imageListAsUri.add(Uri.parse(Objects.toString(object, null)));
            }
            //preparing the image set for display
            timings.addSplit("pickRandomElements(imageListAsUri)");
            List<Uri> imagesToDisplay = pickRandomElements(imageListAsUri, numberOfPassImagesToDisplay);
            passImages = new HashSet<>(imagesToDisplay);
            for (int i=0; i < decoyImagesToDisplay.size(); i++){
                if(!imagesToDisplay.contains(decoyImagesToDisplay.get(i))) {
                    imagesToDisplay.add(decoyImagesToDisplay.get(i));
                }
            }
            //shuffle the list to randomize the order
            timings.addSplit("shuffle(imagesToDisplay");
            Collections.shuffle(imagesToDisplay);
            timings.addSplit("create images");
            for (int i=0; i < imagesToDisplay.size(); i++){
                Image image = new Image(imagesToDisplay.get(i).toString());
                thumbnails.add(image);
            }
            timings.addSplit("prepare the view");

            gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, thumbnails);
            gridView.setAdapter(gridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Image selectedImage = (Image) gridView.getItemAtPosition(position);
                    selectedImage.toggleChecked();
                    if(selectedImage.isChecked()){
                        selectedPassImages.add(selectedImage.getImageUri());
                    }
                    else{
                        if(selectedPassImages.contains(selectedImage.getImageUri())){
                            selectedPassImages.remove(selectedImage.getImageUri());
                        }
                    }
                    gridAdapter.notifyDataSetChanged();
                }
            });
            String username = getValueFromSharedPref("username");
            statistics.setUsername(username);
            statistics.startAuthentication();
        }
        else{
            Snackbar.make(gridView, "No Pass Images selected!", Snackbar.LENGTH_LONG).show();
        }
        timings.dumpToLog();
    }



    /**
     * gets all the photos from the camera gallery
     * @param context Current context
     * @return ArrayList with images
     */
    public ArrayList<Uri> getCameraImages(Context context) {
        final String[] projection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.Media.DATA
        };
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        ArrayList<Uri> result = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                Uri data= ContentUris
                                .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
/*        for (int i = 0; i < result.size(); i++){
        }*/
        return result;
    }

    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static List<Uri> pickRandomElements(ArrayList<Uri> lst, int n) {
        List<Uri> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);

        return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
    }

    public String getValueFromSharedPref(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(key, "0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.authenticate, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int authenticationMaxAttempts = Integer.parseInt(getValueFromSharedPref("numberOfLoginAttempts"));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.authenticate:
                //handle the authentication
                if(authenticationTries <= authenticationMaxAttempts
                        && selectedPassImages.equals(passImages)
                        && !passImages.isEmpty()) {
                    statistics.endAuthentication();
                    Intent intent = new Intent(this, authenticationSuccess.class);
                    startActivity(intent);
                    return true;
                }
                else if(authenticationTries <= authenticationMaxAttempts
                        && !selectedPassImages.equals(passImages)){
                    Snackbar.make(gridView, "Selected wrong Pass Images!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    Snackbar.make(gridView, "Authentication Failure!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                //increase the tries
                authenticationTries++;
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

}
