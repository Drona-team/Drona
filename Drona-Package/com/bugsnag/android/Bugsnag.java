package com.bugsnag.android;

import android.annotation.SuppressLint;
import android.content.Context;
import java.util.Map;

public final class Bugsnag
{
  @SuppressLint({"StaticFieldLeak"})
  static Client client;
  private static final Object lock = new Object();
  
  private Bugsnag() {}
  
  public static void addToTab(String paramString1, String paramString2, Object paramObject)
  {
    getClient().addToTab(paramString1, paramString2, paramObject);
  }
  
  public static void beforeNotify(BeforeNotify paramBeforeNotify)
  {
    getClient().beforeNotify(paramBeforeNotify);
  }
  
  public static void beforeRecordBreadcrumb(BeforeRecordBreadcrumb paramBeforeRecordBreadcrumb)
  {
    getClient().beforeRecordBreadcrumb(paramBeforeRecordBreadcrumb);
  }
  
  public static void clearBreadcrumbs()
  {
    getClient().clearBreadcrumbs();
  }
  
  public static void clearTab(String paramString)
  {
    getClient().clearTab(paramString);
  }
  
  public static void clearUser()
  {
    getClient().clearUser();
  }
  
  public static void disableExceptionHandler()
  {
    getClient().disableExceptionHandler();
  }
  
  public static void enableExceptionHandler()
  {
    getClient().enableExceptionHandler();
  }
  
  public static Client getClient()
  {
    if (client != null) {
      return client;
    }
    throw new IllegalStateException("You must call Bugsnag.init before any other Bugsnag methods");
  }
  
  public static String getContext()
  {
    return getClient().getContext();
  }
  
  public static MetaData getMetaData()
  {
    return getClient().getMetaData();
  }
  
  public static Client init(Context paramContext)
  {
    return init(paramContext, null, true);
  }
  
