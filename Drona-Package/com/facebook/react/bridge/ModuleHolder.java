package com.facebook.react.bridge;

import androidx.annotation.GuardedBy;
import androidx.annotation.Nullable;
import com.facebook.debug.holder.Printer;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.infer.annotation.Assertions;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Provider;

@DoNotStrip
public class ModuleHolder
{
  private static final AtomicInteger sInstanceKeyCounter = new AtomicInteger(1);
  @GuardedBy("this")
  private boolean mInitializable;
  private final int mInstanceKey = sInstanceKeyCounter.getAndIncrement();
  @GuardedBy("this")
  private boolean mIsCreating;
  @GuardedBy("this")
  private boolean mIsInitializing;
  @GuardedBy("this")
  @Nullable
  private NativeModule mModule;
  private final String mName;
  @Nullable
  private Provider<? extends NativeModule> mProvider;
  private final ReactModuleInfo mReactModuleInfo;
  
  public ModuleHolder(NativeModule paramNativeModule)
  {
    mName = paramNativeModule.getName();
    mReactModuleInfo = new ReactModuleInfo(paramNativeModule.getName(), paramNativeModule.getClass().getSimpleName(), paramNativeModule.canOverrideExistingModule(), true, true, CxxModuleWrapper.class.isAssignableFrom(paramNativeModule.getClass()), TurboModule.class.isAssignableFrom(paramNativeModule.getClass()));
    mModule = paramNativeModule;
    PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.NATIVE_MODULE, "NativeModule init: %s", new Object[] { mName });
  }
  
  public ModuleHolder(ReactModuleInfo paramReactModuleInfo, Provider paramProvider)
  {
    mName = paramReactModuleInfo.name();
    mProvider = paramProvider;
    mReactModuleInfo = paramReactModuleInfo;
    if (paramReactModuleInfo.needsEagerInit()) {
      mModule = create();
    }
  }
  
  /* Error */
  private NativeModule create()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 84	com/facebook/react/bridge/ModuleHolder:mModule	Lcom/facebook/react/bridge/NativeModule;
    //   4: astore 4
    //   6: iconst_0
    //   7: istore_2
    //   8: aload 4
    //   10: ifnonnull +8 -> 18
    //   13: iconst_1
    //   14: istore_3
    //   15: goto +5 -> 20
    //   18: iconst_0
    //   19: istore_3
    //   20: iload_3
    //   21: ldc 121
    //   23: invokestatic 127	com/facebook/react/bridge/SoftAssertions:assertCondition	(ZLjava/lang/String;)V
    //   26: getstatic 133	com/facebook/react/bridge/ReactMarkerConstants:CREATE_MODULE_START	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   29: aload_0
    //   30: getfield 54	com/facebook/react/bridge/ModuleHolder:mName	Ljava/lang/String;
    //   33: aload_0
    //   34: getfield 46	com/facebook/react/bridge/ModuleHolder:mInstanceKey	I
    //   37: invokestatic 139	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;Ljava/lang/String;I)V
    //   40: lconst_0
    //   41: ldc -115
    //   43: invokestatic 147	com/facebook/systrace/SystraceMessage:beginSection	(JLjava/lang/String;)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   46: ldc -108
    //   48: aload_0
    //   49: getfield 54	com/facebook/react/bridge/ModuleHolder:mName	Ljava/lang/String;
    //   52: invokevirtual 154	com/facebook/systrace/SystraceMessage$Builder:put	(Ljava/lang/String;Ljava/lang/Object;)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   55: invokevirtual 157	com/facebook/systrace/SystraceMessage$Builder:flush	()V
    //   58: invokestatic 90	com/facebook/debug/holder/PrinterHolder:getPrinter	()Lcom/facebook/debug/holder/Printer;
    //   61: getstatic 96	com/facebook/debug/tags/ReactDebugOverlayTags:NATIVE_MODULE	Lcom/facebook/debug/debugoverlay/model/DebugOverlayTag;
    //   64: ldc 98
    //   66: iconst_1
    //   67: anewarray 4	java/lang/Object
    //   70: dup
    //   71: iconst_0
    //   72: aload_0
    //   73: getfield 54	com/facebook/react/bridge/ModuleHolder:mName	Ljava/lang/String;
    //   76: aastore
    //   77: invokeinterface 104 4 0
    //   82: aload_0
    //   83: getfield 110	com/facebook/react/bridge/ModuleHolder:mProvider	Ljavax/inject/Provider;
    //   86: invokestatic 163	com/facebook/infer/annotation/Assertions:assertNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   89: checkcast 165	javax/inject/Provider
    //   92: invokeinterface 169 1 0
    //   97: checkcast 48	com/facebook/react/bridge/NativeModule
    //   100: astore 4
    //   102: aload_0
    //   103: aconst_null
    //   104: putfield 110	com/facebook/react/bridge/ModuleHolder:mProvider	Ljavax/inject/Provider;
    //   107: aload_0
    //   108: monitorenter
    //   109: aload_0
    //   110: aload 4
    //   112: putfield 84	com/facebook/react/bridge/ModuleHolder:mModule	Lcom/facebook/react/bridge/NativeModule;
    //   115: iload_2
    //   116: istore_1
    //   117: aload_0
    //   118: getfield 171	com/facebook/react/bridge/ModuleHolder:mInitializable	Z
    //   121: ifeq +14 -> 135
    //   124: iload_2
    //   125: istore_1
    //   126: aload_0
    //   127: getfield 173	com/facebook/react/bridge/ModuleHolder:mIsInitializing	Z
    //   130: ifne +5 -> 135
    //   133: iconst_1
    //   134: istore_1
    //   135: aload_0
    //   136: monitorexit
    //   137: iload_1
    //   138: ifeq +9 -> 147
    //   141: aload_0
    //   142: aload 4
    //   144: invokespecial 176	com/facebook/react/bridge/ModuleHolder:doInitialize	(Lcom/facebook/react/bridge/NativeModule;)V
    //   147: getstatic 179	com/facebook/react/bridge/ReactMarkerConstants:CREATE_MODULE_END	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   150: aload_0
    //   151: getfield 54	com/facebook/react/bridge/ModuleHolder:mName	Ljava/lang/String;
    //   154: aload_0
    //   155: getfield 46	com/facebook/react/bridge/ModuleHolder:mInstanceKey	I
    //   158: invokestatic 139	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;Ljava/lang/String;I)V
    //   161: lconst_0
    //   162: invokestatic 183	com/facebook/systrace/SystraceMessage:endSection	(J)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   165: invokevirtual 157	com/facebook/systrace/SystraceMessage$Builder:flush	()V
    //   168: aload 4
    //   170: areturn
    //   171: astore 4
    //   173: aload_0
    //   174: monitorexit
    //   175: aload 4
    //   177: athrow
    //   178: astore 4
    //   180: getstatic 179	com/facebook/react/bridge/ReactMarkerConstants:CREATE_MODULE_END	Lcom/facebook/react/bridge/ReactMarkerConstants;
    //   183: aload_0
    //   184: getfield 54	com/facebook/react/bridge/ModuleHolder:mName	Ljava/lang/String;
    //   187: aload_0
    //   188: getfield 46	com/facebook/react/bridge/ModuleHolder:mInstanceKey	I
    //   191: invokestatic 139	com/facebook/react/bridge/ReactMarker:logMarker	(Lcom/facebook/react/bridge/ReactMarkerConstants;Ljava/lang/String;I)V
    //   194: lconst_0
    //   195: invokestatic 183	com/facebook/systrace/SystraceMessage:endSection	(J)Lcom/facebook/systrace/SystraceMessage$Builder;
    //   198: invokevirtual 157	com/facebook/systrace/SystraceMessage$Builder:flush	()V
    //   201: aload 4
    //   203: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	204	0	this	ModuleHolder
    //   116	22	1	i	int
    //   7	118	2	j	int
    //   14	7	3	bool	boolean
    //   4	165	4	localNativeModule	NativeModule
    //   171	5	4	localThrowable1	Throwable
    //   178	24	4	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   109	115	171	java/lang/Throwable
    //   117	124	171	java/lang/Throwable
    //   126	133	171	java/lang/Throwable
    //   135	137	171	java/lang/Throwable
    //   173	175	171	java/lang/Throwable
    //   82	109	178	java/lang/Throwable
    //   141	147	178	java/lang/Throwable
    //   175	178	178	java/lang/Throwable
  }
  
  private void doInitialize(NativeModule paramNativeModule)
  {
    SystraceMessage.beginSection(0L, "ModuleHolder.initialize").put("name", mName).flush();
    ReactMarker.logMarker(ReactMarkerConstants.INITIALIZE_MODULE_START, mName, mInstanceKey);
    for (;;)
    {
      try
      {
        try
        {
          boolean bool = mInitializable;
          i = 1;
          if ((bool) && (!mIsInitializing))
          {
            mIsInitializing = true;
            if (i != 0)
            {
              paramNativeModule.initialize();
              try
              {
                mIsInitializing = false;
              }
              catch (Throwable paramNativeModule)
              {
                throw paramNativeModule;
              }
            }
            ReactMarker.logMarker(ReactMarkerConstants.INITIALIZE_MODULE_END, mName, mInstanceKey);
            SystraceMessage.endSection(0L).flush();
            return;
          }
        }
        catch (Throwable paramNativeModule)
        {
          throw paramNativeModule;
        }
        int i = 0;
      }
      catch (Throwable paramNativeModule)
      {
        ReactMarker.logMarker(ReactMarkerConstants.INITIALIZE_MODULE_END, mName, mInstanceKey);
        SystraceMessage.endSection(0L).flush();
        throw paramNativeModule;
      }
    }
  }
  
  public void destroy()
  {
    try
    {
      if (mModule != null) {
        mModule.onCatalystInstanceDestroy();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean getCanOverrideExistingModule()
  {
    return mReactModuleInfo.canOverrideExistingModule();
  }
  
  public String getClassName()
  {
    return mReactModuleInfo.className();
  }
  
  public boolean getHasConstants()
  {
    return mReactModuleInfo.hasConstants();
  }
  
  public NativeModule getModule()
  {
    for (;;)
    {
      try
      {
        NativeModule localNativeModule1;
        if (mModule != null)
        {
          localNativeModule1 = mModule;
          return localNativeModule1;
        }
        boolean bool = mIsCreating;
        i = 1;
        if (bool) {
          break label122;
        }
        mIsCreating = true;
        if (i != 0)
        {
          localNativeModule1 = create();
          try
          {
            mIsCreating = false;
            notifyAll();
            return localNativeModule1;
          }
          catch (Throwable localThrowable1)
          {
            throw localThrowable1;
          }
        }
        try
        {
          if (mModule == null)
          {
            bool = mIsCreating;
            if (!bool) {}
          }
        }
        catch (Throwable localThrowable2)
        {
          NativeModule localNativeModule2;
          throw localThrowable2;
        }
      }
      catch (Throwable localThrowable3)
      {
        throw localThrowable3;
      }
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
      localNativeModule2 = (NativeModule)Assertions.assertNotNull(mModule);
      return localNativeModule2;
      continue;
      label122:
      int i = 0;
    }
  }
  
  public String getName()
  {
    return mName;
  }
  
  boolean hasInstance()
  {
    try
    {
      NativeModule localNativeModule = mModule;
      boolean bool;
      if (localNativeModule != null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isCxxModule()
  {
    return mReactModuleInfo.isCxxModule();
  }
  
  public boolean isTurboModule()
  {
    return mReactModuleInfo.isTurboModule();
  }
  
  void markInitializable()
  {
    int i = 1;
    for (;;)
    {
      try
      {
        mInitializable = true;
        if (mModule == null) {
          break label51;
        }
        Assertions.assertCondition(mIsInitializing ^ true);
        NativeModule localNativeModule = mModule;
        if (i != 0)
        {
          doInitialize(localNativeModule);
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      return;
      label51:
      i = 0;
      Object localObject = null;
    }
  }
}
