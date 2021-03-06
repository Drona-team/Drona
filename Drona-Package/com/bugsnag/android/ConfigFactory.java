package com.bugsnag.android;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.BaseBundle;
import android.os.Bundle;
import android.text.TextUtils;

class ConfigFactory
{
  private static final String BUGSNAG_NAMESPACE = "com.bugsnag.android";
  private static final String MF_API_KEY = "com.bugsnag.android.API_KEY";
  private static final String MF_APP_VERSION = "com.bugsnag.android.APP_VERSION";
  private static final String MF_AUTO_CAPTURE_SESSIONS = "com.bugsnag.android.AUTO_CAPTURE_SESSIONS";
  static final String MF_BUILD_UUID = "com.bugsnag.android.BUILD_UUID";
  private static final String MF_DETECT_ANRS = "com.bugsnag.android.DETECT_ANRS";
  private static final String MF_DETECT_NDK_CRASHES = "com.bugsnag.android.DETECT_NDK_CRASHES";
  private static final String MF_ENABLE_EXCEPTION_HANDLER = "com.bugsnag.android.ENABLE_EXCEPTION_HANDLER";
  private static final String MF_ENDPOINT = "com.bugsnag.android.ENDPOINT";
  private static final String MF_PERSIST_USER_BETWEEN_SESSIONS = "com.bugsnag.android.PERSIST_USER_BETWEEN_SESSIONS";
  private static final String MF_RELEASE_STAGE = "com.bugsnag.android.RELEASE_STAGE";
  private static final String MF_SEND_THREADS = "com.bugsnag.android.SEND_THREADS";
  private static final String MF_SESSIONS_ENDPOINT = "com.bugsnag.android.SESSIONS_ENDPOINT";
  private static final String MF_VERSION_CODE = "com.bugsnag.android.VERSION_CODE";
  
  ConfigFactory() {}
  
  static Configuration createNewConfiguration(Context paramContext, String paramString, boolean paramBoolean)
  {
    Context localContext = paramContext.getApplicationContext();
    boolean bool = TextUtils.isEmpty(paramString);
    paramContext = paramString;
    if (bool) {}
    try
    {
      paramContext = localContext.getPackageManager().getApplicationInfo(localContext.getPackageName(), 128);
      paramContext = metaData;
      paramContext = paramContext.getString("com.bugsnag.android.API_KEY");
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }
    Logger.warn("Bugsnag is unable to read api key from manifest.");
    paramContext = paramString;
    if (paramContext != null)
    {
      paramContext = new Configuration(paramContext);
      paramContext.setEnableExceptionHandler(paramBoolean);
      if (!bool) {}
    }
    else
    {
      try
      {
        paramString = localContext.getPackageManager().getApplicationInfo(localContext.getPackageName(), 128);
        paramString = metaData;
        populateConfigFromManifest(paramContext, paramString);
        return paramContext;
      }
      catch (Exception paramString)
      {
        for (;;) {}
      }
      Logger.warn("Bugsnag is unable to read config from manifest.");
      return paramContext;
      throw new NullPointerException("You must provide a Bugsnag API key");
    }
    return paramContext;
  }
  
  static void populateConfigFromManifest(Configuration paramConfiguration, Bundle paramBundle)
  {
    paramConfiguration.setBuildUUID(paramBundle.getString("com.bugsnag.android.BUILD_UUID"));
    paramConfiguration.setAppVersion(paramBundle.getString("com.bugsnag.android.APP_VERSION"));
    paramConfiguration.setReleaseStage(paramBundle.getString("com.bugsnag.android.RELEASE_STAGE"));
    if (paramBundle.containsKey("com.bugsnag.android.VERSION_CODE")) {
      paramConfiguration.setVersionCode(Integer.valueOf(paramBundle.getInt("com.bugsnag.android.VERSION_CODE")));
    }
    if (paramBundle.containsKey("com.bugsnag.android.ENDPOINT")) {
      paramConfiguration.setEndpoints(paramBundle.getString("com.bugsnag.android.ENDPOINT"), paramBundle.getString("com.bugsnag.android.SESSIONS_ENDPOINT"));
    }
    paramConfiguration.setSendThreads(paramBundle.getBoolean("com.bugsnag.android.SEND_THREADS", true));
    paramConfiguration.setPersistUserBetweenSessions(paramBundle.getBoolean("com.bugsnag.android.PERSIST_USER_BETWEEN_SESSIONS", false));
    if (paramBundle.containsKey("com.bugsnag.android.DETECT_NDK_CRASHES")) {
      paramConfiguration.setDetectNdkCrashes(paramBundle.getBoolean("com.bugsnag.android.DETECT_NDK_CRASHES"));
    }
    if (paramBundle.containsKey("com.bugsnag.android.DETECT_ANRS")) {
      paramConfiguration.setDetectAnrs(paramBundle.getBoolean("com.bugsnag.android.DETECT_ANRS"));
    }
    if (paramBundle.containsKey("com.bugsnag.android.AUTO_CAPTURE_SESSIONS")) {
      paramConfiguration.setAutoCaptureSessions(paramBundle.getBoolean("com.bugsnag.android.AUTO_CAPTURE_SESSIONS"));
    }
    paramConfiguration.setEnableExceptionHandler(paramBundle.getBoolean("com.bugsnag.android.ENABLE_EXCEPTION_HANDLER", true));
  }
}
