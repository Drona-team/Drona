package com.facebook.yoga;

public class YogaNodeJNIFinalizer
  extends YogaNodeJNIBase
{
  public YogaNodeJNIFinalizer() {}
  
  public YogaNodeJNIFinalizer(YogaConfig paramYogaConfig)
  {
    super(paramYogaConfig);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      freeNatives();
      super.finalize();
      return;
    }
    catch (Throwable localThrowable)
    {
      super.finalize();
      throw localThrowable;
    }
  }
  
  public void freeNatives()
  {
    if (mNativePointer != 0L)
    {
      long l = mNativePointer;
      mNativePointer = 0L;
      YogaNative.jni_YGNodeFree(l);
    }
  }
}
