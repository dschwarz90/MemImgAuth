package jp.ac.uec.inf.az.memimgauth;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SelectUsers extends AppCompatActivity {

    private DatabaseConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.userlist);
        dbConnection = new DatabaseConnection(this);
        dbConnection.open();

        dbConnection.createUser("Daniel");
        final List<User> userList = dbConnection.getAllUsers();

        final ArrayAdapter<User> adapter = new MySimpleArrayAdapter(this, userList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
                dbConnection.deleteUser(userList.get(position));
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    protected void onResume() {
        dbConnection.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dbConnection.close();
        super.onPause();
    }

    public void registerUser(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.registeruser, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<User>{
        private final Context context;
        private final List<User> users;

        public MySimpleArrayAdapter(Context context, List<User> users) {
            super(context, -1, users);
            this.context = context;
            this.users = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.userlist_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.username);
            textView.setText(users.get(position).getName());

            return rowView;
        }
    }
}
