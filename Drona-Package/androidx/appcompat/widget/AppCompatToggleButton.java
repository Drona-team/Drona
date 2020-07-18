package androidx.appcompat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class AppCompatToggleButton
  extends ToggleButton
{
  private final AppCompatTextHelper mTextHelper = new AppCompatTextHelper(this);
  
  public AppCompatToggleButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppCompatToggleButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842827);
  }
  
  public AppCompatToggleButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    mTextHelper.loadFromAttributes(paramAttributeSet, paramInt);
  }
}
