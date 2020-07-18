package client.testing.delay;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import client.testing.R.styleable;

public class MaskedColorView
  extends ImageView
{
  private ColorStateList colorStateList = null;
  
  public MaskedColorView(Context paramContext)
  {
    super(paramContext, null);
  }
  
  public MaskedColorView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 16842866);
    configure(paramAttributeSet);
  }
  
  public MaskedColorView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    configure(paramAttributeSet);
  }
  
  private void configure(AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet != null)
    {
      paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.MaskedColorView);
      try
      {
        ColorStateList localColorStateList = paramAttributeSet.getColorStateList(R.styleable.MaskedColorView_mainColor);
        if (localColorStateList != null) {
          colorStateList = localColorStateList;
        }
        paramAttributeSet.recycle();
        return;
      }
      catch (Throwable localThrowable)
      {
        paramAttributeSet.recycle();
        throw localThrowable;
      }
    }
  }
  
  private int getCurrentColor(int[] paramArrayOfInt)
  {
    if (colorStateList == null) {
      return -65281;
    }
    return colorStateList.getColorForState(paramArrayOfInt, colorStateList.getDefaultColor());
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    setColorFilter(getCurrentColor(getDrawableState()), PorterDuff.Mode.SRC_ATOP);
    if (Build.VERSION.SDK_INT >= 11) {
      jumpDrawablesToCurrentState();
    }
  }
  
  protected String getDebugState()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("====\ncsl is ");
    String str;
    if (colorStateList != null) {
      str = "NOT";
    } else {
      str = "";
    }
    localStringBuilder.append(str);
    localStringBuilder.append(" null");
    return localStringBuilder.toString();
  }
  
  public void setColorStateList(ColorStateList paramColorStateList)
  {
    colorStateList = paramColorStateList;
  }
}
