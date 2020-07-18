package com.facebook.upgrade;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.soloader.SoLoader;

@DoNotStrip
public class HybridData
{
  @DoNotStrip
  private Destructor mDestructor = new Destructor();
  
  static
  {
    SoLoader.loadLibrary("fb");
  }
  
  public HybridData() {}
  
  public boolean isValid()
  {
    return mDestructor.mNativePointer != 0L;
  }
  
  public void resetNative()
  {
    try
    {
      mDestructor.destruct();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public class Destructor
    extends DestructorThread.Destructor
  {
    @DoNotStrip
    private long mNativePointer;
    
    Destructor()
    {
      super();
    }
    
    static native void deleteNative(long paramLong);
    
    protected final void destruct()
    {
      deleteNative(mNativePointer);
      mNativePointer = 0L;
    }
  }
}
