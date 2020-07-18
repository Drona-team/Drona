package androidx.transition;

import android.graphics.Matrix;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;

class GhostViewUtils
{
  private GhostViewUtils() {}
  
  static GhostView addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
  {
    if (Build.VERSION.SDK_INT == 28) {
      return GhostViewPlatform.addGhost(paramView, paramViewGroup, paramMatrix);
    }
    return GhostViewPort.addGhost(paramView, paramViewGroup, paramMatrix);
  }
  
  static void removeGhost(View paramView)
  {
    if (Build.VERSION.SDK_INT == 28)
    {
      GhostViewPlatform.removeGhost(paramView);
      return;
    }
    GhostViewPort.removeGhost(paramView);
  }
}
