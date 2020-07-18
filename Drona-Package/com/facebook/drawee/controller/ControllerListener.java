package com.facebook.drawee.controller;

import android.graphics.drawable.Animatable;

public abstract interface ControllerListener<INFO>
{
  public abstract void onFailure(String paramString, Throwable paramThrowable);
  
  public abstract void onFinalImageSet(String paramString, Object paramObject, Animatable paramAnimatable);
  
  public abstract void onIntermediateImageFailed(String paramString, Throwable paramThrowable);
  
  public abstract void onIntermediateImageSet(String paramString, Object paramObject);
  
  public abstract void onRelease(String paramString);
  
  public abstract void onSubmit(String paramString, Object paramObject);
}
