package androidx.core.opml;

import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class HandlerCompat
{
  private static final String PAGE_KEY = "HandlerCompat";
  
  private HandlerCompat() {}
  
  public static Handler createAsync(Looper paramLooper)
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
    catch (InvocationTargetException paramLooper)
    {
      paramLooper = paramLooper.getCause();
      if (!(paramLooper instanceof RuntimeException))
      {
        if ((paramLooper instanceof Error)) {
          throw ((Error)paramLooper);
        }
        throw new RuntimeException(paramLooper);
      }
      throw ((RuntimeException)paramLooper);
      Log.v("HandlerCompat", "Unable to invoke Handler(Looper, Callback, boolean) constructor");
      return new Handler(paramLooper);
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
  }
  
  public static Handler createAsync(Looper paramLooper, Handler.Callback paramCallback)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return Handler.createAsync(paramLooper, paramCallback);
    }
    Object localObject;
    if (Build.VERSION.SDK_INT >= 16) {
      localObject = Boolean.TYPE;
    }
    try
    {
      localObject = Handler.class.getDeclaredConstructor(new Class[] { Looper.class, Handler.Callback.class, localObject });
      localObject = ((Constructor)localObject).newInstance(new Object[] { paramLooper, paramCallback, Boolean.valueOf(true) });
      return (Handler)localObject;
    }
    catch (InvocationTargetException paramLooper)
    {
      paramLooper = paramLooper.getCause();
      if (!(paramLooper instanceof RuntimeException))
      {
        if ((paramLooper instanceof Error)) {
          throw ((Error)paramLooper);
        }
        throw new RuntimeException(paramLooper);
      }
      throw ((RuntimeException)paramLooper);
      Log.v("HandlerCompat", "Unable to invoke Handler(Looper, Callback, boolean) constructor");
      return new Handler(paramLooper, paramCallback);
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
  }
  
  public static boolean postDelayed(Handler paramHandler, Runnable paramRunnable, Object paramObject, long paramLong)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramHandler.postDelayed(paramRunnable, paramObject, paramLong);
    }
    paramRunnable = Message.obtain(paramHandler, paramRunnable);
    obj = paramObject;
    return paramHandler.sendMessageDelayed(paramRunnable, paramLong);
  }
}
