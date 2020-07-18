package com.facebook.react.views.scroll;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;
import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.uimanager.MeasureSpecAssertions;
import com.facebook.react.uimanager.ReactClippingViewGroup;
import com.facebook.react.uimanager.ReactClippingViewGroupHelper;
import com.facebook.react.uimanager.events.NativeGestureUtil;
import com.facebook.react.views.view.ReactViewBackgroundManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ReactHorizontalScrollView
  extends HorizontalScrollView
  implements ReactClippingViewGroup
{
  @Nullable
  private static Field sScrollerField;
  private static boolean sTriedToGetScrollerField;
  private boolean mActivelyScrolling;
  @Nullable
  private Rect mClippingRect;
  private float mDecelerationRate = 0.985F;
  private boolean mDisableIntervalMomentum = false;
  private boolean mDragging;
  @Nullable
  private Drawable mEndBackground;
  private int mEndFillColor = 0;
  @Nullable
  private FpsListener mFpsListener = null;
  private final OnScrollDispatchHelper mOnScrollDispatchHelper = new OnScrollDispatchHelper();
  @Nullable
  private String mOverflow = "hidden";
  private boolean mPagedArrowScrolling = false;
  private boolean mPagingEnabled = false;
  @Nullable
  private Runnable mPostTouchRunnable;
  private ReactViewBackgroundManager mReactBackgroundManager = new ReactViewBackgroundManager(this);
  private final Rect mRect = new Rect();
  private boolean mRemoveClippedSubviews;
  private boolean mScrollEnabled = true;
  @Nullable
  private String mScrollPerfTag;
  @Nullable
  private final OverScroller mScroller;
  private boolean mSendMomentumEvents;
  private int mSnapInterval = 0;
  @Nullable
  private List<Integer> mSnapOffsets;
  private boolean mSnapToEnd = true;
  private boolean mSnapToStart = true;
  private final Rect mTempRect = new Rect();
  private final VelocityHelper mVelocityHelper = new VelocityHelper();
  
  public ReactHorizontalScrollView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ReactHorizontalScrollView(Context paramContext, FpsListener paramFpsListener)
  {
    super(paramContext);
    mFpsListener = paramFpsListener;
    mScroller = getOverScrollerFromParent();
  }
  
  private void disableFpsListener()
  {
    if (isScrollPerfLoggingEnabled())
    {
      Assertions.assertNotNull(mFpsListener);
      Assertions.assertNotNull(mScrollPerfTag);
      mFpsListener.disable(mScrollPerfTag);
    }
  }
  
  private void enableFpsListener()
  {
    if (isScrollPerfLoggingEnabled())
    {
      Assertions.assertNotNull(mFpsListener);
      Assertions.assertNotNull(mScrollPerfTag);
      mFpsListener.enable(mScrollPerfTag);
    }
  }
  
  private void flingAndSnap(int paramInt)
  {
    if (getChildCount() <= 0) {
      return;
    }
    if ((mSnapInterval == 0) && (mSnapOffsets == null))
    {
      smoothScrollAndSnap(paramInt);
      return;
    }
    int i2 = Math.max(0, computeHorizontalScrollRange() - getWidth());
    int i = predictFinalScrollPosition(paramInt);
    if (mDisableIntervalMomentum) {
      i = getScrollX();
    }
    int i6 = getWidth();
    int i7 = ViewCompat.getPaddingStart(this);
    int i8 = ViewCompat.getPaddingEnd(this);
    int i3;
    if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == 1) {
      i3 = 1;
    } else {
      i3 = 0;
    }
    int j = i;
    int k = paramInt;
    if (i3 != 0)
    {
      j = i2 - i;
      k = -paramInt;
    }
    int m;
    int i1;
    int n;
    if (mSnapOffsets != null)
    {
      i5 = ((Integer)mSnapOffsets.get(0)).intValue();
      i4 = ((Integer)mSnapOffsets.get(mSnapOffsets.size() - 1)).intValue();
      paramInt = i2;
      m = 0;
      for (i = 0; m < mSnapOffsets.size(); i = n)
      {
        i1 = ((Integer)mSnapOffsets.get(m)).intValue();
        n = i;
        if (i1 <= j)
        {
          n = i;
          if (j - i1 < j - i) {
            n = i1;
          }
        }
        i = paramInt;
        if (i1 >= j)
        {
          i = paramInt;
          if (i1 - j < paramInt - j) {
            i = i1;
          }
        }
        m += 1;
        paramInt = i;
      }
      n = i5;
      m = i4;
    }
    else
    {
      double d1 = getSnapInterval();
      double d2 = j / d1;
      double d3 = Math.floor(d2);
      paramInt = Math.min((int)(Math.ceil(d2) * d1), i2);
      m = i2;
      i = (int)(d3 * d1);
      n = 0;
    }
    int i9 = j - i;
    int i10 = paramInt - j;
    if (i9 < i10) {
      i1 = i;
    } else {
      i1 = paramInt;
    }
    int i5 = getScrollX();
    int i4 = i5;
    if (i3 != 0) {
      i4 = i2 - i5;
    }
    if ((!mSnapToEnd) && (j >= m))
    {
      if (i4 < m)
      {
        paramInt = m;
        j = k;
        break label533;
      }
    }
    else
    {
      if ((mSnapToStart) || (j > n)) {
        break label484;
      }
      if (i4 > n) {
        break label474;
      }
    }
    paramInt = j;
    j = k;
    break label533;
    label474:
    paramInt = n;
    j = k;
    break label533;
    label484:
    if (k > 0)
    {
      j = k + (int)(i10 * 10.0D);
    }
    else
    {
      paramInt = i1;
      j = k;
      if (k < 0)
      {
        j = k - (int)(i9 * 10.0D);
        paramInt = i;
      }
    }
    label533:
    k = Math.min(Math.max(0, paramInt), i2);
    i = k;
    paramInt = j;
    if (i3 != 0)
    {
      i = i2 - k;
      paramInt = -j;
    }
    if (mScroller != null)
    {
      mActivelyScrolling = true;
      OverScroller localOverScroller = mScroller;
      k = getScrollX();
      m = getScrollY();
      if (paramInt == 0) {
        for (;;)
        {
          paramInt = i - getScrollX();
        }
      }
      if ((i != 0) && (i != i2)) {
        j = 0;
      } else {
        j = (i6 - i7 - i8) / 2;
      }
      localOverScroller.fling(k, m, paramInt, 0, i, i, 0, 0, j, 0);
      postInvalidateOnAnimation();
      return;
    }
    smoothScrollTo(i, getScrollY());
  }
  
  private OverScroller getOverScrollerFromParent()
  {
    if (!sTriedToGetScrollerField) {
      sTriedToGetScrollerField = true;
    }
    try
    {
      localObject = HorizontalScrollView.class.getDeclaredField("mScroller");
      sScrollerField = (Field)localObject;
      localObject = sScrollerField;
      ((Field)localObject).setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Object localObject;
      for (;;) {}
    }
    Log.w("ReactNative", "Failed to get mScroller field for HorizontalScrollView! This app will exhibit the bounce-back scrolling bug :(");
    if (sScrollerField != null)
    {
      localObject = sScrollerField;
      try
      {
        localObject = ((Field)localObject).get(this);
        if ((localObject instanceof OverScroller)) {
          return (OverScroller)localObject;
        }
        Log.w("ReactNative", "Failed to cast mScroller field in HorizontalScrollView (probably due to OEM changes to AOSP)! This app will exhibit the bounce-back scrolling bug :(");
        return null;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new RuntimeException("Failed to get mScroller from HorizontalScrollView!", localIllegalAccessException);
      }
    }
    return null;
  }
  
  private int getScrollDelta(View paramView)
  {
    paramView.getDrawingRect(mTempRect);
    offsetDescendantRectToMyCoords(paramView, mTempRect);
    return computeScrollDeltaToGetChildRectOnScreen(mTempRect);
  }
  
  private int getSnapInterval()
  {
    if (mSnapInterval != 0) {
      return mSnapInterval;
    }
    return getWidth();
  }
  
  private void handlePostTouchScrolling(int paramInt1, int paramInt2)
  {
    if ((!mSendMomentumEvents) && (!mPagingEnabled) && (!isScrollPerfLoggingEnabled())) {
      return;
    }
    if (mPostTouchRunnable != null) {
      return;
    }
    if (mSendMomentumEvents) {
      ReactScrollViewHelper.emitScrollMomentumBeginEvent(this, paramInt1, paramInt2);
    }
    mActivelyScrolling = false;
    mPostTouchRunnable = new Runnable()
    {
      private boolean mSnappingToPage = false;
      
      public void run()
      {
        if (mActivelyScrolling)
        {
          ReactHorizontalScrollView.access$002(ReactHorizontalScrollView.this, false);
          ViewCompat.postOnAnimationDelayed(ReactHorizontalScrollView.this, this, 20L);
          return;
        }
        if ((mPagingEnabled) && (!mSnappingToPage))
        {
          mSnappingToPage = true;
          ReactHorizontalScrollView.this.flingAndSnap(0);
          ViewCompat.postOnAnimationDelayed(ReactHorizontalScrollView.this, this, 20L);
          return;
        }
        if (mSendMomentumEvents) {
          ReactScrollViewHelper.emitScrollMomentumEndEvent(ReactHorizontalScrollView.this);
        }
        ReactHorizontalScrollView.access$402(ReactHorizontalScrollView.this, null);
        ReactHorizontalScrollView.this.disableFpsListener();
      }
    };
    ViewCompat.postOnAnimationDelayed(this, mPostTouchRunnable, 20L);
  }
  
  private boolean isMostlyScrolledInView(View paramView)
  {
    int i = getScrollDelta(paramView);
    paramView.getDrawingRect(mTempRect);
    return (i != 0) && (Math.abs(i) < mTempRect.width() / 2);
  }
  
  private boolean isPartiallyScrolledInView(View paramView)
  {
    int i = getScrollDelta(paramView);
    paramView.getDrawingRect(mTempRect);
    return (i != 0) && (Math.abs(i) < mTempRect.width());
  }
  
  private boolean isScrollPerfLoggingEnabled()
  {
    return (mFpsListener != null) && (mScrollPerfTag != null) && (!mScrollPerfTag.isEmpty());
  }
  
  private boolean isScrolledInView(View paramView)
  {
    return getScrollDelta(paramView) == 0;
  }
  
  private int predictFinalScrollPosition(int paramInt)
  {
    OverScroller localOverScroller = new OverScroller(getContext());
    localOverScroller.setFriction(1.0F - mDecelerationRate);
    int i = Math.max(0, computeHorizontalScrollRange() - getWidth());
    int j = getWidth();
    int k = ViewCompat.getPaddingStart(this);
    int m = ViewCompat.getPaddingEnd(this);
    localOverScroller.fling(getScrollX(), getScrollY(), paramInt, 0, 0, i, 0, 0, (j - k - m) / 2, 0);
    return localOverScroller.getFinalX();
  }
  
  private void scrollToChild(View paramView)
  {
    int i = getScrollDelta(paramView);
    if (i != 0) {
      scrollBy(i, 0);
    }
  }
  
  private void smoothScrollAndSnap(int paramInt)
  {
    double d2 = getSnapInterval();
    double d1 = getScrollX();
    double d3 = predictFinalScrollPosition(paramInt);
    double d4 = d1 / d2;
    int k = (int)Math.floor(d4);
    int n = (int)Math.ceil(d4);
    int m = (int)Math.round(d4);
    int i1 = (int)Math.round(d3 / d2);
    int j;
    int i;
    if ((paramInt > 0) && (n == k))
    {
      j = n + 1;
      i = k;
    }
    else
    {
      i = k;
      j = n;
      if (paramInt < 0)
      {
        i = k;
        j = n;
        if (k == n)
        {
          i = k - 1;
          j = n;
        }
      }
    }
    if ((paramInt > 0) && (m < j) && (i1 > i))
    {
      k = j;
    }
    else
    {
      k = m;
      if (paramInt < 0)
      {
        k = m;
        if (m > i)
        {
          k = m;
          if (i1 < j) {
            k = i;
          }
        }
      }
    }
    d2 = k * d2;
    if (d2 != d1)
    {
      mActivelyScrolling = true;
      smoothScrollTo((int)d2, getScrollY());
    }
  }
  
  private void smoothScrollToNextPage(int paramInt)
  {
    int k = getWidth();
    int m = getScrollX();
    int j = m / k;
    int i = j;
    if (m % k != 0) {
      i = j + 1;
    }
    if (paramInt == 17) {
      paramInt = i - 1;
    } else {
      paramInt = i + 1;
    }
    i = paramInt;
    if (paramInt < 0) {
      i = 0;
    }
    smoothScrollTo(i * k, getScrollY());
    handlePostTouchScrolling(0, 0);
  }
  
  public void addFocusables(ArrayList paramArrayList, int paramInt1, int paramInt2)
  {
    Object localObject;
    if ((mPagingEnabled) && (!mPagedArrowScrolling))
    {
      localObject = new ArrayList();
      super.addFocusables((ArrayList)localObject, paramInt1, paramInt2);
      localObject = ((ArrayList)localObject).iterator();
    }
    while (((Iterator)localObject).hasNext())
    {
      View localView = (View)((Iterator)localObject).next();
      if ((isScrolledInView(localView)) || (isPartiallyScrolledInView(localView)) || (localView.isFocused()))
      {
        paramArrayList.add(localView);
        continue;
        super.addFocusables(paramArrayList, paramInt1, paramInt2);
      }
    }
  }
  
  public boolean arrowScroll(int paramInt)
  {
    if (mPagingEnabled)
    {
      boolean bool = true;
      mPagedArrowScrolling = true;
      if (getChildCount() > 0)
      {
        View localView1 = findFocus();
        localView1 = FocusFinder.getInstance().findNextFocus(this, localView1, paramInt);
        View localView2 = getChildAt(0);
        if ((localView2 != null) && (localView1 != null) && (localView1.getParent() == localView2))
        {
          if ((!isScrolledInView(localView1)) && (!isMostlyScrolledInView(localView1))) {
            smoothScrollToNextPage(paramInt);
          }
          localView1.requestFocus();
        }
        else
        {
          smoothScrollToNextPage(paramInt);
        }
      }
      else
      {
        bool = false;
      }
      mPagedArrowScrolling = false;
      return bool;
    }
    return super.arrowScroll(paramInt);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (mEndFillColor != 0)
    {
      View localView = getChildAt(0);
      if ((mEndBackground != null) && (localView != null) && (localView.getRight() < getWidth()))
      {
        mEndBackground.setBounds(localView.getRight(), 0, getWidth(), getHeight());
        mEndBackground.draw(paramCanvas);
      }
    }
    super.draw(paramCanvas);
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getKeyCode();
    if ((!mScrollEnabled) && ((i == 21) || (i == 22))) {
      return false;
    }
    return super.executeKeyEvent(paramKeyEvent);
  }
  
  public void flashScrollIndicators()
  {
    awakenScrollBars();
  }
  
  public void fling(int paramInt)
  {
    paramInt = (int)(Math.abs(paramInt) * Math.signum(mOnScrollDispatchHelper.getXFlingVelocity()));
    if (mPagingEnabled)
    {
      flingAndSnap(paramInt);
    }
    else if (mScroller != null)
    {
      int i = getWidth();
      int j = ViewCompat.getPaddingStart(this);
      int k = ViewCompat.getPaddingEnd(this);
      mScroller.fling(getScrollX(), getScrollY(), paramInt, 0, 0, Integer.MAX_VALUE, 0, 0, (i - j - k) / 2, 0);
      ViewCompat.postInvalidateOnAnimation(this);
    }
    else
    {
      super.fling(paramInt);
    }
    handlePostTouchScrolling(paramInt, 0);
  }
  
  public void getClippingRect(Rect paramRect)
  {
    paramRect.set((Rect)Assertions.assertNotNull(mClippingRect));
  }
  
  public boolean getRemoveClippedSubviews()
  {
    return mRemoveClippedSubviews;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (mRemoveClippedSubviews) {
      updateClippingRect();
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    getDrawingRect(mRect);
    String str = mOverflow;
    int i;
    if ((str.hashCode() == 466743410) && (str.equals("visible"))) {
      i = 0;
    } else {
      i = -1;
    }
    if (i != 0) {
      paramCanvas.clipRect(mRect);
    }
    super.onDraw(paramCanvas);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!mScrollEnabled) {
      return false;
    }
    try
    {
      boolean bool = super.onInterceptTouchEvent(paramMotionEvent);
      if (bool)
      {
        NativeGestureUtil.notifyNativeGestureStarted(this, paramMotionEvent);
        ReactScrollViewHelper.emitScrollBeginDragEvent(this);
        mDragging = true;
        enableFpsListener();
        return true;
      }
    }
    catch (IllegalArgumentException paramMotionEvent)
    {
      Log.w("ReactNative", "Error intercepting touch event.", paramMotionEvent);
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    scrollTo(getScrollX(), getScrollY());
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    MeasureSpecAssertions.assertExplicitMeasureSpec(paramInt1, paramInt2);
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt2));
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramInt1;
    if (mScroller != null)
    {
      i = paramInt1;
      if (!mScroller.isFinished())
      {
        i = paramInt1;
        if (mScroller.getCurrX() != mScroller.getFinalX())
        {
          int j = computeHorizontalScrollRange() - getWidth();
          i = paramInt1;
          if (paramInt1 >= j)
          {
            mScroller.abortAnimation();
            i = j;
          }
        }
      }
    }
    super.onOverScrolled(i, paramInt2, paramBoolean1, paramBoolean2);
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    mActivelyScrolling = true;
    if (mOnScrollDispatchHelper.onScrollChanged(paramInt1, paramInt2))
    {
      if (mRemoveClippedSubviews) {
        updateClippingRect();
      }
      ReactScrollViewHelper.emitScrollEvent(this, mOnScrollDispatchHelper.getXFlingVelocity(), mOnScrollDispatchHelper.getYFlingVelocity());
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (mRemoveClippedSubviews) {
      updateClippingRect();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!mScrollEnabled) {
      return false;
    }
    mVelocityHelper.calculateVelocity(paramMotionEvent);
    if (((paramMotionEvent.getAction() & 0xFF) == 1) && (mDragging))
    {
      float f1 = mVelocityHelper.getXVelocity();
      float f2 = mVelocityHelper.getYVelocity();
      ReactScrollViewHelper.emitScrollEndDragEvent(this, f1, f2);
      mDragging = false;
      handlePostTouchScrolling(Math.round(f1), Math.round(f2));
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public boolean pageScroll(int paramInt)
  {
    boolean bool = super.pageScroll(paramInt);
    if ((mPagingEnabled) && (bool)) {
      handlePostTouchScrolling(0, 0);
    }
    return bool;
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if ((paramView2 != null) && (!mPagingEnabled)) {
      scrollToChild(paramView2);
    }
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public void setBackgroundColor(int paramInt)
  {
    mReactBackgroundManager.setBackgroundColor(paramInt);
  }
  
  public void setBorderColor(int paramInt, float paramFloat1, float paramFloat2)
  {
    mReactBackgroundManager.setBorderColor(paramInt, paramFloat1, paramFloat2);
  }
  
  public void setBorderRadius(float paramFloat)
  {
    mReactBackgroundManager.setBorderRadius(paramFloat);
  }
  
  public void setBorderRadius(float paramFloat, int paramInt)
  {
    mReactBackgroundManager.setBorderRadius(paramFloat, paramInt);
  }
  
  public void setBorderStyle(String paramString)
  {
    mReactBackgroundManager.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(int paramInt, float paramFloat)
  {
    mReactBackgroundManager.setBorderWidth(paramInt, paramFloat);
  }
  
  public void setDecelerationRate(float paramFloat)
  {
    mDecelerationRate = paramFloat;
    if (mScroller != null) {
      mScroller.setFriction(1.0F - mDecelerationRate);
    }
  }
  
  public void setDisableIntervalMomentum(boolean paramBoolean)
  {
    mDisableIntervalMomentum = paramBoolean;
  }
  
  public void setEndFillColor(int paramInt)
  {
    if (paramInt != mEndFillColor)
    {
      mEndFillColor = paramInt;
      mEndBackground = new ColorDrawable(mEndFillColor);
    }
  }
  
  public void setOverflow(String paramString)
  {
    mOverflow = paramString;
    invalidate();
  }
  
  public void setPagingEnabled(boolean paramBoolean)
  {
    mPagingEnabled = paramBoolean;
  }
  
  public void setRemoveClippedSubviews(boolean paramBoolean)
  {
    if ((paramBoolean) && (mClippingRect == null)) {
      mClippingRect = new Rect();
    }
    mRemoveClippedSubviews = paramBoolean;
    updateClippingRect();
  }
  
  public void setScrollEnabled(boolean paramBoolean)
  {
    mScrollEnabled = paramBoolean;
  }
  
  public void setScrollPerfTag(String paramString)
  {
    mScrollPerfTag = paramString;
  }
  
  public void setSendMomentumEvents(boolean paramBoolean)
  {
    mSendMomentumEvents = paramBoolean;
  }
  
  public void setSnapInterval(int paramInt)
  {
    mSnapInterval = paramInt;
  }
  
  public void setSnapOffsets(List paramList)
  {
    mSnapOffsets = paramList;
  }
  
  public void setSnapToEnd(boolean paramBoolean)
  {
    mSnapToEnd = paramBoolean;
  }
  
  public void setSnapToStart(boolean paramBoolean)
  {
    mSnapToStart = paramBoolean;
  }
  
  public void updateClippingRect()
  {
    if (!mRemoveClippedSubviews) {
      return;
    }
    Assertions.assertNotNull(mClippingRect);
    ReactClippingViewGroupHelper.calculateClippingRect(this, mClippingRect);
    View localView = getChildAt(0);
    if ((localView instanceof ReactClippingViewGroup)) {
      ((ReactClippingViewGroup)localView).updateClippingRect();
    }
  }
}
