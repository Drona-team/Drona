package androidx.arch.core.executor;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class DefaultTaskExecutor
  extends TaskExecutor
{
  private final ExecutorService mDiskIO = Executors.newFixedThreadPool(4, new ThreadFactory()
  {
    private static final String THREAD_NAME_STEM = "arch_disk_io_%d";
    private final AtomicInteger mThreadId = new AtomicInteger(0);
    
    public Thread newThread(Runnable paramAnonymousRunnable)
    {
      paramAnonymousRunnable = new Thread(paramAnonymousRunnable);
      paramAnonymousRunnable.setName(String.format("arch_disk_io_%d", new Object[] { Integer.valueOf(mThreadId.getAndIncrement()) }));
      return paramAnonymousRunnable;
    }
  });
  private final Object mLock = new Object();
  @Nullable
  private volatile Handler mMainHandler;
  
  public DefaultTaskExecutor() {}
  
  private static Handler createAsync(Looper paramLooper)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return Handler.createAsync(paramLooper);
    }
    Object localObject;
    if (Build.VERSION.SDK_INT >= 16) {
      localObject = Boolean.TYPE;
    }
    try
    {
      localObject = Handler.class.getDeclaredConstructor(new Class[] { Looper.class, Handler.Callback.class, localObject });
      localObject = ((Constructor)localObject).newInstance(new Object[] { paramLooper, null, Boolean.valueOf(true) });
      return (Handler)localObject;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;) {}
    }
    catch (InstantiationException localInstantiationException)
    {
      for (;;) {}
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;) {}
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;) {}
    }
    return new Handler(paramLooper);
    return new Handler(paramLooper);
  }
  
  public void executeOnDiskIO(Runnable paramRunnable)
  {
    mDiskIO.execute(paramRunnable);
  }
  
  public boolean isMainThread()
  {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }
  
  public void postToMainThread(Runnable paramRunnable)
  {
    if (mMainHandler == null)
    {
      Object localObject = mLock;
      try
      {
        if (mMainHandler == null) {
          mMainHandler = createAsync(Looper.getMainLooper());
        }
      }
      catch (Throwable paramRunnable)
      {
        throw paramRunnable;
      }
    }
    mMainHandler.post(paramRunnable);
  }
}
