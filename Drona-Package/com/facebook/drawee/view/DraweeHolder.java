package com.facebook.drawee.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.components.DraweeEventTracker;
import com.facebook.drawee.components.DraweeEventTracker.Event;
import com.facebook.drawee.drawable.VisibilityAwareDrawable;
import com.facebook.drawee.drawable.VisibilityCallback;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;

public class DraweeHolder<DH extends DraweeHierarchy>
  implements VisibilityCallback
{
  private DraweeController mController = null;
  private final DraweeEventTracker mEventTracker = DraweeEventTracker.newInstance();
  private DH mHierarchy;
  private boolean mIsControllerAttached = false;
  private boolean mIsHolderAttached = false;
  private boolean mIsVisible = true;
  
  public DraweeHolder(DraweeHierarchy paramDraweeHierarchy)
  {
    if (paramDraweeHierarchy != null) {
      setHierarchy(paramDraweeHierarchy);
    }
  }
  
  private void attachController()
  {
    if (mIsControllerAttached) {
      return;
    }
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_ATTACH_CONTROLLER);
    mIsControllerAttached = true;
    if ((mController != null) && (mController.getHierarchy() != null)) {
      mController.onAttach();
    }
  }
  
  private void attachOrDetachController()
  {
    if ((mIsHolderAttached) && (mIsVisible))
    {
      attachController();
      return;
    }
    detachController();
  }
  
  public static DraweeHolder create(DraweeHierarchy paramDraweeHierarchy, Context paramContext)
  {
    paramDraweeHierarchy = new DraweeHolder(paramDraweeHierarchy);
    paramDraweeHierarchy.registerWithContext(paramContext);
    return paramDraweeHierarchy;
  }
  
  private void detachController()
  {
    if (!mIsControllerAttached) {
      return;
    }
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_DETACH_CONTROLLER);
    mIsControllerAttached = false;
    if (isControllerValid()) {
      mController.onDetach();
    }
  }
  
  private void setVisibilityCallback(VisibilityCallback paramVisibilityCallback)
  {
    Drawable localDrawable = getTopLevelDrawable();
    if ((localDrawable instanceof VisibilityAwareDrawable)) {
      ((VisibilityAwareDrawable)localDrawable).setVisibilityCallback(paramVisibilityCallback);
    }
  }
  
  public DraweeController getController()
  {
    return mController;
  }
  
  protected DraweeEventTracker getDraweeEventTracker()
  {
    return mEventTracker;
  }
  
  public DraweeHierarchy getHierarchy()
  {
    return (DraweeHierarchy)Preconditions.checkNotNull(mHierarchy);
  }
  
  public Drawable getTopLevelDrawable()
  {
    if (mHierarchy == null) {
      return null;
    }
    return mHierarchy.getTopLevelDrawable();
  }
  
  public boolean hasHierarchy()
  {
    return mHierarchy != null;
  }
  
  public boolean isAttached()
  {
    return mIsHolderAttached;
  }
  
  public boolean isControllerValid()
  {
    return (mController != null) && (mController.getHierarchy() == mHierarchy);
  }
  
  public void onAttach()
  {
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_HOLDER_ATTACH);
    mIsHolderAttached = true;
    attachOrDetachController();
  }
  
  public void onDetach()
  {
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_HOLDER_DETACH);
    mIsHolderAttached = false;
    attachOrDetachController();
  }
  
  public void onDraw()
  {
    if (mIsControllerAttached) {
      return;
    }
    FLog.w(DraweeEventTracker.class, "%x: Draw requested for a non-attached controller %x. %s", new Object[] { Integer.valueOf(System.identityHashCode(this)), Integer.valueOf(System.identityHashCode(mController)), toString() });
    mIsHolderAttached = true;
    mIsVisible = true;
    attachOrDetachController();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isControllerValid()) {
      return false;
    }
    return mController.onTouchEvent(paramMotionEvent);
  }
  
  public void onVisibilityChange(boolean paramBoolean)
  {
    if (mIsVisible == paramBoolean) {
      return;
    }
    DraweeEventTracker localDraweeEventTracker = mEventTracker;
    DraweeEventTracker.Event localEvent;
    if (paramBoolean) {
      localEvent = DraweeEventTracker.Event.ON_DRAWABLE_SHOW;
    } else {
      localEvent = DraweeEventTracker.Event.ON_DRAWABLE_HIDE;
    }
    localDraweeEventTracker.recordEvent(localEvent);
    mIsVisible = paramBoolean;
    attachOrDetachController();
  }
  
  public void registerWithContext(Context paramContext) {}
  
  public void setController(DraweeController paramDraweeController)
  {
    boolean bool = mIsControllerAttached;
    if (bool) {
      detachController();
    }
    if (isControllerValid())
    {
      mEventTracker.recordEvent(DraweeEventTracker.Event.ON_CLEAR_OLD_CONTROLLER);
      mController.setHierarchy(null);
    }
    mController = paramDraweeController;
    if (mController != null)
    {
      mEventTracker.recordEvent(DraweeEventTracker.Event.ON_SET_CONTROLLER);
      mController.setHierarchy(mHierarchy);
    }
    else
    {
      mEventTracker.recordEvent(DraweeEventTracker.Event.ON_CLEAR_CONTROLLER);
    }
    if (bool) {
      attachController();
    }
  }
  
  public void setHierarchy(DraweeHierarchy paramDraweeHierarchy)
  {
    mEventTracker.recordEvent(DraweeEventTracker.Event.ON_SET_HIERARCHY);
    boolean bool2 = isControllerValid();
    setVisibilityCallback(null);
    mHierarchy = ((DraweeHierarchy)Preconditions.checkNotNull(paramDraweeHierarchy));
    Drawable localDrawable = mHierarchy.getTopLevelDrawable();
    boolean bool1;
    if ((localDrawable != null) && (!localDrawable.isVisible())) {
      bool1 = false;
    } else {
      bool1 = true;
    }
    onVisibilityChange(bool1);
    setVisibilityCallback(this);
    if (bool2) {
      mController.setHierarchy(paramDraweeHierarchy);
    }
  }
  
  public String toString()
  {
    return Objects.toStringHelper(this).addValue("controllerAttached", mIsControllerAttached).addValue("holderAttached", mIsHolderAttached).addValue("drawableVisible", mIsVisible).addValue("events", mEventTracker.toString()).toString();
  }
}
