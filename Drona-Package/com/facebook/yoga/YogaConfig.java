package com.facebook.yoga;

public class YogaConfig
{
  public static int SPACING_TYPE;
  private YogaLogger mLogger;
  long mNativePointer = YogaNative.jni_YGConfigNew();
  private YogaNodeCloneFunction mYogaNodeCloneFunction;
  
  public YogaConfig()
  {
    if (mNativePointer != 0L) {
      return;
    }
    throw new IllegalStateException("Failed to allocate native memory");
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      YogaNative.jni_YGConfigFree(mNativePointer);
      super.finalize();
      return;
    }
    catch (Throwable localThrowable)
    {
      super.finalize();
      throw localThrowable;
    }
  }
  
  public YogaLogger getLogger()
  {
    return mLogger;
  }
  
  public void setExperimentalFeatureEnabled(YogaExperimentalFeature paramYogaExperimentalFeature, boolean paramBoolean)
  {
    YogaNative.jni_YGConfigSetExperimentalFeatureEnabled(mNativePointer, paramYogaExperimentalFeature.intValue(), paramBoolean);
  }
  
  public void setLogger(YogaLogger paramYogaLogger)
  {
    mLogger = paramYogaLogger;
    YogaNative.jni_YGConfigSetLogger(mNativePointer, paramYogaLogger);
  }
  
  public void setPointScaleFactor(float paramFloat)
  {
    YogaNative.jni_YGConfigSetPointScaleFactor(mNativePointer, paramFloat);
  }
  
  public void setPrintTreeFlag(boolean paramBoolean)
  {
    YogaNative.jni_YGConfigSetPrintTreeFlag(mNativePointer, paramBoolean);
  }
  
  public void setShouldDiffLayoutWithoutLegacyStretchBehaviour(boolean paramBoolean)
  {
    YogaNative.jni_YGConfigSetShouldDiffLayoutWithoutLegacyStretchBehaviour(mNativePointer, paramBoolean);
  }
  
  public void setUseLegacyStretchBehaviour(boolean paramBoolean)
  {
    YogaNative.jni_YGConfigSetUseLegacyStretchBehaviour(mNativePointer, paramBoolean);
  }
  
  public void setUseWebDefaults(boolean paramBoolean)
  {
    YogaNative.jni_YGConfigSetUseWebDefaults(mNativePointer, paramBoolean);
  }
}
