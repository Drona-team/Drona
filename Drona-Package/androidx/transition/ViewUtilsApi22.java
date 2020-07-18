package androidx.transition;

import android.view.View;
import androidx.annotation.RequiresApi;

@RequiresApi(22)
class ViewUtilsApi22
  extends ViewUtilsApi21
{
  private static boolean sTryHiddenSetLeftTopRightBottom;
  
  ViewUtilsApi22() {}
  
  public void setLeftTopRightBottom(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (sTryHiddenSetLeftTopRightBottom)
    {
      try
      {
        paramView.setLeftTopRightBottom(paramInt1, paramInt2, paramInt3, paramInt4);
        return;
      }
      catch (NoSuchMethodError paramView)
      {
        for (;;) {}
      }
      sTryHiddenSetLeftTopRightBottom = false;
      return;
    }
  }
}
