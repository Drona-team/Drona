package com.bumptech.glide.request.target;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.R.id;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CustomViewTarget<T extends View, Z>
  implements Target<Z>
{
  private static final String PAGE_KEY = "CustomViewTarget";
  @IdRes
  private static final int VIEW_TAG_ID = R.id.glide_custom_view_target_tag;
  @Nullable
  private View.OnAttachStateChangeListener attachStateListener;
  private boolean isAttachStateListenerAdded;
  private boolean isClearedByUs;
  @IdRes
  private int overrideTag;
  private final SizeDeterminer sizeDeterminer;
  protected final T view;
  
  public CustomViewTarget(View paramView)
  {
    view = ((View)Preconditions.checkNotNull(paramView));
    sizeDeterminer = new SizeDeterminer(paramView);
  }
  
  private Object getTag()
  {
    View localView = view;
    int i;
    if (overrideTag == 0) {
      i = VIEW_TAG_ID;
    } else {
      i = overrideTag;
    }
    return localView.getTag(i);
  }
  
  private void maybeAddAttachStateListener()
  {
    if (attachStateListener != null)
    {
      if (isAttachStateListenerAdded) {
        return;
      }
      view.addOnAttachStateChangeListener(attachStateListener);
      isAttachStateListenerAdded = true;
    }
  }
  
  private void maybeRemoveAttachStateListener()
  {
    if (attachStateListener != null)
    {
      if (!isAttachStateListenerAdded) {
        return;
      }
      view.removeOnAttachStateChangeListener(attachStateListener);
      isAttachStateListenerAdded = false;
    }
  }
  
  private void setTag(Object paramObject)
  {
    View localView = view;
    int i;
    if (overrideTag == 0) {
      i = VIEW_TAG_ID;
    } else {
      i = overrideTag;
    }
    localView.setTag(i, paramObject);
  }
  
  public final CustomViewTarget clearOnDetach()
  {
    if (attachStateListener != null) {
      return this;
    }
    attachStateListener = new View.OnAttachStateChangeListener()
    {
      public void onViewAttachedToWindow(View paramAnonymousView)
      {
        resumeMyRequest();
      }
      
      public void onViewDetachedFromWindow(View paramAnonymousView)
      {
        pauseMyRequest();
      }
    };
    maybeAddAttachStateListener();
    return this;
  }
  
  public final Request getRequest()
  {
    Object localObject = getTag();
    if (localObject != null)
    {
      if ((localObject instanceof Request)) {
        return (Request)localObject;
      }
      throw new IllegalArgumentException("You must not pass non-R.id ids to setTag(id)");
    }
    return null;
  }
  
  public final void getSize(SizeReadyCallback paramSizeReadyCallback)
  {
    sizeDeterminer.getSize(paramSizeReadyCallback);
  }
  
  public final View getView()
  {
    return view;
  }
  
  public void onDestroy() {}
  
  public final void onLoadCleared(Drawable paramDrawable)
  {
    sizeDeterminer.clearCallbacksAndListener();
    onResourceCleared(paramDrawable);
    if (!isClearedByUs) {
      maybeRemoveAttachStateListener();
    }
  }
  
  public final void onLoadStarted(Drawable paramDrawable)
  {
    maybeAddAttachStateListener();
    onResourceLoading(paramDrawable);
  }
  
  protected abstract void onResourceCleared(Drawable paramDrawable);
  
  protected void onResourceLoading(Drawable paramDrawable) {}
  
  public void onStart() {}
  
  public void onStop() {}
  
  final void pauseMyRequest()
  {
    Request localRequest = getRequest();
    if (localRequest != null)
    {
      isClearedByUs = true;
      localRequest.clear();
      isClearedByUs = false;
    }
  }
  
  public final void removeCallback(SizeReadyCallback paramSizeReadyCallback)
  {
    sizeDeterminer.removeCallback(paramSizeReadyCallback);
  }
  
  final void resumeMyRequest()
  {
    Request localRequest = getRequest();
    if ((localRequest != null) && (localRequest.isCleared())) {
      localRequest.begin();
    }
  }
  
  public final void setRequest(Request paramRequest)
  {
    setTag(paramRequest);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Target for: ");
    localStringBuilder.append(view);
    return localStringBuilder.toString();
  }
  
  public final CustomViewTarget useTagId(int paramInt)
  {
    if (overrideTag == 0)
    {
      overrideTag = paramInt;
      return this;
    }
    throw new IllegalArgumentException("You cannot change the tag id once it has been set.");
  }
  
  public final CustomViewTarget waitForLayout()
  {
    sizeDeterminer.waitForLayout = true;
    return this;
  }
  
  @VisibleForTesting
  static final class SizeDeterminer
  {
    private static final int PENDING_SIZE = 0;
    @Nullable
    @VisibleForTesting
    static Integer maxDisplayLength;
    private final List<SizeReadyCallback> cbs = new ArrayList();
    @Nullable
    private SizeDeterminerLayoutListener layoutListener;
    private final View view;
    boolean waitForLayout;
    
    SizeDeterminer(View paramView)
    {
      view = paramView;
    }
    
    private static int getMaxDisplayLength(Context paramContext)
    {
      if (maxDisplayLength == null)
      {
        paramContext = ((WindowManager)Preconditions.checkNotNull((WindowManager)paramContext.getSystemService("window"))).getDefaultDisplay();
        Point localPoint = new Point();
        paramContext.getSize(localPoint);
        maxDisplayLength = Integer.valueOf(Math.max(x, y));
      }
      return maxDisplayLength.intValue();
    }
    
    private int getTargetDimen(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt2 - paramInt3;
      if (i > 0) {
        return i;
      }
      if ((waitForLayout) && (view.isLayoutRequested())) {
        return 0;
      }
      paramInt1 -= paramInt3;
      if (paramInt1 > 0) {
        return paramInt1;
      }
      if ((!view.isLayoutRequested()) && (paramInt2 == -2))
      {
        if (Log.isLoggable("CustomViewTarget", 4)) {
          Log.i("CustomViewTarget", "Glide treats LayoutParams.WRAP_CONTENT as a request for an image the size of this device's screen dimensions. If you want to load the original image and are ok with the corresponding memory cost and OOMs (depending on the input size), use .override(Target.SIZE_ORIGINAL). Otherwise, use LayoutParams.MATCH_PARENT, set layout_width and layout_height to fixed dimension, or use .override() with fixed dimensions.");
        }
        return getMaxDisplayLength(view.getContext());
      }
      return 0;
    }
    
    private int getTargetHeight()
    {
      int j = view.getPaddingTop();
      int k = view.getPaddingBottom();
      ViewGroup.LayoutParams localLayoutParams = view.getLayoutParams();
      int i;
      if (localLayoutParams != null) {
        i = height;
      } else {
        i = 0;
      }
      return getTargetDimen(view.getHeight(), i, j + k);
    }
    
    private int getTargetWidth()
    {
      int j = view.getPaddingLeft();
      int k = view.getPaddingRight();
      ViewGroup.LayoutParams localLayoutParams = view.getLayoutParams();
      int i;
      if (localLayoutParams != null) {
        i = width;
      } else {
        i = 0;
      }
      return getTargetDimen(view.getWidth(), i, j + k);
    }
    
    private boolean isDimensionValid(int paramInt)
    {
      return (paramInt > 0) || (paramInt == Integer.MIN_VALUE);
    }
    
    private boolean isViewStateAndSizeValid(int paramInt1, int paramInt2)
    {
      return (isDimensionValid(paramInt1)) && (isDimensionValid(paramInt2));
    }
    
    private void notifyCbs(int paramInt1, int paramInt2)
    {
      Iterator localIterator = new ArrayList(cbs).iterator();
      while (localIterator.hasNext()) {
        ((SizeReadyCallback)localIterator.next()).onSizeReady(paramInt1, paramInt2);
      }
    }
    
    void checkCurrentDimens()
    {
      if (cbs.isEmpty()) {
        return;
      }
      int i = getTargetWidth();
      int j = getTargetHeight();
      if (!isViewStateAndSizeValid(i, j)) {
        return;
      }
      notifyCbs(i, j);
      clearCallbacksAndListener();
    }
    
    void clearCallbacksAndListener()
    {
      ViewTreeObserver localViewTreeObserver = view.getViewTreeObserver();
      if (localViewTreeObserver.isAlive()) {
        localViewTreeObserver.removeOnPreDrawListener(layoutListener);
      }
      layoutListener = null;
      cbs.clear();
    }
    
    void getSize(SizeReadyCallback paramSizeReadyCallback)
    {
      int i = getTargetWidth();
      int j = getTargetHeight();
      if (isViewStateAndSizeValid(i, j))
      {
        paramSizeReadyCallback.onSizeReady(i, j);
        return;
      }
      if (!cbs.contains(paramSizeReadyCallback)) {
        cbs.add(paramSizeReadyCallback);
      }
      if (layoutListener == null)
      {
        paramSizeReadyCallback = view.getViewTreeObserver();
        layoutListener = new SizeDeterminerLayoutListener(this);
        paramSizeReadyCallback.addOnPreDrawListener(layoutListener);
      }
    }
    
    void removeCallback(SizeReadyCallback paramSizeReadyCallback)
    {
      cbs.remove(paramSizeReadyCallback);
    }
    
    private static final class SizeDeterminerLayoutListener
      implements ViewTreeObserver.OnPreDrawListener
    {
      private final WeakReference<CustomViewTarget.SizeDeterminer> sizeDeterminerRef;
      
      SizeDeterminerLayoutListener(CustomViewTarget.SizeDeterminer paramSizeDeterminer)
      {
        sizeDeterminerRef = new WeakReference(paramSizeDeterminer);
      }
      
      public boolean onPreDraw()
      {
        if (Log.isLoggable("CustomViewTarget", 2))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("OnGlobalLayoutListener called attachStateListener=");
          ((StringBuilder)localObject).append(this);
          Log.v("CustomViewTarget", ((StringBuilder)localObject).toString());
        }
        Object localObject = (CustomViewTarget.SizeDeterminer)sizeDeterminerRef.get();
        if (localObject != null) {
          ((CustomViewTarget.SizeDeterminer)localObject).checkCurrentDimens();
        }
        return true;
      }
    }
  }
}
