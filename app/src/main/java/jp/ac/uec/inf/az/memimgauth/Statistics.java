package jp.ac.uec.inf.az.memimgauth;

import java.text.SimpleDateFormat;
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
            int mins = (int) (absDiff/(1000*60)) % 60;
            long secs = (int) (absDiff / 1000) % 60;
            neededTimeForAuthenticationProcess = hours+":"+mins+":"+secs;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
