package com.facebook.react.uimanager;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import com.facebook.common.logging.FLog;
import com.facebook.debug.holder.Printer;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.OnBatchCompleteListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ClearableSynchronizedPool;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.common.ViewUtil;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@ReactModule(name="UIManager")
public class UIManagerModule
  extends ReactContextBaseJavaModule
  implements OnBatchCompleteListener, LifecycleEventListener, UIManager
{
  private static final boolean DEBUG = PrinterHolder.getPrinter().shouldDisplayLogMessage(ReactDebugOverlayTags.UI_MANAGER);
  public static final String NAME = "UIManager";
  private int mBatchId = 0;
  private final Map<String, Object> mCustomDirectEvents;
  private final EventDispatcher mEventDispatcher;
  private final List<UIManagerModuleListener> mListeners = new ArrayList();
  private final MemoryTrimCallback mMemoryTrimCallback = new MemoryTrimCallback(null);
  private final Map<String, Object> mModuleConstants;
  private final UIImplementation mUIImplementation;
  @Nullable
  private Map<String, WritableMap> mViewManagerConstantsCache;
  private volatile int mViewManagerConstantsCacheSize;
  private final ViewManagerRegistry mViewManagerRegistry;
  
  public UIManagerModule(ReactApplicationContext paramReactApplicationContext, ViewManagerResolver paramViewManagerResolver, int paramInt)
  {
    this(paramReactApplicationContext, paramViewManagerResolver, new UIImplementationProvider(), paramInt);
  }
  
  public UIManagerModule(ReactApplicationContext paramReactApplicationContext, ViewManagerResolver paramViewManagerResolver, UIImplementationProvider paramUIImplementationProvider, int paramInt)
  {
    super(paramReactApplicationContext);
    DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(paramReactApplicationContext);
    mEventDispatcher = new EventDispatcher(paramReactApplicationContext);
    mModuleConstants = createConstants(paramViewManagerResolver);
    mCustomDirectEvents = UIManagerModuleConstants.getDirectEventTypeConstants();
    mViewManagerRegistry = new ViewManagerRegistry(paramViewManagerResolver);
    mUIImplementation = paramUIImplementationProvider.createUIImplementation(paramReactApplicationContext, mViewManagerRegistry, mEventDispatcher, paramInt);
    paramReactApplicationContext.addLifecycleEventListener(this);
  }
  
  public UIManagerModule(ReactApplicationContext paramReactApplicationContext, List paramList, int paramInt)
  {
    this(paramReactApplicationContext, paramList, new UIImplementationProvider(), paramInt);
  }
  
  public UIManagerModule(ReactApplicationContext paramReactApplicationContext, List paramList, UIImplementationProvider paramUIImplementationProvider, int paramInt)
  {
    super(paramReactApplicationContext);
    DisplayMetricsHolder.initDisplayMetricsIfNotInitialized(paramReactApplicationContext);
    mEventDispatcher = new EventDispatcher(paramReactApplicationContext);
    mCustomDirectEvents = MapBuilder.newHashMap();
    mModuleConstants = createConstants(paramList, null, mCustomDirectEvents);
    mViewManagerRegistry = new ViewManagerRegistry(paramList);
    mUIImplementation = paramUIImplementationProvider.createUIImplementation(paramReactApplicationContext, mViewManagerRegistry, mEventDispatcher, paramInt);
    paramReactApplicationContext.addLifecycleEventListener(this);
  }
  
  private WritableMap computeConstantsForViewManager(String paramString)
  {
    if (paramString != null) {
      paramString = mUIImplementation.resolveViewManager(paramString);
    } else {
      paramString = null;
    }
    if (paramString == null) {
      return null;
    }
    SystraceMessage.beginSection(0L, "UIManagerModule.getConstantsForViewManager").put("ViewManager", paramString.getName()).put("Lazy", Boolean.valueOf(true)).flush();
    try
    {
      paramString = UIManagerModuleConstantsHelper.createConstantsForViewManager(paramString, null, null, null, mCustomDirectEvents);
      if (paramString != null)
      {
        paramString = Arguments.makeNativeMap(paramString);
        SystraceMessage.endSection(0L).flush();
        return paramString;
      }
      SystraceMessage.endSection(0L).flush();
      return null;
    }
    catch (Throwable paramString)
    {
      SystraceMessage.endSection(0L).flush();
      throw paramString;
    }
  }
  
  private static Map createConstants(ViewManagerResolver paramViewManagerResolver)
  {
    ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_START);
    SystraceMessage.beginSection(0L, "CreateUIManagerConstants").put("Lazy", Boolean.valueOf(true)).flush();
    try
    {
      paramViewManagerResolver = UIManagerModuleConstantsHelper.createConstants(paramViewManagerResolver);
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_END);
      return paramViewManagerResolver;
    }
    catch (Throwable paramViewManagerResolver)
    {
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_END);
      throw paramViewManagerResolver;
    }
  }
  
  private static Map createConstants(List paramList, Map paramMap1, Map paramMap2)
  {
    ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_START);
    SystraceMessage.beginSection(0L, "CreateUIManagerConstants").put("Lazy", Boolean.valueOf(false)).flush();
    try
    {
      paramList = UIManagerModuleConstantsHelper.createConstants(paramList, paramMap1, paramMap2);
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_END);
      return paramList;
    }
    catch (Throwable paramList)
    {
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_CONSTANTS_END);
      throw paramList;
    }
  }
  
  public int addRootView(View paramView)
  {
    return addRootView(paramView, null, null);
  }
  
  public int addRootView(View paramView, WritableMap paramWritableMap, String paramString)
  {
    Systrace.beginSection(0L, "UIManagerModule.addRootView");
    int i = ReactRootViewTagGenerator.getNextRootViewTag();
    paramWritableMap = new ThemedReactContext(getReactApplicationContext(), paramView.getContext());
    mUIImplementation.registerRootView(paramView, i, paramWritableMap);
    Systrace.endSection(0L);
    return i;
  }
  
  public void addUIBlock(UIBlock paramUIBlock)
  {
    mUIImplementation.addUIBlock(paramUIBlock);
  }
  
  public void addUIManagerListener(UIManagerModuleListener paramUIManagerModuleListener)
  {
    mListeners.add(paramUIManagerModuleListener);
  }
  
  public void clearJSResponder()
  {
    mUIImplementation.clearJSResponder();
  }
  
  public void configureNextLayoutAnimation(ReadableMap paramReadableMap, Callback paramCallback1, Callback paramCallback2)
  {
    mUIImplementation.configureNextLayoutAnimation(paramReadableMap, paramCallback1);
  }
  
  public void createView(int paramInt1, String paramString, int paramInt2, ReadableMap paramReadableMap)
  {
    if (DEBUG)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("(UIManager.createView) tag: ");
      ((StringBuilder)localObject).append(paramInt1);
      ((StringBuilder)localObject).append(", class: ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(", props: ");
      ((StringBuilder)localObject).append(paramReadableMap);
      localObject = ((StringBuilder)localObject).toString();
      FLog.d("ReactNative", (String)localObject);
      PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, (String)localObject);
    }
    mUIImplementation.createView(paramInt1, paramString, paramInt2, paramReadableMap);
  }
  
  public void dismissPopupMenu()
  {
    mUIImplementation.dismissPopupMenu();
  }
  
  public void dispatchCommand(int paramInt1, int paramInt2, ReadableArray paramReadableArray)
  {
    mUIImplementation.dispatchViewManagerCommand(paramInt1, paramInt2, paramReadableArray);
  }
  
  public void dispatchCommand(int paramInt, String paramString, ReadableArray paramReadableArray)
  {
    mUIImplementation.dispatchViewManagerCommand(paramInt, paramString, paramReadableArray);
  }
  
  public void dispatchViewManagerCommand(int paramInt, Dynamic paramDynamic, ReadableArray paramReadableArray)
  {
    if (paramDynamic.getType() == ReadableType.Number)
    {
      int i = paramDynamic.asInt();
      UIManagerHelper.getUIManager(getReactApplicationContext(), ViewUtil.getUIManagerType(paramInt)).dispatchCommand(paramInt, i, paramReadableArray);
      return;
    }
    if (paramDynamic.getType() == ReadableType.String)
    {
      paramDynamic = paramDynamic.asString();
      UIManagerHelper.getUIManager(getReactApplicationContext(), ViewUtil.getUIManagerType(paramInt)).dispatchCommand(paramInt, paramDynamic, paramReadableArray);
    }
  }
  
  public void findSubviewIn(int paramInt, ReadableArray paramReadableArray, Callback paramCallback)
  {
    mUIImplementation.findSubviewIn(paramInt, Math.round(PixelUtil.toPixelFromDIP(paramReadableArray.getDouble(0))), Math.round(PixelUtil.toPixelFromDIP(paramReadableArray.getDouble(1))), paramCallback);
  }
  
  public Map getConstants()
  {
    return mModuleConstants;
  }
  
  public WritableMap getConstantsForViewManager(String paramString)
  {
    if ((mViewManagerConstantsCache != null) && (mViewManagerConstantsCache.containsKey(paramString)))
    {
      WritableMap localWritableMap = (WritableMap)mViewManagerConstantsCache.get(paramString);
      int i = mViewManagerConstantsCacheSize - 1;
      mViewManagerConstantsCacheSize = i;
      paramString = localWritableMap;
      if (i <= 0)
      {
        mViewManagerConstantsCache = null;
        return localWritableMap;
      }
    }
    else
    {
      paramString = computeConstantsForViewManager(paramString);
    }
    return paramString;
  }
  
  public WritableMap getDefaultEventTypes()
  {
    return Arguments.makeNativeMap(UIManagerModuleConstantsHelper.getDefaultExportableEventTypes());
  }
  
  public CustomEventNamesResolver getDirectEventNamesResolver()
  {
    new CustomEventNamesResolver()
    {
      public String resolveCustomEventName(String paramAnonymousString)
      {
        Map localMap = (Map)mCustomDirectEvents.get(paramAnonymousString);
        if (localMap != null) {
          paramAnonymousString = (String)localMap.get("registrationName");
        }
        return paramAnonymousString;
      }
    };
  }
  
  public EventDispatcher getEventDispatcher()
  {
    return mEventDispatcher;
  }
  
  public String getName()
  {
    return "UIManager";
  }
  
  public Map getPerformanceCounters()
  {
    return mUIImplementation.getProfiledBatchPerfCounters();
  }
  
  public UIImplementation getUIImplementation()
  {
    return mUIImplementation;
  }
  
  public ViewManagerRegistry getViewManagerRegistry_DO_NOT_USE()
  {
    return mViewManagerRegistry;
  }
  
  public void initialize()
  {
    getReactApplicationContext().registerComponentCallbacks(mMemoryTrimCallback);
    mEventDispatcher.registerEventEmitter(1, (RCTEventEmitter)getReactApplicationContext().getJSModule(RCTEventEmitter.class));
  }
  
  public void invalidateNodeLayout(int paramInt)
  {
    Object localObject = mUIImplementation.resolveShadowNode(paramInt);
    if (localObject == null)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Warning : attempted to dirty a non-existent react shadow node. reactTag=");
      ((StringBuilder)localObject).append(paramInt);
      FLog.warn("ReactNative", ((StringBuilder)localObject).toString());
      return;
    }
    ((ReactShadowNode)localObject).dirty();
    mUIImplementation.dispatchViewUpdates(-1);
  }
  
  public void manageChildren(int paramInt, ReadableArray paramReadableArray1, ReadableArray paramReadableArray2, ReadableArray paramReadableArray3, ReadableArray paramReadableArray4, ReadableArray paramReadableArray5)
  {
    if (DEBUG)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("(UIManager.manageChildren) tag: ");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(", moveFrom: ");
      ((StringBuilder)localObject).append(paramReadableArray1);
      ((StringBuilder)localObject).append(", moveTo: ");
      ((StringBuilder)localObject).append(paramReadableArray2);
      ((StringBuilder)localObject).append(", addTags: ");
      ((StringBuilder)localObject).append(paramReadableArray3);
      ((StringBuilder)localObject).append(", atIndices: ");
      ((StringBuilder)localObject).append(paramReadableArray4);
      ((StringBuilder)localObject).append(", removeFrom: ");
      ((StringBuilder)localObject).append(paramReadableArray5);
      localObject = ((StringBuilder)localObject).toString();
      FLog.d("ReactNative", (String)localObject);
      PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, (String)localObject);
    }
    mUIImplementation.manageChildren(paramInt, paramReadableArray1, paramReadableArray2, paramReadableArray3, paramReadableArray4, paramReadableArray5);
  }
  
  public void measure(int paramInt, Callback paramCallback)
  {
    mUIImplementation.measure(paramInt, paramCallback);
  }
  
  public void measureInWindow(int paramInt, Callback paramCallback)
  {
    mUIImplementation.measureInWindow(paramInt, paramCallback);
  }
  
  public void measureLayout(int paramInt1, int paramInt2, Callback paramCallback1, Callback paramCallback2)
  {
    mUIImplementation.measureLayout(paramInt1, paramInt2, paramCallback1, paramCallback2);
  }
  
  public void measureLayoutRelativeToParent(int paramInt, Callback paramCallback1, Callback paramCallback2)
  {
    mUIImplementation.measureLayoutRelativeToParent(paramInt, paramCallback1, paramCallback2);
  }
  
  public void onBatchComplete()
  {
    int i = mBatchId;
    mBatchId += 1;
    SystraceMessage.beginSection(0L, "onBatchCompleteUI").getStream("BatchId", i).flush();
    Iterator localIterator = mListeners.iterator();
    while (localIterator.hasNext()) {
      ((UIManagerModuleListener)localIterator.next()).willDispatchViewUpdates(this);
    }
    try
    {
      mUIImplementation.dispatchViewUpdates(i);
      Systrace.endSection(0L);
      return;
    }
    catch (Throwable localThrowable)
    {
      Systrace.endSection(0L);
      throw localThrowable;
    }
  }
  
  public void onCatalystInstanceDestroy()
  {
    super.onCatalystInstanceDestroy();
    mEventDispatcher.onCatalystInstanceDestroyed();
    getReactApplicationContext().unregisterComponentCallbacks(mMemoryTrimCallback);
    YogaNodePool.getInstance().clear();
    ViewManagerPropertyUpdater.clear();
  }
  
  public void onHostDestroy()
  {
    mUIImplementation.onHostDestroy();
  }
  
  public void onHostPause()
  {
    mUIImplementation.onHostPause();
  }
  
  public void onHostResume()
  {
    mUIImplementation.onHostResume();
  }
  
  public void playTouchSound()
  {
    AudioManager localAudioManager = (AudioManager)getReactApplicationContext().getSystemService("audio");
    if (localAudioManager != null) {
      localAudioManager.playSoundEffect(0);
    }
  }
  
  public void preComputeConstantsForViewManager(List paramList)
  {
    ArrayMap localArrayMap = new ArrayMap();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      WritableMap localWritableMap = computeConstantsForViewManager(str);
      if (localWritableMap != null) {
        localArrayMap.put(str, localWritableMap);
      }
    }
    mViewManagerConstantsCacheSize = paramList.size();
    mViewManagerConstantsCache = Collections.unmodifiableMap(localArrayMap);
  }
  
  public void prependUIBlock(UIBlock paramUIBlock)
  {
    mUIImplementation.prependUIBlock(paramUIBlock);
  }
  
  public void profileNextBatch()
  {
    mUIImplementation.profileNextBatch();
  }
  
  public void removeRootView(int paramInt)
  {
    mUIImplementation.removeRootView(paramInt);
  }
  
  public void removeSubviewsFromContainerWithID(int paramInt)
  {
    mUIImplementation.removeSubviewsFromContainerWithID(paramInt);
  }
  
  public void removeUIManagerListener(UIManagerModuleListener paramUIManagerModuleListener)
  {
    mListeners.remove(paramUIManagerModuleListener);
  }
  
  public void replaceExistingNonRootView(int paramInt1, int paramInt2)
  {
    mUIImplementation.replaceExistingNonRootView(paramInt1, paramInt2);
  }
  
  public int resolveRootTagFromReactTag(int paramInt)
  {
    if (ViewUtil.isRootTag(paramInt)) {
      return paramInt;
    }
    return mUIImplementation.resolveRootTagFromReactTag(paramInt);
  }
  
  public View resolveView(int paramInt)
  {
    UiThreadUtil.assertOnUiThread();
    return mUIImplementation.getUIViewOperationQueue().getNativeViewHierarchyManager().resolveView(paramInt);
  }
  
  public void sendAccessibilityEvent(int paramInt1, int paramInt2)
  {
    mUIImplementation.sendAccessibilityEvent(paramInt1, paramInt2);
  }
  
  public void setChildren(int paramInt, ReadableArray paramReadableArray)
  {
    if (DEBUG)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("(UIManager.setChildren) tag: ");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(", children: ");
      ((StringBuilder)localObject).append(paramReadableArray);
      localObject = ((StringBuilder)localObject).toString();
      FLog.d("ReactNative", (String)localObject);
      PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, (String)localObject);
    }
    mUIImplementation.setChildren(paramInt, paramReadableArray);
  }
  
  public void setJSResponder(int paramInt, boolean paramBoolean)
  {
    mUIImplementation.setJSResponder(paramInt, paramBoolean);
  }
  
  public void setLayoutAnimationEnabledExperimental(boolean paramBoolean)
  {
    mUIImplementation.setLayoutAnimationEnabledExperimental(paramBoolean);
  }
  
  public void setViewHierarchyUpdateDebugListener(NotThreadSafeViewHierarchyUpdateDebugListener paramNotThreadSafeViewHierarchyUpdateDebugListener)
  {
    mUIImplementation.setViewHierarchyUpdateDebugListener(paramNotThreadSafeViewHierarchyUpdateDebugListener);
  }
  
  public void setViewLocalData(final int paramInt, final Object paramObject)
  {
    ReactApplicationContext localReactApplicationContext = getReactApplicationContext();
    localReactApplicationContext.assertOnUiQueueThread();
    localReactApplicationContext.runOnNativeModulesQueueThread(new GuardedRunnable(localReactApplicationContext)
    {
      public void runGuarded()
      {
        mUIImplementation.setViewLocalData(paramInt, paramObject);
      }
    });
  }
  
  public void showPopupMenu(int paramInt, ReadableArray paramReadableArray, Callback paramCallback1, Callback paramCallback2)
  {
    mUIImplementation.showPopupMenu(paramInt, paramReadableArray, paramCallback1, paramCallback2);
  }
  
  public void synchronouslyUpdateViewOnUIThread(int paramInt, ReadableMap paramReadableMap)
  {
    int i = ViewUtil.getUIManagerType(paramInt);
    if (i == 2)
    {
      UIManagerHelper.getUIManager(getReactApplicationContext(), i).synchronouslyUpdateViewOnUIThread(paramInt, paramReadableMap);
      return;
    }
    mUIImplementation.synchronouslyUpdateViewOnUIThread(paramInt, new ReactStylesDiffMap(paramReadableMap));
  }
  
  public void updateNodeSize(int paramInt1, int paramInt2, int paramInt3)
  {
    getReactApplicationContext().assertOnNativeModulesQueueThread();
    mUIImplementation.updateNodeSize(paramInt1, paramInt2, paramInt3);
  }
  
  public void updateRootLayoutSpecs(final int paramInt1, final int paramInt2, final int paramInt3)
  {
    ReactApplicationContext localReactApplicationContext = getReactApplicationContext();
    localReactApplicationContext.runOnNativeModulesQueueThread(new GuardedRunnable(localReactApplicationContext)
    {
      public void runGuarded()
      {
        mUIImplementation.updateRootView(paramInt1, paramInt2, paramInt3);
        mUIImplementation.dispatchViewUpdates(-1);
      }
    });
  }
  
  public void updateView(int paramInt, String paramString, ReadableMap paramReadableMap)
  {
    if (DEBUG)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("(UIManager.updateView) tag: ");
      ((StringBuilder)localObject).append(paramInt);
      ((StringBuilder)localObject).append(", class: ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(", props: ");
      ((StringBuilder)localObject).append(paramReadableMap);
      localObject = ((StringBuilder)localObject).toString();
      FLog.d("ReactNative", (String)localObject);
      PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.UI_MANAGER, (String)localObject);
    }
    int i = ViewUtil.getUIManagerType(paramInt);
    if (i == 2)
    {
      UIManagerHelper.getUIManager(getReactApplicationContext(), i).synchronouslyUpdateViewOnUIThread(paramInt, paramReadableMap);
      return;
    }
    mUIImplementation.updateView(paramInt, paramString, paramReadableMap);
  }
  
  public void viewIsDescendantOf(int paramInt1, int paramInt2, Callback paramCallback)
  {
    mUIImplementation.viewIsDescendantOf(paramInt1, paramInt2, paramCallback);
  }
  
  public static abstract interface CustomEventNamesResolver
  {
    public abstract String resolveCustomEventName(String paramString);
  }
  
  private class MemoryTrimCallback
    implements ComponentCallbacks2
  {
    private MemoryTrimCallback() {}
    
    public void onConfigurationChanged(Configuration paramConfiguration) {}
    
    public void onLowMemory() {}
    
    public void onTrimMemory(int paramInt)
    {
      if (paramInt >= 60) {
        YogaNodePool.getInstance().clear();
      }
    }
  }
  
  public static abstract interface ViewManagerResolver
  {
    public abstract ViewManager getViewManager(String paramString);
    
    public abstract List getViewManagerNames();
  }
}
