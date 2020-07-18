package com.google.android.gms.package_8.formats;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import java.util.List;

public abstract class NativeAd
{
  public static final String ASSET_ADCHOICES_CONTAINER_VIEW = "1098";
  
  public NativeAd() {}
  
  public abstract void performClick(Bundle paramBundle);
  
  public abstract boolean recordImpression(Bundle paramBundle);
  
  public abstract void reportTouchEvent(Bundle paramBundle);
  
  protected abstract Object zzkq();
  
  public abstract class AdChoicesInfo
  {
    public AdChoicesInfo() {}
    
    public abstract List getImages();
    
    public abstract CharSequence getText();
  }
  
  public abstract class Image
  {
    public Image() {}
    
    public abstract Drawable getDrawable();
    
    public int getHeight()
    {
      return -1;
    }
    
    public abstract double getScale();
    
    public abstract Uri getUri();
    
    public int getWidth()
    {
      return -1;
    }
  }
}
