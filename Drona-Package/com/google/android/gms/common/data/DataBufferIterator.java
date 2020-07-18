package com.google.android.gms.common.data;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import java.util.Iterator;
import java.util.NoSuchElementException;

@KeepForSdk
public class DataBufferIterator<T>
  implements Iterator<T>
{
  protected final DataBuffer<T> zalk;
  protected int zall;
  
  public DataBufferIterator(DataBuffer paramDataBuffer)
  {
    zalk = ((DataBuffer)Preconditions.checkNotNull(paramDataBuffer));
    zall = -1;
  }
  
  public boolean hasNext()
  {
    return zall < zalk.getCount() - 1;
  }
  
  public Object next()
  {
    if (hasNext())
    {
      localObject = zalk;
      i = zall + 1;
      zall = i;
      return ((DataBuffer)localObject).get(i);
    }
    int i = zall;
    Object localObject = new StringBuilder(46);
    ((StringBuilder)localObject).append("Cannot advance the iterator beyond ");
    ((StringBuilder)localObject).append(i);
    throw new NoSuchElementException(((StringBuilder)localObject).toString());
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException("Cannot remove elements from a DataBufferIterator");
  }
}