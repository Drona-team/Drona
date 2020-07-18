package com.bumptech.glide.util;

public class MultiClassKey
{
  private Class<?> first;
  private Class<?> second;
  private Class<?> third;
  
  public MultiClassKey() {}
  
  public MultiClassKey(Class paramClass1, Class paramClass2)
  {
    set(paramClass1, paramClass2);
  }
  
  public MultiClassKey(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    set(paramClass1, paramClass2, paramClass3);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (MultiClassKey)paramObject;
      if (!first.equals(first)) {
        return false;
      }
      if (!second.equals(second)) {
        return false;
      }
      return Util.bothNullOrEqual(third, third);
    }
    return false;
  }
  
  public int hashCode()
  {
    int j = first.hashCode();
    int k = second.hashCode();
    int i;
    if (third != null) {
      i = third.hashCode();
    } else {
      i = 0;
    }
    return (j * 31 + k) * 31 + i;
  }
  
  public void set(Class paramClass1, Class paramClass2)
  {
    set(paramClass1, paramClass2, null);
  }
  
  public void set(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    first = paramClass1;
    second = paramClass2;
    third = paramClass3;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("MultiClassKey{first=");
    localStringBuilder.append(first);
    localStringBuilder.append(", second=");
    localStringBuilder.append(second);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}
