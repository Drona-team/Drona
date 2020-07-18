package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import androidx.annotation.RestrictTo;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract interface TintableImageSourceView
{
  public abstract ColorStateList getSupportImageTintList();
  
  public abstract PorterDuff.Mode getSupportImageTintMode();
  
  public abstract void setSupportImageTintList(ColorStateList paramColorStateList);
  
  public abstract void setSupportImageTintMode(PorterDuff.Mode paramMode);
}
