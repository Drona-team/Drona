package com.facebook.react.uimanager;

import android.view.View;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.DynamicFromObject;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ViewManagersPropertyCache
{
  private static final Map<Class, Map<String, PropSetter>> CLASS_PROPS_CACHE = new HashMap();
  private static final Map<String, PropSetter> EMPTY_PROPS_MAP = new HashMap();
  
  ViewManagersPropertyCache() {}
  
  public static void clear()
  {
    CLASS_PROPS_CACHE.clear();
    EMPTY_PROPS_MAP.clear();
  }
  
  private static PropSetter createPropSetter(ReactProp paramReactProp, Method paramMethod, Class paramClass)
  {
    if (paramClass == Dynamic.class) {
      return new DynamicPropSetter(paramReactProp, paramMethod);
    }
    if (paramClass == Boolean.TYPE) {
      return new BooleanPropSetter(paramReactProp, paramMethod, paramReactProp.defaultBoolean());
    }
    if (paramClass == Integer.TYPE) {
      return new IntPropSetter(paramReactProp, paramMethod, paramReactProp.defaultInt());
    }
    if (paramClass == Float.TYPE) {
      return new FloatPropSetter(paramReactProp, paramMethod, paramReactProp.defaultFloat());
    }
    if (paramClass == Double.TYPE) {
      return new DoublePropSetter(paramReactProp, paramMethod, paramReactProp.defaultDouble());
    }
    if (paramClass == String.class) {
      return new StringPropSetter(paramReactProp, paramMethod);
    }
    if (paramClass == Boolean.class) {
      return new BoxedBooleanPropSetter(paramReactProp, paramMethod);
    }
    if (paramClass == Integer.class) {
      return new BoxedIntPropSetter(paramReactProp, paramMethod);
    }
    if (paramClass == ReadableArray.class) {
      return new ArrayPropSetter(paramReactProp, paramMethod);
    }
    if (paramClass == ReadableMap.class) {
      return new MapPropSetter(paramReactProp, paramMethod);
    }
    paramReactProp = new StringBuilder();
    paramReactProp.append("Unrecognized type: ");
    paramReactProp.append(paramClass);
    paramReactProp.append(" for method: ");
    paramReactProp.append(paramMethod.getDeclaringClass().getName());
    paramReactProp.append("#");
    paramReactProp.append(paramMethod.getName());
    throw new RuntimeException(paramReactProp.toString());
  }
  
  private static void createPropSetters(ReactPropGroup paramReactPropGroup, Method paramMethod, Class paramClass, Map paramMap)
  {
    String[] arrayOfString = paramReactPropGroup.names();
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i = 0;
    if (paramClass == Dynamic.class) {
      while (i < arrayOfString.length)
      {
        paramMap.put(arrayOfString[i], new DynamicPropSetter(paramReactPropGroup, paramMethod, i));
        i += 1;
      }
    }
    if (paramClass == Integer.TYPE)
    {
      i = j;
      while (i < arrayOfString.length)
      {
        paramMap.put(arrayOfString[i], new IntPropSetter(paramReactPropGroup, paramMethod, i, paramReactPropGroup.defaultInt()));
        i += 1;
      }
    }
    if (paramClass == Float.TYPE)
    {
      i = k;
      while (i < arrayOfString.length)
      {
        paramMap.put(arrayOfString[i], new FloatPropSetter(paramReactPropGroup, paramMethod, i, paramReactPropGroup.defaultFloat()));
        i += 1;
      }
    }
    if (paramClass == Double.TYPE)
    {
      i = m;
      while (i < arrayOfString.length)
      {
        paramMap.put(arrayOfString[i], new DoublePropSetter(paramReactPropGroup, paramMethod, i, paramReactPropGroup.defaultDouble()));
        i += 1;
      }
    }
    if (paramClass == Integer.class)
    {
      i = n;
      while (i < arrayOfString.length)
      {
        paramMap.put(arrayOfString[i], new BoxedIntPropSetter(paramReactPropGroup, paramMethod, i));
        i += 1;
      }
      return;
    }
    paramReactPropGroup = new StringBuilder();
    paramReactPropGroup.append("Unrecognized type: ");
    paramReactPropGroup.append(paramClass);
    paramReactPropGroup.append(" for method: ");
    paramReactPropGroup.append(paramMethod.getDeclaringClass().getName());
    paramReactPropGroup.append("#");
    paramReactPropGroup.append(paramMethod.getName());
    throw new RuntimeException(paramReactPropGroup.toString());
  }
  
  private static void extractPropSettersFromShadowNodeClassDefinition(Class paramClass, Map paramMap)
  {
    Method[] arrayOfMethod = paramClass.getDeclaredMethods();
    int j = arrayOfMethod.length;
    int i = 0;
    while (i < j)
    {
      Method localMethod = arrayOfMethod[i];
      Object localObject = (ReactProp)localMethod.getAnnotation(ReactProp.class);
      Class[] arrayOfClass;
      if (localObject != null)
      {
        arrayOfClass = localMethod.getParameterTypes();
        if (arrayOfClass.length == 1)
        {
          paramMap.put(((ReactProp)localObject).name(), createPropSetter((ReactProp)localObject, localMethod, arrayOfClass[0]));
        }
        else
        {
          paramMap = new StringBuilder();
          paramMap.append("Wrong number of args for prop setter: ");
          paramMap.append(paramClass.getName());
          paramMap.append("#");
          paramMap.append(localMethod.getName());
          throw new RuntimeException(paramMap.toString());
        }
      }
      localObject = (ReactPropGroup)localMethod.getAnnotation(ReactPropGroup.class);
      if (localObject != null)
      {
        arrayOfClass = localMethod.getParameterTypes();
        if (arrayOfClass.length == 2)
        {
          if (arrayOfClass[0] == Integer.TYPE)
          {
            createPropSetters((ReactPropGroup)localObject, localMethod, arrayOfClass[1], paramMap);
          }
          else
          {
            paramMap = new StringBuilder();
            paramMap.append("Second argument should be property index: ");
            paramMap.append(paramClass.getName());
            paramMap.append("#");
            paramMap.append(localMethod.getName());
            throw new RuntimeException(paramMap.toString());
          }
        }
        else
        {
          paramMap = new StringBuilder();
          paramMap.append("Wrong number of args for group prop setter: ");
          paramMap.append(paramClass.getName());
          paramMap.append("#");
          paramMap.append(localMethod.getName());
          throw new RuntimeException(paramMap.toString());
        }
      }
      i += 1;
    }
  }
  
  private static void extractPropSettersFromViewManagerClassDefinition(Class paramClass, Map paramMap)
  {
    Method[] arrayOfMethod = paramClass.getDeclaredMethods();
    int i = 0;
    while (i < arrayOfMethod.length)
    {
      Method localMethod = arrayOfMethod[i];
      Object localObject = (ReactProp)localMethod.getAnnotation(ReactProp.class);
      Class[] arrayOfClass;
      if (localObject != null)
      {
        arrayOfClass = localMethod.getParameterTypes();
        if (arrayOfClass.length == 2)
        {
          if (View.class.isAssignableFrom(arrayOfClass[0]))
          {
            paramMap.put(((ReactProp)localObject).name(), createPropSetter((ReactProp)localObject, localMethod, arrayOfClass[1]));
          }
          else
          {
            paramMap = new StringBuilder();
            paramMap.append("First param should be a view subclass to be updated: ");
            paramMap.append(paramClass.getName());
            paramMap.append("#");
            paramMap.append(localMethod.getName());
            throw new RuntimeException(paramMap.toString());
          }
        }
        else
        {
          paramMap = new StringBuilder();
          paramMap.append("Wrong number of args for prop setter: ");
          paramMap.append(paramClass.getName());
          paramMap.append("#");
          paramMap.append(localMethod.getName());
          throw new RuntimeException(paramMap.toString());
        }
      }
      localObject = (ReactPropGroup)localMethod.getAnnotation(ReactPropGroup.class);
      if (localObject != null)
      {
        arrayOfClass = localMethod.getParameterTypes();
        if (arrayOfClass.length == 3)
        {
          if (View.class.isAssignableFrom(arrayOfClass[0]))
          {
            if (arrayOfClass[1] == Integer.TYPE)
            {
              createPropSetters((ReactPropGroup)localObject, localMethod, arrayOfClass[2], paramMap);
            }
            else
            {
              paramMap = new StringBuilder();
              paramMap.append("Second argument should be property index: ");
              paramMap.append(paramClass.getName());
              paramMap.append("#");
              paramMap.append(localMethod.getName());
              throw new RuntimeException(paramMap.toString());
            }
          }
          else
          {
            paramMap = new StringBuilder();
            paramMap.append("First param should be a view subclass to be updated: ");
            paramMap.append(paramClass.getName());
            paramMap.append("#");
            paramMap.append(localMethod.getName());
            throw new RuntimeException(paramMap.toString());
          }
        }
        else
        {
          paramMap = new StringBuilder();
          paramMap.append("Wrong number of args for group prop setter: ");
          paramMap.append(paramClass.getName());
          paramMap.append("#");
          paramMap.append(localMethod.getName());
          throw new RuntimeException(paramMap.toString());
        }
      }
      i += 1;
    }
  }
  
  static Map getNativePropSettersForShadowNodeClass(Class paramClass)
  {
    Object localObject = paramClass.getInterfaces();
    int j = localObject.length;
    int i = 0;
    while (i < j)
    {
      if (localObject[i] == ReactShadowNode.class) {
        return EMPTY_PROPS_MAP;
      }
      i += 1;
    }
    localObject = (Map)CLASS_PROPS_CACHE.get(paramClass);
    if (localObject != null) {
      return localObject;
    }
    localObject = new HashMap(getNativePropSettersForShadowNodeClass(paramClass.getSuperclass()));
    extractPropSettersFromShadowNodeClassDefinition(paramClass, (Map)localObject);
    CLASS_PROPS_CACHE.put(paramClass, localObject);
    return localObject;
  }
  
  static Map getNativePropSettersForViewManagerClass(Class paramClass)
  {
    if (paramClass == ViewManager.class) {
      return EMPTY_PROPS_MAP;
    }
    Object localObject = (Map)CLASS_PROPS_CACHE.get(paramClass);
    if (localObject != null) {
      return localObject;
    }
    localObject = new HashMap(getNativePropSettersForViewManagerClass(paramClass.getSuperclass()));
    extractPropSettersFromViewManagerClassDefinition(paramClass, (Map)localObject);
    CLASS_PROPS_CACHE.put(paramClass, localObject);
    return localObject;
  }
  
  static Map getNativePropsForView(Class paramClass1, Class paramClass2)
  {
    HashMap localHashMap = new HashMap();
    paramClass1 = getNativePropSettersForViewManagerClass(paramClass1).values().iterator();
    while (paramClass1.hasNext())
    {
      PropSetter localPropSetter = (PropSetter)paramClass1.next();
      localHashMap.put(localPropSetter.getPropName(), localPropSetter.getPropType());
    }
    paramClass1 = getNativePropSettersForShadowNodeClass(paramClass2).values().iterator();
    while (paramClass1.hasNext())
    {
      paramClass2 = (PropSetter)paramClass1.next();
      localHashMap.put(paramClass2.getPropName(), paramClass2.getPropType());
    }
    return localHashMap;
  }
  
  private static class ArrayPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    public ArrayPropSetter(ReactProp paramReactProp, Method paramMethod)
    {
      super("Array", paramMethod, null);
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      return (ReadableArray)paramObject;
    }
  }
  
  private static class BooleanPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    private final boolean mDefaultValue;
    
    public BooleanPropSetter(ReactProp paramReactProp, Method paramMethod, boolean paramBoolean)
    {
      super("boolean", paramMethod, null);
      mDefaultValue = paramBoolean;
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      boolean bool;
      if (paramObject == null) {
        bool = mDefaultValue;
      } else {
        bool = ((Boolean)paramObject).booleanValue();
      }
      if (bool) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
  }
  
  private static class BoxedBooleanPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    public BoxedBooleanPropSetter(ReactProp paramReactProp, Method paramMethod)
    {
      super("boolean", paramMethod, null);
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      if (paramObject != null)
      {
        if (((Boolean)paramObject).booleanValue()) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      return null;
    }
  }
  
  private static class BoxedIntPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    public BoxedIntPropSetter(ReactProp paramReactProp, Method paramMethod)
    {
      super("number", paramMethod, null);
    }
    
    public BoxedIntPropSetter(ReactPropGroup paramReactPropGroup, Method paramMethod, int paramInt)
    {
      super("number", paramMethod, paramInt, null);
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      if (paramObject != null)
      {
        if ((paramObject instanceof Double)) {
          return Integer.valueOf(((Double)paramObject).intValue());
        }
        return (Integer)paramObject;
      }
      return null;
    }
  }
  
  private static class DoublePropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    private final double mDefaultValue;
    
    public DoublePropSetter(ReactProp paramReactProp, Method paramMethod, double paramDouble)
    {
      super("number", paramMethod, null);
      mDefaultValue = paramDouble;
    }
    
    public DoublePropSetter(ReactPropGroup paramReactPropGroup, Method paramMethod, int paramInt, double paramDouble)
    {
      super("number", paramMethod, paramInt, null);
      mDefaultValue = paramDouble;
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      double d;
      if (paramObject == null) {
        d = mDefaultValue;
      } else {
        d = ((Double)paramObject).doubleValue();
      }
      return Double.valueOf(d);
    }
  }
  
  private static class DynamicPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    public DynamicPropSetter(ReactProp paramReactProp, Method paramMethod)
    {
      super("mixed", paramMethod, null);
    }
    
    public DynamicPropSetter(ReactPropGroup paramReactPropGroup, Method paramMethod, int paramInt)
    {
      super("mixed", paramMethod, paramInt, null);
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      if ((paramObject instanceof Dynamic)) {
        return paramObject;
      }
      return new DynamicFromObject(paramObject);
    }
  }
  
  private static class FloatPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    private final float mDefaultValue;
    
    public FloatPropSetter(ReactProp paramReactProp, Method paramMethod, float paramFloat)
    {
      super("number", paramMethod, null);
      mDefaultValue = paramFloat;
    }
    
    public FloatPropSetter(ReactPropGroup paramReactPropGroup, Method paramMethod, int paramInt, float paramFloat)
    {
      super("number", paramMethod, paramInt, null);
      mDefaultValue = paramFloat;
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      float f;
      if (paramObject == null) {
        f = mDefaultValue;
      } else {
        f = Float.valueOf(((Double)paramObject).floatValue()).floatValue();
      }
      return Float.valueOf(f);
    }
  }
  
  private static class IntPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    private final int mDefaultValue;
    
    public IntPropSetter(ReactProp paramReactProp, Method paramMethod, int paramInt)
    {
      super("number", paramMethod, null);
      mDefaultValue = paramInt;
    }
    
    public IntPropSetter(ReactPropGroup paramReactPropGroup, Method paramMethod, int paramInt1, int paramInt2)
    {
      super("number", paramMethod, paramInt1, null);
      mDefaultValue = paramInt2;
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      int i;
      if (paramObject == null) {
        i = mDefaultValue;
      } else {
        i = Integer.valueOf(((Double)paramObject).intValue()).intValue();
      }
      return Integer.valueOf(i);
    }
  }
  
  private static class MapPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    public MapPropSetter(ReactProp paramReactProp, Method paramMethod)
    {
      super("Map", paramMethod, null);
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      return (ReadableMap)paramObject;
    }
  }
  
  static abstract class PropSetter
  {
    private static final Object[] SHADOW_ARGS = new Object[1];
    private static final Object[] SHADOW_GROUP_ARGS = new Object[2];
    private static final Object[] VIEW_MGR_ARGS = new Object[2];
    private static final Object[] VIEW_MGR_GROUP_ARGS = new Object[3];
    @Nullable
    protected final Integer mIndex;
    protected final String mPropName;
    protected final String mPropType;
    protected final Method mSetter;
    
    private PropSetter(ReactProp paramReactProp, String paramString, Method paramMethod)
    {
      mPropName = paramReactProp.name();
      if (!"__default_type__".equals(paramReactProp.customType())) {
        paramString = paramReactProp.customType();
      }
      mPropType = paramString;
      mSetter = paramMethod;
      mIndex = null;
    }
    
    private PropSetter(ReactPropGroup paramReactPropGroup, String paramString, Method paramMethod, int paramInt)
    {
      mPropName = paramReactPropGroup.names()[paramInt];
      if (!"__default_type__".equals(paramReactPropGroup.customType())) {
        paramString = paramReactPropGroup.customType();
      }
      mPropType = paramString;
      mSetter = paramMethod;
      mIndex = Integer.valueOf(paramInt);
    }
    
    public String getPropName()
    {
      return mPropName;
    }
    
    public String getPropType()
    {
      return mPropType;
    }
    
    protected abstract Object getValueOrDefault(Object paramObject);
    
    public void updateShadowNodeProp(ReactShadowNode paramReactShadowNode, Object paramObject)
    {
      try
      {
        localObject = mIndex;
        if (localObject == null)
        {
          SHADOW_ARGS[0] = getValueOrDefault(paramObject);
          mSetter.invoke(paramReactShadowNode, SHADOW_ARGS);
          Arrays.fill(SHADOW_ARGS, null);
          return;
        }
        SHADOW_GROUP_ARGS[0] = mIndex;
        SHADOW_GROUP_ARGS[1] = getValueOrDefault(paramObject);
        mSetter.invoke(paramReactShadowNode, SHADOW_GROUP_ARGS);
        Arrays.fill(SHADOW_GROUP_ARGS, null);
        return;
      }
      catch (Throwable paramObject)
      {
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Error while updating prop ");
        ((StringBuilder)localObject).append(mPropName);
        FLog.e(ViewManager.class, ((StringBuilder)localObject).toString(), paramObject);
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Error while updating property '");
        ((StringBuilder)localObject).append(mPropName);
        ((StringBuilder)localObject).append("' in shadow node of type: ");
        ((StringBuilder)localObject).append(paramReactShadowNode.getViewClass());
        throw new JSApplicationIllegalArgumentException(((StringBuilder)localObject).toString(), paramObject);
      }
    }
    
    public void updateViewProp(ViewManager paramViewManager, View paramView, Object paramObject)
    {
      try
      {
        Integer localInteger = mIndex;
        if (localInteger == null)
        {
          VIEW_MGR_ARGS[0] = paramView;
          VIEW_MGR_ARGS[1] = getValueOrDefault(paramObject);
          mSetter.invoke(paramViewManager, VIEW_MGR_ARGS);
          Arrays.fill(VIEW_MGR_ARGS, null);
          return;
        }
        VIEW_MGR_GROUP_ARGS[0] = paramView;
        VIEW_MGR_GROUP_ARGS[1] = mIndex;
        VIEW_MGR_GROUP_ARGS[2] = getValueOrDefault(paramObject);
        mSetter.invoke(paramViewManager, VIEW_MGR_GROUP_ARGS);
        Arrays.fill(VIEW_MGR_GROUP_ARGS, null);
        return;
      }
      catch (Throwable paramView)
      {
        paramObject = new StringBuilder();
        paramObject.append("Error while updating prop ");
        paramObject.append(mPropName);
        FLog.e(ViewManager.class, paramObject.toString(), paramView);
        paramObject = new StringBuilder();
        paramObject.append("Error while updating property '");
        paramObject.append(mPropName);
        paramObject.append("' of a view managed by: ");
        paramObject.append(paramViewManager.getName());
        throw new JSApplicationIllegalArgumentException(paramObject.toString(), paramView);
      }
    }
  }
  
  private static class StringPropSetter
    extends ViewManagersPropertyCache.PropSetter
  {
    public StringPropSetter(ReactProp paramReactProp, Method paramMethod)
    {
      super("String", paramMethod, null);
    }
    
    protected Object getValueOrDefault(Object paramObject)
    {
      return (String)paramObject;
    }
  }
}
