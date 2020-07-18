package com.google.android.gms.common.images;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.internal.base.zak;

public abstract class Layer
{
  final Response zamv;
  private int zamw = 0;
  protected int zamx = 0;
  private boolean zamy = false;
  private boolean zamz = true;
  private boolean zana = false;
  private boolean zanb = true;
  
  public Layer(Uri paramUri, int paramInt)
  {
    zamv = new Response(paramUri);
    zamx = paramInt;
  }
  
  final void load(Context paramContext, Bitmap paramBitmap, boolean paramBoolean)
  {
    Asserts.checkNotNull(paramBitmap);
    loadImage(new BitmapDrawable(paramContext.getResources(), paramBitmap), paramBoolean, false, true);
  }
  
  final void load(Context paramContext, zak paramZak)
  {
    if (zanb) {
      loadImage(null, false, true, false);
    }
  }
  
  final void load(Context paramContext, zak paramZak, boolean paramBoolean)
  {
    if (zamx != 0)
    {
      int i = zamx;
      paramContext = paramContext.getResources().getDrawable(i);
    }
    else
    {
      paramContext = null;
    }
    loadImage(paramContext, paramBoolean, false, false);
  }
  
  protected abstract void loadImage(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  protected final boolean loadImage(boolean paramBoolean1, boolean paramBoolean2)
  {
    return (zamz) && (!paramBoolean2) && (!paramBoolean1);
  }
}
