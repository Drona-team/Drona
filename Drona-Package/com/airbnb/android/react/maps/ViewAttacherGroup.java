package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.View;
import com.facebook.react.views.view.ReactViewGroup;

public class ViewAttacherGroup
  extends ReactViewGroup
{
  public ViewAttacherGroup(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(true);
    setVisibility(0);
    setAlpha(0.0F);
    setRemoveClippedSubviews(false);
    if (Build.VERSION.SDK_INT >= 18) {
      setClipBounds(new Rect(0, 0, 0, 0));
    }
    setOverflow("hidden");
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
}
