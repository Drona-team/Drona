package com.bumptech.glide.load.engine.prefill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class PreFillQueue
{
  private final Map<PreFillType, Integer> bitmapsPerType;
  private int bitmapsRemaining;
  private int keyIndex;
  private final List<PreFillType> keyList;
  
  public PreFillQueue(Map paramMap)
  {
    bitmapsPerType = paramMap;
    keyList = new ArrayList(paramMap.keySet());
    paramMap = paramMap.values().iterator();
    while (paramMap.hasNext())
    {
      Integer localInteger = (Integer)paramMap.next();
      bitmapsRemaining += localInteger.intValue();
    }
  }
  
  public int getSize()
  {
    return bitmapsRemaining;
  }
  
  public boolean isEmpty()
  {
    return bitmapsRemaining == 0;
  }
  
  public PreFillType remove()
  {
    PreFillType localPreFillType = (PreFillType)keyList.get(keyIndex);
    Integer localInteger = (Integer)bitmapsPerType.get(localPreFillType);
    if (localInteger.intValue() == 1)
    {
      bitmapsPerType.remove(localPreFillType);
      keyList.remove(keyIndex);
    }
    else
    {
      bitmapsPerType.put(localPreFillType, Integer.valueOf(localInteger.intValue() - 1));
    }
    bitmapsRemaining -= 1;
    int i;
    if (keyList.isEmpty()) {
      i = 0;
    } else {
      i = (keyIndex + 1) % keyList.size();
    }
    keyIndex = i;
    return localPreFillType;
  }
}
