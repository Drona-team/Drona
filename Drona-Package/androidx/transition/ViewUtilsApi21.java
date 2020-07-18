package androidx.transition;

import android.graphics.Matrix;
import android.view.View;
import androidx.annotation.RequiresApi;

@RequiresApi(21)
class ViewUtilsApi21
  extends ViewUtilsApi19
{
  private static boolean sTryHiddenSetAnimationMatrix;
  private static boolean sTryHiddenTransformMatrixToGlobal;
  private static boolean sTryHiddenTransformMatrixToLocal;
  
  ViewUtilsApi21() {}
  
  public void setAnimationMatrix(View paramView, Matrix paramMatrix)
  {
    if (sTryHiddenSetAnimationMatrix)
    {
      try
      {
        paramView.setAnimationMatrix(paramMatrix);
        return;
      }
      catch (NoSuchMethodError paramView)
      {
        for (;;) {}
      }
      sTryHiddenSetAnimationMatrix = false;
      return;
    }
  }
  
  public void transformMatrixToGlobal(View paramView, Matrix paramMatrix)
  {
    if (sTryHiddenTransformMatrixToGlobal)
    {
      try
      {
        paramView.transformMatrixToGlobal(paramMatrix);
        return;
      }
      catch (NoSuchMethodError paramView)
      {
        for (;;) {}
      }
      sTryHiddenTransformMatrixToGlobal = false;
      return;
    }
  }
  
  public void transformMatrixToLocal(View paramView, Matrix paramMatrix)
  {
    if (sTryHiddenTransformMatrixToLocal)
    {
      try
      {
        paramView.transformMatrixToLocal(paramMatrix);
        return;
      }
      catch (NoSuchMethodError paramView)
      {
        for (;;) {}
      }
      sTryHiddenTransformMatrixToLocal = false;
      return;
    }
  }
}
