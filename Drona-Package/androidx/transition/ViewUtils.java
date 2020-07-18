package androidx.transition;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.Property;
import android.view.View;
import androidx.core.view.ViewCompat;

class ViewUtils
{
  static final Property<View, Rect> CLIP_BOUNDS = new Property(Rect.class, "clipBounds")
  {
    public void bind(View paramAnonymousView, Rect paramAnonymousRect)
    {
      ViewCompat.setClipBounds(paramAnonymousView, paramAnonymousRect);
    }
    
    public Rect getDirty(View paramAnonymousView)
    {
      return ViewCompat.getClipBounds(paramAnonymousView);
    }
  };
  private static final ViewUtilsBase IMPL;
  private static final String TAG = "ViewUtils";
  static final Property<View, Float> TRANSITION_ALPHA;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 29) {
      IMPL = new ViewUtilsApi29();
    } else if (Build.VERSION.SDK_INT >= 23) {
      IMPL = new ViewUtilsApi23();
    } else if (Build.VERSION.SDK_INT >= 22) {
      IMPL = new ViewUtilsApi22();
    } else if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new ViewUtilsApi21();
    } else if (Build.VERSION.SDK_INT >= 19) {
      IMPL = new ViewUtilsApi19();
    } else {
      IMPL = new ViewUtilsBase();
    }
    TRANSITION_ALPHA = new Property(Float.class, "translationAlpha")
    {
      public Float getData(View paramAnonymousView)
      {
        return Float.valueOf(ViewUtils.getTransitionAlpha(paramAnonymousView));
      }
      
      public void put(View paramAnonymousView, Float paramAnonymousFloat)
      {
        ViewUtils.setTransitionAlpha(paramAnonymousView, paramAnonymousFloat.floatValue());
      }
    };
  }
  
  private ViewUtils() {}
  
  static void clearNonTransitionAlpha(View paramView)
  {
    IMPL.clearNonTransitionAlpha(paramView);
  }
  
  static ViewOverlayImpl getOverlay(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      return new ViewOverlayApi18(paramView);
    }
    return ViewOverlayApi14.createFrom(paramView);
  }
  
  static float getTransitionAlpha(View paramView)
  {
    return IMPL.getTransitionAlpha(paramView);
  }
  
  static WindowIdImpl getWindowId(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      return new WindowIdApi18(paramView);
    }
    return new WindowIdApi14(paramView.getWindowToken());
  }
  
  static void saveNonTransitionAlpha(View paramView)
  {
    IMPL.saveNonTransitionAlpha(paramView);
  }
  
  static void setAnimationMatrix(View paramView, Matrix paramMatrix)
  {
    IMPL.setAnimationMatrix(paramView, paramMatrix);
  }
  
  static void setLeftTopRightBottom(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    IMPL.setLeftTopRightBottom(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  static void setTransitionAlpha(View paramView, float paramFloat)
  {
    IMPL.setTransitionAlpha(paramView, paramFloat);
  }
  
  static void setTransitionVisibility(View paramView, int paramInt)
  {
    IMPL.setTransitionVisibility(paramView, paramInt);
  }
  
  static void transformMatrixToGlobal(View paramView, Matrix paramMatrix)
  {
    IMPL.transformMatrixToGlobal(paramView, paramMatrix);
  }
  
  static void transformMatrixToLocal(View paramView, Matrix paramMatrix)
  {
    IMPL.transformMatrixToLocal(paramView, paramMatrix);
  }
}
