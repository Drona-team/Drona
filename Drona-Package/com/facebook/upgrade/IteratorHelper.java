package com.facebook.upgrade;

import androidx.annotation.Nullable;
import com.facebook.proguard.annotations.DoNotStrip;
import java.util.Iterator;

@DoNotStrip
public class IteratorHelper
{
  @Nullable
  @DoNotStrip
  private Object mElement;
  private final Iterator mIterator;
  
  public IteratorHelper(Iterable paramIterable)
  {
    mIterator = paramIterable.iterator();
  }
  
  public IteratorHelper(Iterator paramIterator)
  {
    mIterator = paramIterator;
  }
  
  boolean hasNext()
  {
    if (mIterator.hasNext())
    {
      mElement = mIterator.next();
      return true;
    }
    mElement = null;
    return false;
  }
}
