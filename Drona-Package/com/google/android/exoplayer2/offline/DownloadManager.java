package com.google.android.exoplayer2.offline;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public final class DownloadManager
{
  private static final boolean DEBUG = false;
  public static final int DEFAULT_MAX_SIMULTANEOUS_DOWNLOADS = 1;
  public static final int DEFAULT_MIN_RETRY_COUNT = 5;
  private static final String REPOSITORY_KEY = "DownloadManager";
  private final ActionFile actionFile;
  private final ArrayList<Task> activeDownloadTasks;
  private final DownloadAction.Deserializer[] deserializers;
  private final DownloaderConstructorHelper downloaderConstructorHelper;
  private boolean downloadsStopped;
  private final Handler fileIOHandler;
  private final HandlerThread fileIOThread;
  private final Handler handler;
  private boolean initialized;
  private final CopyOnWriteArraySet<Listener> listeners;
  private final int maxActiveDownloadTasks;
  private final int minRetryCount;
  private int nextTaskId;
  private boolean released;
  private final ArrayList<Task> tasks;
  
  public DownloadManager(DownloaderConstructorHelper paramDownloaderConstructorHelper, int paramInt1, int paramInt2, File paramFile, DownloadAction.Deserializer... paramVarArgs)
  {
    downloaderConstructorHelper = paramDownloaderConstructorHelper;
    maxActiveDownloadTasks = paramInt1;
    minRetryCount = paramInt2;
    actionFile = new ActionFile(paramFile);
    if (paramVarArgs.length <= 0) {
      paramVarArgs = DownloadAction.getDefaultDeserializers();
    }
    deserializers = paramVarArgs;
    downloadsStopped = true;
    tasks = new ArrayList();
    activeDownloadTasks = new ArrayList();
    paramFile = Looper.myLooper();
    paramDownloaderConstructorHelper = paramFile;
    if (paramFile == null) {
      paramDownloaderConstructorHelper = Looper.getMainLooper();
    }
    handler = new Handler(paramDownloaderConstructorHelper);
    fileIOThread = new HandlerThread("DownloadManager file i/o");
    fileIOThread.start();
    fileIOHandler = new Handler(fileIOThread.getLooper());
    listeners = new CopyOnWriteArraySet();
    loadActions();
    logd("Created");
  }
  
  public DownloadManager(DownloaderConstructorHelper paramDownloaderConstructorHelper, File paramFile, DownloadAction.Deserializer... paramVarArgs)
  {
    this(paramDownloaderConstructorHelper, 1, 5, paramFile, paramVarArgs);
  }
  
  public DownloadManager(Cache paramCache, DataSource.Factory paramFactory, File paramFile, DownloadAction.Deserializer... paramVarArgs)
  {
    this(new DownloaderConstructorHelper(paramCache, paramFactory), paramFile, paramVarArgs);
  }
  
  private Task addTaskForAction(DownloadAction paramDownloadAction)
  {
    int i = nextTaskId;
    nextTaskId = (i + 1);
    paramDownloadAction = new Task(i, this, paramDownloadAction, minRetryCount, null);
    tasks.add(paramDownloadAction);
    logd("Task is added", paramDownloadAction);
    return paramDownloadAction;
  }
  
  private void loadActions()
  {
    fileIOHandler.post(new -..Lambda.DownloadManager.0LJSbWXADhROJkmo8hGQn9eqfcs(this));
  }
  
  private static void logd(String paramString) {}
  
  private static void logd(String paramString, Task paramTask)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append(": ");
    localStringBuilder.append(paramTask);
    logd(localStringBuilder.toString());
  }
  
  private void maybeNotifyListenersIdle()
  {
    if (!isIdle()) {
      return;
    }
    logd("Notify idle state");
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onIdle(this);
    }
  }
  
  private void maybeStartTasks()
  {
    if (initialized)
    {
      if (released) {
        return;
      }
      int i;
      if ((!downloadsStopped) && (activeDownloadTasks.size() != maxActiveDownloadTasks)) {
        i = 0;
      } else {
        i = 1;
      }
      int m = 0;
      while (m < tasks.size())
      {
        Task localTask1 = (Task)tasks.get(m);
        if (localTask1.canStart())
        {
          DownloadAction localDownloadAction = action;
          boolean bool = isRemoveAction;
          if ((bool) || (i == 0))
          {
            int n = 0;
            int k;
            int i1;
            for (int j = 1;; j = k)
            {
              k = i;
              i1 = j;
              if (n >= m) {
                break;
              }
              Task localTask2 = (Task)tasks.get(n);
              k = j;
              if (action.isSameMedia(localDownloadAction)) {
                if (bool)
                {
                  StringBuilder localStringBuilder = new StringBuilder();
                  localStringBuilder.append(localTask1);
                  localStringBuilder.append(" clashes with ");
                  localStringBuilder.append(localTask2);
                  logd(localStringBuilder.toString());
                  localTask2.cancel();
                  k = 0;
                }
                else
                {
                  k = j;
                  if (action.isRemoveAction)
                  {
                    k = 1;
                    i1 = 0;
                    break;
                  }
                }
              }
              n += 1;
            }
            i = k;
            if (i1 != 0)
            {
              localTask1.start();
              i = k;
              if (!bool)
              {
                activeDownloadTasks.add(localTask1);
                if (activeDownloadTasks.size() == maxActiveDownloadTasks) {
                  i = 1;
                } else {
                  i = 0;
                }
              }
            }
          }
        }
        m += 1;
      }
    }
  }
  
  private void notifyListenersTaskStateChange(Task paramTask)
  {
    logd("Task state is changed", paramTask);
    paramTask = paramTask.getDownloadState();
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onTaskStateChanged(this, paramTask);
    }
  }
  
  private void onTaskStateChange(Task paramTask)
  {
    if (released) {
      return;
    }
    boolean bool = paramTask.isActive() ^ true;
    if (bool) {
      activeDownloadTasks.remove(paramTask);
    }
    notifyListenersTaskStateChange(paramTask);
    if (paramTask.isFinished())
    {
      tasks.remove(paramTask);
      saveActions();
    }
    if (bool)
    {
      maybeStartTasks();
      maybeNotifyListenersIdle();
    }
  }
  
  private void saveActions()
  {
    if (released) {
      return;
    }
    DownloadAction[] arrayOfDownloadAction = new DownloadAction[tasks.size()];
    int i = 0;
    while (i < tasks.size())
    {
      arrayOfDownloadAction[i] = tasks.get(i)).action;
      i += 1;
    }
    fileIOHandler.post(new -..Lambda.DownloadManager.SgHHqKrgOJ8vvRnakgUybwmDe2w(this, arrayOfDownloadAction));
  }
  
  public void addListener(Listener paramListener)
  {
    listeners.add(paramListener);
  }
  
  public TaskState[] getAllTaskStates()
  {
    Assertions.checkState(released ^ true);
    TaskState[] arrayOfTaskState = new TaskState[tasks.size()];
    int i = 0;
    while (i < arrayOfTaskState.length)
    {
      arrayOfTaskState[i] = ((Task)tasks.get(i)).getDownloadState();
      i += 1;
    }
    return arrayOfTaskState;
  }
  
  public int getDownloadCount()
  {
    int i = 0;
    int k;
    for (int j = 0; i < tasks.size(); j = k)
    {
      k = j;
      if (!tasks.get(i)).action.isRemoveAction) {
        k = j + 1;
      }
      i += 1;
    }
    return j;
  }
  
  public int getTaskCount()
  {
    Assertions.checkState(released ^ true);
    return tasks.size();
  }
  
  public TaskState getTaskState(int paramInt)
  {
    Assertions.checkState(released ^ true);
    int i = 0;
    while (i < tasks.size())
    {
      Task localTask = (Task)tasks.get(i);
      if (exception == paramInt) {
        return localTask.getDownloadState();
      }
      i += 1;
    }
    return null;
  }
  
  public int handleAction(DownloadAction paramDownloadAction)
  {
    Assertions.checkState(released ^ true);
    paramDownloadAction = addTaskForAction(paramDownloadAction);
    if (initialized)
    {
      saveActions();
      maybeStartTasks();
      if (currentState == 0) {
        notifyListenersTaskStateChange(paramDownloadAction);
      }
    }
    return exception;
  }
  
  public int handleAction(byte[] paramArrayOfByte)
    throws IOException
  {
    Assertions.checkState(released ^ true);
    paramArrayOfByte = new ByteArrayInputStream(paramArrayOfByte);
    return handleAction(DownloadAction.deserializeFromStream(deserializers, paramArrayOfByte));
  }
  
  public boolean isIdle()
  {
    Assertions.checkState(released ^ true);
    if (!initialized) {
      return false;
    }
    int i = 0;
    while (i < tasks.size())
    {
      if (((Task)tasks.get(i)).isActive()) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public boolean isInitialized()
  {
    Assertions.checkState(released ^ true);
    return initialized;
  }
  
  public void release()
  {
    if (released) {
      return;
    }
    released = true;
    int i = 0;
    while (i < tasks.size())
    {
      ((Task)tasks.get(i)).stop();
      i += 1;
    }
    ConditionVariable localConditionVariable = new ConditionVariable();
    Handler localHandler = fileIOHandler;
    localConditionVariable.getClass();
    localHandler.post(new -..Lambda.xEDVsWySjOhZCU-CTVGu6ziJ2xc(localConditionVariable));
    localConditionVariable.block();
    fileIOThread.quit();
    logd("Released");
  }
  
  public void removeListener(Listener paramListener)
  {
    listeners.remove(paramListener);
  }
  
  public void startDownloads()
  {
    Assertions.checkState(released ^ true);
    if (downloadsStopped)
    {
      downloadsStopped = false;
      maybeStartTasks();
      logd("Downloads are started");
    }
  }
  
  public void stopDownloads()
  {
    Assertions.checkState(released ^ true);
    if (!downloadsStopped)
    {
      downloadsStopped = true;
      int i = 0;
      while (i < activeDownloadTasks.size())
      {
        ((Task)activeDownloadTasks.get(i)).stop();
        i += 1;
      }
      logd("Downloads are stopping");
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onIdle(DownloadManager paramDownloadManager);
    
    public abstract void onInitialized(DownloadManager paramDownloadManager);
    
    public abstract void onTaskStateChanged(DownloadManager paramDownloadManager, DownloadManager.TaskState paramTaskState);
  }
  
  private static final class Task
    implements Runnable
  {
    public static final int STATE_QUEUED_CANCELING = 5;
    public static final int STATE_STARTED_CANCELING = 6;
    public static final int STATE_STARTED_STOPPING = 7;
    private final DownloadAction action;
    private volatile int currentState;
    private final DownloadManager downloadManager;
    private volatile Downloader downloader;
    private Throwable error;
    private final int exception;
    private final int minRetryCount;
    private Thread thread;
    
    private Task(int paramInt1, DownloadManager paramDownloadManager, DownloadAction paramDownloadAction, int paramInt2)
    {
      exception = paramInt1;
      downloadManager = paramDownloadManager;
      action = paramDownloadAction;
      currentState = 0;
      minRetryCount = paramInt2;
    }
    
    private boolean canStart()
    {
      return currentState == 0;
    }
    
    private void cancel()
    {
      if (changeStateAndNotify(0, 5))
      {
        downloadManager.handler.post(new -..Lambda.DownloadManager.Task.BscZ_DsnJwLao_N7rZjz7bnzplk(this));
        return;
      }
      if (changeStateAndNotify(1, 6)) {
        cancelDownload();
      }
    }
    
    private void cancelDownload()
    {
      if (downloader != null) {
        downloader.cancel();
      }
      thread.interrupt();
    }
    
    private boolean changeStateAndNotify(int paramInt1, int paramInt2)
    {
      return changeStateAndNotify(paramInt1, paramInt2, null);
    }
    
    private boolean changeStateAndNotify(int paramInt1, int paramInt2, Throwable paramThrowable)
    {
      int j = currentState;
      int i = 0;
      if (j != paramInt1) {
        return false;
      }
      currentState = paramInt2;
      error = paramThrowable;
      paramInt1 = i;
      if (currentState != getExternalState()) {
        paramInt1 = 1;
      }
      if (paramInt1 == 0) {
        downloadManager.onTaskStateChange(this);
      }
      return true;
    }
    
    private int getExternalState()
    {
      switch (currentState)
      {
      default: 
        return currentState;
      case 6: 
      case 7: 
        return 1;
      }
      return 0;
    }
    
    private int getRetryDelayMillis(int paramInt)
    {
      return Math.min((paramInt - 1) * 1000, 5000);
    }
    
    private String getStateString()
    {
      switch (currentState)
      {
      default: 
        return DownloadManager.TaskState.getStateString(currentState);
      case 7: 
        return "STOPPING";
      }
      return "CANCELING";
    }
    
    private void start()
    {
      if (changeStateAndNotify(0, 1))
      {
        thread = new Thread(this);
        thread.start();
      }
    }
    
    private void stop()
    {
      if (changeStateAndNotify(1, 7))
      {
        DownloadManager.logd("Stopping", this);
        cancelDownload();
      }
    }
    
    private static String toString(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length > 100) {
        return "<data is too long>";
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append('\'');
      localStringBuilder.append(Util.fromUtf8Bytes(paramArrayOfByte));
      localStringBuilder.append('\'');
      return localStringBuilder.toString();
    }
    
    public float getDownloadPercentage()
    {
      if (downloader != null) {
        return downloader.getDownloadPercentage();
      }
      return -1.0F;
    }
    
    public DownloadManager.TaskState getDownloadState()
    {
      int i = getExternalState();
      return new DownloadManager.TaskState(exception, action, i, getDownloadPercentage(), getDownloadedBytes(), error, null);
    }
    
    public long getDownloadedBytes()
    {
      if (downloader != null) {
        return downloader.getDownloadedBytes();
      }
      return 0L;
    }
    
    public boolean isActive()
    {
      if ((currentState != 5) && (currentState != 1) && (currentState != 7)) {
        return currentState == 6;
      }
      return true;
    }
    
    public boolean isFinished()
    {
      return (currentState == 4) || (currentState == 2) || (currentState == 3);
    }
    
    public void run()
    {
      DownloadManager.logd("Task is started", this);
      try
      {
        downloader = action.createDownloader(downloadManager.downloaderConstructorHelper);
        boolean bool = action.isRemoveAction;
        if (bool)
        {
          downloader.remove();
        }
        else
        {
          long l1 = -1L;
          int i = 0;
          for (;;)
          {
            bool = Thread.interrupted();
            if (!bool)
            {
              Downloader localDownloader = downloader;
              try
              {
                localDownloader.download();
              }
              catch (IOException localIOException)
              {
                long l3 = downloader.getDownloadedBytes();
                long l2 = l1;
                if (l3 != l1)
                {
                  StringBuilder localStringBuilder2 = new StringBuilder();
                  localStringBuilder2.append("Reset error count. downloadedBytes = ");
                  localStringBuilder2.append(l3);
                  DownloadManager.logd(localStringBuilder2.toString(), this);
                  l2 = l3;
                  i = 0;
                }
                int j = currentState;
                if (j == 1)
                {
                  i += 1;
                  j = minRetryCount;
                  if (i <= j)
                  {
                    localStringBuilder1 = new StringBuilder();
                    localStringBuilder1.append("Download error. Retry ");
                    localStringBuilder1.append(i);
                    DownloadManager.logd(localStringBuilder1.toString(), this);
                    Thread.sleep(getRetryDelayMillis(i));
                    l1 = l2;
                    continue;
                  }
                }
                throw localStringBuilder1;
              }
            }
          }
        }
        StringBuilder localStringBuilder1 = null;
      }
      catch (Throwable localThrowable) {}
      downloadManager.handler.post(new -..Lambda.DownloadManager.Task.tMCSa8vI5Qy5JY5aoxlLoYvc2xQ(this, localThrowable));
    }
    
    public String toString()
    {
      return super.toString();
    }
    
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public static @interface InternalState {}
  }
  
  public static final class TaskState
  {
    public static final int STATE_CANCELED = 3;
    public static final int STATE_COMPLETED = 2;
    public static final int STATE_FAILED = 4;
    public static final int STATE_QUEUED = 0;
    public static final int STATE_STARTED = 1;
    public final DownloadAction action;
    public final float downloadPercentage;
    public final long downloadedBytes;
    public final Throwable error;
    public final int state;
    public final int taskId;
    
    private TaskState(int paramInt1, DownloadAction paramDownloadAction, int paramInt2, float paramFloat, long paramLong, Throwable paramThrowable)
    {
      taskId = paramInt1;
      action = paramDownloadAction;
      state = paramInt2;
      downloadPercentage = paramFloat;
      downloadedBytes = paramLong;
      error = paramThrowable;
    }
    
    public static String getStateString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalStateException();
      case 4: 
        return "FAILED";
      case 3: 
        return "CANCELED";
      case 2: 
        return "COMPLETED";
      case 1: 
        return "STARTED";
      }
      return "QUEUED";
    }
    
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public static @interface State {}
  }
}
