package androidx.core.view;

import android.view.View;
import android.view.ViewGroup;

public class NestedScrollingParentHelper
{
  private int mNestedScrollAxesNonTouch;
  private int mNestedScrollAxesTouch;
  
  public NestedScrollingParentHelper(ViewGroup paramViewGroup) {}
  
  public int getNestedScrollAxes()
  {
    return mNestedScrollAxesTouch | mNestedScrollAxesNonTouch;
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    onNestedScrollAccepted(paramView1, paramView2, paramInt, 0);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1)
    {
      mNestedScrollAxesNonTouch = paramInt1;
      return;
    }
    mNestedScrollAxesTouch = paramInt1;
  }
  
  public void onStopNestedScroll(View paramView)
  {
    onStopNestedScroll(paramView, 0);
  }
  
  public void onStopNestedScroll(View paramView, int paramInt)
  {
    if (paramInt == 1)
    {
      mNestedScrollAxesNonTouch = 0;
      return;
    }
    mNestedScrollAxesTouch = 0;
  }
}
