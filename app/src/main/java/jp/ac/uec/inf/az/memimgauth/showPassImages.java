package jp.ac.uec.inf.az.memimgauth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.util.ArrayList;

public class showPassImages extends AppCompatActivity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<Image> thumbnails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_images);

        ArrayList<String> imageList;

        Bundle b = getIntent().getExtras();
        if(b!=null){
            imageList = b.getStringArrayList("passImages");
            thumbnails = new ArrayList<>();
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
