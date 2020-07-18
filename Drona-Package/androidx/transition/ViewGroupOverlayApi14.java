package androidx.transition;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

class ViewGroupOverlayApi14
  extends ViewOverlayApi14
  implements ViewGroupOverlayImpl
{
  ViewGroupOverlayApi14(Context paramContext, ViewGroup paramViewGroup, View paramView)
  {
    super(paramContext, paramViewGroup, paramView);
  }
  
  static ViewGroupOverlayApi14 createFrom(ViewGroup paramViewGroup)
  {
    return (ViewGroupOverlayApi14)ViewOverlayApi14.createFrom(paramViewGroup);
  }
  
  public void remove(View paramView)
  {
    mOverlayViewGroup.remove(paramView);
  }
  
  public void setProgram(View paramView)
  {
    mOverlayViewGroup.init(paramView);
  }
}
