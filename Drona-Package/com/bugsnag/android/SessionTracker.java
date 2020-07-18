package com.bugsnag.android;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

class SessionTracker
  extends Observable
  implements Application.ActivityLifecycleCallbacks
{
  private static final int DEFAULT_TIMEOUT_MS = 30000;
  private static final String KEY_LIFECYCLE_CALLBACK = "ActivityLifecycle";
  final Client client;
  final Configuration configuration;
  private final AtomicReference<Session> currentSession = new AtomicReference();
  private final Semaphore flushingRequest = new Semaphore(1);
  private final Collection<String> foregroundActivities = new ConcurrentLinkedQueue();
  private final ForegroundDetector foregroundDetector;
  private final AtomicLong lastEnteredForegroundMs = new AtomicLong(0L);
  private final AtomicLong lastExitedForegroundMs = new AtomicLong(0L);
  final SessionStore sessionStore;
  private final long timeoutMs;
  
  SessionTracker(Configuration paramConfiguration, Client paramClient, long paramLong, SessionStore paramSessionStore)
  {
    configuration = paramConfiguration;
    client = paramClient;
    timeoutMs = paramLong;
    sessionStore = paramSessionStore;
    foregroundDetector = new ForegroundDetector(appContext);
    notifyNdkInForeground();
  }
  
  SessionTracker(Configuration paramConfiguration, Client paramClient, SessionStore paramSessionStore)
  {
    this(paramConfiguration, paramClient, 30000L, paramSessionStore);
  }
  
  private String getActivityName(Activity paramActivity)
  {
    return paramActivity.getClass().getSimpleName();
  }
  
  private String getReleaseStage()
  {
    return MapUtils.getStringFromMap("releaseStage", client.appData.getAppDataSummary());
  }
  
  private void leaveBreadcrumb(String paramString1, String paramString2)
  {
    if (configuration.isAutomaticallyCollectingBreadcrumbs())
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("ActivityLifecycle", paramString2);
      paramString2 = client;
      BreadcrumbType localBreadcrumbType = BreadcrumbType.NAVIGATION;
      try
      {
        paramString2.leaveBreadcrumb(paramString1, localBreadcrumbType, localHashMap);
        return;
      }
      catch (Exception paramString1)
      {
        paramString2 = new StringBuilder();
        paramString2.append("Failed to leave breadcrumb in SessionTracker: ");
        paramString2.append(paramString1.getMessage());
        Logger.warn(paramString2.toString());
      }
    }
  }
  
  private void notifyNdkInForeground()
  {
    Boolean localBoolean = isInForeground();
    if (localBoolean != null) {
      notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.UPDATE_IN_FOREGROUND, Arrays.asList(new Serializable[] { localBoolean, getContextActivity() })));
    }
  }
  
  private void notifySessionStartObserver(Session paramSession)
  {
    setChanged();
    String str = DateUtils.toIso8601(paramSession.getStartedAt());
    notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.START_SESSION, Arrays.asList(new Serializable[] { paramSession.getId(), str, Integer.valueOf(paramSession.getHandledCount()), Integer.valueOf(paramSession.getUnhandledCount()) })));
  }
  
  private void trackSessionIfNeeded(final Session paramSession)
  {
    if ((configuration.shouldNotifyForReleaseStage(getReleaseStage())) && ((configuration.getAutoCaptureSessions()) || (!paramSession.isAutoCaptured())) && (paramSession.isTracked().compareAndSet(false, true)))
    {
      notifySessionStartObserver(paramSession);
      Configuration localConfiguration = configuration;
      try
      {
        localConfiguration.getSessionEndpoint();
        Async.runAsync(new Runnable()
        {
          public void run()
          {
            flushStoredSessions();
            SessionTrackingPayload localSessionTrackingPayload = new SessionTrackingPayload(paramSession, null, client.appData, client.deviceData);
            Object localObject1 = configuration;
            try
            {
              localObject1 = ((Configuration)localObject1).getSessionCallbacks().iterator();
              for (;;)
              {
                boolean bool = ((Iterator)localObject1).hasNext();
                if (!bool) {
                  break;
                }
                localObject2 = ((Iterator)localObject1).next();
                localObject2 = (BeforeSendSession)localObject2;
                ((BeforeSendSession)localObject2).beforeSendSession(localSessionTrackingPayload);
              }
              localObject1 = configuration;
              localObject1 = ((Configuration)localObject1).getDelivery();
              Object localObject2 = configuration;
              ((Delivery)localObject1).deliver(localSessionTrackingPayload, (Configuration)localObject2);
              return;
            }
            catch (Exception localException)
            {
              Logger.warn("Dropping invalid session tracking payload", localException);
              return;
            }
            catch (DeliveryFailureException localDeliveryFailureException)
            {
              Logger.warn("Storing session payload for future delivery", localDeliveryFailureException);
              sessionStore.write(paramSession);
            }
          }
        });
        return;
      }
      catch (RejectedExecutionException localRejectedExecutionException)
      {
        for (;;) {}
      }
      sessionStore.write(paramSession);
      return;
    }
  }
  
  void flushStoredSessions()
  {
    if (flushingRequest.tryAcquire(1)) {
      try
      {
        List localList = sessionStore.findStoredFiles();
        boolean bool = localList.isEmpty();
        if (!bool)
        {
          Object localObject1 = new SessionTrackingPayload(null, localList, client.appData, client.deviceData);
          Object localObject2 = configuration;
          try
          {
            localObject2 = ((Configuration)localObject2).getDelivery();
            Configuration localConfiguration = configuration;
            ((Delivery)localObject2).deliver((SessionTrackingPayload)localObject1, localConfiguration);
            localObject1 = sessionStore;
            ((FileStore)localObject1).deleteStoredFiles(localList);
          }
          catch (Exception localException)
          {
            Logger.warn("Deleting invalid session tracking payload", localException);
            sessionStore.deleteStoredFiles(localList);
          }
          catch (DeliveryFailureException localDeliveryFailureException)
          {
            sessionStore.cancelQueuedFiles(localList);
            Logger.warn("Leaving session payload for future delivery", localDeliveryFailureException);
          }
        }
        flushingRequest.release(1);
        return;
      }
      catch (Throwable localThrowable)
      {
        flushingRequest.release(1);
        throw localThrowable;
      }
    }
  }
  
  String getContextActivity()
  {
    if (foregroundActivities.isEmpty()) {
      return null;
    }
    int i = foregroundActivities.size();
    return ((String[])foregroundActivities.toArray(new String[i]))[(i - 1)];
  }
  
  Session getCurrentSession()
  {
    Session localSession = (Session)currentSession.get();
    if ((localSession != null) && (!isStopped.get())) {
      return localSession;
    }
    return null;
  }
  
  Long getDurationInForegroundMs(long paramLong)
  {
    long l = lastEnteredForegroundMs.get();
    Boolean localBoolean = isInForeground();
    if (localBoolean == null) {
      return null;
    }
    if ((localBoolean.booleanValue()) && (l != 0L)) {
      paramLong -= l;
    } else {
      paramLong = 0L;
    }
    if (paramLong <= 0L) {
      paramLong = 0L;
    }
    return Long.valueOf(paramLong);
  }
  
  Session incrementHandledAndCopy()
  {
    Session localSession = getCurrentSession();
    if (localSession != null) {
      return localSession.incrementHandledAndCopy();
    }
    return null;
  }
  
  Session incrementUnhandledAndCopy()
  {
    Session localSession = getCurrentSession();
    if (localSession != null) {
      return localSession.incrementUnhandledAndCopy();
    }
    return null;
  }
  
  Boolean isInForeground()
  {
    return foregroundDetector.isInForeground();
  }
  
  void leaveLifecycleBreadcrumb(String paramString1, String paramString2)
  {
    leaveBreadcrumb(paramString1, paramString2);
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    leaveLifecycleBreadcrumb(getActivityName(paramActivity), "onCreate()");
  }
  
  public void onActivityDestroyed(Activity paramActivity)
  {
    leaveLifecycleBreadcrumb(getActivityName(paramActivity), "onDestroy()");
  }
  
  public void onActivityPaused(Activity paramActivity)
  {
    leaveLifecycleBreadcrumb(getActivityName(paramActivity), "onPause()");
  }
  
  public void onActivityResumed(Activity paramActivity)
  {
    leaveLifecycleBreadcrumb(getActivityName(paramActivity), "onResume()");
  }
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    leaveLifecycleBreadcrumb(getActivityName(paramActivity), "onSaveInstanceState()");
  }
  
  public void onActivityStarted(Activity paramActivity)
  {
    paramActivity = getActivityName(paramActivity);
    leaveLifecycleBreadcrumb(paramActivity, "onStart()");
    updateForegroundTracker(paramActivity, true, System.currentTimeMillis());
  }
  
  public void onActivityStopped(Activity paramActivity)
  {
    paramActivity = getActivityName(paramActivity);
    leaveLifecycleBreadcrumb(paramActivity, "onStop()");
    updateForegroundTracker(paramActivity, false, System.currentTimeMillis());
  }
  
  void onAutoCaptureEnabled()
  {
    Session localSession = (Session)currentSession.get();
    if ((localSession != null) && (!foregroundActivities.isEmpty())) {
      trackSessionIfNeeded(localSession);
    }
  }
  
  Session registerExistingSession(Date paramDate, String paramString, User paramUser, int paramInt1, int paramInt2)
  {
    Object localObject = null;
    if ((paramDate != null) && (paramString != null))
    {
      paramDate = new Session(paramString, paramDate, paramUser, paramInt1, paramInt2);
      notifySessionStartObserver(paramDate);
    }
    else
    {
      setChanged();
      notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.STOP_SESSION, null));
      paramDate = localObject;
    }
    currentSession.set(paramDate);
    return paramDate;
  }
  
  boolean resumeSession()
  {
    Session localSession = (Session)currentSession.get();
    boolean bool = false;
    if (localSession == null) {
      localSession = startSession(false);
    } else {
      bool = isStopped.compareAndSet(true, false);
    }
    if (localSession != null) {
      notifySessionStartObserver(localSession);
    }
    return bool;
  }
  
  void startFirstSession(Activity paramActivity)
  {
    if ((Session)currentSession.get() == null)
    {
      long l = System.currentTimeMillis();
      lastEnteredForegroundMs.set(l);
      startNewSession(new Date(l), client.getUser(), true);
      foregroundActivities.add(getActivityName(paramActivity));
    }
  }
  
  Session startNewSession(Date paramDate, User paramUser, boolean paramBoolean)
  {
    if (configuration.getSessionEndpoint() == null)
    {
      Logger.warn("The session tracking endpoint has not been set. Session tracking is disabled");
      return null;
    }
    paramDate = new Session(UUID.randomUUID().toString(), paramDate, paramUser, paramBoolean);
    currentSession.set(paramDate);
    trackSessionIfNeeded(paramDate);
    return paramDate;
  }
  
  Session startSession(boolean paramBoolean)
  {
    return startNewSession(new Date(), client.getUser(), paramBoolean);
  }
  
  void stopSession()
  {
    Session localSession = (Session)currentSession.get();
    if (localSession != null)
    {
      isStopped.set(true);
      setChanged();
      notifyObservers(new NativeInterface.Message(NativeInterface.MessageType.STOP_SESSION, null));
    }
  }
  
  void updateForegroundTracker(String paramString, boolean paramBoolean, long paramLong)
  {
    if (paramBoolean)
    {
      long l = lastExitedForegroundMs.get();
      if (foregroundActivities.isEmpty())
      {
        lastEnteredForegroundMs.set(paramLong);
        if ((paramLong - l >= timeoutMs) && (configuration.getAutoCaptureSessions())) {
          startNewSession(new Date(paramLong), client.getUser(), true);
        }
      }
      foregroundActivities.add(paramString);
    }
    else
    {
      foregroundActivities.remove(paramString);
      if (foregroundActivities.isEmpty()) {
        lastExitedForegroundMs.set(paramLong);
      }
    }
    setChanged();
    notifyNdkInForeground();
  }
}
