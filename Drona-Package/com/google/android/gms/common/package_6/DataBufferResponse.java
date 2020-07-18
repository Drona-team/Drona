package com.google.android.gms.common.package_6;

import android.os.Bundle;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.data.AbstractDataBuffer;
import com.google.android.gms.common.data.DataBuffer;
import java.util.Iterator;

@KeepForSdk
public class DataBufferResponse<T, R extends AbstractDataBuffer<T>,  extends com.google.android.gms.common.api.Result>
  extends com.google.android.gms.common.api.Response<R>
  implements DataBuffer<T>
{
  public DataBufferResponse() {}
  
  public DataBufferResponse(AbstractDataBuffer paramAbstractDataBuffer)
  {
    super((Result)paramAbstractDataBuffer);
  }
  
  public void close()
  {
    ((AbstractDataBuffer)getResult()).close();
  }
  
  public Object get(int paramInt)
  {
    return ((AbstractDataBuffer)getResult()).get(paramInt);
  }
  
  public int getCount()
  {
    return ((AbstractDataBuffer)getResult()).getCount();
  }
  
  public Bundle getMetadata()
  {
    return ((AbstractDataBuffer)getResult()).getMetadata();
  }
  
  public boolean isClosed()
  {
    return ((AbstractDataBuffer)getResult()).isClosed();
  }
  
  public Iterator iterator()
  {
    return ((AbstractDataBuffer)getResult()).iterator();
  }
  
  public void release()
  {
    ((AbstractDataBuffer)getResult()).release();
  }
  
  public Iterator singleRefIterator()
  {
    return ((AbstractDataBuffer)getResult()).singleRefIterator();
  }
}
