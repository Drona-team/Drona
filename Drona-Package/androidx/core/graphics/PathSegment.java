package androidx.core.graphics;

import android.graphics.PointF;
import androidx.core.util.Preconditions;

public final class PathSegment
{
  private final PointF mEnd;
  private final float mEndFraction;
  private final PointF mStart;
  private final float mStartFraction;
  
  public PathSegment(PointF paramPointF1, float paramFloat1, PointF paramPointF2, float paramFloat2)
  {
    mStart = ((PointF)Preconditions.checkNotNull(paramPointF1, "start == null"));
    mStartFraction = paramFloat1;
    mEnd = ((PointF)Preconditions.checkNotNull(paramPointF2, "end == null"));
    mEndFraction = paramFloat2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PathSegment)) {
      return false;
    }
    paramObject = (PathSegment)paramObject;
    return (Float.compare(mStartFraction, mStartFraction) == 0) && (Float.compare(mEndFraction, mEndFraction) == 0) && (mStart.equals(mStart)) && (mEnd.equals(mEnd));
  }
  
  public PointF getEnd()
  {
    return mEnd;
  }
  
  public float getEndFraction()
  {
    return mEndFraction;
  }
  
  public PointF getStart()
  {
    return mStart;
  }
  
  public float getStartFraction()
  {
    return mStartFraction;
  }
  
  public int hashCode()
  {
    int k = mStart.hashCode();
    float f = mStartFraction;
    int j = 0;
    int i;
    if (f != 0.0F) {
      i = Float.floatToIntBits(mStartFraction);
    } else {
      i = 0;
    }
    int m = mEnd.hashCode();
    if (mEndFraction != 0.0F) {
      j = Float.floatToIntBits(mEndFraction);
    }
    return ((k * 31 + i) * 31 + m) * 31 + j;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PathSegment{start=");
    localStringBuilder.append(mStart);
    localStringBuilder.append(", startFraction=");
    localStringBuilder.append(mStartFraction);
    localStringBuilder.append(", end=");
    localStringBuilder.append(mEnd);
    localStringBuilder.append(", endFraction=");
    localStringBuilder.append(mEndFraction);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}
