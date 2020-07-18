package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;
import androidx.annotation.Nullable;

public class PagerSnapHelper
  extends SnapHelper
{
  private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
  @Nullable
  private OrientationHelper mHorizontalHelper;
  @Nullable
  private OrientationHelper mVerticalHelper;
  
  public PagerSnapHelper() {}
  
  private int distanceToCenter(RecyclerView.LayoutManager paramLayoutManager, View paramView, OrientationHelper paramOrientationHelper)
  {
    return paramOrientationHelper.getDecoratedStart(paramView) + paramOrientationHelper.getDecoratedMeasurement(paramView) / 2 - (paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2);
  }
  
  private View findCenterView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    int n = paramLayoutManager.getChildCount();
    Object localObject = null;
    if (n == 0) {
      return null;
    }
    int i1 = paramOrientationHelper.getStartAfterPadding();
    int i2 = paramOrientationHelper.getTotalSpace() / 2;
    int j = Integer.MAX_VALUE;
    int i = 0;
    while (i < n)
    {
      View localView = paramLayoutManager.getChildAt(i);
      int m = Math.abs(paramOrientationHelper.getDecoratedStart(localView) + paramOrientationHelper.getDecoratedMeasurement(localView) / 2 - (i1 + i2));
      int k = j;
      if (m < j)
      {
        localObject = localView;
        k = m;
      }
      i += 1;
      j = k;
    }
    return localObject;
  }
  
  private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((mHorizontalHelper == null) || (mHorizontalHelper.mLayoutManager != paramLayoutManager)) {
      mHorizontalHelper = OrientationHelper.createHorizontalHelper(paramLayoutManager);
    }
    return mHorizontalHelper;
  }
  
  private OrientationHelper getOrientationHelper(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager.canScrollVertically()) {
      return getVerticalHelper(paramLayoutManager);
    }
    if (paramLayoutManager.canScrollHorizontally()) {
      return getHorizontalHelper(paramLayoutManager);
    }
    return null;
  }
  
  private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((mVerticalHelper == null) || (mVerticalHelper.mLayoutManager != paramLayoutManager)) {
      mVerticalHelper = OrientationHelper.createVerticalHelper(paramLayoutManager);
    }
    return mVerticalHelper;
  }
  
  private boolean isForwardFling(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    if (paramLayoutManager.canScrollHorizontally())
    {
      if (paramInt1 > 0) {
        return true;
      }
    }
    else if (paramInt2 > 0) {
      return true;
    }
    return false;
  }
  
  private boolean isReverseLayout(RecyclerView.LayoutManager paramLayoutManager)
  {
    int i = paramLayoutManager.getItemCount();
    if ((paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
    {
      paramLayoutManager = ((RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager).computeScrollVectorForPosition(i - 1);
      if (paramLayoutManager != null)
      {
        if (x < 0.0F) {
          break label53;
        }
        return y < 0.0F;
      }
    }
    return false;
    label53:
    return true;
  }
  
  public int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager paramLayoutManager, View paramView)
  {
    int[] arrayOfInt = new int[2];
    if (paramLayoutManager.canScrollHorizontally()) {
      arrayOfInt[0] = distanceToCenter(paramLayoutManager, paramView, getHorizontalHelper(paramLayoutManager));
    } else {
      arrayOfInt[0] = 0;
    }
    if (paramLayoutManager.canScrollVertically())
    {
      arrayOfInt[1] = distanceToCenter(paramLayoutManager, paramView, getVerticalHelper(paramLayoutManager));
      return arrayOfInt;
    }
    arrayOfInt[1] = 0;
    return arrayOfInt;
  }
  
  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
      return null;
    }
    new LinearSmoothScroller(mRecyclerView.getContext())
    {
      protected float calculateSpeedPerPixel(DisplayMetrics paramAnonymousDisplayMetrics)
      {
        return 100.0F / densityDpi;
      }
      
      protected int calculateTimeForScrolling(int paramAnonymousInt)
      {
        return Math.min(100, super.calculateTimeForScrolling(paramAnonymousInt));
      }
      
      protected void onTargetFound(View paramAnonymousView, RecyclerView.State paramAnonymousState, RecyclerView.SmoothScroller.Action paramAnonymousAction)
      {
        paramAnonymousView = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(), paramAnonymousView);
        int i = paramAnonymousView[0];
        int j = paramAnonymousView[1];
        int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
        if (k > 0) {
          paramAnonymousAction.update(i, j, k, mDecelerateInterpolator);
        }
      }
    };
  }
  
  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager.canScrollVertically()) {
      return findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
    }
    if (paramLayoutManager.canScrollHorizontally()) {
      return findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
    }
    return null;
  }
  
  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    int i2 = paramLayoutManager.getItemCount();
    if (i2 == 0) {
      return -1;
    }
    OrientationHelper localOrientationHelper = getOrientationHelper(paramLayoutManager);
    if (localOrientationHelper == null) {
      return -1;
    }
    int j = Integer.MIN_VALUE;
    int i3 = paramLayoutManager.getChildCount();
    int k = 0;
    Object localObject2 = null;
    Object localObject1 = null;
    int i1;
    for (int m = Integer.MAX_VALUE; k < i3; m = i1)
    {
      View localView = paramLayoutManager.getChildAt(k);
      Object localObject4;
      if (localView == null)
      {
        localObject4 = localObject2;
        i1 = m;
      }
      else
      {
        int n = distanceToCenter(paramLayoutManager, localView, localOrientationHelper);
        int i = j;
        Object localObject3 = localObject1;
        if (n <= 0)
        {
          i = j;
          localObject3 = localObject1;
          if (n > j)
          {
            localObject3 = localView;
            i = n;
          }
        }
        j = i;
        localObject4 = localObject2;
        localObject1 = localObject3;
        i1 = m;
        if (n >= 0)
        {
          j = i;
          localObject4 = localObject2;
          localObject1 = localObject3;
          i1 = m;
          if (n < m)
          {
            i1 = n;
            localObject1 = localObject3;
            localObject4 = localView;
            j = i;
          }
        }
      }
      k += 1;
      localObject2 = localObject4;
    }
    boolean bool = isForwardFling(paramLayoutManager, paramInt1, paramInt2);
    if ((bool) && (localObject2 != null)) {
      return paramLayoutManager.getPosition(localObject2);
    }
    if ((!bool) && (localObject1 != null)) {
      return paramLayoutManager.getPosition(localObject1);
    }
    if (!bool) {
      localObject1 = localObject2;
    }
    if (localObject1 == null) {
      return -1;
    }
    paramInt2 = paramLayoutManager.getPosition(localObject1);
    if (isReverseLayout(paramLayoutManager) == bool) {
      paramInt1 = -1;
    } else {
      paramInt1 = 1;
    }
    paramInt1 = paramInt2 + paramInt1;
    if (paramInt1 >= 0)
    {
      if (paramInt1 >= i2) {
        return -1;
      }
      return paramInt1;
    }
    return -1;
  }
}
