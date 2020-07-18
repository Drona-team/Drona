package androidx.transition;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOverlay;
import androidx.annotation.RequiresApi;

@RequiresApi(18)
class ViewOverlayApi18
  implements ViewOverlayImpl
{
  private final ViewOverlay mViewOverlay;
  
  ViewOverlayApi18(View paramView)
  {
    mViewOverlay = paramView.getOverlay();
  }
  
  public void addWaypoints(Drawable paramDrawable)
  {
    mViewOverlay.add(paramDrawable);
  }
  
  public void remove(Drawable paramDrawable)
  {
    mViewOverlay.remove(paramDrawable);
  }
}
