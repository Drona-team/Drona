package androidx.recyclerview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityRecord;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager
  extends RecyclerView.LayoutManager
  implements RecyclerView.SmoothScroller.ScrollVectorProvider
{
  static final boolean DEBUG = false;
  @Deprecated
  public static final int GAP_HANDLING_LAZY = 1;
  public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
  public static final int GAP_HANDLING_NONE = 0;
  public static final int HORIZONTAL = 0;
  static final int INVALID_OFFSET = Integer.MIN_VALUE;
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  private static final String TAG = "StaggeredGridLManager";
  public static final int VERTICAL = 1;
  private final AnchorInfo mAnchorInfo = new AnchorInfo();
  private final Runnable mCheckForGapsRunnable = new Runnable()
  {
    public void run()
    {
      checkForGaps();
    }
  };
  private int mFullSizeSpec;
  private int mGapStrategy = 2;
  private boolean mLaidOutInvalidFullSpan = false;
  private boolean mLastLayoutFromEnd;
  private boolean mLastLayoutRTL;
  @NonNull
  private final LayoutState mLayoutState;
  LazySpanLookup mLazySpanLookup = new LazySpanLookup();
  private int mOrientation;
  private SavedState mPendingSavedState;
  int mPendingScrollPosition = -1;
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  private int[] mPrefetchDistances;
  @NonNull
  OrientationHelper mPrimaryOrientation;
  private BitSet mRemainingSpans;
  boolean mReverseLayout = false;
  @NonNull
  OrientationHelper mSecondaryOrientation;
  boolean mShouldReverseLayout = false;
  private int mSizePerSpan;
  private boolean mSmoothScrollbarEnabled = true;
  private int mSpanCount = -1;
  Span[] mSpans;
  private final Rect mTmpRect = new Rect();
  
  public StaggeredGridLayoutManager(int paramInt1, int paramInt2)
  {
    mOrientation = paramInt2;
    setSpanCount(paramInt1);
    mLayoutState = new LayoutState();
    createOrientationHelpers();
  }
  
  public StaggeredGridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    paramContext = RecyclerView.LayoutManager.getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOrientation(orientation);
    setSpanCount(spanCount);
    setReverseLayout(reverseLayout);
    mLayoutState = new LayoutState();
    createOrientationHelpers();
  }
  
  private void appendViewToAllSpans(View paramView)
  {
    int i = mSpanCount - 1;
    while (i >= 0)
    {
      mSpans[i].appendToSpan(paramView);
      i -= 1;
    }
  }
  
  private void applyPendingSavedState(AnchorInfo paramAnchorInfo)
  {
    if (mPendingSavedState.mSpanOffsetsSize > 0)
    {
      if (mPendingSavedState.mSpanOffsetsSize == mSpanCount)
      {
        int j = 0;
        while (j < mSpanCount)
        {
          mSpans[j].clear();
          int k = mPendingSavedState.mSpanOffsets[j];
          int i = k;
          if (k != Integer.MIN_VALUE) {
            if (mPendingSavedState.mAnchorLayoutFromEnd) {
              i = k + mPrimaryOrientation.getEndAfterPadding();
            } else {
              i = k + mPrimaryOrientation.getStartAfterPadding();
            }
          }
          mSpans[j].setLine(i);
          j += 1;
        }
      }
      mPendingSavedState.invalidateSpanInfo();
      mPendingSavedState.mAnchorPosition = mPendingSavedState.mVisibleAnchorPosition;
    }
    mLastLayoutRTL = mPendingSavedState.mLastLayoutRTL;
    setReverseLayout(mPendingSavedState.mReverseLayout);
    resolveShouldLayoutReverse();
    if (mPendingSavedState.mAnchorPosition != -1)
    {
      mPendingScrollPosition = mPendingSavedState.mAnchorPosition;
      mLayoutFromEnd = mPendingSavedState.mAnchorLayoutFromEnd;
    }
    else
    {
      mLayoutFromEnd = mShouldReverseLayout;
    }
    if (mPendingSavedState.mSpanLookupSize > 1)
    {
      mLazySpanLookup.mData = mPendingSavedState.mSpanLookup;
      mLazySpanLookup.mFullSpanItems = mPendingSavedState.mFullSpanItems;
    }
  }
  
  private void attachViewToSpans(View paramView, LayoutParams paramLayoutParams, LayoutState paramLayoutState)
  {
    if (mLayoutDirection == 1)
    {
      if (mFullSpan)
      {
        appendViewToAllSpans(paramView);
        return;
      }
      mSpan.appendToSpan(paramView);
      return;
    }
    if (mFullSpan)
    {
      prependViewToAllSpans(paramView);
      return;
    }
    mSpan.prependToSpan(paramView);
  }
  
  private int calculateScrollDirectionForPosition(int paramInt)
  {
    if (getChildCount() == 0)
    {
      if (mShouldReverseLayout) {
        return 1;
      }
    }
    else
    {
      int i;
      if (paramInt < getFirstChildPosition()) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != mShouldReverseLayout) {
        return -1;
      }
      return 1;
    }
    return -1;
  }
  
  private boolean checkSpanForGap(Span paramSpan)
  {
    if (mShouldReverseLayout)
    {
      if (paramSpan.getEndLine() < mPrimaryOrientation.getEndAfterPadding()) {
        return getLayoutParamsmViews.get(mViews.size() - 1)).mFullSpan ^ true;
      }
    }
    else if (paramSpan.getStartLine() > mPrimaryOrientation.getStartAfterPadding()) {
      return getLayoutParamsmViews.get(0)).mFullSpan ^ true;
    }
    return false;
  }
  
  private int computeScrollExtent(RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    return ScrollbarHelper.computeScrollExtent(paramState, mPrimaryOrientation, findFirstVisibleItemClosestToStart(mSmoothScrollbarEnabled ^ true), findFirstVisibleItemClosestToEnd(mSmoothScrollbarEnabled ^ true), this, mSmoothScrollbarEnabled);
  }
  
  private int computeScrollOffset(RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    return ScrollbarHelper.computeScrollOffset(paramState, mPrimaryOrientation, findFirstVisibleItemClosestToStart(mSmoothScrollbarEnabled ^ true), findFirstVisibleItemClosestToEnd(mSmoothScrollbarEnabled ^ true), this, mSmoothScrollbarEnabled, mShouldReverseLayout);
  }
  
  private int computeScrollRange(RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    return ScrollbarHelper.computeScrollRange(paramState, mPrimaryOrientation, findFirstVisibleItemClosestToStart(mSmoothScrollbarEnabled ^ true), findFirstVisibleItemClosestToEnd(mSmoothScrollbarEnabled ^ true), this, mSmoothScrollbarEnabled);
  }
  
  private int convertFocusDirectionToLayoutDirection(int paramInt)
  {
    if (paramInt != 17)
    {
      if (paramInt != 33)
      {
        if (paramInt != 66)
        {
          if (paramInt != 130)
          {
            switch (paramInt)
            {
            default: 
              return Integer.MIN_VALUE;
            case 2: 
              if (mOrientation == 1) {
                return 1;
              }
              if (isLayoutRTL()) {
                return -1;
              }
              return 1;
            }
            if (mOrientation == 1) {
              return -1;
            }
            if (isLayoutRTL()) {
              return 1;
            }
            return -1;
          }
          if (mOrientation == 1) {
            return 1;
          }
        }
        else if (mOrientation == 0)
        {
          return 1;
        }
      }
      else
      {
        if (mOrientation == 1) {
          return -1;
        }
        return Integer.MIN_VALUE;
      }
    }
    else
    {
      if (mOrientation == 0) {
        return -1;
      }
      return Integer.MIN_VALUE;
    }
    return Integer.MIN_VALUE;
  }
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromEnd(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    mGapPerSpan = new int[mSpanCount];
    int i = 0;
    while (i < mSpanCount)
    {
      mGapPerSpan[i] = (paramInt - mSpans[i].getEndLine(paramInt));
      i += 1;
    }
    return localFullSpanItem;
  }
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromStart(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    mGapPerSpan = new int[mSpanCount];
    int i = 0;
    while (i < mSpanCount)
    {
      mGapPerSpan[i] = (mSpans[i].getStartLine(paramInt) - paramInt);
      i += 1;
    }
    return localFullSpanItem;
  }
  
  private void createOrientationHelpers()
  {
    mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, mOrientation);
    mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - mOrientation);
  }
  
  private int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState)
  {
    mRemainingSpans.set(0, mSpanCount, true);
    int i;
    if (mLayoutState.mInfinite)
    {
      if (mLayoutDirection == 1) {
        i = Integer.MAX_VALUE;
      } else {
        i = Integer.MIN_VALUE;
      }
    }
    else
    {
      if (mLayoutDirection == 1) {}
      for (i = mEndLine + mAvailable;; i = mStartLine - mAvailable) {
        break;
      }
    }
    updateAllRemainingSpans(mLayoutDirection, i);
    if (mShouldReverseLayout) {}
    for (int k = mPrimaryOrientation.getEndAfterPadding();; k = mPrimaryOrientation.getStartAfterPadding()) {
      break;
    }
    for (int j = 0; (paramLayoutState.hasMore(paramState)) && ((mLayoutState.mInfinite) || (!mRemainingSpans.isEmpty())); j = 1)
    {
      View localView = paramLayoutState.next(paramRecycler);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int i3 = localLayoutParams.getViewLayoutPosition();
      j = mLazySpanLookup.getSpan(i3);
      int i1;
      if (j == -1) {
        i1 = 1;
      } else {
        i1 = 0;
      }
      Span localSpan;
      if (i1 != 0)
      {
        if (mFullSpan) {
          localSpan = mSpans[0];
        } else {
          localSpan = getNextSpan(paramLayoutState);
        }
        mLazySpanLookup.setSpan(i3, localSpan);
      }
      else
      {
        localSpan = mSpans[j];
      }
      mSpan = localSpan;
      if (mLayoutDirection == 1) {
        addView(localView);
      } else {
        addView(localView, 0);
      }
      measureChildWithDecorationsAndMargin(localView, localLayoutParams, false);
      int m;
      StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem;
      int n;
      int i2;
      if (mLayoutDirection == 1)
      {
        if (mFullSpan) {
          j = getMaxEnd(k);
        } else {
          j = localSpan.getEndLine(k);
        }
        m = mPrimaryOrientation.getDecoratedMeasurement(localView) + j;
        if ((i1 != 0) && (mFullSpan))
        {
          localFullSpanItem = createFullSpanItemFromEnd(j);
          mGapDir = -1;
          mPosition = i3;
          mLazySpanLookup.addFullSpanItem(localFullSpanItem);
        }
        n = j;
      }
      else
      {
        if (mFullSpan) {
          j = getMinStart(k);
        } else {
          j = localSpan.getStartLine(k);
        }
        i2 = j - mPrimaryOrientation.getDecoratedMeasurement(localView);
        m = j;
        n = i2;
        if (i1 != 0)
        {
          m = j;
          n = i2;
          if (mFullSpan)
          {
            localFullSpanItem = createFullSpanItemFromStart(j);
            mGapDir = 1;
            mPosition = i3;
            mLazySpanLookup.addFullSpanItem(localFullSpanItem);
            n = i2;
            m = j;
          }
        }
      }
      if ((mFullSpan) && (mItemDirection == -1)) {
        if (i1 != 0)
        {
          mLaidOutInvalidFullSpan = true;
        }
        else
        {
          if (mLayoutDirection == 1) {}
          for (boolean bool = areAllEndsEqual();; bool = areAllStartsEqual()) {
            break;
          }
          if ((bool ^ true))
          {
            localFullSpanItem = mLazySpanLookup.getFullSpanItem(i3);
            if (localFullSpanItem != null) {
              mHasUnwantedGapAfter = true;
            }
            mLaidOutInvalidFullSpan = true;
          }
        }
      }
      attachViewToSpans(localView, localLayoutParams, paramLayoutState);
      if ((isLayoutRTL()) && (mOrientation == 1))
      {
        if (mFullSpan) {
          j = mSecondaryOrientation.getEndAfterPadding();
        } else {
          j = mSecondaryOrientation.getEndAfterPadding() - (mSpanCount - 1 - mIndex) * mSizePerSpan;
        }
        i1 = j - mSecondaryOrientation.getDecoratedMeasurement(localView);
      }
      else
      {
        if (mFullSpan) {
          j = mSecondaryOrientation.getStartAfterPadding();
        } else {
          j = mIndex * mSizePerSpan + mSecondaryOrientation.getStartAfterPadding();
        }
        i2 = mSecondaryOrientation.getDecoratedMeasurement(localView);
        i1 = j;
        j = i2 + j;
      }
      if (mOrientation == 1) {
        layoutDecoratedWithMargins(localView, i1, n, j, m);
      } else {
        layoutDecoratedWithMargins(localView, n, i1, m, j);
      }
      if (mFullSpan) {
        updateAllRemainingSpans(mLayoutState.mLayoutDirection, i);
      } else {
        updateRemainingSpans(localSpan, mLayoutState.mLayoutDirection, i);
      }
      recycle(paramRecycler, mLayoutState);
      if ((mLayoutState.mStopInFocusable) && (localView.hasFocusable())) {
        if (mFullSpan) {
          mRemainingSpans.clear();
        } else {
          mRemainingSpans.set(mIndex, false);
        }
      }
    }
    if (j == 0) {
      recycle(paramRecycler, mLayoutState);
    }
    if (mLayoutState.mLayoutDirection == -1)
    {
      i = getMinStart(mPrimaryOrientation.getStartAfterPadding());
      i = mPrimaryOrientation.getStartAfterPadding() - i;
    }
    else
    {
      i = getMaxEnd(mPrimaryOrientation.getEndAfterPadding()) - mPrimaryOrientation.getEndAfterPadding();
    }
    if (i > 0) {
      return Math.min(mAvailable, i);
    }
    return 0;
  }
  
  private int findFirstReferenceChildPosition(int paramInt)
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      int k = getPosition(getChildAt(i));
      if ((k >= 0) && (k < paramInt)) {
        return k;
      }
      i += 1;
    }
    return 0;
  }
  
  private int findLastReferenceChildPosition(int paramInt)
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      int j = getPosition(getChildAt(i));
      if ((j >= 0) && (j < paramInt)) {
        return j;
      }
      i -= 1;
    }
    return 0;
  }
  
  private void fixEndGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMaxEnd(Integer.MIN_VALUE);
    if (i == Integer.MIN_VALUE) {
      return;
    }
    i = mPrimaryOrientation.getEndAfterPadding() - i;
    if (i > 0)
    {
      i -= -scrollBy(-i, paramRecycler, paramState);
      if ((paramBoolean) && (i > 0)) {
        mPrimaryOrientation.offsetChildren(i);
      }
    }
  }
  
  private void fixStartGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMinStart(Integer.MAX_VALUE);
    if (i == Integer.MAX_VALUE) {
      return;
    }
    i -= mPrimaryOrientation.getStartAfterPadding();
    if (i > 0)
    {
      i -= scrollBy(i, paramRecycler, paramState);
      if ((paramBoolean) && (i > 0)) {
        mPrimaryOrientation.offsetChildren(-i);
      }
    }
  }
  
  private int getMaxEnd(int paramInt)
  {
    int j = mSpans[0].getEndLine(paramInt);
    int i = 1;
    while (i < mSpanCount)
    {
      int m = mSpans[i].getEndLine(paramInt);
      int k = j;
      if (m > j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private int getMaxStart(int paramInt)
  {
    int j = mSpans[0].getStartLine(paramInt);
    int i = 1;
    while (i < mSpanCount)
    {
      int m = mSpans[i].getStartLine(paramInt);
      int k = j;
      if (m > j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private int getMinEnd(int paramInt)
  {
    int j = mSpans[0].getEndLine(paramInt);
    int i = 1;
    while (i < mSpanCount)
    {
      int m = mSpans[i].getEndLine(paramInt);
      int k = j;
      if (m < j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private int getMinStart(int paramInt)
  {
    int j = mSpans[0].getStartLine(paramInt);
    int i = 1;
    while (i < mSpanCount)
    {
      int m = mSpans[i].getStartLine(paramInt);
      int k = j;
      if (m < j) {
        k = m;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private Span getNextSpan(LayoutState paramLayoutState)
  {
    boolean bool = preferLastSpan(mLayoutDirection);
    int j = -1;
    int i;
    int k;
    if (bool)
    {
      i = mSpanCount - 1;
      k = -1;
    }
    else
    {
      i = 0;
      j = mSpanCount;
      k = 1;
    }
    int m = mLayoutDirection;
    Span localSpan = null;
    paramLayoutState = null;
    int i1;
    int n;
    if (m == 1)
    {
      m = Integer.MAX_VALUE;
      i2 = mPrimaryOrientation.getStartAfterPadding();
      while (i != j)
      {
        localSpan = mSpans[i];
        i1 = localSpan.getEndLine(i2);
        n = m;
        if (i1 < m)
        {
          paramLayoutState = localSpan;
          n = i1;
        }
        i += k;
        m = n;
      }
      return paramLayoutState;
    }
    m = Integer.MIN_VALUE;
    int i2 = mPrimaryOrientation.getEndAfterPadding();
    paramLayoutState = localSpan;
    while (i != j)
    {
      localSpan = mSpans[i];
      i1 = localSpan.getStartLine(i2);
      n = m;
      if (i1 > m)
      {
        paramLayoutState = localSpan;
        n = i1;
      }
      i += k;
      m = n;
    }
    return paramLayoutState;
  }
  
  private void handleUpdate(int paramInt1, int paramInt2, int paramInt3)
  {
    int j;
    if (mShouldReverseLayout) {
      j = getLastChildPosition();
    } else {
      j = getFirstChildPosition();
    }
    if (paramInt3 == 8)
    {
      if (paramInt1 < paramInt2)
      {
        i = paramInt2 + 1;
      }
      else
      {
        k = paramInt1 + 1;
        i = paramInt2;
        break label64;
      }
    }
    else {
      i = paramInt1 + paramInt2;
    }
    int k = i;
    int i = paramInt1;
    label64:
    mLazySpanLookup.invalidateAfter(i);
    if (paramInt3 != 8)
    {
      switch (paramInt3)
      {
      default: 
        break;
      case 2: 
        mLazySpanLookup.offsetForRemoval(paramInt1, paramInt2);
        break;
      case 1: 
        mLazySpanLookup.offsetForAddition(paramInt1, paramInt2);
        break;
      }
    }
    else
    {
      mLazySpanLookup.offsetForRemoval(paramInt1, 1);
      mLazySpanLookup.offsetForAddition(paramInt2, 1);
    }
    if (k <= j) {
      return;
    }
    if (mShouldReverseLayout) {
      paramInt1 = getFirstChildPosition();
    } else {
      paramInt1 = getLastChildPosition();
    }
    if (i <= paramInt1) {
      requestLayout();
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    calculateItemDecorationsForChild(paramView, mTmpRect);
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    paramInt1 = updateSpecWithExtra(paramInt1, leftMargin + mTmpRect.left, rightMargin + mTmpRect.right);
    paramInt2 = updateSpecWithExtra(paramInt2, topMargin + mTmpRect.top, bottomMargin + mTmpRect.bottom);
    if (paramBoolean) {
      paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);
    } else {
      paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);
    }
    if (paramBoolean) {
      paramView.measure(paramInt1, paramInt2);
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (mFullSpan)
    {
      if (mOrientation == 1)
      {
        measureChildWithDecorationsAndMargin(paramView, mFullSizeSpec, RecyclerView.LayoutManager.getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), height, true), paramBoolean);
        return;
      }
      measureChildWithDecorationsAndMargin(paramView, RecyclerView.LayoutManager.getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), width, true), mFullSizeSpec, paramBoolean);
      return;
    }
    if (mOrientation == 1)
    {
      measureChildWithDecorationsAndMargin(paramView, RecyclerView.LayoutManager.getChildMeasureSpec(mSizePerSpan, getWidthMode(), 0, width, false), RecyclerView.LayoutManager.getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), height, true), paramBoolean);
      return;
    }
    measureChildWithDecorationsAndMargin(paramView, RecyclerView.LayoutManager.getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), width, true), RecyclerView.LayoutManager.getChildMeasureSpec(mSizePerSpan, getHeightMode(), 0, height, false), paramBoolean);
  }
  
  private void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    AnchorInfo localAnchorInfo = mAnchorInfo;
    if (((mPendingSavedState != null) || (mPendingScrollPosition != -1)) && (paramState.getItemCount() == 0))
    {
      removeAndRecycleAllViews(paramRecycler);
      localAnchorInfo.reset();
      return;
    }
    boolean bool = mValid;
    int j = 1;
    if ((bool) && (mPendingScrollPosition == -1) && (mPendingSavedState == null)) {
      i = 0;
    } else {
      i = 1;
    }
    if (i != 0)
    {
      localAnchorInfo.reset();
      if (mPendingSavedState != null)
      {
        applyPendingSavedState(localAnchorInfo);
      }
      else
      {
        resolveShouldLayoutReverse();
        mLayoutFromEnd = mShouldReverseLayout;
      }
      updateAnchorInfoForLayout(paramState, localAnchorInfo);
      mValid = true;
    }
    if ((mPendingSavedState == null) && (mPendingScrollPosition == -1) && ((mLayoutFromEnd != mLastLayoutFromEnd) || (isLayoutRTL() != mLastLayoutRTL)))
    {
      mLazySpanLookup.clear();
      mInvalidateOffsets = true;
    }
    if ((getChildCount() > 0) && ((mPendingSavedState == null) || (mPendingSavedState.mSpanOffsetsSize < 1)))
    {
      if (mInvalidateOffsets)
      {
        i = 0;
        while (i < mSpanCount)
        {
          mSpans[i].clear();
          if (mOffset != Integer.MIN_VALUE) {
            mSpans[i].setLine(mOffset);
          }
          i += 1;
        }
      }
      if ((i == 0) && (mAnchorInfo.mSpanReferenceLines != null)) {
        i = 0;
      }
      while (i < mSpanCount)
      {
        Span localSpan = mSpans[i];
        localSpan.clear();
        localSpan.setLine(mAnchorInfo.mSpanReferenceLines[i]);
        i += 1;
        continue;
        i = 0;
        while (i < mSpanCount)
        {
          mSpans[i].cacheReferenceLineAndClear(mShouldReverseLayout, mOffset);
          i += 1;
        }
        mAnchorInfo.saveSpanReferenceLines(mSpans);
      }
    }
    detachAndScrapAttachedViews(paramRecycler);
    mLayoutState.mRecycle = false;
    mLaidOutInvalidFullSpan = false;
    updateMeasureSpecs(mSecondaryOrientation.getTotalSpace());
    updateLayoutState(mPosition, paramState);
    if (mLayoutFromEnd)
    {
      setLayoutStateDirection(-1);
      fill(paramRecycler, mLayoutState, paramState);
      setLayoutStateDirection(1);
      mLayoutState.mCurrentPosition = (mPosition + mLayoutState.mItemDirection);
      fill(paramRecycler, mLayoutState, paramState);
    }
    else
    {
      setLayoutStateDirection(1);
      fill(paramRecycler, mLayoutState, paramState);
      setLayoutStateDirection(-1);
      mLayoutState.mCurrentPosition = (mPosition + mLayoutState.mItemDirection);
      fill(paramRecycler, mLayoutState, paramState);
    }
    repositionToWrapContentIfNecessary();
    if (getChildCount() > 0) {
      if (mShouldReverseLayout)
      {
        fixEndGap(paramRecycler, paramState, true);
        fixStartGap(paramRecycler, paramState, false);
      }
      else
      {
        fixStartGap(paramRecycler, paramState, true);
        fixEndGap(paramRecycler, paramState, false);
      }
    }
    if ((paramBoolean) && (!paramState.isPreLayout()))
    {
      if ((mGapStrategy != 0) && (getChildCount() > 0) && ((mLaidOutInvalidFullSpan) || (hasGapsToFix() != null))) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0)
      {
        removeCallbacks(mCheckForGapsRunnable);
        if (checkForGaps())
        {
          i = j;
          break label674;
        }
      }
    }
    int i = 0;
    label674:
    if (paramState.isPreLayout()) {
      mAnchorInfo.reset();
    }
    mLastLayoutFromEnd = mLayoutFromEnd;
    mLastLayoutRTL = isLayoutRTL();
    if (i != 0)
    {
      mAnchorInfo.reset();
      onLayoutChildren(paramRecycler, paramState, false);
    }
  }
  
  private boolean preferLastSpan(int paramInt)
  {
    int i;
    if (mOrientation == 0)
    {
      if (paramInt == -1) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != mShouldReverseLayout) {
        return true;
      }
    }
    else
    {
      if (paramInt == -1) {
        i = 1;
      } else {
        i = 0;
      }
      if (i == mShouldReverseLayout) {
        i = 1;
      } else {
        i = 0;
      }
      if (i == isLayoutRTL()) {
        return true;
      }
    }
    return false;
  }
  
  private void prependViewToAllSpans(View paramView)
  {
    int i = mSpanCount - 1;
    while (i >= 0)
    {
      mSpans[i].prependToSpan(paramView);
      i -= 1;
    }
  }
  
  private void recycle(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState)
  {
    if (mRecycle)
    {
      if (mInfinite) {
        return;
      }
      if (mAvailable == 0)
      {
        if (mLayoutDirection == -1)
        {
          recycleFromEnd(paramRecycler, mEndLine);
          return;
        }
        recycleFromStart(paramRecycler, mStartLine);
        return;
      }
      if (mLayoutDirection == -1)
      {
        i = mStartLine - getMaxStart(mStartLine);
        if (i < 0) {
          i = mEndLine;
        } else {
          i = mEndLine - Math.min(i, mAvailable);
        }
        recycleFromEnd(paramRecycler, i);
        return;
      }
      int i = getMinEnd(mEndLine) - mEndLine;
      if (i < 0)
      {
        i = mStartLine;
      }
      else
      {
        int j = mStartLine;
        i = Math.min(i, mAvailable) + j;
      }
      recycleFromStart(paramRecycler, i);
    }
  }
  
  private void recycleFromEnd(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      View localView = getChildAt(i);
      if ((mPrimaryOrientation.getDecoratedStart(localView) < paramInt) || (mPrimaryOrientation.getTransformedStartWithDecoration(localView) < paramInt)) {
        break;
      }
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (mFullSpan)
      {
        int m = 0;
        int j = 0;
        int k;
        for (;;)
        {
          k = m;
          if (j >= mSpanCount) {
            break;
          }
          if (mSpans[j].mViews.size() == 1) {
            return;
          }
          j += 1;
        }
        while (k < mSpanCount)
        {
          mSpans[k].popEnd();
          k += 1;
        }
      }
      if (mSpan.mViews.size() == 1) {
        return;
      }
      mSpan.popEnd();
      removeAndRecycleView(localView, paramRecycler);
      i -= 1;
    }
  }
  
  private void recycleFromStart(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    while (getChildCount() > 0)
    {
      int k = 0;
      View localView = getChildAt(0);
      if ((mPrimaryOrientation.getDecoratedEnd(localView) > paramInt) || (mPrimaryOrientation.getTransformedEndWithDecoration(localView) > paramInt)) {
        break;
      }
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (mFullSpan)
      {
        int i = 0;
        int j;
        for (;;)
        {
          j = k;
          if (i >= mSpanCount) {
            break;
          }
          if (mSpans[i].mViews.size() == 1) {
            return;
          }
          i += 1;
        }
        while (j < mSpanCount)
        {
          mSpans[j].popStart();
          j += 1;
        }
      }
      if (mSpan.mViews.size() == 1) {
        return;
      }
      mSpan.popStart();
      removeAndRecycleView(localView, paramRecycler);
    }
  }
  
  private void repositionToWrapContentIfNecessary()
  {
    if (mSecondaryOrientation.getMode() == 1073741824) {
      return;
    }
    int m = getChildCount();
    int j = 0;
    int i = 0;
    float f1 = 0.0F;
    View localView;
    while (i < m)
    {
      localView = getChildAt(i);
      float f3 = mSecondaryOrientation.getDecoratedMeasurement(localView);
      if (f3 >= f1)
      {
        float f2 = f3;
        if (((LayoutParams)localView.getLayoutParams()).isFullSpan()) {
          f2 = f3 * 1.0F / mSpanCount;
        }
        f1 = Math.max(f1, f2);
      }
      i += 1;
    }
    int n = mSizePerSpan;
    int k = Math.round(f1 * mSpanCount);
    i = k;
    if (mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
      i = Math.min(k, mSecondaryOrientation.getTotalSpace());
    }
    updateMeasureSpecs(i);
    i = j;
    if (mSizePerSpan == n) {
      return;
    }
    while (i < m)
    {
      localView = getChildAt(i);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (!mFullSpan) {
        if ((isLayoutRTL()) && (mOrientation == 1))
        {
          localView.offsetLeftAndRight(-(mSpanCount - 1 - mSpan.mIndex) * mSizePerSpan - -(mSpanCount - 1 - mSpan.mIndex) * n);
        }
        else
        {
          j = mSpan.mIndex * mSizePerSpan;
          k = mSpan.mIndex * n;
          if (mOrientation == 1) {
            localView.offsetLeftAndRight(j - k);
          } else {
            localView.offsetTopAndBottom(j - k);
          }
        }
      }
      i += 1;
    }
  }
  
  private void resolveShouldLayoutReverse()
  {
    if ((mOrientation != 1) && (isLayoutRTL()))
    {
      mShouldReverseLayout = (mReverseLayout ^ true);
      return;
    }
    mShouldReverseLayout = mReverseLayout;
  }
  
  private void setLayoutStateDirection(int paramInt)
  {
    mLayoutState.mLayoutDirection = paramInt;
    LayoutState localLayoutState = mLayoutState;
    boolean bool2 = mShouldReverseLayout;
    int i = 1;
    boolean bool1;
    if (paramInt == -1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    if (bool2 == bool1) {
      paramInt = i;
    } else {
      paramInt = -1;
    }
    mItemDirection = paramInt;
  }
  
  private void updateAllRemainingSpans(int paramInt1, int paramInt2)
  {
    int i = 0;
    while (i < mSpanCount)
    {
      if (!mSpans[i].mViews.isEmpty()) {
        updateRemainingSpans(mSpans[i], paramInt1, paramInt2);
      }
      i += 1;
    }
  }
  
  private boolean updateAnchorFromChildren(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    int i;
    if (mLastLayoutFromEnd) {
      i = findLastReferenceChildPosition(paramState.getItemCount());
    } else {
      i = findFirstReferenceChildPosition(paramState.getItemCount());
    }
    mPosition = i;
    mOffset = Integer.MIN_VALUE;
    return true;
  }
  
  private void updateLayoutState(int paramInt, RecyclerView.State paramState)
  {
    LayoutState localLayoutState = mLayoutState;
    boolean bool2 = false;
    mAvailable = 0;
    mLayoutState.mCurrentPosition = paramInt;
    if (isSmoothScrolling())
    {
      i = paramState.getTargetScrollPosition();
      if (i != -1)
      {
        boolean bool3 = mShouldReverseLayout;
        if (i < paramInt) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        if (bool3 == bool1)
        {
          i = mPrimaryOrientation.getTotalSpace();
          paramInt = 0;
          break label95;
        }
        paramInt = mPrimaryOrientation.getTotalSpace();
        break label93;
      }
    }
    paramInt = 0;
    label93:
    int i = 0;
    label95:
    if (getClipToPadding())
    {
      mLayoutState.mStartLine = (mPrimaryOrientation.getStartAfterPadding() - paramInt);
      mLayoutState.mEndLine = (mPrimaryOrientation.getEndAfterPadding() + i);
    }
    else
    {
      mLayoutState.mEndLine = (mPrimaryOrientation.getEnd() + i);
      mLayoutState.mStartLine = (-paramInt);
    }
    mLayoutState.mStopInFocusable = false;
    mLayoutState.mRecycle = true;
    paramState = mLayoutState;
    boolean bool1 = bool2;
    if (mPrimaryOrientation.getMode() == 0)
    {
      bool1 = bool2;
      if (mPrimaryOrientation.getEnd() == 0) {
        bool1 = true;
      }
    }
    mInfinite = bool1;
  }
  
  private void updateRemainingSpans(Span paramSpan, int paramInt1, int paramInt2)
  {
    int i = paramSpan.getDeletedSize();
    if (paramInt1 == -1)
    {
      if (paramSpan.getStartLine() + i <= paramInt2) {
        mRemainingSpans.set(mIndex, false);
      }
    }
    else if (paramSpan.getEndLine() - i >= paramInt2) {
      mRemainingSpans.set(mIndex, false);
    }
  }
  
  private int updateSpecWithExtra(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 == 0) && (paramInt3 == 0)) {
      return paramInt1;
    }
    int i = View.MeasureSpec.getMode(paramInt1);
    if ((i != Integer.MIN_VALUE) && (i != 1073741824)) {
      return paramInt1;
    }
    return View.MeasureSpec.makeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt1) - paramInt2 - paramInt3), i);
  }
  
  boolean areAllEndsEqual()
  {
    int j = mSpans[0].getEndLine(Integer.MIN_VALUE);
    int i = 1;
    while (i < mSpanCount)
    {
      if (mSpans[i].getEndLine(Integer.MIN_VALUE) != j) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  boolean areAllStartsEqual()
  {
    int j = mSpans[0].getStartLine(Integer.MIN_VALUE);
    int i = 1;
    while (i < mSpanCount)
    {
      if (mSpans[i].getStartLine(Integer.MIN_VALUE) != j) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public void assertNotInLayoutOrScroll(String paramString)
  {
    if (mPendingSavedState == null) {
      super.assertNotInLayoutOrScroll(paramString);
    }
  }
  
  public boolean canScrollHorizontally()
  {
    return mOrientation == 0;
  }
  
  public boolean canScrollVertically()
  {
    return mOrientation == 1;
  }
  
  boolean checkForGaps()
  {
    if ((getChildCount() != 0) && (mGapStrategy != 0))
    {
      if (!isAttachedToWindow()) {
        return false;
      }
      int i;
      int j;
      if (mShouldReverseLayout)
      {
        i = getLastChildPosition();
        j = getFirstChildPosition();
      }
      else
      {
        i = getFirstChildPosition();
        j = getLastChildPosition();
      }
      if ((i == 0) && (hasGapsToFix() != null))
      {
        mLazySpanLookup.clear();
        requestSimpleAnimationsInNextLayout();
        requestLayout();
        return true;
      }
      if (!mLaidOutInvalidFullSpan) {
        return false;
      }
      int k;
      if (mShouldReverseLayout) {
        k = -1;
      } else {
        k = 1;
      }
      Object localObject = mLazySpanLookup;
      j += 1;
      localObject = ((LazySpanLookup)localObject).getFirstFullSpanItemInRange(i, j, k, true);
      if (localObject == null)
      {
        mLaidOutInvalidFullSpan = false;
        mLazySpanLookup.forceInvalidateAfter(j);
        return false;
      }
      StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = mLazySpanLookup.getFirstFullSpanItemInRange(i, mPosition, k * -1, true);
      if (localFullSpanItem == null) {
        mLazySpanLookup.forceInvalidateAfter(mPosition);
      } else {
        mLazySpanLookup.forceInvalidateAfter(mPosition + 1);
      }
      requestSimpleAnimationsInNextLayout();
      requestLayout();
      return true;
    }
    return false;
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    if (mOrientation != 0) {
      paramInt1 = paramInt2;
    }
    if (getChildCount() != 0)
    {
      if (paramInt1 == 0) {
        return;
      }
      prepareLayoutStateForDelta(paramInt1, paramState);
      if ((mPrefetchDistances == null) || (mPrefetchDistances.length < mSpanCount)) {
        mPrefetchDistances = new int[mSpanCount];
      }
      int k = 0;
      paramInt2 = 0;
      int i;
      for (paramInt1 = 0; paramInt2 < mSpanCount; paramInt1 = i)
      {
        int j;
        if (mLayoutState.mItemDirection == -1) {
          j = mLayoutState.mStartLine - mSpans[paramInt2].getStartLine(mLayoutState.mStartLine);
        } else {
          j = mSpans[paramInt2].getEndLine(mLayoutState.mEndLine) - mLayoutState.mEndLine;
        }
        i = paramInt1;
        if (j >= 0)
        {
          mPrefetchDistances[paramInt1] = j;
          i = paramInt1 + 1;
        }
        paramInt2 += 1;
      }
      Arrays.sort(mPrefetchDistances, 0, paramInt1);
      paramInt2 = k;
      while ((paramInt2 < paramInt1) && (mLayoutState.hasMore(paramState)))
      {
        paramLayoutPrefetchRegistry.addPosition(mLayoutState.mCurrentPosition, mPrefetchDistances[paramInt2]);
        LayoutState localLayoutState = mLayoutState;
        mCurrentPosition += mLayoutState.mItemDirection;
        paramInt2 += 1;
      }
    }
  }
  
  public int computeHorizontalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeHorizontalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeHorizontalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  public PointF computeScrollVectorForPosition(int paramInt)
  {
    paramInt = calculateScrollDirectionForPosition(paramInt);
    PointF localPointF = new PointF();
    if (paramInt == 0) {
      return null;
    }
    if (mOrientation == 0)
    {
      x = paramInt;
      y = 0.0F;
      return localPointF;
    }
    x = 0.0F;
    y = paramInt;
    return localPointF;
  }
  
  public int computeVerticalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeVerticalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeVerticalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  public int[] findFirstCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[mSpanCount];
    } else {
      if (paramArrayOfInt.length < mSpanCount) {
        break label54;
      }
    }
    int i = 0;
    while (i < mSpanCount)
    {
      paramArrayOfInt[i] = mSpans[i].findFirstCompletelyVisibleItemPosition();
      i += 1;
    }
    return paramArrayOfInt;
    label54:
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Provided int[]'s size must be more than or equal to span count. Expected:");
    localStringBuilder.append(mSpanCount);
    localStringBuilder.append(", array size:");
    localStringBuilder.append(paramArrayOfInt.length);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  View findFirstVisibleItemClosestToEnd(boolean paramBoolean)
  {
    int j = mPrimaryOrientation.getStartAfterPadding();
    int k = mPrimaryOrientation.getEndAfterPadding();
    int i = getChildCount() - 1;
    Object localObject2;
    for (Object localObject1 = null; i >= 0; localObject1 = localObject2)
    {
      View localView = getChildAt(i);
      int m = mPrimaryOrientation.getDecoratedStart(localView);
      int n = mPrimaryOrientation.getDecoratedEnd(localView);
      localObject2 = localObject1;
      if (n > j) {
        if (m >= k)
        {
          localObject2 = localObject1;
        }
        else if (n > k)
        {
          if (!paramBoolean) {
            return localView;
          }
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = localView;
          }
        }
        else
        {
          return localView;
        }
      }
      i -= 1;
    }
    return localObject1;
  }
  
  View findFirstVisibleItemClosestToStart(boolean paramBoolean)
  {
    int j = mPrimaryOrientation.getStartAfterPadding();
    int k = mPrimaryOrientation.getEndAfterPadding();
    int m = getChildCount();
    Object localObject1 = null;
    int i = 0;
    while (i < m)
    {
      View localView = getChildAt(i);
      int n = mPrimaryOrientation.getDecoratedStart(localView);
      Object localObject2 = localObject1;
      if (mPrimaryOrientation.getDecoratedEnd(localView) > j) {
        if (n >= k)
        {
          localObject2 = localObject1;
        }
        else if (n < j)
        {
          if (!paramBoolean) {
            return localView;
          }
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = localView;
          }
        }
        else
        {
          return localView;
        }
      }
      i += 1;
      localObject1 = localObject2;
    }
    return localObject1;
  }
  
  int findFirstVisibleItemPositionInt()
  {
    View localView;
    if (mShouldReverseLayout) {
      localView = findFirstVisibleItemClosestToEnd(true);
    } else {
      localView = findFirstVisibleItemClosestToStart(true);
    }
    if (localView == null) {
      return -1;
    }
    return getPosition(localView);
  }
  
  public int[] findFirstVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[mSpanCount];
    } else {
      if (paramArrayOfInt.length < mSpanCount) {
        break label54;
      }
    }
    int i = 0;
    while (i < mSpanCount)
    {
      paramArrayOfInt[i] = mSpans[i].findFirstVisibleItemPosition();
      i += 1;
    }
    return paramArrayOfInt;
    label54:
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Provided int[]'s size must be more than or equal to span count. Expected:");
    localStringBuilder.append(mSpanCount);
    localStringBuilder.append(", array size:");
    localStringBuilder.append(paramArrayOfInt.length);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public int[] findLastCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[mSpanCount];
    } else {
      if (paramArrayOfInt.length < mSpanCount) {
        break label54;
      }
    }
    int i = 0;
    while (i < mSpanCount)
    {
      paramArrayOfInt[i] = mSpans[i].findLastCompletelyVisibleItemPosition();
      i += 1;
    }
    return paramArrayOfInt;
    label54:
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Provided int[]'s size must be more than or equal to span count. Expected:");
    localStringBuilder.append(mSpanCount);
    localStringBuilder.append(", array size:");
    localStringBuilder.append(paramArrayOfInt.length);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public int[] findLastVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[mSpanCount];
    } else {
      if (paramArrayOfInt.length < mSpanCount) {
        break label54;
      }
    }
    int i = 0;
    while (i < mSpanCount)
    {
      paramArrayOfInt[i] = mSpans[i].findLastVisibleItemPosition();
      i += 1;
    }
    return paramArrayOfInt;
    label54:
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Provided int[]'s size must be more than or equal to span count. Expected:");
    localStringBuilder.append(mSpanCount);
    localStringBuilder.append(", array size:");
    localStringBuilder.append(paramArrayOfInt.length);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    if (mOrientation == 0) {
      return new LayoutParams(-2, -1);
    }
    return new LayoutParams(-1, -2);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
  {
    return new LayoutParams(paramContext, paramAttributeSet);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (mOrientation == 1) {
      return mSpanCount;
    }
    return super.getColumnCountForAccessibility(paramRecycler, paramState);
  }
  
  int getFirstChildPosition()
  {
    if (getChildCount() == 0) {
      return 0;
    }
    return getPosition(getChildAt(0));
  }
  
  public int getGapStrategy()
  {
    return mGapStrategy;
  }
  
  int getLastChildPosition()
  {
    int i = getChildCount();
    if (i == 0) {
      return 0;
    }
    return getPosition(getChildAt(i - 1));
  }
  
  public int getOrientation()
  {
    return mOrientation;
  }
  
  public boolean getReverseLayout()
  {
    return mReverseLayout;
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (mOrientation == 0) {
      return mSpanCount;
    }
    return super.getRowCountForAccessibility(paramRecycler, paramState);
  }
  
  public int getSpanCount()
  {
    return mSpanCount;
  }
  
  View hasGapsToFix()
  {
    int i = getChildCount() - 1;
    BitSet localBitSet = new BitSet(mSpanCount);
    localBitSet.set(0, mSpanCount, true);
    int j = mOrientation;
    int n = -1;
    if ((j == 1) && (isLayoutRTL())) {
      j = 1;
    } else {
      j = -1;
    }
    int k;
    if (mShouldReverseLayout)
    {
      k = -1;
    }
    else
    {
      k = i + 1;
      i = 0;
    }
    int m = i;
    if (i < k)
    {
      n = 1;
      m = i;
    }
    while (m != k)
    {
      View localView = getChildAt(m);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (localBitSet.get(mSpan.mIndex))
      {
        if (checkSpanForGap(mSpan)) {
          return localView;
        }
        localBitSet.clear(mSpan.mIndex);
      }
      if (!mFullSpan)
      {
        i = m + n;
        if (i != k)
        {
          Object localObject = getChildAt(i);
          int i1;
          if (mShouldReverseLayout)
          {
            i = mPrimaryOrientation.getDecoratedEnd(localView);
            i1 = mPrimaryOrientation.getDecoratedEnd((View)localObject);
            if (i < i1) {
              return localView;
            }
            if (i != i1) {
              break label274;
            }
          }
          else
          {
            i = mPrimaryOrientation.getDecoratedStart(localView);
            i1 = mPrimaryOrientation.getDecoratedStart((View)localObject);
            if (i > i1) {
              return localView;
            }
            if (i != i1) {
              break label274;
            }
          }
          i = 1;
          break label276;
          label274:
          i = 0;
          label276:
          if (i != 0)
          {
            localObject = (LayoutParams)((View)localObject).getLayoutParams();
            if (mSpan.mIndex - mSpan.mIndex < 0) {
              i = 1;
            } else {
              i = 0;
            }
            if (j < 0) {
              i1 = 1;
            } else {
              i1 = 0;
            }
            if (i != i1) {
              return localView;
            }
          }
        }
      }
      m += n;
    }
    return null;
  }
  
  public void invalidateSpanAssignments()
  {
    mLazySpanLookup.clear();
    requestLayout();
  }
  
  public boolean isAutoMeasureEnabled()
  {
    return mGapStrategy != 0;
  }
  
  boolean isLayoutRTL()
  {
    return getLayoutDirection() == 1;
  }
  
  public void offsetChildrenHorizontal(int paramInt)
  {
    super.offsetChildrenHorizontal(paramInt);
    int i = 0;
    while (i < mSpanCount)
    {
      mSpans[i].onOffset(paramInt);
      i += 1;
    }
  }
  
  public void offsetChildrenVertical(int paramInt)
  {
    super.offsetChildrenVertical(paramInt);
    int i = 0;
    while (i < mSpanCount)
    {
      mSpans[i].onOffset(paramInt);
      i += 1;
    }
  }
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
  {
    super.onDetachedFromWindow(paramRecyclerView, paramRecycler);
    removeCallbacks(mCheckForGapsRunnable);
    int i = 0;
    while (i < mSpanCount)
    {
      mSpans[i].clear();
      i += 1;
    }
    paramRecyclerView.requestLayout();
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return null;
    }
    paramView = findContainingItemView(paramView);
    if (paramView == null) {
      return null;
    }
    resolveShouldLayoutReverse();
    int k = convertFocusDirectionToLayoutDirection(paramInt);
    if (k == Integer.MIN_VALUE) {
      return null;
    }
    Object localObject = (LayoutParams)paramView.getLayoutParams();
    boolean bool1 = mFullSpan;
    localObject = mSpan;
    if (k == 1) {
      paramInt = getLastChildPosition();
    } else {
      paramInt = getFirstChildPosition();
    }
    updateLayoutState(paramInt, paramState);
    setLayoutStateDirection(k);
    mLayoutState.mCurrentPosition = (mLayoutState.mItemDirection + paramInt);
    mLayoutState.mAvailable = ((int)(mPrimaryOrientation.getTotalSpace() * 0.33333334F));
    mLayoutState.mStopInFocusable = true;
    LayoutState localLayoutState = mLayoutState;
    int j = 0;
    mRecycle = false;
    fill(paramRecycler, mLayoutState, paramState);
    mLastLayoutFromEnd = mShouldReverseLayout;
    if (!bool1)
    {
      paramRecycler = ((Span)localObject).getFocusableViewAfter(paramInt, k);
      if ((paramRecycler != null) && (paramRecycler != paramView)) {
        return paramRecycler;
      }
    }
    if (preferLastSpan(k))
    {
      i = mSpanCount - 1;
      while (i >= 0)
      {
        paramRecycler = mSpans[i].getFocusableViewAfter(paramInt, k);
        if ((paramRecycler != null) && (paramRecycler != paramView)) {
          return paramRecycler;
        }
        i -= 1;
      }
    }
    int i = 0;
    while (i < mSpanCount)
    {
      paramRecycler = mSpans[i].getFocusableViewAfter(paramInt, k);
      if ((paramRecycler != null) && (paramRecycler != paramView)) {
        return paramRecycler;
      }
      i += 1;
    }
    boolean bool2 = mReverseLayout;
    if (k == -1) {
      paramInt = 1;
    } else {
      paramInt = 0;
    }
    if ((bool2 ^ true) == paramInt) {
      paramInt = 1;
    } else {
      paramInt = 0;
    }
    if (!bool1)
    {
      if (paramInt != 0) {
        i = ((Span)localObject).findFirstPartiallyVisibleItemPosition();
      } else {
        i = ((Span)localObject).findLastPartiallyVisibleItemPosition();
      }
      paramRecycler = findViewByPosition(i);
      if ((paramRecycler != null) && (paramRecycler != paramView)) {
        return paramRecycler;
      }
    }
    i = j;
    if (preferLastSpan(k))
    {
      i = mSpanCount - 1;
      while (i >= 0)
      {
        if (i != mIndex)
        {
          if (paramInt != 0) {
            j = mSpans[i].findFirstPartiallyVisibleItemPosition();
          } else {
            j = mSpans[i].findLastPartiallyVisibleItemPosition();
          }
          paramRecycler = findViewByPosition(j);
          if ((paramRecycler != null) && (paramRecycler != paramView)) {
            return paramRecycler;
          }
        }
        i -= 1;
      }
    }
    while (i < mSpanCount)
    {
      if (paramInt != 0) {
        j = mSpans[i].findFirstPartiallyVisibleItemPosition();
      } else {
        j = mSpans[i].findLastPartiallyVisibleItemPosition();
      }
      paramRecycler = findViewByPosition(j);
      if ((paramRecycler != null) && (paramRecycler != paramView)) {
        return paramRecycler;
      }
      i += 1;
    }
    return null;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (getChildCount() > 0)
    {
      View localView1 = findFirstVisibleItemClosestToStart(false);
      View localView2 = findFirstVisibleItemClosestToEnd(false);
      if (localView1 != null)
      {
        if (localView2 == null) {
          return;
        }
        int i = getPosition(localView1);
        int j = getPosition(localView2);
        if (i < j)
        {
          paramAccessibilityEvent.setFromIndex(i);
          paramAccessibilityEvent.setToIndex(j);
          return;
        }
        paramAccessibilityEvent.setFromIndex(j);
        paramAccessibilityEvent.setToIndex(i);
      }
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    paramRecycler = paramView.getLayoutParams();
    if (!(paramRecycler instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    paramRecycler = (LayoutParams)paramRecycler;
    int i;
    if (mOrientation == 0)
    {
      j = paramRecycler.getSpanIndex();
      if (mFullSpan) {
        i = mSpanCount;
      } else {
        i = 1;
      }
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(j, i, -1, -1, false, false));
      return;
    }
    int j = paramRecycler.getSpanIndex();
    if (mFullSpan) {
      i = mSpanCount;
    } else {
      i = 1;
    }
    paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(-1, -1, j, i, false, false));
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 1);
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    mLazySpanLookup.clear();
    requestLayout();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    handleUpdate(paramInt1, paramInt2, 8);
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 2);
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    handleUpdate(paramInt1, paramInt2, 4);
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    onLayoutChildren(paramRecycler, paramState, true);
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    mPendingScrollPosition = -1;
    mPendingScrollPositionOffset = Integer.MIN_VALUE;
    mPendingSavedState = null;
    mAnchorInfo.reset();
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof SavedState))
    {
      mPendingSavedState = ((SavedState)paramParcelable);
      requestLayout();
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    if (mPendingSavedState != null) {
      return new SavedState(mPendingSavedState);
    }
    SavedState localSavedState = new SavedState();
    mReverseLayout = mReverseLayout;
    mAnchorLayoutFromEnd = mLastLayoutFromEnd;
    mLastLayoutRTL = mLastLayoutRTL;
    LazySpanLookup localLazySpanLookup = mLazySpanLookup;
    int j = 0;
    if ((localLazySpanLookup != null) && (mLazySpanLookup.mData != null))
    {
      mSpanLookup = mLazySpanLookup.mData;
      mSpanLookupSize = mSpanLookup.length;
      mFullSpanItems = mLazySpanLookup.mFullSpanItems;
    }
    else
    {
      mSpanLookupSize = 0;
    }
    if (getChildCount() > 0)
    {
      int i;
      if (mLastLayoutFromEnd) {
        i = getLastChildPosition();
      } else {
        i = getFirstChildPosition();
      }
      mAnchorPosition = i;
      mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
      mSpanOffsetsSize = mSpanCount;
      mSpanOffsets = new int[mSpanCount];
      while (j < mSpanCount)
      {
        int k;
        if (mLastLayoutFromEnd)
        {
          k = mSpans[j].getEndLine(Integer.MIN_VALUE);
          i = k;
          if (k != Integer.MIN_VALUE) {
            i = k - mPrimaryOrientation.getEndAfterPadding();
          }
        }
        else
        {
          k = mSpans[j].getStartLine(Integer.MIN_VALUE);
          i = k;
          if (k != Integer.MIN_VALUE) {
            i = k - mPrimaryOrientation.getStartAfterPadding();
          }
        }
        mSpanOffsets[j] = i;
        j += 1;
      }
    }
    mAnchorPosition = -1;
    mVisibleAnchorPosition = -1;
    mSpanOffsetsSize = 0;
    return localSavedState;
  }
  
  public void onScrollStateChanged(int paramInt)
  {
    if (paramInt == 0) {
      checkForGaps();
    }
  }
  
  void prepareLayoutStateForDelta(int paramInt, RecyclerView.State paramState)
  {
    int i;
    int j;
    if (paramInt > 0)
    {
      i = getLastChildPosition();
      j = 1;
    }
    else
    {
      i = getFirstChildPosition();
      j = -1;
    }
    mLayoutState.mRecycle = true;
    updateLayoutState(i, paramState);
    setLayoutStateDirection(j);
    mLayoutState.mCurrentPosition = (i + mLayoutState.mItemDirection);
    mLayoutState.mAvailable = Math.abs(paramInt);
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (getChildCount() != 0)
    {
      if (paramInt == 0) {
        return 0;
      }
      prepareLayoutStateForDelta(paramInt, paramState);
      int i = fill(paramRecycler, mLayoutState, paramState);
      if (mLayoutState.mAvailable >= i) {
        if (paramInt < 0) {
          paramInt = -i;
        } else {
          paramInt = i;
        }
      }
      mPrimaryOrientation.offsetChildren(-paramInt);
      mLastLayoutFromEnd = mShouldReverseLayout;
      mLayoutState.mAvailable = 0;
      recycle(paramRecycler, mLayoutState);
      return paramInt;
    }
    return 0;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void scrollToPosition(int paramInt)
  {
    if ((mPendingSavedState != null) && (mPendingSavedState.mAnchorPosition != paramInt)) {
      mPendingSavedState.invalidateAnchorPositionInfo();
    }
    mPendingScrollPosition = paramInt;
    mPendingScrollPositionOffset = Integer.MIN_VALUE;
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2)
  {
    if (mPendingSavedState != null) {
      mPendingSavedState.invalidateAnchorPositionInfo();
    }
    mPendingScrollPosition = paramInt1;
    mPendingScrollPositionOffset = paramInt2;
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void setGapStrategy(int paramInt)
  {
    assertNotInLayoutOrScroll(null);
    if (paramInt == mGapStrategy) {
      return;
    }
    if ((paramInt != 0) && (paramInt != 2)) {
      throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
    }
    mGapStrategy = paramInt;
    requestLayout();
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    if (mOrientation == 1)
    {
      paramInt2 = RecyclerView.LayoutManager.chooseSize(paramInt2, paramRect.height() + j, getMinimumHeight());
      paramInt1 = RecyclerView.LayoutManager.chooseSize(paramInt1, mSizePerSpan * mSpanCount + i, getMinimumWidth());
    }
    else
    {
      paramInt1 = RecyclerView.LayoutManager.chooseSize(paramInt1, paramRect.width() + i, getMinimumWidth());
      paramInt2 = RecyclerView.LayoutManager.chooseSize(paramInt2, mSizePerSpan * mSpanCount + j, getMinimumHeight());
    }
    setMeasuredDimension(paramInt1, paramInt2);
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("invalid orientation.");
    }
    assertNotInLayoutOrScroll(null);
    if (paramInt == mOrientation) {
      return;
    }
    mOrientation = paramInt;
    OrientationHelper localOrientationHelper = mPrimaryOrientation;
    mPrimaryOrientation = mSecondaryOrientation;
    mSecondaryOrientation = localOrientationHelper;
    requestLayout();
  }
  
  public void setReverseLayout(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if ((mPendingSavedState != null) && (mPendingSavedState.mReverseLayout != paramBoolean)) {
      mPendingSavedState.mReverseLayout = paramBoolean;
    }
    mReverseLayout = paramBoolean;
    requestLayout();
  }
  
  public void setSpanCount(int paramInt)
  {
    assertNotInLayoutOrScroll(null);
    if (paramInt != mSpanCount)
    {
      invalidateSpanAssignments();
      mSpanCount = paramInt;
      mRemainingSpans = new BitSet(mSpanCount);
      mSpans = new Span[mSpanCount];
      paramInt = 0;
      while (paramInt < mSpanCount)
      {
        mSpans[paramInt] = new Span(paramInt);
        paramInt += 1;
      }
      requestLayout();
    }
  }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
  {
    paramRecyclerView = new LinearSmoothScroller(paramRecyclerView.getContext());
    paramRecyclerView.setTargetPosition(paramInt);
    startSmoothScroll(paramRecyclerView);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return mPendingSavedState == null;
  }
  
  boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    boolean bool2 = paramState.isPreLayout();
    boolean bool1 = false;
    if (!bool2)
    {
      if (mPendingScrollPosition == -1) {
        return false;
      }
      if ((mPendingScrollPosition >= 0) && (mPendingScrollPosition < paramState.getItemCount()))
      {
        if ((mPendingSavedState != null) && (mPendingSavedState.mAnchorPosition != -1) && (mPendingSavedState.mSpanOffsetsSize >= 1))
        {
          mOffset = Integer.MIN_VALUE;
          mPosition = mPendingScrollPosition;
          return true;
        }
        paramState = findViewByPosition(mPendingScrollPosition);
        if (paramState != null)
        {
          if (mShouldReverseLayout) {
            i = getLastChildPosition();
          } else {
            i = getFirstChildPosition();
          }
          mPosition = i;
          if (mPendingScrollPositionOffset != Integer.MIN_VALUE)
          {
            if (mLayoutFromEnd)
            {
              mOffset = (mPrimaryOrientation.getEndAfterPadding() - mPendingScrollPositionOffset - mPrimaryOrientation.getDecoratedEnd(paramState));
              return true;
            }
            mOffset = (mPrimaryOrientation.getStartAfterPadding() + mPendingScrollPositionOffset - mPrimaryOrientation.getDecoratedStart(paramState));
            return true;
          }
          if (mPrimaryOrientation.getDecoratedMeasurement(paramState) > mPrimaryOrientation.getTotalSpace())
          {
            if (mLayoutFromEnd) {
              i = mPrimaryOrientation.getEndAfterPadding();
            } else {
              i = mPrimaryOrientation.getStartAfterPadding();
            }
            mOffset = i;
            return true;
          }
          int i = mPrimaryOrientation.getDecoratedStart(paramState) - mPrimaryOrientation.getStartAfterPadding();
          if (i < 0)
          {
            mOffset = (-i);
            return true;
          }
          i = mPrimaryOrientation.getEndAfterPadding() - mPrimaryOrientation.getDecoratedEnd(paramState);
          if (i < 0)
          {
            mOffset = i;
            return true;
          }
          mOffset = Integer.MIN_VALUE;
          return true;
        }
        mPosition = mPendingScrollPosition;
        if (mPendingScrollPositionOffset == Integer.MIN_VALUE)
        {
          if (calculateScrollDirectionForPosition(mPosition) == 1) {
            bool1 = true;
          }
          mLayoutFromEnd = bool1;
          paramAnchorInfo.assignCoordinateFromPadding();
        }
        else
        {
          paramAnchorInfo.assignCoordinateFromPadding(mPendingScrollPositionOffset);
        }
        mInvalidateOffsets = true;
        return true;
      }
      mPendingScrollPosition = -1;
      mPendingScrollPositionOffset = Integer.MIN_VALUE;
    }
    return false;
  }
  
  void updateAnchorInfoForLayout(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo)) {
      return;
    }
    if (updateAnchorFromChildren(paramState, paramAnchorInfo)) {
      return;
    }
    paramAnchorInfo.assignCoordinateFromPadding();
    mPosition = 0;
  }
  
  void updateMeasureSpecs(int paramInt)
  {
    mSizePerSpan = (paramInt / mSpanCount);
    mFullSizeSpec = View.MeasureSpec.makeMeasureSpec(paramInt, mSecondaryOrientation.getMode());
  }
  
  class AnchorInfo
  {
    boolean mInvalidateOffsets;
    boolean mLayoutFromEnd;
    int mOffset;
    int mPosition;
    int[] mSpanReferenceLines;
    boolean mValid;
    
    AnchorInfo()
    {
      reset();
    }
    
    void assignCoordinateFromPadding()
    {
      int i;
      if (mLayoutFromEnd) {
        i = mPrimaryOrientation.getEndAfterPadding();
      } else {
        i = mPrimaryOrientation.getStartAfterPadding();
      }
      mOffset = i;
    }
    
    void assignCoordinateFromPadding(int paramInt)
    {
      if (mLayoutFromEnd)
      {
        mOffset = (mPrimaryOrientation.getEndAfterPadding() - paramInt);
        return;
      }
      mOffset = (mPrimaryOrientation.getStartAfterPadding() + paramInt);
    }
    
    void reset()
    {
      mPosition = -1;
      mOffset = Integer.MIN_VALUE;
      mLayoutFromEnd = false;
      mInvalidateOffsets = false;
      mValid = false;
      if (mSpanReferenceLines != null) {
        Arrays.fill(mSpanReferenceLines, -1);
      }
    }
    
    void saveSpanReferenceLines(StaggeredGridLayoutManager.Span[] paramArrayOfSpan)
    {
      int j = paramArrayOfSpan.length;
      if ((mSpanReferenceLines == null) || (mSpanReferenceLines.length < j)) {
        mSpanReferenceLines = new int[mSpans.length];
      }
      int i = 0;
      while (i < j)
      {
        mSpanReferenceLines[i] = paramArrayOfSpan[i].getStartLine(Integer.MIN_VALUE);
        i += 1;
      }
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    boolean mFullSpan;
    StaggeredGridLayoutManager.Span mSpan;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    public LayoutParams(RecyclerView.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public final int getSpanIndex()
    {
      if (mSpan == null) {
        return -1;
      }
      return mSpan.mIndex;
    }
    
    public boolean isFullSpan()
    {
      return mFullSpan;
    }
    
    public void setFullSpan(boolean paramBoolean)
    {
      mFullSpan = paramBoolean;
    }
  }
  
  static class LazySpanLookup
  {
    private static final int MIN_SIZE = 10;
    int[] mData;
    List<FullSpanItem> mFullSpanItems;
    
    LazySpanLookup() {}
    
    private int invalidateFullSpansAfter(int paramInt)
    {
      if (mFullSpanItems == null) {
        return -1;
      }
      FullSpanItem localFullSpanItem = getFullSpanItem(paramInt);
      if (localFullSpanItem != null) {
        mFullSpanItems.remove(localFullSpanItem);
      }
      int j = mFullSpanItems.size();
      int i = 0;
      while (i < j)
      {
        if (mFullSpanItems.get(i)).mPosition >= paramInt) {
          break label82;
        }
        i += 1;
      }
      i = -1;
      label82:
      if (i != -1)
      {
        localFullSpanItem = (FullSpanItem)mFullSpanItems.get(i);
        mFullSpanItems.remove(i);
        return mPosition;
      }
      return -1;
    }
    
    private void offsetFullSpansForAddition(int paramInt1, int paramInt2)
    {
      if (mFullSpanItems == null) {
        return;
      }
      int i = mFullSpanItems.size() - 1;
      while (i >= 0)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)mFullSpanItems.get(i);
        if (mPosition >= paramInt1) {
          mPosition += paramInt2;
        }
        i -= 1;
      }
    }
    
    private void offsetFullSpansForRemoval(int paramInt1, int paramInt2)
    {
      if (mFullSpanItems == null) {
        return;
      }
      int i = mFullSpanItems.size() - 1;
      while (i >= 0)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)mFullSpanItems.get(i);
        if (mPosition >= paramInt1) {
          if (mPosition < paramInt1 + paramInt2) {
            mFullSpanItems.remove(i);
          } else {
            mPosition -= paramInt2;
          }
        }
        i -= 1;
      }
    }
    
    public void addFullSpanItem(FullSpanItem paramFullSpanItem)
    {
      if (mFullSpanItems == null) {
        mFullSpanItems = new ArrayList();
      }
      int j = mFullSpanItems.size();
      int i = 0;
      while (i < j)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)mFullSpanItems.get(i);
        if (mPosition == mPosition) {
          mFullSpanItems.remove(i);
        }
        if (mPosition >= mPosition)
        {
          mFullSpanItems.add(i, paramFullSpanItem);
          return;
        }
        i += 1;
      }
      mFullSpanItems.add(paramFullSpanItem);
    }
    
    void clear()
    {
      if (mData != null) {
        Arrays.fill(mData, -1);
      }
      mFullSpanItems = null;
    }
    
    void ensureSize(int paramInt)
    {
      if (mData == null)
      {
        mData = new int[Math.max(paramInt, 10) + 1];
        Arrays.fill(mData, -1);
        return;
      }
      if (paramInt >= mData.length)
      {
        int[] arrayOfInt = mData;
        mData = new int[sizeForPosition(paramInt)];
        System.arraycopy(arrayOfInt, 0, mData, 0, arrayOfInt.length);
        Arrays.fill(mData, arrayOfInt.length, mData.length, -1);
      }
    }
    
    int forceInvalidateAfter(int paramInt)
    {
      if (mFullSpanItems != null)
      {
        int i = mFullSpanItems.size() - 1;
        while (i >= 0)
        {
          if (mFullSpanItems.get(i)).mPosition >= paramInt) {
            mFullSpanItems.remove(i);
          }
          i -= 1;
        }
      }
      return invalidateAfter(paramInt);
    }
    
    public FullSpanItem getFirstFullSpanItemInRange(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      if (mFullSpanItems == null) {
        return null;
      }
      int j = mFullSpanItems.size();
      int i = 0;
      FullSpanItem localFullSpanItem;
      while (i < j)
      {
        localFullSpanItem = (FullSpanItem)mFullSpanItems.get(i);
        if (mPosition >= paramInt2) {
          return null;
        }
        if (mPosition >= paramInt1)
        {
          if ((paramInt3 == 0) || (mGapDir == paramInt3)) {
            break label106;
          }
          if ((paramBoolean) && (mHasUnwantedGapAfter)) {
            return localFullSpanItem;
          }
        }
        i += 1;
      }
      return null;
      label106:
      return localFullSpanItem;
    }
    
    public FullSpanItem getFullSpanItem(int paramInt)
    {
      if (mFullSpanItems == null) {
        return null;
      }
      int i = mFullSpanItems.size() - 1;
      while (i >= 0)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)mFullSpanItems.get(i);
        if (mPosition == paramInt) {
          return localFullSpanItem;
        }
        i -= 1;
      }
      return null;
    }
    
    int getSpan(int paramInt)
    {
      if ((mData != null) && (paramInt < mData.length)) {
        return mData[paramInt];
      }
      return -1;
    }
    
    int invalidateAfter(int paramInt)
    {
      if (mData == null) {
        return -1;
      }
      if (paramInt >= mData.length) {
        return -1;
      }
      int i = invalidateFullSpansAfter(paramInt);
      if (i == -1)
      {
        Arrays.fill(mData, paramInt, mData.length, -1);
        return mData.length;
      }
      int[] arrayOfInt = mData;
      i += 1;
      Arrays.fill(arrayOfInt, paramInt, i, -1);
      return i;
    }
    
    void offsetForAddition(int paramInt1, int paramInt2)
    {
      if (mData != null)
      {
        if (paramInt1 >= mData.length) {
          return;
        }
        int i = paramInt1 + paramInt2;
        ensureSize(i);
        System.arraycopy(mData, paramInt1, mData, i, mData.length - paramInt1 - paramInt2);
        Arrays.fill(mData, paramInt1, i, -1);
        offsetFullSpansForAddition(paramInt1, paramInt2);
      }
    }
    
    void offsetForRemoval(int paramInt1, int paramInt2)
    {
      if (mData != null)
      {
        if (paramInt1 >= mData.length) {
          return;
        }
        int i = paramInt1 + paramInt2;
        ensureSize(i);
        System.arraycopy(mData, i, mData, paramInt1, mData.length - paramInt1 - paramInt2);
        Arrays.fill(mData, mData.length - paramInt2, mData.length, -1);
        offsetFullSpansForRemoval(paramInt1, paramInt2);
      }
    }
    
    void setSpan(int paramInt, StaggeredGridLayoutManager.Span paramSpan)
    {
      ensureSize(paramInt);
      mData[paramInt] = mIndex;
    }
    
    int sizeForPosition(int paramInt)
    {
      int i = mData.length;
      while (i <= paramInt) {
        i *= 2;
      }
      return i;
    }
    
    @SuppressLint({"BanParcelableUsage"})
    static class FullSpanItem
      implements Parcelable
    {
      public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator()
      {
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel paramAnonymousParcel)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(paramAnonymousParcel);
        }
        
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int paramAnonymousInt)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[paramAnonymousInt];
        }
      };
      int mGapDir;
      int[] mGapPerSpan;
      boolean mHasUnwantedGapAfter;
      int mPosition;
      
      FullSpanItem() {}
      
      FullSpanItem(Parcel paramParcel)
      {
        mPosition = paramParcel.readInt();
        mGapDir = paramParcel.readInt();
        int i = paramParcel.readInt();
        boolean bool = true;
        if (i != 1) {
          bool = false;
        }
        mHasUnwantedGapAfter = bool;
        i = paramParcel.readInt();
        if (i > 0)
        {
          mGapPerSpan = new int[i];
          paramParcel.readIntArray(mGapPerSpan);
        }
      }
      
      public int describeContents()
      {
        return 0;
      }
      
      int getGapForSpan(int paramInt)
      {
        if (mGapPerSpan == null) {
          return 0;
        }
        return mGapPerSpan[paramInt];
      }
      
      public String toString()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("FullSpanItem{mPosition=");
        localStringBuilder.append(mPosition);
        localStringBuilder.append(", mGapDir=");
        localStringBuilder.append(mGapDir);
        localStringBuilder.append(", mHasUnwantedGapAfter=");
        localStringBuilder.append(mHasUnwantedGapAfter);
        localStringBuilder.append(", mGapPerSpan=");
        localStringBuilder.append(Arrays.toString(mGapPerSpan));
        localStringBuilder.append('}');
        return localStringBuilder.toString();
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
      }
    }
  }
  
  @SuppressLint({"BanParcelableUsage"})
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public StaggeredGridLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new StaggeredGridLayoutManager.SavedState(paramAnonymousParcel);
      }
      
      public StaggeredGridLayoutManager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new StaggeredGridLayoutManager.SavedState[paramAnonymousInt];
      }
    };
    boolean mAnchorLayoutFromEnd;
    int mAnchorPosition;
    List<StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem> mFullSpanItems;
    boolean mLastLayoutRTL;
    boolean mReverseLayout;
    int[] mSpanLookup;
    int mSpanLookupSize;
    int[] mSpanOffsets;
    int mSpanOffsetsSize;
    int mVisibleAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      mAnchorPosition = paramParcel.readInt();
      mVisibleAnchorPosition = paramParcel.readInt();
      mSpanOffsetsSize = paramParcel.readInt();
      if (mSpanOffsetsSize > 0)
      {
        mSpanOffsets = new int[mSpanOffsetsSize];
        paramParcel.readIntArray(mSpanOffsets);
      }
      mSpanLookupSize = paramParcel.readInt();
      if (mSpanLookupSize > 0)
      {
        mSpanLookup = new int[mSpanLookupSize];
        paramParcel.readIntArray(mSpanLookup);
      }
      int i = paramParcel.readInt();
      boolean bool2 = false;
      if (i == 1) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      mReverseLayout = bool1;
      if (paramParcel.readInt() == 1) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      mAnchorLayoutFromEnd = bool1;
      boolean bool1 = bool2;
      if (paramParcel.readInt() == 1) {
        bool1 = true;
      }
      mLastLayoutRTL = bool1;
      mFullSpanItems = paramParcel.readArrayList(StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem.class.getClassLoader());
    }
    
    public SavedState(SavedState paramSavedState)
    {
      mSpanOffsetsSize = mSpanOffsetsSize;
      mAnchorPosition = mAnchorPosition;
      mVisibleAnchorPosition = mVisibleAnchorPosition;
      mSpanOffsets = mSpanOffsets;
      mSpanLookupSize = mSpanLookupSize;
      mSpanLookup = mSpanLookup;
      mReverseLayout = mReverseLayout;
      mAnchorLayoutFromEnd = mAnchorLayoutFromEnd;
      mLastLayoutRTL = mLastLayoutRTL;
      mFullSpanItems = mFullSpanItems;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    void invalidateAnchorPositionInfo()
    {
      mSpanOffsets = null;
      mSpanOffsetsSize = 0;
      mAnchorPosition = -1;
      mVisibleAnchorPosition = -1;
    }
    
    void invalidateSpanInfo()
    {
      mSpanOffsets = null;
      mSpanOffsetsSize = 0;
      mSpanLookupSize = 0;
      mSpanLookup = null;
      mFullSpanItems = null;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
    }
  }
  
  class Span
  {
    static final int INVALID_LINE = Integer.MIN_VALUE;
    int mCachedEnd = Integer.MIN_VALUE;
    int mCachedStart = Integer.MIN_VALUE;
    int mDeletedSize = 0;
    final int mIndex;
    ArrayList<View> mViews = new ArrayList();
    
    Span(int paramInt)
    {
      mIndex = paramInt;
    }
    
    void appendToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      mSpan = this;
      mViews.add(paramView);
      mCachedEnd = Integer.MIN_VALUE;
      if (mViews.size() == 1) {
        mCachedStart = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        mDeletedSize += mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void cacheReferenceLineAndClear(boolean paramBoolean, int paramInt)
    {
      int i;
      if (paramBoolean) {
        i = getEndLine(Integer.MIN_VALUE);
      } else {
        i = getStartLine(Integer.MIN_VALUE);
      }
      clear();
      if (i == Integer.MIN_VALUE) {
        return;
      }
      if ((!paramBoolean) || (i >= mPrimaryOrientation.getEndAfterPadding()))
      {
        if ((!paramBoolean) && (i > mPrimaryOrientation.getStartAfterPadding())) {
          return;
        }
        int j = i;
        if (paramInt != Integer.MIN_VALUE) {
          j = i + paramInt;
        }
        mCachedEnd = j;
        mCachedStart = j;
      }
    }
    
    void calculateCachedEnd()
    {
      Object localObject = (View)mViews.get(mViews.size() - 1);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams((View)localObject);
      mCachedEnd = mPrimaryOrientation.getDecoratedEnd((View)localObject);
      if (mFullSpan)
      {
        localObject = mLazySpanLookup.getFullSpanItem(localLayoutParams.getViewLayoutPosition());
        if ((localObject != null) && (mGapDir == 1)) {
          mCachedEnd += ((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).getGapForSpan(mIndex);
        }
      }
    }
    
    void calculateCachedStart()
    {
      Object localObject = (View)mViews.get(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams((View)localObject);
      mCachedStart = mPrimaryOrientation.getDecoratedStart((View)localObject);
      if (mFullSpan)
      {
        localObject = mLazySpanLookup.getFullSpanItem(localLayoutParams.getViewLayoutPosition());
        if ((localObject != null) && (mGapDir == -1)) {
          mCachedStart -= ((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).getGapForSpan(mIndex);
        }
      }
    }
    
    void clear()
    {
      mViews.clear();
      invalidateCache();
      mDeletedSize = 0;
    }
    
    public int findFirstCompletelyVisibleItemPosition()
    {
      if (mReverseLayout) {
        return findOneVisibleChild(mViews.size() - 1, -1, true);
      }
      return findOneVisibleChild(0, mViews.size(), true);
    }
    
    public int findFirstPartiallyVisibleItemPosition()
    {
      if (mReverseLayout) {
        return findOnePartiallyVisibleChild(mViews.size() - 1, -1, true);
      }
      return findOnePartiallyVisibleChild(0, mViews.size(), true);
    }
    
    public int findFirstVisibleItemPosition()
    {
      if (mReverseLayout) {
        return findOneVisibleChild(mViews.size() - 1, -1, false);
      }
      return findOneVisibleChild(0, mViews.size(), false);
    }
    
    public int findLastCompletelyVisibleItemPosition()
    {
      if (mReverseLayout) {
        return findOneVisibleChild(0, mViews.size(), true);
      }
      return findOneVisibleChild(mViews.size() - 1, -1, true);
    }
    
    public int findLastPartiallyVisibleItemPosition()
    {
      if (mReverseLayout) {
        return findOnePartiallyVisibleChild(0, mViews.size(), true);
      }
      return findOnePartiallyVisibleChild(mViews.size() - 1, -1, true);
    }
    
    public int findLastVisibleItemPosition()
    {
      if (mReverseLayout) {
        return findOneVisibleChild(0, mViews.size(), false);
      }
      return findOneVisibleChild(mViews.size() - 1, -1, false);
    }
    
    int findOnePartiallyOrCompletelyVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      int m = mPrimaryOrientation.getStartAfterPadding();
      int n = mPrimaryOrientation.getEndAfterPadding();
      int i;
      if (paramInt2 > paramInt1) {
        i = 1;
      } else {
        i = -1;
      }
      while (paramInt1 != paramInt2)
      {
        View localView = (View)mViews.get(paramInt1);
        int i1 = mPrimaryOrientation.getDecoratedStart(localView);
        int i2 = mPrimaryOrientation.getDecoratedEnd(localView);
        int k = 0;
        if (paramBoolean3) {
          if (i1 > n) {}
        }
        int j;
        for (;;)
        {
          j = 1;
          break;
          do
          {
            j = 0;
            break;
          } while (i1 >= n);
        }
        if (paramBoolean3)
        {
          if (i2 < m) {}
        }
        else {
          while (i2 > m)
          {
            k = 1;
            break;
          }
        }
        if ((j != 0) && (k != 0)) {
          if ((paramBoolean1) && (paramBoolean2))
          {
            if ((i1 >= m) && (i2 <= n)) {
              return getPosition(localView);
            }
          }
          else
          {
            if (paramBoolean2) {
              return getPosition(localView);
            }
            if ((i1 < m) || (i2 > n)) {
              return getPosition(localView);
            }
          }
        }
        paramInt1 += i;
      }
      return -1;
    }
    
    int findOnePartiallyVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return findOnePartiallyOrCompletelyVisibleChild(paramInt1, paramInt2, false, false, paramBoolean);
    }
    
    int findOneVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return findOnePartiallyOrCompletelyVisibleChild(paramInt1, paramInt2, paramBoolean, true, false);
    }
    
    public int getDeletedSize()
    {
      return mDeletedSize;
    }
    
    int getEndLine()
    {
      if (mCachedEnd != Integer.MIN_VALUE) {
        return mCachedEnd;
      }
      calculateCachedEnd();
      return mCachedEnd;
    }
    
    int getEndLine(int paramInt)
    {
      if (mCachedEnd != Integer.MIN_VALUE) {
        return mCachedEnd;
      }
      if (mViews.size() == 0) {
        return paramInt;
      }
      calculateCachedEnd();
      return mCachedEnd;
    }
    
    public View getFocusableViewAfter(int paramInt1, int paramInt2)
    {
      Object localObject2 = null;
      Object localObject1 = null;
      View localView;
      if (paramInt2 == -1)
      {
        int i = mViews.size();
        paramInt2 = 0;
        for (;;)
        {
          localObject2 = localObject1;
          if (paramInt2 >= i) {
            break;
          }
          localView = (View)mViews.get(paramInt2);
          if (mReverseLayout)
          {
            localObject2 = localObject1;
            if (getPosition(localView) <= paramInt1) {
              break;
            }
          }
          if ((!mReverseLayout) && (getPosition(localView) >= paramInt1)) {
            return localObject1;
          }
          localObject2 = localObject1;
          if (!localView.hasFocusable()) {
            break;
          }
          paramInt2 += 1;
          localObject1 = localView;
        }
      }
      paramInt2 = mViews.size() - 1;
      for (localObject1 = localObject2;; localObject1 = localView)
      {
        localObject2 = localObject1;
        if (paramInt2 < 0) {
          break;
        }
        localView = (View)mViews.get(paramInt2);
        if (mReverseLayout)
        {
          localObject2 = localObject1;
          if (getPosition(localView) >= paramInt1) {
            break;
          }
        }
        if ((!mReverseLayout) && (getPosition(localView) <= paramInt1)) {
          return localObject1;
        }
        localObject2 = localObject1;
        if (!localView.hasFocusable()) {
          break;
        }
        paramInt2 -= 1;
      }
      return localObject2;
    }
    
    StaggeredGridLayoutManager.LayoutParams getLayoutParams(View paramView)
    {
      return (StaggeredGridLayoutManager.LayoutParams)paramView.getLayoutParams();
    }
    
    int getStartLine()
    {
      if (mCachedStart != Integer.MIN_VALUE) {
        return mCachedStart;
      }
      calculateCachedStart();
      return mCachedStart;
    }
    
    int getStartLine(int paramInt)
    {
      if (mCachedStart != Integer.MIN_VALUE) {
        return mCachedStart;
      }
      if (mViews.size() == 0) {
        return paramInt;
      }
      calculateCachedStart();
      return mCachedStart;
    }
    
    void invalidateCache()
    {
      mCachedStart = Integer.MIN_VALUE;
      mCachedEnd = Integer.MIN_VALUE;
    }
    
    void onOffset(int paramInt)
    {
      if (mCachedStart != Integer.MIN_VALUE) {
        mCachedStart += paramInt;
      }
      if (mCachedEnd != Integer.MIN_VALUE) {
        mCachedEnd += paramInt;
      }
    }
    
    void popEnd()
    {
      int i = mViews.size();
      View localView = (View)mViews.remove(i - 1);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      mSpan = null;
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        mDeletedSize -= mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      if (i == 1) {
        mCachedStart = Integer.MIN_VALUE;
      }
      mCachedEnd = Integer.MIN_VALUE;
    }
    
    void popStart()
    {
      View localView = (View)mViews.remove(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      mSpan = null;
      if (mViews.size() == 0) {
        mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        mDeletedSize -= mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      mCachedStart = Integer.MIN_VALUE;
    }
    
    void prependToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      mSpan = this;
      mViews.add(0, paramView);
      mCachedStart = Integer.MIN_VALUE;
      if (mViews.size() == 1) {
        mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        mDeletedSize += mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void setLine(int paramInt)
    {
      mCachedStart = paramInt;
      mCachedEnd = paramInt;
    }
  }
}
