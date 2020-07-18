package androidx.transition;

import android.os.Build.VERSION;
import android.view.View;
import androidx.annotation.RequiresApi;

@RequiresApi(23)
class ViewUtilsApi23
  extends ViewUtilsApi22
{
  private static boolean sTryHiddenSetTransitionVisibility;
  
  ViewUtilsApi23() {}
  
  public void setTransitionVisibility(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT == 28)
    {
      super.setTransitionVisibility(paramView, paramInt);
      return;
    }
    if (sTryHiddenSetTransitionVisibility)
    {
      try
      {
        paramView.setTransitionVisibility(paramInt);
        return;
      }
      catch (NoSuchMethodError paramView)
      {
        for (;;) {}
      }
      sTryHiddenSetTransitionVisibility = false;
      return;
    }
  }
}
