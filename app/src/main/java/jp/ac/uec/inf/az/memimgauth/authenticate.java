package jp.ac.uec.inf.az.memimgauth;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class authenticate extends AppCompatActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private HashSet<Uri> selectedPassImages;
    private HashSet<Uri> passImages;
    private Statistics statistics = Statistics.getInstance();
    private String photoFolderName = "/";
    private DatabaseConnection dbConnection;
    TextView textView;
    private int userId = 0;
    private int selectedImagesCounter = 0;
    private boolean keyImageSuccessfullySelected = false;

    /**
     * Handler for an authentication trial
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_images);
        gridView = (GridView) findViewById(R.id.gridView);
        selectedPassImages = new HashSet<>();
        TimingLogger timings = new TimingLogger("AUTHENTICATE", "onCreate");

        textView = (TextView)findViewById(R.id.selectedImagesCounter);
        changeImageCounterText(textView);
        Button selectKeyPassImageButton = (Button)findViewById(R.id.selectKeyPassImageButton);
        selectKeyPassImageButton.setVisibility(View.GONE);
        userId = getIntent().getIntExtra("userId", 0);

        //receive all the pass images
        if(userId > 0){
            dbConnection = new DatabaseConnection(this);
            dbConnection.open();
            ArrayList<Uri> passImagesForUser = dbConnection.getPassImagesForUser(userId);
            //receiving the folder name of the first pass image (pass images can only originate from the same folder)
            photoFolderName = getPhotofolderName(getApplicationContext(), passImagesForUser.get(0));
            ArrayList<Image> thumbnails = new ArrayList<>();
            timings.addSplit("getCameraImages()");
            ArrayList<Uri> cameraImages = getCameraImages(getApplicationContext());
            Log.d("Size cameraImages", ""+cameraImages.size());
            final int numberOfDecoyImagesToDisplay = getIntent().getIntExtra("numberOfDecoyImages", 0);
            int numberOfPassImagesToDisplay = 4;
            //int numberOfPassImagesToDisplay = Integer.parseInt(getValueFromSharedPref("numberOfDisplayedPassImages"));
            timings.addSplit("pickRandomElements(cameraImages)");
            List<Uri> decoyImagesToDisplay = pickRandomElements(cameraImages, numberOfDecoyImagesToDisplay+4);
            //preparing the image set for display
            timings.addSplit("pickRandomElements(imageListAsUri)");
            List<Uri> imagesToDisplay = pickRandomElements(passImagesForUser, numberOfPassImagesToDisplay);
            passImages = new HashSet<>(passImagesForUser);
            //since pass images and decoy images have diffent URIs, we need to compare the file path
            HashSet<String> imagesToDisplayPaths = new HashSet<>();
            for (int i=0; i < passImagesForUser.size(); i++){
                imagesToDisplayPaths.add(getFilePath(getApplicationContext(), passImagesForUser.get(i)));
            }
            Iterator<Uri> iterator = decoyImagesToDisplay.iterator();
            int numberToDisplay = numberOfDecoyImagesToDisplay+numberOfPassImagesToDisplay;
            Log.d("Should show", numberToDisplay+" images");
            while(imagesToDisplayPaths.size() < numberToDisplay){
                //we don't want to have duplicates
                if(iterator.hasNext()) {
                    Uri uri = iterator.next();
                    String path = getFilePath(getApplicationContext(), uri);
                    if (!imagesToDisplayPaths.contains(path)){
                        imagesToDisplayPaths.add(path);
                        imagesToDisplay.add(uri);
                    }
                    else {
                        Log.d("Found collision", "selecting new decoy img");
                    }
                }
                else {
                    Log.d("Will display", imagesToDisplay.size()+" images");
                    break;
                }
            }
            //shuffle the list to randomize the order
            timings.addSplit("shuffle(imagesToDisplay");
            Collections.shuffle(imagesToDisplay);
            timings.addSplit("create images");
            for (int i=0; i < imagesToDisplay.size(); i++){
                Image image = new Image(imagesToDisplay.get(i));
                thumbnails.add(image);
            }
            timings.addSplit("prepare the view");
            final User user = dbConnection.getUserForId(userId);
            //process the statistics
            statistics.setUsername(user.getName());
            statistics.setUserId(user.getId());
            statistics.setNumberOfDecoyImages(numberOfDecoyImagesToDisplay);
            statistics.setNumberOfPassImages(numberOfPassImagesToDisplay);
            statistics.startAuthentication();
            //set the image grid content
            gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, thumbnails);
            gridView.setAdapter(gridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //todo only 4 items should be selectable
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Image selectedImage = (Image) gridAdapter.getItem(position);
                    selectedImage.toggleChecked();
                    if (selectedImage.isChecked()) {
                        selectedImagesCounter++;
                        statistics.addEnteredPassImage(selectedImage.getImageUri());
                        statistics.addNeededTimeForPassImageSelection();
                        //change bg color
                        if (selectedPassImages.isEmpty()) {
                            selectedImage.setColor(Color.RED);
                        } else {
                            selectedImage.setColor(Color.BLUE);
                        }
                        selectedPassImages.add(selectedImage.getImageUri());
                    } else {
                        selectedImagesCounter--;
                        if (selectedPassImages.contains(selectedImage.getImageUri())) {
                            selectedPassImages.remove(selectedImage.getImageUri());
                        }
                        selectedImage.setColor(Color.TRANSPARENT);
                    }
                    //check selection of first key pass image
                    if(selectedPassImages.size() == 1
                            && selectedImage.getImageUri().equals(dbConnection.getKeyPassImageForUser(userId))){
                        keyImageSuccessfullySelected = true;
                    }
                    gridAdapter.notifyDataSetChanged();
                    changeImageCounterText(textView);
                    invalidateOptionsMenu();
                }
            });
            ImageButton buttonUp = (ImageButton)findViewById(R.id.pageUpButton);
            buttonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = gridView.getFirstVisiblePosition() - 20;
                    gridView.smoothScrollToPosition(pos);
                    Log.d("Jump to Pos", ""+pos);
                }
            });

            ImageButton buttonDown = (ImageButton)findViewById(R.id.pageDownButton);
            buttonDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = gridView.getLastVisiblePosition() + 20;
                    gridView.smoothScrollToPosition(pos);
                    Log.d("Jump to Pos", ""+pos);
                }
            });
        }
        else{
            Snackbar.make(gridView, "No valid user!", Snackbar.LENGTH_LONG).show();
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
        final String[] selectionArgs = { getBucketId(photoFolderName) };
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        ArrayList<Uri> result = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                Uri data = ContentUris
                        .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                result.add(data);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public List<Uri> pickRandomElements(ArrayList<Uri> lst, int n) {
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
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (selectedImagesCounter == 4) {
            menu.findItem(R.id.authenticate).setEnabled(true);
        }
        else{
            menu.findItem(R.id.authenticate).setEnabled(false);
        }
        Log.d("Enable auth menu", String.valueOf(selectedImagesCounter==4));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int authenticationMaxAttempts = Integer.parseInt(getValueFromSharedPref("numberOfLoginAttempts"));
        int authenticationMaxAttempts = 3;
        statistics.setMaxAuthenticationTries(authenticationMaxAttempts);
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.authenticate:
                //handle the authentication
                Log.d("image selection ok?", String.valueOf(selectedPassImages.equals(passImages)));
                Log.d("keyImageSuccessfullySel", String.valueOf(keyImageSuccessfullySelected));
                //everything is ok
                if(statistics.getAuthenticationTries() <= authenticationMaxAttempts
                        && keyImageSuccessfullySelected
                        && selectedPassImages.equals(passImages)) {
                    statistics.setAuthenticationResult(AuthResult.OK);
                    Log.d("Auth Result", "OK");
                }
                // key image not ok
                else if(statistics.getAuthenticationTries() <= authenticationMaxAttempts
                        && !keyImageSuccessfullySelected){
                        statistics.setAuthenticationResult(AuthResult.WrongKeyImage);
                        Log.d("Auth Result", "Wrong Key Image");
                }
                // pass images not ok
                else if(statistics.getAuthenticationTries() <= authenticationMaxAttempts
                        && keyImageSuccessfullySelected
                        && !selectedPassImages.equals(passImages)){
                        statistics.setAuthenticationResult(AuthResult.WrongPassImages);
                        Log.d("Auth Result", "Wrong Pass Images");
                }
                //too many auth tries
                else{
                    Log.d("Too many Auth Tries", statistics.getAuthenticationTries()+" needed");
                    statistics.setAuthenticationResult(AuthResult.TooManyAuthTries);
                }
                statistics.addAuthenticationTry();
                //end the auth try
                statistics.endAuthentication();
                Intent intent = new Intent(this, authenticationSuccess.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //finds the path of selected pass images.
    public String getPhotofolderName(Context context, Uri uri)
    {
        String absolutePhotoPath = getFilePath(context, uri);
        /*String path = "";
        List<String> pathsegments = filePathUri.getPathSegments();
        path = "/" + pathsegments.get(pathsegments.size() - 3) + "/"; //DCIM or custom folder
        path += pathsegments.get(pathsegments.size() - 2); // subfolder*/
        File file = new File(absolutePhotoPath);

        return file.getParent();
    }

    @Override
    protected void onResume() {
        Log.d("Auth Activity", "resume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("Auth Activity", "paused");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Auth Activity", "stopped");
        super.onStop();
    }

    private void changeImageCounterText(TextView textView){
        if(textView != null) {
            textView.setText(selectedImagesCounter + " Pass Images selected");
        }
    }

    /**
     * From https://stackoverflow.com/questions/19834842/android-gallery-on-kitkat-returns-different-uri-for-intent-action-get-content
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getFilePath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
