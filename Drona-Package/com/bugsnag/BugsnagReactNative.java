package com.bugsnag;

import android.content.Context;
import com.bugsnag.android.BreadcrumbType;
import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Callback;
import com.bugsnag.android.Client;
import com.bugsnag.android.Configuration;
import com.bugsnag.android.InternalHooks;
import com.bugsnag.android.JavaScriptException;
import com.bugsnag.android.Notifier;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BugsnagReactNative
  extends ReactContextBaseJavaModule
{
  public static final Logger logger = Logger.getLogger("bugsnag-react-native");
  private String bugsnagAndroidVersion;
  private String libraryVersion;
  private ReactContext reactContext;
  
  public BugsnagReactNative(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    reactContext = paramReactApplicationContext;
    libraryVersion = null;
    bugsnagAndroidVersion = null;
  }
  
  private void configureRuntimeOptions(Client paramClient, ReadableMap paramReadableMap)
  {
    paramClient.setIgnoreClasses(new String[] { "com.facebook.react.common.JavascriptException" });
    Configuration localConfiguration = paramClient.getConfig();
    Object localObject1;
    if (paramReadableMap.hasKey("appVersion"))
    {
      localObject1 = paramReadableMap.getString("appVersion");
      if ((localObject1 != null) && (((String)localObject1).length() > 0)) {
        paramClient.setAppVersion((String)localObject1);
      }
    }
    boolean bool = paramReadableMap.hasKey("endpoint");
    Object localObject2 = null;
    if (bool) {
      localObject1 = paramReadableMap.getString("endpoint");
    } else {
      localObject1 = null;
    }
    if (paramReadableMap.hasKey("sessionsEndpoint")) {
      localObject2 = paramReadableMap.getString("sessionsEndpoint");
    }
    if ((localObject1 != null) && (((String)localObject1).length() > 0)) {
      localConfiguration.setEndpoints((String)localObject1, (String)localObject2);
    } else if ((localObject2 != null) && (((String)localObject2).length() > 0)) {
      logger.warning("The session tracking endpoint should not be set without the error reporting endpoint.");
    }
    if (paramReadableMap.hasKey("releaseStage"))
    {
      localObject1 = paramReadableMap.getString("releaseStage");
      if ((localObject1 != null) && (((String)localObject1).length() > 0)) {
        paramClient.setReleaseStage((String)localObject1);
      }
    }
    if (paramReadableMap.hasKey("autoNotify")) {
      if (paramReadableMap.getBoolean("autoNotify")) {
        paramClient.enableExceptionHandler();
      } else {
        paramClient.disableExceptionHandler();
      }
    }
    if (paramReadableMap.hasKey("codeBundleId"))
    {
      localObject1 = paramReadableMap.getString("codeBundleId");
      if ((localObject1 != null) && (((String)localObject1).length() > 0)) {
        paramClient.addToTab("app", "codeBundleId", localObject1);
      }
    }
    if (paramReadableMap.hasKey("notifyReleaseStages"))
    {
      localObject1 = paramReadableMap.getArray("notifyReleaseStages");
      if ((localObject1 != null) && (((ReadableArray)localObject1).size() > 0))
      {
        localObject2 = new String[((ReadableArray)localObject1).size()];
        int i = 0;
        while (i < ((ReadableArray)localObject1).size())
        {
          localObject2[i] = ((ReadableArray)localObject1).getString(i);
          i += 1;
        }
        paramClient.setNotifyReleaseStages((String[])localObject2);
      }
    }
    if (paramReadableMap.hasKey("automaticallyCollectBreadcrumbs")) {
      localConfiguration.setAutomaticallyCollectBreadcrumbs(paramReadableMap.getBoolean("automaticallyCollectBreadcrumbs"));
    }
    if (paramReadableMap.hasKey("autoCaptureSessions"))
    {
      bool = paramReadableMap.getBoolean("autoCaptureSessions");
      localConfiguration.setAutoCaptureSessions(bool);
      if (bool) {
        paramClient.resumeSession();
      }
    }
  }
  
  private Client getClient(String paramString)
  {
    try
    {
      Client localClient = Bugsnag.getClient();
      return localClient;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
    if (paramString != null) {
      return Bugsnag.init(reactContext, paramString);
    }
    return Bugsnag.init(reactContext);
  }
  
  public static ReactPackage getPackage()
  {
    return new BugsnagPackage();
  }
  
  private BreadcrumbType parseBreadcrumbType(String paramString)
  {
    BreadcrumbType[] arrayOfBreadcrumbType = BreadcrumbType.values();
    int j = arrayOfBreadcrumbType.length;
    int i = 0;
    while (i < j)
    {
      BreadcrumbType localBreadcrumbType = arrayOfBreadcrumbType[i];
      if (localBreadcrumbType.toString().equals(paramString)) {
        return localBreadcrumbType;
      }
      i += 1;
    }
    return BreadcrumbType.MANUAL;
  }
  
  private Map readStringMap(ReadableMap paramReadableMap)
  {
    HashMap localHashMap = new HashMap();
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    while (localReadableMapKeySetIterator.hasNextKey())
    {
      String str1 = localReadableMapKeySetIterator.nextKey();
      ReadableMap localReadableMap = paramReadableMap.getMap(str1);
      String str2 = localReadableMap.getString("type");
      int i = -1;
      int j = str2.hashCode();
      if (j != -1034364087)
      {
        if (j != -891985903)
        {
          if (j != 107868)
          {
            if ((j == 64711720) && (str2.equals("boolean"))) {
              i = 0;
            }
          }
          else if (str2.equals("map")) {
            i = 3;
          }
        }
        else if (str2.equals("string")) {
          i = 2;
        }
      }
      else if (str2.equals("number")) {
        i = 1;
      }
      switch (i)
      {
      default: 
        break;
      case 3: 
        localHashMap.put(str1, String.valueOf(readStringMap(localReadableMap.getMap("value"))));
        break;
      case 2: 
        localHashMap.put(str1, localReadableMap.getString("value"));
        break;
      case 1: 
        localHashMap.put(str1, String.valueOf(localReadableMap.getDouble("value")));
        break;
      case 0: 
        localHashMap.put(str1, String.valueOf(localReadableMap.getBoolean("value")));
      }
    }
    return localHashMap;
  }
  
  public static Client start(Context paramContext)
  {
    paramContext = Bugsnag.init(paramContext);
    paramContext.setAutoCaptureSessions(false);
    return paramContext;
  }
  
  public static Client startWithApiKey(Context paramContext, String paramString)
  {
    paramContext = Bugsnag.init(paramContext, paramString);
    paramContext.setAutoCaptureSessions(false);
    return paramContext;
  }
  
  public static Client startWithConfiguration(Context paramContext, Configuration paramConfiguration)
  {
    paramConfiguration.setAutoCaptureSessions(false);
    return Bugsnag.init(paramContext, paramConfiguration);
  }
  
  public void clearUser() {}
  
  public String getName()
  {
    return "BugsnagReactNative";
  }
  
  public void leaveBreadcrumb(ReadableMap paramReadableMap)
  {
    Bugsnag.leaveBreadcrumb(paramReadableMap.getString("name"), parseBreadcrumbType(paramReadableMap.getString("type")), readStringMap(paramReadableMap.getMap("metadata")));
  }
  
  public void notify(ReadableMap paramReadableMap, Promise paramPromise)
  {
    if (!paramReadableMap.hasKey("errorClass"))
    {
      logger.warning("Bugsnag could not notify: No error class");
      return;
    }
    if (!paramReadableMap.hasKey("stacktrace"))
    {
      logger.warning("Bugsnag could not notify: No stacktrace");
      return;
    }
    Object localObject1 = paramReadableMap.getString("errorClass");
    Object localObject2 = paramReadableMap.getString("errorMessage");
    Object localObject3 = paramReadableMap.getString("stacktrace");
    Object localObject4 = logger;
    boolean bool2 = false;
    ((Logger)localObject4).info(String.format("Sending exception: %s - %s %s\n", new Object[] { localObject1, localObject2, localObject3 }));
    localObject1 = new JavaScriptException((String)localObject1, (String)localObject2, (String)localObject3);
    localObject2 = new DiagnosticsCallback(libraryVersion, bugsnagAndroidVersion, paramReadableMap);
    localObject3 = new HashMap();
    localObject4 = paramReadableMap.getString("severity");
    String str = paramReadableMap.getString("severityReason");
    ((Map)localObject3).put("severity", localObject4);
    ((Map)localObject3).put("severityReason", str);
    boolean bool1 = bool2;
    if (paramReadableMap.hasKey("blocking"))
    {
      bool1 = bool2;
      if (paramReadableMap.getBoolean("blocking")) {
        bool1 = true;
      }
    }
    Bugsnag.internalClientNotify((Throwable)localObject1, (Map)localObject3, bool1, (Callback)localObject2);
    if (paramPromise != null) {
      paramPromise.resolve(null);
    }
  }
  
  public void resumeSession()
  {
    Bugsnag.resumeSession();
  }
  
  public void setUser(ReadableMap paramReadableMap)
  {
    boolean bool = paramReadableMap.hasKey("id");
    String str3 = null;
    String str1;
    if (bool) {
      str1 = paramReadableMap.getString("id");
    } else {
      str1 = null;
    }
    String str2;
    if (paramReadableMap.hasKey("email")) {
      str2 = paramReadableMap.getString("email");
    } else {
      str2 = null;
    }
    if (paramReadableMap.hasKey("name")) {
      str3 = paramReadableMap.getString("name");
    }
    Bugsnag.setUser(str1, str2, str3);
  }
  
  public void startSession() {}
  
  public void startWithOptions(ReadableMap paramReadableMap)
  {
    if (paramReadableMap.hasKey("apiKey")) {
      localObject = paramReadableMap.getString("apiKey");
    } else {
      localObject = null;
    }
    Object localObject = getClient((String)localObject);
    libraryVersion = paramReadableMap.getString("version");
    bugsnagAndroidVersion = Notifier.getInstance().getVersion();
    configureRuntimeOptions((Client)localObject, paramReadableMap);
    InternalHooks.configureClient((Client)localObject);
    logger.info(String.format("Initialized Bugsnag React Native %s/Android %s", new Object[] { libraryVersion, bugsnagAndroidVersion }));
  }
  
  public void stopSession() {}
}
