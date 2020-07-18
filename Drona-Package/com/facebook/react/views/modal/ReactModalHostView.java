package com.facebook.react.views.modal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStructure;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.R.style;
import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.JSTouchDispatcher;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.RootView;
import com.facebook.react.uimanager.StateWrapper;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.common.ContextUtils;
import com.facebook.react.views.view.ReactViewGroup;
import java.util.ArrayList;

public class ReactModalHostView
  extends ViewGroup
  implements LifecycleEventListener
{
  private String mAnimationType;
  @Nullable
  private Dialog mDialog;
  private boolean mHardwareAccelerated;
  private DialogRootViewGroup mHostView;
  @Nullable
  private OnRequestCloseListener mOnRequestCloseListener;
  @Nullable
  private DialogInterface.OnShowListener mOnShowListener;
  private boolean mPropertyRequiresNewDialog;
  private boolean mTransparent;
  
  public ReactModalHostView(Context paramContext)
  {
    super(paramContext);
    ((ReactContext)paramContext).addLifecycleEventListener(this);
    mHostView = new DialogRootViewGroup(paramContext);
  }
  
  private void dismiss()
  {
    if (mDialog != null)
    {
      if (mDialog.isShowing())
      {
        Activity localActivity = (Activity)ContextUtils.findContextOfType(mDialog.getContext(), Activity.class);
        if ((localActivity == null) || (!localActivity.isFinishing())) {
          mDialog.dismiss();
        }
      }
      mDialog = null;
      ((ViewGroup)mHostView.getParent()).removeViewAt(0);
    }
  }
  
  private View getContentView()
  {
    FrameLayout localFrameLayout = new FrameLayout(getContext());
    localFrameLayout.addView(mHostView);
    localFrameLayout.setFitsSystemWindows(true);
    return localFrameLayout;
  }
  
  private Activity getCurrentActivity()
  {
    return ((ReactContext)getContext()).getCurrentActivity();
  }
  
  private void updateProperties()
  {
    Assertions.assertNotNull(mDialog, "mDialog must exist when we call updateProperties");
    Activity localActivity = getCurrentActivity();
    if (localActivity != null) {
      if ((getWindowgetAttributesflags & 0x400) != 0) {
        mDialog.getWindow().addFlags(1024);
      } else {
        mDialog.getWindow().clearFlags(1024);
      }
    }
    if (mTransparent)
    {
      mDialog.getWindow().clearFlags(2);
      return;
    }
    mDialog.getWindow().setDimAmount(0.5F);
    mDialog.getWindow().setFlags(2, 2);
  }
  
  public void addChildrenForAccessibility(ArrayList paramArrayList) {}
  
  public void addView(View paramView, int paramInt)
  {
    mHostView.addView(paramView, paramInt);
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    return false;
  }
  
  public void dispatchProvideStructure(ViewStructure paramViewStructure)
  {
    mHostView.dispatchProvideStructure(paramViewStructure);
  }
  
  public View getChildAt(int paramInt)
  {
    return mHostView.getChildAt(paramInt);
  }
  
  public int getChildCount()
  {
    return mHostView.getChildCount();
  }
  
  public Dialog getDialog()
  {
    return mDialog;
  }
  
  public void onDropInstance()
  {
    ((ReactContext)getContext()).removeLifecycleEventListener(this);
    dismiss();
  }
  
  public void onHostDestroy()
  {
    onDropInstance();
  }
  
  public void onHostPause() {}
  
  public void onHostResume()
  {
    showOrUpdate();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void removeView(View paramView)
  {
    mHostView.removeView(paramView);
  }
  
  public void removeViewAt(int paramInt)
  {
    View localView = getChildAt(paramInt);
    mHostView.removeView(localView);
  }
  
  protected void setAnimationType(String paramString)
  {
    mAnimationType = paramString;
    mPropertyRequiresNewDialog = true;
  }
  
  protected void setHardwareAccelerated(boolean paramBoolean)
  {
    mHardwareAccelerated = paramBoolean;
    mPropertyRequiresNewDialog = true;
  }
  
  protected void setOnRequestCloseListener(OnRequestCloseListener paramOnRequestCloseListener)
  {
    mOnRequestCloseListener = paramOnRequestCloseListener;
  }
  
  protected void setOnShowListener(DialogInterface.OnShowListener paramOnShowListener)
  {
    mOnShowListener = paramOnShowListener;
  }
  
  protected void setTransparent(boolean paramBoolean)
  {
    mTransparent = paramBoolean;
  }
  
  protected void showOrUpdate()
  {
    if (mDialog != null) {
      if (mPropertyRequiresNewDialog)
      {
        dismiss();
      }
      else
      {
        updateProperties();
        return;
      }
    }
    mPropertyRequiresNewDialog = false;
    int i = R.style.Theme_FullScreenDialog;
    if (mAnimationType.equals("fade")) {
      i = R.style.Theme_FullScreenDialogAnimatedFade;
    } else if (mAnimationType.equals("slide")) {
      i = R.style.Theme_FullScreenDialogAnimatedSlide;
    }
    Activity localActivity = getCurrentActivity();
    Object localObject;
    if (localActivity == null) {
      localObject = getContext();
    } else {
      localObject = localActivity;
    }
    mDialog = new Dialog((Context)localObject, i);
    mDialog.getWindow().setFlags(8, 8);
    mDialog.setContentView(getContentView());
    updateProperties();
    mDialog.setOnShowListener(mOnShowListener);
    mDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
    {
      public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if (paramAnonymousKeyEvent.getAction() == 1)
        {
          if (paramAnonymousInt == 4)
          {
            Assertions.assertNotNull(mOnRequestCloseListener, "setOnRequestCloseListener must be called by the manager");
            mOnRequestCloseListener.onRequestClose(paramAnonymousDialogInterface);
            return true;
          }
          paramAnonymousDialogInterface = ((ReactContext)getContext()).getCurrentActivity();
          if (paramAnonymousDialogInterface != null) {
            return paramAnonymousDialogInterface.onKeyUp(paramAnonymousInt, paramAnonymousKeyEvent);
          }
        }
        return false;
      }
    });
    mDialog.getWindow().setSoftInputMode(16);
    if (mHardwareAccelerated) {
      mDialog.getWindow().addFlags(16777216);
    }
    if ((localActivity != null) && (!localActivity.isFinishing()))
    {
      mDialog.show();
      if ((localObject instanceof Activity)) {
        mDialog.getWindow().getDecorView().setSystemUiVisibility(((Activity)localObject).getWindow().getDecorView().getSystemUiVisibility());
      }
      mDialog.getWindow().clearFlags(8);
    }
  }
  
  public void updateState(StateWrapper paramStateWrapper, int paramInt1, int paramInt2)
  {
    mHostView.updateState(paramStateWrapper, paramInt1, paramInt2);
  }
  
  static class DialogRootViewGroup
    extends ReactViewGroup
    implements RootView
  {
    private boolean hasAdjustedSize = false;
    private final JSTouchDispatcher mJSTouchDispatcher = new JSTouchDispatcher(this);
    @Nullable
    private StateWrapper mStateWrapper;
    private int viewHeight;
    private int viewWidth;
    
    public DialogRootViewGroup(Context paramContext)
    {
      super();
    }
    
    private EventDispatcher getEventDispatcher()
    {
      return ((UIManagerModule)getReactContext().getNativeModule(UIManagerModule.class)).getEventDispatcher();
    }
    
    private ReactContext getReactContext()
    {
      return (ReactContext)getContext();
    }
    
    private void updateFirstChildView()
    {
      if (getChildCount() > 0)
      {
        hasAdjustedSize = false;
        final int i = getChildAt(0).getId();
        if (mStateWrapper != null)
        {
          updateState(mStateWrapper, viewWidth, viewHeight);
          return;
        }
        ReactContext localReactContext = getReactContext();
        localReactContext.runOnNativeModulesQueueThread(new GuardedRunnable(localReactContext)
        {
          public void runGuarded()
          {
            ((UIManagerModule)ReactModalHostView.DialogRootViewGroup.this.getReactContext().getNativeModule(UIManagerModule.class)).updateNodeSize(i, viewWidth, viewHeight);
          }
        });
        return;
      }
      hasAdjustedSize = true;
    }
    
    public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
    {
      super.addView(paramView, paramInt, paramLayoutParams);
      if (hasAdjustedSize) {
        updateFirstChildView();
      }
    }
    
    public void handleException(Throwable paramThrowable)
    {
      getReactContext().handleException(new RuntimeException(paramThrowable));
    }
    
    public void onChildStartedNativeGesture(MotionEvent paramMotionEvent)
    {
      mJSTouchDispatcher.onChildStartedNativeGesture(paramMotionEvent, getEventDispatcher());
    }
    
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      mJSTouchDispatcher.handleTouchEvent(paramMotionEvent, getEventDispatcher());
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    
    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
      viewWidth = paramInt1;
      viewHeight = paramInt2;
      updateFirstChildView();
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      mJSTouchDispatcher.handleTouchEvent(paramMotionEvent, getEventDispatcher());
      super.onTouchEvent(paramMotionEvent);
      return true;
    }
    
    public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {}
    
    public void updateState(StateWrapper paramStateWrapper, int paramInt1, int paramInt2)
    {
      mStateWrapper = paramStateWrapper;
      WritableNativeMap localWritableNativeMap = new WritableNativeMap();
      localWritableNativeMap.putDouble("screenWidth", PixelUtil.toDIPFromPixel(paramInt1));
      localWritableNativeMap.putDouble("screenHeight", PixelUtil.toDIPFromPixel(paramInt2));
      paramStateWrapper.updateState(localWritableNativeMap);
    }
  }
  
  public static abstract interface OnRequestCloseListener
  {
    public abstract void onRequestClose(DialogInterface paramDialogInterface);
  }
}
