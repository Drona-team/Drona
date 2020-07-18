package com.google.android.gms.common.package_6.internal;

import android.util.Log;
import androidx.collection.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.zai;
import com.google.android.gms.common.package_6.AvailabilityException;
import com.google.android.gms.common.package_6.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

final class zaaa
  implements OnCompleteListener<Map<zai<?>, String>>
{
  private SignInConnectionListener zafj;
  
  zaaa(TaskManager paramTaskManager, SignInConnectionListener paramSignInConnectionListener)
  {
    zafj = paramSignInConnectionListener;
  }
  
  final void cancel()
  {
    zafj.onComplete();
  }
  
  public final void onComplete(Task paramTask)
  {
    TaskManager.getLock(zafi).lock();
    try
    {
      boolean bool = TaskManager.isConnected(zafi);
      if (!bool)
      {
        zafj.onComplete();
        TaskManager.getLock(zafi).unlock();
        return;
      }
      bool = paramTask.isSuccessful();
      Object localObject;
      if (bool)
      {
        TaskManager.getString(zafi, new ArrayMap(TaskManager.getMountPoints(zafi).size()));
        paramTask = TaskManager.getMountPoints(zafi).values().iterator();
        for (;;)
        {
          bool = paramTask.hasNext();
          if (!bool) {
            break;
          }
          localObject = (Errors)paramTask.next();
          TaskManager.next(zafi).put(((GoogleApi)localObject).get(), ConnectionResult.RESULT_SUCCESS);
        }
      }
      bool = paramTask.getException() instanceof AvailabilityException;
      if (bool)
      {
        paramTask = (AvailabilityException)paramTask.getException();
        bool = TaskManager.hasNext(zafi);
        if (bool)
        {
          TaskManager.getString(zafi, new ArrayMap(TaskManager.getMountPoints(zafi).size()));
          localObject = TaskManager.getMountPoints(zafi).values().iterator();
          for (;;)
          {
            bool = ((Iterator)localObject).hasNext();
            if (!bool) {
              break;
            }
            Errors localErrors = (Errors)((Iterator)localObject).next();
            Msg localMsg = localErrors.get();
            ConnectionResult localConnectionResult = paramTask.getConnectionResult(localErrors);
            bool = TaskManager.next(zafi, localErrors, localConnectionResult);
            if (bool) {
              TaskManager.next(zafi).put(localMsg, new ConnectionResult(16));
            } else {
              TaskManager.next(zafi).put(localMsg, localConnectionResult);
            }
          }
        }
        TaskManager.getString(zafi, paramTask.getString());
      }
      else
      {
        Log.e("ConnectionlessGAC", "Unexpected availability exception", paramTask.getException());
        TaskManager.getString(zafi, Collections.emptyMap());
      }
      bool = zafi.isConnected();
      if (bool)
      {
        TaskManager.get(zafi).putAll(TaskManager.next(zafi));
        paramTask = TaskManager.getString(zafi);
        if (paramTask == null)
        {
          TaskManager.finish(zafi);
          TaskManager.removeTask(zafi);
          TaskManager.doToast(zafi).signalAll();
        }
      }
      zafj.onComplete();
      TaskManager.getLock(zafi).unlock();
      return;
    }
    catch (Throwable paramTask)
    {
      TaskManager.getLock(zafi).unlock();
      throw paramTask;
    }
  }
}
