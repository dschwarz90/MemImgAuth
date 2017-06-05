package jp.ac.uec.inf.az.memimgauth;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by azlabadm on 05.06.2017.
 */

public class User {

    private String name;
    private int id;
    private ArrayList<Uri> passimages;

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Uri> getPassimages() {
        return passimages;
    }

    public void setPassimages(ArrayList<Uri> passimages) {
        this.passimages = passimages;
    }

    public void addPassImage(Uri uri){
        passimages.add(uri);
    }
}
