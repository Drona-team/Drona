package com.facebook.react;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.appregistry.AppRegistry;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.deviceinfo.DeviceInfoModule;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.JSTouchDispatcher;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactRoot;
import com.facebook.react.uimanager.RootView;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.systrace.Systrace;

public class ReactRootView
  extends FrameLayout
  implements RootView, ReactRoot
{
  private final ReactAndroidHWInputDeviceHelper mAndroidHWInputDeviceHelper = new ReactAndroidHWInputDeviceHelper(this);
  @Nullable
  private Bundle mAppProperties;
  @Nullable
  private CustomGlobalLayoutListener mCustomGlobalLayoutListener;
  private int mHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
  @Nullable
  private String mInitialUITemplate;
  private boolean mIsAttachedToInstance;
  @Nullable
  private String mJSModuleName;
  @Nullable
  private JSTouchDispatcher mJSTouchDispatcher;
  private int mLastHeight = 0;
  private int mLastWidth = 0;
  @Nullable
  private ReactInstanceManager mReactInstanceManager;
  @Nullable
  private ReactRootViewEventListener mRootViewEventListener;
  private int mRootViewTag;
  private boolean mShouldLogContentAppeared;
  private int mUIManagerType = 1;
  private final boolean mUseSurface;
  private boolean mWasMeasured = false;
  private int mWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
  
  public ReactRootView(Context paramContext)
  {
    super(paramContext);
    mUseSurface = false;
    init();
  }
  
  public ReactRootView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    mUseSurface = false;
    init();
  }
  
  public ReactRootView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    mUseSurface = false;
    init();
  }
  
  public ReactRootView(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    mUseSurface = paramBoolean;
    init();
  }
  
  private void attachToReactInstanceManager()
  {
    Systrace.beginSection(0L, "attachToReactInstanceManager");
    try
    {
      boolean bool = mIsAttachedToInstance;
      if (bool)
      {
        Systrace.endSection(0L);
        return;
      }
      mIsAttachedToInstance = true;
      ((ReactInstanceManager)Assertions.assertNotNull(mReactInstanceManager)).attachRootView(this);
      getViewTreeObserver().addOnGlobalLayoutListener(getCustomGlobalLayoutListener());
      Systrace.endSection(0L);
      return;
    }
    catch (Throwable localThrowable)
    {
      Systrace.endSection(0L);
      throw localThrowable;
    }
  }
  
  private void dispatchJSTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((mReactInstanceManager != null) && (mIsAttachedToInstance) && (mReactInstanceManager.getCurrentReactContext() != null))
    {
      if (mJSTouchDispatcher == null)
      {
        FLog.warn("ReactNative", "Unable to dispatch touch to JS before the dispatcher is available");
        return;
      }
      EventDispatcher localEventDispatcher = ((UIManagerModule)mReactInstanceManager.getCurrentReactContext().getNativeModule(UIManagerModule.class)).getEventDispatcher();
      mJSTouchDispatcher.handleTouchEvent(paramMotionEvent, localEventDispatcher);
      return;
    }
    FLog.warn("ReactNative", "Unable to dispatch touch to JS as the catalyst instance has not been attached");
  }
  
  private CustomGlobalLayoutListener getCustomGlobalLayoutListener()
  {
    if (mCustomGlobalLayoutListener == null) {
      mCustomGlobalLayoutListener = new CustomGlobalLayoutListener();
    }
    return mCustomGlobalLayoutListener;
  }
  
  private void init()
  {
    setClipChildren(false);
  }
  
  private void removeOnGlobalLayoutListener()
  {
    getViewTreeObserver().removeOnGlobalLayoutListener(getCustomGlobalLayoutListener());
  }
  
  private void updateRootLayoutSpecs(int paramInt1, int paramInt2)
  {
    if (mReactInstanceManager == null)
    {
      FLog.warn("ReactNative", "Unable to update root layout specs for uninitialized ReactInstanceManager");
      return;
    }
    ReactContext localReactContext = mReactInstanceManager.getCurrentReactContext();
    if (localReactContext != null) {
      UIManagerHelper.getUIManager(localReactContext, getUIManagerType()).updateRootLayoutSpecs(getRootViewTag(), paramInt1, paramInt2);
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    try
    {
      super.dispatchDraw(paramCanvas);
      return;
    }
    catch (StackOverflowError paramCanvas)
    {
      handleException(paramCanvas);
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((mReactInstanceManager != null) && (mIsAttachedToInstance) && (mReactInstanceManager.getCurrentReactContext() != null))
    {
      mAndroidHWInputDeviceHelper.handleKeyEvent(paramKeyEvent);
      return super.dispatchKeyEvent(paramKeyEvent);
    }
    FLog.warn("ReactNative", "Unable to handle key event as the catalyst instance has not been attached");
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    Assertions.assertCondition(mIsAttachedToInstance ^ true, "The application this ReactRootView was rendering was not unmounted before the ReactRootView was garbage collected. This usually means that your application is leaking large amounts of memory. To solve this, make sure to call ReactRootView#unmountReactApplication in the onDestroy() of your hosting Activity or in the onDestroyView() of your hosting Fragment.");
  }
  
  public Bundle getAppProperties()
  {
    return mAppProperties;
  }
  
  public int getHeightMeasureSpec()
  {
    return mHeightMeasureSpec;
  }
  
  public String getInitialUITemplate()
  {
    return mInitialUITemplate;
  }
  
  public String getJSModuleName()
  {
    return (String)Assertions.assertNotNull(mJSModuleName);
  }
  
  public ReactInstanceManager getReactInstanceManager()
  {
    return mReactInstanceManager;
  }
  
  public ViewGroup getRootViewGroup()
  {
    return this;
  }
  
  public int getRootViewTag()
  {
    return mRootViewTag;
  }
  
  public int getUIManagerType()
  {
    return mUIManagerType;
  }
  
  public int getWidthMeasureSpec()
  {
    return mWidthMeasureSpec;
  }
  
  public void handleException(Throwable paramThrowable)
  {
    if ((mReactInstanceManager != null) && (mReactInstanceManager.getCurrentReactContext() != null))
    {
      paramThrowable = new IllegalViewOperationException(paramThrowable.getMessage(), this, paramThrowable);
      mReactInstanceManager.getCurrentReactContext().handleException(paramThrowable);
      return;
    }
    throw new RuntimeException(paramThrowable);
  }
  
  public void onAttachedToReactInstance()
  {
    mJSTouchDispatcher = new JSTouchDispatcher(this);
    if (mRootViewEventListener != null) {
      mRootViewEventListener.onAttachedToReactInstance(this);
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (mIsAttachedToInstance)
    {
      removeOnGlobalLayoutListener();
      getViewTreeObserver().addOnGlobalLayoutListener(getCustomGlobalLayoutListener());
    }
  }
  
  public void onChildStartedNativeGesture(MotionEvent paramMotionEvent)
  {
    if ((mReactInstanceManager != null) && (mIsAttachedToInstance) && (mReactInstanceManager.getCurrentReactContext() != null))
    {
      if (mJSTouchDispatcher == null)
      {
        FLog.warn("ReactNative", "Unable to dispatch touch to JS before the dispatcher is available");
        return;
      }
      EventDispatcher localEventDispatcher = ((UIManagerModule)mReactInstanceManager.getCurrentReactContext().getNativeModule(UIManagerModule.class)).getEventDispatcher();
      mJSTouchDispatcher.onChildStartedNativeGesture(paramMotionEvent, localEventDispatcher);
      return;
    }
    FLog.warn("ReactNative", "Unable to dispatch touch to JS as the catalyst instance has not been attached");
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (mIsAttachedToInstance) {
      removeOnGlobalLayoutListener();
    }
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    if ((mReactInstanceManager != null) && (mIsAttachedToInstance) && (mReactInstanceManager.getCurrentReactContext() != null))
    {
      mAndroidHWInputDeviceHelper.clearFocus();
      super.onFocusChanged(paramBoolean, paramInt, paramRect);
      return;
    }
    FLog.warn("ReactNative", "Unable to handle focus changed event as the catalyst instance has not been attached");
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    dispatchJSTouchEvent(paramMotionEvent);
    return super.onInterceptTouchEvent(paramMotionEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (mUseSurface) {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (mUseSurface)
    {
      super.onMeasure(paramInt1, paramInt2);
      return;
    }
    Systrace.beginSection(0L, "ReactRootView.onMeasure");
    try
    {
      int i = mWidthMeasureSpec;
      int k = 0;
      if (paramInt1 == i)
      {
        i = mHeightMeasureSpec;
        if (paramInt2 == i)
        {
          i = 0;
          break label54;
        }
      }
      i = 1;
      label54:
      mWidthMeasureSpec = paramInt1;
      mHeightMeasureSpec = paramInt2;
      int j = View.MeasureSpec.getMode(paramInt1);
      int m;
      int n;
      int i1;
      if ((j != Integer.MIN_VALUE) && (j != 0))
      {
        j = View.MeasureSpec.getSize(paramInt1);
      }
      else
      {
        j = 0;
        paramInt1 = 0;
        for (;;)
        {
          m = getChildCount();
          if (j >= m) {
            break;
          }
          localObject = getChildAt(j);
          m = ((View)localObject).getLeft();
          n = ((View)localObject).getMeasuredWidth();
          i1 = ((View)localObject).getPaddingLeft();
          int i2 = ((View)localObject).getPaddingRight();
          paramInt1 = Math.max(paramInt1, m + n + i1 + i2);
          j += 1;
        }
        j = paramInt1;
      }
      paramInt1 = View.MeasureSpec.getMode(paramInt2);
      if ((paramInt1 != Integer.MIN_VALUE) && (paramInt1 != 0))
      {
        k = View.MeasureSpec.getSize(paramInt2);
      }
      else
      {
        paramInt1 = 0;
        paramInt2 = k;
        for (;;)
        {
          m = getChildCount();
          k = paramInt1;
          if (paramInt2 >= m) {
            break;
          }
          localObject = getChildAt(paramInt2);
          k = ((View)localObject).getTop();
          m = ((View)localObject).getMeasuredHeight();
          n = ((View)localObject).getPaddingTop();
          i1 = ((View)localObject).getPaddingBottom();
          paramInt1 = Math.max(paramInt1, k + m + n + i1);
          paramInt2 += 1;
        }
      }
      setMeasuredDimension(j, k);
      mWasMeasured = true;
      Object localObject = mReactInstanceManager;
      if (localObject != null)
      {
        boolean bool = mIsAttachedToInstance;
        if (!bool)
        {
          attachToReactInstanceManager();
          break label363;
        }
      }
      if (i == 0)
      {
        paramInt1 = mLastWidth;
        if (paramInt1 == j)
        {
          paramInt1 = mLastHeight;
          if (paramInt1 == k) {
            break label363;
          }
        }
      }
      updateRootLayoutSpecs(mWidthMeasureSpec, mHeightMeasureSpec);
      label363:
      mLastWidth = j;
      mLastHeight = k;
      Systrace.endSection(0L);
      return;
    }
    catch (Throwable localThrowable)
    {
      Systrace.endSection(0L);
      throw localThrowable;
    }
  }
  
  public void onStage(int paramInt)
  {
    if (paramInt != 101) {
      return;
    }
    onAttachedToReactInstance();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    dispatchJSTouchEvent(paramMotionEvent);
    super.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public void onViewAdded(View paramView)
  {
    super.onViewAdded(paramView);
    if (mShouldLogContentAppeared)
    {
      mShouldLogContentAppeared = false;
      if (mJSModuleName != null) {
        ReactMarker.logMarker(ReactMarkerConstants.CONTENT_APPEARED, mJSModuleName, mRootViewTag);
      }
    }
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if ((mReactInstanceManager != null) && (mIsAttachedToInstance) && (mReactInstanceManager.getCurrentReactContext() != null))
    {
      mAndroidHWInputDeviceHelper.onFocusChanged(paramView2);
      super.requestChildFocus(paramView1, paramView2);
      return;
    }
    FLog.warn("ReactNative", "Unable to handle child focus changed event as the catalyst instance has not been attached");
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if (getParent() != null) {
      getParent().requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
  
  public void runApplication()
  {
    Systrace.beginSection(0L, "ReactRootView.runApplication");
    try
    {
      Object localObject = mReactInstanceManager;
      if (localObject != null)
      {
        boolean bool = mIsAttachedToInstance;
        if (bool)
        {
          localObject = mReactInstanceManager.getCurrentReactContext();
          if (localObject == null)
          {
            Systrace.endSection(0L);
            return;
          }
          localObject = ((ReactContext)localObject).getCatalystInstance();
          String str = getJSModuleName();
          bool = mUseSurface;
          if (!bool)
          {
            bool = mWasMeasured;
            if (bool) {
              updateRootLayoutSpecs(mWidthMeasureSpec, mHeightMeasureSpec);
            }
            WritableNativeMap localWritableNativeMap = new WritableNativeMap();
            localWritableNativeMap.putDouble("rootTag", getRootViewTag());
            Bundle localBundle = getAppProperties();
            if (localBundle != null) {
              localWritableNativeMap.putMap("initialProps", Arguments.fromBundle(localBundle));
            }
            mShouldLogContentAppeared = true;
            ((AppRegistry)((CatalystInstance)localObject).getJSModule(AppRegistry.class)).runApplication(str, localWritableNativeMap);
          }
          Systrace.endSection(0L);
          return;
        }
      }
      Systrace.endSection(0L);
      return;
    }
    catch (Throwable localThrowable)
    {
      Systrace.endSection(0L);
      throw localThrowable;
    }
  }
  
  void sendEvent(String paramString, WritableMap paramWritableMap)
  {
    if (mReactInstanceManager != null) {
      ((DeviceEventManagerModule.RCTDeviceEventEmitter)mReactInstanceManager.getCurrentReactContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(paramString, paramWritableMap);
    }
  }
  
  public void setAppProperties(Bundle paramBundle)
  {
    UiThreadUtil.assertOnUiThread();
    mAppProperties = paramBundle;
    if (getRootViewTag() != 0) {
      runApplication();
    }
  }
  
  public void setEventListener(ReactRootViewEventListener paramReactRootViewEventListener)
  {
    mRootViewEventListener = paramReactRootViewEventListener;
  }
  
  public void setIsFabric(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = 2;
    } else {
      i = 1;
    }
    mUIManagerType = i;
  }
  
  public void setRootViewTag(int paramInt)
  {
    mRootViewTag = paramInt;
  }
  
  public void setShouldLogContentAppeared(boolean paramBoolean)
  {
    mShouldLogContentAppeared = paramBoolean;
  }
  
  void simulateAttachForTesting()
  {
    mIsAttachedToInstance = true;
    mJSTouchDispatcher = new JSTouchDispatcher(this);
  }
  
  public void startReactApplication(ReactInstanceManager paramReactInstanceManager, String paramString)
  {
    startReactApplication(paramReactInstanceManager, paramString, null);
  }
  
  public void startReactApplication(ReactInstanceManager paramReactInstanceManager, String paramString, Bundle paramBundle)
  {
    startReactApplication(paramReactInstanceManager, paramString, paramBundle, null);
  }
  
  public void startReactApplication(ReactInstanceManager paramReactInstanceManager, String paramString1, Bundle paramBundle, String paramString2)
  {
    Systrace.beginSection(0L, "startReactApplication");
    try
    {
      UiThreadUtil.assertOnUiThread();
      ReactInstanceManager localReactInstanceManager = mReactInstanceManager;
      boolean bool;
      if (localReactInstanceManager == null) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.assertCondition(bool, "This root view has already been attached to a catalyst instance manager");
      mReactInstanceManager = paramReactInstanceManager;
      mJSModuleName = paramString1;
      mAppProperties = paramBundle;
      mInitialUITemplate = paramString2;
      mReactInstanceManager.createReactContextInBackground();
      attachToReactInstanceManager();
      Systrace.endSection(0L);
      return;
    }
    catch (Throwable paramReactInstanceManager)
    {
      Systrace.endSection(0L);
      throw paramReactInstanceManager;
    }
  }
  
  public void unmountReactApplication()
  {
    if ((mReactInstanceManager != null) && (mIsAttachedToInstance))
    {
      mReactInstanceManager.detachRootView(this);
      mReactInstanceManager = null;
      mIsAttachedToInstance = false;
    }
    mShouldLogContentAppeared = false;
  }
  
  private class CustomGlobalLayoutListener
    implements ViewTreeObserver.OnGlobalLayoutListener
  {
    private int mDeviceRotation = 0;
    private int mKeyboardHeight = 0;
    private final int mMinKeyboardHeightDetected;
    private DisplayMetrics mScreenMetrics = new DisplayMetrics();
    private final Rect mVisibleViewArea;
    private DisplayMetrics mWindowMetrics = new DisplayMetrics();
    
    CustomGlobalLayoutListener()
    {
      DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(getContext().getApplicationContext());
      mVisibleViewArea = new Rect();
      mMinKeyboardHeightDetected = ((int)PixelUtil.toPixelFromDIP(60.0F));
    }
    
    private boolean areMetricsEqual(DisplayMetrics paramDisplayMetrics1, DisplayMetrics paramDisplayMetrics2)
    {
      if (Build.VERSION.SDK_INT >= 17) {
        return paramDisplayMetrics1.equals(paramDisplayMetrics2);
      }
      return (widthPixels == widthPixels) && (heightPixels == heightPixels) && (density == density) && (densityDpi == densityDpi) && (scaledDensity == scaledDensity) && (xdpi == xdpi) && (ydpi == ydpi);
    }
    
    private void checkForDeviceDimensionsChanges()
    {
      DisplayMetricsHolder.initDisplayMetrics(getContext());
      if ((!areMetricsEqual(mWindowMetrics, DisplayMetricsHolder.getWindowDisplayMetrics())) || (!areMetricsEqual(mScreenMetrics, DisplayMetricsHolder.getScreenDisplayMetrics())))
      {
        mWindowMetrics.setTo(DisplayMetricsHolder.getWindowDisplayMetrics());
        mScreenMetrics.setTo(DisplayMetricsHolder.getScreenDisplayMetrics());
        emitUpdateDimensionsEvent();
      }
    }
    
    private void checkForDeviceOrientationChanges()
    {
      int i = ((WindowManager)getContext().getSystemService("window")).getDefaultDisplay().getRotation();
      if (mDeviceRotation == i) {
        return;
      }
      mDeviceRotation = i;
      emitOrientationChanged(i);
    }
    
    private void checkForKeyboardEvents()
    {
      getRootView().getWindowVisibleDisplayFrame(mVisibleViewArea);
      int k = getWindowDisplayMetricsheightPixels - mVisibleViewArea.bottom;
      int i = mKeyboardHeight;
      int j = 1;
      if ((i != k) && (k > mMinKeyboardHeightDetected)) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0)
      {
        mKeyboardHeight = k;
        sendEvent("keyboardDidShow", createKeyboardEventPayload(PixelUtil.toDIPFromPixel(mVisibleViewArea.bottom), PixelUtil.toDIPFromPixel(mVisibleViewArea.left), PixelUtil.toDIPFromPixel(mVisibleViewArea.width()), PixelUtil.toDIPFromPixel(mKeyboardHeight)));
        return;
      }
      if ((mKeyboardHeight != 0) && (k <= mMinKeyboardHeightDetected)) {
        i = j;
      } else {
        i = 0;
      }
      if (i != 0)
      {
        mKeyboardHeight = 0;
        sendEvent("keyboardDidHide", createKeyboardEventPayload(PixelUtil.toDIPFromPixel(mVisibleViewArea.height()), 0.0D, PixelUtil.toDIPFromPixel(mVisibleViewArea.width()), 0.0D));
      }
    }
    
    private WritableMap createKeyboardEventPayload(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      WritableMap localWritableMap1 = Arguments.createMap();
      WritableMap localWritableMap2 = Arguments.createMap();
      localWritableMap2.putDouble("height", paramDouble4);
      localWritableMap2.putDouble("screenX", paramDouble2);
      localWritableMap2.putDouble("width", paramDouble3);
      localWritableMap2.putDouble("screenY", paramDouble1);
      localWritableMap1.putMap("endCoordinates", localWritableMap2);
      localWritableMap1.putString("easing", "keyboard");
      localWritableMap1.putDouble("duration", 0.0D);
      return localWritableMap1;
    }
    
    private void emitOrientationChanged(int paramInt)
    {
      boolean bool = true;
      String str;
      double d;
      switch (paramInt)
      {
      default: 
        return;
      case 3: 
        str = "landscape-secondary";
        d = 90.0D;
        break;
      case 2: 
        str = "portrait-secondary";
        d = 180.0D;
        break;
      case 1: 
        str = "landscape-primary";
        d = -90.0D;
        break;
      case 0: 
        str = "portrait-primary";
        d = 0.0D;
      }
      bool = false;
      WritableMap localWritableMap = Arguments.createMap();
      localWritableMap.putString("name", str);
      localWritableMap.putDouble("rotationDegrees", d);
      localWritableMap.putBoolean("isLandscape", bool);
      sendEvent("namedOrientationDidChange", localWritableMap);
    }
    
    private void emitUpdateDimensionsEvent()
    {
      ((DeviceInfoModule)mReactInstanceManager.getCurrentReactContext().getNativeModule(DeviceInfoModule.class)).emitUpdateDimensionsEvent();
    }
    
    public void onGlobalLayout()
    {
      if ((mReactInstanceManager != null) && (mIsAttachedToInstance))
      {
        if (mReactInstanceManager.getCurrentReactContext() == null) {
          return;
        }
        checkForKeyboardEvents();
        checkForDeviceOrientationChanges();
        checkForDeviceDimensionsChanges();
      }
    }
  }
  
  public static abstract interface ReactRootViewEventListener
  {
    public abstract void onAttachedToReactInstance(ReactRootView paramReactRootView);
  }
}
