package com.facebook.infer.annotation;

import java.util.List;
import java.util.Map;

public class Assertions
{
  public Assertions() {}
  
  public static void assertCondition(boolean paramBoolean)
  {
    if (paramBoolean) {
      return;
    }
    throw new AssertionError();
  }
  
  public static void assertCondition(boolean paramBoolean, String paramString)
  {
    if (paramBoolean) {
      return;
    }
    throw new AssertionError(paramString);
  }
  
  public static Object assertNotNull(Object paramObject)
  {
    if (paramObject != null) {
      return paramObject;
    }
    throw new AssertionError();
  }
  
  public static Object assertNotNull(Object paramObject, String paramString)
  {
    if (paramObject != null) {
      return paramObject;
    }
    throw new AssertionError(paramString);
  }
  
  public static AssertionError assertUnreachable()
  {
    throw new AssertionError();
  }
  
  public static AssertionError assertUnreachable(Exception paramException)
  {
    throw new AssertionError(paramException);
  }
  
  public static AssertionError assertUnreachable(String paramString)
  {
    throw new AssertionError(paramString);
  }
  
  public static void assumeCondition(boolean paramBoolean) {}
  
  public static void assumeCondition(boolean paramBoolean, String paramString) {}
  
  public static Object assumeNotNull(Object paramObject)
  {
    return paramObject;
  }
  
  public static Object assumeNotNull(Object paramObject, String paramString)
  {
    return paramObject;
  }
  
  public static Object getAssertingNotNull(List paramList, int paramInt)
  {
    boolean bool;
    if ((paramInt >= 0) && (paramInt < paramList.size())) {
      bool = true;
    } else {
      bool = false;
    }
    assertCondition(bool);
    return assertNotNull(paramList.get(paramInt));
  }
  
  public static Object getAssertingNotNull(Map paramMap, Object paramObject)
  {
    assertCondition(paramMap.containsKey(paramObject));
    return assertNotNull(paramMap.get(paramObject));
  }
  
  public static Object getAssumingNotNull(List paramList, int paramInt)
  {
    return paramList.get(paramInt);
  }
  
  public static Object getAssumingNotNull(Map paramMap, Object paramObject)
  {
    return paramMap.get(paramObject);
  }
}
