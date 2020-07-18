package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import java.util.Arrays;

public class GridLayoutManager
  extends LinearLayoutManager
{
  private static final boolean DEBUG = false;
  public static final int DEFAULT_SPAN_COUNT = -1;
  private static final String TAG = "GridLayoutManager";
  int[] mCachedBorders;
  final Rect mDecorInsets = new Rect();
  boolean mPendingSpanCountChange = false;
  final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
  final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
  View[] mSet;
  int mSpanCount = -1;
  SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();
  private boolean mUsingSpansToEstimateScrollBarDimensions;
  
  public GridLayoutManager(Context paramContext, int paramInt)
  {
    super(paramContext);
    setSpanCount(paramInt);
  }
  
  public GridLayoutManager(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramInt2, paramBoolean);
    setSpanCount(paramInt1);
  }
  
  public GridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setSpanCount(getPropertiesspanCount);
  }
  
  private void assignSpans(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt, boolean paramBoolean)
  {
    int i = -1;
    int k = 0;
    int j;
    if (paramBoolean)
    {
      int m = 0;
      j = 1;
      i = paramInt;
      paramInt = m;
    }
    else
    {
      paramInt -= 1;
      j = -1;
    }
    while (paramInt != i)
    {
      View localView = mSet[paramInt];
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      mSpanSize = getSpanSize(paramRecycler, paramState, getPosition(localView));
      mSpanIndex = k;
      k += mSpanSize;
      paramInt += j;
    }
  }
  
  private void cachePreLayoutSpanMapping()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      LayoutParams localLayoutParams = (LayoutParams)getChildAt(i).getLayoutParams();
      int k = localLayoutParams.getViewLayoutPosition();
      mPreLayoutSpanSizeCache.put(k, localLayoutParams.getSpanSize());
      mPreLayoutSpanIndexCache.put(k, localLayoutParams.getSpanIndex());
      i += 1;
    }
  }
  
  private void calculateItemBorders(int paramInt)
  {
    mCachedBorders = calculateItemBorders(mCachedBorders, mSpanCount, paramInt);
  }
  
  static int[] calculateItemBorders(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int j = 1;
    int[] arrayOfInt;
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length == paramInt1 + 1))
    {
      arrayOfInt = paramArrayOfInt;
      if (paramArrayOfInt[(paramArrayOfInt.length - 1)] == paramInt2) {}
    }
    else
    {
      arrayOfInt = new int[paramInt1 + 1];
    }
    int k = 0;
    arrayOfInt[0] = 0;
    int m = paramInt2 / paramInt1;
    int n = paramInt2 % paramInt1;
    int i = 0;
    paramInt2 = k;
    while (j <= paramInt1)
    {
      paramInt2 += n;
      if ((paramInt2 > 0) && (paramInt1 - paramInt2 < n))
      {
        k = m + 1;
        paramInt2 -= paramInt1;
      }
      else
      {
        k = m;
      }
      i += k;
      arrayOfInt[j] = i;
      j += 1;
    }
    return arrayOfInt;
  }
  
  private void clearPreLayoutSpanMappingCache()
  {
    mPreLayoutSpanSizeCache.clear();
    mPreLayoutSpanIndexCache.clear();
  }
  
  private int computeScrollOffsetWithSpanInfo(RecyclerView.State paramState)
  {
    if (getChildCount() != 0)
    {
      if (paramState.getItemCount() == 0) {
        return 0;
      }
      ensureLayoutState();
      boolean bool = isSmoothScrollbarEnabled();
      View localView1 = findFirstVisibleChildClosestToStart(bool ^ true, true);
      View localView2 = findFirstVisibleChildClosestToEnd(bool ^ true, true);
      if (localView1 != null)
      {
        if (localView2 == null) {
          return 0;
        }
        int j = mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(localView1), mSpanCount);
        int k = mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(localView2), mSpanCount);
        int i = Math.min(j, k);
        j = Math.max(j, k);
        k = mSpanSizeLookup.getCachedSpanGroupIndex(paramState.getItemCount() - 1, mSpanCount);
        if (mShouldReverseLayout) {
          i = Math.max(0, k + 1 - j - 1);
        } else {
          i = Math.max(0, i);
        }
        if (!bool) {
          return i;
        }
        j = Math.abs(mOrientationHelper.getDecoratedEnd(localView2) - mOrientationHelper.getDecoratedStart(localView1));
        k = mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(localView1), mSpanCount);
        int m = mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(localView2), mSpanCount);
        float f = j / (m - k + 1);
        return Math.round(i * f + (mOrientationHelper.getStartAfterPadding() - mOrientationHelper.getDecoratedStart(localView1)));
      }
    }
    return 0;
  }
  
  private int computeScrollRangeWithSpanInfo(RecyclerView.State paramState)
  {
    if (getChildCount() != 0)
    {
      if (paramState.getItemCount() == 0) {
        return 0;
      }
      ensureLayoutState();
      View localView1 = findFirstVisibleChildClosestToStart(isSmoothScrollbarEnabled() ^ true, true);
      View localView2 = findFirstVisibleChildClosestToEnd(isSmoothScrollbarEnabled() ^ true, true);
      if (localView1 != null)
      {
        if (localView2 == null) {
          return 0;
        }
        if (!isSmoothScrollbarEnabled()) {
          return mSpanSizeLookup.getCachedSpanGroupIndex(paramState.getItemCount() - 1, mSpanCount) + 1;
        }
        int i = mOrientationHelper.getDecoratedEnd(localView2);
        int j = mOrientationHelper.getDecoratedStart(localView1);
        int k = mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(localView1), mSpanCount);
        int m = mSpanSizeLookup.getCachedSpanGroupIndex(getPosition(localView2), mSpanCount);
        int n = mSpanSizeLookup.getCachedSpanGroupIndex(paramState.getItemCount() - 1, mSpanCount);
        return (int)((i - j) / (m - k + 1) * (n + 1));
      }
    }
    return 0;
  }
  
  private void ensureAnchorIsInCorrectSpan(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    if (paramInt == 1) {
      paramInt = 1;
    } else {
      paramInt = 0;
    }
    int i = getSpanIndex(paramRecycler, paramState, mPosition);
    if (paramInt != 0) {
      while ((i > 0) && (mPosition > 0))
      {
        mPosition -= 1;
        i = getSpanIndex(paramRecycler, paramState, mPosition);
      }
    }
    int m = paramState.getItemCount();
    paramInt = mPosition;
    while (paramInt < m - 1)
    {
      int k = paramInt + 1;
      int j = getSpanIndex(paramRecycler, paramState, k);
      if (j <= i) {
        break;
      }
      paramInt = k;
      i = j;
    }
    mPosition = paramInt;
  }
  
  private void ensureViewSet()
  {
    if ((mSet == null) || (mSet.length != mSpanCount)) {
      mSet = new View[mSpanCount];
    }
  }
  
  private int getSpanGroupIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      return mSpanSizeLookup.getCachedSpanGroupIndex(paramInt, mSpanCount);
    }
    int i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1)
    {
      paramRecycler = new StringBuilder();
      paramRecycler.append("Cannot find span size for pre layout position. ");
      paramRecycler.append(paramInt);
      Log.w("GridLayoutManager", paramRecycler.toString());
      return 0;
    }
    return mSpanSizeLookup.getCachedSpanGroupIndex(i, mSpanCount);
  }
  
  private int getSpanIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      return mSpanSizeLookup.getCachedSpanIndex(paramInt, mSpanCount);
    }
    int i = mPreLayoutSpanIndexCache.get(paramInt, -1);
    if (i != -1) {
      return i;
    }
    i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1)
    {
      paramRecycler = new StringBuilder();
      paramRecycler.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
      paramRecycler.append(paramInt);
      Log.w("GridLayoutManager", paramRecycler.toString());
      return 0;
    }
    return mSpanSizeLookup.getCachedSpanIndex(i, mSpanCount);
  }
  
  private int getSpanSize(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      return mSpanSizeLookup.getSpanSize(paramInt);
    }
    int i = mPreLayoutSpanSizeCache.get(paramInt, -1);
    if (i != -1) {
      return i;
    }
    i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1)
    {
      paramRecycler = new StringBuilder();
      paramRecycler.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
      paramRecycler.append(paramInt);
      Log.w("GridLayoutManager", paramRecycler.toString());
      return 1;
    }
    return mSpanSizeLookup.getSpanSize(i);
  }
  
  private void guessMeasurement(float paramFloat, int paramInt)
  {
    calculateItemBorders(Math.max(Math.round(paramFloat * mSpanCount), paramInt));
  }
  
  private void measureChild(View paramView, int paramInt, boolean paramBoolean)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = mDecorInsets;
    int j = top + bottom + topMargin + bottomMargin;
    int i = left + right + leftMargin + rightMargin;
    int k = getSpaceForSpanRange(mSpanIndex, mSpanSize);
    if (mOrientation == 1)
    {
      i = RecyclerView.LayoutManager.getChildMeasureSpec(k, paramInt, i, width, false);
      paramInt = RecyclerView.LayoutManager.getChildMeasureSpec(mOrientationHelper.getTotalSpace(), getHeightMode(), j, height, true);
    }
    else
    {
      paramInt = RecyclerView.LayoutManager.getChildMeasureSpec(k, paramInt, j, height, false);
      i = RecyclerView.LayoutManager.getChildMeasureSpec(mOrientationHelper.getTotalSpace(), getWidthMode(), i, width, true);
    }
    measureChildWithDecorationsAndMargin(paramView, i, paramInt, paramBoolean);
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
    if (paramBoolean) {
      paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);
    } else {
      paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);
    }
    if (paramBoolean) {
      paramView.measure(paramInt1, paramInt2);
    }
  }
  
  private void updateMeasurements()
  {
    int i;
    if (getOrientation() == 1) {
      i = getWidth() - getPaddingRight() - getPaddingLeft();
    } else {
      i = getHeight() - getPaddingBottom() - getPaddingTop();
    }
    calculateItemBorders(i);
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void collectPrefetchPositionsForLayoutState(RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int j = mSpanCount;
    int i = 0;
    while ((i < mSpanCount) && (paramLayoutState.hasMore(paramState)) && (j > 0))
    {
      int k = mCurrentPosition;
      paramLayoutPrefetchRegistry.addPosition(k, Math.max(0, mScrollingOffset));
      j -= mSpanSizeLookup.getSpanSize(k);
      mCurrentPosition += mItemDirection;
      i += 1;
    }
  }
  
  public int computeHorizontalScrollOffset(RecyclerView.State paramState)
  {
    if (mUsingSpansToEstimateScrollBarDimensions) {
      return computeScrollOffsetWithSpanInfo(paramState);
    }
    return super.computeHorizontalScrollOffset(paramState);
  }
  
  public int computeHorizontalScrollRange(RecyclerView.State paramState)
  {
    if (mUsingSpansToEstimateScrollBarDimensions) {
      return computeScrollRangeWithSpanInfo(paramState);
    }
    return super.computeHorizontalScrollRange(paramState);
  }
  
  public int computeVerticalScrollOffset(RecyclerView.State paramState)
  {
    if (mUsingSpansToEstimateScrollBarDimensions) {
      return computeScrollOffsetWithSpanInfo(paramState);
    }
    return super.computeVerticalScrollOffset(paramState);
  }
  
  public int computeVerticalScrollRange(RecyclerView.State paramState)
  {
    if (mUsingSpansToEstimateScrollBarDimensions) {
      return computeScrollRangeWithSpanInfo(paramState);
    }
    return super.computeVerticalScrollRange(paramState);
  }
  
  View findReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, int paramInt3)
  {
    ensureLayoutState();
    int j = mOrientationHelper.getStartAfterPadding();
    int k = mOrientationHelper.getEndAfterPadding();
    int i;
    if (paramInt2 > paramInt1) {
      i = 1;
    } else {
      i = -1;
    }
    Object localObject2 = null;
    Object localObject4;
    for (Object localObject1 = null; paramInt1 != paramInt2; localObject1 = localObject4)
    {
      View localView = getChildAt(paramInt1);
      int m = getPosition(localView);
      Object localObject3 = localObject2;
      localObject4 = localObject1;
      if (m >= 0)
      {
        localObject3 = localObject2;
        localObject4 = localObject1;
        if (m < paramInt3) {
          if (getSpanIndex(paramRecycler, paramState, m) != 0)
          {
            localObject3 = localObject2;
            localObject4 = localObject1;
          }
          else if (((RecyclerView.LayoutParams)localView.getLayoutParams()).isItemRemoved())
          {
            localObject3 = localObject2;
            localObject4 = localObject1;
            if (localObject1 == null)
            {
              localObject4 = localView;
              localObject3 = localObject2;
            }
          }
          else
          {
            if ((mOrientationHelper.getDecoratedStart(localView) < k) && (mOrientationHelper.getDecoratedEnd(localView) >= j)) {
              return localView;
            }
            localObject3 = localObject2;
            localObject4 = localObject1;
            if (localObject2 == null)
            {
              localObject3 = localView;
              localObject4 = localObject1;
            }
          }
        }
      }
      paramInt1 += i;
      localObject2 = localObject3;
    }
    if (localObject2 != null) {
      return localObject2;
    }
    return localObject1;
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
    if (paramState.getItemCount() < 1) {
      return 0;
    }
    return getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1;
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (mOrientation == 0) {
      return mSpanCount;
    }
    if (paramState.getItemCount() < 1) {
      return 0;
    }
    return getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1;
  }
  
  int getSpaceForSpanRange(int paramInt1, int paramInt2)
  {
    if ((mOrientation == 1) && (isLayoutRTL())) {
      return mCachedBorders[(mSpanCount - paramInt1)] - mCachedBorders[(mSpanCount - paramInt1 - paramInt2)];
    }
    return mCachedBorders[(paramInt2 + paramInt1)] - mCachedBorders[paramInt1];
  }
  
  public int getSpanCount()
  {
    return mSpanCount;
  }
  
  public SpanSizeLookup getSpanSizeLookup()
  {
    return mSpanSizeLookup;
  }
  
  public boolean isUsingSpansToEstimateScrollbarDimensions()
  {
    return mUsingSpansToEstimateScrollBarDimensions;
  }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, LinearLayoutManager.LayoutChunkResult paramLayoutChunkResult)
  {
    int i3 = mOrientationHelper.getModeInOther();
    int j;
    if (i3 != 1073741824) {
      j = 1;
    } else {
      j = 0;
    }
    int m;
    if (getChildCount() > 0) {
      m = mCachedBorders[mSpanCount];
    } else {
      m = 0;
    }
    if (j != 0) {
      updateMeasurements();
    }
    boolean bool;
    if (mItemDirection == 1) {
      bool = true;
    } else {
      bool = false;
    }
    int i = mSpanCount;
    if (!bool) {
      i = getSpanIndex(paramRecycler, paramState, mCurrentPosition) + getSpanSize(paramRecycler, paramState, mCurrentPosition);
    }
    int n = 0;
    Object localObject;
    while ((n < mSpanCount) && (paramLayoutState.hasMore(paramState)) && (i > 0))
    {
      k = mCurrentPosition;
      i1 = getSpanSize(paramRecycler, paramState, k);
      if (i1 <= mSpanCount)
      {
        i -= i1;
        if (i >= 0)
        {
          localObject = paramLayoutState.next(paramRecycler);
          if (localObject != null)
          {
            mSet[n] = localObject;
            n += 1;
          }
        }
      }
      else
      {
        paramRecycler = new StringBuilder();
        paramRecycler.append("Item at position ");
        paramRecycler.append(k);
        paramRecycler.append(" requires ");
        paramRecycler.append(i1);
        paramRecycler.append(" spans but GridLayoutManager has only ");
        paramRecycler.append(mSpanCount);
        paramRecycler.append(" spans.");
        throw new IllegalArgumentException(paramRecycler.toString());
      }
    }
    if (n == 0)
    {
      mFinished = true;
      return;
    }
    float f1 = 0.0F;
    assignSpans(paramRecycler, paramState, n, bool);
    int k = 0;
    i = 0;
    int i2;
    while (k < n)
    {
      paramRecycler = mSet[k];
      if (mScrapList == null)
      {
        if (bool) {
          addView(paramRecycler);
        } else {
          addView(paramRecycler, 0);
        }
      }
      else if (bool) {
        addDisappearingView(paramRecycler);
      } else {
        addDisappearingView(paramRecycler, 0);
      }
      calculateItemDecorationsForChild(paramRecycler, mDecorInsets);
      measureChild(paramRecycler, i3, false);
      i2 = mOrientationHelper.getDecoratedMeasurement(paramRecycler);
      i1 = i;
      if (i2 > i) {
        i1 = i2;
      }
      paramState = (LayoutParams)paramRecycler.getLayoutParams();
      float f3 = mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler) * 1.0F / mSpanSize;
      float f2 = f1;
      if (f3 > f1) {
        f2 = f3;
      }
      k += 1;
      i = i1;
      f1 = f2;
    }
    k = i;
    if (j != 0)
    {
      guessMeasurement(f1, m);
      j = 0;
      for (i = 0;; i = k)
      {
        k = i;
        if (j >= n) {
          break;
        }
        paramRecycler = mSet[j];
        measureChild(paramRecycler, 1073741824, true);
        m = mOrientationHelper.getDecoratedMeasurement(paramRecycler);
        k = i;
        if (m > i) {
          k = m;
        }
        j += 1;
      }
    }
    i = 0;
    while (i < n)
    {
      paramRecycler = mSet[i];
      if (mOrientationHelper.getDecoratedMeasurement(paramRecycler) != k)
      {
        paramState = (LayoutParams)paramRecycler.getLayoutParams();
        localObject = mDecorInsets;
        j = top + bottom + topMargin + bottomMargin;
        m = left + right + leftMargin + rightMargin;
        i1 = getSpaceForSpanRange(mSpanIndex, mSpanSize);
        if (mOrientation == 1)
        {
          m = RecyclerView.LayoutManager.getChildMeasureSpec(i1, 1073741824, m, width, false);
          j = View.MeasureSpec.makeMeasureSpec(k - j, 1073741824);
        }
        else
        {
          m = View.MeasureSpec.makeMeasureSpec(k - m, 1073741824);
          j = RecyclerView.LayoutManager.getChildMeasureSpec(i1, 1073741824, j, height, false);
        }
        measureChildWithDecorationsAndMargin(paramRecycler, m, j, true);
      }
      i += 1;
    }
    int i1 = 0;
    mConsumed = k;
    if (mOrientation == 1)
    {
      if (mLayoutDirection == -1)
      {
        i = mOffset;
        j = i - k;
      }
      for (;;)
      {
        m = 0;
        k = 0;
        break;
        j = mOffset;
        i = j + k;
      }
    }
    if (mLayoutDirection == -1)
    {
      m = mOffset;
      i2 = m - k;
      j = 0;
      i = 0;
      k = m;
      m = i2;
    }
    else
    {
      m = mOffset;
      k = m + k;
      j = 0;
      i = 0;
    }
    while (i1 < n)
    {
      paramRecycler = mSet[i1];
      paramState = (LayoutParams)paramRecycler.getLayoutParams();
      if (mOrientation == 1)
      {
        if (isLayoutRTL())
        {
          k = getPaddingLeft() + mCachedBorders[(mSpanCount - mSpanIndex)];
          m = k - mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler);
        }
        else
        {
          m = getPaddingLeft() + mCachedBorders[mSpanIndex];
          k = mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler) + m;
        }
      }
      else
      {
        j = getPaddingTop() + mCachedBorders[mSpanIndex];
        i = mOrientationHelper.getDecoratedMeasurementInOther(paramRecycler) + j;
      }
      layoutDecoratedWithMargins(paramRecycler, m, j, k, i);
      if ((paramState.isItemRemoved()) || (paramState.isItemChanged())) {
        mIgnoreConsumed = true;
      }
      mFocusable |= paramRecycler.hasFocusable();
      i1 += 1;
    }
    Arrays.fill(mSet, null);
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    super.onAnchorReady(paramRecycler, paramState, paramAnchorInfo, paramInt);
    updateMeasurements();
    if ((paramState.getItemCount() > 0) && (!paramState.isPreLayout())) {
      ensureAnchorIsInCorrectSpan(paramRecycler, paramState, paramAnchorInfo, paramInt);
    }
    ensureViewSet();
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    View localView = findContainingItemView(paramView);
    Object localObject1 = null;
    if (localView == null) {
      return null;
    }
    Object localObject2 = (LayoutParams)localView.getLayoutParams();
    int i4 = mSpanIndex;
    int i5 = mSpanIndex + mSpanSize;
    if (super.onFocusSearchFailed(paramView, paramInt, paramRecycler, paramState) == null) {
      return null;
    }
    int i10;
    if (convertFocusDirectionToLayoutDirection(paramInt) == 1) {
      i10 = 1;
    } else {
      i10 = 0;
    }
    if (i10 != mShouldReverseLayout) {
      paramInt = 1;
    } else {
      paramInt = 0;
    }
    int j;
    int k;
    if (paramInt != 0)
    {
      paramInt = getChildCount() - 1;
      j = -1;
      k = -1;
    }
    else
    {
      j = getChildCount();
      paramInt = 0;
      k = 1;
    }
    int m;
    if ((mOrientation == 1) && (isLayoutRTL())) {
      m = 1;
    } else {
      m = 0;
    }
    int i6 = getSpanGroupIndex(paramRecycler, paramState, paramInt);
    localObject2 = null;
    int i2 = -1;
    int i1 = 0;
    int i = 0;
    int i3 = -1;
    int n = paramInt;
    paramInt = i3;
    paramView = localObject1;
    localObject1 = localObject2;
    while (n != j)
    {
      i3 = getSpanGroupIndex(paramRecycler, paramState, n);
      localObject2 = getChildAt(n);
      if (localObject2 == localView) {
        break;
      }
      if ((((View)localObject2).hasFocusable()) && (i3 != i6))
      {
        if (paramView != null) {
          break;
        }
      }
      else
      {
        LayoutParams localLayoutParams = (LayoutParams)((View)localObject2).getLayoutParams();
        int i7 = mSpanIndex;
        int i8 = mSpanIndex + mSpanSize;
        if ((((View)localObject2).hasFocusable()) && (i7 == i4) && (i8 == i5)) {
          return localObject2;
        }
        if (((((View)localObject2).hasFocusable()) && (paramView == null)) || ((!((View)localObject2).hasFocusable()) && (localObject1 == null))) {}
        do
        {
          int i9;
          do
          {
            for (;;)
            {
              i3 = 1;
              break label464;
              i3 = Math.max(i7, i4);
              i9 = Math.min(i8, i5) - i3;
              if (!((View)localObject2).hasFocusable()) {
                break;
              }
              if (i9 <= i1)
              {
                if (i9 != i1) {
                  break label461;
                }
                if (i7 > i2) {
                  i3 = 1;
                } else {
                  i3 = 0;
                }
                if (m != i3) {
                  break label461;
                }
              }
            }
            if (paramView != null) {
              break;
            }
            i3 = 0;
            if (!isViewPartiallyVisible((View)localObject2, false, true)) {
              break;
            }
          } while (i9 > i);
          if (i9 != i) {
            break;
          }
          if (i7 > paramInt) {
            i3 = 1;
          }
        } while (m == i3);
        label461:
        i3 = 0;
        label464:
        if (i3 != 0) {
          if (((View)localObject2).hasFocusable())
          {
            i2 = mSpanIndex;
            i1 = Math.min(i8, i5) - Math.max(i7, i4);
            paramView = (View)localObject2;
          }
          else
          {
            paramInt = mSpanIndex;
            i = Math.min(i8, i5);
            i3 = Math.max(i7, i4);
            localObject1 = localObject2;
            i -= i3;
          }
        }
      }
      n += k;
    }
    if (paramView != null) {
      return paramView;
    }
    return localObject1;
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    if (!(localLayoutParams instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    paramView = (LayoutParams)localLayoutParams;
    int i = getSpanGroupIndex(paramRecycler, paramState, paramView.getViewLayoutPosition());
    if (mOrientation == 0)
    {
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(paramView.getSpanIndex(), paramView.getSpanSize(), i, 1, false, false));
      return;
    }
    paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, paramView.getSpanIndex(), paramView.getSpanSize(), false, false));
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    mSpanSizeLookup.invalidateSpanIndexCache();
    mSpanSizeLookup.invalidateSpanGroupIndexCache();
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    mSpanSizeLookup.invalidateSpanIndexCache();
    mSpanSizeLookup.invalidateSpanGroupIndexCache();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    mSpanSizeLookup.invalidateSpanIndexCache();
    mSpanSizeLookup.invalidateSpanGroupIndexCache();
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    mSpanSizeLookup.invalidateSpanIndexCache();
    mSpanSizeLookup.invalidateSpanGroupIndexCache();
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    mSpanSizeLookup.invalidateSpanIndexCache();
    mSpanSizeLookup.invalidateSpanGroupIndexCache();
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (paramState.isPreLayout()) {
      cachePreLayoutSpanMapping();
    }
    super.onLayoutChildren(paramRecycler, paramState);
    clearPreLayoutSpanMappingCache();
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    mPendingSpanCountChange = false;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollHorizontallyBy(paramInt, paramRecycler, paramState);
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollVerticallyBy(paramInt, paramRecycler, paramState);
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    if (mCachedBorders == null) {
      super.setMeasuredDimension(paramRect, paramInt1, paramInt2);
    }
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    if (mOrientation == 1)
    {
      paramInt2 = RecyclerView.LayoutManager.chooseSize(paramInt2, paramRect.height() + j, getMinimumHeight());
      paramInt1 = RecyclerView.LayoutManager.chooseSize(paramInt1, mCachedBorders[(mCachedBorders.length - 1)] + i, getMinimumWidth());
    }
    else
    {
      paramInt1 = RecyclerView.LayoutManager.chooseSize(paramInt1, paramRect.width() + i, getMinimumWidth());
      paramInt2 = RecyclerView.LayoutManager.chooseSize(paramInt2, mCachedBorders[(mCachedBorders.length - 1)] + j, getMinimumHeight());
    }
    setMeasuredDimension(paramInt1, paramInt2);
  }
  
  public void setSpanCount(int paramInt)
  {
    if (paramInt == mSpanCount) {
      return;
    }
    mPendingSpanCountChange = true;
    if (paramInt >= 1)
    {
      mSpanCount = paramInt;
      mSpanSizeLookup.invalidateSpanIndexCache();
      requestLayout();
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Span count should be at least 1. Provided ");
    localStringBuilder.append(paramInt);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public void setSpanSizeLookup(SpanSizeLookup paramSpanSizeLookup)
  {
    mSpanSizeLookup = paramSpanSizeLookup;
  }
  
  public void setStackFromEnd(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      super.setStackFromEnd(false);
      return;
    }
    throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
  }
  
  public void setUsingSpansToEstimateScrollbarDimensions(boolean paramBoolean)
  {
    mUsingSpansToEstimateScrollBarDimensions = paramBoolean;
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return (mPendingSavedState == null) && (!mPendingSpanCountChange);
  }
  
  public static final class DefaultSpanSizeLookup
    extends GridLayoutManager.SpanSizeLookup
  {
    public DefaultSpanSizeLookup() {}
    
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      return paramInt1 % paramInt2;
    }
    
    public int getSpanSize(int paramInt)
    {
      return 1;
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    int mSpanIndex = -1;
    int mSpanSize = 0;
    
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
    
    public int getSpanIndex()
    {
      return mSpanIndex;
    }
    
    public int getSpanSize()
    {
      return mSpanSize;
    }
  }
  
  public static abstract class SpanSizeLookup
  {
    private boolean mCacheSpanGroupIndices = false;
    private boolean mCacheSpanIndices = false;
    final SparseIntArray mSpanGroupIndexCache = new SparseIntArray();
    final SparseIntArray mSpanIndexCache = new SparseIntArray();
    
    public SpanSizeLookup() {}
    
    static int findFirstKeyLessThan(SparseIntArray paramSparseIntArray, int paramInt)
    {
      int j = paramSparseIntArray.size() - 1;
      int i = 0;
      while (i <= j)
      {
        int k = i + j >>> 1;
        if (paramSparseIntArray.keyAt(k) < paramInt) {
          i = k + 1;
        } else {
          j = k - 1;
        }
      }
      paramInt = i - 1;
      if ((paramInt >= 0) && (paramInt < paramSparseIntArray.size())) {
        return paramSparseIntArray.keyAt(paramInt);
      }
      return -1;
    }
    
    int getCachedSpanGroupIndex(int paramInt1, int paramInt2)
    {
      if (!mCacheSpanGroupIndices) {
        return getSpanGroupIndex(paramInt1, paramInt2);
      }
      int i = mSpanGroupIndexCache.get(paramInt1, -1);
      if (i != -1) {
        return i;
      }
      paramInt2 = getSpanGroupIndex(paramInt1, paramInt2);
      mSpanGroupIndexCache.put(paramInt1, paramInt2);
      return paramInt2;
    }
    
    int getCachedSpanIndex(int paramInt1, int paramInt2)
    {
      if (!mCacheSpanIndices) {
        return getSpanIndex(paramInt1, paramInt2);
      }
      int i = mSpanIndexCache.get(paramInt1, -1);
      if (i != -1) {
        return i;
      }
      paramInt2 = getSpanIndex(paramInt1, paramInt2);
      mSpanIndexCache.put(paramInt1, paramInt2);
      return paramInt2;
    }
    
    public int getSpanGroupIndex(int paramInt1, int paramInt2)
    {
      int n;
      int i1;
      if (mCacheSpanGroupIndices)
      {
        j = findFirstKeyLessThan(mSpanGroupIndexCache, paramInt1);
        if (j != -1)
        {
          n = mSpanGroupIndexCache.get(j);
          i = n;
          m = j + 1;
          k = getCachedSpanIndex(j, paramInt2);
          i1 = getSpanSize(j) + k;
          k = m;
          j = i1;
          if (i1 != paramInt2) {
            break label100;
          }
          i = n + 1;
          j = 0;
          k = m;
          break label100;
        }
      }
      int j = 0;
      int i = 0;
      int k = 0;
      label100:
      int i2 = getSpanSize(paramInt1);
      int m = i;
      while (k < paramInt1)
      {
        n = getSpanSize(k);
        i1 = j + n;
        if (i1 == paramInt2)
        {
          j = m + 1;
          i = 0;
        }
        else
        {
          j = m;
          i = i1;
          if (i1 > paramInt2)
          {
            j = m + 1;
            i = n;
          }
        }
        k += 1;
        m = j;
        j = i;
      }
      if (j + i2 > paramInt2) {
        return m + 1;
      }
      return m;
    }
    
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      int n = getSpanSize(paramInt1);
      if (n == paramInt2) {
        return 0;
      }
      if (mCacheSpanIndices)
      {
        j = findFirstKeyLessThan(mSpanIndexCache, paramInt1);
        if (j >= 0)
        {
          i = mSpanIndexCache.get(j) + getSpanSize(j);
          j += 1;
          break label68;
        }
      }
      int j = 0;
      int i = 0;
      label68:
      while (j < paramInt1)
      {
        int k = getSpanSize(j);
        int m = i + k;
        if (m == paramInt2)
        {
          i = 0;
        }
        else
        {
          i = m;
          if (m > paramInt2) {
            i = k;
          }
        }
        j += 1;
      }
      if (n + i <= paramInt2) {
        return i;
      }
      return 0;
    }
    
    public abstract int getSpanSize(int paramInt);
    
    public void invalidateSpanGroupIndexCache()
    {
      mSpanGroupIndexCache.clear();
    }
    
    public void invalidateSpanIndexCache()
    {
      mSpanIndexCache.clear();
    }
    
    public boolean isSpanGroupIndexCacheEnabled()
    {
      return mCacheSpanGroupIndices;
    }
    
    public boolean isSpanIndexCacheEnabled()
    {
      return mCacheSpanIndices;
    }
    
    public void setSpanGroupIndexCacheEnabled(boolean paramBoolean)
    {
      if (!paramBoolean) {
        mSpanGroupIndexCache.clear();
      }
      mCacheSpanGroupIndices = paramBoolean;
    }
    
    public void setSpanIndexCacheEnabled(boolean paramBoolean)
    {
      if (!paramBoolean) {
        mSpanGroupIndexCache.clear();
      }
      mCacheSpanIndices = paramBoolean;
    }
  }
}
