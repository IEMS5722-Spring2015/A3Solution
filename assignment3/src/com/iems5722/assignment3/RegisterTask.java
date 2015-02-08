package com.iems5722.assignment3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterTask extends AsyncTask<Void, Void, HttpResponse> {
	private static final String TAG = "RegisterTask";
	
	private String GCM_SENDER_ID = "145180457203";
	private String url = "http://iems5722v.ie.cuhk.edu.hk:8080/gcm_register.php";
	
	private MainActivity activity;
	private GoogleCloudMessaging gcm;
	
	public RegisterTask(MainActivity activity, GoogleCloudMessaging gcm) {
		this.activity = activity;
		this.gcm = gcm;
	}
	
	
	@Override
	protected HttpResponse doInBackground(Void... params) {
		// Register app with Google Cloud Messaging
		if (gcm == null) {
			gcm = GoogleCloudMessaging.getInstance(activity.context);
		}
		HttpResponse response = null;
		try {
			activity.regId = gcm.register(GCM_SENDER_ID);
			Log.d(TAG, "Device registered " + activity.regId);
			response = sendToBackend();
		}
		catch (IOException e) {
	    	Log.e(TAG, e.getMessage(), e);
	    }
		return response;
	}
	
	@Override
	protected void onPostExecute(HttpResponse response) {
		// If successfully registered with our server, then save to preferences
		if (response == null) {
			Log.e(TAG, "HttpResponse is null");
		   return;
		}
		StatusLine httpStatus = response.getStatusLine();
		if (httpStatus.getStatusCode() != 200) {
			Log.e(TAG, "Status: " + httpStatus.getStatusCode());
			return;
		}
		storeRegistrationId();
	}

	// Submit our token and student id to our server
	private HttpResponse sendToBackend() {
		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("sid", "10101010101"));
			postParams.add(new BasicNameValuePair("gcm_id", activity.regId));
			
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(postParams));
			HttpClient httpClient = new DefaultHttpClient();
			return httpClient.execute(httppost);
		} 
		catch (ClientProtocolException e) {
	        Log.e(TAG, e.getMessage(), e);
	    } catch (IOException e) {
	    	Log.e(TAG, e.getMessage(), e);
	    }
		return null;
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId() {
		Log.d(TAG, "Saving registration id");
	    final SharedPreferences prefs = activity.getGCMPreferences(activity.context);
	    int appVersion = activity.getAppVersion(activity.context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(activity.PROPERTY_REG_ID, activity.regId);
	    editor.putInt(activity.PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	    Log.d(TAG, "Registration Id is " + activity.regId);
	}
}

