package androidx.transition;

import android.graphics.drawable.Drawable;

abstract interface ViewOverlayImpl
{
  public abstract void addWaypoints(Drawable paramDrawable);
  
  public abstract void remove(Drawable paramDrawable);
}
