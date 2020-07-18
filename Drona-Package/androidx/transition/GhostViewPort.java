package androidx.transition;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

@SuppressLint({"ViewConstructor"})
class GhostViewPort
  extends ViewGroup
  implements GhostView
{
  @Nullable
  private Matrix mMatrix;
  private final ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
  {
    public boolean onPreDraw()
    {
      ViewCompat.postInvalidateOnAnimation(GhostViewPort.this);
      if ((mStartParent != null) && (mStartView != null))
      {
        mStartParent.endViewTransition(mStartView);
        ViewCompat.postInvalidateOnAnimation(mStartParent);
        mStartParent = null;
        mStartView = null;
      }
      return true;
    }
  };
  int mReferences;
  ViewGroup mStartParent;
  View mStartView;
  final View mView;
  
  GhostViewPort(View paramView)
  {
    super(paramView.getContext());
    mView = paramView;
    setWillNotDraw(false);
    setLayerType(2, null);
  }
  
  static GhostViewPort addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
  {
    if ((paramView.getParent() instanceof ViewGroup))
    {
      GhostViewHolder localGhostViewHolder2 = GhostViewHolder.getHolder(paramViewGroup);
      GhostViewHolder localGhostViewHolder1 = localGhostViewHolder2;
      GhostViewPort localGhostViewPort2 = getGhostView(paramView);
      GhostViewPort localGhostViewPort1 = localGhostViewPort2;
      int j = 0;
      Object localObject = localGhostViewPort1;
      int i = j;
      if (localGhostViewPort2 != null)
      {
        GhostViewHolder localGhostViewHolder3 = (GhostViewHolder)localGhostViewPort2.getParent();
        localObject = localGhostViewPort1;
        i = j;
        if (localGhostViewHolder3 != localGhostViewHolder2)
        {
          i = mReferences;
          localGhostViewHolder3.removeView(localGhostViewPort2);
          localObject = null;
        }
      }
      if (localObject == null)
      {
        localObject = paramMatrix;
        if (paramMatrix == null)
        {
          localObject = new Matrix();
          calculateMatrix(paramView, paramViewGroup, (Matrix)localObject);
        }
        paramView = new GhostViewPort(paramView);
        paramView.setMatrix((Matrix)localObject);
        if (localGhostViewHolder2 == null) {
          localGhostViewHolder1 = new GhostViewHolder(paramViewGroup);
        } else {
          localGhostViewHolder2.popToOverlayTop();
        }
        copySize(paramViewGroup, localGhostViewHolder1);
        copySize(paramViewGroup, paramView);
        localGhostViewHolder1.addGhostView(paramView);
        mReferences = i;
      }
      else
      {
        paramView = (View)localObject;
        if (paramMatrix != null)
        {
          ((GhostViewPort)localObject).setMatrix(paramMatrix);
          paramView = (View)localObject;
        }
      }
      mReferences += 1;
      return paramView;
    }
    throw new IllegalArgumentException("Ghosted views must be parented by a ViewGroup");
  }
  
  static void calculateMatrix(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
  {
    paramView = (ViewGroup)paramView.getParent();
    paramMatrix.reset();
    ViewUtils.transformMatrixToGlobal(paramView, paramMatrix);
    paramMatrix.preTranslate(-paramView.getScrollX(), -paramView.getScrollY());
    ViewUtils.transformMatrixToLocal(paramViewGroup, paramMatrix);
  }
  
  static void copySize(View paramView1, View paramView2)
  {
    ViewUtils.setLeftTopRightBottom(paramView2, paramView2.getLeft(), paramView2.getTop(), paramView2.getLeft() + paramView1.getWidth(), paramView2.getTop() + paramView1.getHeight());
  }
  
  static GhostViewPort getGhostView(View paramView)
  {
    return (GhostViewPort)paramView.getTag(R.id.ghost_view);
  }
  
  static void removeGhost(View paramView)
  {
    paramView = getGhostView(paramView);
    if (paramView != null)
    {
      mReferences -= 1;
      if (mReferences <= 0) {
        ((GhostViewHolder)paramView.getParent()).removeView(paramView);
      }
    }
  }
  
  static void setGhostView(View paramView, GhostViewPort paramGhostViewPort)
  {
    paramView.setTag(R.id.ghost_view, paramGhostViewPort);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    setGhostView(mView, this);
    mView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
    ViewUtils.setTransitionVisibility(mView, 4);
    if (mView.getParent() != null) {
      ((View)mView.getParent()).invalidate();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    mView.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
    ViewUtils.setTransitionVisibility(mView, 0);
    setGhostView(mView, null);
    if (mView.getParent() != null) {
      ((View)mView.getParent()).invalidate();
    }
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    CanvasUtils.enableZ(paramCanvas, true);
    paramCanvas.setMatrix(mMatrix);
    ViewUtils.setTransitionVisibility(mView, 0);
    mView.invalidate();
    ViewUtils.setTransitionVisibility(mView, 4);
    drawChild(paramCanvas, mView, getDrawingTime());
    CanvasUtils.enableZ(paramCanvas, false);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void reserveEndViewTransition(ViewGroup paramViewGroup, View paramView)
  {
    mStartParent = paramViewGroup;
    mStartView = paramView;
  }
  
  void setMatrix(Matrix paramMatrix)
  {
    mMatrix = paramMatrix;
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (getGhostView(mView) == this)
    {
      if (paramInt == 0) {
        paramInt = 4;
      } else {
        paramInt = 0;
      }
      ViewUtils.setTransitionVisibility(mView, paramInt);
    }
  }
}
