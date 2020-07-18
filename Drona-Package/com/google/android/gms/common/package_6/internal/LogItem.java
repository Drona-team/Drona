package com.google.android.gms.common.package_6.internal;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.zai;
import com.google.android.gms.common.package_6.AvailabilityException;
import com.google.android.gms.common.package_6.GoogleApi;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class LogItem
{
  private final ArrayMap<zai<?>, ConnectionResult> zaay = new ArrayMap();
  private final ArrayMap<zai<?>, String> zadb = new ArrayMap();
  private final TaskCompletionSource<Map<zai<?>, String>> zadc = new TaskCompletionSource();
  private int zadd;
  private boolean zade = false;
  
  public LogItem(Iterable paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      GoogleApi localGoogleApi = (GoogleApi)paramIterable.next();
      zaay.put(localGoogleApi.get(), null);
    }
    zadd = zaay.keySet().size();
  }
  
  public final Task getTask()
  {
    return zadc.getTask();
  }
  
  public final Set getTypes()
  {
    return zaay.keySet();
  }
  
  public final void setTimestamp(Msg paramMsg, ConnectionResult paramConnectionResult, String paramString)
  {
    zaay.put(paramMsg, paramConnectionResult);
    zadb.put(paramMsg, paramString);
    zadd -= 1;
    if (!paramConnectionResult.isSuccess()) {
      zade = true;
    }
    if (zadd == 0)
    {
      if (zade)
      {
        paramMsg = new AvailabilityException(zaay);
        zadc.setException(paramMsg);
        return;
      }
      zadc.setResult(zadb);
    }
  }
}
