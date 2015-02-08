package com.iems5722.assignment3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

// Actually handles the message
public class GcmIntentService extends IntentService {
	private static final String TAG = "GcmIntentService";
	
	public static final int NOTIFICATION_ID = 5722;
	
	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling intent");
		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
		
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	createNotification(extras);
            }
            else {
            	Log.i(TAG, "Unexpected msg type - " + messageType);
            }
        }

     // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void createNotification(Bundle extras) {
		String title = extras.getString("title");
		String url = extras.getString("url");
		String desc = extras.getString("desc");
		Log.d(TAG, "Creating notfication " + title + " " + desc + " " + url);
		
		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra("url", url);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
												.setSmallIcon(R.drawable.ic_launcher)
										        .setContentTitle(title)
										        .setContentText(desc)
												.setLights(Color.YELLOW, 500, 300);
		mBuilder.setContentIntent(resultPendingIntent);
		
		Notification notification = mBuilder.build();
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
}
