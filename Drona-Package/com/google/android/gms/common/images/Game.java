package com.google.android.gms.common.images;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Objects;
import java.lang.ref.WeakReference;

public final class Game
  extends Layer
{
  private WeakReference<ImageManager.OnImageLoadedListener> zand;
  
  public Game(ImageManager.OnImageLoadedListener paramOnImageLoadedListener, Uri paramUri)
  {
    super(paramUri, 0);
    Asserts.checkNotNull(paramOnImageLoadedListener);
    zand = new WeakReference(paramOnImageLoadedListener);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Game)) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    paramObject = (Game)paramObject;
    ImageManager.OnImageLoadedListener localOnImageLoadedListener1 = (ImageManager.OnImageLoadedListener)zand.get();
    ImageManager.OnImageLoadedListener localOnImageLoadedListener2 = (ImageManager.OnImageLoadedListener)zand.get();
    return (localOnImageLoadedListener2 != null) && (localOnImageLoadedListener1 != null) && (Objects.equal(localOnImageLoadedListener2, localOnImageLoadedListener1)) && (Objects.equal(zamv, zamv));
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { zamv });
  }
  
  protected final void loadImage(Drawable paramDrawable, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (!paramBoolean2)
    {
      ImageManager.OnImageLoadedListener localOnImageLoadedListener = (ImageManager.OnImageLoadedListener)zand.get();
      if (localOnImageLoadedListener != null) {
        localOnImageLoadedListener.onImageLoaded(zamv.url, paramDrawable, paramBoolean3);
      }
    }
  }
}
