package com.facebook.react;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import com.facebook.react.bridge.MemoryPressureListener;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class MemoryPressureRouter
  implements ComponentCallbacks2
{
  private final Set<MemoryPressureListener> mListeners = Collections.synchronizedSet(new LinkedHashSet());
  
  MemoryPressureRouter(Context paramContext)
  {
    paramContext.getApplicationContext().registerComponentCallbacks(this);
  }
  
  private void dispatchMemoryPressure(int paramInt)
  {
    MemoryPressureListener[] arrayOfMemoryPressureListener = (MemoryPressureListener[])mListeners.toArray(new MemoryPressureListener[mListeners.size()]);
    int j = arrayOfMemoryPressureListener.length;
    int i = 0;
    while (i < j)
    {
      arrayOfMemoryPressureListener[i].handleMemoryPressure(paramInt);
      i += 1;
    }
  }
  
  public void addMemoryPressureListener(MemoryPressureListener paramMemoryPressureListener)
  {
    mListeners.add(paramMemoryPressureListener);
  }
  
  public void destroy(Context paramContext)
  {
    paramContext.getApplicationContext().unregisterComponentCallbacks(this);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onLowMemory() {}
  
  public void onTrimMemory(int paramInt)
  {
    dispatchMemoryPressure(paramInt);
  }
  
  public void removeMemoryPressureListener(MemoryPressureListener paramMemoryPressureListener)
  {
    mListeners.remove(paramMemoryPressureListener);
  }
}
