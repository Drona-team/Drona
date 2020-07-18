package androidx.core.view;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.DisplayCutout;
import java.util.List;

public final class DisplayCutoutCompat
{
  private final Object mDisplayCutout;
  
  public DisplayCutoutCompat(Rect paramRect, List paramList)
  {
    this(paramRect);
  }
  
  private DisplayCutoutCompat(Object paramObject)
  {
    mDisplayCutout = paramObject;
  }
  
  static DisplayCutoutCompat wrap(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    return new DisplayCutoutCompat(paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (DisplayCutoutCompat)paramObject;
      if (mDisplayCutout == null) {
        return mDisplayCutout == null;
      }
      return mDisplayCutout.equals(mDisplayCutout);
    }
    return false;
  }
  
  public List getBoundingRects()
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return ((DisplayCutout)mDisplayCutout).getBoundingRects();
    }
    return null;
  }
  
  public int getSafeInsetBottom()
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return ((DisplayCutout)mDisplayCutout).getSafeInsetBottom();
    }
    return 0;
  }
  
  public int getSafeInsetLeft()
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return ((DisplayCutout)mDisplayCutout).getSafeInsetLeft();
    }
    return 0;
  }
  
  public int getSafeInsetRight()
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return ((DisplayCutout)mDisplayCutout).getSafeInsetRight();
    }
    return 0;
  }
  
  public int getSafeInsetTop()
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return ((DisplayCutout)mDisplayCutout).getSafeInsetTop();
    }
    return 0;
  }
  
  public int hashCode()
  {
    if (mDisplayCutout == null) {
      return 0;
    }
    return mDisplayCutout.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DisplayCutoutCompat{");
    localStringBuilder.append(mDisplayCutout);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}
