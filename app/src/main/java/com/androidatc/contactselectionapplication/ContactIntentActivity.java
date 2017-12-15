package com.androidatc.contactselectionapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactIntentActivity extends AppCompatActivity {

    private List<ContactObject> contactsList;
    private int RUNTIME_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_intent);

        ListView intentListView = (ListView) findViewById(R.id.listView1);
        contactsList = new ArrayList<>();

        contactsList.add(new ContactObject("Android One","111-1111-1111",  "www.androidATC.com"));
        contactsList.add(new ContactObject("Android Two","222-2222-2222", "www.androidATC.com"));
        contactsList.add(new ContactObject("Android Three","333-3333-3333", "www.androidATC.com"));
        contactsList.add(new ContactObject("Android Four","444-4444-4444", "www.androidATC.com"));

        List<String> listName = new ArrayList<>();
        for (int i = 0; i < contactsList.size(); i++) {
            listName.add(contactsList.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(ContactIntentActivity.this,android.R.layout.simple_list_item_1,listName);
        intentListView.setAdapter(adapter);
        intentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ContactIntentActivity.this, ContactPageActivity.class);
                i.putExtra("Object", contactsList.get(position));
                startActivityForResult(i, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (data == null) {
            Bundle resultData = data.getExtras();
            String value = resultData.getString("Value");
            switch (resultCode){
                case Constants.PHONE:
                    makeCall(value);
                    break;

                case Constants.WEBSITE:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + value)));
                    break;
            }
        }
    }

    private void makeCall(String value) {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (result == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + value)));
        } else {
            requestCallPermission();
        }
    }

    private void requestCallPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
            explainPermissionDialog();
        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},RUNTIME_PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RUNTIME_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted now you can make the phone call", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void explainPermissionDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Call Permission Required");

        alertDialogBuilder.setMessage("This app requires Call permission to make calls. Please grant the permission")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

}
