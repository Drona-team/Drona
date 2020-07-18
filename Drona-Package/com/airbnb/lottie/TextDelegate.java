package com.airbnb.lottie;

import android.view.View;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TextDelegate
{
  @Nullable
  private final LottieAnimationView animationView;
  private boolean cacheText = true;
  @Nullable
  private final LottieDrawable drawable;
  private final Map<String, String> stringMap = new HashMap();
  
  TextDelegate()
  {
    animationView = null;
    drawable = null;
  }
  
  public TextDelegate(LottieAnimationView paramLottieAnimationView)
  {
    animationView = paramLottieAnimationView;
    drawable = null;
  }
  
  public TextDelegate(LottieDrawable paramLottieDrawable)
  {
    drawable = paramLottieDrawable;
    animationView = null;
  }
  
  private String getText(String paramString)
  {
    return paramString;
  }
  
  private void invalidate()
  {
    if (animationView != null) {
      animationView.invalidate();
    }
    if (drawable != null) {
      drawable.invalidateSelf();
    }
  }
  
  public final String getTextInternal(String paramString)
  {
    if ((cacheText) && (stringMap.containsKey(paramString))) {
      return (String)stringMap.get(paramString);
    }
    String str = getText(paramString);
    if (cacheText) {
      stringMap.put(paramString, str);
    }
    return str;
  }
  
  public void invalidateAllText()
  {
    stringMap.clear();
    invalidate();
  }
  
  public void invalidateText(String paramString)
  {
    stringMap.remove(paramString);
    invalidate();
  }
  
  public void setCacheText(boolean paramBoolean)
  {
    cacheText = paramBoolean;
  }
  
  public void setText(String paramString1, String paramString2)
  {
    stringMap.put(paramString1, paramString2);
    invalidate();
  }
}
