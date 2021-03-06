package com.google.android.gms.common.data;

import com.google.android.gms.common.annotation.KeepForSdk;
import java.util.NoSuchElementException;

@KeepForSdk
public class SingleRefDataBufferIterator<T>
  extends DataBufferIterator<T>
{
  private T zamg;
  
  public SingleRefDataBufferIterator(DataBuffer paramDataBuffer)
  {
    super(paramDataBuffer);
  }
  
  public Object next()
  {
    if (hasNext())
    {
      zall += 1;
      if (zall == 0)
      {
        zamg = zalk.get(0);
        if (!(zamg instanceof DataBufferRef))
        {
          localObject = String.valueOf(zamg.getClass());
          StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject).length() + 44);
          localStringBuilder.append("DataBuffer reference of type ");
          localStringBuilder.append((String)localObject);
          localStringBuilder.append(" is not movable");
          throw new IllegalStateException(localStringBuilder.toString());
        }
      }
      else
      {
        ((DataBufferRef)zamg).register(zall);
      }
      return zamg;
    }
    int i = zall;
    Object localObject = new StringBuilder(46);
    ((StringBuilder)localObject).append("Cannot advance the iterator beyond ");
    ((StringBuilder)localObject).append(i);
    throw new NoSuchElementException(((StringBuilder)localObject).toString());
  }
}
