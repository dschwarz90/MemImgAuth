package jp.ac.uec.inf.az.memimgauth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Shows the Authentication Result screen
 */
public class authenticationSuccess extends AppCompatActivity {

    //statistics instance
    Statistics statistics = Statistics.getInstance();

    /**
     * Creates the page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_success);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processStatisticsMail();
            }
        });
        Log.d("needed time", statistics.getNeededTimeForAuthenticationProcess());
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        //changes to success/failed layout
        if(statistics.getAuthenticationResult() == AuthResult.OK){
            setTitle("Authentication Success!");
            imageView.setImageResource(+R.raw.success);
        }
        else {
            setTitle("Authentication Failure!");
        }
        //display needed time
        TextView operationTime = (TextView)findViewById(R.id.operationTime);
        operationTime.setText(statistics.getNeededTimeForAuthenticationProcess());

        //offer a fast way to redo the auth. trial
        Button retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), authenticate.class);
                intent.putExtra("userId", statistics.getUserId());
                intent.putExtra("numberOfDecoyImages", statistics.getNumberOfDecoyImages());
                startActivity(intent);
            }
        });
        retryButton.setEnabled(false);

        //link to login screen
        Button userChooserButton = (Button) findViewById(R.id.userChooserButton);
        userChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectUsers.class);
                startActivity(intent);
            }
        });
        //reset the statistics
        statistics.reset();
    }

    /**
     * Enables a button after the statistics mail was sent
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 100) {
            //enable the retry button
            Button retryButton = (Button) findViewById(R.id.retryButton);
            retryButton.setEnabled(true);
        }
    }

    /**
     * Send the statistics mail
     */
    private void processStatisticsMail(){
        String[] recipients = {"uecazlab@gmail.com"};
        String message = "Please find attached the Research Data.";
        File data = null;
        Date dateVal = new Date();
        String filename = statistics.getUsername()+"_"+dateVal.toString();
        try {
            //create a writeable temp file
            data = File.createTempFile(filename, ".csv", getExternalCacheDir());
            data.setReadable(true, false);
            //write the csv data
            FileWriter out = (FileWriter) generateCsvFile(data);
            sendMail(recipients, "DejaVu Data", message, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the email
     * @param recipients
     * @param subject
     * @param message
     * @param attachment
     */
    private void sendMail(String[] recipients, String subject, String message, File attachment){
        Intent i = new Intent(Intent.ACTION_SEND);
        //i.setType("plain/text");
        //i.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        i.setType("message/rfc822");
        if(attachment.exists()) {
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment));
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        i.putExtra(Intent.EXTRA_EMAIL, recipients);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
            //startActivityForResult(i, 100);
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(findViewById(R.id.fab), "There are no email clients installed.", Snackbar.LENGTH_LONG).show();
        }

    }

    /**
     * Write content to file
     * @param sFileName
     * @return
     */
    public FileWriter generateCsvFile(File sFileName) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(sFileName);
            CSVUtils.writeLine(writer, generateAttachmentHeadline());
            CSVUtils.writeLine(writer, generateAttachmentContent());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return writer;
    }

    /**
     * After pressing the hardware Back button, link to start screen
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userId", statistics.getUserId());
        startActivity(intent);
    }

    /**
     * Generate csv headline
     * @return headlines
     */
    private List<String> generateAttachmentHeadline() {
        List<String> content= new ArrayList<>();
        content.add("Username");
        content.add("dateOfExperiment");
        content.add("neededTimeForAuthTry");
        //content.add("authenticationTries");
        //content.add("maxAuthenticationTries");
        content.add("authResult");
        content.add("numberOfPassImages");
        content.add("numberOfDecoyImages");
        content.add("numberOfEnteredPassImages");
        content.add("authenticationStartTime");
        content.add("authenticationEndTime");
        content.add("neededTimeForPassImageSelection");

        return content;
    }

    /**
     * Generate csv content
     * @return csv content
     */
    private List<String> generateAttachmentContent(){
        List<String> content= new ArrayList<>();
        content.add(statistics.getUsername());
        content.add(statistics.getDateOfToday());
        content.add(String.valueOf(statistics.getNeededTimeFotAuthentication()));
        //content.add(String.valueOf(statistics.getAuthenticationTries()));
        //content.add(String.valueOf(statistics.getMaxAuthenticationTries()));
        content.add(statistics.getAuthenticationResult().toString());
        content.add(String.valueOf(statistics.getNumberOfPassImages()));
        content.add(String.valueOf(statistics.getNumberOfDecoyImages()));
        content.add(String.valueOf(statistics.getNumberOfEnteredPassImages()));
        content.add(String.valueOf(statistics.getAuthenticationStartTime()));
        content.add(String.valueOf(statistics.getAuthenticationEndTime()));
        content.add(statistics.getNeededTimeForPassImageSelection());

        return content;
    }

    /**
     * enable the retry button after activity restart
     */
    @Override
    public void onRestart(){
        //enable the retry button
        Button retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setEnabled(true);
        super.onRestart();
    }
}
