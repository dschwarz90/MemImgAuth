package jp.ac.uec.inf.az.memimgauth;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.util.ArrayList;

public class showPassImages extends AppCompatActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<Image> thumbnails;
    private DatabaseConnection dbConnection;
    int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_images);

        dbConnection = new DatabaseConnection(this);
        dbConnection.open();

        ArrayList<Uri> imageList;

        userId = getIntent().getIntExtra("userId", 0);
        thumbnails = new ArrayList<>();
        if(userId > 0){
            imageList = dbConnection.getPassImagesForUser(userId);
            for (int i=0; i < imageList.size(); i++){
                Image image = new Image(imageList.get(i));
                thumbnails.add(image);
            }
        }
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, thumbnails);
        gridView.setAdapter(gridAdapter);

    }

}
