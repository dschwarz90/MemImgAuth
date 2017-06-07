package jp.ac.uec.inf.az.memimgauth;

import android.net.Uri;

/**
 * Created by azlabadm on 29.04.2017.
 */

public class Image {

    private Uri imageUri;
    boolean isChecked = false;

    public Image(Uri uri) {
        this.imageUri = uri;
    }

    public Uri getImageUri(){
        return this.imageUri;
    }

    public boolean isChecked(){
        return isChecked;
    }
    public void toggleChecked(){
        isChecked = !isChecked;
    }
}
