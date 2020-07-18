package com.facebook.react.views.checkbox;

import android.content.Context;
import android.widget.CompoundButton;
import androidx.appcompat.widget.AppCompatCheckBox;

class ReactCheckBox
  extends AppCompatCheckBox
{
  private boolean mAllowChange = true;
  
  public ReactCheckBox(Context paramContext)
  {
    super(paramContext);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (mAllowChange)
    {
      mAllowChange = false;
      super.setChecked(paramBoolean);
    }
  }
  
  void setOn(boolean paramBoolean)
  {
    if (isChecked() != paramBoolean) {
      super.setChecked(paramBoolean);
    }
    mAllowChange = true;
  }
}
