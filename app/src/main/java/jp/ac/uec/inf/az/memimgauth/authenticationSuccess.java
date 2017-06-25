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

public class authenticationSuccess extends AppCompatActivity {

    Statistics statistics = Statistics.getInstance();

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

        Button retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), authenticate.class);
                intent.putExtra("userId", statistics.getUserId());
                intent.putExtra("numberOfDecoyImages", statistics.getNumberOfDecoyImages());
                //todo maybe calculate the authTries
                startActivity(intent);
            }
        });

        Button userChooserButton = (Button) findViewById(R.id.userChooserButton);
        userChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectUsers.class);
                startActivity(intent);
            }
        });
        statistics.reset();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void processStatisticsMail(){
        //String[] recipients = {"uecazlab@gmail.com"};
        String[] recipients = {"ds313373@gmail.com"};
        String message = "Please find attached the Research Data.";
        File data = null;
        Date dateVal = new Date();
        String filename = dateVal.toString();
        try {
            data = File.createTempFile(filename, ".csv", getExternalCacheDir());
            data.setReadable(true, false);
            FileWriter out = (FileWriter) generateCsvFile(data);
            sendMail(recipients, "DejaVu Data", message, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMail(String[] recipients, String subject, String message, File attachment){
        Intent i = new Intent(Intent.ACTION_SEND);
        //i.setType("plain/text");
        i.setType("message/rfc822");
        if(attachment.exists()) {
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment));
        }
        i.putExtra(Intent.EXTRA_EMAIL, recipients);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(findViewById(R.id.fab), "There are no email clients installed.", Snackbar.LENGTH_LONG).show();
        }

    }

    public FileWriter generateCsvFile(File sFileName) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(sFileName);
            CSVUtils.writeLine(writer, generateAttachmentHeadline(), ',', '"');
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userId", statistics.getUserId());
        startActivity(intent);
    }

    private List<String> generateAttachmentHeadline() {
        List<String> content= new ArrayList<>();
        content.add("Username");
        content.add("dateOfExperiment");
        content.add("neededTimeForAuthTry");
        content.add("authenticationTries");
        content.add("maxAuthenticationTries");
        content.add("authResult");
        content.add("numberOfPassImages");
        content.add("numberOfDecoyImages");

        return content;
    }

    private List<String> generateAttachmentContent(){
        List<String> content= new ArrayList<>();
        content.add(statistics.getUsername());
        content.add(statistics.getDateOfToday());
        content.add(statistics.getNeededTimeForAuthenticationProcess());
        content.add(String.valueOf(statistics.getAuthenticationTries()));
        content.add(String.valueOf(statistics.getMaxAuthenticationTries()));
        content.add(statistics.getAuthenticationResult().toString());
        content.add(String.valueOf(statistics.getNumberOfPassImages()));
        content.add(String.valueOf(statistics.getNumberOfDecoyImages()));
        return content;
    }
}
