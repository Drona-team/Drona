package com.facebook.drawee.controller;

import android.graphics.drawable.Animatable;

public class BaseControllerListener<INFO>
  implements ControllerListener<INFO>
{
  private static final ControllerListener<Object> NO_OP_LISTENER = new BaseControllerListener();
  
  public BaseControllerListener() {}
  
  public static ControllerListener getNoOpListener()
  {
    return NO_OP_LISTENER;
  }
  
  public void onFailure(String paramString, Throwable paramThrowable) {}
  
  public void onFinalImageSet(String paramString, Object paramObject, Animatable paramAnimatable) {}
  
  public void onIntermediateImageFailed(String paramString, Throwable paramThrowable) {}
  
  public void onIntermediateImageSet(String paramString, Object paramObject) {}
  
  public void onRelease(String paramString) {}
  
  public void onSubmit(String paramString, Object paramObject) {}
}
