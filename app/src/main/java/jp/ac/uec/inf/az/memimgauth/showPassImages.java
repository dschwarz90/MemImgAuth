package jp.ac.uec.inf.az.memimgauth;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class showPassImages extends AppCompatActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<Image> thumbnails;
    private DatabaseConnection dbConnection;
    private Uri selectedKeyPassImage = null;
    int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_images);

        dbConnection = new DatabaseConnection(this);
        dbConnection.open();
        ArrayList<Uri> imageList;
        userId = getIntent().getIntExtra("userId", 0);
        TextView textView = (TextView)findViewById(R.id.selectedImagesCounter);
        textView.setVisibility(View.GONE);
        Button selectKeyPassImageButton = (Button)findViewById(R.id.selectKeyPassImageButton);
        selectKeyPassImageButton.setVisibility(View.VISIBLE);
        selectKeyPassImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedKeyPassImage != null){
                    dbConnection.setKeyPassImageForUser(userId, selectedKeyPassImage);
                    Snackbar.make(v,"Successfully selected a Key Pass Image!", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(v,"Please select a Key Pass Image!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        thumbnails = new ArrayList<>();
        selectedKeyPassImage = dbConnection.getKeyPassImageForUser(userId);
        if(userId > 0){
            imageList = dbConnection.getPassImagesForUser(userId);
            for (int i=0; i < imageList.size(); i++){
                Image image = new Image(imageList.get(i));
                if(image.getImageUri().equals(selectedKeyPassImage)){

                    image.setColor(Color.RED);
                }
                Log.d("Image uri is the same", Boolean.toString(image.getImageUri().equals(selectedKeyPassImage)));
                thumbnails.add(image);
            }
        }
        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, thumbnails);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Image selectedImage = (Image) gridAdapter.getItem(position);
                selectedImage.toggleChecked();
                if(selectedImage.isChecked()){
                    //change bg color
                    selectedImage.setColor(Color.RED);
                    selectedKeyPassImage = selectedImage.getImageUri();
                }
                else{
                    selectedImage.setColor(Color.TRANSPARENT);
                    selectedKeyPassImage = null;
                }
                gridAdapter.notifyDataSetChanged();
            }
        });

    }
}
