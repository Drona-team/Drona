package com.bumptech.glide.util;

import android.view.View;
import com.bumptech.glide.ListPreloader.PreloadSizeProvider;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.Arrays;

public class ViewPreloadSizeProvider<T>
  implements ListPreloader.PreloadSizeProvider<T>, SizeReadyCallback
{
  private int[] size;
  private SizeViewTarget viewTarget;
  
  public ViewPreloadSizeProvider() {}
  
  public ViewPreloadSizeProvider(View paramView)
  {
    viewTarget = new SizeViewTarget(paramView, this);
  }
  
  public int[] getPreloadSize(Object paramObject, int paramInt1, int paramInt2)
  {
    if (size == null) {
      return null;
    }
    return Arrays.copyOf(size, size.length);
  }
  
  public void onSizeReady(int paramInt1, int paramInt2)
  {
    size = new int[] { paramInt1, paramInt2 };
    viewTarget = null;
  }
  
  public void setView(View paramView)
  {
    if (size == null)
    {
      if (viewTarget != null) {
        return;
      }
      viewTarget = new SizeViewTarget(paramView, this);
    }
  }
  
  private static final class SizeViewTarget
    extends ViewTarget<View, Object>
  {
    SizeViewTarget(View paramView, SizeReadyCallback paramSizeReadyCallback)
    {
      super();
      getSize(paramSizeReadyCallback);
    }
    
    public void onResourceReady(Object paramObject, Transition paramTransition) {}
  }
}