  public static Client init(Context paramContext, Configuration paramConfiguration)
  {
    Object localObject = lock;
    try
    {
      if (client == null) {
        client = new Client(paramContext, paramConfiguration);
      } else {
        logClientInitWarning();
      }
      return client;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public static Client init(Context paramContext, String paramString)
  {
    return init(paramContext, paramString, true);
  }
  
  public static Client init(Context paramContext, String paramString, boolean paramBoolean)
  {
    return init(paramContext, ConfigFactory.createNewConfiguration(paramContext, paramString, paramBoolean));
  }
  
  public static void internalClientNotify(Throwable paramThrowable, Map paramMap, boolean paramBoolean, Callback paramCallback)
  {
    getClient().internalClientNotify(paramThrowable, paramMap, paramBoolean, paramCallback);
  }
  
  public static void leaveBreadcrumb(String paramString)
  {
    getClient().leaveBreadcrumb(paramString);
  }
  
  public static void leaveBreadcrumb(String paramString, BreadcrumbType paramBreadcrumbType, Map paramMap)
  {
    getClient().leaveBreadcrumb(paramString, paramBreadcrumbType, paramMap);
  }
  
  private static void logClientInitWarning()
  {
    Logger.warn("It appears that Bugsnag.init() was called more than once. Subsequent calls have no effect, but may indicate that Bugsnag is not integrated in an Application subclass, which can lead to undesired behaviour.");
  }
  
  public static void notify(String paramString1, String paramString2, final String paramString3, StackTraceElement[] paramArrayOfStackTraceElement, Severity paramSeverity, final MetaData paramMetaData)
  {
    getClient().notify(paramString1, paramString2, paramArrayOfStackTraceElement, new Callback()
    {
      public void beforeNotify(Report paramAnonymousReport)
      {
        paramAnonymousReport.getError().setSeverity(val$finalSeverity);
        paramAnonymousReport.getError().setMetaData(paramMetaData);
        paramAnonymousReport.getError().setContext(paramString3);
      }
    });
  }
  
  public static void notify(String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement, Callback paramCallback)
  {
    getClient().notify(paramString1, paramString2, paramArrayOfStackTraceElement, paramCallback);
  }
  
  public static void notify(String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement, Severity paramSeverity, final MetaData paramMetaData)
  {
    getClient().notify(paramString1, paramString2, paramArrayOfStackTraceElement, new Callback()
    {
      public void beforeNotify(Report paramAnonymousReport)
      {
        paramAnonymousReport.getError().setSeverity(val$finalSeverity);
        paramAnonymousReport.getError().setMetaData(paramMetaData);
      }
    });
  }
  
  public static void notify(Throwable paramThrowable)
  {
    getClient().notify(paramThrowable);
  }
  
  public static void notify(Throwable paramThrowable, Callback paramCallback)
  {
    getClient().notify(paramThrowable, paramCallback);
  }
  
  public static void notify(Throwable paramThrowable, MetaData paramMetaData)
  {
    getClient().notify(paramThrowable, new Callback()
    {
      public void beforeNotify(Report paramAnonymousReport)
      {
        paramAnonymousReport.getError().setMetaData(val$metaData);
      }
    });
  }
  
  public static void notify(Throwable paramThrowable, Severity paramSeverity)
  {
    getClient().notify(paramThrowable, paramSeverity);
  }
  
  public static void notify(Throwable paramThrowable, Severity paramSeverity, final MetaData paramMetaData)
  {
    getClient().notify(paramThrowable, new Callback()
    {
      public void beforeNotify(Report paramAnonymousReport)
      {
        paramAnonymousReport.getError().setSeverity(val$severity);
        paramAnonymousReport.getError().setMetaData(paramMetaData);
      }
    });
  }
  
  public static boolean resumeSession()
  {
    return getClient().resumeSession();
  }
  
  public static void setAppVersion(String paramString)
  {
    getClient().setAppVersion(paramString);
  }
  
  public static void setAutoCaptureSessions(boolean paramBoolean)
  {
    getClient().setAutoCaptureSessions(paramBoolean);
  }
  
  public static void setBuildUUID(String paramString)
  {
    getClient().setBuildUUID(paramString);
  }
  
  public static void setContext(String paramString)
  {
    getClient().setContext(paramString);
  }
  
  public static void setEndpoint(String paramString)
  {
    getClient().setEndpoint(paramString);
  }
  
  public static void setErrorReportApiClient(ErrorReportApiClient paramErrorReportApiClient)
  {
    getClient().setErrorReportApiClient(paramErrorReportApiClient);
  }
  
  public static void setFilters(String... paramVarArgs)
  {
    getClient().setFilters(paramVarArgs);
  }
  
  public static void setIgnoreClasses(String... paramVarArgs)
  {
    getClient().setIgnoreClasses(paramVarArgs);
  }
  
  public static void setLoggingEnabled(boolean paramBoolean)
  {
    getClient().setLoggingEnabled(paramBoolean);
  }
  
  public static void setMaxBreadcrumbs(int paramInt)
  {
    getClientconfig.setMaxBreadcrumbs(paramInt);
  }
  
  public static void setMetaData(MetaData paramMetaData)
  {
    getClient().setMetaData(paramMetaData);
  }
  
  public static void setNotifyReleaseStages(String... paramVarArgs)
  {
    getClient().setNotifyReleaseStages(paramVarArgs);
  }
  
  public static void setProjectPackages(String... paramVarArgs)
  {
    getClient().setProjectPackages(paramVarArgs);
  }
  
  public static void setReleaseStage(String paramString)
  {
    getClient().setReleaseStage(paramString);
  }
  
  public static void setSendThreads(boolean paramBoolean)
  {
    getClient().setSendThreads(paramBoolean);
  }
  
  public static void setSessionTrackingApiClient(SessionTrackingApiClient paramSessionTrackingApiClient)
  {
    getClient().setSessionTrackingApiClient(paramSessionTrackingApiClient);
  }
  
  public static void setUser(String paramString1, String paramString2, String paramString3)
  {
    getClient().setUser(paramString1, paramString2, paramString3);
  }
  
  public static void setUserEmail(String paramString)
  {
    getClient().setUserEmail(paramString);
  }
  
  public static void setUserId(String paramString)
  {
    getClient().setUserId(paramString);
  }
  
  public static void setUserName(String paramString)
  {
    getClient().setUserName(paramString);
  }
  
  public static void startSession()
  {
    getClient().startSession();
  }
  
  public static void stopSession()
  {
    getClient().stopSession();
  }
}
