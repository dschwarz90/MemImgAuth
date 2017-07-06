package jp.ac.uec.inf.az.memimgauth;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
    private long neededTimeFotAuthentication;
    private ArrayList<Long> neededTimeForPassImageSelection = new ArrayList<>();

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
            neededTimeFotAuthentication = end.getTime() - start.getTime();

            long absDiff = Math.abs(neededTimeFotAuthentication);
            long hours =  (absDiff/(1000 * 60 * 60));
            //long min =  absDiff / (60 * 1000) % 60;
            //long secs = absDiff / 1000 % 60;
            //long millis = diff;
            long min = TimeUnit.MILLISECONDS.toMinutes(neededTimeFotAuthentication);
            long secs = TimeUnit.MILLISECONDS.toSeconds(neededTimeFotAuthentication);
            long millis = TimeUnit.MILLISECONDS.toMillis(neededTimeFotAuthentication);
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

    public int getNumberOfEnteredPassImages() {
        return enteredPassImages.size();
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

    /**
     * returns the auth. result
     * @return the auth result as enum
     */
    public Enum<AuthResult> getAuthenticationResult() {
        return authenticationResult;
    }

    /**
     * sets the auth results
     * @param result the auth. result (enum)
     */
    public void setAuthenticationResult(Enum<AuthResult> result) {
        authenticationResult = result;
    }

    /**
     * reset the current auth. session (current instance = null)
     */
    public void reset(){
        ourInstance = null;
    }

    /**
     * date of today
     * @return returns today's date
     */
    public String getDateOfToday() {
        return dateOfToday;
    }

    /**
     * returns a string with the needed selection time for each pass image
     * @return concatenated string with needed selection time for each image
     */
    public String getNeededTimeForPassImageSelection() {
        String stringForLogs = new String();
        if(!neededTimeForPassImageSelection.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<Long> iterator = neededTimeForPassImageSelection.iterator();
            int index = 1;
            //build the string
            while (iterator.hasNext()){
                stringBuilder.append(String.valueOf(index) + ":" + Long.toString(iterator.next()) + ";");
                index++;
            }
            //delete the last ";"
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            stringForLogs = stringBuilder.toString();
        }
        return stringForLogs;
    }

    /**
     * add a timestamp for an image selection
     */
    public void addNeededTimeForPassImageSelection() {
        this.neededTimeForPassImageSelection.add(System.currentTimeMillis());
    }

    /**
     * returns the needed time (end time - start time)
     * @return needed time for auth.
     */
    public long getNeededTimeFotAuthentication() {
        return neededTimeFotAuthentication;
    }

    /**
     * start time of auth. session as long
     * @return start time
     */
    public long getAuthenticationStartTime(){
        Date start = new Date();
        try{
            start = sdf.parse(startTime);
            return start.getTime();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return (long) 0;
    }

    /**
     * end time of auth. session as long
     * @return end time
     */
    public long getAuthenticationEndTime(){
        Date end = new Date();
        try{
            end = sdf.parse(endTime);
            return end.getTime();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return (long) 0;
    }
}
