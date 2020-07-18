package com.geektime.rnonesignalandroid;

import android.util.Log;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationNotDisplayingExtender
  extends NotificationExtenderService
{
  public NotificationNotDisplayingExtender() {}
  
  protected boolean onNotificationProcessing(OSNotificationReceivedResult paramOSNotificationReceivedResult)
  {
    paramOSNotificationReceivedResult = payload.additionalData;
    try
    {
      boolean bool = paramOSNotificationReceivedResult.has("hidden");
      if (bool)
      {
        bool = paramOSNotificationReceivedResult.getBoolean("hidden");
        return bool;
      }
    }
    catch (JSONException paramOSNotificationReceivedResult)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("onNotificationProcessing Failure: ");
      localStringBuilder.append(paramOSNotificationReceivedResult.getMessage());
      Log.e("OneSignal", localStringBuilder.toString());
    }
    return false;
  }
}
