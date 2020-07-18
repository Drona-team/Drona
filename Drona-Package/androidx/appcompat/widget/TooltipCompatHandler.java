package androidx.appcompat.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityManager;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewConfigurationCompat;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
class TooltipCompatHandler
  implements View.OnLongClickListener, View.OnHoverListener, View.OnAttachStateChangeListener
{
  private static final long HOVER_HIDE_TIMEOUT_MS = 15000L;
  private static final long HOVER_HIDE_TIMEOUT_SHORT_MS = 3000L;
  private static final long LONG_CLICK_HIDE_TIMEOUT_MS = 2500L;
  private static final String PAGE_KEY = "TooltipCompatHandler";
  private static TooltipCompatHandler sActiveHandler;
  private static TooltipCompatHandler sPendingHandler;
  private final View mAnchor;
  private int mAnchorX;
  private int mAnchorY;
  private boolean mFromTouch;
  private final Runnable mHideRunnable = new Runnable()
  {
    public void run()
    {
      hide();
    }
  };
  private final int mHoverSlop;
  private TooltipPopup mPopup;
  private final Runnable mShowRunnable = new Runnable()
  {
    public void run()
    {
      show(false);
    }
  };
  private final CharSequence mTooltipText;
  
  private TooltipCompatHandler(View paramView, CharSequence paramCharSequence)
  {
    mAnchor = paramView;
    mTooltipText = paramCharSequence;
    mHoverSlop = ViewConfigurationCompat.getScaledHoverSlop(ViewConfiguration.get(mAnchor.getContext()));
    clearAnchorPos();
    mAnchor.setOnLongClickListener(this);
    mAnchor.setOnHoverListener(this);
  }
  
  private void cancelPendingShow()
  {
    mAnchor.removeCallbacks(mShowRunnable);
  }
  
  private void clearAnchorPos()
  {
    mAnchorX = Integer.MAX_VALUE;
    mAnchorY = Integer.MAX_VALUE;
  }
  
  private void scheduleShow()
  {
    mAnchor.postDelayed(mShowRunnable, ViewConfiguration.getLongPressTimeout());
  }
  
  private static void setPendingHandler(TooltipCompatHandler paramTooltipCompatHandler)
  {
    if (sPendingHandler != null) {
      sPendingHandler.cancelPendingShow();
    }
    sPendingHandler = paramTooltipCompatHandler;
    if (sPendingHandler != null) {
      sPendingHandler.scheduleShow();
    }
  }
  
  public static void setTooltipText(View paramView, CharSequence paramCharSequence)
  {
    if ((sPendingHandler != null) && (sPendingHandlermAnchor == paramView)) {
      setPendingHandler(null);
    }
    if (TextUtils.isEmpty(paramCharSequence))
    {
      if ((sActiveHandler != null) && (sActiveHandlermAnchor == paramView)) {
        sActiveHandler.hide();
      }
      paramView.setOnLongClickListener(null);
      paramView.setLongClickable(false);
      paramView.setOnHoverListener(null);
      return;
    }
    new TooltipCompatHandler(paramView, paramCharSequence);
  }
  
  private boolean updateAnchorPos(MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    if ((Math.abs(i - mAnchorX) <= mHoverSlop) && (Math.abs(j - mAnchorY) <= mHoverSlop)) {
      return false;
    }
    mAnchorX = i;
    mAnchorY = j;
    return true;
  }
  
  void hide()
  {
    if (sActiveHandler == this)
    {
      sActiveHandler = null;
      if (mPopup != null)
      {
        mPopup.hide();
        mPopup = null;
        clearAnchorPos();
        mAnchor.removeOnAttachStateChangeListener(this);
      }
      else
      {
        Log.e("TooltipCompatHandler", "sActiveHandler.mPopup == null");
      }
    }
    if (sPendingHandler == this) {
      setPendingHandler(null);
    }
    mAnchor.removeCallbacks(mHideRunnable);
  }
  
  public boolean onHover(View paramView, MotionEvent paramMotionEvent)
  {
    if ((mPopup != null) && (mFromTouch)) {
      return false;
    }
    paramView = (AccessibilityManager)mAnchor.getContext().getSystemService("accessibility");
    if ((paramView.isEnabled()) && (paramView.isTouchExplorationEnabled())) {
      return false;
    }
    int i = paramMotionEvent.getAction();
    if (i != 7)
    {
      if (i != 10) {
        return false;
      }
      clearAnchorPos();
      hide();
      return false;
    }
    if ((mAnchor.isEnabled()) && (mPopup == null) && (updateAnchorPos(paramMotionEvent))) {
      setPendingHandler(this);
    }
    return false;
  }
  
  public boolean onLongClick(View paramView)
  {
    mAnchorX = (paramView.getWidth() / 2);
    mAnchorY = (paramView.getHeight() / 2);
    show(true);
    return true;
  }
  
  public void onViewAttachedToWindow(View paramView) {}
  
  public void onViewDetachedFromWindow(View paramView)
  {
    hide();
  }
  
  void show(boolean paramBoolean)
  {
    if (!ViewCompat.isAttachedToWindow(mAnchor)) {
      return;
    }
    setPendingHandler(null);
    if (sActiveHandler != null) {
      sActiveHandler.hide();
    }
    sActiveHandler = this;
    mFromTouch = paramBoolean;
    mPopup = new TooltipPopup(mAnchor.getContext());
    mPopup.show(mAnchor, mAnchorX, mAnchorY, mFromTouch, mTooltipText);
    mAnchor.addOnAttachStateChangeListener(this);
    long l;
    if (mFromTouch) {
      l = 2500L;
    } else if ((ViewCompat.getWindowSystemUiVisibility(mAnchor) & 0x1) == 1) {
      l = 3000L - ViewConfiguration.getLongPressTimeout();
    } else {
      l = 15000L - ViewConfiguration.getLongPressTimeout();
    }
    mAnchor.removeCallbacks(mHideRunnable);
    mAnchor.postDelayed(mHideRunnable, l);
  }
}
