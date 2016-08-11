package ohadcn.vasos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static private final int CODE_REQUEST_PERMISSION = 3456;
    static final int CODE_REQUEST_PERMISSION_IN_ACTION = 3356;

    public static SharedPreferences mPreferenceManager;
    public static MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        activity = this;

        ImageButton SOSbtn = (ImageButton)findViewById(R.id.sosbtn);
        SOSbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actions.call();
            }
        });

        ImageButton settingsbtn = (ImageButton)findViewById(R.id.menubtn);
        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        verifyPerms();
        verifyPhone();
        Actions.call();
    }


    private void verifyPhone() {
        String phoneNumber = mPreferenceManager.getString("contact_phone_number", null);
        if (phoneNumber == null) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Please set a phone number to send SMS to");
            dlgAlert.setTitle("Voice Activated SOS");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
        }
    }

    private void verifyPerms() {
        if (ActivityCompat.checkSelfPermission(MainActivity.activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.activity, Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS},
                        MainActivity.CODE_REQUEST_PERMISSION);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "failed", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case CODE_REQUEST_PERMISSION:
                verifyPhone();
                break;

            case CODE_REQUEST_PERMISSION_IN_ACTION:
                Actions.call();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
