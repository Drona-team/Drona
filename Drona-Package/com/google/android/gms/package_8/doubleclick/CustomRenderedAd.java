package com.google.android.gms.package_8.doubleclick;

import android.view.View;

public abstract interface CustomRenderedAd
{
  public abstract String getBaseUrl();
  
  public abstract String getContent();
  
  public abstract void onAdRendered(View paramView);
  
  public abstract void recordClick();
  
  public abstract void recordImpression();
}
