package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Util;

public abstract class CustomTarget<T>
  implements Target<T>
{
  private final int height;
  @Nullable
  private Request request;
  private final int width;
  
  public CustomTarget()
  {
    this(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public CustomTarget(int paramInt1, int paramInt2)
  {
    if (Util.isValidDimensions(paramInt1, paramInt2))
    {
      width = paramInt1;
      height = paramInt2;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width: ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(" and height: ");
    localStringBuilder.append(paramInt2);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public final Request getRequest()
  {
    return request;
  }
  
  public final void getSize(SizeReadyCallback paramSizeReadyCallback)
  {
    paramSizeReadyCallback.onSizeReady(width, height);
  }
  
  public void onDestroy() {}
  
  public void onLoadFailed(Drawable paramDrawable) {}
  
  public void onLoadStarted(Drawable paramDrawable) {}
  
  public void onStart() {}
  
  public void onStop() {}
  
  public final void removeCallback(SizeReadyCallback paramSizeReadyCallback) {}
  
  public final void setRequest(Request paramRequest)
  {
    request = paramRequest;
  }
}
