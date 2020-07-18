package com.geektime.rnonesignalandroid;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.BaseBundle;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.onesignal.OSEmailSubscriptionState;
import com.onesignal.OSInAppMessageAction;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSPermissionState;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OSSubscriptionState;
import com.onesignal.OneSignal;
import com.onesignal.OneSignal.Builder;
import com.onesignal.OneSignal.EmailUpdateError;
import com.onesignal.OneSignal.EmailUpdateHandler;
import com.onesignal.OneSignal.GetTagsHandler;
import com.onesignal.OneSignal.IdsAvailableHandler;
import com.onesignal.OneSignal.InAppMessageClickHandler;
import com.onesignal.OneSignal.NotificationOpenedHandler;
import com.onesignal.OneSignal.NotificationReceivedHandler;
import com.onesignal.OneSignal.OSExternalUserIdUpdateCompletionHandler;
import com.onesignal.OneSignal.OutcomeCallback;
import com.onesignal.OneSignal.PostNotificationResponseHandler;
import com.onesignal.OutcomeEvent;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RNOneSignal
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener, OneSignal.NotificationReceivedHandler, OneSignal.NotificationOpenedHandler, OneSignal.InAppMessageClickHandler
{
  public static final String HIDDEN_MESSAGE_KEY = "hidden";
  private OSNotificationOpenResult coldStartNotificationResult;
  private boolean hasSetInAppClickedHandler = false;
  private boolean hasSetNotificationOpenedHandler = false;
  private boolean hasSetRequiresPrivacyConsent = false;
  private OSInAppMessageAction inAppMessageActionResult;
  private ReactApplicationContext mReactApplicationContext;
  private ReactContext mReactContext;
  private boolean oneSignalInitDone;
  private Callback pendingGetTagsCallback;
  private boolean registeredEvents = false;
  private boolean waitingForUserPrivacyConsent = false;
  
  public RNOneSignal(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    mReactApplicationContext = paramReactApplicationContext;
    mReactContext = paramReactApplicationContext;
    mReactContext.addLifecycleEventListener(this);
    initOneSignal();
  }
  
  private String appIdFromManifest(ReactApplicationContext paramReactApplicationContext)
  {
    try
    {
      PackageManager localPackageManager = paramReactApplicationContext.getPackageManager();
      String str = paramReactApplicationContext.getPackageName();
      paramReactApplicationContext.getPackageManager();
      paramReactApplicationContext = getApplicationInfo128metaData.getString("onesignal_app_id");
      return paramReactApplicationContext;
    }
    catch (Throwable paramReactApplicationContext)
    {
      paramReactApplicationContext.printStackTrace();
    }
    return null;
  }
  
  private void initOneSignal()
  {
    OneSignal.sdkType = "react";
    String str = appIdFromManifest(mReactApplicationContext);
    if ((str != null) && (str.length() > 0)) {
      init(str);
    }
  }
  
  private JSONObject jsonFromErrorMessageString(String paramString)
    throws JSONException
  {
    return new JSONObject().put("error", paramString);
  }
  
  private void sendEvent(String paramString, Object paramObject)
  {
    ((DeviceEventManagerModule.RCTDeviceEventEmitter)mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(paramString, paramObject);
  }
  
  public void addTrigger(String paramString, Object paramObject)
  {
    OneSignal.addTrigger(paramString, paramObject);
  }
  
  public void addTriggers(ReadableMap paramReadableMap)
  {
    OneSignal.addTriggers(paramReadableMap.toHashMap());
  }
  
  public void cancelNotification(int paramInt)
  {
    OneSignal.cancelNotification(paramInt);
  }
  
  public void clearOneSignalNotifications() {}
  
  public void deleteTag(String paramString)
  {
    OneSignal.deleteTag(paramString);
  }
  
  public void enableSound(Boolean paramBoolean)
  {
    OneSignal.enableSound(paramBoolean.booleanValue());
  }
  
  public void enableVibrate(Boolean paramBoolean)
  {
    OneSignal.enableVibrate(paramBoolean.booleanValue());
  }
  
  public String getName()
  {
    return "OneSignal";
  }
  
  public void getPermissionSubscriptionState(Callback paramCallback)
  {
    Object localObject3 = OneSignal.getPermissionSubscriptionState();
    if (localObject3 == null) {
      return;
    }
    Object localObject2 = ((OSPermissionSubscriptionState)localObject3).getPermissionStatus();
    Object localObject1 = ((OSPermissionSubscriptionState)localObject3).getSubscriptionStatus();
    localObject3 = ((OSPermissionSubscriptionState)localObject3).getEmailSubscriptionStatus();
    boolean bool1 = ((OSPermissionState)localObject2).getEnabled();
    boolean bool2 = ((OSSubscriptionState)localObject1).getSubscribed();
    boolean bool3 = ((OSSubscriptionState)localObject1).getUserSubscriptionSetting();
    try
    {
      localObject2 = new JSONObject();
      ((JSONObject)localObject2).put("notificationsEnabled", bool1).put("subscriptionEnabled", bool2).put("userSubscriptionEnabled", bool3).put("pushToken", ((OSSubscriptionState)localObject1).getPushToken()).put("userId", ((OSSubscriptionState)localObject1).getUserId()).put("emailUserId", ((OSEmailSubscriptionState)localObject3).getEmailUserId()).put("emailAddress", ((OSEmailSubscriptionState)localObject3).getEmailAddress());
      localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("permission subscription state: ");
      ((StringBuilder)localObject1).append(((JSONObject)localObject2).toString());
      Log.d("onesignal", ((StringBuilder)localObject1).toString());
      localObject1 = RNUtils.jsonToWritableMap((JSONObject)localObject2);
      paramCallback.invoke(new Object[] { localObject1 });
      return;
    }
    catch (JSONException paramCallback)
    {
      paramCallback.printStackTrace();
    }
  }
  
  public void getTags(Callback paramCallback)
  {
    if (pendingGetTagsCallback == null) {
      pendingGetTagsCallback = paramCallback;
    }
    OneSignal.getTags(new OneSignal.GetTagsHandler()
    {
      public void tagsAvailable(JSONObject paramAnonymousJSONObject)
      {
        if (pendingGetTagsCallback != null) {
          pendingGetTagsCallback.invoke(new Object[] { RNUtils.jsonToWritableMap(paramAnonymousJSONObject) });
        }
        RNOneSignal.access$002(RNOneSignal.this, null);
      }
    });
  }
  
  public void getTriggerValueForKey(String paramString, Promise paramPromise)
  {
    paramPromise.resolve(OneSignal.getTriggerValueForKey(paramString));
  }
  
  public void idsAvailable()
  {
    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler()
    {
      public void idsAvailable(String paramAnonymousString1, String paramAnonymousString2)
      {
        WritableMap localWritableMap = Arguments.createMap();
        localWritableMap.putString("userId", paramAnonymousString1);
        localWritableMap.putString("pushToken", paramAnonymousString2);
        RNOneSignal.this.sendEvent("OneSignal-idsAvailable", localWritableMap);
      }
    });
  }
  
  public void inAppMessageClicked(OSInAppMessageAction paramOSInAppMessageAction)
  {
    if (!hasSetInAppClickedHandler)
    {
      inAppMessageActionResult = paramOSInAppMessageAction;
      return;
    }
    sendEvent("OneSignal-inAppMessageClicked", RNUtils.jsonToWritableMap(paramOSInAppMessageAction.toJSONObject()));
  }
  
  public void inFocusDisplaying(int paramInt)
  {
    OneSignal.setInFocusDisplaying(paramInt);
  }
  
  public void init(String paramString)
  {
    Activity localActivity = mReactApplicationContext.getCurrentActivity();
    Object localObject = localActivity;
    if (oneSignalInitDone)
    {
      Log.e("onesignal", "Already initialized the OneSignal React-Native SDK");
      return;
    }
    oneSignalInitDone = true;
    OneSignal.sdkType = "react";
    if (localActivity == null) {
      localObject = mReactApplicationContext.getApplicationContext();
    }
    OneSignal.getCurrentOrNewInitBuilder().setInAppMessageClickHandler(this);
    OneSignal.init((Context)localObject, null, paramString, this, this);
    if (hasSetRequiresPrivacyConsent) {
      waitingForUserPrivacyConsent = true;
    }
  }
  
  public void initInAppMessageClickHandlerParams()
  {
    hasSetInAppClickedHandler = true;
    if (inAppMessageActionResult != null)
    {
      inAppMessageClicked(inAppMessageActionResult);
      inAppMessageActionResult = null;
    }
  }
  
  public void initNotificationOpenedHandlerParams()
  {
    hasSetNotificationOpenedHandler = true;
    if (coldStartNotificationResult != null)
    {
      notificationOpened(coldStartNotificationResult);
      coldStartNotificationResult = null;
    }
  }
  
  public void logoutEmail(final Callback paramCallback)
  {
    OneSignal.logoutEmail(new OneSignal.EmailUpdateHandler()
    {
      public void onFailure(OneSignal.EmailUpdateError paramAnonymousEmailUpdateError)
      {
        Callback localCallback = paramCallback;
        RNOneSignal localRNOneSignal = RNOneSignal.this;
        try
        {
          paramAnonymousEmailUpdateError = RNUtils.jsonToWritableMap(localRNOneSignal.jsonFromErrorMessageString(paramAnonymousEmailUpdateError.getMessage()));
          localCallback.invoke(new Object[] { paramAnonymousEmailUpdateError });
          return;
        }
        catch (JSONException paramAnonymousEmailUpdateError)
        {
          paramAnonymousEmailUpdateError.printStackTrace();
        }
      }
      
      public void onSuccess()
      {
        paramCallback.invoke(new Object[0]);
      }
    });
  }
  
  public void notificationOpened(OSNotificationOpenResult paramOSNotificationOpenResult)
  {
    if (!hasSetNotificationOpenedHandler)
    {
      coldStartNotificationResult = paramOSNotificationOpenResult;
      return;
    }
    sendEvent("OneSignal-remoteNotificationOpened", RNUtils.jsonToWritableMap(paramOSNotificationOpenResult.toJSONObject()));
  }
  
  public void notificationReceived(OSNotification paramOSNotification)
  {
    sendEvent("OneSignal-remoteNotificationReceived", RNUtils.jsonToWritableMap(paramOSNotification.toJSONObject()));
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause() {}
  
  public void onHostResume()
  {
    initOneSignal();
  }
  
  public void pauseInAppMessages(Boolean paramBoolean)
  {
    OneSignal.pauseInAppMessages(paramBoolean.booleanValue());
  }
  
  public void postNotification(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    try
    {
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("contents", new JSONObject(paramString1));
      if (paramString3 != null) {
        localJSONObject.put("include_player_ids", new JSONArray(paramString3));
      }
      if (paramString2 != null)
      {
        paramString1 = new JSONObject();
        paramString1.put("p2p_notification", new JSONObject(paramString2));
        localJSONObject.put("data", paramString1);
      }
      if (paramString4 != null)
      {
        boolean bool = paramString4.trim().isEmpty();
        if (!bool)
        {
          paramString1 = new JSONObject(paramString4.trim());
          paramString2 = paramString1.keys();
          for (;;)
          {
            bool = paramString2.hasNext();
            if (!bool) {
              break;
            }
            paramString3 = paramString2.next();
            paramString3 = (String)paramString3;
            localJSONObject.put(paramString3, paramString1.get(paramString3));
          }
          bool = paramString1.has("hidden");
          if (bool)
          {
            bool = paramString1.getBoolean("hidden");
            if (bool) {
              localJSONObject.getJSONObject("data").put("hidden", true);
            }
          }
        }
      }
      OneSignal.postNotification(localJSONObject, new OneSignal.PostNotificationResponseHandler()
      {
        public void onFailure(JSONObject paramAnonymousJSONObject)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("postNotification Failure: ");
          localStringBuilder.append(paramAnonymousJSONObject.toString());
          Log.e("OneSignal", localStringBuilder.toString());
        }
        
        public void onSuccess(JSONObject paramAnonymousJSONObject)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("postNotification Success: ");
          localStringBuilder.append(paramAnonymousJSONObject.toString());
          Log.i("OneSignal", localStringBuilder.toString());
        }
      });
      return;
    }
    catch (JSONException paramString1)
    {
      paramString1.printStackTrace();
    }
  }
  
  public void promptLocation() {}
  
  public void provideUserConsent(Boolean paramBoolean)
  {
    OneSignal.provideUserConsent(paramBoolean.booleanValue());
  }
  
  public void removeExternalUserId(final Callback paramCallback)
  {
    OneSignal.removeExternalUserId(new OneSignal.OSExternalUserIdUpdateCompletionHandler()
    {
      public void onComplete(JSONObject paramAnonymousJSONObject)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Completed removing external user id with results: ");
        localStringBuilder.append(paramAnonymousJSONObject.toString());
        Log.i("OneSignal", localStringBuilder.toString());
        if (paramCallback != null) {
          paramCallback.invoke(new Object[] { RNUtils.jsonToWritableMap(paramAnonymousJSONObject) });
        }
      }
    });
  }
  
  public void removeTriggerForKey(String paramString)
  {
    OneSignal.removeTriggerForKey(paramString);
  }
  
  public void removeTriggersForKeys(ReadableArray paramReadableArray)
  {
    OneSignal.removeTriggersForKeys(RNUtils.convertReableArrayIntoStringCollection(paramReadableArray));
  }
  
  public void sendOutcome(final String paramString, final Callback paramCallback)
  {
    OneSignal.sendOutcome(paramString, new OneSignal.OutcomeCallback()
    {
      public void onSuccess(OutcomeEvent paramAnonymousOutcomeEvent)
      {
        if (paramAnonymousOutcomeEvent == null)
        {
          paramCallback.invoke(new Object[] { new WritableNativeMap() });
          return;
        }
        Object localObject = paramCallback;
        try
        {
          paramAnonymousOutcomeEvent = RNUtils.jsonToWritableMap(paramAnonymousOutcomeEvent.toJSONObject());
          ((Callback)localObject).invoke(new Object[] { paramAnonymousOutcomeEvent });
          return;
        }
        catch (JSONException paramAnonymousOutcomeEvent)
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("sendOutcome with name: ");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(", failed with message: ");
          ((StringBuilder)localObject).append(paramAnonymousOutcomeEvent.getMessage());
          Log.e("OneSignal", ((StringBuilder)localObject).toString());
        }
      }
    });
  }
  
  public void sendOutcomeWithValue(final String paramString, final float paramFloat, final Callback paramCallback)
  {
    OneSignal.sendOutcomeWithValue(paramString, paramFloat, new OneSignal.OutcomeCallback()
    {
      public void onSuccess(OutcomeEvent paramAnonymousOutcomeEvent)
      {
        if (paramAnonymousOutcomeEvent == null)
        {
          paramCallback.invoke(new Object[] { new WritableNativeMap() });
          return;
        }
        Object localObject = paramCallback;
        try
        {
          paramAnonymousOutcomeEvent = RNUtils.jsonToWritableMap(paramAnonymousOutcomeEvent.toJSONObject());
          ((Callback)localObject).invoke(new Object[] { paramAnonymousOutcomeEvent });
          return;
        }
        catch (JSONException paramAnonymousOutcomeEvent)
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("sendOutcomeWithValue with name: ");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(" and value: ");
          ((StringBuilder)localObject).append(paramFloat);
          ((StringBuilder)localObject).append(", failed with message: ");
          ((StringBuilder)localObject).append(paramAnonymousOutcomeEvent.getMessage());
          Log.e("OneSignal", ((StringBuilder)localObject).toString());
        }
      }
    });
  }
  
  public void sendTag(String paramString1, String paramString2)
  {
    OneSignal.sendTag(paramString1, paramString2);
  }
  
  public void sendTags(ReadableMap paramReadableMap)
  {
    OneSignal.sendTags(RNUtils.readableMapToJson(paramReadableMap));
  }
  
  public void sendUniqueOutcome(final String paramString, final Callback paramCallback)
  {
    OneSignal.sendUniqueOutcome(paramString, new OneSignal.OutcomeCallback()
    {
      public void onSuccess(OutcomeEvent paramAnonymousOutcomeEvent)
      {
        if (paramAnonymousOutcomeEvent == null)
        {
          paramCallback.invoke(new Object[] { new WritableNativeMap() });
          return;
        }
        Object localObject = paramCallback;
        try
        {
          paramAnonymousOutcomeEvent = RNUtils.jsonToWritableMap(paramAnonymousOutcomeEvent.toJSONObject());
          ((Callback)localObject).invoke(new Object[] { paramAnonymousOutcomeEvent });
          return;
        }
        catch (JSONException paramAnonymousOutcomeEvent)
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("sendUniqueOutcome with name: ");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(", failed with message: ");
          ((StringBuilder)localObject).append(paramAnonymousOutcomeEvent.getMessage());
          Log.e("OneSignal", ((StringBuilder)localObject).toString());
        }
      }
    });
  }
  
  public void setEmail(String paramString1, String paramString2, final Callback paramCallback)
  {
    OneSignal.setEmail(paramString1, paramString2, new OneSignal.EmailUpdateHandler()
    {
      public void onFailure(OneSignal.EmailUpdateError paramAnonymousEmailUpdateError)
      {
        Callback localCallback = paramCallback;
        RNOneSignal localRNOneSignal = RNOneSignal.this;
        try
        {
          paramAnonymousEmailUpdateError = RNUtils.jsonToWritableMap(localRNOneSignal.jsonFromErrorMessageString(paramAnonymousEmailUpdateError.getMessage()));
          localCallback.invoke(new Object[] { paramAnonymousEmailUpdateError });
          return;
        }
        catch (JSONException paramAnonymousEmailUpdateError)
        {
          paramAnonymousEmailUpdateError.printStackTrace();
        }
      }
      
      public void onSuccess()
      {
        paramCallback.invoke(new Object[0]);
      }
    });
  }
  
  public void setExternalUserId(final String paramString, final Callback paramCallback)
  {
    OneSignal.setExternalUserId(paramString, new OneSignal.OSExternalUserIdUpdateCompletionHandler()
    {
      public void onComplete(JSONObject paramAnonymousJSONObject)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Completed setting external user id: ");
        localStringBuilder.append(paramString);
        localStringBuilder.append("with results: ");
        localStringBuilder.append(paramAnonymousJSONObject.toString());
        Log.i("OneSignal", localStringBuilder.toString());
        if (paramCallback != null) {
          paramCallback.invoke(new Object[] { RNUtils.jsonToWritableMap(paramAnonymousJSONObject) });
        }
      }
    });
  }
  
  public void setLocationShared(Boolean paramBoolean)
  {
    OneSignal.setLocationShared(paramBoolean.booleanValue());
  }
  
  public void setLogLevel(int paramInt1, int paramInt2)
  {
    OneSignal.setLogLevel(paramInt1, paramInt2);
  }
  
  public void setRequiresUserPrivacyConsent(Boolean paramBoolean)
  {
    OneSignal.setRequiresUserPrivacyConsent(paramBoolean.booleanValue());
  }
  
  public void setSubscription(Boolean paramBoolean)
  {
    OneSignal.setSubscription(paramBoolean.booleanValue());
  }
  
  public void syncHashedEmail(String paramString)
  {
    OneSignal.syncHashedEmail(paramString);
  }
  
  public void userProvidedPrivacyConsent(Promise paramPromise)
  {
    paramPromise.resolve(Boolean.valueOf(OneSignal.userProvidedPrivacyConsent()));
  }
}
