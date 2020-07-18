package com.bugsnag.android.jgit;

import android.os.Build.VERSION;
import android.util.Log;
import com.bugsnag.android.Breadcrumb;
import com.bugsnag.android.BreadcrumbType;
import com.bugsnag.android.Configuration;
import com.bugsnag.android.NativeInterface;
import com.bugsnag.android.NativeInterface.Message;
import com.bugsnag.android.NativeInterface.MessageType;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NativeBridge
  implements Observer
{
  private static final String LOG_TAG = "BugsnagNDK:NativeBridge";
  private static final int METADATA_KEY = 1;
  private static final int METADATA_SECTION = 0;
  private static final int METADATA_VALUE = 2;
  private static final AtomicBoolean installed = new AtomicBoolean(false);
  private static final Lock lock = new ReentrantLock();
  private boolean loggingEnabled = true;
  private final String reportDirectory = NativeInterface.getNativeReportPath();
  
  public NativeBridge()
  {
    File localFile = new File(reportDirectory);
    if ((!localFile.exists()) && (!localFile.mkdirs())) {
      warn("The native reporting directory cannot be created.");
    }
  }
  
  public static native void addBreadcrumb(String paramString1, String paramString2, String paramString3, Object paramObject);
  
  public static native void addHandledEvent();
  
  public static native void addMetadataBoolean(String paramString1, String paramString2, boolean paramBoolean);
  
  public static native void addMetadataDouble(String paramString1, String paramString2, double paramDouble);
  
  public static native void addMetadataString(String paramString1, String paramString2, String paramString3);
  
  public static native void addUnhandledEvent();
  
  public static native void clearBreadcrumbs();
  
  public static native void clearMetadataTab(String paramString);
  
  private void deliverPendingReports()
  {
    lock.lock();
    Object localObject = reportDirectory;
    try
    {
      localObject = new File((String)localObject);
      boolean bool = ((File)localObject).exists();
      if (bool)
      {
        localObject = ((File)localObject).listFiles();
        if (localObject != null)
        {
          int j = localObject.length;
          int i = 0;
          while (i < j)
          {
            localStringBuilder = localObject[i];
            deliverReportAtPath(localStringBuilder.getAbsolutePath());
            i += 1;
          }
        }
      }
      else
      {
        warn("Report directory does not exist, cannot read pending reports");
      }
    }
    catch (Throwable localThrowable) {}catch (Exception localException)
    {
      for (;;)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Failed to parse/write pending reports: ");
        localStringBuilder.append(localException);
        warn(localStringBuilder.toString());
      }
    }
    lock.unlock();
    return;
    lock.unlock();
    throw localException;
  }
  
  public static native void deliverReportAtPath(String paramString);
  
  public static native void disableCrashReporting();
  
  public static native void enableCrashReporting();
  
  private void enableOrDisableReportingIfNeeded(Configuration paramConfiguration)
  {
    if (paramConfiguration.shouldNotifyForReleaseStage(paramConfiguration.getReleaseStage()))
    {
      if (paramConfiguration.getDetectNdkCrashes()) {
        enableCrashReporting();
      }
    }
    else {
      disableCrashReporting();
    }
  }
  
  private void handleAddBreadcrumb(Object paramObject)
  {
    if ((paramObject instanceof Breadcrumb))
    {
      paramObject = (Breadcrumb)paramObject;
      localObject = new HashMap();
      if (paramObject.getMetadata() != null)
      {
        Iterator localIterator = paramObject.getMetadata().entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (localEntry.getValue() != null) {
            ((Map)localObject).put(makeSafe((String)localEntry.getKey()), makeSafe((String)localEntry.getValue()));
          }
        }
      }
      addBreadcrumb(paramObject.getName(), paramObject.getType().toString(), paramObject.getTimestamp(), localObject);
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Attempted to add non-breadcrumb: ");
    ((StringBuilder)localObject).append(paramObject);
    warn(((StringBuilder)localObject).toString());
  }
  
  private void handleAddMetadata(Object paramObject)
  {
    if ((paramObject instanceof List))
    {
      localObject = (List)paramObject;
      if ((((List)localObject).size() == 3) && ((((List)localObject).get(0) instanceof String)) && ((((List)localObject).get(1) instanceof String)))
      {
        String str1 = makeSafe((String)((List)localObject).get(0));
        String str2 = makeSafe((String)((List)localObject).get(1));
        if ((((List)localObject).get(2) instanceof String))
        {
          addMetadataString(str1, str2, makeSafe((String)((List)localObject).get(2)));
          return;
        }
        if ((((List)localObject).get(2) instanceof Boolean))
        {
          addMetadataBoolean(str1, str2, ((Boolean)((List)localObject).get(2)).booleanValue());
          return;
        }
        if ((((List)localObject).get(2) instanceof Number)) {
          addMetadataDouble(str1, str2, ((Number)((List)localObject).get(2)).doubleValue());
        }
      }
      else if ((((List)localObject).size() == 2) && ((((List)localObject).get(0) instanceof String)) && ((((List)localObject).get(1) instanceof String)))
      {
        removeMetadata(makeSafe((String)((List)localObject).get(0)), makeSafe((String)((List)localObject).get(1)));
        return;
      }
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("ADD_METADATA object is invalid: ");
    ((StringBuilder)localObject).append(paramObject);
    warn(((StringBuilder)localObject).toString());
  }
  
  private void handleAppVersionChange(Object paramObject)
  {
    if ((paramObject instanceof String))
    {
      updateAppVersion(makeSafe((String)paramObject));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_APP_VERSION object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleBuildUUIDChange(Object paramObject)
  {
    if (paramObject == null)
    {
      updateBuildUUID("");
      return;
    }
    if ((paramObject instanceof String))
    {
      updateBuildUUID(makeSafe((String)paramObject));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_BUILD_UUID object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleClearMetadataTab(Object paramObject)
  {
    if ((paramObject instanceof String))
    {
      clearMetadataTab(makeSafe((String)paramObject));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CLEAR_METADATA_TAB object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleContextChange(Object paramObject)
  {
    if (paramObject == null)
    {
      updateContext("");
      return;
    }
    if ((paramObject instanceof String))
    {
      updateContext(makeSafe((String)paramObject));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_CONTEXT object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleForegroundActivityChange(Object paramObject)
  {
    if ((paramObject instanceof List))
    {
      localObject = (List)paramObject;
      if (((List)localObject).size() == 2)
      {
        updateInForeground(((Boolean)((List)localObject).get(0)).booleanValue(), makeSafe((String)((List)localObject).get(1)));
        return;
      }
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("UPDATE_IN_FOREGROUND object is invalid: ");
    ((StringBuilder)localObject).append(paramObject);
    warn(((StringBuilder)localObject).toString());
  }
  
  private void handleInstallMessage(Object paramObject)
  {
    lock.lock();
    try
    {
      boolean bool = installed.get();
      StringBuilder localStringBuilder;
      if (bool)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Received duplicate setup message with arg: ");
        localStringBuilder.append(paramObject);
        warn(localStringBuilder.toString());
      }
      else
      {
        bool = paramObject instanceof List;
        if (bool)
        {
          paramObject = (List)paramObject;
          int i = paramObject.size();
          if (i > 0)
          {
            bool = paramObject.get(0) instanceof Configuration;
            if (bool)
            {
              paramObject = (Configuration)paramObject.get(0);
              localStringBuilder = new StringBuilder();
              localStringBuilder.append(reportDirectory);
              localStringBuilder.append(UUID.randomUUID().toString());
              localStringBuilder.append(".crash");
              install(localStringBuilder.toString(), paramObject.getDetectNdkCrashes(), Build.VERSION.SDK_INT, is32bit());
              installed.set(true);
            }
          }
        }
        else
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Received install message with incorrect arg: ");
          localStringBuilder.append(paramObject);
          warn(localStringBuilder.toString());
        }
      }
      lock.unlock();
      return;
    }
    catch (Throwable paramObject)
    {
      lock.unlock();
      throw paramObject;
    }
  }
  
  private void handleLowMemoryChange(Object paramObject)
  {
    if ((paramObject instanceof Boolean))
    {
      updateLowMemory(((Boolean)paramObject).booleanValue());
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_LOW_MEMORY object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleNotifyReleaseStagesChange(Object paramObject)
  {
    if ((paramObject instanceof Configuration))
    {
      enableOrDisableReportingIfNeeded((Configuration)paramObject);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_NOTIFY_RELEASE_STAGES object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleOrientationChange(Object paramObject)
  {
    if ((paramObject instanceof Integer))
    {
      updateOrientation(((Integer)paramObject).intValue());
      return;
    }
    if (paramObject == null)
    {
      warn("UPDATE_ORIENTATION object is null");
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_ORIENTATION object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleReleaseStageChange(Object paramObject)
  {
    if ((paramObject instanceof Configuration))
    {
      paramObject = (Configuration)paramObject;
      updateReleaseStage(makeSafe(paramObject.getReleaseStage()));
      enableOrDisableReportingIfNeeded(paramObject);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_RELEASE_STAGE object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleRemoveMetadata(Object paramObject)
  {
    if ((paramObject instanceof List))
    {
      localObject = (List)paramObject;
      if (((List)localObject).size() == 2)
      {
        removeMetadata(makeSafe((String)((List)localObject).get(0)), makeSafe((String)((List)localObject).get(1)));
        return;
      }
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("REMOVE_METADATA object is invalid: ");
    ((StringBuilder)localObject).append(paramObject);
    warn(((StringBuilder)localObject).toString());
  }
  
  private void handleStartSession(Object paramObject)
  {
    if ((paramObject instanceof List))
    {
      Object localObject4 = (List)paramObject;
      if (((List)localObject4).size() == 4)
      {
        localObject1 = ((List)localObject4).get(0);
        Object localObject2 = ((List)localObject4).get(1);
        Object localObject3 = ((List)localObject4).get(2);
        localObject4 = ((List)localObject4).get(3);
        if (((localObject1 instanceof String)) && ((localObject2 instanceof String)) && ((localObject3 instanceof Integer)) && ((localObject4 instanceof Integer)))
        {
          startedSession((String)localObject1, (String)localObject2, ((Integer)localObject3).intValue(), ((Integer)localObject4).intValue());
          return;
        }
      }
    }
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("START_SESSION object is invalid: ");
    ((StringBuilder)localObject1).append(paramObject);
    warn(((StringBuilder)localObject1).toString());
  }
  
  private void handleStopSession() {}
  
  private void handleUpdateMetadata(Object paramObject)
  {
    if ((paramObject instanceof Map))
    {
      updateMetadata(paramObject);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_METADATA object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleUserEmailChange(Object paramObject)
  {
    if (paramObject == null)
    {
      updateUserEmail("");
      return;
    }
    if ((paramObject instanceof String))
    {
      updateUserEmail(makeSafe((String)paramObject));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_USER_EMAIL object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleUserIdChange(Object paramObject)
  {
    if (paramObject == null)
    {
      updateUserId("");
      return;
    }
    if ((paramObject instanceof String))
    {
      updateUserId(makeSafe((String)paramObject));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_USER_ID object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  private void handleUserNameChange(Object paramObject)
  {
    if (paramObject == null)
    {
      updateUserName("");
      return;
    }
    if ((paramObject instanceof String))
    {
      updateUserName(makeSafe((String)paramObject));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UPDATE_USER_NAME object is invalid: ");
    localStringBuilder.append(paramObject);
    warn(localStringBuilder.toString());
  }
  
  public static native void install(String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2);
  
  private boolean is32bit()
  {
    String[] arrayOfString = NativeInterface.getCpuAbi();
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (arrayOfString[i].contains("64")) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private String makeSafe(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return new String(paramString.getBytes(Charset.defaultCharset()));
  }
  
  private NativeInterface.Message parseMessage(Object paramObject)
  {
    StringBuilder localStringBuilder;
    if ((paramObject instanceof NativeInterface.Message))
    {
      paramObject = (NativeInterface.Message)paramObject;
      if (type != NativeInterface.MessageType.INSTALL)
      {
        if (!installed.get())
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Received message before INSTALL: ");
          localStringBuilder.append(type);
          warn(localStringBuilder.toString());
          return null;
        }
      }
      else {
        return paramObject;
      }
    }
    else
    {
      if (paramObject == null)
      {
        warn("Received observable update with null Message");
        return null;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Received observable update object which is not instance of Message: ");
      localStringBuilder.append(paramObject.getClass());
      warn(localStringBuilder.toString());
      return null;
    }
    return paramObject;
  }
  
  public static native void removeMetadata(String paramString1, String paramString2);
  
  public static native void startedSession(String paramString1, String paramString2, int paramInt1, int paramInt2);
  
  public static native void stoppedSession();
  
  public static native void updateAppVersion(String paramString);
  
  public static native void updateBuildUUID(String paramString);
  
  public static native void updateContext(String paramString);
  
  public static native void updateInForeground(boolean paramBoolean, String paramString);
  
  public static native void updateLowMemory(boolean paramBoolean);
  
  public static native void updateMetadata(Object paramObject);
  
  public static native void updateOrientation(int paramInt);
  
  public static native void updateReleaseStage(String paramString);
  
  public static native void updateUserEmail(String paramString);
  
  public static native void updateUserId(String paramString);
  
  public static native void updateUserName(String paramString);
  
  private void warn(String paramString)
  {
    if (loggingEnabled) {
      Log.w("BugsnagNDK:NativeBridge", paramString);
    }
  }
  
  public void update(Observable paramObservable, Object paramObject)
  {
    paramObservable = parseMessage(paramObject);
    if (paramObservable == null) {
      return;
    }
    paramObject = value;
    switch (1.$SwitchMap$com$bugsnag$android$NativeInterface$MessageType[type.ordinal()])
    {
    default: 
      return;
    case 25: 
      handleUserEmailChange(paramObject);
      return;
    case 24: 
      handleUserNameChange(paramObject);
      return;
    case 23: 
      handleUserIdChange(paramObject);
      return;
    case 22: 
      handleNotifyReleaseStagesChange(paramObject);
      return;
    case 21: 
      handleReleaseStageChange(paramObject);
      return;
    case 20: 
      handleOrientationChange(paramObject);
      return;
    case 19: 
      handleUpdateMetadata(paramObject);
      return;
    case 18: 
      handleLowMemoryChange(paramObject);
      return;
    case 17: 
      handleForegroundActivityChange(paramObject);
      return;
    case 16: 
      handleContextChange(paramObject);
      return;
    case 15: 
      handleBuildUUIDChange(paramObject);
      return;
    case 14: 
      handleAppVersionChange(paramObject);
      return;
    case 13: 
      stoppedSession();
      return;
    case 12: 
      handleStartSession(paramObject);
      return;
    case 11: 
      handleRemoveMetadata(paramObject);
      return;
    case 10: 
      addUnhandledEvent();
      return;
    case 9: 
      addHandledEvent();
      return;
    case 8: 
      handleClearMetadataTab(paramObject);
      return;
    case 7: 
      clearBreadcrumbs();
      return;
    case 6: 
      handleAddMetadata(paramObject);
      return;
    case 5: 
      handleAddBreadcrumb(paramObject);
      return;
    case 4: 
      deliverPendingReports();
      return;
    case 3: 
      disableCrashReporting();
      return;
    case 2: 
      enableCrashReporting();
      return;
    }
    handleInstallMessage(paramObject);
  }
}
