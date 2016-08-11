package ohadcn.vasos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by ohad on 11/08/16.
 */
public class Actions {

    public static void call() {
        (new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String phoneNumber = MainActivity.mPreferenceManager.getString("contact_phone_number", null);
                String myName = MainActivity.mPreferenceManager.getString("my_name", null);
                boolean addLocation = MainActivity.mPreferenceManager.getBoolean("sen_location", true);
                String msg = formatMessage(myName, addLocation);
                publishProgress("got location, sending message...");
                sendSms(phoneNumber, msg);
                publishProgress("sent");
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                Toast.makeText(MainActivity.activity, values[0], Toast.LENGTH_LONG).show();
            }
        }).execute();
    }

    public static String formatMessage(String name, boolean addLocation) {
        String res = "I'm in troubles and needs your help...";
        if (addLocation) {
            LocationManager locationManager = (LocationManager)
                    MainActivity.activity.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(true);
                criteria.setBearingRequired(true);
                criteria.setSpeedRequired(true);

            }
            String provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(MainActivity.activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MainActivity.activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, MainActivity.CODE_REQUEST_PERMISSION_IN_ACTION);
                }
                return res;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            String locStr = String.format("%.7f", location.getLatitude()) + "," +
                    String.format("%.6f", location.getLongitude());
            res += "\nto get my location click here https://www.google.com/maps/place/" + locStr;
//                    "(" +(name + " location").replace(' ', '+') + ")" +
//                    " or here geo:"+locStr +
//                    "?q=" + locStr + "(" + (name + " location").replace(' ', '+') + ")";
        }

        res += "\n" + name;
        return res;
    }






    public static void sendSms(String phone, String text) {
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.activity, Manifest.permission.SEND_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        MainActivity.activity.requestPermissions(new String[]{
                                Manifest.permission.SEND_SMS}, MainActivity.CODE_REQUEST_PERMISSION_IN_ACTION);
                }
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendMultipartTextMessage(phone, null,
                    smsManager.divideMessage(text), null, null);
//            smsManager.sendTextMessage(phone, null, text, null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidPhoneNumber(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            if (target.length() < 6 || target.length() > 13) {
                return false;
            } else {
                return android.util.Patterns.PHONE.matcher(target).matches();
            }
        }
    }
}
