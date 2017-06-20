package jp.ac.uec.inf.az.memimgauth;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by azlabadm on 15.05.2017.
 */

public class Statistics {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String dateOfToday;
    private String  startTime;
    private String  endTime;
    private String  neededTimeForAuthenticationProcess;
    private String username;
    private int authenticationTries = 1;
    private int maxAuthenticationTries = 0;
    private ArrayList<Uri> enteredPassImages = new ArrayList<>();
    private int numberOfPassImages = 0;
    private int numberOfDecoyImages = 0;
    private boolean authenticationIsSuccessful = false;

    private static final Statistics ourInstance = new Statistics();

    public static Statistics getInstance() {
        return ourInstance;
    }

    private Statistics() {
        dateOfToday =  sdf.format(new Date());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getNeededTimeForAuthenticationProcess(){
        return neededTimeForAuthenticationProcess;
    }

    public void startAuthentication(){
        startTime = sdf.format(new Date());
    }

    public void endAuthentication(){
        endTime = sdf.format(new Date());
        try {
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            long diff = end.getTime() - start.getTime();
            long absDiff = Math.abs(diff);
            int hours = (int) (absDiff/(1000 * 60 * 60));
            int min = (int) (absDiff/(1000*60)) % 60;
            long secs = (int) (absDiff / 1000) % 60;
            neededTimeForAuthenticationProcess = hours+":"+min+":"+secs;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getAuthenticationTries() {
        return authenticationTries;
    }

    public void addAuthenticationTry() {
        this.authenticationTries++;
    }

    public ArrayList<Uri> getEnteredPassImages() {
        return enteredPassImages;
    }

    public void addEnteredPassImage(Uri enteredPassImage) {
        this.enteredPassImages.add(enteredPassImage);
    }

    public void setNumberOfPassImages(int numberOfPassImages) {
        this.numberOfPassImages = numberOfPassImages;
    }

    public void setNumberOfDecoyImages(int numberOfDecoyImages) {
        this.numberOfDecoyImages = numberOfDecoyImages;
    }

    public int getNumberOfPassImages() {
        return numberOfPassImages;
    }

    public int getNumberOfDecoyImages() {
        return numberOfDecoyImages;
    }

    public int getMaxAuthenticationTries() {
        return maxAuthenticationTries;
    }

    public void setMaxAuthenticationTries(int maxAuthenticationTries) {
        this.maxAuthenticationTries = maxAuthenticationTries;
    }

    public boolean isAuthenticationIsSuccessful() {
        return authenticationIsSuccessful;
    }

    public void setAuthenticationIsSuccessful(boolean authenticationIsSuccessful) {
        this.authenticationIsSuccessful = authenticationIsSuccessful;
    }
}
