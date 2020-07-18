package com.airbnb.android.react.maps;

import android.os.Handler;
import android.os.Looper;
import java.util.Iterator;
import java.util.LinkedList;

public class ViewChangesTracker
{
  private static ViewChangesTracker instance;
  private final long delay = 40L;
  private Handler handler = new Handler(Looper.myLooper());
  private boolean hasScheduledFrame = false;
  private LinkedList<AirMapMarker> markers = new LinkedList();
  private LinkedList<AirMapMarker> markersToRemove = new LinkedList();
  private Runnable updateRunnable = new Runnable()
  {
    public void run()
    {
      ViewChangesTracker.access$002(ViewChangesTracker.this, false);
      update();
      if (markers.size() > 0) {
        handler.postDelayed(updateRunnable, 40L);
      }
    }
  };
  
  private ViewChangesTracker() {}
  
  static ViewChangesTracker getInstance()
  {
    if (instance == null) {
      try
      {
        instance = new ViewChangesTracker();
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    return instance;
  }
  
  public void addMarker(AirMapMarker paramAirMapMarker)
  {
    markers.add(paramAirMapMarker);
    if (!hasScheduledFrame)
    {
      hasScheduledFrame = true;
      handler.postDelayed(updateRunnable, 40L);
    }
  }
  
  public boolean containsMarker(AirMapMarker paramAirMapMarker)
  {
    return markers.contains(paramAirMapMarker);
  }
  
  public void removeMarker(AirMapMarker paramAirMapMarker)
  {
    markers.remove(paramAirMapMarker);
  }
  
  public void update()
  {
    Iterator localIterator = markers.iterator();
    while (localIterator.hasNext())
    {
      AirMapMarker localAirMapMarker = (AirMapMarker)localIterator.next();
      if (!localAirMapMarker.updateCustomForTracking()) {
        markersToRemove.add(localAirMapMarker);
      }
    }
    if (markersToRemove.size() > 0)
    {
      markers.removeAll(markersToRemove);
      markersToRemove.clear();
    }
  }
}
