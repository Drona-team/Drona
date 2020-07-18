package com.facebook.common.internal;

import java.util.Arrays;
import javax.annotation.Nullable;

public final class Objects
{
  private Objects() {}
  
  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }
  
  public static Object firstNonNull(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != null) {
      return paramObject1;
    }
    return Preconditions.checkNotNull(paramObject2);
  }
  
  public static int hashCode(Object... paramVarArgs)
  {
    return Arrays.hashCode(paramVarArgs);
  }
  
  private static String simpleName(Class paramClass)
  {
    paramClass = paramClass.getName().replaceAll("\\$[0-9]+", "\\$");
    int j = paramClass.lastIndexOf('$');
    int i = j;
    if (j == -1) {
      i = paramClass.lastIndexOf('.');
    }
    return paramClass.substring(i + 1);
  }
  
  public static ToStringHelper toStringHelper(Class paramClass)
  {
    return new ToStringHelper(simpleName(paramClass), null);
  }
  
  public static ToStringHelper toStringHelper(Object paramObject)
  {
    return new ToStringHelper(simpleName(paramObject.getClass()), null);
  }
  
  public static ToStringHelper toStringHelper(String paramString)
  {
    return new ToStringHelper(paramString, null);
  }
  
  public static final class ToStringHelper
  {
    private final String className;
    private ValueHolder holderHead = new ValueHolder(null);
    private ValueHolder holderTail = holderHead;
    private boolean omitNullValues = false;
    
    private ToStringHelper(String paramString)
    {
      className = ((String)Preconditions.checkNotNull(paramString));
    }
    
    private ValueHolder addHolder()
    {
      ValueHolder localValueHolder = new ValueHolder(null);
      holderTail.next = localValueHolder;
      holderTail = localValueHolder;
      return localValueHolder;
    }
    
    private ToStringHelper addHolder(Object paramObject)
    {
      addHoldervalue = paramObject;
      return this;
    }
    
    private ToStringHelper addHolder(String paramString, Object paramObject)
    {
      ValueHolder localValueHolder = addHolder();
      value = paramObject;
      name = ((String)Preconditions.checkNotNull(paramString));
      return this;
    }
    
    public ToStringHelper add(String paramString, char paramChar)
    {
      return addHolder(paramString, String.valueOf(paramChar));
    }
    
    public ToStringHelper add(String paramString, double paramDouble)
    {
      return addHolder(paramString, String.valueOf(paramDouble));
    }
    
    public ToStringHelper add(String paramString, float paramFloat)
    {
      return addHolder(paramString, String.valueOf(paramFloat));
    }
    
    public ToStringHelper addValue(char paramChar)
    {
      return addHolder(String.valueOf(paramChar));
    }
    
    public ToStringHelper addValue(double paramDouble)
    {
      return addHolder(String.valueOf(paramDouble));
    }
    
    public ToStringHelper addValue(float paramFloat)
    {
      return addHolder(String.valueOf(paramFloat));
    }
    
    public ToStringHelper addValue(int paramInt)
    {
      return addHolder(String.valueOf(paramInt));
    }
    
    public ToStringHelper addValue(long paramLong)
    {
      return addHolder(String.valueOf(paramLong));
    }
    
    public ToStringHelper addValue(Object paramObject)
    {
      return addHolder(paramObject);
    }
    
    public ToStringHelper addValue(String paramString, int paramInt)
    {
      return addHolder(paramString, String.valueOf(paramInt));
    }
    
    public ToStringHelper addValue(String paramString, long paramLong)
    {
      return addHolder(paramString, String.valueOf(paramLong));
    }
    
    public ToStringHelper addValue(String paramString, Object paramObject)
    {
      return addHolder(paramString, paramObject);
    }
    
    public ToStringHelper addValue(String paramString, boolean paramBoolean)
    {
      return addHolder(paramString, String.valueOf(paramBoolean));
    }
    
    public ToStringHelper addValue(boolean paramBoolean)
    {
      return addHolder(String.valueOf(paramBoolean));
    }
    
    public ToStringHelper omitNullValues()
    {
      omitNullValues = true;
      return this;
    }
    
    public String toString()
    {
      boolean bool = omitNullValues;
      Object localObject1 = "";
      StringBuilder localStringBuilder = new StringBuilder(32);
      localStringBuilder.append(className);
      localStringBuilder.append('{');
      ValueHolder localValueHolder = holderHead.next;
      while (localValueHolder != null)
      {
        Object localObject2;
        if (bool)
        {
          localObject2 = localObject1;
          if (value == null) {}
        }
        else
        {
          localStringBuilder.append((String)localObject1);
          localObject2 = ", ";
          if (name != null)
          {
            localStringBuilder.append(name);
            localStringBuilder.append('=');
          }
          localStringBuilder.append(value);
        }
        localValueHolder = next;
        localObject1 = localObject2;
      }
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    private static final class ValueHolder
    {
      @Nullable
      String name;
      ValueHolder next;
      @Nullable
      Object value;
      
      private ValueHolder() {}
    }
  }
}