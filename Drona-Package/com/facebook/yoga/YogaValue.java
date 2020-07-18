package com.facebook.yoga;

public class YogaValue
{
  static final YogaValue AUTO = new YogaValue(NaN.0F, YogaUnit.AUTO);
  static final YogaValue UNDEFINED = new YogaValue(NaN.0F, YogaUnit.UNDEFINED);
  static final YogaValue ZERO = new YogaValue(0.0F, YogaUnit.POINT);
  public final YogaUnit unit;
  public final float value;
  
  YogaValue(float paramFloat, int paramInt)
  {
    this(paramFloat, YogaUnit.fromInt(paramInt));
  }
  
  public YogaValue(float paramFloat, YogaUnit paramYogaUnit)
  {
    value = paramFloat;
    unit = paramYogaUnit;
  }
  
  public static YogaValue parse(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    if ("undefined".equals(paramString)) {
      return UNDEFINED;
    }
    if ("auto".equals(paramString)) {
      return AUTO;
    }
    if (paramString.endsWith("%")) {
      return new YogaValue(Float.parseFloat(paramString.substring(0, paramString.length() - 1)), YogaUnit.PERCENT);
    }
    return new YogaValue(Float.parseFloat(paramString), YogaUnit.POINT);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof YogaValue))
    {
      paramObject = (YogaValue)paramObject;
      if ((unit == unit) && ((unit == YogaUnit.UNDEFINED) || (unit == YogaUnit.AUTO) || (Float.compare(value, value) == 0))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return Float.floatToIntBits(value) + unit.intValue();
  }
  
  public String toString()
  {
    switch (1.$SwitchMap$com$facebook$yoga$YogaUnit[unit.ordinal()])
    {
    default: 
      throw new IllegalStateException();
    case 4: 
      return "auto";
    case 3: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(value);
      localStringBuilder.append("%");
      return localStringBuilder.toString();
    case 2: 
      return Float.toString(value);
    }
    return "undefined";
  }
}
