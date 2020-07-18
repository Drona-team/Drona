package com.google.android.gms.package_7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.stats.ConnectionTracker;
import com.google.android.gms.iid.zzz;
import com.google.android.gms.internal.gcm.zzj;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;

final class HttpHeaders
  implements ServiceConnection
{
  @GuardedBy("this")
  int state = 0;
  final Messenger zzch = new Messenger((Handler)new zzj(Looper.getMainLooper(), new MainActivity.6(this)));
  Registry zzci;
  @GuardedBy("this")
  final Queue<zzz<?>> zzcj = new ArrayDeque();
  @GuardedBy("this")
  final SparseArray<zzz<?>> zzck = new SparseArray();
  
  private HttpHeaders(DownloadFile paramDownloadFile) {}
  
  private final void execute()
  {
    DownloadFile.access$getHandler(zzcl).execute(new FileUtils.23(this));
  }
  
  final void add(int paramInt)
  {
    try
    {
      Dictionary localDictionary = (Dictionary)zzck.get(paramInt);
      if (localDictionary != null)
      {
        StringBuilder localStringBuilder = new StringBuilder(31);
        localStringBuilder.append("Timing out request: ");
        localStringBuilder.append(paramInt);
        Log.w("MessengerIpcClient", localStringBuilder.toString());
        zzck.remove(paramInt);
        localDictionary.write(new zzaa(3, "Timed out waiting for response"));
        clear();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  final boolean add(Message paramMessage)
  {
    int i = arg1;
    Object localObject;
    if (Log.isLoggable("MessengerIpcClient", 3))
    {
      localObject = new StringBuilder(41);
      ((StringBuilder)localObject).append("Received response to request: ");
      ((StringBuilder)localObject).append(i);
      Log.d("MessengerIpcClient", ((StringBuilder)localObject).toString());
    }
    try
    {
      localObject = (Dictionary)zzck.get(i);
      if (localObject == null)
      {
        paramMessage = new StringBuilder(50);
        paramMessage.append("Received response for unknown request: ");
        paramMessage.append(i);
        Log.w("MessengerIpcClient", paramMessage.toString());
        return true;
      }
      zzck.remove(i);
      clear();
      paramMessage = paramMessage.getData();
      if (paramMessage.getBoolean("unsupported", false))
      {
        ((Dictionary)localObject).write(new zzaa(4, "Not supported by GmsCore"));
        return true;
      }
      ((Dictionary)localObject).add(paramMessage);
      return true;
    }
    catch (Throwable paramMessage)
    {
      throw paramMessage;
    }
  }
  
  final void clear()
  {
    try
    {
      if ((state == 2) && (zzcj.isEmpty()) && (zzck.size() == 0))
      {
        if (Log.isLoggable("MessengerIpcClient", 2)) {
          Log.v("MessengerIpcClient", "Finished handling requests, unbinding");
        }
        state = 3;
        ConnectionTracker.getInstance().unbindService(DownloadFile.access$getContext(zzcl), this);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  final void close(int paramInt, String paramString)
  {
    for (;;)
    {
      try
      {
        Object localObject;
        if (Log.isLoggable("MessengerIpcClient", 3))
        {
          localObject = String.valueOf(paramString);
          if (((String)localObject).length() != 0) {
            localObject = "Disconnected: ".concat((String)localObject);
          } else {
            localObject = new String("Disconnected: ");
          }
          Log.d("MessengerIpcClient", (String)localObject);
        }
        switch (state)
        {
        case 4: 
          continue;
          return;
        case 3: 
          state = 4;
          return;
        case 1: 
        case 2: 
          if (Log.isLoggable("MessengerIpcClient", 2)) {
            Log.v("MessengerIpcClient", "Unbinding service");
          }
          state = 4;
          ConnectionTracker.getInstance().unbindService(DownloadFile.access$getContext(zzcl), this);
          paramString = new zzaa(paramInt, paramString);
          localObject = zzcj.iterator();
          if (((Iterator)localObject).hasNext())
          {
            ((Dictionary)((Iterator)localObject).next()).write(paramString);
          }
          else
          {
            zzcj.clear();
            paramInt = 0;
            if (paramInt < zzck.size())
            {
              ((Dictionary)zzck.valueAt(paramInt)).write(paramString);
              paramInt += 1;
            }
            else
            {
              zzck.clear();
              return;
            }
          }
          break;
        case 0: 
          throw new IllegalStateException();
          paramInt = state;
          paramString = new StringBuilder(26);
          paramString.append("Unknown state: ");
          paramString.append(paramInt);
          throw new IllegalStateException(paramString.toString());
        }
      }
      catch (Throwable paramString)
      {
        throw paramString;
      }
    }
  }
  
  final boolean execute(Dictionary paramDictionary)
  {
    for (;;)
    {
      try
      {
        switch (state)
        {
        case 3: 
          break;
        case 4: 
          return false;
        case 2: 
          zzcj.add(paramDictionary);
          execute();
          return true;
        case 1: 
          zzcj.add(paramDictionary);
          return true;
        case 0: 
          zzcj.add(paramDictionary);
          if (state != 0) {
            break label275;
          }
          bool = true;
          Preconditions.checkState(bool);
          if (Log.isLoggable("MessengerIpcClient", 2)) {
            Log.v("MessengerIpcClient", "Starting bind to GmsCore");
          }
          state = 1;
          paramDictionary = new Intent("com.google.android.c2dm.intent.REGISTER");
          paramDictionary.setPackage("com.google.android.gms");
          if (!ConnectionTracker.getInstance().bindService(DownloadFile.access$getContext(zzcl), paramDictionary, this, 1)) {
            close(0, "Unable to bind to service");
          } else {
            DownloadFile.access$getHandler(zzcl).schedule(new IonBitmapRequestBuilder.1(this), 30L, TimeUnit.SECONDS);
          }
          return true;
          int i = state;
          paramDictionary = new StringBuilder(26);
          paramDictionary.append("Unknown state: ");
          paramDictionary.append(i);
          throw new IllegalStateException(paramDictionary.toString());
        }
      }
      catch (Throwable paramDictionary)
      {
        throw paramDictionary;
      }
      continue;
      label275:
      boolean bool = false;
    }
  }
  
  /* Error */
  public final void onServiceConnected(ComponentName paramComponentName, android.os.IBinder paramIBinder)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc 111
    //   4: iconst_2
    //   5: invokestatic 148	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   8: ifeq +12 -> 20
    //   11: ldc 111
    //   13: ldc_w 300
    //   16: invokestatic 187	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   19: pop
    //   20: aload_2
    //   21: ifnonnull +14 -> 35
    //   24: aload_0
    //   25: iconst_0
    //   26: ldc_w 302
    //   29: invokevirtual 279	com/google/android/gms/package_7/HttpHeaders:close	(ILjava/lang/String;)V
    //   32: aload_0
    //   33: monitorexit
    //   34: return
    //   35: new 304	com/google/android/gms/package_7/Registry
    //   38: dup
    //   39: aload_2
    //   40: invokespecial 307	com/google/android/gms/package_7/Registry:<init>	(Landroid/os/IBinder;)V
    //   43: astore_1
    //   44: aload_0
    //   45: aload_1
    //   46: putfield 309	com/google/android/gms/package_7/HttpHeaders:zzci	Lcom/google/android/gms/package_7/Registry;
    //   49: aload_0
    //   50: iconst_2
    //   51: putfield 32	com/google/android/gms/package_7/HttpHeaders:state	I
    //   54: aload_0
    //   55: invokespecial 252	com/google/android/gms/package_7/HttpHeaders:execute	()V
    //   58: aload_0
    //   59: monitorexit
    //   60: return
    //   61: astore_1
    //   62: aload_0
    //   63: iconst_0
    //   64: aload_1
    //   65: invokevirtual 314	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   68: invokevirtual 279	com/google/android/gms/package_7/HttpHeaders:close	(ILjava/lang/String;)V
    //   71: aload_0
    //   72: monitorexit
    //   73: return
    //   74: astore_1
    //   75: aload_0
    //   76: monitorexit
    //   77: aload_1
    //   78: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	79	0	this	HttpHeaders
    //   0	79	1	paramComponentName	ComponentName
    //   0	79	2	paramIBinder	android.os.IBinder
    // Exception table:
    //   from	to	target	type
    //   35	44	61	android/os/RemoteException
    //   2	20	74	java/lang/Throwable
    //   24	32	74	java/lang/Throwable
    //   35	44	74	java/lang/Throwable
    //   44	58	74	java/lang/Throwable
    //   62	71	74	java/lang/Throwable
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    try
    {
      if (Log.isLoggable("MessengerIpcClient", 2)) {
        Log.v("MessengerIpcClient", "Service disconnected");
      }
      close(2, "Service disconnected");
      return;
    }
    catch (Throwable paramComponentName)
    {
      throw paramComponentName;
    }
  }
  
  final void set()
  {
    try
    {
      if (state == 1) {
        close(1, "Timed out while binding");
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
