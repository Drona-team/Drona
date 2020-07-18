package com.google.android.gms.common.images;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.internal.base.zae;
import com.google.android.gms.internal.base.zaj;
import java.lang.ref.WeakReference;

public final class Image
  extends Layer
{
  private WeakReference<ImageView> zanc;
  
  public Image(ImageView paramImageView, int paramInt)
  {
    super(null, paramInt);
    Asserts.checkNotNull(paramImageView);
    zanc = new WeakReference(paramImageView);
  }
  
  public Image(ImageView paramImageView, Uri paramUri)
  {
    super(paramUri, 0);
    Asserts.checkNotNull(paramImageView);
    zanc = new WeakReference(paramImageView);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Image)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    Object localObject = (Image)paramObject;
    paramObject = (ImageView)zanc.get();
    localObject = (ImageView)zanc.get();
    return (localObject != null) && (paramObject != null) && (Objects.equal(localObject, paramObject));
  }
  
  public final int hashCode()
  {
    return 0;
  }
  
  protected final void loadImage(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    ImageView localImageView = (ImageView)zanc.get();
    if (localImageView != null)
    {
      int j = 0;
      int i;
      if ((!paramBoolean2) && (!paramBoolean3)) {
        i = 1;
      } else {
        i = 0;
      }
      if ((i != 0) && ((localImageView instanceof zaj)))
      {
        int k = zaj.zach();
        if ((zamx != 0) && (k == zamx)) {}
      }
      else
      {
        paramBoolean1 = loadImage(paramBoolean1, paramBoolean2);
        Object localObject2 = null;
        Object localObject1 = paramDrawable;
        if (paramBoolean1)
        {
          Drawable localDrawable = localImageView.getDrawable();
          localObject1 = localDrawable;
          if (localDrawable != null)
          {
            if ((localDrawable instanceof zae)) {
              localObject1 = ((zae)localDrawable).zacf();
            }
          }
          else {
            localObject1 = null;
          }
          localObject1 = new zae((Drawable)localObject1, (Drawable)paramDrawable);
        }
        localImageView.setImageDrawable((Drawable)localObject1);
        if ((localImageView instanceof zaj))
        {
          paramDrawable = localObject2;
          if (paramBoolean3) {
            paramDrawable = zamv.url;
          }
          zaj.zaa(paramDrawable);
          if (i != 0) {
            j = zamx;
          }
          zaj.zai(j);
        }
        if (paramBoolean1) {
          ((zae)localObject1).startTransition(250);
        }
      }
    }
  }
}
