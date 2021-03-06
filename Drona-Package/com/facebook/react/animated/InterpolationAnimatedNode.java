package com.facebook.react.animated;

import androidx.annotation.Nullable;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InterpolationAnimatedNode
  extends ValueAnimatedNode
{
  public static final String EXTRAPOLATE_TYPE_CLAMP = "clamp";
  public static final String EXTRAPOLATE_TYPE_EXTEND = "extend";
  public static final String EXTRAPOLATE_TYPE_IDENTITY = "identity";
  private static final Pattern fpPattern = Pattern.compile("[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?");
  private static final String fpRegex = "[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?";
  private final String mExtrapolateLeft;
  private final String mExtrapolateRight;
  private final boolean mHasStringOutput;
  private final double[] mInputRange;
  private int mNumVals;
  private final double[] mOutputRange;
  private double[][] mOutputs;
  @Nullable
  private ValueAnimatedNode mParent;
  private String mPattern;
  private final Matcher mSOutputMatcher;
  private boolean mShouldRound;
  
  public InterpolationAnimatedNode(ReadableMap paramReadableMap)
  {
    mInputRange = fromDoubleArray(paramReadableMap.getArray("inputRange"));
    Object localObject1 = paramReadableMap.getArray("outputRange");
    boolean bool;
    if (((ReadableArray)localObject1).getType(0) == ReadableType.String) {
      bool = true;
    } else {
      bool = false;
    }
    mHasStringOutput = bool;
    if (mHasStringOutput)
    {
      int k = ((ReadableArray)localObject1).size();
      mOutputRange = new double[k];
      mPattern = ((ReadableArray)localObject1).getString(0);
      mShouldRound = mPattern.startsWith("rgb");
      mSOutputMatcher = fpPattern.matcher(mPattern);
      ArrayList localArrayList1 = new ArrayList();
      int i = 0;
      while (i < k)
      {
        Object localObject2 = ((ReadableArray)localObject1).getString(i);
        localObject2 = fpPattern.matcher((CharSequence)localObject2);
        ArrayList localArrayList2 = new ArrayList();
        localArrayList1.add(localArrayList2);
        while (((Matcher)localObject2).find()) {
          localArrayList2.add(Double.valueOf(Double.parseDouble(((Matcher)localObject2).group())));
        }
        mOutputRange[i] = ((Double)localArrayList2.get(0)).doubleValue();
        i += 1;
      }
      mNumVals = ((ArrayList)localArrayList1.get(0)).size();
      mOutputs = new double[mNumVals][];
      i = 0;
      while (i < mNumVals)
      {
        localObject1 = new double[k];
        mOutputs[i] = localObject1;
        int j = 0;
        while (j < k)
        {
          localObject1[j] = ((Double)((ArrayList)localArrayList1.get(j)).get(i)).doubleValue();
          j += 1;
        }
        i += 1;
      }
    }
    mOutputRange = fromDoubleArray((ReadableArray)localObject1);
    mSOutputMatcher = null;
    mExtrapolateLeft = paramReadableMap.getString("extrapolateLeft");
    mExtrapolateRight = paramReadableMap.getString("extrapolateRight");
  }
  
  private static int findRangeIndex(double paramDouble, double[] paramArrayOfDouble)
  {
    int i = 1;
    while ((i < paramArrayOfDouble.length - 1) && (paramArrayOfDouble[i] < paramDouble)) {
      i += 1;
    }
    return i - 1;
  }
  
  private static double[] fromDoubleArray(ReadableArray paramReadableArray)
  {
    double[] arrayOfDouble = new double[paramReadableArray.size()];
    int i = 0;
    while (i < arrayOfDouble.length)
    {
      arrayOfDouble[i] = paramReadableArray.getDouble(i);
      i += 1;
    }
    return arrayOfDouble;
  }
  
  private static double interpolate(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, String paramString1, String paramString2)
  {
    boolean bool = paramDouble1 < paramDouble2;
    double d = paramDouble1;
    int i;
    if (bool)
    {
      i = paramString1.hashCode();
      if (i != -1289044198)
      {
        if (i != -135761730)
        {
          if ((i == 94742715) && (paramString1.equals("clamp")))
          {
            i = 1;
            break label95;
          }
        }
        else if (paramString1.equals("identity"))
        {
          i = 0;
          break label95;
        }
      }
      else if (paramString1.equals("extend"))
      {
        i = 2;
        break label95;
      }
      i = -1;
      label95:
      d = paramDouble1;
      switch (i)
      {
      default: 
        paramString2 = new StringBuilder();
        paramString2.append("Invalid extrapolation type ");
        paramString2.append(paramString1);
        paramString2.append("for left extrapolation");
        throw new JSApplicationIllegalArgumentException(paramString2.toString());
      case 1: 
        d = paramDouble2;
        break;
      case 0: 
        return paramDouble1;
      }
    }
    paramDouble1 = d;
    if (d > paramDouble3)
    {
      i = paramString2.hashCode();
      if (i != -1289044198)
      {
        if (i != -135761730)
        {
          if ((i == 94742715) && (paramString2.equals("clamp")))
          {
            i = 1;
            break label286;
          }
        }
        else if (paramString2.equals("identity"))
        {
          i = 0;
          break label286;
        }
      }
      else if (paramString2.equals("extend"))
      {
        i = 2;
        break label286;
      }
      i = -1;
      label286:
      paramDouble1 = d;
      switch (i)
      {
      default: 
        paramString1 = new StringBuilder();
        paramString1.append("Invalid extrapolation type ");
        paramString1.append(paramString2);
        paramString1.append("for right extrapolation");
        throw new JSApplicationIllegalArgumentException(paramString1.toString());
      case 1: 
        paramDouble1 = paramDouble3;
        break;
      case 0: 
        return d;
      }
    }
    if (paramDouble4 == paramDouble5) {
      return paramDouble4;
    }
    if (paramDouble2 == paramDouble3)
    {
      if (!bool) {
        return paramDouble4;
      }
      return paramDouble5;
    }
    return paramDouble4 + (paramDouble5 - paramDouble4) * (paramDouble1 - paramDouble2) / (paramDouble3 - paramDouble2);
  }
  
  static double interpolate(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, String paramString1, String paramString2)
  {
    int i = findRangeIndex(paramDouble, paramArrayOfDouble1);
    double d = paramArrayOfDouble1[i];
    int j = i + 1;
    return interpolate(paramDouble, d, paramArrayOfDouble1[j], paramArrayOfDouble2[i], paramArrayOfDouble2[j], paramString1, paramString2);
  }
  
  public void onAttachedToNode(AnimatedNode paramAnimatedNode)
  {
    if (mParent == null)
    {
      if ((paramAnimatedNode instanceof ValueAnimatedNode))
      {
        mParent = ((ValueAnimatedNode)paramAnimatedNode);
        return;
      }
      throw new IllegalArgumentException("Parent is of an invalid type");
    }
    throw new IllegalStateException("Parent already attached");
  }
  
  public void onDetachedFromNode(AnimatedNode paramAnimatedNode)
  {
    if (paramAnimatedNode == mParent)
    {
      mParent = null;
      return;
    }
    throw new IllegalArgumentException("Invalid parent node provided");
  }
  
  public void update()
  {
    if (mParent == null) {
      return;
    }
    double d3 = mParent.getValue();
    mValue = interpolate(d3, mInputRange, mOutputRange, mExtrapolateLeft, mExtrapolateRight);
    if (mHasStringOutput)
    {
      if (mNumVals > 1)
      {
        StringBuffer localStringBuffer = new StringBuffer(mPattern.length());
        mSOutputMatcher.reset();
        int j;
        for (int i = 0; mSOutputMatcher.find(); i = j)
        {
          Object localObject = mInputRange;
          double[][] arrayOfDouble = mOutputs;
          j = i + 1;
          double d2 = interpolate(d3, (double[])localObject, arrayOfDouble[i], mExtrapolateLeft, mExtrapolateRight);
          double d1 = d2;
          if (mShouldRound)
          {
            if (j == 4) {
              i = 1;
            } else {
              i = 0;
            }
            if (i != 0) {
              d1 = d2 * 1000.0D;
            }
            int k = (int)Math.round(d1);
            if (i != 0) {
              localObject = Double.toString(k / 1000.0D);
            } else {
              localObject = Integer.toString(k);
            }
            mSOutputMatcher.appendReplacement(localStringBuffer, (String)localObject);
          }
          else
          {
            i = (int)d2;
            if (i != d2) {
              localObject = Double.toString(d2);
            } else {
              localObject = Integer.toString(i);
            }
            mSOutputMatcher.appendReplacement(localStringBuffer, (String)localObject);
          }
        }
        mSOutputMatcher.appendTail(localStringBuffer);
        mAnimatedObject = localStringBuffer.toString();
        return;
      }
      mAnimatedObject = mSOutputMatcher.replaceFirst(String.valueOf(mValue));
    }
  }
}
