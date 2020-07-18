package com.facebook.react.bridge;

import com.facebook.proguard.annotations.DoNotStrip;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@DoNotStrip
public class ReactMarker
{
  private static final List<FabricMarkerListener> sFabricMarkerListeners = new CopyOnWriteArrayList();
  private static final List<MarkerListener> sListeners = new CopyOnWriteArrayList();
  
  public ReactMarker() {}
  
  public static void addFabricListener(FabricMarkerListener paramFabricMarkerListener)
  {
    if (!sFabricMarkerListeners.contains(paramFabricMarkerListener)) {
      sFabricMarkerListeners.add(paramFabricMarkerListener);
    }
  }
  
  public static void addListener(MarkerListener paramMarkerListener)
  {
    if (!sListeners.contains(paramMarkerListener)) {
      sListeners.add(paramMarkerListener);
    }
  }
  
  public static void clearFabricMarkerListeners()
  {
    sFabricMarkerListeners.clear();
  }
  
  public static void clearMarkerListeners()
  {
    sListeners.clear();
  }
  
  public static void logFabricMarker(ReactMarkerConstants paramReactMarkerConstants, String paramString, int paramInt)
  {
    logFabricMarker(paramReactMarkerConstants, paramString, paramInt, -1L);
  }
  
  public static void logFabricMarker(ReactMarkerConstants paramReactMarkerConstants, String paramString, int paramInt, long paramLong)
  {
    Iterator localIterator = sFabricMarkerListeners.iterator();
    while (localIterator.hasNext()) {
      ((FabricMarkerListener)localIterator.next()).logFabricMarker(paramReactMarkerConstants, paramString, paramInt, paramLong);
    }
  }
  
  public static void logMarker(ReactMarkerConstants paramReactMarkerConstants)
  {
    logMarker(paramReactMarkerConstants, null, 0);
  }
  
  public static void logMarker(ReactMarkerConstants paramReactMarkerConstants, int paramInt)
  {
    logMarker(paramReactMarkerConstants, null, paramInt);
  }
  
  public static void logMarker(ReactMarkerConstants paramReactMarkerConstants, String paramString)
  {
    logMarker(paramReactMarkerConstants, paramString, 0);
  }
  
  public static void logMarker(ReactMarkerConstants paramReactMarkerConstants, String paramString, int paramInt)
  {
    logFabricMarker(paramReactMarkerConstants, paramString, paramInt);
    Iterator localIterator = sListeners.iterator();
    while (localIterator.hasNext()) {
      ((MarkerListener)localIterator.next()).logMarker(paramReactMarkerConstants, paramString, paramInt);
    }
  }
  
  public static void logMarker(String paramString)
  {
    logMarker(paramString, null);
  }
  
  public static void logMarker(String paramString, int paramInt)
  {
    logMarker(paramString, null, paramInt);
  }
  
  public static void logMarker(String paramString1, String paramString2)
  {
    logMarker(paramString1, paramString2, 0);
  }
  
  public static void logMarker(String paramString1, String paramString2, int paramInt)
  {
    logMarker(ReactMarkerConstants.valueOf(paramString1), paramString2, paramInt);
  }
  
  public static void removeFabricListener(FabricMarkerListener paramFabricMarkerListener)
  {
    sFabricMarkerListeners.remove(paramFabricMarkerListener);
  }
  
  public static void removeListener(MarkerListener paramMarkerListener)
  {
    sListeners.remove(paramMarkerListener);
  }
  
  public static abstract interface FabricMarkerListener
  {
    public abstract void logFabricMarker(ReactMarkerConstants paramReactMarkerConstants, String paramString, int paramInt, long paramLong);
  }
  
  public static abstract interface MarkerListener
  {
    public abstract void logMarker(ReactMarkerConstants paramReactMarkerConstants, String paramString, int paramInt);
  }
}
