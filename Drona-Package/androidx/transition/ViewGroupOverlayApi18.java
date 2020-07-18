package androidx.transition;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewOverlay;
import androidx.annotation.RequiresApi;

@RequiresApi(18)
class ViewGroupOverlayApi18
  implements ViewGroupOverlayImpl
{
  private final ViewGroupOverlay mViewGroupOverlay;
  
  ViewGroupOverlayApi18(ViewGroup paramViewGroup)
  {
    mViewGroupOverlay = paramViewGroup.getOverlay();
  }
  
  public void addWaypoints(Drawable paramDrawable)
  {
    mViewGroupOverlay.add(paramDrawable);
  }
  
  public void remove(Drawable paramDrawable)
  {
    mViewGroupOverlay.remove(paramDrawable);
  }
  
  public void remove(View paramView)
  {
    mViewGroupOverlay.remove(paramView);
  }
  
  public void setProgram(View paramView)
  {
    mViewGroupOverlay.add(paramView);
  }
}
