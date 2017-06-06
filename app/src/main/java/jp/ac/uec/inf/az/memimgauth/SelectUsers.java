package jp.ac.uec.inf.az.memimgauth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SelectUsers extends AppCompatActivity {

    private DatabaseConnection dbConnection;
    ArrayAdapter<User> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = (ListView) findViewById(R.id.userlist);
        dbConnection = new DatabaseConnection(this);
        dbConnection.open();

        List<User> userList = dbConnection.getAllUsers();
        adapter = new MySimpleArrayAdapter(this, userList);
        listView.setAdapter(adapter);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.registeruser, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.registerUserButton:
               AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //LayoutInflater inflater = getLayoutInflater();
                LayoutInflater inflater =  LayoutInflater.from(getApplicationContext());
                builder.setTitle("Register new User");
                final View view = inflater.inflate(R.layout.registeruser, null);
                final EditText input = (EditText) view.findViewById(R.id.usernameInput);
                builder.setView(view);
                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String userinput = input.getText().toString();
                        if(!userinput.isEmpty()){
                            dbConnection.createUser(userinput);
                            adapter.clear();
                            adapter.addAll(dbConnection.getAllUsers());
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),
                                    "User " + userinput + " created.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                //AlertDialog dialog = builder.create();
                //dialog.show();
                builder.show();
        }
        return super.onOptionsItemSelected(item);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.userlist_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.username);
            textView.setText(users.get(position).getName());
            //handle the deletion of a user
            Button deleteButton = (Button) rowView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete User");
                    builder.setMessage("Do you want to delete this user?");
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dbConnection.deleteUser(users.get(position));
                            clear();
                            addAll(dbConnection.getAllUsers());
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder.show();
                }
            });
            //handle the login
            Button userLoginButton = (Button) rowView.findViewById(R.id.loginButton);
            userLoginButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("userId", users.get(position).getId());
                    startActivity(intent);
                }
            });

            return rowView;
        }



    }
}
