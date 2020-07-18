package com.bugsnag.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Configuration
  extends Observable
  implements Observer
{
  static final String DEFAULT_EXCEPTION_TYPE = "android";
  private static final int DEFAULT_MAX_SIZE = 32;
  static final String HEADER_API_KEY = "Bugsnag-Api-Key";
  private static final String HEADER_API_PAYLOAD_VERSION = "Bugsnag-Payload-Version";
  private static final String HEADER_BUGSNAG_SENT_AT = "Bugsnag-Sent-At";
  private long anrThresholdMs = 5000L;
  @NonNull
  private final String apiKey;
  private String appVersion;
  private boolean autoCaptureSessions = true;
  private boolean automaticallyCollectBreadcrumbs = true;
  private final Collection<BeforeNotify> beforeNotifyTasks = new ConcurrentLinkedQueue();
  private final Collection<BeforeRecordBreadcrumb> beforeRecordBreadcrumbTasks = new ConcurrentLinkedQueue();
  private final Collection<BeforeSend> beforeSendTasks = new ConcurrentLinkedQueue();
  private String buildUuid;
  private boolean callPreviousSigquitHandler = true;
  private String codeBundleId;
  private String context;
  private Delivery delivery;
  private boolean detectAnrs = false;
  private boolean detectNdkCrashes;
  private boolean enableExceptionHandler = true;
  private volatile String endpoint = "https://notify.bugsnag.com";
  private String[] ignoreClasses;
  private long launchCrashThresholdMs = 5000L;
  private int maxBreadcrumbs = 32;
  @NonNull
  private MetaData metaData;
  private String notifierType;
  @Nullable
  private String[] notifyReleaseStages = null;
  private boolean persistUserBetweenSessions = false;
  private String[] projectPackages;
  private String releaseStage;
  private boolean sendThreads = true;
  private final Collection<BeforeSendSession> sessionCallbacks = new ConcurrentLinkedQueue();
  private volatile String sessionEndpoint = "https://sessions.bugsnag.com";
  private Integer versionCode;
  
  public Configuration(String paramString)
  {
    apiKey = paramString;
    metaData = new MetaData();
    metaData.addObserver(this);
    try
    {
      detectNdkCrashes = Class.forName("com.bugsnag.android.BuildConfig").getDeclaredField("DETECT_NDK_CRASHES").getBoolean(null);
      return;
    }
    catch (Throwable paramString)
    {
      for (;;) {}
    }
    detectNdkCrashes = false;
  }
  
  void addBeforeSendSession(BeforeSendSession paramBeforeSendSession)
  {
    sessionCallbacks.add(paramBeforeSendSession);
  }
  
  protected void beforeNotify(BeforeNotify paramBeforeNotify)
  {
    if (!beforeNotifyTasks.contains(paramBeforeNotify)) {
      beforeNotifyTasks.add(paramBeforeNotify);
    }
  }
  
  protected void beforeRecordBreadcrumb(BeforeRecordBreadcrumb paramBeforeRecordBreadcrumb)
  {
    if (!beforeRecordBreadcrumbTasks.contains(paramBeforeRecordBreadcrumb)) {
      beforeRecordBreadcrumbTasks.add(paramBeforeRecordBreadcrumb);
    }
  }
  
  public void beforeSend(BeforeSend paramBeforeSend)
  {
    if (!beforeSendTasks.contains(paramBeforeSend)) {
      beforeSendTasks.add(paramBeforeSend);
    }
  }
  
  public long getAnrThresholdMs()
  {
    return anrThresholdMs;
  }
  
  public String getApiKey()
  {
    return apiKey;
  }
  
  public String getAppVersion()
  {
    return appVersion;
  }
  
  public boolean getAutoCaptureSessions()
  {
    return autoCaptureSessions;
  }
  
  protected Collection getBeforeNotifyTasks()
  {
    return beforeNotifyTasks;
  }
  
  protected Collection getBeforeRecordBreadcrumbTasks()
  {
    return beforeRecordBreadcrumbTasks;
  }
  
  protected Collection getBeforeSendTasks()
  {
    return beforeSendTasks;
  }
  
  public String getBuildUUID()
  {
    return buildUuid;
  }
  
  boolean getCallPreviousSigquitHandler()
  {
    return callPreviousSigquitHandler;
  }
  
  public String getCodeBundleId()
  {
    return codeBundleId;
  }
  
  public String getContext()
  {
    return context;
  }
  
  public Delivery getDelivery()
  {
    return delivery;
  }
  
  public boolean getDetectAnrs()
  {
    return detectAnrs;
  }
  
  public boolean getDetectNdkCrashes()
  {
    return detectNdkCrashes;
  }
  
  public boolean getEnableExceptionHandler()
  {
    return enableExceptionHandler;
  }
  
  public String getEndpoint()
  {
    return endpoint;
  }
  
  public Map getErrorApiHeaders()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("Bugsnag-Payload-Version", "4.0");
    localHashMap.put("Bugsnag-Api-Key", apiKey);
    localHashMap.put("Bugsnag-Sent-At", DateUtils.toIso8601(new Date()));
    return localHashMap;
  }
  
  public String[] getFilters()
  {
    return metaData.getFilters();
  }
  
  public String[] getIgnoreClasses()
  {
    return ignoreClasses;
  }
  
  public long getLaunchCrashThresholdMs()
  {
    return launchCrashThresholdMs;
  }
  
  public int getMaxBreadcrumbs()
  {
    return maxBreadcrumbs;
  }
  
  public MetaData getMetaData()
  {
    return metaData;
  }
  
  public String getNotifierType()
  {
    return notifierType;
  }
  
  public String[] getNotifyReleaseStages()
  {
    return notifyReleaseStages;
  }
  
  public boolean getPersistUserBetweenSessions()
  {
    return persistUserBetweenSessions;
  }
  
  public String[] getProjectPackages()
  {
    return projectPackages;
  }
  
  public String getReleaseStage()
  {
    return releaseStage;
  }
  
  public boolean getSendThreads()
  {
    return sendThreads;
  }
  
  public Map getSessionApiHeaders()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("Bugsnag-Payload-Version", "1.0");
    localHashMap.put("Bugsnag-Api-Key", apiKey);
    localHashMap.put("Bugsnag-Sent-At", DateUtils.toIso8601(new Date()));
    return localHashMap;
  }
  
  Collection getSessionCallbacks()
  {
    return sessionCallbacks;
  }
  
  public String getSessionEndpoint()
  {
    return sessionEndpoint;
  }
  
  public Integer getVersionCode()
  {
    return versionCode;
  }
  
  protected boolean inProject(String paramString)
  {
    return Stacktrace.inProject(paramString, projectPackages);
  }
  
  public boolean isAutomaticallyCollectingBreadcrumbs()
  {
    return automaticallyCollectBreadcrumbs;
  }
  
  public void setAnrThresholdMs(long paramLong) {}
  
  public void setAppVersion(String paramString)
  {
    appVersion = paramString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_APP_VERSION, paramString));
  }
  
  public void setAutoCaptureSessions(boolean paramBoolean)
  {
    autoCaptureSessions = paramBoolean;
  }
  
  public void setAutomaticallyCollectBreadcrumbs(boolean paramBoolean)
  {
    automaticallyCollectBreadcrumbs = paramBoolean;
  }
  
  public void setBuildUUID(String paramString)
  {
    buildUuid = paramString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_BUILD_UUID, paramString));
  }
  
  void setCallPreviousSigquitHandler(boolean paramBoolean)
  {
    callPreviousSigquitHandler = paramBoolean;
  }
  
  public void setCodeBundleId(String paramString)
  {
    codeBundleId = paramString;
  }
  
  public void setContext(String paramString)
  {
    context = paramString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_CONTEXT, paramString));
  }
  
  public void setDelivery(Delivery paramDelivery)
  {
    if (paramDelivery != null)
    {
      delivery = paramDelivery;
      return;
    }
    throw new IllegalArgumentException("Delivery cannot be null");
  }
  
  public void setDetectAnrs(boolean paramBoolean)
  {
    detectAnrs = paramBoolean;
  }
  
  public void setDetectNdkCrashes(boolean paramBoolean)
  {
    detectNdkCrashes = paramBoolean;
  }
  
  public void setEnableExceptionHandler(boolean paramBoolean)
  {
    enableExceptionHandler = paramBoolean;
  }
  
  public void setEndpoint(String paramString)
  {
    endpoint = paramString;
  }
  
  public void setEndpoints(String paramString1, String paramString2)
    throws IllegalArgumentException
  {
    if (!Intrinsics.isEmpty(paramString1))
    {
      endpoint = paramString1;
      if (Intrinsics.isEmpty(paramString2))
      {
        Logger.warn("The session tracking endpoint has not been set. Session tracking is disabled");
        sessionEndpoint = null;
        autoCaptureSessions = false;
        return;
      }
      sessionEndpoint = paramString2;
      return;
    }
    throw new IllegalArgumentException("Notify endpoint cannot be empty or null.");
  }
  
  public void setFilters(String[] paramArrayOfString)
  {
    metaData.setFilters(paramArrayOfString);
  }
  
  public void setIgnoreClasses(String[] paramArrayOfString)
  {
    ignoreClasses = paramArrayOfString;
  }
  
  public void setLaunchCrashThresholdMs(long paramLong)
  {
    if (paramLong <= 0L)
    {
      launchCrashThresholdMs = 0L;
      return;
    }
    launchCrashThresholdMs = paramLong;
  }
  
  public void setMaxBreadcrumbs(int paramInt)
  {
    if (paramInt < 0)
    {
      Logger.warn("Ignoring invalid breadcrumb capacity. Must be >= 0.");
      return;
    }
    maxBreadcrumbs = paramInt;
  }
  
  public void setMetaData(MetaData paramMetaData)
  {
    metaData.deleteObserver(this);
    if (paramMetaData == null) {
      metaData = new MetaData();
    } else {
      metaData = paramMetaData;
    }
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_METADATA, metaData.store));
    metaData.addObserver(this);
  }
  
  public void setNotifierType(String paramString)
  {
    notifierType = paramString;
  }
  
  public void setNotifyReleaseStages(String[] paramArrayOfString)
  {
    notifyReleaseStages = paramArrayOfString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_NOTIFY_RELEASE_STAGES, this));
  }
  
  public void setPersistUserBetweenSessions(boolean paramBoolean)
  {
    persistUserBetweenSessions = paramBoolean;
  }
  
  public void setProjectPackages(String[] paramArrayOfString)
  {
    projectPackages = paramArrayOfString;
  }
  
  public void setReleaseStage(String paramString)
  {
    releaseStage = paramString;
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_RELEASE_STAGE, this));
  }
  
  public void setSendThreads(boolean paramBoolean)
  {
    sendThreads = paramBoolean;
  }
  
  public void setSessionEndpoint(String paramString)
  {
    sessionEndpoint = paramString;
  }
  
  public void setVersionCode(Integer paramInteger)
  {
    versionCode = paramInteger;
  }
  
  public boolean shouldAutoCaptureSessions()
  {
    return getAutoCaptureSessions();
  }
  
  protected boolean shouldIgnoreClass(String paramString)
  {
    if (ignoreClasses == null) {
      return false;
    }
    return Arrays.asList(ignoreClasses).contains(paramString);
  }
  
  public boolean shouldNotifyForReleaseStage(String paramString)
  {
    if (notifyReleaseStages == null) {
      return true;
    }
    return Arrays.asList(notifyReleaseStages).contains(paramString);
  }
  
  public void update(Observable paramObservable, Object paramObject)
  {
    if ((paramObject instanceof NativeInterface.Message))
    {
      setChanged();
      notifyObservers(paramObject);
    }
  }
}
