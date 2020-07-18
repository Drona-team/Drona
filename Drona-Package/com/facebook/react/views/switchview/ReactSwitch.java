package com.facebook.react.views.switchview;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.widget.CompoundButton;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

class ReactSwitch
  extends SwitchCompat
{
  private boolean mAllowChange = true;
  @Nullable
  private Integer mTrackColorForFalse = null;
  @Nullable
  private Integer mTrackColorForTrue = null;
  
  public ReactSwitch(Context paramContext)
  {
    super(paramContext);
  }
  
  private void setTrackColor(boolean paramBoolean)
  {
    if ((mTrackColorForTrue != null) || (mTrackColorForFalse != null))
    {
      Integer localInteger;
      if (paramBoolean) {
        localInteger = mTrackColorForTrue;
      } else {
        localInteger = mTrackColorForFalse;
      }
      setTrackColor(localInteger);
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if ((mAllowChange) && (isChecked() != paramBoolean))
    {
      mAllowChange = false;
      super.setChecked(paramBoolean);
      setTrackColor(paramBoolean);
      return;
    }
    super.setChecked(isChecked());
  }
  
  void setColor(Drawable paramDrawable, Integer paramInteger)
  {
    if (paramInteger == null)
    {
      paramDrawable.clearColorFilter();
      return;
    }
    paramDrawable.setColorFilter(paramInteger.intValue(), PorterDuff.Mode.MULTIPLY);
  }
  
  void setOn(boolean paramBoolean)
  {
    if (isChecked() != paramBoolean)
    {
      super.setChecked(paramBoolean);
      setTrackColor(paramBoolean);
    }
    mAllowChange = true;
  }
  
  public void setThumbColor(Integer paramInteger)
  {
    setColor(super.getThumbDrawable(), paramInteger);
  }
  
  public void setTrackColor(Integer paramInteger)
  {
    setColor(super.getTrackDrawable(), paramInteger);
  }
  
  public void setTrackColorForFalse(Integer paramInteger)
  {
    if (paramInteger == mTrackColorForFalse) {
      return;
    }
    mTrackColorForFalse = paramInteger;
    if (!isChecked()) {
      setTrackColor(mTrackColorForFalse);
    }
  }
  
  public void setTrackColorForTrue(Integer paramInteger)
  {
    if (paramInteger == mTrackColorForTrue) {
      return;
    }
    mTrackColorForTrue = paramInteger;
    if (isChecked()) {
      setTrackColor(mTrackColorForTrue);
    }
  }
}
