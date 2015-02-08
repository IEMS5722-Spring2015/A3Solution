package com.iems5722.assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class RetrieveDataTask extends AsyncTask<String, Void, HttpResponse> {
	private static final String TAG = "RetrieveDataTask";
	
	private MainActivity activity;
	
	public RetrieveDataTask(MainActivity activity) {
		this.activity = activity;
	}
	
	@Override
	protected HttpResponse doInBackground(String... params) {
		String url = params[0];
		
		HttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			HttpClient httpClient = new DefaultHttpClient();
			response = httpClient.execute(httppost);
		}
		catch (ClientProtocolException e) {
	        Log.e(TAG, e.getMessage(), e);
	    } catch (IOException e) {
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
		JSONArray jsonArray = processResponse(response);
		activity.dataList.clear();
		for(int i = 0; i < jsonArray.length(); i ++) {
			try {
				JSONObject row = jsonArray.getJSONObject(i);
				if (row.has("title") && row.has("desc") && row.has("image")) {
					DisplayObject dispObj = new DisplayObject(row.getString("title"), row.getString("desc"), row.getString("image"));
					activity.dataList.add(dispObj);
				}
			} catch (JSONException e) {
				Log.e(TAG, "Failed to get JSON object from array");
				e.printStackTrace();
			}
		}

		activity.mAdapter.notifyDataSetChanged();
	}

	private JSONArray processResponse(HttpResponse response) {
		try {
			InputStream inStream = response.getEntity().getContent();
			InputStreamReader insReader = new InputStreamReader(inStream);
			BufferedReader bReader = new BufferedReader(insReader);
			StringBuilder strBld = new StringBuilder();
			
			String strChunk = null;
			while((strChunk = bReader.readLine()) != null) {
				strBld.append(strChunk);
				strBld.append("\n");
			}
			insReader.close();
			String content = strBld.toString();
			return new JSONArray(content);

		} catch (IllegalStateException e) {
			Log.e(TAG, "IllegalStateException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException");
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(TAG, "Error converting to JSON");
			e.printStackTrace();
		}
		return null;
	}
}
