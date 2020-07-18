package com.facebook.react.uimanager;

import android.view.View;

public abstract interface ViewManagerDelegate<T extends View>
{
  public abstract void setProperty(View paramView, String paramString, Object paramObject);
}
