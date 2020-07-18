package com.bugsnag.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Error
  implements JsonStream.Streamable
{
  @NonNull
  private Map<String, Object> appData = new HashMap();
  private Breadcrumbs breadcrumbs;
  @NonNull
  final Configuration config;
  @Nullable
  private String context;
  @NonNull
  private Map<String, Object> deviceData = new HashMap();
  private final BugsnagException exception;
  private final Exceptions exceptions;
  @Nullable
  private String groupingHash;
  private final HandledState handledState;
  private boolean incomplete = false;
  @NonNull
  private MetaData metaData = new MetaData();
  private String[] projectPackages;
  private final Session session;
  @Nullable
  private Severity severity;
  private final ThreadState threadState;
  @NonNull
  private User user = new User();
  
  Error(Configuration paramConfiguration, Throwable paramThrowable, HandledState paramHandledState, Severity paramSeverity, Session paramSession, ThreadState paramThreadState)
  {
    threadState = paramThreadState;
    config = paramConfiguration;
    if ((paramThrowable instanceof BugsnagException)) {
      exception = ((BugsnagException)paramThrowable);
    } else {
      exception = new BugsnagException(paramThrowable);
    }
    handledState = paramHandledState;
    severity = paramSeverity;
    session = paramSession;
    projectPackages = paramConfiguration.getProjectPackages();
    exceptions = new Exceptions(paramConfiguration, exception);
  }
  
  public void addToTab(String paramString1, String paramString2, Object paramObject)
  {
    metaData.addToTab(paramString1, paramString2, paramObject);
  }
  
  public void clearTab(String paramString)
  {
    metaData.clearTab(paramString);
  }
  
  Map getAppData()
  {
    return appData;
  }
  
  public String getContext()
  {
    return context;
  }
  
  public Map getDeviceData()
  {
    return deviceData;
  }
  
  public Throwable getException()
  {
    return exception;
  }
  
  public String getExceptionMessage()
  {
    String str = exception.getMessage();
    if (str != null) {
      return str;
    }
    return "";
  }
  
  public String getExceptionName()
  {
    return exception.getName();
  }
  
  Exceptions getExceptions()
  {
    return exceptions;
  }
  
  public String getGroupingHash()
  {
    return groupingHash;
  }
  
  public HandledState getHandledState()
  {
    return handledState;
  }
  
  public MetaData getMetaData()
  {
    return metaData;
  }
  
  String[] getProjectPackages()
  {
    return projectPackages;
  }
  
  Session getSession()
  {
    return session;
  }
  
  public Severity getSeverity()
  {
    return severity;
  }
  
  public User getUser()
  {
    return user;
  }
  
  boolean isIncomplete()
  {
    return incomplete;
  }
  
  void setAppData(Map paramMap)
  {
    appData = paramMap;
  }
  
  void setBreadcrumbs(Breadcrumbs paramBreadcrumbs)
  {
    breadcrumbs = paramBreadcrumbs;
  }
  
  public void setContext(String paramString)
  {
    context = paramString;
  }
  
  void setDeviceData(Map paramMap)
  {
    deviceData = paramMap;
  }
  
  public void setDeviceId(String paramString)
  {
    deviceData.put("id", paramString);
  }
  
  public void setExceptionMessage(String paramString)
  {
    exception.setMessage(paramString);
  }
  
  public void setExceptionName(String paramString)
  {
    exception.setName(paramString);
  }
  
  public void setGroupingHash(String paramString)
  {
    groupingHash = paramString;
  }
  
  void setIncomplete(boolean paramBoolean)
  {
    incomplete = paramBoolean;
  }
  
  public void setMetaData(MetaData paramMetaData)
  {
    if (paramMetaData == null)
    {
      metaData = new MetaData();
      return;
    }
    metaData = paramMetaData;
  }
  
  void setProjectPackages(String[] paramArrayOfString)
  {
    projectPackages = paramArrayOfString;
    if (exceptions != null) {
      exceptions.setProjectPackages(paramArrayOfString);
    }
  }
  
  public void setSeverity(Severity paramSeverity)
  {
    if (paramSeverity != null)
    {
      severity = paramSeverity;
      handledState.setCurrentSeverity(paramSeverity);
    }
  }
  
  void setUser(User paramUser)
  {
    user = paramUser;
  }
  
  public void setUser(String paramString1, String paramString2, String paramString3)
  {
    user = new User(paramString1, paramString2, paramString3);
  }
  
  public void setUserEmail(String paramString)
  {
    user = new User(user);
    user.setEmail(paramString);
  }
  
  public void setUserId(String paramString)
  {
    user = new User(user);
    user.setId(paramString);
  }
  
  public void setUserName(String paramString)
  {
    user = new User(user);
    user.setName(paramString);
  }
  
  boolean shouldIgnoreClass()
  {
    return config.shouldIgnoreClass(getExceptionName());
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    Object localObject = config.getMetaData();
    int i = 0;
    localObject = MetaData.merge(new MetaData[] { localObject, metaData });
    paramJsonStream.beginObject();
    paramJsonStream.name("context").value(context);
    paramJsonStream.name("metaData").value((JsonStream.Streamable)localObject);
    paramJsonStream.name("severity").value(severity);
    paramJsonStream.name("severityReason").value(handledState);
    paramJsonStream.name("unhandled").value(handledState.isUnhandled());
    paramJsonStream.name("incomplete").value(incomplete);
    if (projectPackages != null)
    {
      paramJsonStream.name("projectPackages").beginArray();
      localObject = projectPackages;
      int j = localObject.length;
      while (i < j)
      {
        paramJsonStream.value(localObject[i]);
        i += 1;
      }
      paramJsonStream.endArray();
    }
    paramJsonStream.name("exceptions").value(exceptions);
    paramJsonStream.name("user").value(user);
    paramJsonStream.name("app").value(appData);
    paramJsonStream.name("device").value(deviceData);
    paramJsonStream.name("breadcrumbs").value(breadcrumbs);
    paramJsonStream.name("groupingHash").value(groupingHash);
    if (config.getSendThreads()) {
      paramJsonStream.name("threads").value(threadState);
    }
    if (session != null)
    {
      paramJsonStream.name("session").beginObject();
      paramJsonStream.name("id").value(session.getId());
      paramJsonStream.name("startedAt").value(DateUtils.toIso8601(session.getStartedAt()));
      paramJsonStream.name("events").beginObject();
      paramJsonStream.name("handled").value(session.getHandledCount());
      paramJsonStream.name("unhandled").value(session.getUnhandledCount());
      paramJsonStream.endObject();
      paramJsonStream.endObject();
    }
    paramJsonStream.endObject();
  }
  
  static class Builder
  {
    private String attributeValue;
    private final Configuration config;
    private final Throwable exception;
    private MetaData metaData;
    private final SessionTracker sessionTracker;
    private Severity severity = Severity.WARNING;
    private String severityReasonType;
    private final ThreadState threadState;
    
    Builder(Configuration paramConfiguration, String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement, SessionTracker paramSessionTracker, Thread paramThread)
    {
      this(paramConfiguration, new BugsnagException(paramString1, paramString2, paramArrayOfStackTraceElement), paramSessionTracker, paramThread, false);
    }
    
    Builder(Configuration paramConfiguration, Throwable paramThrowable, SessionTracker paramSessionTracker, Thread paramThread, boolean paramBoolean)
    {
      Throwable localThrowable;
      if (paramBoolean) {
        localThrowable = paramThrowable;
      } else {
        localThrowable = null;
      }
      threadState = new ThreadState(paramConfiguration, paramThread, Thread.getAllStackTraces(), localThrowable);
      config = paramConfiguration;
      exception = paramThrowable;
      severityReasonType = "userSpecifiedSeverity";
      sessionTracker = paramSessionTracker;
    }
    
    private Session getSession(HandledState paramHandledState)
    {
      if (sessionTracker == null) {
        return null;
      }
      Session localSession = sessionTracker.getCurrentSession();
      if (localSession == null) {
        return null;
      }
      if ((config.getAutoCaptureSessions()) || (!localSession.isAutoCaptured()))
      {
        if (paramHandledState.isUnhandled()) {
          return sessionTracker.incrementUnhandledAndCopy();
        }
        return sessionTracker.incrementHandledAndCopy();
      }
      return null;
    }
    
    Builder attributeValue(String paramString)
    {
      attributeValue = paramString;
      return this;
    }
    
    Error build()
    {
      Object localObject = HandledState.newInstance(severityReasonType, severity, attributeValue);
      Session localSession = getSession((HandledState)localObject);
      localObject = new Error(config, exception, (HandledState)localObject, severity, localSession, threadState);
      if (metaData != null) {
        ((Error)localObject).setMetaData(metaData);
      }
      return localObject;
    }
    
    Builder metaData(MetaData paramMetaData)
    {
      metaData = paramMetaData;
      return this;
    }
    
    Builder severity(Severity paramSeverity)
    {
      severity = paramSeverity;
      return this;
    }
    
    Builder severityReasonType(String paramString)
    {
      severityReasonType = paramString;
      return this;
    }
  }
}
