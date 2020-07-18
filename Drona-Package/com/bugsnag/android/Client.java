package com.bugsnag.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.view.OrientationEventListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.RejectedExecutionException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Client
  extends Observable
  implements Observer
{
  private static final boolean BLOCKING = true;
  static final String INTERNAL_DIAGNOSTICS_TAB = "BugsnagDiagnostics";
  private static final String SHARED_PREF_KEY = "com.bugsnag.android";
  private static final String USER_EMAIL_KEY = "user.email";
  private static final String USER_ID_KEY = "user.id";
  private static final String USER_NAME_KEY = "user.name";
  @Nullable
  private Class<?> anrPluginClz;
  final Context appContext;
  @NonNull
  protected final AppData appData;
  @NonNull
  final Breadcrumbs breadcrumbs;
  @NonNull
  protected final Configuration config;
  private final Connectivity connectivity;
  @NonNull
  protected final DeviceData deviceData;
  @NonNull
  protected final ErrorStore errorStore;
  final EventReceiver eventReceiver;
  @Nullable
  private Class<?> ndkPluginClz;
  private final OrientationEventListener orientationListener;
  final SessionStore sessionStore;
  final SessionTracker sessionTracker;
  final SharedPreferences sharedPrefs;
  final StorageManager storageManager;
  @NonNull
  private final User user = new User();
  
  public Client(Context paramContext)
  {
    this(paramContext, null, true);
  }
  
  public Client(Context paramContext, Configuration paramConfiguration)
  {
    warnIfNotAppContext(paramContext);
    appContext = paramContext.getApplicationContext();
    config = paramConfiguration;
    sessionStore = new SessionStore(config, appContext, null);
    storageManager = ((StorageManager)appContext.getSystemService("storage"));
    connectivity = new ConnectivityCompat(appContext, new Function1()
    {
      public Unit invoke(Boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean.booleanValue()) {
          errorStore.flushAsync();
        }
        return null;
      }
    });
    if (paramConfiguration.getDelivery() == null) {
      paramConfiguration.setDelivery(new DefaultDelivery(connectivity));
    }
    sessionTracker = new SessionTracker(paramConfiguration, this, sessionStore);
    eventReceiver = new EventReceiver(this);
    sharedPrefs = appContext.getSharedPreferences("com.bugsnag.android", 0);
    appData = new AppData(appContext, appContext.getPackageManager(), config, sessionTracker);
    paramContext = appContext.getResources();
    deviceData = new DeviceData(connectivity, appContext, paramContext, sharedPrefs);
    breadcrumbs = new Breadcrumbs(paramConfiguration);
    if (config.getProjectPackages() == null) {
      setProjectPackages(new String[] { appContext.getPackageName() });
    }
    paramContext = deviceData.getId();
    if (config.getPersistUserBetweenSessions())
    {
      user.setId(sharedPrefs.getString("user.id", paramContext));
      user.setName(sharedPrefs.getString("user.name", null));
      user.setEmail(sharedPrefs.getString("user.email", null));
    }
    else
    {
      user.setId(paramContext);
    }
    if ((appContext instanceof Application)) {
      ((Application)appContext).registerActivityLifecycleCallbacks(sessionTracker);
    } else {
      Logger.warn("Bugsnag is unable to setup automatic activity lifecycle breadcrumbs on API Levels below 14.");
    }
    if (config.getBuildUUID() == null) {
      paramContext = appContext;
    }
    try
    {
      paramContext = paramContext.getPackageManager();
      paramConfiguration = appContext;
      paramContext = paramContext.getApplicationInfo(paramConfiguration.getPackageName(), 128);
      paramContext = metaData;
      paramContext = paramContext.getString("com.bugsnag.android.BUILD_UUID");
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }
    Logger.warn("Bugsnag is unable to read build UUID from manifest.");
    paramContext = null;
    if (paramContext != null) {
      config.setBuildUUID(paramContext);
    }
    errorStore = new ErrorStore(config, appContext, new FileStore.Delegate()
    {
      public void onErrorIOFailure(Exception paramAnonymousException, File paramAnonymousFile, String paramAnonymousString)
      {
        Thread localThread = Thread.currentThread();
        paramAnonymousException = new Error.Builder(config, paramAnonymousException, null, localThread, true).build();
        paramAnonymousException.setContext(paramAnonymousString);
        paramAnonymousString = paramAnonymousException.getMetaData();
        paramAnonymousString.addToTab("BugsnagDiagnostics", "canRead", Boolean.valueOf(paramAnonymousFile.canRead()));
        paramAnonymousString.addToTab("BugsnagDiagnostics", "canWrite", Boolean.valueOf(paramAnonymousFile.canWrite()));
        paramAnonymousString.addToTab("BugsnagDiagnostics", "exists", Boolean.valueOf(paramAnonymousFile.exists()));
        paramAnonymousString.addToTab("BugsnagDiagnostics", "usableSpace", Long.valueOf(appContext.getCacheDir().getUsableSpace()));
        paramAnonymousString.addToTab("BugsnagDiagnostics", "filename", paramAnonymousFile.getName());
        paramAnonymousString.addToTab("BugsnagDiagnostics", "fileLength", Long.valueOf(paramAnonymousFile.length()));
        recordStorageCacheBehavior(paramAnonymousString);
        reportInternalBugsnagError(paramAnonymousException);
      }
    });
    if (config.getEnableExceptionHandler()) {
      enableExceptionHandler();
    }
    try
    {
      paramContext = Class.forName("com.bugsnag.android.NdkPlugin");
      ndkPluginClz = paramContext;
    }
    catch (ClassNotFoundException paramContext)
    {
      for (;;) {}
    }
    Logger.warn("bugsnag-plugin-android-ndk artefact not found on classpath, NDK errors will not be captured.");
    try
    {
      paramContext = Class.forName("com.bugsnag.android.AnrPlugin");
      anrPluginClz = paramContext;
    }
    catch (ClassNotFoundException paramContext)
    {
      for (;;) {}
    }
    Logger.warn("bugsnag-plugin-android-anr artefact not found on classpath, ANR errors will not be captured.");
    try
    {
      Async.runAsync(new Runnable()
      {
        public void run()
        {
          appContext.registerReceiver(eventReceiver, EventReceiver.getIntentFilter());
        }
      });
    }
    catch (RejectedExecutionException paramContext)
    {
      Logger.warn("Failed to register for automatic breadcrumb broadcasts", paramContext);
    }
    connectivity.registerForNetworkChanges();
    Logger.setEnabled("production".equals(appData.guessReleaseStage()) ^ true);
    config.addObserver(this);
    breadcrumbs.addObserver(this);
    sessionTracker.addObserver(this);
    user.addObserver(this);
    orientationListener = new OrientationEventListener(appContext)
    {
      public void onOrientationChanged(int paramAnonymousInt)
      {
        jdField_this.setChanged();
        jdField_this.notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_ORIENTATION, Integer.valueOf(paramAnonymousInt)));
      }
    };
    paramContext = orientationListener;
    try
    {
      paramContext.enable();
    }
    catch (IllegalStateException paramContext)
    {
      paramConfiguration = new StringBuilder();
      paramConfiguration.append("Failed to set up orientation tracking: ");
      paramConfiguration.append(paramContext);
      Logger.warn(paramConfiguration.toString());
    }
    errorStore.flushOnLaunch();
    loadPlugins();
    paramContext = new ClientConfigObserver(this, config);
    config.addObserver(paramContext);
    addObserver(paramContext);
  }
  
  public Client(Context paramContext, String paramString)
  {
    this(paramContext, paramString, true);
  }
  
  public Client(Context paramContext, String paramString, boolean paramBoolean)
  {
    this(paramContext, ConfigFactory.createNewConfiguration(paramContext, paramString, paramBoolean));
  }
  
  private void deliverReportAsync(final Error paramError, final Report paramReport)
  {
    try
    {
      Async.runAsync(new Runnable()
      {
        public void run()
        {
          deliver(paramReport, paramError);
        }
      });
      return;
    }
    catch (RejectedExecutionException paramReport)
    {
      for (;;) {}
    }
    errorStore.write(paramError);
    Logger.warn("Exceeded max queue count, saving to disk to send later");
  }
  
  private String getKeyFromClientData(Map paramMap, String paramString, boolean paramBoolean)
  {
    paramMap = paramMap.get(paramString);
    if ((paramMap instanceof String)) {
      return (String)paramMap;
    }
    if (!paramBoolean) {
      return null;
    }
    paramMap = new StringBuilder();
    paramMap.append("Failed to set ");
    paramMap.append(paramString);
    paramMap.append(" in client data!");
    throw new IllegalStateException(paramMap.toString());
  }
  
  private void leaveErrorBreadcrumb(Error paramError)
  {
    Map localMap = Collections.singletonMap("message", paramError.getExceptionMessage());
    breadcrumbs.i(new Breadcrumb(paramError.getExceptionName(), BreadcrumbType.ERROR, localMap));
  }
  
  private void loadPlugins()
  {
    NativeInterface.setClient(this);
    enableOrDisableNdkReporting();
    enableOrDisableAnrReporting();
    BugsnagPluginInterface.INSTANCE.loadRegisteredPlugins(this);
  }
  
  private void notify(Error paramError, boolean paramBoolean)
  {
    DeliveryStyle localDeliveryStyle;
    if (paramBoolean) {
      localDeliveryStyle = DeliveryStyle.SAME_THREAD;
    } else {
      localDeliveryStyle = DeliveryStyle.ASYNC;
    }
    notify(paramError, localDeliveryStyle, null);
  }
  
  private boolean runBeforeBreadcrumbTasks(Breadcrumb paramBreadcrumb)
  {
    Iterator localIterator = config.getBeforeRecordBreadcrumbTasks().iterator();
    while (localIterator.hasNext())
    {
      BeforeRecordBreadcrumb localBeforeRecordBreadcrumb = (BeforeRecordBreadcrumb)localIterator.next();
      try
      {
        boolean bool = localBeforeRecordBreadcrumb.shouldRecord(paramBreadcrumb);
        if (!bool) {
          return false;
        }
      }
      catch (Throwable localThrowable)
      {
        Logger.warn("BeforeRecordBreadcrumb threw an Exception", localThrowable);
      }
    }
    return true;
  }
  
  private boolean runBeforeNotifyTasks(Error paramError)
  {
    Iterator localIterator = config.getBeforeNotifyTasks().iterator();
    while (localIterator.hasNext())
    {
      BeforeNotify localBeforeNotify = (BeforeNotify)localIterator.next();
      try
      {
        boolean bool = localBeforeNotify.isUnchanged(paramError);
        if (!bool) {
          return false;
        }
      }
      catch (Throwable localThrowable)
      {
        Logger.warn("BeforeNotify threw an Exception", localThrowable);
      }
    }
    return true;
  }
  
  private boolean runBeforeSendTasks(Report paramReport)
  {
    Iterator localIterator = config.getBeforeSendTasks().iterator();
    while (localIterator.hasNext())
    {
      BeforeSend localBeforeSend = (BeforeSend)localIterator.next();
      try
      {
        boolean bool = localBeforeSend.canExecute(paramReport);
        if (!bool) {
          return false;
        }
      }
      catch (Throwable localThrowable)
      {
        Logger.warn("BeforeSend threw an Exception", localThrowable);
      }
    }
    return true;
  }
  
  private void storeInSharedPrefs(String paramString1, String paramString2)
  {
    appContext.getSharedPreferences("com.bugsnag.android", 0).edit().putString(paramString1, paramString2).apply();
  }
  
  private static void warnIfNotAppContext(Context paramContext)
  {
    if (!(paramContext instanceof Application)) {
      Logger.warn("Warning - Non-Application context detected! Please ensure that you are initializing Bugsnag from a custom Application class.");
    }
  }
  
  public void addToTab(String paramString1, String paramString2, Object paramObject)
  {
    config.getMetaData().addToTab(paramString1, paramString2, paramObject);
  }
  
  public void beforeNotify(BeforeNotify paramBeforeNotify)
  {
    config.beforeNotify(paramBeforeNotify);
  }
  
  public void beforeRecordBreadcrumb(BeforeRecordBreadcrumb paramBeforeRecordBreadcrumb)
  {
    config.beforeRecordBreadcrumb(paramBeforeRecordBreadcrumb);
  }
  
  void cacheAndNotify(Throwable paramThrowable, Severity paramSeverity, MetaData paramMetaData, String paramString1, String paramString2, Thread paramThread)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, paramThread, true).severity(paramSeverity).metaData(paramMetaData).severityReasonType(paramString1).attributeValue(paramString2).build(), DeliveryStyle.ASYNC_WITH_CACHE, null);
  }
  
  public void clearBreadcrumbs()
  {
    breadcrumbs.clear();
  }
  
  public void clearTab(String paramString)
  {
    config.getMetaData().clearTab(paramString);
  }
  
  public void clearUser()
  {
    user.setId(MapUtils.getStringFromMap("id", deviceData.getDeviceData()));
    user.setEmail(null);
    user.setName(null);
    appContext.getSharedPreferences("com.bugsnag.android", 0).edit().remove("user.id").remove("user.email").remove("user.name").apply();
  }
  
  void close()
  {
    orientationListener.disable();
    connectivity.unregisterForNetworkChanges();
  }
  
  void deliver(Report paramReport, Error paramError)
  {
    if (!runBeforeSendTasks(paramReport))
    {
      Logger.info("Skipping notification - beforeSend task returned false");
      return;
    }
    Object localObject = config;
    try
    {
      localObject = ((Configuration)localObject).getDelivery();
      Configuration localConfiguration = config;
      ((Delivery)localObject).deliver(paramReport, localConfiguration);
      Logger.info("Sent 1 new error to Bugsnag");
      leaveErrorBreadcrumb(paramError);
      return;
    }
    catch (Exception paramReport)
    {
      Logger.warn("Problem sending error to Bugsnag", paramReport);
      return;
    }
    catch (DeliveryFailureException localDeliveryFailureException)
    {
      if (!paramReport.isCachingDisabled())
      {
        Logger.warn("Could not send error(s) to Bugsnag, saving to disk to send later", localDeliveryFailureException);
        errorStore.write(paramError);
        leaveErrorBreadcrumb(paramError);
      }
    }
  }
  
  void disableAnrReporting()
  {
    getConfig().setDetectAnrs(false);
    enableOrDisableAnrReporting();
  }
  
  public void disableExceptionHandler()
  {
    ExceptionHandler.disable(this);
  }
  
  void disableNdkCrashReporting()
  {
    getConfig().setDetectNdkCrashes(false);
    enableOrDisableNdkReporting();
  }
  
  void enableAnrReporting()
  {
    getConfig().setDetectAnrs(true);
    enableOrDisableAnrReporting();
  }
  
  public void enableExceptionHandler()
  {
    ExceptionHandler.enable(this);
  }
  
  void enableNdkCrashReporting()
  {
    getConfig().setDetectNdkCrashes(true);
    enableOrDisableNdkReporting();
  }
  
  void enableOrDisableAnrReporting()
  {
    if (anrPluginClz == null) {
      return;
    }
    if (config.getDetectAnrs())
    {
      BugsnagPluginInterface.INSTANCE.loadPlugin(this, anrPluginClz);
      return;
    }
    BugsnagPluginInterface.INSTANCE.unloadPlugin(anrPluginClz);
  }
  
  void enableOrDisableNdkReporting()
  {
    if (ndkPluginClz == null) {
      return;
    }
    if (config.getDetectNdkCrashes())
    {
      BugsnagPluginInterface.INSTANCE.loadPlugin(this, ndkPluginClz);
      return;
    }
    BugsnagPluginInterface.INSTANCE.unloadPlugin(ndkPluginClz);
  }
  
  void enqueuePendingNativeReports()
  {
    setChanged();
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.DELIVER_PENDING, null));
  }
  
  protected void finalize()
    throws Throwable
  {
    Context localContext;
    EventReceiver localEventReceiver;
    if (eventReceiver != null)
    {
      localContext = appContext;
      localEventReceiver = eventReceiver;
    }
    try
    {
      localContext.unregisterReceiver(localEventReceiver);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
    Logger.warn("Receiver not registered");
    super.finalize();
  }
  
  DeliveryCompat getAndSetDeliveryCompat()
  {
    Object localObject = config.getDelivery();
    if ((localObject instanceof DeliveryCompat)) {
      return (DeliveryCompat)localObject;
    }
    localObject = new DeliveryCompat();
    config.setDelivery((Delivery)localObject);
    return localObject;
  }
  
  public AppData getAppData()
  {
    return appData;
  }
  
  public Collection getBreadcrumbs()
  {
    return new ArrayList(breadcrumbs.store);
  }
  
  public Configuration getConfig()
  {
    return config;
  }
  
  public String getContext()
  {
    return config.getContext();
  }
  
  public DeviceData getDeviceData()
  {
    return deviceData;
  }
  
  ErrorStore getErrorStore()
  {
    return errorStore;
  }
  
  public long getLaunchTimeMs()
  {
    return AppData.getDurationMs();
  }
  
  public MetaData getMetaData()
  {
    return config.getMetaData();
  }
  
  OrientationEventListener getOrientationListener()
  {
    return orientationListener;
  }
  
  SessionTracker getSessionTracker()
  {
    return sessionTracker;
  }
  
  public User getUser()
  {
    return user;
  }
  
  public void internalClientNotify(Throwable paramThrowable, Map paramMap, boolean paramBoolean, Callback paramCallback)
  {
    String str1 = getKeyFromClientData(paramMap, "severity", true);
    String str2 = getKeyFromClientData(paramMap, "severityReason", true);
    paramMap = getKeyFromClientData(paramMap, "logLevel", false);
    Logger.info(String.format("Internal client notify, severity = '%s', severityReason = '%s'", new Object[] { str1, str2 }));
    paramMap = new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severity(Severity.fromString(str1)).severityReasonType(str2).attributeValue(paramMap).build();
    if (paramBoolean) {
      paramThrowable = DeliveryStyle.SAME_THREAD;
    } else {
      paramThrowable = DeliveryStyle.ASYNC;
    }
    notify(paramMap, paramThrowable, paramCallback);
  }
  
  public void leaveBreadcrumb(String paramString)
  {
    paramString = new Breadcrumb(paramString);
    if (runBeforeBreadcrumbTasks(paramString)) {
      breadcrumbs.i(paramString);
    }
  }
  
  public void leaveBreadcrumb(String paramString, BreadcrumbType paramBreadcrumbType, Map paramMap)
  {
    paramString = new Breadcrumb(paramString, paramBreadcrumbType, paramMap);
    if (runBeforeBreadcrumbTasks(paramString)) {
      breadcrumbs.i(paramString);
    }
  }
  
  void notify(Error paramError, DeliveryStyle paramDeliveryStyle, Callback paramCallback)
  {
    if (paramError.shouldIgnoreClass()) {
      return;
    }
    Object localObject = appData.getAppData();
    String str = MapUtils.getStringFromMap("releaseStage", (Map)localObject);
    if (!config.shouldNotifyForReleaseStage(str)) {
      return;
    }
    paramError.setDeviceData(deviceData.getDeviceData());
    getMetaDatastore.put("device", deviceData.getDeviceMetaData());
    paramError.setAppData((Map)localObject);
    getMetaDatastore.put("app", appData.getAppDataMetaData());
    paramError.setBreadcrumbs(breadcrumbs);
    paramError.setUser(user);
    if (TextUtils.isEmpty(paramError.getContext()))
    {
      str = config.getContext();
      localObject = str;
      if (str == null) {
        localObject = appData.getActiveScreenClass();
      }
      paramError.setContext((String)localObject);
    }
    if (!runBeforeNotifyTasks(paramError))
    {
      Logger.info("Skipping notification - beforeNotify task returned false");
      return;
    }
    localObject = new Report(config.getApiKey(), paramError);
    if (paramCallback != null) {
      paramCallback.beforeNotify((Report)localObject);
    }
    if (paramError.getSession() != null)
    {
      setChanged();
      if (paramError.getHandledState().isUnhandled()) {
        notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.NOTIFY_UNHANDLED, null));
      } else {
        notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.NOTIFY_HANDLED, paramError.getExceptionName()));
      }
    }
    switch (8.$SwitchMap$com$bugsnag$android$DeliveryStyle[paramDeliveryStyle.ordinal()])
    {
    default: 
      return;
    case 4: 
      errorStore.write(paramError);
      errorStore.flushAsync();
      return;
    case 3: 
      deliverReportAsync(paramError, (Report)localObject);
      return;
    case 2: 
      ((Report)localObject).setCachingDisabled(true);
      deliverReportAsync(paramError, (Report)localObject);
      return;
    }
    deliver((Report)localObject, paramError);
  }
  
  public void notify(String paramString1, String paramString2, String paramString3, StackTraceElement[] paramArrayOfStackTraceElement, Severity paramSeverity, MetaData paramMetaData)
  {
    paramString1 = new Error.Builder(config, paramString1, paramString2, paramArrayOfStackTraceElement, sessionTracker, Thread.currentThread()).severity(paramSeverity).metaData(paramMetaData).build();
    paramString1.setContext(paramString3);
    notify(paramString1, false);
  }
  
  public void notify(String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement, Callback paramCallback)
  {
    notify(new Error.Builder(config, paramString1, paramString2, paramArrayOfStackTraceElement, sessionTracker, Thread.currentThread()).severityReasonType("handledException").build(), DeliveryStyle.ASYNC, paramCallback);
  }
  
  public void notify(String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement, Severity paramSeverity, MetaData paramMetaData)
  {
    notify(new Error.Builder(config, paramString1, paramString2, paramArrayOfStackTraceElement, sessionTracker, Thread.currentThread()).severity(paramSeverity).metaData(paramMetaData).build(), false);
  }
  
  public void notify(Throwable paramThrowable)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severityReasonType("handledException").build(), false);
  }
  
  public void notify(Throwable paramThrowable, Callback paramCallback)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severityReasonType("handledException").build(), DeliveryStyle.ASYNC, paramCallback);
  }
  
  public void notify(Throwable paramThrowable, MetaData paramMetaData)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).metaData(paramMetaData).severityReasonType("handledException").build(), false);
  }
  
  public void notify(Throwable paramThrowable, Severity paramSeverity)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severity(paramSeverity).build(), false);
  }
  
  public void notify(Throwable paramThrowable, Severity paramSeverity, MetaData paramMetaData)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).metaData(paramMetaData).severity(paramSeverity).build(), false);
  }
  
  public void notifyBlocking(String paramString1, String paramString2, String paramString3, StackTraceElement[] paramArrayOfStackTraceElement, Severity paramSeverity, MetaData paramMetaData)
  {
    paramString1 = new Error.Builder(config, paramString1, paramString2, paramArrayOfStackTraceElement, sessionTracker, Thread.currentThread()).severity(paramSeverity).metaData(paramMetaData).build();
    paramString1.setContext(paramString3);
    notify(paramString1, true);
  }
  
  public void notifyBlocking(String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement, Callback paramCallback)
  {
    notify(new Error.Builder(config, paramString1, paramString2, paramArrayOfStackTraceElement, sessionTracker, Thread.currentThread()).severityReasonType("handledException").build(), DeliveryStyle.SAME_THREAD, paramCallback);
  }
  
  public void notifyBlocking(String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement, Severity paramSeverity, MetaData paramMetaData)
  {
    notify(new Error.Builder(config, paramString1, paramString2, paramArrayOfStackTraceElement, sessionTracker, Thread.currentThread()).severity(paramSeverity).metaData(paramMetaData).build(), true);
  }
  
  public void notifyBlocking(Throwable paramThrowable)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severityReasonType("handledException").build(), true);
  }
  
  public void notifyBlocking(Throwable paramThrowable, Callback paramCallback)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severityReasonType("handledException").build(), DeliveryStyle.SAME_THREAD, paramCallback);
  }
  
  public void notifyBlocking(Throwable paramThrowable, MetaData paramMetaData)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severityReasonType("handledException").metaData(paramMetaData).build(), true);
  }
  
  public void notifyBlocking(Throwable paramThrowable, Severity paramSeverity)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).severity(paramSeverity).build(), true);
  }
  
  public void notifyBlocking(Throwable paramThrowable, Severity paramSeverity, MetaData paramMetaData)
  {
    notify(new Error.Builder(config, paramThrowable, sessionTracker, Thread.currentThread(), false).metaData(paramMetaData).severity(paramSeverity).build(), true);
  }
  
  void recordStorageCacheBehavior(MetaData paramMetaData)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      File localFile = new File(appContext.getCacheDir(), "bugsnag-errors");
      StorageManager localStorageManager = storageManager;
      try
      {
        boolean bool1 = localStorageManager.isCacheBehaviorTombstone(localFile);
        localStorageManager = storageManager;
        boolean bool2 = localStorageManager.isCacheBehaviorGroup(localFile);
        paramMetaData.addToTab("BugsnagDiagnostics", "cacheTombstone", Boolean.valueOf(bool1));
        paramMetaData.addToTab("BugsnagDiagnostics", "cacheGroup", Boolean.valueOf(bool2));
        return;
      }
      catch (IOException paramMetaData)
      {
        Logger.warn("Failed to record cache behaviour, skipping diagnostics", paramMetaData);
      }
    }
  }
  
  void reportInternalBugsnagError(final Error paramError)
  {
    Object localObject = appData.getAppDataSummary();
    ((Map)localObject).put("duration", Long.valueOf(AppData.getDurationMs()));
    ((Map)localObject).put("durationInForeground", appData.calculateDurationInForeground());
    ((Map)localObject).put("inForeground", sessionTracker.isInForeground());
    paramError.setAppData((Map)localObject);
    localObject = deviceData.getDeviceDataSummary();
    ((Map)localObject).put("freeDisk", Long.valueOf(deviceData.calculateFreeDisk()));
    paramError.setDeviceData((Map)localObject);
    localObject = paramError.getMetaData();
    Notifier localNotifier = Notifier.getInstance();
    ((MetaData)localObject).addToTab("BugsnagDiagnostics", "notifierName", localNotifier.getName());
    ((MetaData)localObject).addToTab("BugsnagDiagnostics", "notifierVersion", localNotifier.getVersion());
    ((MetaData)localObject).addToTab("BugsnagDiagnostics", "apiKey", config.getApiKey());
    ((MetaData)localObject).addToTab("BugsnagDiagnostics", "packageName", appData.getAppData().get("packageName"));
    paramError = new Report(null, paramError);
    try
    {
      Async.runAsync(new Runnable()
      {
        public void run()
        {
          Object localObject1 = config;
          try
          {
            Object localObject2 = ((Configuration)localObject1).getDelivery();
            if ((localObject2 instanceof DefaultDelivery))
            {
              localObject1 = config;
              localObject1 = ((Configuration)localObject1).getErrorApiHeaders();
              ((Map)localObject1).put("Bugsnag-Internal-Error", "true");
              ((Map)localObject1).remove("Bugsnag-Api-Key");
              localObject2 = (DefaultDelivery)localObject2;
              Object localObject3 = config;
              localObject3 = ((Configuration)localObject3).getEndpoint();
              Report localReport = paramError;
              ((DefaultDelivery)localObject2).deliver((String)localObject3, localReport, (Map)localObject1);
              return;
            }
          }
          catch (Exception localException)
          {
            Logger.warn("Failed to report internal error to Bugsnag", localException);
          }
        }
      });
      return;
    }
    catch (RejectedExecutionException paramError) {}
  }
  
  public final boolean resumeSession()
  {
    return sessionTracker.resumeSession();
  }
  
  void sendNativeSetupNotification()
  {
    setChanged();
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(config);
    super.notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.INSTALL, localArrayList));
    try
    {
      Async.runAsync(new Runnable()
      {
        public void run()
        {
          enqueuePendingNativeReports();
        }
      });
      return;
    }
    catch (RejectedExecutionException localRejectedExecutionException)
    {
      Logger.warn("Failed to enqueue native reports, will retry next launch: ", localRejectedExecutionException);
    }
  }
  
  public void setAppVersion(String paramString)
  {
    config.setAppVersion(paramString);
  }
  
  public void setAutoCaptureSessions(boolean paramBoolean)
  {
    config.setAutoCaptureSessions(paramBoolean);
    if (paramBoolean) {
      sessionTracker.onAutoCaptureEnabled();
    }
  }
  
  void setBinaryArch(String paramString)
  {
    getAppData().setBinaryArch(paramString);
  }
  
  public void setBuildUUID(String paramString)
  {
    config.setBuildUUID(paramString);
  }
  
  public void setContext(String paramString)
  {
    config.setContext(paramString);
  }
  
  public void setEndpoint(String paramString)
  {
    config.setEndpoint(paramString);
  }
  
  void setErrorReportApiClient(ErrorReportApiClient paramErrorReportApiClient)
  {
    if (paramErrorReportApiClient != null)
    {
      getAndSetDeliveryCompaterrorReportApiClient = paramErrorReportApiClient;
      return;
    }
    throw new IllegalArgumentException("ErrorReportApiClient cannot be null.");
  }
  
  public void setFilters(String... paramVarArgs)
  {
    config.setFilters(paramVarArgs);
  }
  
  public void setIgnoreClasses(String... paramVarArgs)
  {
    config.setIgnoreClasses(paramVarArgs);
  }
  
  public void setLoggingEnabled(boolean paramBoolean)
  {
    Logger.setEnabled(paramBoolean);
  }
  
  public void setMaxBreadcrumbs(int paramInt)
  {
    config.setMaxBreadcrumbs(paramInt);
  }
  
  public void setMetaData(MetaData paramMetaData)
  {
    config.setMetaData(paramMetaData);
  }
  
  public void setNotifyReleaseStages(String... paramVarArgs)
  {
    config.setNotifyReleaseStages(paramVarArgs);
  }
  
  public void setProjectPackages(String... paramVarArgs)
  {
    config.setProjectPackages(paramVarArgs);
  }
  
  public void setReleaseStage(String paramString)
  {
    config.setReleaseStage(paramString);
    Logger.setEnabled("production".equals(paramString) ^ true);
  }
  
  public void setSendThreads(boolean paramBoolean)
  {
    config.setSendThreads(paramBoolean);
  }
  
  void setSessionTrackingApiClient(SessionTrackingApiClient paramSessionTrackingApiClient)
  {
    if (paramSessionTrackingApiClient != null)
    {
      getAndSetDeliveryCompatsessionTrackingApiClient = paramSessionTrackingApiClient;
      return;
    }
    throw new IllegalArgumentException("SessionTrackingApiClient cannot be null.");
  }
  
  public void setUser(String paramString1, String paramString2, String paramString3)
  {
    setUserId(paramString1);
    setUserEmail(paramString2);
    setUserName(paramString3);
  }
  
  public void setUserEmail(String paramString)
  {
    user.setEmail(paramString);
    if (config.getPersistUserBetweenSessions()) {
      storeInSharedPrefs("user.email", paramString);
    }
  }
  
  public void setUserId(String paramString)
  {
    user.setId(paramString);
    if (config.getPersistUserBetweenSessions()) {
      storeInSharedPrefs("user.id", paramString);
    }
  }
  
  public void setUserName(String paramString)
  {
    user.setName(paramString);
    if (config.getPersistUserBetweenSessions()) {
      storeInSharedPrefs("user.name", paramString);
    }
  }
  
  public void startFirstSession(Activity paramActivity)
  {
    sessionTracker.startFirstSession(paramActivity);
  }
  
  public void startSession()
  {
    sessionTracker.startSession(false);
  }
  
  public final void stopSession()
  {
    sessionTracker.stopSession();
  }
  
  public void update(Observable paramObservable, Object paramObject)
  {
    if ((paramObject instanceof NativeInterface.Message))
    {
      setChanged();
      super.notifyObservers(paramObject);
    }
  }
}
