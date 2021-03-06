package com.airbnb.lottie.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import com.airbnb.lottie.Way;
import com.airbnb.lottie.animation.LPaint;
import com.airbnb.lottie.animation.content.TrimPathContent;
import com.airbnb.lottie.animation.keyframe.FloatKeyframeAnimation;
import java.io.Closeable;

public final class Utils
{
  public static final int SECOND_IN_NANOS = 1000000000;
  private static final float SQRT_2 = (float)Math.sqrt(2.0D);
  private static float dpScale = -1.0F;
  private static final PathMeasure pathMeasure = new PathMeasure();
  private static final float[] points;
  private static final Path tempPath = new Path();
  private static final Path tempPath2 = new Path();
  
  static
  {
    points = new float[4];
  }
  
  private Utils() {}
  
  public static void applyTrimPathIfNeeded(Path paramPath, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    Way.beginSection("applyTrimPathIfNeeded");
    pathMeasure.setPath(paramPath, false);
    float f2 = pathMeasure.getLength();
    if ((paramFloat1 == 1.0F) && (paramFloat2 == 0.0F))
    {
      Way.endSection("applyTrimPathIfNeeded");
      return;
    }
    if ((f2 >= 1.0F) && (Math.abs(paramFloat2 - paramFloat1 - 1.0F) >= 0.01D))
    {
      float f1 = paramFloat1 * f2;
      paramFloat2 *= f2;
      paramFloat1 = Math.min(f1, paramFloat2);
      f1 = Math.max(f1, paramFloat2);
      paramFloat3 *= f2;
      paramFloat2 = paramFloat1 + paramFloat3;
      f1 += paramFloat3;
      paramFloat3 = paramFloat2;
      paramFloat1 = f1;
      if (paramFloat2 >= f2)
      {
        paramFloat3 = paramFloat2;
        paramFloat1 = f1;
        if (f1 >= f2)
        {
          paramFloat3 = MiscUtils.floorMod(paramFloat2, f2);
          paramFloat1 = MiscUtils.floorMod(f1, f2);
        }
      }
      paramFloat2 = paramFloat3;
      if (paramFloat3 < 0.0F) {
        paramFloat2 = MiscUtils.floorMod(paramFloat3, f2);
      }
      paramFloat3 = paramFloat1;
      if (paramFloat1 < 0.0F) {
        paramFloat3 = MiscUtils.floorMod(paramFloat1, f2);
      }
      boolean bool = paramFloat2 < paramFloat3;
      if (!bool)
      {
        paramPath.reset();
        Way.endSection("applyTrimPathIfNeeded");
        return;
      }
      paramFloat1 = paramFloat2;
      if (!bool) {
        paramFloat1 = paramFloat2 - f2;
      }
      tempPath.reset();
      pathMeasure.getSegment(paramFloat1, paramFloat3, tempPath, true);
      if (paramFloat3 > f2)
      {
        tempPath2.reset();
        pathMeasure.getSegment(0.0F, paramFloat3 % f2, tempPath2, true);
        tempPath.addPath(tempPath2);
      }
      else if (paramFloat1 < 0.0F)
      {
        tempPath2.reset();
        pathMeasure.getSegment(paramFloat1 + f2, f2, tempPath2, true);
        tempPath.addPath(tempPath2);
      }
      paramPath.set(tempPath);
      Way.endSection("applyTrimPathIfNeeded");
      return;
    }
    Way.endSection("applyTrimPathIfNeeded");
  }
  
