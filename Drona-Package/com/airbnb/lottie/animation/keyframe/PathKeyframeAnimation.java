package com.airbnb.lottie.animation.keyframe;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.LottieValueCallback;
import java.util.List;

public class PathKeyframeAnimation
  extends KeyframeAnimation<PointF>
{
  private final float[] coords = new float[2];
  private PathMeasure pathMeasure = new PathMeasure();
  private PathKeyframe pathMeasureKeyframe;
  private final PointF point = new PointF();
  
  public PathKeyframeAnimation(List paramList)
  {
    super(paramList);
  }
  
  public PointF getValue(Keyframe paramKeyframe, float paramFloat)
  {
    PathKeyframe localPathKeyframe = (PathKeyframe)paramKeyframe;
    Path localPath = localPathKeyframe.getPath();
    if (localPath == null) {
      return (PointF)startValue;
    }
    if (valueCallback != null)
    {
      paramKeyframe = (PointF)valueCallback.getValueInternal(startFrame, endFrame.floatValue(), startValue, endValue, getLinearCurrentKeyframeProgress(), paramFloat, getProgress());
      if (paramKeyframe != null) {
        return paramKeyframe;
      }
    }
    if (pathMeasureKeyframe != localPathKeyframe)
    {
      pathMeasure.setPath(localPath, false);
      pathMeasureKeyframe = localPathKeyframe;
    }
    pathMeasure.getPosTan(paramFloat * pathMeasure.getLength(), coords, null);
    point.set(coords[0], coords[1]);
    return point;
  }
}
