package com.facebook.common.references;

import java.lang.ref.SoftReference;

public class OOMSoftReference<T>
{
  SoftReference<T> softRef1 = null;
  SoftReference<T> softRef2 = null;
  SoftReference<T> softRef3 = null;
  
  public OOMSoftReference() {}
  
  public void clear()
  {
    if (softRef1 != null)
    {
      softRef1.clear();
      softRef1 = null;
    }
    if (softRef2 != null)
    {
      softRef2.clear();
      softRef2 = null;
    }
    if (softRef3 != null)
    {
      softRef3.clear();
      softRef3 = null;
    }
  }
  
  public Object get()
  {
    if (softRef1 == null) {
      return null;
    }
    return softRef1.get();
  }
  
  public void offer(Object paramObject)
  {
    softRef1 = new SoftReference(paramObject);
    softRef2 = new SoftReference(paramObject);
    softRef3 = new SoftReference(paramObject);
  }
}
