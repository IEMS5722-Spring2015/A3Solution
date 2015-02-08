package com.iems5722.assignment3;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	Context context;
	
	ResultListAdapter mAdapter;
	ListView list;
	List<DisplayObject> dataList;
	
	// Google Play Services
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	// Google Cloud Messaging
	GoogleCloudMessaging gcm;
	final String GCM_SENDER_ID = "145180457203";
	// Registration Id stored in SharedPreferences
	String regId;
	// SharedPreference keys
	final String PROPERTY_REG_ID = "registration_id";
	final String PROPERTY_APP_VERSION = "appVersion";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    context = getApplicationContext();

	    // Check device for Play Services APK.
	    if (checkPlayServices()) {
	        // If this check succeeds, proceed with normal processing.
	        // Otherwise, prompt user to get valid Play Services APK.
	    	gcm = GoogleCloudMessaging.getInstance(this);
	    	regId = getRegistrationId(context);
            if (regId.isEmpty()) {
            	// Register with Google Cloud Messaging and our server
                RegisterTask register = new RegisterTask(this, gcm);
                register.execute();
            }	    	
	    }
	    
	    // Set up view
	    setContentView(R.layout.activity_main);
	    list = (ListView) this.findViewById(R.id.mainList);
	    
	    // Get data from GcmIntentService if app started from intent
	    dataList = new ArrayList<DisplayObject>();
	    mAdapter = new ResultListAdapter(this, dataList);
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	    	if (extras.containsKey("url")) {
		    	Toast.makeText(this, "Please wait, retrieving data", Toast.LENGTH_SHORT).show();
		    	RetrieveDataTask rdTask = new RetrieveDataTask(this);
		    	rdTask.execute(extras.getString("url"));
	    	}
	    }
	    list.setAdapter(mAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Check Google Play Services is available
		checkPlayServices();
	}	
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	SharedPreferences getGCMPreferences(Context context) {
	    // Store registration id in shared preferences
	    return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}	
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}	
}
