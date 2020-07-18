package androidx.viewpager2.widget;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.recyclerview.widget.RecyclerView;

final class FakeDrag
{
  private int mActualDraggedDistance;
  private long mFakeDragBeginTime;
  private int mMaximumVelocity;
  private final RecyclerView mRecyclerView;
  private float mRequestedDragDistance;
  private final ScrollEventAdapter mScrollEventAdapter;
  private VelocityTracker mVelocityTracker;
  private final ViewPager2 mViewPager;
  
  FakeDrag(ViewPager2 paramViewPager2, ScrollEventAdapter paramScrollEventAdapter, RecyclerView paramRecyclerView)
  {
    mViewPager = paramViewPager2;
    mScrollEventAdapter = paramScrollEventAdapter;
    mRecyclerView = paramRecyclerView;
  }
  
  private void addFakeMotionEvent(long paramLong, int paramInt, float paramFloat1, float paramFloat2)
  {
    MotionEvent localMotionEvent = MotionEvent.obtain(mFakeDragBeginTime, paramLong, paramInt, paramFloat1, paramFloat2, 0);
    mVelocityTracker.addMovement(localMotionEvent);
    localMotionEvent.recycle();
  }
  
  private void beginFakeVelocityTracker()
  {
    if (mVelocityTracker == null)
    {
      mVelocityTracker = VelocityTracker.obtain();
      mMaximumVelocity = ViewConfiguration.get(mViewPager.getContext()).getScaledMaximumFlingVelocity();
      return;
    }
    mVelocityTracker.clear();
  }
  
  boolean beginFakeDrag()
  {
    if (mScrollEventAdapter.isDragging()) {
      return false;
    }
    mActualDraggedDistance = 0;
    mRequestedDragDistance = 0.0F;
    mFakeDragBeginTime = SystemClock.uptimeMillis();
    beginFakeVelocityTracker();
    mScrollEventAdapter.notifyBeginFakeDrag();
    if (!mScrollEventAdapter.isIdle()) {
      mRecyclerView.stopScroll();
    }
    addFakeMotionEvent(mFakeDragBeginTime, 0, 0.0F, 0.0F);
    return true;
  }
  
  boolean endFakeDrag()
  {
    if (!mScrollEventAdapter.isFakeDragging()) {
      return false;
    }
    mScrollEventAdapter.notifyEndFakeDrag();
    VelocityTracker localVelocityTracker = mVelocityTracker;
    localVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
    int i = (int)localVelocityTracker.getXVelocity();
    int j = (int)localVelocityTracker.getYVelocity();
    if (!mRecyclerView.fling(i, j)) {
      mViewPager.snapToPage();
    }
    return true;
  }
  
  boolean fakeDragBy(float paramFloat)
  {
    if (!mScrollEventAdapter.isFakeDragging()) {
      return false;
    }
    mRequestedDragDistance -= paramFloat;
    int k = Math.round(mRequestedDragDistance - mActualDraggedDistance);
    int i = k;
    mActualDraggedDistance += k;
    long l = SystemClock.uptimeMillis();
    int j;
    if (mViewPager.getOrientation() == 0) {
      j = 1;
    } else {
      j = 0;
    }
    if (j == 0) {
      k = 0;
    }
    if (j != 0) {
      i = 0;
    }
    if (j != 0) {
      paramFloat = mRequestedDragDistance;
    } else {
      paramFloat = 0.0F;
    }
    float f;
    if (j != 0) {
      f = 0.0F;
    } else {
      f = mRequestedDragDistance;
    }
    mRecyclerView.scrollBy(k, i);
    addFakeMotionEvent(l, 2, paramFloat, f);
    return true;
  }
  
  boolean isFakeDragging()
  {
    return mScrollEventAdapter.isFakeDragging();
  }
}
