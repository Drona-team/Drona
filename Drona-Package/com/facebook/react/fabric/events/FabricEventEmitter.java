package com.facebook.react.fabric.events;

import android.util.Pair;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.fabric.FabricUIManager;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.systrace.Systrace;
import java.util.HashSet;
import java.util.Set;

public class FabricEventEmitter
  implements RCTEventEmitter
{
  private static final String TAG = "FabricEventEmitter";
  private final FabricUIManager mUIManager;
  
  public FabricEventEmitter(FabricUIManager paramFabricUIManager)
  {
    mUIManager = paramFabricUIManager;
  }
  
  private WritableArray copyWritableArray(WritableArray paramWritableArray)
  {
    WritableNativeArray localWritableNativeArray = new WritableNativeArray();
    int i = 0;
    while (i < paramWritableArray.size())
    {
      localWritableNativeArray.pushMap(getWritableMap(paramWritableArray.getMap(i)));
      i += 1;
    }
    return localWritableNativeArray;
  }
  
  private WritableMap getWritableMap(ReadableMap paramReadableMap)
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    localWritableNativeMap.merge(paramReadableMap);
    return localWritableNativeMap;
  }
  
  private Pair removeTouchesAtIndices(WritableArray paramWritableArray1, WritableArray paramWritableArray2)
  {
    WritableNativeArray localWritableNativeArray1 = new WritableNativeArray();
    WritableNativeArray localWritableNativeArray2 = new WritableNativeArray();
    HashSet localHashSet = new HashSet();
    int k = 0;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      if (i >= paramWritableArray2.size()) {
        break;
      }
      j = paramWritableArray2.getInt(i);
      localWritableNativeArray1.pushMap(getWritableMap(paramWritableArray1.getMap(j)));
      localHashSet.add(Integer.valueOf(j));
      i += 1;
    }
    while (j < paramWritableArray1.size())
    {
      if (!localHashSet.contains(Integer.valueOf(j))) {
        localWritableNativeArray2.pushMap(getWritableMap(paramWritableArray1.getMap(j)));
      }
      j += 1;
    }
    return new Pair(localWritableNativeArray1, localWritableNativeArray2);
  }
  
  private Pair touchSubsequence(WritableArray paramWritableArray1, WritableArray paramWritableArray2)
  {
    WritableNativeArray localWritableNativeArray = new WritableNativeArray();
    int i = 0;
    while (i < paramWritableArray2.size())
    {
      localWritableNativeArray.pushMap(getWritableMap(paramWritableArray1.getMap(paramWritableArray2.getInt(i))));
      i += 1;
    }
    return new Pair(localWritableNativeArray, paramWritableArray1);
  }
  
  public void receiveEvent(int paramInt, String paramString, WritableMap paramWritableMap)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("FabricEventEmitter.receiveEvent('");
    localStringBuilder.append(paramString);
    localStringBuilder.append("')");
    Systrace.beginSection(0L, localStringBuilder.toString());
    mUIManager.receiveEvent(paramInt, paramString, paramWritableMap);
    Systrace.endSection(0L);
  }
  
  public void receiveTouches(String paramString, WritableArray paramWritableArray1, WritableArray paramWritableArray2)
  {
    if ((!"topTouchEnd".equalsIgnoreCase(paramString)) && (!"topTouchCancel".equalsIgnoreCase(paramString))) {
      paramWritableArray1 = touchSubsequence(paramWritableArray1, paramWritableArray2);
    } else {
      paramWritableArray1 = removeTouchesAtIndices(paramWritableArray1, paramWritableArray2);
    }
    paramWritableArray2 = (WritableArray)first;
    paramWritableArray1 = (WritableArray)second;
    int i = 0;
    while (i < paramWritableArray2.size())
    {
      WritableMap localWritableMap = getWritableMap(paramWritableArray2.getMap(i));
      localWritableMap.putArray("changedTouches", copyWritableArray(paramWritableArray2));
      localWritableMap.putArray("touches", copyWritableArray(paramWritableArray1));
      int k = localWritableMap.getInt("target");
      int j = k;
      if (k < 1)
      {
        FLog.e(TAG, "A view is reporting that a touch occurred on tag zero.");
        j = 0;
      }
      receiveEvent(j, paramString, localWritableMap);
      i += 1;
    }
  }
}