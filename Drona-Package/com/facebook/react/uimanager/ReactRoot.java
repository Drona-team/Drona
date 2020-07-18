package com.facebook.react.uimanager;

import android.os.Bundle;
import android.view.ViewGroup;

public abstract interface ReactRoot
{
  public abstract Bundle getAppProperties();
  
  public abstract int getHeightMeasureSpec();
  
  public abstract String getInitialUITemplate();
  
  public abstract String getJSModuleName();
  
  public abstract ViewGroup getRootViewGroup();
  
  public abstract int getRootViewTag();
  
  public abstract int getUIManagerType();
  
  public abstract int getWidthMeasureSpec();
  
  public abstract void onStage(int paramInt);
  
  public abstract void runApplication();
  
  public abstract void setRootViewTag(int paramInt);
  
  public abstract void setShouldLogContentAppeared(boolean paramBoolean);
}
