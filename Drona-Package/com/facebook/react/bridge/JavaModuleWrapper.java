package com.facebook.react.bridge;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.systrace.SystraceMessage.Builder;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DoNotStrip
public class JavaModuleWrapper
{
  private final ArrayList<MethodDescriptor> mDescs;
  private final JSInstance mJSInstance;
  private final ArrayList<NativeModule.NativeMethod> mMethods;
  private final ModuleHolder mModuleHolder;
  
  public JavaModuleWrapper(JSInstance paramJSInstance, ModuleHolder paramModuleHolder)
  {
    mJSInstance = paramJSInstance;
    mModuleHolder = paramModuleHolder;
    mMethods = new ArrayList();
    mDescs = new ArrayList();
  }
  
  private void findMethods()
  {
    Systrace.beginSection(0L, "findMethods");
    HashSet localHashSet = new HashSet();
    Object localObject2 = mModuleHolder.getModule().getClass();
    Object localObject1 = localObject2;
    localObject2 = ((Class)localObject2).getSuperclass();
    if (ReactModuleWithSpec.class.isAssignableFrom((Class)localObject2)) {
      localObject1 = localObject2;
    }
    localObject2 = ((Class)localObject1).getDeclaredMethods();
    int j = localObject2.length;
    int i = 0;
    while (i < j)
    {
      Method localMethod = localObject2[i];
      Object localObject3 = (ReactMethod)localMethod.getAnnotation(ReactMethod.class);
      if (localObject3 != null)
      {
        localObject1 = localMethod.getName();
        if (!localHashSet.contains(localObject1))
        {
          MethodDescriptor localMethodDescriptor = new MethodDescriptor();
          localObject3 = new JavaMethodWrapper(this, localMethod, ((ReactMethod)localObject3).isBlockingSynchronousMethod());
          name = ((String)localObject1);
          type = ((JavaMethodWrapper)localObject3).getType();
          if (type == "sync")
          {
            signature = ((JavaMethodWrapper)localObject3).getSignature();
            method = localMethod;
          }
          mMethods.add(localObject3);
          mDescs.add(localMethodDescriptor);
        }
        else
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Java Module ");
          ((StringBuilder)localObject2).append(getName());
          ((StringBuilder)localObject2).append(" method name already registered: ");
          ((StringBuilder)localObject2).append((String)localObject1);
          throw new IllegalArgumentException(((StringBuilder)localObject2).toString());
        }
      }
      i += 1;
    }
    Systrace.endSection(0L);
  }
  
  public NativeMap getConstants()
  {
    if (!mModuleHolder.getHasConstants()) {
      return null;
    }
    String str = getName();
    SystraceMessage.beginSection(0L, "JavaModuleWrapper.getConstants").put("moduleName", str).flush();
    ReactMarker.logMarker(ReactMarkerConstants.GET_CONSTANTS_START, str);
    Object localObject = getModule();
    Systrace.beginSection(0L, "module.getConstants");
    localObject = ((BaseJavaModule)localObject).getConstants();
    Systrace.endSection(0L);
    Systrace.beginSection(0L, "create WritableNativeMap");
    ReactMarker.logMarker(ReactMarkerConstants.CONVERT_CONSTANTS_START, str);
    try
    {
      localObject = Arguments.makeNativeMap((Map)localObject);
      ReactMarker.logMarker(ReactMarkerConstants.CONVERT_CONSTANTS_END, str);
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.GET_CONSTANTS_END, str);
      SystraceMessage.endSection(0L).flush();
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      ReactMarker.logMarker(ReactMarkerConstants.CONVERT_CONSTANTS_END, str);
      Systrace.endSection(0L);
      ReactMarker.logMarker(ReactMarkerConstants.GET_CONSTANTS_END, str);
      SystraceMessage.endSection(0L).flush();
      throw localThrowable;
    }
  }
  
  public List getMethodDescriptors()
  {
    if (mDescs.isEmpty()) {
      findMethods();
    }
    return mDescs;
  }
  
  public BaseJavaModule getModule()
  {
    return (BaseJavaModule)mModuleHolder.getModule();
  }
  
  public String getName()
  {
    return mModuleHolder.getName();
  }
  
  public void invoke(int paramInt, ReadableNativeArray paramReadableNativeArray)
  {
    if (mMethods != null)
    {
      if (paramInt >= mMethods.size()) {
        return;
      }
      ((NativeModule.NativeMethod)mMethods.get(paramInt)).invoke(mJSInstance, paramReadableNativeArray);
    }
  }
  
  @DoNotStrip
  public class MethodDescriptor
  {
    @DoNotStrip
    Method method;
    @DoNotStrip
    String name;
    @DoNotStrip
    String signature;
    @DoNotStrip
    String type;
    
    public MethodDescriptor() {}
  }
}
