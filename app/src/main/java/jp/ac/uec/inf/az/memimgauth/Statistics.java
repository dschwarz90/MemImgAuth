package jp.ac.uec.inf.az.memimgauth;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by azlabadm on 15.05.2017.
 */

public class Statistics {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String dateOfToday;
    private String  startTime;
    private String  endTime;
    private String  neededTimeForAuthenticationProcess;
    private String username;
    private int userid;
    private int authenticationTries = 0;
    private int maxAuthenticationTries = 0;
    private ArrayList<Uri> enteredPassImages = new ArrayList<>();
    private int numberOfPassImages = 0;
    private int numberOfDecoyImages = 0;
    private Enum<AuthResult> authenticationResult;

    private static Statistics ourInstance = null;

    public static synchronized Statistics getInstance() {
        if(ourInstance == null) {
            ourInstance = new Statistics();
        }
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

    public int getUserId() {
        return userid;
    }

    public void setUserId(int userid) {
        this.userid = userid;
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
            long hours =  (absDiff/(1000 * 60 * 60));
            //long min =  absDiff / (60 * 1000) % 60;
            //long secs = absDiff / 1000 % 60;
            //long millis = diff;
            long min = TimeUnit.MILLISECONDS.toMinutes(diff);
            long secs = TimeUnit.MILLISECONDS.toSeconds(diff);
            long millis = TimeUnit.MILLISECONDS.toMillis(diff);
            neededTimeForAuthenticationProcess = String.format("%d sec: %03d ms",
                    //TimeUnit.MILLISECONDS.toMinutes(absDiff),
                    TimeUnit.MILLISECONDS.toSeconds(absDiff), //- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(absDiff)),
                    TimeUnit.MILLISECONDS.toMillis(absDiff) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(absDiff))
            );
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

    public Enum<AuthResult> getAuthenticationResult() {
        return authenticationResult;
    }

    public void setAuthenticationResult(Enum<AuthResult> result) {
        authenticationResult = result;
    }

    public void reset(){
        ourInstance = null;
    }
}
