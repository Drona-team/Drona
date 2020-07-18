package com.google.android.exoplayer2.pc;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityRecord;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArraySet;

public class DefaultTimeBar
  extends View
  implements TimeBar
{
  private static final String ACCESSIBILITY_CLASS_NAME = "android.widget.SeekBar";
  public static final int DEFAULT_AD_MARKER_COLOR = -1291845888;
  public static final int DEFAULT_AD_MARKER_WIDTH_DP = 4;
  public static final int DEFAULT_BAR_HEIGHT_DP = 4;
  private static final int DEFAULT_INCREMENT_COUNT = 20;
  public static final int DEFAULT_PLAYED_COLOR = -1;
  public static final int DEFAULT_SCRUBBER_DISABLED_SIZE_DP = 0;
  public static final int DEFAULT_SCRUBBER_DRAGGED_SIZE_DP = 16;
  public static final int DEFAULT_SCRUBBER_ENABLED_SIZE_DP = 12;
  public static final int DEFAULT_TOUCH_TARGET_HEIGHT_DP = 26;
  private static final int FINE_SCRUB_RATIO = 3;
  private static final int FINE_SCRUB_Y_THRESHOLD_DP = -50;
  private static final long STOP_SCRUBBING_TIMEOUT_MS = 1000L;
  private int adGroupCount;
  @Nullable
  private long[] adGroupTimesMs;
  private final Paint adMarkerPaint = new Paint();
  private final int adMarkerWidth;
  private final int barHeight;
  private final Rect bufferedBar = new Rect();
  private final Paint bufferedPaint = new Paint();
  private long bufferedPosition;
  private long duration;
  private final int fineScrubYThreshold;
  private final StringBuilder formatBuilder;
  private final Formatter formatter;
  private int keyCountIncrement;
  private long keyTimeIncrement;
  private int lastCoarseScrubXPosition;
  private final CopyOnWriteArraySet<com.google.android.exoplayer2.ui.TimeBar.OnScrubListener> listeners;
  private final int[] locationOnScreen;
  @Nullable
  private boolean[] playedAdGroups;
  private final Paint playedAdMarkerPaint = new Paint();
  private final Paint playedPaint = new Paint();
  private long position;
  private final Rect progressBar = new Rect();
  private long scrubPosition;
  private final Rect scrubberBar = new Rect();
  private final int scrubberDisabledSize;
  private final int scrubberDraggedSize;
  @Nullable
  private final Drawable scrubberDrawable;
  private final int scrubberEnabledSize;
  private final int scrubberPadding;
  private final Paint scrubberPaint = new Paint();
  private boolean scrubbing;
  private final Rect seekBounds = new Rect();
  private final Runnable stopScrubbingRunnable;
  private final Point touchPosition;
  private final int touchTargetHeight;
  private final Paint unplayedPaint = new Paint();
  
  public DefaultTimeBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    scrubberPaint.setAntiAlias(true);
    listeners = new CopyOnWriteArraySet();
    locationOnScreen = new int[2];
    touchPosition = new Point();
    DisplayMetrics localDisplayMetrics = paramContext.getResources().getDisplayMetrics();
    fineScrubYThreshold = dpToPx(localDisplayMetrics, -50);
    int k = dpToPx(localDisplayMetrics, 4);
    int j = dpToPx(localDisplayMetrics, 26);
    int i = j;
    int m = dpToPx(localDisplayMetrics, 4);
    int n = dpToPx(localDisplayMetrics, 12);
    int i1 = dpToPx(localDisplayMetrics, 0);
    int i2 = dpToPx(localDisplayMetrics, 16);
    if (paramAttributeSet != null)
    {
      paramContext = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.DefaultTimeBar, 0, 0);
      try
      {
        scrubberDrawable = paramContext.getDrawable(R.styleable.DefaultTimeBar_scrubber_drawable);
        paramAttributeSet = scrubberDrawable;
        if (paramAttributeSet != null)
        {
          setDrawableLayoutDirection(scrubberDrawable);
          i = Math.max(scrubberDrawable.getMinimumHeight(), j);
        }
        barHeight = paramContext.getDimensionPixelSize(R.styleable.DefaultTimeBar_bar_height, k);
        touchTargetHeight = paramContext.getDimensionPixelSize(R.styleable.DefaultTimeBar_touch_target_height, i);
        adMarkerWidth = paramContext.getDimensionPixelSize(R.styleable.DefaultTimeBar_ad_marker_width, m);
        scrubberEnabledSize = paramContext.getDimensionPixelSize(R.styleable.DefaultTimeBar_scrubber_enabled_size, n);
        scrubberDisabledSize = paramContext.getDimensionPixelSize(R.styleable.DefaultTimeBar_scrubber_disabled_size, i1);
        scrubberDraggedSize = paramContext.getDimensionPixelSize(R.styleable.DefaultTimeBar_scrubber_dragged_size, i2);
        i = paramContext.getInt(R.styleable.DefaultTimeBar_played_color, -1);
        j = paramContext.getInt(R.styleable.DefaultTimeBar_scrubber_color, getDefaultScrubberColor(i));
        k = paramContext.getInt(R.styleable.DefaultTimeBar_buffered_color, getDefaultBufferedColor(i));
        m = paramContext.getInt(R.styleable.DefaultTimeBar_unplayed_color, getDefaultUnplayedColor(i));
        n = paramContext.getInt(R.styleable.DefaultTimeBar_ad_marker_color, -1291845888);
        i1 = paramContext.getInt(R.styleable.DefaultTimeBar_played_ad_marker_color, getDefaultPlayedAdMarkerColor(n));
        playedPaint.setColor(i);
        scrubberPaint.setColor(j);
        bufferedPaint.setColor(k);
        unplayedPaint.setColor(m);
        adMarkerPaint.setColor(n);
        playedAdMarkerPaint.setColor(i1);
        paramContext.recycle();
      }
      catch (Throwable paramAttributeSet)
      {
        paramContext.recycle();
        throw paramAttributeSet;
      }
    }
    barHeight = k;
    touchTargetHeight = j;
    adMarkerWidth = m;
    scrubberEnabledSize = n;
    scrubberDisabledSize = i1;
    scrubberDraggedSize = i2;
    playedPaint.setColor(-1);
    scrubberPaint.setColor(getDefaultScrubberColor(-1));
    bufferedPaint.setColor(getDefaultBufferedColor(-1));
    unplayedPaint.setColor(getDefaultUnplayedColor(-1));
    adMarkerPaint.setColor(-1291845888);
    scrubberDrawable = null;
    formatBuilder = new StringBuilder();
    formatter = new Formatter(formatBuilder, Locale.getDefault());
    stopScrubbingRunnable = new -..Lambda.DefaultTimeBar.Qcgn0kqjCzq5x_ej2phsDpb1YTU(this);
    if (scrubberDrawable != null) {
      scrubberPadding = ((scrubberDrawable.getMinimumWidth() + 1) / 2);
    } else {
      scrubberPadding = ((Math.max(scrubberDisabledSize, Math.max(scrubberEnabledSize, scrubberDraggedSize)) + 1) / 2);
    }
    duration = -9223372036854775807L;
    keyTimeIncrement = -9223372036854775807L;
    keyCountIncrement = 20;
    setFocusable(true);
    if (Util.SDK_INT >= 16) {
      maybeSetImportantForAccessibilityV16();
    }
  }
  
  private static int dpToPx(DisplayMetrics paramDisplayMetrics, int paramInt)
  {
    return (int)(paramInt * density + 0.5F);
  }
  
  private void drawPlayhead(Canvas paramCanvas)
  {
    if (duration <= 0L) {
      return;
    }
    int j = Util.constrainValue(scrubberBar.right, scrubberBar.left, progressBar.right);
    int k = scrubberBar.centerY();
    if (scrubberDrawable == null)
    {
      if ((!scrubbing) && (!isFocused()))
      {
        if (isEnabled()) {
          i = scrubberEnabledSize;
        } else {
          i = scrubberDisabledSize;
        }
      }
      else {
        i = scrubberDraggedSize;
      }
      i /= 2;
      paramCanvas.drawCircle(j, k, i, scrubberPaint);
      return;
    }
    int m = scrubberDrawable.getIntrinsicWidth();
    int i = scrubberDrawable.getIntrinsicHeight();
    Drawable localDrawable = scrubberDrawable;
    m /= 2;
    i /= 2;
    localDrawable.setBounds(j - m, k - i, j + m, k + i);
    scrubberDrawable.draw(paramCanvas);
  }
  
  private void drawTimeBar(Canvas paramCanvas)
  {
    int i = progressBar.height();
    int j = progressBar.centerY() - i / 2;
    int k = i + j;
    if (duration <= 0L)
    {
      paramCanvas.drawRect(progressBar.left, j, progressBar.right, k, unplayedPaint);
      return;
    }
    int m = bufferedBar.left;
    i = bufferedBar.right;
    int n = Math.max(Math.max(progressBar.left, i), scrubberBar.right);
    if (n < progressBar.right) {
      paramCanvas.drawRect(n, j, progressBar.right, k, unplayedPaint);
    }
    m = Math.max(m, scrubberBar.right);
    if (i > m) {
      paramCanvas.drawRect(m, j, i, k, bufferedPaint);
    }
    if (scrubberBar.width() > 0) {
      paramCanvas.drawRect(scrubberBar.left, j, scrubberBar.right, k, playedPaint);
    }
    if (adGroupCount == 0) {
      return;
    }
    long[] arrayOfLong = (long[])Assertions.checkNotNull(adGroupTimesMs);
    boolean[] arrayOfBoolean = (boolean[])Assertions.checkNotNull(playedAdGroups);
    m = adMarkerWidth / 2;
    i = 0;
    while (i < adGroupCount)
    {
      long l = Util.constrainValue(arrayOfLong[i], 0L, duration);
      n = (int)(progressBar.width() * l / duration);
      n = progressBar.left + Math.min(progressBar.width() - adMarkerWidth, Math.max(0, n - m));
      if (arrayOfBoolean[i] != 0) {}
      for (Paint localPaint = playedAdMarkerPaint;; localPaint = adMarkerPaint) {
        break;
      }
      paramCanvas.drawRect(n, j, n + adMarkerWidth, k, localPaint);
      i += 1;
    }
  }
  
  public static int getDefaultBufferedColor(int paramInt)
  {
    return paramInt & 0xFFFFFF | 0xCC000000;
  }
  
  public static int getDefaultPlayedAdMarkerColor(int paramInt)
  {
    return paramInt & 0xFFFFFF | 0x33000000;
  }
  
  public static int getDefaultScrubberColor(int paramInt)
  {
    return paramInt | 0xFF000000;
  }
  
  public static int getDefaultUnplayedColor(int paramInt)
  {
    return paramInt & 0xFFFFFF | 0x33000000;
  }
  
  private long getPositionIncrement()
  {
    if (keyTimeIncrement == -9223372036854775807L)
    {
      if (duration == -9223372036854775807L) {
        return 0L;
      }
      return duration / keyCountIncrement;
    }
    return keyTimeIncrement;
  }
  
  private String getProgressText()
  {
    return Util.getStringForTime(formatBuilder, formatter, position);
  }
  
  private long getScrubberPosition()
  {
    if ((progressBar.width() > 0) && (duration != -9223372036854775807L)) {
      return scrubberBar.width() * duration / progressBar.width();
    }
    return 0L;
  }
  
  private boolean isInSeekBar(float paramFloat1, float paramFloat2)
  {
    return seekBounds.contains((int)paramFloat1, (int)paramFloat2);
  }
  
  private void maybeSetImportantForAccessibilityV16()
  {
    if (getImportantForAccessibility() == 0) {
      setImportantForAccessibility(1);
    }
  }
  
  private void positionScrubber(float paramFloat)
  {
    scrubberBar.right = Util.constrainValue((int)paramFloat, progressBar.left, progressBar.right);
  }
  
  private Point resolveRelativeTouchPosition(MotionEvent paramMotionEvent)
  {
    getLocationOnScreen(locationOnScreen);
    touchPosition.set((int)paramMotionEvent.getRawX() - locationOnScreen[0], (int)paramMotionEvent.getRawY() - locationOnScreen[1]);
    return touchPosition;
  }
  
  private boolean scrubIncrementally(long paramLong)
  {
    if (duration <= 0L) {
      return false;
    }
    long l = getScrubberPosition();
    scrubPosition = Util.constrainValue(l + paramLong, 0L, duration);
    if (scrubPosition == l) {
      return false;
    }
    if (!scrubbing) {
      startScrubbing();
    }
    Iterator localIterator = listeners.iterator();
    while (localIterator.hasNext()) {
      ((TimeBar.OnScrubListener)localIterator.next()).onScrubMove(this, scrubPosition);
    }
    update();
    return true;
  }
  
  private boolean setDrawableLayoutDirection(Drawable paramDrawable)
  {
    return (Util.SDK_INT >= 23) && (setDrawableLayoutDirection(paramDrawable, getLayoutDirection()));
  }
  
  private static boolean setDrawableLayoutDirection(Drawable paramDrawable, int paramInt)
  {
    return (Util.SDK_INT >= 23) && (paramDrawable.setLayoutDirection(paramInt));
  }
  
  private void startScrubbing()
  {
    scrubbing = true;
    setPressed(true);
    Object localObject = getParent();
    if (localObject != null) {
      ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
    }
    localObject = listeners.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((TimeBar.OnScrubListener)((Iterator)localObject).next()).onScrubStart(this, getScrubberPosition());
    }
  }
  
  private void stopScrubbing(boolean paramBoolean)
  {
    scrubbing = false;
    setPressed(false);
    Object localObject = getParent();
    if (localObject != null) {
      ((ViewParent)localObject).requestDisallowInterceptTouchEvent(false);
    }
    invalidate();
    localObject = listeners.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((TimeBar.OnScrubListener)((Iterator)localObject).next()).onScrubStop(this, getScrubberPosition(), paramBoolean);
    }
  }
  
  private void update()
  {
    bufferedBar.set(progressBar);
    scrubberBar.set(progressBar);
    long l;
    if (scrubbing) {
      l = scrubPosition;
    } else {
      l = position;
    }
    if (duration > 0L)
    {
      int i = (int)(progressBar.width() * bufferedPosition / duration);
      bufferedBar.right = Math.min(progressBar.left + i, progressBar.right);
      i = (int)(progressBar.width() * l / duration);
      scrubberBar.right = Math.min(progressBar.left + i, progressBar.right);
    }
    else
    {
      bufferedBar.right = progressBar.left;
      scrubberBar.right = progressBar.left;
    }
    invalidate(seekBounds);
  }
  
  private void updateDrawableState()
  {
    if ((scrubberDrawable != null) && (scrubberDrawable.isStateful()) && (scrubberDrawable.setState(getDrawableState()))) {
      invalidate();
    }
  }
  
  public void addListener(TimeBar.OnScrubListener paramOnScrubListener)
  {
    listeners.add(paramOnScrubListener);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateDrawableState();
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (scrubberDrawable != null) {
      scrubberDrawable.jumpToCurrentState();
    }
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    paramCanvas.save();
    drawTimeBar(paramCanvas);
    drawPlayhead(paramCanvas);
    paramCanvas.restore();
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (paramAccessibilityEvent.getEventType() == 4) {
      paramAccessibilityEvent.getText().add(getProgressText());
    }
    paramAccessibilityEvent.setClassName("android.widget.SeekBar");
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName("android.widget.SeekBar");
    paramAccessibilityNodeInfo.setContentDescription(getProgressText());
    if (duration <= 0L) {
      return;
    }
    if (Util.SDK_INT >= 21)
    {
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
      return;
    }
    if (Util.SDK_INT >= 16)
    {
      paramAccessibilityNodeInfo.addAction(4096);
      paramAccessibilityNodeInfo.addAction(8192);
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (isEnabled())
    {
      long l2 = getPositionIncrement();
      long l1 = l2;
      if (paramInt != 66)
      {
        switch (paramInt)
        {
        default: 
          break;
        case 21: 
          l1 = -l2;
        case 22: 
          if (!scrubIncrementally(l1)) {
            break;
          }
          removeCallbacks(stopScrubbingRunnable);
          postDelayed(stopScrubbingRunnable, 1000L);
          return true;
        }
      }
      else if (scrubbing)
      {
        removeCallbacks(stopScrubbingRunnable);
        stopScrubbingRunnable.run();
        return true;
      }
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = (paramInt4 - paramInt2 - touchTargetHeight) / 2;
    paramInt4 = getPaddingLeft();
    int i = getPaddingRight();
    int j = (touchTargetHeight - barHeight) / 2 + paramInt2;
    seekBounds.set(paramInt4, paramInt2, paramInt3 - paramInt1 - i, touchTargetHeight + paramInt2);
    progressBar.set(seekBounds.left + scrubberPadding, j, seekBounds.right - scrubberPadding, barHeight + j);
    update();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getMode(paramInt2);
    int i = View.MeasureSpec.getSize(paramInt2);
    paramInt2 = i;
    if (j == 0) {
      paramInt2 = touchTargetHeight;
    } else if (j != 1073741824) {
      paramInt2 = Math.min(touchTargetHeight, i);
    }
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), paramInt2);
    updateDrawableState();
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    if ((scrubberDrawable != null) && (setDrawableLayoutDirection(scrubberDrawable, paramInt))) {
      invalidate();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = isEnabled();
    boolean bool1 = false;
    if (bool2)
    {
      if (duration <= 0L) {
        return false;
      }
      Point localPoint = resolveRelativeTouchPosition(paramMotionEvent);
      int i = x;
      int j = y;
      switch (paramMotionEvent.getAction())
      {
      default: 
        return false;
      case 2: 
        if (scrubbing)
        {
          if (j < fineScrubYThreshold)
          {
            j = lastCoarseScrubXPosition;
            positionScrubber(lastCoarseScrubXPosition + (i - j) / 3);
          }
          else
          {
            lastCoarseScrubXPosition = i;
            positionScrubber(i);
          }
          scrubPosition = getScrubberPosition();
          paramMotionEvent = listeners.iterator();
          while (paramMotionEvent.hasNext()) {
            ((TimeBar.OnScrubListener)paramMotionEvent.next()).onScrubMove(this, scrubPosition);
          }
          update();
          invalidate();
          return true;
        }
        break;
      case 1: 
      case 3: 
        if (scrubbing)
        {
          if (paramMotionEvent.getAction() == 3) {
            bool1 = true;
          }
          stopScrubbing(bool1);
          return true;
        }
        break;
      case 0: 
        float f = i;
        if (isInSeekBar(f, j))
        {
          positionScrubber(f);
          startScrubbing();
          scrubPosition = getScrubberPosition();
          update();
          invalidate();
          return true;
        }
        break;
      }
    }
    return false;
  }
  
  public boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityAction(paramInt, paramBundle)) {
      return true;
    }
    if (duration <= 0L) {
      return false;
    }
    if (paramInt == 8192)
    {
      if (scrubIncrementally(-getPositionIncrement())) {
        stopScrubbing(false);
      }
    }
    else
    {
      if (paramInt != 4096) {
        break label79;
      }
      if (scrubIncrementally(getPositionIncrement())) {
        stopScrubbing(false);
      }
    }
    sendAccessibilityEvent(4);
    return true;
    label79:
    return false;
  }
  
  public void removeListener(TimeBar.OnScrubListener paramOnScrubListener)
  {
    listeners.remove(paramOnScrubListener);
  }
  
  public void setAdGroupTimesMs(long[] paramArrayOfLong, boolean[] paramArrayOfBoolean, int paramInt)
  {
    boolean bool;
    if ((paramInt != 0) && ((paramArrayOfLong == null) || (paramArrayOfBoolean == null))) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkArgument(bool);
    adGroupCount = paramInt;
    adGroupTimesMs = paramArrayOfLong;
    playedAdGroups = paramArrayOfBoolean;
    update();
  }
  
  public void setAdMarkerColor(int paramInt)
  {
    adMarkerPaint.setColor(paramInt);
    invalidate(seekBounds);
  }
  
  public void setBufferedColor(int paramInt)
  {
    bufferedPaint.setColor(paramInt);
    invalidate(seekBounds);
  }
  
  public void setBufferedPosition(long paramLong)
  {
    bufferedPosition = paramLong;
    update();
  }
  
  public void setDuration(long paramLong)
  {
    duration = paramLong;
    if ((scrubbing) && (paramLong == -9223372036854775807L)) {
      stopScrubbing(true);
    }
    update();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if ((scrubbing) && (!paramBoolean)) {
      stopScrubbing(true);
    }
  }
  
  public void setKeyCountIncrement(int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    keyCountIncrement = paramInt;
    keyTimeIncrement = -9223372036854775807L;
  }
  
  public void setKeyTimeIncrement(long paramLong)
  {
    boolean bool;
    if (paramLong > 0L) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    keyCountIncrement = -1;
    keyTimeIncrement = paramLong;
  }
  
  public void setPlayedAdMarkerColor(int paramInt)
  {
    playedAdMarkerPaint.setColor(paramInt);
    invalidate(seekBounds);
  }
  
  public void setPlayedColor(int paramInt)
  {
    playedPaint.setColor(paramInt);
    invalidate(seekBounds);
  }
  
  public void setPosition(long paramLong)
  {
    position = paramLong;
    setContentDescription(getProgressText());
    update();
  }
  
  public void setScrubberColor(int paramInt)
  {
    scrubberPaint.setColor(paramInt);
    invalidate(seekBounds);
  }
  
  public void setUnplayedColor(int paramInt)
  {
    unplayedPaint.setColor(paramInt);
    invalidate(seekBounds);
  }
}
