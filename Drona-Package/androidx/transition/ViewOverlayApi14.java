package androidx.transition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.core.view.ViewCompat;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

class ViewOverlayApi14
  implements ViewOverlayImpl
{
  protected OverlayViewGroup mOverlayViewGroup = new OverlayViewGroup(paramContext, paramViewGroup, paramView, this);
  
  ViewOverlayApi14(Context paramContext, ViewGroup paramViewGroup, View paramView) {}
  
  static ViewOverlayApi14 createFrom(View paramView)
  {
    ViewGroup localViewGroup = getContentView(paramView);
    if (localViewGroup != null)
    {
      int j = localViewGroup.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = localViewGroup.getChildAt(i);
        if ((localView instanceof OverlayViewGroup)) {
          return mViewOverlay;
        }
        i += 1;
      }
      return new ViewGroupOverlayApi14(localViewGroup.getContext(), localViewGroup, paramView);
    }
    return null;
  }
  
  static ViewGroup getContentView(View paramView)
  {
    while (paramView != null)
    {
      if ((paramView.getId() == 16908290) && ((paramView instanceof ViewGroup))) {
        return (ViewGroup)paramView;
      }
      if ((paramView.getParent() instanceof ViewGroup)) {
        paramView = (ViewGroup)paramView.getParent();
      }
    }
    return null;
  }
  
  public void addWaypoints(Drawable paramDrawable)
  {
    mOverlayViewGroup.setDrawable(paramDrawable);
  }
  
  public void remove(Drawable paramDrawable)
  {
    mOverlayViewGroup.remove(paramDrawable);
  }
  
  static class OverlayViewGroup
    extends ViewGroup
  {
    static Method sInvalidateChildInParentFastMethod;
    private boolean mDisposed;
    ArrayList<Drawable> mDrawables = null;
    ViewGroup mHostView;
    View mRequestingView;
    ViewOverlayApi14 mViewOverlay;
    
    static
    {
      Object localObject = Integer.TYPE;
      Class localClass = Integer.TYPE;
      try
      {
        localObject = ViewGroup.class.getDeclaredMethod("invalidateChildInParentFast", new Class[] { localObject, localClass, Rect.class });
        sInvalidateChildInParentFastMethod = (Method)localObject;
        return;
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
    }
    
    OverlayViewGroup(Context paramContext, ViewGroup paramViewGroup, View paramView, ViewOverlayApi14 paramViewOverlayApi14)
    {
      super();
      mHostView = paramViewGroup;
      mRequestingView = paramView;
      setRight(paramViewGroup.getWidth());
      setBottom(paramViewGroup.getHeight());
      paramViewGroup.addView(this);
      mViewOverlay = paramViewOverlayApi14;
    }
    
    private void assertNotDisposed()
    {
      if (!mDisposed) {
        return;
      }
      throw new IllegalStateException("This overlay was disposed already. Please use a new one via ViewGroupUtils.getOverlay()");
    }
    
    private void disposeIfEmpty()
    {
      if ((getChildCount() == 0) && ((mDrawables == null) || (mDrawables.size() == 0)))
      {
        mDisposed = true;
        mHostView.removeView(this);
      }
    }
    
    private void getOffset(int[] paramArrayOfInt)
    {
      int[] arrayOfInt1 = new int[2];
      int[] arrayOfInt2 = new int[2];
      mHostView.getLocationOnScreen(arrayOfInt1);
      mRequestingView.getLocationOnScreen(arrayOfInt2);
      arrayOfInt2[0] -= arrayOfInt1[0];
      arrayOfInt2[1] -= arrayOfInt1[1];
    }
    
    protected void dispatchDraw(Canvas paramCanvas)
    {
      int[] arrayOfInt1 = new int[2];
      int[] arrayOfInt2 = new int[2];
      mHostView.getLocationOnScreen(arrayOfInt1);
      mRequestingView.getLocationOnScreen(arrayOfInt2);
      int j = 0;
      paramCanvas.translate(arrayOfInt2[0] - arrayOfInt1[0], arrayOfInt2[1] - arrayOfInt1[1]);
      paramCanvas.clipRect(new Rect(0, 0, mRequestingView.getWidth(), mRequestingView.getHeight()));
      super.dispatchDraw(paramCanvas);
      int i;
      if (mDrawables == null) {
        i = 0;
      } else {
        i = mDrawables.size();
      }
      while (j < i)
      {
        ((Drawable)mDrawables.get(j)).draw(paramCanvas);
        j += 1;
      }
    }
    
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public void init(View paramView)
    {
      assertNotDisposed();
      if ((paramView.getParent() instanceof ViewGroup))
      {
        ViewGroup localViewGroup = (ViewGroup)paramView.getParent();
        if ((localViewGroup != mHostView) && (localViewGroup.getParent() != null) && (ViewCompat.isAttachedToWindow(localViewGroup)))
        {
          int[] arrayOfInt1 = new int[2];
          int[] arrayOfInt2 = new int[2];
          localViewGroup.getLocationOnScreen(arrayOfInt1);
          mHostView.getLocationOnScreen(arrayOfInt2);
          ViewCompat.offsetLeftAndRight(paramView, arrayOfInt1[0] - arrayOfInt2[0]);
          ViewCompat.offsetTopAndBottom(paramView, arrayOfInt1[1] - arrayOfInt2[1]);
        }
        localViewGroup.removeView(paramView);
        if (paramView.getParent() != null) {
          localViewGroup.removeView(paramView);
        }
      }
      super.addView(paramView);
    }
    
    public ViewParent invalidateChildInParent(int[] paramArrayOfInt, Rect paramRect)
    {
      if (mHostView != null)
      {
        paramRect.offset(paramArrayOfInt[0], paramArrayOfInt[1]);
        if ((mHostView instanceof ViewGroup))
        {
          paramArrayOfInt[0] = 0;
          paramArrayOfInt[1] = 0;
          int[] arrayOfInt = new int[2];
          getOffset(arrayOfInt);
          paramRect.offset(arrayOfInt[0], arrayOfInt[1]);
          return super.invalidateChildInParent(paramArrayOfInt, paramRect);
        }
        invalidate(paramRect);
      }
      return null;
    }
    
    protected ViewParent invalidateChildInParentFast(int paramInt1, int paramInt2, Rect paramRect)
    {
      if (((mHostView instanceof ViewGroup)) && (sInvalidateChildInParentFastMethod != null))
      {
        Object localObject = new int[2];
        try
        {
          getOffset((int[])localObject);
          localObject = sInvalidateChildInParentFastMethod;
          ViewGroup localViewGroup = mHostView;
          ((Method)localObject).invoke(localViewGroup, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramRect });
        }
        catch (InvocationTargetException paramRect)
        {
          paramRect.printStackTrace();
        }
        catch (IllegalAccessException paramRect)
        {
          paramRect.printStackTrace();
        }
      }
      return null;
    }
    
    public void invalidateDrawable(Drawable paramDrawable)
    {
      invalidate(paramDrawable.getBounds());
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void remove(Drawable paramDrawable)
    {
      if (mDrawables != null)
      {
        mDrawables.remove(paramDrawable);
        invalidate(paramDrawable.getBounds());
        paramDrawable.setCallback(null);
        disposeIfEmpty();
      }
    }
    
    public void remove(View paramView)
    {
      super.removeView(paramView);
      disposeIfEmpty();
    }
    
    public void setDrawable(Drawable paramDrawable)
    {
      assertNotDisposed();
      if (mDrawables == null) {
        mDrawables = new ArrayList();
      }
      if (!mDrawables.contains(paramDrawable))
      {
        mDrawables.add(paramDrawable);
        invalidate(paramDrawable.getBounds());
        paramDrawable.setCallback(this);
      }
    }
    
    protected boolean verifyDrawable(Drawable paramDrawable)
    {
      return (super.verifyDrawable(paramDrawable)) || ((mDrawables != null) && (mDrawables.contains(paramDrawable)));
    }
  }
}
