package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;

public abstract interface TintableCompoundDrawablesView
{
  public abstract ColorStateList getSupportCompoundDrawablesTintList();
  
  public abstract PorterDuff.Mode getSupportCompoundDrawablesTintMode();
  
  public abstract void setSupportCompoundDrawablesTintList(ColorStateList paramColorStateList);
  
  public abstract void setSupportCompoundDrawablesTintMode(PorterDuff.Mode paramMode);
}
