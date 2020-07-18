package androidx.core.view;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.DragShadowBuilder;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnUnhandledKeyEventListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.R.id;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewCompat
{
  private static final int[] ACCESSIBILITY_ACTIONS_RESOURCE_IDS = { R.id.accessibility_custom_action_0, R.id.accessibility_custom_action_1, R.id.accessibility_custom_action_2, R.id.accessibility_custom_action_3, R.id.accessibility_custom_action_4, R.id.accessibility_custom_action_5, R.id.accessibility_custom_action_6, R.id.accessibility_custom_action_7, R.id.accessibility_custom_action_8, R.id.accessibility_custom_action_9, R.id.accessibility_custom_action_10, R.id.accessibility_custom_action_11, R.id.accessibility_custom_action_12, R.id.accessibility_custom_action_13, R.id.accessibility_custom_action_14, R.id.accessibility_custom_action_15, R.id.accessibility_custom_action_16, R.id.accessibility_custom_action_17, R.id.accessibility_custom_action_18, R.id.accessibility_custom_action_19, R.id.accessibility_custom_action_20, R.id.accessibility_custom_action_21, R.id.accessibility_custom_action_22, R.id.accessibility_custom_action_23, R.id.accessibility_custom_action_24, R.id.accessibility_custom_action_25, R.id.accessibility_custom_action_26, R.id.accessibility_custom_action_27, R.id.accessibility_custom_action_28, R.id.accessibility_custom_action_29, R.id.accessibility_custom_action_30, R.id.accessibility_custom_action_31 };
  public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
  public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
  public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
  @Deprecated
  public static final int LAYER_TYPE_HARDWARE = 2;
  @Deprecated
  public static final int LAYER_TYPE_NONE = 0;
  @Deprecated
  public static final int LAYER_TYPE_SOFTWARE = 1;
  public static final int LAYOUT_DIRECTION_INHERIT = 2;
  public static final int LAYOUT_DIRECTION_LOCALE = 3;
  public static final int LAYOUT_DIRECTION_LTR = 0;
  public static final int LAYOUT_DIRECTION_RTL = 1;
  @Deprecated
  public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
  @Deprecated
  public static final int MEASURED_SIZE_MASK = 16777215;
  @Deprecated
  public static final int MEASURED_STATE_MASK = -16777216;
  @Deprecated
  public static final int MEASURED_STATE_TOO_SMALL = 16777216;
  @Deprecated
  public static final int OVER_SCROLL_ALWAYS = 0;
  @Deprecated
  public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
  @Deprecated
  public static final int OVER_SCROLL_NEVER = 2;
  public static final int SCROLL_AXIS_HORIZONTAL = 1;
  public static final int SCROLL_AXIS_NONE = 0;
  public static final int SCROLL_AXIS_VERTICAL = 2;
  public static final int SCROLL_INDICATOR_BOTTOM = 2;
  public static final int SCROLL_INDICATOR_END = 32;
  public static final int SCROLL_INDICATOR_LEFT = 4;
  public static final int SCROLL_INDICATOR_RIGHT = 8;
  public static final int SCROLL_INDICATOR_START = 16;
  public static final int SCROLL_INDICATOR_TOP = 1;
  private static final String TAG = "ViewCompat";
  public static final int TYPE_NON_TOUCH = 1;
  public static final int TYPE_TOUCH = 0;
  private static boolean sAccessibilityDelegateCheckFailed;
  private static Field sAccessibilityDelegateField;
  private static AccessibilityPaneVisibilityManager sAccessibilityPaneVisibilityManager = new AccessibilityPaneVisibilityManager();
  private static Method sChildrenDrawingOrderMethod;
  private static Method sDispatchFinishTemporaryDetach;
  private static Method sDispatchStartTemporaryDetach;
  private static Field sMinHeightField;
  private static boolean sMinHeightFieldFetched;
  private static Field sMinWidthField;
  private static boolean sMinWidthFieldFetched;
  private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
  private static boolean sTempDetachBound;
  private static ThreadLocal<Rect> sThreadLocalRect;
  private static WeakHashMap<View, String> sTransitionNameMap;
  private static WeakHashMap<View, ViewPropertyAnimatorCompat> sViewPropertyAnimatorMap = null;
  
  static
  {
    sAccessibilityDelegateCheckFailed = false;
  }
  
  protected ViewCompat() {}
  
  private static AccessibilityViewProperty accessibilityHeadingProperty()
  {
    new AccessibilityViewProperty(R.id.tag_accessibility_heading, Boolean.class, 28)
    {
      Boolean frameworkGet(View paramAnonymousView)
      {
        return Boolean.valueOf(paramAnonymousView.isAccessibilityHeading());
      }
      
      void frameworkSet(View paramAnonymousView, Boolean paramAnonymousBoolean)
      {
        paramAnonymousView.setAccessibilityHeading(paramAnonymousBoolean.booleanValue());
      }
      
      boolean shouldUpdate(Boolean paramAnonymousBoolean1, Boolean paramAnonymousBoolean2)
      {
        return booleanNullToFalseEquals(paramAnonymousBoolean1, paramAnonymousBoolean2) ^ true;
      }
    };
  }
  
  public static int addAccessibilityAction(View paramView, CharSequence paramCharSequence, AccessibilityViewCommand paramAccessibilityViewCommand)
  {
    int i = getAvailableActionIdFromResources(paramView);
    if (i != -1) {
      addAccessibilityAction(paramView, new AccessibilityNodeInfoCompat.AccessibilityActionCompat(i, paramCharSequence, paramAccessibilityViewCommand));
    }
    return i;
  }
  
  private static void addAccessibilityAction(View paramView, AccessibilityNodeInfoCompat.AccessibilityActionCompat paramAccessibilityActionCompat)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      getOrCreateAccessibilityDelegateCompat(paramView);
      removeActionWithId(paramAccessibilityActionCompat.getId(), paramView);
      getActionList(paramView).add(paramAccessibilityActionCompat);
      notifyViewAccessibilityStateChangedIfNeeded(paramView, 0);
    }
  }
  
  public static void addKeyboardNavigationClusters(View paramView, Collection paramCollection, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.addKeyboardNavigationClusters(paramCollection, paramInt);
    }
  }
  
  public static void addOnUnhandledKeyEventListener(View paramView, OnUnhandledKeyEventListenerCompat paramOnUnhandledKeyEventListenerCompat)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  public static ViewPropertyAnimatorCompat animate(View paramView)
  {
    if (sViewPropertyAnimatorMap == null) {
      sViewPropertyAnimatorMap = new WeakHashMap();
    }
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat2 = (ViewPropertyAnimatorCompat)sViewPropertyAnimatorMap.get(paramView);
    ViewPropertyAnimatorCompat localViewPropertyAnimatorCompat1 = localViewPropertyAnimatorCompat2;
    if (localViewPropertyAnimatorCompat2 == null)
    {
      localViewPropertyAnimatorCompat1 = new ViewPropertyAnimatorCompat(paramView);
      sViewPropertyAnimatorMap.put(paramView, localViewPropertyAnimatorCompat1);
    }
    return localViewPropertyAnimatorCompat1;
  }
  
  private static void bindTempDetach()
  {
    try
    {
      Method localMethod = View.class.getDeclaredMethod("dispatchStartTemporaryDetach", new Class[0]);
      sDispatchStartTemporaryDetach = localMethod;
      localMethod = View.class.getDeclaredMethod("dispatchFinishTemporaryDetach", new Class[0]);
      sDispatchFinishTemporaryDetach = localMethod;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Log.e("ViewCompat", "Couldn't find method", localNoSuchMethodException);
    }
    sTempDetachBound = true;
  }
  
  public static boolean canScrollHorizontally(View paramView, int paramInt)
  {
    return paramView.canScrollHorizontally(paramInt);
  }
  
  public static boolean canScrollVertically(View paramView, int paramInt)
  {
    return paramView.canScrollVertically(paramInt);
  }
  
  public static void cancelDragAndDrop(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 24) {
      paramView.cancelDragAndDrop();
    }
  }
  
  public static int combineMeasuredStates(int paramInt1, int paramInt2)
  {
    return View.combineMeasuredStates(paramInt1, paramInt2);
  }
  
  private static void compatOffsetLeftAndRight(View paramView, int paramInt)
  {
    paramView.offsetLeftAndRight(paramInt);
    if (paramView.getVisibility() == 0)
    {
      tickleInvalidationFlag(paramView);
      paramView = paramView.getParent();
      if ((paramView instanceof View)) {
        tickleInvalidationFlag((View)paramView);
      }
    }
  }
  
  private static void compatOffsetTopAndBottom(View paramView, int paramInt)
  {
    paramView.offsetTopAndBottom(paramInt);
    if (paramView.getVisibility() == 0)
    {
      tickleInvalidationFlag(paramView);
      paramView = paramView.getParent();
      if ((paramView instanceof View)) {
        tickleInvalidationFlag((View)paramView);
      }
    }
  }
  
  public static WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
  {
    Object localObject = paramWindowInsetsCompat;
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramWindowInsetsCompat = (WindowInsets)WindowInsetsCompat.unwrap(paramWindowInsetsCompat);
      localObject = paramView.dispatchApplyWindowInsets(paramWindowInsetsCompat);
      paramView = paramWindowInsetsCompat;
      if (!localObject.equals(paramWindowInsetsCompat)) {
        paramView = new WindowInsets((WindowInsets)localObject);
      }
      localObject = WindowInsetsCompat.wrap(paramView);
    }
    return localObject;
  }
  
  public static void dispatchFinishTemporaryDetach(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      paramView.dispatchFinishTemporaryDetach();
      return;
    }
    if (!sTempDetachBound) {
      bindTempDetach();
    }
    if (sDispatchFinishTemporaryDetach != null)
    {
      Method localMethod = sDispatchFinishTemporaryDetach;
      try
      {
        localMethod.invoke(paramView, new Object[0]);
        return;
      }
      catch (Exception paramView)
      {
        Log.d("ViewCompat", "Error calling dispatchFinishTemporaryDetach", paramView);
        return;
      }
    }
    paramView.onFinishTemporaryDetach();
  }
  
  public static boolean dispatchNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
    }
    if ((paramView instanceof NestedScrollingChild)) {
      return ((NestedScrollingChild)paramView).dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
    }
    return false;
  }
  
  public static boolean dispatchNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.dispatchNestedPreFling(paramFloat1, paramFloat2);
    }
    if ((paramView instanceof NestedScrollingChild)) {
      return ((NestedScrollingChild)paramView).dispatchNestedPreFling(paramFloat1, paramFloat2);
    }
    return false;
  }
  
  public static boolean dispatchNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
    }
    if ((paramView instanceof NestedScrollingChild)) {
      return ((NestedScrollingChild)paramView).dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
    }
    return false;
  }
  
  public static boolean dispatchNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3)
  {
    if ((paramView instanceof NestedScrollingChild2)) {
      return ((NestedScrollingChild2)paramView).dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3);
    }
    if (paramInt3 == 0) {
      return dispatchNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
    }
    return false;
  }
  
  public static void dispatchNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int paramInt5, int[] paramArrayOfInt2)
  {
    if ((paramView instanceof NestedScrollingChild3))
    {
      ((NestedScrollingChild3)paramView).dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt1, paramInt5, paramArrayOfInt2);
      return;
    }
    dispatchNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt1, paramInt5);
  }
  
  public static boolean dispatchNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
    }
    if ((paramView instanceof NestedScrollingChild)) {
      return ((NestedScrollingChild)paramView).dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
    }
    return false;
  }
  
  public static boolean dispatchNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5)
  {
    if ((paramView instanceof NestedScrollingChild2)) {
      return ((NestedScrollingChild2)paramView).dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5);
    }
    if (paramInt5 == 0) {
      return dispatchNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
    }
    return false;
  }
  
  public static void dispatchStartTemporaryDetach(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      paramView.dispatchStartTemporaryDetach();
      return;
    }
    if (!sTempDetachBound) {
      bindTempDetach();
    }
    if (sDispatchStartTemporaryDetach != null)
    {
      Method localMethod = sDispatchStartTemporaryDetach;
      try
      {
        localMethod.invoke(paramView, new Object[0]);
        return;
      }
      catch (Exception paramView)
      {
        Log.d("ViewCompat", "Error calling dispatchStartTemporaryDetach", paramView);
        return;
      }
    }
    paramView.onStartTemporaryDetach();
  }
  
  static boolean dispatchUnhandledKeyEventBeforeCallback(View paramView, KeyEvent paramKeyEvent)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return false;
    }
    return UnhandledKeyEventManager.getSectionView(paramView).dispatch(paramView, paramKeyEvent);
  }
  
  static boolean dispatchUnhandledKeyEventBeforeHierarchy(View paramView, KeyEvent paramKeyEvent)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return false;
    }
    return UnhandledKeyEventManager.getSectionView(paramView).preDispatch(paramKeyEvent);
  }
  
  public static void enableAccessibleClickableSpanSupport(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      getOrCreateAccessibilityDelegateCompat(paramView);
    }
  }
  
  public static int generateViewId()
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return View.generateViewId();
    }
    int k;
    int i;
    do
    {
      k = sNextGeneratedId.get();
      int j = k + 1;
      i = j;
      if (j > 16777215) {
        i = 1;
      }
    } while (!sNextGeneratedId.compareAndSet(k, i));
    return k;
  }
  
  public static AccessibilityDelegateCompat getAccessibilityDelegate(View paramView)
  {
    paramView = getAccessibilityDelegateInternal(paramView);
    if (paramView == null) {
      return null;
    }
    if ((paramView instanceof AccessibilityDelegateCompat.AccessibilityDelegateAdapter)) {
      return mCompat;
    }
    return new AccessibilityDelegateCompat(paramView);
  }
  
  private static View.AccessibilityDelegate getAccessibilityDelegateInternal(View paramView)
  {
    if (sAccessibilityDelegateCheckFailed) {
      return null;
    }
    if (sAccessibilityDelegateField == null) {}
    try
    {
      sAccessibilityDelegateField = View.class.getDeclaredField("mAccessibilityDelegate");
      sAccessibilityDelegateField.setAccessible(true);
    }
    catch (Throwable paramView)
    {
      for (;;) {}
    }
    sAccessibilityDelegateCheckFailed = true;
    return null;
    try
    {
      paramView = sAccessibilityDelegateField.get(paramView);
      boolean bool = paramView instanceof View.AccessibilityDelegate;
      if (bool)
      {
        paramView = (View.AccessibilityDelegate)paramView;
        return paramView;
      }
      return null;
    }
    catch (Throwable paramView)
    {
      for (;;) {}
    }
    sAccessibilityDelegateCheckFailed = true;
    return null;
  }
  
  public static int getAccessibilityLiveRegion(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return paramView.getAccessibilityLiveRegion();
    }
    return 0;
  }
  
  public static AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramView = paramView.getAccessibilityNodeProvider();
      if (paramView != null) {
        return new AccessibilityNodeProviderCompat(paramView);
      }
    }
    return null;
  }
  
  public static CharSequence getAccessibilityPaneTitle(View paramView)
  {
    return (CharSequence)paneTitleProperty().getFragment(paramView);
  }
  
  private static List getActionList(View paramView)
  {
    ArrayList localArrayList2 = (ArrayList)paramView.getTag(R.id.tag_accessibility_actions);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      paramView.setTag(R.id.tag_accessibility_actions, localArrayList1);
    }
    return localArrayList1;
  }
  
  public static float getAlpha(View paramView)
  {
    return paramView.getAlpha();
  }
  
  private static int getAvailableActionIdFromResources(View paramView)
  {
    paramView = getActionList(paramView);
    int i = 0;
    int j = -1;
    while ((i < ACCESSIBILITY_ACTIONS_RESOURCE_IDS.length) && (j == -1))
    {
      int i1 = ACCESSIBILITY_ACTIONS_RESOURCE_IDS[i];
      int m = 0;
      int k = 1;
      while (m < paramView.size())
      {
        int n;
        if (((AccessibilityNodeInfoCompat.AccessibilityActionCompat)paramView.get(m)).getId() != i1) {
          n = 1;
        } else {
          n = 0;
        }
        k &= n;
        m += 1;
      }
      if (k != 0) {
        j = i1;
      }
      i += 1;
    }
    return j;
  }
  
  public static ColorStateList getBackgroundTintList(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.getBackgroundTintList();
    }
    if ((paramView instanceof TintableBackgroundView)) {
      return ((TintableBackgroundView)paramView).getSupportBackgroundTintList();
    }
    return null;
  }
  
  public static PorterDuff.Mode getBackgroundTintMode(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.getBackgroundTintMode();
    }
    if ((paramView instanceof TintableBackgroundView)) {
      return ((TintableBackgroundView)paramView).getSupportBackgroundTintMode();
    }
    return null;
  }
  
  public static Rect getClipBounds(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      return paramView.getClipBounds();
    }
    return null;
  }
  
  public static Display getDisplay(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return paramView.getDisplay();
    }
    if (isAttachedToWindow(paramView)) {
      return ((WindowManager)paramView.getContext().getSystemService("window")).getDefaultDisplay();
    }
    return null;
  }
  
  public static float getElevation(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.getElevation();
    }
    return 0.0F;
  }
  
  private static Rect getEmptyTempRect()
  {
    if (sThreadLocalRect == null) {
      sThreadLocalRect = new ThreadLocal();
    }
    Rect localRect2 = (Rect)sThreadLocalRect.get();
    Rect localRect1 = localRect2;
    if (localRect2 == null)
    {
      localRect1 = new Rect();
      sThreadLocalRect.set(localRect1);
    }
    localRect1.setEmpty();
    return localRect1;
  }
  
  public static boolean getFitsSystemWindows(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.getFitsSystemWindows();
    }
    return false;
  }
  
  public static int getImportantForAccessibility(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.getImportantForAccessibility();
    }
    return 0;
  }
  
  public static int getImportantForAutofill(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView.getImportantForAutofill();
    }
    return 0;
  }
  
  public static int getLabelFor(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return paramView.getLabelFor();
    }
    return 0;
  }
  
  public static int getLayerType(View paramView)
  {
    return paramView.getLayerType();
  }
  
  public static int getLayoutDirection(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return paramView.getLayoutDirection();
    }
    return 0;
  }
  
  public static Matrix getMatrix(View paramView)
  {
    return paramView.getMatrix();
  }
  
  public static int getMeasuredHeightAndState(View paramView)
  {
    return paramView.getMeasuredHeightAndState();
  }
  
  public static int getMeasuredState(View paramView)
  {
    return paramView.getMeasuredState();
  }
  
  public static int getMeasuredWidthAndState(View paramView)
  {
    return paramView.getMeasuredWidthAndState();
  }
  
  public static int getMinimumHeight(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.getMinimumHeight();
    }
    if (!sMinHeightFieldFetched) {}
    try
    {
      localField = View.class.getDeclaredField("mMinHeight");
      sMinHeightField = localField;
      localField = sMinHeightField;
      localField.setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Field localField;
      for (;;) {}
    }
    sMinHeightFieldFetched = true;
    if (sMinHeightField != null) {
      localField = sMinHeightField;
    }
    try
    {
      paramView = localField.get(paramView);
      paramView = (Integer)paramView;
      int i = paramView.intValue();
      return i;
    }
    catch (Exception paramView)
    {
      for (;;) {}
    }
    return 0;
  }
  
  public static int getMinimumWidth(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.getMinimumWidth();
    }
    if (!sMinWidthFieldFetched) {}
    try
    {
      localField = View.class.getDeclaredField("mMinWidth");
      sMinWidthField = localField;
      localField = sMinWidthField;
      localField.setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Field localField;
      for (;;) {}
    }
    sMinWidthFieldFetched = true;
    if (sMinWidthField != null) {
      localField = sMinWidthField;
    }
    try
    {
      paramView = localField.get(paramView);
      paramView = (Integer)paramView;
      int i = paramView.intValue();
      return i;
    }
    catch (Exception paramView)
    {
      for (;;) {}
    }
    return 0;
  }
  
  public static int getNextClusterForwardId(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView.getNextClusterForwardId();
    }
    return -1;
  }
  
  static AccessibilityDelegateCompat getOrCreateAccessibilityDelegateCompat(View paramView)
  {
    AccessibilityDelegateCompat localAccessibilityDelegateCompat2 = getAccessibilityDelegate(paramView);
    AccessibilityDelegateCompat localAccessibilityDelegateCompat1 = localAccessibilityDelegateCompat2;
    if (localAccessibilityDelegateCompat2 == null) {
      localAccessibilityDelegateCompat1 = new AccessibilityDelegateCompat();
    }
    setAccessibilityDelegate(paramView, localAccessibilityDelegateCompat1);
    return localAccessibilityDelegateCompat1;
  }
  
  public static int getOverScrollMode(View paramView)
  {
    return paramView.getOverScrollMode();
  }
  
  public static int getPaddingEnd(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return paramView.getPaddingEnd();
    }
    return paramView.getPaddingRight();
  }
  
  public static int getPaddingStart(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return paramView.getPaddingStart();
    }
    return paramView.getPaddingLeft();
  }
  
  public static ViewParent getParentForAccessibility(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.getParentForAccessibility();
    }
    return paramView.getParent();
  }
  
  public static float getPivotX(View paramView)
  {
    return paramView.getPivotX();
  }
  
  public static float getPivotY(View paramView)
  {
    return paramView.getPivotY();
  }
  
  public static float getRotation(View paramView)
  {
    return paramView.getRotation();
  }
  
  public static float getRotationX(View paramView)
  {
    return paramView.getRotationX();
  }
  
  public static float getRotationY(View paramView)
  {
    return paramView.getRotationY();
  }
  
  public static float getScaleX(View paramView)
  {
    return paramView.getScaleX();
  }
  
  public static float getScaleY(View paramView)
  {
    return paramView.getScaleY();
  }
  
  public static int getScrollIndicators(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return paramView.getScrollIndicators();
    }
    return 0;
  }
  
  public static String getTransitionName(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.getTransitionName();
    }
    if (sTransitionNameMap == null) {
      return null;
    }
    return (String)sTransitionNameMap.get(paramView);
  }
  
  public static float getTranslationX(View paramView)
  {
    return paramView.getTranslationX();
  }
  
  public static float getTranslationY(View paramView)
  {
    return paramView.getTranslationY();
  }
  
  public static float getTranslationZ(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.getTranslationZ();
    }
    return 0.0F;
  }
  
  public static int getWindowSystemUiVisibility(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.getWindowSystemUiVisibility();
    }
    return 0;
  }
  
  public static float getX(View paramView)
  {
    return paramView.getX();
  }
  
  public static float getY(View paramView)
  {
    return paramView.getY();
  }
  
  public static float getZ(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.getZ();
    }
    return 0.0F;
  }
  
  public static boolean hasAccessibilityDelegate(View paramView)
  {
    return getAccessibilityDelegateInternal(paramView) != null;
  }
  
  public static boolean hasExplicitFocusable(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView.hasExplicitFocusable();
    }
    return paramView.hasFocusable();
  }
  
  public static boolean hasNestedScrollingParent(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.hasNestedScrollingParent();
    }
    if ((paramView instanceof NestedScrollingChild)) {
      return ((NestedScrollingChild)paramView).hasNestedScrollingParent();
    }
    return false;
  }
  
  public static boolean hasNestedScrollingParent(View paramView, int paramInt)
  {
    if ((paramView instanceof NestedScrollingChild2)) {
      ((NestedScrollingChild2)paramView).hasNestedScrollingParent(paramInt);
    } else if (paramInt == 0) {
      return hasNestedScrollingParent(paramView);
    }
    return false;
  }
  
  public static boolean hasOnClickListeners(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 15) {
      return paramView.hasOnClickListeners();
    }
    return false;
  }
  
  public static boolean hasOverlappingRendering(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.hasOverlappingRendering();
    }
    return true;
  }
  
  public static boolean hasTransientState(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.hasTransientState();
    }
    return false;
  }
  
  public static boolean isAccessibilityHeading(View paramView)
  {
    paramView = (Boolean)accessibilityHeadingProperty().getFragment(paramView);
    if (paramView == null) {
      return false;
    }
    return paramView.booleanValue();
  }
  
  public static boolean isAttachedToWindow(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return paramView.isAttachedToWindow();
    }
    return paramView.getWindowToken() != null;
  }
  
  public static boolean isFocusedByDefault(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView.isFocusedByDefault();
    }
    return false;
  }
  
  public static boolean isImportantForAccessibility(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.isImportantForAccessibility();
    }
    return true;
  }
  
  public static boolean isImportantForAutofill(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView.isImportantForAutofill();
    }
    return true;
  }
  
  public static boolean isInLayout(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      return paramView.isInLayout();
    }
    return false;
  }
  
  public static boolean isKeyboardNavigationCluster(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView.isKeyboardNavigationCluster();
    }
    return false;
  }
  
  public static boolean isLaidOut(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return paramView.isLaidOut();
    }
    return (paramView.getWidth() > 0) && (paramView.getHeight() > 0);
  }
  
  public static boolean isLayoutDirectionResolved(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return paramView.isLayoutDirectionResolved();
    }
    return false;
  }
  
  public static boolean isNestedScrollingEnabled(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.isNestedScrollingEnabled();
    }
    if ((paramView instanceof NestedScrollingChild)) {
      return ((NestedScrollingChild)paramView).isNestedScrollingEnabled();
    }
    return false;
  }
  
  public static boolean isOpaque(View paramView)
  {
    return paramView.isOpaque();
  }
  
  public static boolean isPaddingRelative(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return paramView.isPaddingRelative();
    }
    return false;
  }
  
  public static boolean isScreenReaderFocusable(View paramView)
  {
    paramView = (Boolean)screenReaderFocusableProperty().getFragment(paramView);
    if (paramView == null) {
      return false;
    }
    return paramView.booleanValue();
  }
  
  public static void jumpDrawablesToCurrentState(View paramView)
  {
    paramView.jumpDrawablesToCurrentState();
  }
  
  public static View keyboardNavigationClusterSearch(View paramView1, View paramView2, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView1.keyboardNavigationClusterSearch(paramView2, paramInt);
    }
    return null;
  }
  
  static void notifyViewAccessibilityStateChangedIfNeeded(View paramView, int paramInt)
  {
    if (!((AccessibilityManager)paramView.getContext().getSystemService("accessibility")).isEnabled()) {
      return;
    }
    int i;
    if (getAccessibilityPaneTitle(paramView) != null) {
      i = 1;
    } else {
      i = 0;
    }
    if ((getAccessibilityLiveRegion(paramView) == 0) && ((i == 0) || (paramView.getVisibility() != 0)))
    {
      if (paramView.getParent() == null) {
        return;
      }
      try
      {
        paramView.getParent().notifySubtreeAccessibilityStateChanged(paramView, paramView, paramInt);
        return;
      }
      catch (AbstractMethodError localAbstractMethodError)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramView.getParent().getClass().getSimpleName());
        localStringBuilder.append(" does not fully implement ViewParent");
        Log.e("ViewCompat", localStringBuilder.toString(), localAbstractMethodError);
        return;
      }
    }
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
    if (i != 0) {
      i = 32;
    } else {
      i = 2048;
    }
    localAccessibilityEvent.setEventType(i);
    localAccessibilityEvent.setContentChangeTypes(paramInt);
    paramView.sendAccessibilityEventUnchecked(localAccessibilityEvent);
  }
  
  public static void offsetLeftAndRight(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      paramView.offsetLeftAndRight(paramInt);
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      Rect localRect = getEmptyTempRect();
      boolean bool = false;
      ViewParent localViewParent = paramView.getParent();
      if ((localViewParent instanceof View))
      {
        View localView = (View)localViewParent;
        localRect.set(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom());
        bool = localRect.intersects(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom()) ^ true;
      }
      compatOffsetLeftAndRight(paramView, paramInt);
      if ((bool) && (localRect.intersect(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom()))) {
        ((View)localViewParent).invalidate(localRect);
      }
    }
    else
    {
      compatOffsetLeftAndRight(paramView, paramInt);
    }
  }
  
  public static void offsetTopAndBottom(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      paramView.offsetTopAndBottom(paramInt);
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      Rect localRect = getEmptyTempRect();
      boolean bool = false;
      ViewParent localViewParent = paramView.getParent();
      if ((localViewParent instanceof View))
      {
        View localView = (View)localViewParent;
        localRect.set(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom());
        bool = localRect.intersects(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom()) ^ true;
      }
      compatOffsetTopAndBottom(paramView, paramInt);
      if ((bool) && (localRect.intersect(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom()))) {
        ((View)localViewParent).invalidate(localRect);
      }
    }
    else
    {
      compatOffsetTopAndBottom(paramView, paramInt);
    }
  }
  
  public static WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
  {
    Object localObject = paramWindowInsetsCompat;
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramWindowInsetsCompat = (WindowInsets)WindowInsetsCompat.unwrap(paramWindowInsetsCompat);
      localObject = paramView.onApplyWindowInsets(paramWindowInsetsCompat);
      paramView = paramWindowInsetsCompat;
      if (!localObject.equals(paramWindowInsetsCompat)) {
        paramView = new WindowInsets((WindowInsets)localObject);
      }
      localObject = WindowInsetsCompat.wrap(paramView);
    }
    return localObject;
  }
  
  public static void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    paramView.onInitializeAccessibilityEvent(paramAccessibilityEvent);
  }
  
  public static void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    paramView.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfoCompat.unwrap());
  }
  
  public static void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    paramView.onPopulateAccessibilityEvent(paramAccessibilityEvent);
  }
  
  private static AccessibilityViewProperty paneTitleProperty()
  {
    new AccessibilityViewProperty(R.id.tag_accessibility_pane_title, CharSequence.class, 8, 28)
    {
      CharSequence frameworkGet(View paramAnonymousView)
      {
        return paramAnonymousView.getAccessibilityPaneTitle();
      }
      
      void frameworkSet(View paramAnonymousView, CharSequence paramAnonymousCharSequence)
      {
        paramAnonymousView.setAccessibilityPaneTitle(paramAnonymousCharSequence);
      }
      
      boolean shouldUpdate(CharSequence paramAnonymousCharSequence1, CharSequence paramAnonymousCharSequence2)
      {
        return TextUtils.equals(paramAnonymousCharSequence1, paramAnonymousCharSequence2) ^ true;
      }
    };
  }
  
  public static boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramView.performAccessibilityAction(paramInt, paramBundle);
    }
    return false;
  }
  
  public static void postInvalidateOnAnimation(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramView.postInvalidateOnAnimation();
      return;
    }
    paramView.postInvalidate();
  }
  
  public static void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramView.postInvalidateOnAnimation(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    paramView.postInvalidate(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void postOnAnimation(View paramView, Runnable paramRunnable)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramView.postOnAnimation(paramRunnable);
      return;
    }
    paramView.postDelayed(paramRunnable, ValueAnimator.getFrameDelay());
  }
  
  public static void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramView.postOnAnimationDelayed(paramRunnable, paramLong);
      return;
    }
    paramView.postDelayed(paramRunnable, ValueAnimator.getFrameDelay() + paramLong);
  }
  
  public static void removeAccessibilityAction(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      removeActionWithId(paramInt, paramView);
      notifyViewAccessibilityStateChangedIfNeeded(paramView, 0);
    }
  }
  
  private static void removeActionWithId(int paramInt, View paramView)
  {
    paramView = getActionList(paramView);
    int i = 0;
    while (i < paramView.size())
    {
      if (((AccessibilityNodeInfoCompat.AccessibilityActionCompat)paramView.get(i)).getId() == paramInt)
      {
        paramView.remove(i);
        return;
      }
      i += 1;
    }
  }
  
  public static void removeOnUnhandledKeyEventListener(View paramView, OnUnhandledKeyEventListenerCompat paramOnUnhandledKeyEventListenerCompat)
  {
    Object localObject;
    if (Build.VERSION.SDK_INT >= 28)
    {
      localObject = (Map)paramView.getTag(R.id.tag_unhandled_key_listeners);
      if (localObject == null) {
        return;
      }
      paramOnUnhandledKeyEventListenerCompat = (View.OnUnhandledKeyEventListener)((Map)localObject).get(paramOnUnhandledKeyEventListenerCompat);
      if (paramOnUnhandledKeyEventListenerCompat != null) {
        paramView.removeOnUnhandledKeyEventListener(paramOnUnhandledKeyEventListenerCompat);
      }
    }
    else
    {
      localObject = (ArrayList)paramView.getTag(R.id.tag_unhandled_key_listeners);
      if (localObject != null)
      {
        ((ArrayList)localObject).remove(paramOnUnhandledKeyEventListenerCompat);
        if (((ArrayList)localObject).size() == 0) {
          UnhandledKeyEventManager.unregisterListeningView(paramView);
        }
      }
    }
  }
  
  public static void replaceAccessibilityAction(View paramView, AccessibilityNodeInfoCompat.AccessibilityActionCompat paramAccessibilityActionCompat, CharSequence paramCharSequence, AccessibilityViewCommand paramAccessibilityViewCommand)
  {
    addAccessibilityAction(paramView, paramAccessibilityActionCompat.createReplacementAction(paramCharSequence, paramAccessibilityViewCommand));
  }
  
  public static void requestApplyInsets(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 20)
    {
      paramView.requestApplyInsets();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16) {
      paramView.requestFitSystemWindows();
    }
  }
  
  public static View requireViewById(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramView.requireViewById(paramInt);
    }
    paramView = paramView.findViewById(paramInt);
    if (paramView != null) {
      return paramView;
    }
    throw new IllegalArgumentException("ID does not reference a View inside this View");
  }
  
  public static int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3)
  {
    return View.resolveSizeAndState(paramInt1, paramInt2, paramInt3);
  }
  
  public static boolean restoreDefaultFocus(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return paramView.restoreDefaultFocus();
    }
    return paramView.requestFocus();
  }
  
  private static AccessibilityViewProperty screenReaderFocusableProperty()
  {
    new AccessibilityViewProperty(R.id.tag_screen_reader_focusable, Boolean.class, 28)
    {
      Boolean frameworkGet(View paramAnonymousView)
      {
        return Boolean.valueOf(paramAnonymousView.isScreenReaderFocusable());
      }
      
      void frameworkSet(View paramAnonymousView, Boolean paramAnonymousBoolean)
      {
        paramAnonymousView.setScreenReaderFocusable(paramAnonymousBoolean.booleanValue());
      }
      
      boolean shouldUpdate(Boolean paramAnonymousBoolean1, Boolean paramAnonymousBoolean2)
      {
        return booleanNullToFalseEquals(paramAnonymousBoolean1, paramAnonymousBoolean2) ^ true;
      }
    };
  }
  
  public static void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
  {
    AccessibilityDelegateCompat localAccessibilityDelegateCompat = paramAccessibilityDelegateCompat;
    if (paramAccessibilityDelegateCompat == null)
    {
      localAccessibilityDelegateCompat = paramAccessibilityDelegateCompat;
      if ((getAccessibilityDelegateInternal(paramView) instanceof AccessibilityDelegateCompat.AccessibilityDelegateAdapter)) {
        localAccessibilityDelegateCompat = new AccessibilityDelegateCompat();
      }
    }
    if (localAccessibilityDelegateCompat == null) {
      paramAccessibilityDelegateCompat = null;
    } else {
      paramAccessibilityDelegateCompat = localAccessibilityDelegateCompat.getBridge();
    }
    paramView.setAccessibilityDelegate(paramAccessibilityDelegateCompat);
  }
  
  public static void setAccessibilityHeading(View paramView, boolean paramBoolean)
  {
    accessibilityHeadingProperty().setContent(paramView, Boolean.valueOf(paramBoolean));
  }
  
  public static void setAccessibilityLiveRegion(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      paramView.setAccessibilityLiveRegion(paramInt);
    }
  }
  
  public static void setAccessibilityPaneTitle(View paramView, CharSequence paramCharSequence)
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      paneTitleProperty().setContent(paramView, paramCharSequence);
      if (paramCharSequence != null)
      {
        sAccessibilityPaneVisibilityManager.addAccessibilityPane(paramView);
        return;
      }
      sAccessibilityPaneVisibilityManager.removeAccessibilityPane(paramView);
    }
  }
  
  public static void setActivated(View paramView, boolean paramBoolean)
  {
    paramView.setActivated(paramBoolean);
  }
  
  public static void setAlpha(View paramView, float paramFloat)
  {
    paramView.setAlpha(paramFloat);
  }
  
  public static void setAutofillHints(View paramView, String... paramVarArgs)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setAutofillHints(paramVarArgs);
    }
  }
  
  public static void setBackground(View paramView, Drawable paramDrawable)
  {
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramView.setBackground(paramDrawable);
      return;
    }
    paramView.setBackgroundDrawable(paramDrawable);
  }
  
  public static void setBackgroundTintList(View paramView, ColorStateList paramColorStateList)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramView.setBackgroundTintList(paramColorStateList);
      if (Build.VERSION.SDK_INT == 21)
      {
        paramColorStateList = paramView.getBackground();
        int i;
        if ((paramView.getBackgroundTintList() == null) && (paramView.getBackgroundTintMode() == null)) {
          i = 0;
        } else {
          i = 1;
        }
        if ((paramColorStateList != null) && (i != 0))
        {
          if (paramColorStateList.isStateful()) {
            paramColorStateList.setState(paramView.getDrawableState());
          }
          paramView.setBackground(paramColorStateList);
        }
      }
    }
    else if ((paramView instanceof TintableBackgroundView))
    {
      ((TintableBackgroundView)paramView).setSupportBackgroundTintList(paramColorStateList);
    }
  }
  
  public static void setBackgroundTintMode(View paramView, PorterDuff.Mode paramMode)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramView.setBackgroundTintMode(paramMode);
      if (Build.VERSION.SDK_INT == 21)
      {
        paramMode = paramView.getBackground();
        int i;
        if ((paramView.getBackgroundTintList() == null) && (paramView.getBackgroundTintMode() == null)) {
          i = 0;
        } else {
          i = 1;
        }
        if ((paramMode != null) && (i != 0))
        {
          if (paramMode.isStateful()) {
            paramMode.setState(paramView.getDrawableState());
          }
          paramView.setBackground(paramMode);
        }
      }
    }
    else if ((paramView instanceof TintableBackgroundView))
    {
      ((TintableBackgroundView)paramView).setSupportBackgroundTintMode(paramMode);
    }
  }
  
  public static void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    if (sChildrenDrawingOrderMethod == null)
    {
      Object localObject = Boolean.TYPE;
      try
      {
        localObject = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", new Class[] { localObject });
        sChildrenDrawingOrderMethod = (Method)localObject;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        Log.e("ViewCompat", "Unable to find childrenDrawingOrderEnabled", localNoSuchMethodException);
      }
      sChildrenDrawingOrderMethod.setAccessible(true);
    }
    Method localMethod = sChildrenDrawingOrderMethod;
    try
    {
      localMethod.invoke(paramViewGroup, new Object[] { Boolean.valueOf(paramBoolean) });
      return;
    }
    catch (InvocationTargetException paramViewGroup)
    {
      Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", paramViewGroup);
      return;
    }
    catch (IllegalArgumentException paramViewGroup)
    {
      Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", paramViewGroup);
      return;
    }
    catch (IllegalAccessException paramViewGroup)
    {
      Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", paramViewGroup);
    }
  }
  
  public static void setClipBounds(View paramView, Rect paramRect)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      paramView.setClipBounds(paramRect);
    }
  }
  
  public static void setElevation(View paramView, float paramFloat)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      paramView.setElevation(paramFloat);
    }
  }
  
  public static void setFitsSystemWindows(View paramView, boolean paramBoolean)
  {
    paramView.setFitsSystemWindows(paramBoolean);
  }
  
  public static void setFocusedByDefault(View paramView, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setFocusedByDefault(paramBoolean);
    }
  }
  
  public static void setHasTransientState(View paramView, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      paramView.setHasTransientState(paramBoolean);
    }
  }
  
  public static void setImportantForAccessibility(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 19)
    {
      paramView.setImportantForAccessibility(paramInt);
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      int i = paramInt;
      if (paramInt == 4) {
        i = 2;
      }
      paramView.setImportantForAccessibility(i);
    }
  }
  
  public static void setImportantForAutofill(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setImportantForAutofill(paramInt);
    }
  }
  
  public static void setKeyboardNavigationCluster(View paramView, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setKeyboardNavigationCluster(paramBoolean);
    }
  }
  
  public static void setLabelFor(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      paramView.setLabelFor(paramInt);
    }
  }
  
  public static void setLayerPaint(View paramView, Paint paramPaint)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      paramView.setLayerPaint(paramPaint);
      return;
    }
    paramView.setLayerType(paramView.getLayerType(), paramPaint);
    paramView.invalidate();
  }
  
  public static void setLayerType(View paramView, int paramInt, Paint paramPaint)
  {
    paramView.setLayerType(paramInt, paramPaint);
  }
  
  public static void setLayoutDirection(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      paramView.setLayoutDirection(paramInt);
    }
  }
  
  public static void setNestedScrollingEnabled(View paramView, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramView.setNestedScrollingEnabled(paramBoolean);
      return;
    }
    if ((paramView instanceof NestedScrollingChild)) {
      ((NestedScrollingChild)paramView).setNestedScrollingEnabled(paramBoolean);
    }
  }
  
  public static void setNextClusterForwardId(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setNextClusterForwardId(paramInt);
    }
  }
  
  public static void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      if (paramOnApplyWindowInsetsListener == null)
      {
        paramView.setOnApplyWindowInsetsListener(null);
        return;
      }
      paramView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
        {
          paramAnonymousWindowInsets = WindowInsetsCompat.wrap(paramAnonymousWindowInsets);
          return (WindowInsets)WindowInsetsCompat.unwrap(val$listener.onApplyWindowInsets(paramAnonymousView, paramAnonymousWindowInsets));
        }
      });
    }
  }
  
  public static void setOverScrollMode(View paramView, int paramInt)
  {
    paramView.setOverScrollMode(paramInt);
  }
  
  public static void setPaddingRelative(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      paramView.setPaddingRelative(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    paramView.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void setPivotX(View paramView, float paramFloat)
  {
    paramView.setPivotX(paramFloat);
  }
  
  public static void setPivotY(View paramView, float paramFloat)
  {
    paramView.setPivotY(paramFloat);
  }
  
  public static void setPointerIcon(View paramView, PointerIconCompat paramPointerIconCompat)
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      if (paramPointerIconCompat != null) {
        paramPointerIconCompat = paramPointerIconCompat.getPointerIcon();
      } else {
        paramPointerIconCompat = null;
      }
      paramView.setPointerIcon((PointerIcon)paramPointerIconCompat);
    }
  }
  
  public static void setRotation(View paramView, float paramFloat)
  {
    paramView.setRotation(paramFloat);
  }
  
  public static void setRotationX(View paramView, float paramFloat)
  {
    paramView.setRotationX(paramFloat);
  }
  
  public static void setRotationY(View paramView, float paramFloat)
  {
    paramView.setRotationY(paramFloat);
  }
  
  public static void setSaveFromParentEnabled(View paramView, boolean paramBoolean)
  {
    paramView.setSaveFromParentEnabled(paramBoolean);
  }
  
  public static void setScaleX(View paramView, float paramFloat)
  {
    paramView.setScaleX(paramFloat);
  }
  
  public static void setScaleY(View paramView, float paramFloat)
  {
    paramView.setScaleY(paramFloat);
  }
  
  public static void setScreenReaderFocusable(View paramView, boolean paramBoolean)
  {
    screenReaderFocusableProperty().setContent(paramView, Boolean.valueOf(paramBoolean));
  }
  
  public static void setScrollIndicators(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      paramView.setScrollIndicators(paramInt);
    }
  }
  
  public static void setScrollIndicators(View paramView, int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      paramView.setScrollIndicators(paramInt1, paramInt2);
    }
  }
  
  public static void setTooltipText(View paramView, CharSequence paramCharSequence)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setTooltipText(paramCharSequence);
    }
  }
  
  public static void setTransitionName(View paramView, String paramString)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramView.setTransitionName(paramString);
      return;
    }
    if (sTransitionNameMap == null) {
      sTransitionNameMap = new WeakHashMap();
    }
    sTransitionNameMap.put(paramView, paramString);
  }
  
  public static void setTranslationX(View paramView, float paramFloat)
  {
    paramView.setTranslationX(paramFloat);
  }
  
  public static void setTranslationY(View paramView, float paramFloat)
  {
    paramView.setTranslationY(paramFloat);
  }
  
  public static void setTranslationZ(View paramView, float paramFloat)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      paramView.setTranslationZ(paramFloat);
    }
  }
  
  public static void setX(View paramView, float paramFloat)
  {
    paramView.setX(paramFloat);
  }
  
  public static void setY(View paramView, float paramFloat)
  {
    paramView.setY(paramFloat);
  }
  
  public static void setZ(View paramView, float paramFloat)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      paramView.setZ(paramFloat);
    }
  }
  
  public static boolean startDragAndDrop(View paramView, ClipData paramClipData, View.DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 24) {
      return paramView.startDragAndDrop(paramClipData, paramDragShadowBuilder, paramObject, paramInt);
    }
    return paramView.startDrag(paramClipData, paramDragShadowBuilder, paramObject, paramInt);
  }
  
  public static boolean startNestedScroll(View paramView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramView.startNestedScroll(paramInt);
    }
    if ((paramView instanceof NestedScrollingChild)) {
      return ((NestedScrollingChild)paramView).startNestedScroll(paramInt);
    }
    return false;
  }
  
  public static boolean startNestedScroll(View paramView, int paramInt1, int paramInt2)
  {
    if ((paramView instanceof NestedScrollingChild2)) {
      return ((NestedScrollingChild2)paramView).startNestedScroll(paramInt1, paramInt2);
    }
    if (paramInt2 == 0) {
      return startNestedScroll(paramView, paramInt1);
    }
    return false;
  }
  
  public static void stopNestedScroll(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramView.stopNestedScroll();
      return;
    }
    if ((paramView instanceof NestedScrollingChild)) {
      ((NestedScrollingChild)paramView).stopNestedScroll();
    }
  }
  
  public static void stopNestedScroll(View paramView, int paramInt)
  {
    if ((paramView instanceof NestedScrollingChild2))
    {
      ((NestedScrollingChild2)paramView).stopNestedScroll(paramInt);
      return;
    }
    if (paramInt == 0) {
      stopNestedScroll(paramView);
    }
  }
  
  private static void tickleInvalidationFlag(View paramView)
  {
    float f = paramView.getTranslationY();
    paramView.setTranslationY(1.0F + f);
    paramView.setTranslationY(f);
  }
  
  public static void updateDragShadow(View paramView, View.DragShadowBuilder paramDragShadowBuilder)
  {
    if (Build.VERSION.SDK_INT >= 24) {
      paramView.updateDragShadow(paramDragShadowBuilder);
    }
  }
  
  static class AccessibilityPaneVisibilityManager
    implements ViewTreeObserver.OnGlobalLayoutListener, View.OnAttachStateChangeListener
  {
    private WeakHashMap<View, Boolean> mPanesToVisible = new WeakHashMap();
    
    AccessibilityPaneVisibilityManager() {}
    
    private void checkPaneVisibility(View paramView, boolean paramBoolean)
    {
      boolean bool;
      if (paramView.getVisibility() == 0) {
        bool = true;
      } else {
        bool = false;
      }
      if (paramBoolean != bool)
      {
        if (bool) {
          ViewCompat.notifyViewAccessibilityStateChangedIfNeeded(paramView, 16);
        }
        mPanesToVisible.put(paramView, Boolean.valueOf(bool));
      }
    }
    
    private void registerForLayoutCallback(View paramView)
    {
      paramView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
    
    private void unregisterForLayoutCallback(View paramView)
    {
      paramView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
    
    void addAccessibilityPane(View paramView)
    {
      WeakHashMap localWeakHashMap = mPanesToVisible;
      boolean bool;
      if (paramView.getVisibility() == 0) {
        bool = true;
      } else {
        bool = false;
      }
      localWeakHashMap.put(paramView, Boolean.valueOf(bool));
      paramView.addOnAttachStateChangeListener(this);
      if (paramView.isAttachedToWindow()) {
        registerForLayoutCallback(paramView);
      }
    }
    
    public void onGlobalLayout()
    {
      Iterator localIterator = mPanesToVisible.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        checkPaneVisibility((View)localEntry.getKey(), ((Boolean)localEntry.getValue()).booleanValue());
      }
    }
    
    public void onViewAttachedToWindow(View paramView)
    {
      registerForLayoutCallback(paramView);
    }
    
    public void onViewDetachedFromWindow(View paramView) {}
    
    void removeAccessibilityPane(View paramView)
    {
      mPanesToVisible.remove(paramView);
      paramView.removeOnAttachStateChangeListener(this);
      unregisterForLayoutCallback(paramView);
    }
  }
  
  static abstract class AccessibilityViewProperty<T>
  {
    private final int mContentChangeType;
    private final int mFrameworkMinimumSdk;
    private final int mTagKey;
    private final Class<T> mType;
    
    AccessibilityViewProperty(int paramInt1, Class paramClass, int paramInt2)
    {
      this(paramInt1, paramClass, 0, paramInt2);
    }
    
    AccessibilityViewProperty(int paramInt1, Class paramClass, int paramInt2, int paramInt3)
    {
      mTagKey = paramInt1;
      mType = paramClass;
      mContentChangeType = paramInt2;
      mFrameworkMinimumSdk = paramInt3;
    }
    
    private boolean extrasAvailable()
    {
      return Build.VERSION.SDK_INT >= 19;
    }
    
    private boolean frameworkAvailable()
    {
      return Build.VERSION.SDK_INT >= mFrameworkMinimumSdk;
    }
    
    boolean booleanNullToFalseEquals(Boolean paramBoolean1, Boolean paramBoolean2)
    {
      boolean bool1;
      if (paramBoolean1 == null) {
        bool1 = false;
      } else {
        bool1 = paramBoolean1.booleanValue();
      }
      boolean bool2;
      if (paramBoolean2 == null) {
        bool2 = false;
      } else {
        bool2 = paramBoolean2.booleanValue();
      }
      return bool1 == bool2;
    }
    
    abstract Object frameworkGet(View paramView);
    
    abstract void frameworkSet(View paramView, Object paramObject);
    
    Object getFragment(View paramView)
    {
      if (frameworkAvailable()) {
        return frameworkGet(paramView);
      }
      if (extrasAvailable())
      {
        paramView = paramView.getTag(mTagKey);
        if (mType.isInstance(paramView)) {
          return paramView;
        }
      }
      return null;
    }
    
    void setContent(View paramView, Object paramObject)
    {
      if (frameworkAvailable())
      {
        frameworkSet(paramView, paramObject);
        return;
      }
      if ((extrasAvailable()) && (shouldUpdate(getFragment(paramView), paramObject)))
      {
        ViewCompat.getOrCreateAccessibilityDelegateCompat(paramView);
        paramView.setTag(mTagKey, paramObject);
        ViewCompat.notifyViewAccessibilityStateChangedIfNeeded(paramView, 0);
      }
    }
    
    boolean shouldUpdate(Object paramObject1, Object paramObject2)
    {
      return paramObject2.equals(paramObject1) ^ true;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface FocusDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface FocusRealDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface FocusRelativeDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface NestedScrollType {}
  
  public static abstract interface OnUnhandledKeyEventListenerCompat
  {
    public abstract boolean onUnhandledKeyEvent(View paramView, KeyEvent paramKeyEvent);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface ScrollAxis {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface ScrollIndicators {}
  
  static class UnhandledKeyEventManager
  {
    private static final ArrayList<WeakReference<View>> sViewsWithListeners = new ArrayList();
    private SparseArray<WeakReference<View>> mCapturedKeys = null;
    private WeakReference<KeyEvent> mLastDispatchedPreViewKeyEvent = null;
    @Nullable
    private WeakHashMap<View, Boolean> mViewsContainingListeners = null;
    
    UnhandledKeyEventManager() {}
    
    private View dispatchInOrder(View paramView, KeyEvent paramKeyEvent)
    {
      if (mViewsContainingListeners != null)
      {
        if (!mViewsContainingListeners.containsKey(paramView)) {
          return null;
        }
        if ((paramView instanceof ViewGroup))
        {
          ViewGroup localViewGroup = (ViewGroup)paramView;
          int i = localViewGroup.getChildCount() - 1;
          while (i >= 0)
          {
            View localView = dispatchInOrder(localViewGroup.getChildAt(i), paramKeyEvent);
            if (localView != null) {
              return localView;
            }
            i -= 1;
          }
        }
        if (onUnhandledKeyEvent(paramView, paramKeyEvent)) {
          return paramView;
        }
      }
      return null;
    }
    
    private SparseArray getCapturedKeys()
    {
      if (mCapturedKeys == null) {
        mCapturedKeys = new SparseArray();
      }
      return mCapturedKeys;
    }
    
    static UnhandledKeyEventManager getSectionView(View paramView)
    {
      UnhandledKeyEventManager localUnhandledKeyEventManager2 = (UnhandledKeyEventManager)paramView.getTag(R.id.tag_unhandled_key_event_manager);
      UnhandledKeyEventManager localUnhandledKeyEventManager1 = localUnhandledKeyEventManager2;
      if (localUnhandledKeyEventManager2 == null)
      {
        localUnhandledKeyEventManager1 = new UnhandledKeyEventManager();
        paramView.setTag(R.id.tag_unhandled_key_event_manager, localUnhandledKeyEventManager1);
      }
      return localUnhandledKeyEventManager1;
    }
    
    private boolean onUnhandledKeyEvent(View paramView, KeyEvent paramKeyEvent)
    {
      ArrayList localArrayList = (ArrayList)paramView.getTag(R.id.tag_unhandled_key_listeners);
      if (localArrayList != null)
      {
        int i = localArrayList.size() - 1;
        while (i >= 0)
        {
          if (((ViewCompat.OnUnhandledKeyEventListenerCompat)localArrayList.get(i)).onUnhandledKeyEvent(paramView, paramKeyEvent)) {
            return true;
          }
          i -= 1;
        }
      }
      return false;
    }
    
    private void recalcViewsWithUnhandled()
    {
      if (mViewsContainingListeners != null) {
        mViewsContainingListeners.clear();
      }
      if (sViewsWithListeners.isEmpty()) {
        return;
      }
      ArrayList localArrayList = sViewsWithListeners;
      for (;;)
      {
        int i;
        try
        {
          if (mViewsContainingListeners == null) {
            mViewsContainingListeners = new WeakHashMap();
          }
          i = sViewsWithListeners.size() - 1;
          if (i >= 0)
          {
            Object localObject = (View)((WeakReference)sViewsWithListeners.get(i)).get();
            if (localObject == null)
            {
              sViewsWithListeners.remove(i);
            }
            else
            {
              mViewsContainingListeners.put(localObject, Boolean.TRUE);
              localObject = ((View)localObject).getParent();
              if ((localObject instanceof View))
              {
                mViewsContainingListeners.put((View)localObject, Boolean.TRUE);
                localObject = ((ViewParent)localObject).getParent();
                continue;
              }
            }
          }
          else
          {
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
        i -= 1;
      }
    }
    
    static void registerListeningView(View paramView)
    {
      ArrayList localArrayList = sViewsWithListeners;
      try
      {
        Iterator localIterator = sViewsWithListeners.iterator();
        while (localIterator.hasNext()) {
          if (((WeakReference)localIterator.next()).get() == paramView) {
            return;
          }
        }
        sViewsWithListeners.add(new WeakReference(paramView));
        return;
      }
      catch (Throwable paramView)
      {
        throw paramView;
      }
    }
    
    static void unregisterListeningView(View paramView)
    {
      ArrayList localArrayList = sViewsWithListeners;
      int i = 0;
      try
      {
        while (i < sViewsWithListeners.size())
        {
          if (((WeakReference)sViewsWithListeners.get(i)).get() == paramView)
          {
            sViewsWithListeners.remove(i);
            return;
          }
          i += 1;
        }
        return;
      }
      catch (Throwable paramView)
      {
        throw paramView;
      }
    }
    
    boolean dispatch(View paramView, KeyEvent paramKeyEvent)
    {
      if (paramKeyEvent.getAction() == 0) {
        recalcViewsWithUnhandled();
      }
      paramView = dispatchInOrder(paramView, paramKeyEvent);
      if (paramKeyEvent.getAction() == 0)
      {
        int i = paramKeyEvent.getKeyCode();
        if ((paramView != null) && (!KeyEvent.isModifierKey(i))) {
          getCapturedKeys().put(i, new WeakReference(paramView));
        }
      }
      return paramView != null;
    }
    
    boolean preDispatch(KeyEvent paramKeyEvent)
    {
      if ((mLastDispatchedPreViewKeyEvent != null) && (mLastDispatchedPreViewKeyEvent.get() == paramKeyEvent)) {
        return false;
      }
      mLastDispatchedPreViewKeyEvent = new WeakReference(paramKeyEvent);
      Object localObject2 = null;
      SparseArray localSparseArray = getCapturedKeys();
      Object localObject1 = localObject2;
      if (paramKeyEvent.getAction() == 1)
      {
        int i = localSparseArray.indexOfKey(paramKeyEvent.getKeyCode());
        localObject1 = localObject2;
        if (i >= 0)
        {
          localObject1 = (WeakReference)localSparseArray.valueAt(i);
          localSparseArray.removeAt(i);
        }
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = (WeakReference)localSparseArray.get(paramKeyEvent.getKeyCode());
      }
      if (localObject2 != null)
      {
        localObject1 = (View)((WeakReference)localObject2).get();
        if ((localObject1 != null) && (ViewCompat.isAttachedToWindow((View)localObject1)))
        {
          onUnhandledKeyEvent((View)localObject1, paramKeyEvent);
          return true;
        }
      }
      else
      {
        return false;
      }
      return true;
    }
  }
}