  public static void applyTrimPathIfNeeded(Path paramPath, TrimPathContent paramTrimPathContent)
  {
    if (paramTrimPathContent != null)
    {
      if (paramTrimPathContent.isHidden()) {
        return;
      }
      float f1 = ((FloatKeyframeAnimation)paramTrimPathContent.getStart()).getFloatValue();
      float f2 = ((FloatKeyframeAnimation)paramTrimPathContent.getEnd()).getFloatValue();
      float f3 = ((FloatKeyframeAnimation)paramTrimPathContent.getOffset()).getFloatValue();
      applyTrimPathIfNeeded(paramPath, f1 / 100.0F, f2 / 100.0F, f3 / 360.0F);
    }
  }
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
        return;
      }
      catch (RuntimeException paramCloseable)
      {
        throw paramCloseable;
      }
      catch (Exception paramCloseable) {}
    }
  }
  
  public static Path createPath(PointF paramPointF1, PointF paramPointF2, PointF paramPointF3, PointF paramPointF4)
  {
    Path localPath = new Path();
    localPath.moveTo(x, y);
    if ((paramPointF3 != null) && (paramPointF4 != null) && ((paramPointF3.length() != 0.0F) || (paramPointF4.length() != 0.0F)))
    {
      float f = x;
      localPath.cubicTo(x + f, y + y, x + x, y + y, x, y);
      return localPath;
    }
    localPath.lineTo(x, y);
    return localPath;
  }
  
  public static float dpScale()
  {
    if (dpScale == -1.0F) {
      dpScale = getSystemgetDisplayMetricsdensity;
    }
    return dpScale;
  }
  
  public static float getAnimationScale(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return Settings.Global.getFloat(paramContext.getContentResolver(), "animator_duration_scale", 1.0F);
    }
    return Settings.System.getFloat(paramContext.getContentResolver(), "animator_duration_scale", 1.0F);
  }
  
  public static float getScale(Matrix paramMatrix)
  {
    points[0] = 0.0F;
    points[1] = 0.0F;
    points[2] = SQRT_2;
    points[3] = SQRT_2;
    paramMatrix.mapPoints(points);
    float f1 = points[2];
    float f2 = points[0];
    float f3 = points[3];
    float f4 = points[1];
    return (float)Math.hypot(f1 - f2, f3 - f4) / 2.0F;
  }
  
  public static boolean hasZeroScaleAxis(Matrix paramMatrix)
  {
    points[0] = 0.0F;
    points[1] = 0.0F;
    points[2] = 1.19236672E9F;
    points[3] = 1.19292493E9F;
    paramMatrix.mapPoints(points);
    if (points[0] != points[2]) {
      return points[1] == points[3];
    }
    return true;
  }
  
  public static int hashFor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (paramFloat1 != 0.0F) {
      j = (int)(527.0F * paramFloat1);
    } else {
      j = 17;
    }
    int i = j;
    if (paramFloat2 != 0.0F) {
      i = (int)(j * 31 * paramFloat2);
    }
    int j = i;
    if (paramFloat3 != 0.0F) {
      j = (int)(i * 31 * paramFloat3);
    }
    i = j;
    if (paramFloat4 != 0.0F) {
      i = (int)(j * 31 * paramFloat4);
    }
    return i;
  }
  
  public static boolean isAtLeastVersion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (paramInt1 < paramInt4) {
      return false;
    }
    if (paramInt1 > paramInt4) {
      return true;
    }
    if (paramInt2 < paramInt5) {
      return false;
    }
    if (paramInt2 > paramInt5) {
      return true;
    }
    return paramInt3 >= paramInt6;
  }
  
  public static Bitmap renderPath(Path paramPath)
  {
    Object localObject = new RectF();
    paramPath.computeBounds((RectF)localObject, false);
    localObject = Bitmap.createBitmap((int)right, (int)bottom, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas((Bitmap)localObject);
    LPaint localLPaint = new LPaint();
    localLPaint.setAntiAlias(true);
    localLPaint.setColor(-16776961);
    localCanvas.drawPath(paramPath, localLPaint);
    return localObject;
  }
  
  public static Bitmap resizeBitmapIfNeeded(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if ((paramBitmap.getWidth() == paramInt1) && (paramBitmap.getHeight() == paramInt2)) {
      return paramBitmap;
    }
    paramBitmap.getWidth();
    paramBitmap.getHeight();
    Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, paramInt1, paramInt2, true);
    paramBitmap.recycle();
    return localBitmap;
  }
}
