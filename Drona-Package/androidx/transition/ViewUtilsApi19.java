package androidx.transition;

import android.view.View;
import androidx.annotation.RequiresApi;

@RequiresApi(19)
class ViewUtilsApi19
  extends ViewUtilsBase
{
  private static boolean sTryHiddenTransitionAlpha;
  
  ViewUtilsApi19() {}
  
  public void clearNonTransitionAlpha(View paramView) {}
  
  public float getTransitionAlpha(View paramView)
  {
    if (sTryHiddenTransitionAlpha) {}
    try
    {
      float f = paramView.getTransitionAlpha();
      return f;
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      for (;;) {}
    }
    sTryHiddenTransitionAlpha = false;
    return paramView.getAlpha();
  }
  
  public void saveNonTransitionAlpha(View paramView) {}
  
  public void setTransitionAlpha(View paramView, float paramFloat)
  {
    if (sTryHiddenTransitionAlpha) {}
    try
    {
      paramView.setTransitionAlpha(paramFloat);
      return;
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      for (;;) {}
    }
    sTryHiddenTransitionAlpha = false;
    paramView.setAlpha(paramFloat);
  }
}
