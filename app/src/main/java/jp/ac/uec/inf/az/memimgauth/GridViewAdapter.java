package jp.ac.uec.inf.az.memimgauth;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by azlabadm on 05.04.2017.
 */

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        setBitmap(holder.image, position, row);
        return row;
    }

    private void setBitmap(final ImageView iv, final int position, final View row) {
        new AsyncTask<Void, Void, Image>() {
            @Override
            protected Image doInBackground(Void... params) {
                Image image = (Image) data.get(position);
                return image;
            }

            @Override
            protected void onPostExecute(Image result) {
                super.onPostExecute(result);
                Picasso.with(getContext())
                        .load(result.getImageUri())
                        .placeholder(R.raw.loading)
                        .error(R.raw.error)
                        .noFade()
                        .resize(150, 150)
                        .centerCrop()
                        .into(iv);
                if(result.isChecked()){
                    row.setBackgroundColor(Color.BLACK);
                }
                else {
                    row.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }.execute();
    }

    static class ViewHolder {
        ImageView image;
    }
}