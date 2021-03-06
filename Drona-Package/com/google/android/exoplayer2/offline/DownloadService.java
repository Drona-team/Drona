package com.google.android.exoplayer2.offline;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.RequirementsWatcher;
import com.google.android.exoplayer2.scheduler.RequirementsWatcher.Listener;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.HashMap;

public abstract class DownloadService
  extends Service
{
  public static final String ACTION_ADD = "com.google.android.exoplayer.downloadService.action.ADD";
  public static final String ACTION_INIT = "com.google.android.exoplayer.downloadService.action.INIT";
  public static final String ACTION_RELOAD_REQUIREMENTS = "com.google.android.exoplayer.downloadService.action.RELOAD_REQUIREMENTS";
  private static final String ACTION_RESTART = "com.google.android.exoplayer.downloadService.action.RESTART";
  private static final boolean DEBUG = false;
  public static final long DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000L;
  private static final Requirements DEFAULT_REQUIREMENTS = new Requirements(1, false, false);
  public static final int FOREGROUND_NOTIFICATION_ID_NONE = 0;
  public static final String KEY_DOWNLOAD_ACTION = "download_action";
  public static final String KEY_FOREGROUND = "foreground";
  private static final String TAG = "DownloadService";
  private static final HashMap<Class<? extends DownloadService>, RequirementsHelper> requirementsHelpers = new HashMap();
  @Nullable
  private final String channelId;
  @StringRes
  private final int channelName;
  private DownloadManager downloadManager;
  private DownloadManagerListener downloadManagerListener;
  @Nullable
  private final ForegroundNotificationUpdater foregroundNotificationUpdater;
  private int lastStartId;
  private boolean startedInForeground;
  private boolean taskRemoved;
  
  protected DownloadService(int paramInt)
  {
    this(paramInt, 1000L);
  }
  
  protected DownloadService(int paramInt, long paramLong)
  {
    this(paramInt, paramLong, null, 0);
  }
  
  protected DownloadService(int paramInt1, long paramLong, String paramString, int paramInt2)
  {
    ForegroundNotificationUpdater localForegroundNotificationUpdater;
    if (paramInt1 == 0) {
      localForegroundNotificationUpdater = null;
    } else {
      localForegroundNotificationUpdater = new ForegroundNotificationUpdater(paramInt1, paramLong);
    }
    foregroundNotificationUpdater = localForegroundNotificationUpdater;
    channelId = paramString;
    channelName = paramInt2;
  }
  
  public static Intent buildAddActionIntent(Context paramContext, Class paramClass, DownloadAction paramDownloadAction, boolean paramBoolean)
  {
    return getIntent(paramContext, paramClass, "com.google.android.exoplayer.downloadService.action.ADD").putExtra("download_action", paramDownloadAction.toByteArray()).putExtra("foreground", paramBoolean);
  }
  
  private static Intent getIntent(Context paramContext, Class paramClass, String paramString)
  {
    return new Intent(paramContext, paramClass).setAction(paramString);
  }
  
  private void logd(String paramString) {}
  
  private void maybeStartWatchingRequirements(Requirements paramRequirements)
  {
    if (downloadManager.getDownloadCount() == 0) {
      return;
    }
    Class localClass = getClass();
    if ((RequirementsHelper)requirementsHelpers.get(localClass) == null)
    {
      paramRequirements = new RequirementsHelper(this, paramRequirements, getScheduler(), localClass, null);
      requirementsHelpers.put(localClass, paramRequirements);
      paramRequirements.start();
      logd("started watching requirements");
    }
  }
  
  private void maybeStopWatchingRequirements()
  {
    if (downloadManager.getDownloadCount() > 0) {
      return;
    }
    stopWatchingRequirements();
  }
  
  public static void start(Context paramContext, Class paramClass)
  {
    paramContext.startService(getIntent(paramContext, paramClass, "com.google.android.exoplayer.downloadService.action.INIT"));
  }
  
  public static void startForeground(Context paramContext, Class paramClass)
  {
    Util.startForegroundService(paramContext, getIntent(paramContext, paramClass, "com.google.android.exoplayer.downloadService.action.INIT").putExtra("foreground", true));
  }
  
  public static void startWithAction(Context paramContext, Class paramClass, DownloadAction paramDownloadAction, boolean paramBoolean)
  {
    paramClass = buildAddActionIntent(paramContext, paramClass, paramDownloadAction, paramBoolean);
    if (paramBoolean)
    {
      Util.startForegroundService(paramContext, paramClass);
      return;
    }
    paramContext.startService(paramClass);
  }
  
  private void stop()
  {
    if (foregroundNotificationUpdater != null)
    {
      foregroundNotificationUpdater.stopPeriodicUpdates();
      if ((startedInForeground) && (Util.SDK_INT >= 26)) {
        foregroundNotificationUpdater.showNotificationIfNotAlready();
      }
    }
    if ((Util.SDK_INT < 28) && (taskRemoved))
    {
      stopSelf();
      logd("stopSelf()");
      return;
    }
    boolean bool = stopSelfResult(lastStartId);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("stopSelf(");
    localStringBuilder.append(lastStartId);
    localStringBuilder.append(") result: ");
    localStringBuilder.append(bool);
    logd(localStringBuilder.toString());
  }
  
  private void stopWatchingRequirements()
  {
    RequirementsHelper localRequirementsHelper = (RequirementsHelper)requirementsHelpers.remove(getClass());
    if (localRequirementsHelper != null)
    {
      localRequirementsHelper.stop();
      logd("stopped watching requirements");
    }
  }
  
  protected abstract DownloadManager getDownloadManager();
  
  protected Notification getForegroundNotification(DownloadManager.TaskState[] paramArrayOfTaskState)
  {
    paramArrayOfTaskState = new StringBuilder();
    paramArrayOfTaskState.append(getClass().getName());
    paramArrayOfTaskState.append(" is started in the foreground but getForegroundNotification() is not implemented.");
    throw new IllegalStateException(paramArrayOfTaskState.toString());
  }
  
  protected Requirements getRequirements()
  {
    return DEFAULT_REQUIREMENTS;
  }
  
  protected abstract Scheduler getScheduler();
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onCreate()
  {
    logd("onCreate");
    if (channelId != null) {
      NotificationUtil.createNotificationChannel(this, channelId, channelName, 2);
    }
    downloadManager = getDownloadManager();
    downloadManagerListener = new DownloadManagerListener(null);
    downloadManager.addListener(downloadManagerListener);
  }
  
  public void onDestroy()
  {
    logd("onDestroy");
    if (foregroundNotificationUpdater != null) {
      foregroundNotificationUpdater.stopPeriodicUpdates();
    }
    downloadManager.removeListener(downloadManagerListener);
    maybeStopWatchingRequirements();
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    lastStartId = paramInt2;
    int i = 0;
    taskRemoved = false;
    if (paramIntent != null)
    {
      localObject2 = paramIntent.getAction();
      localObject1 = localObject2;
      int j = startedInForeground;
      if ((!paramIntent.getBooleanExtra("foreground", false)) && (!"com.google.android.exoplayer.downloadService.action.RESTART".equals(localObject2))) {
        paramInt1 = 0;
      } else {
        paramInt1 = 1;
      }
      startedInForeground = (j | paramInt1);
    }
    else
    {
      localObject1 = null;
    }
    Object localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = "com.google.android.exoplayer.downloadService.action.INIT";
    }
    Object localObject1 = new StringBuilder();
    ((StringBuilder)localObject1).append("onStartCommand action: ");
    ((StringBuilder)localObject1).append((String)localObject2);
    ((StringBuilder)localObject1).append(" startId: ");
    ((StringBuilder)localObject1).append(paramInt2);
    logd(((StringBuilder)localObject1).toString());
    paramInt1 = ((String)localObject2).hashCode();
    if (paramInt1 != -871181424)
    {
      if (paramInt1 != -608867945)
      {
        if (paramInt1 != -382886238)
        {
          if ((paramInt1 == 1015676687) && (((String)localObject2).equals("com.google.android.exoplayer.downloadService.action.INIT")))
          {
            paramInt1 = i;
            break label241;
          }
        }
        else if (((String)localObject2).equals("com.google.android.exoplayer.downloadService.action.ADD"))
        {
          paramInt1 = 2;
          break label241;
        }
      }
      else if (((String)localObject2).equals("com.google.android.exoplayer.downloadService.action.RELOAD_REQUIREMENTS"))
      {
        paramInt1 = 3;
        break label241;
      }
    }
    else if (((String)localObject2).equals("com.google.android.exoplayer.downloadService.action.RESTART"))
    {
      paramInt1 = 1;
      break label241;
    }
    paramInt1 = -1;
    switch (paramInt1)
    {
    default: 
      paramIntent = new StringBuilder();
      paramIntent.append("Ignoring unrecognized action: ");
      paramIntent.append((String)localObject2);
      Log.e("DownloadService", paramIntent.toString());
      break;
    case 3: 
      stopWatchingRequirements();
      break;
    case 2: 
      label241:
      paramIntent = paramIntent.getByteArrayExtra("download_action");
      if (paramIntent == null)
      {
        Log.e("DownloadService", "Ignoring ADD action with no action data");
      }
      else
      {
        localObject1 = downloadManager;
        try
        {
          ((DownloadManager)localObject1).handleAction(paramIntent);
        }
        catch (IOException paramIntent)
        {
          Log.e("DownloadService", "Failed to handle ADD action", paramIntent);
        }
      }
      break;
    }
    paramIntent = getRequirements();
    if (paramIntent.checkRequirements(this)) {
      downloadManager.startDownloads();
    } else {
      downloadManager.stopDownloads();
    }
    maybeStartWatchingRequirements(paramIntent);
    if (downloadManager.isIdle()) {
      stop();
    }
    return 1;
  }
  
  public void onTaskRemoved(Intent paramIntent)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("onTaskRemoved rootIntent: ");
    localStringBuilder.append(paramIntent);
    logd(localStringBuilder.toString());
    taskRemoved = true;
  }
  
  protected void onTaskStateChanged(DownloadManager.TaskState paramTaskState) {}
  
  private final class DownloadManagerListener
    implements DownloadManager.Listener
  {
    private DownloadManagerListener() {}
    
    public final void onIdle(DownloadManager paramDownloadManager)
    {
      DownloadService.this.stop();
    }
    
    public void onInitialized(DownloadManager paramDownloadManager)
    {
      DownloadService.this.maybeStartWatchingRequirements(getRequirements());
    }
    
    public void onTaskStateChanged(DownloadManager paramDownloadManager, DownloadManager.TaskState paramTaskState)
    {
      onTaskStateChanged(paramTaskState);
      if (foregroundNotificationUpdater != null)
      {
        if (state == 1)
        {
          foregroundNotificationUpdater.startPeriodicUpdates();
          return;
        }
        foregroundNotificationUpdater.update();
      }
    }
  }
  
  private final class ForegroundNotificationUpdater
    implements Runnable
  {
    private final Handler handler;
    private boolean notificationDisplayed;
    private final int notificationId;
    private boolean periodicUpdatesStarted;
    private final long updateInterval;
    
    public ForegroundNotificationUpdater(int paramInt, long paramLong)
    {
      notificationId = paramInt;
      updateInterval = paramLong;
      handler = new Handler(Looper.getMainLooper());
    }
    
    public void run()
    {
      update();
    }
    
    public void showNotificationIfNotAlready()
    {
      if (!notificationDisplayed) {
        update();
      }
    }
    
    public void startPeriodicUpdates()
    {
      periodicUpdatesStarted = true;
      update();
    }
    
    public void stopPeriodicUpdates()
    {
      periodicUpdatesStarted = false;
      handler.removeCallbacks(this);
    }
    
    public void update()
    {
      DownloadManager.TaskState[] arrayOfTaskState = downloadManager.getAllTaskStates();
      startForeground(notificationId, getForegroundNotification(arrayOfTaskState));
      notificationDisplayed = true;
      if (periodicUpdatesStarted)
      {
        handler.removeCallbacks(this);
        handler.postDelayed(this, updateInterval);
      }
    }
  }
  
  private static final class RequirementsHelper
    implements RequirementsWatcher.Listener
  {
    private final Context context;
    private final Requirements requirements;
    private final RequirementsWatcher requirementsWatcher;
    @Nullable
    private final Scheduler scheduler;
    private final Class<? extends DownloadService> serviceClass;
    
    private RequirementsHelper(Context paramContext, Requirements paramRequirements, Scheduler paramScheduler, Class paramClass)
    {
      context = paramContext;
      requirements = paramRequirements;
      scheduler = paramScheduler;
      serviceClass = paramClass;
      requirementsWatcher = new RequirementsWatcher(paramContext, this, paramRequirements);
    }
    
    private void notifyService()
      throws Exception
    {
      Intent localIntent = DownloadService.getIntent(context, serviceClass, "com.google.android.exoplayer.downloadService.action.INIT");
      Context localContext = context;
      try
      {
        localContext.startService(localIntent);
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        throw new Exception(localIllegalStateException);
      }
    }
    
    public void requirementsMet(RequirementsWatcher paramRequirementsWatcher)
    {
      try
      {
        notifyService();
        if (scheduler != null)
        {
          scheduler.cancel();
          return;
        }
      }
      catch (Exception paramRequirementsWatcher) {}
    }
    
    public void requirementsNotMet(RequirementsWatcher paramRequirementsWatcher)
    {
      try
      {
        notifyService();
        if (scheduler != null)
        {
          paramRequirementsWatcher = context.getPackageName();
          if (!scheduler.schedule(requirements, paramRequirementsWatcher, "com.google.android.exoplayer.downloadService.action.RESTART"))
          {
            Log.e("DownloadService", "Scheduling downloads failed.");
            return;
          }
        }
      }
      catch (Exception paramRequirementsWatcher)
      {
        for (;;) {}
      }
    }
    
    public void start()
    {
      requirementsWatcher.start();
    }
    
    public void stop()
    {
      requirementsWatcher.stop();
      if (scheduler != null) {
        scheduler.cancel();
      }
    }
  }
}
