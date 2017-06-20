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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
        if(statistics.isAuthenticationIsSuccessful()){
            setTitle("Authentication Success!");
        }
        else {
            setTitle("Authentication Failure!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void processStatisticsMail(){
        String[] recipients = {"ds313373@gmail.com"};
        String message = "Please find attached the Research Data.";
        File data = null;
        Date dateVal = new Date();
        String filename = dateVal.toString();
        try {
            data = File.createTempFile(filename, ".csv", getExternalCacheDir());
            data.setReadable(true, false);
            FileWriter out = (FileWriter) generateCsvFile(
                    data, "Name,Data1");
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

    public FileWriter generateCsvFile(File sFileName,String fileContent) {
        FileWriter writer = null;

        try {
            writer = new FileWriter(sFileName);
            writer.append(fileContent);
            writer.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally
        {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return writer;
    }


}
