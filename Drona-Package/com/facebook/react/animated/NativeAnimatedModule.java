package com.facebook.react.animated;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.core.ReactChoreographer.CallbackType;
import com.facebook.react.uimanager.GuardedFrameCallback;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.UIManagerModuleListener;
import java.util.ArrayList;
import java.util.Iterator;

@ReactModule(name="NativeAnimatedModule")
public class NativeAnimatedModule
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener, UIManagerModuleListener
{
  public static final String NAME = "NativeAnimatedModule";
  private final GuardedFrameCallback mAnimatedFrameCallback;
  @Nullable
  private NativeAnimatedNodesManager mNodesManager;
  private ArrayList<UIThreadOperation> mOperations = new ArrayList();
  private ArrayList<UIThreadOperation> mPreOperations = new ArrayList();
  private final ReactChoreographer mReactChoreographer = ReactChoreographer.getInstance();
  
  public NativeAnimatedModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    mAnimatedFrameCallback = new GuardedFrameCallback(paramReactApplicationContext)
    {
      protected void doFrameGuarded(long paramAnonymousLong)
      {
        NativeAnimatedNodesManager localNativeAnimatedNodesManager = NativeAnimatedModule.this.getNodesManager();
        if (localNativeAnimatedNodesManager.hasActiveAnimations()) {
          localNativeAnimatedNodesManager.runUpdates(paramAnonymousLong);
        }
        ((ReactChoreographer)Assertions.assertNotNull(mReactChoreographer)).postFrameCallback(ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE, mAnimatedFrameCallback);
      }
    };
  }
  
  private void clearFrameCallback()
  {
    ((ReactChoreographer)Assertions.assertNotNull(mReactChoreographer)).removeFrameCallback(ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE, mAnimatedFrameCallback);
  }
  
  private void enqueueFrameCallback()
  {
    ((ReactChoreographer)Assertions.assertNotNull(mReactChoreographer)).postFrameCallback(ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE, mAnimatedFrameCallback);
  }
  
  private NativeAnimatedNodesManager getNodesManager()
  {
    if (mNodesManager == null) {
      mNodesManager = new NativeAnimatedNodesManager((UIManagerModule)getReactApplicationContext().getNativeModule(UIManagerModule.class));
    }
    return mNodesManager;
  }
  
  public void addAnimatedEventToView(final int paramInt, final String paramString, final ReadableMap paramReadableMap)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.addAnimatedEventToView(paramInt, paramString, paramReadableMap);
      }
    });
  }
  
  public void connectAnimatedNodeToView(final int paramInt1, final int paramInt2)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.connectAnimatedNodeToView(paramInt1, paramInt2);
      }
    });
  }
  
  public void connectAnimatedNodes(final int paramInt1, final int paramInt2)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.connectAnimatedNodes(paramInt1, paramInt2);
      }
    });
  }
  
  public void createAnimatedNode(final int paramInt, final ReadableMap paramReadableMap)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.createAnimatedNode(paramInt, paramReadableMap);
      }
    });
  }
  
  public void disconnectAnimatedNodeFromView(final int paramInt1, final int paramInt2)
  {
    mPreOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.restoreDefaultValues(paramInt1, paramInt2);
      }
    });
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.disconnectAnimatedNodeFromView(paramInt1, paramInt2);
      }
    });
  }
  
  public void disconnectAnimatedNodes(final int paramInt1, final int paramInt2)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.disconnectAnimatedNodes(paramInt1, paramInt2);
      }
    });
  }
  
  public void dropAnimatedNode(final int paramInt)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.dropAnimatedNode(paramInt);
      }
    });
  }
  
  public void extractAnimatedNodeOffset(final int paramInt)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.extractAnimatedNodeOffset(paramInt);
      }
    });
  }
  
  public void flattenAnimatedNodeOffset(final int paramInt)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.flattenAnimatedNodeOffset(paramInt);
      }
    });
  }
  
  public String getName()
  {
    return "NativeAnimatedModule";
  }
  
  public void initialize()
  {
    ReactApplicationContext localReactApplicationContext = getReactApplicationContext();
    UIManagerModule localUIManagerModule = (UIManagerModule)localReactApplicationContext.getNativeModule(UIManagerModule.class);
    localReactApplicationContext.addLifecycleEventListener(this);
    localUIManagerModule.addUIManagerListener(this);
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause()
  {
    clearFrameCallback();
  }
  
  public void onHostResume()
  {
    enqueueFrameCallback();
  }
  
  public void removeAnimatedEventFromView(final int paramInt1, final String paramString, final int paramInt2)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.removeAnimatedEventFromView(paramInt1, paramString, paramInt2);
      }
    });
  }
  
  public void setAnimatedNodeOffset(final int paramInt, final double paramDouble)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.setAnimatedNodeOffset(paramInt, paramDouble);
      }
    });
  }
  
  public void setAnimatedNodeValue(final int paramInt, final double paramDouble)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.setAnimatedNodeValue(paramInt, paramDouble);
      }
    });
  }
  
  public void setNodesManager(NativeAnimatedNodesManager paramNativeAnimatedNodesManager)
  {
    mNodesManager = paramNativeAnimatedNodesManager;
  }
  
  public void startAnimatingNode(final int paramInt1, final int paramInt2, final ReadableMap paramReadableMap, final Callback paramCallback)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.startAnimatingNode(paramInt1, paramInt2, paramReadableMap, paramCallback);
      }
    });
  }
  
  public void startListeningToAnimatedNodeValue(final int paramInt)
  {
    final AnimatedNodeValueListener local5 = new AnimatedNodeValueListener()
    {
      public void onValueUpdate(double paramAnonymousDouble)
      {
        WritableMap localWritableMap = Arguments.createMap();
        localWritableMap.putInt("tag", paramInt);
        localWritableMap.putDouble("value", paramAnonymousDouble);
        ((DeviceEventManagerModule.RCTDeviceEventEmitter)getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit("onAnimatedValueUpdate", localWritableMap);
      }
    };
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.startListeningToAnimatedNodeValue(paramInt, local5);
      }
    });
  }
  
  public void stopAnimation(final int paramInt)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.stopAnimation(paramInt);
      }
    });
  }
  
  public void stopListeningToAnimatedNodeValue(final int paramInt)
  {
    mOperations.add(new UIThreadOperation()
    {
      public void execute(NativeAnimatedNodesManager paramAnonymousNativeAnimatedNodesManager)
      {
        paramAnonymousNativeAnimatedNodesManager.stopListeningToAnimatedNodeValue(paramInt);
      }
    });
  }
  
  public void willDispatchViewUpdates(UIManagerModule paramUIManagerModule)
  {
    if ((mOperations.isEmpty()) && (mPreOperations.isEmpty())) {
      return;
    }
    final ArrayList localArrayList1 = mPreOperations;
    final ArrayList localArrayList2 = mOperations;
    mPreOperations = new ArrayList();
    mOperations = new ArrayList();
    paramUIManagerModule.prependUIBlock(new UIBlock()
    {
      public void execute(NativeViewHierarchyManager paramAnonymousNativeViewHierarchyManager)
      {
        paramAnonymousNativeViewHierarchyManager = NativeAnimatedModule.this.getNodesManager();
        Iterator localIterator = localArrayList1.iterator();
        while (localIterator.hasNext()) {
          ((NativeAnimatedModule.UIThreadOperation)localIterator.next()).execute(paramAnonymousNativeViewHierarchyManager);
        }
      }
    });
    paramUIManagerModule.addUIBlock(new UIBlock()
    {
      public void execute(NativeViewHierarchyManager paramAnonymousNativeViewHierarchyManager)
      {
        paramAnonymousNativeViewHierarchyManager = NativeAnimatedModule.this.getNodesManager();
        Iterator localIterator = localArrayList2.iterator();
        while (localIterator.hasNext()) {
          ((NativeAnimatedModule.UIThreadOperation)localIterator.next()).execute(paramAnonymousNativeViewHierarchyManager);
        }
      }
    });
  }
  
  private static abstract interface UIThreadOperation
  {
    public abstract void execute(NativeAnimatedNodesManager paramNativeAnimatedNodesManager);
  }
}
