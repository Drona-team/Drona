package com.facebook.upgrade;

import androidx.annotation.Nullable;
import com.facebook.proguard.annotations.DoNotStrip;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@DoNotStrip
public class MapIteratorHelper
{
  @DoNotStrip
  private final Iterator<Map.Entry> mIterator;
  @Nullable
  @DoNotStrip
  private Object mKey;
  @Nullable
  @DoNotStrip
  private Object mValue;
  
  public MapIteratorHelper(Map paramMap)
  {
    mIterator = paramMap.entrySet().iterator();
  }
  
  boolean hasNext()
  {
    if (mIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)mIterator.next();
      mKey = localEntry.getKey();
      mValue = localEntry.getValue();
      return true;
    }
    mKey = null;
    mValue = null;
    return false;
  }
}
