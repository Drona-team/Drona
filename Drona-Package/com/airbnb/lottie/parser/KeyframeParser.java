package com.airbnb.lottie.parser;

import android.graphics.PointF;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.collection.SparseArrayCompat;
import androidx.core.view.animation.PathInterpolatorCompat;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Options;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.utils.Utils;
import com.airbnb.lottie.value.Keyframe;
import java.io.IOException;
import java.lang.ref.WeakReference;

class KeyframeParser
{
  private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
  private static final float MAX_CP_VALUE = 100.0F;
  static JsonReader.Options NAMES = JsonReader.Options.init(new String[] { "t", "s", "e", "o", "i", "h", "to", "ti" });
  private static SparseArrayCompat<WeakReference<Interpolator>> pathInterpolatorCache;
  
  KeyframeParser() {}
  
  private static WeakReference getInterpolator(int paramInt)
  {
    try
    {
      WeakReference localWeakReference = (WeakReference)pathInterpolatorCache().get(paramInt);
      return localWeakReference;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  static Keyframe parse(JsonReader paramJsonReader, LottieComposition paramLottieComposition, float paramFloat, ValueParser paramValueParser, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      return parseKeyframe(paramLottieComposition, paramJsonReader, paramFloat, paramValueParser);
    }
    return parseStaticValue(paramJsonReader, paramFloat, paramValueParser);
  }
  
  private static Keyframe parseKeyframe(LottieComposition paramLottieComposition, JsonReader paramJsonReader, float paramFloat, ValueParser paramValueParser)
    throws IOException
  {
    paramJsonReader.beginObject();
    Object localObject3 = null;
    PointF localPointF4 = null;
    PointF localPointF3 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    PointF localPointF2 = null;
    PointF localPointF1 = null;
    int i = 0;
    float f1 = 0.0F;
    while (paramJsonReader.hasNext()) {
      switch (paramJsonReader.selectName(NAMES))
      {
      default: 
        paramJsonReader.skipValue();
        break;
      case 7: 
        localPointF1 = JsonUtils.jsonToPoint(paramJsonReader, paramFloat);
        break;
      case 6: 
        localPointF2 = JsonUtils.jsonToPoint(paramJsonReader, paramFloat);
        break;
      case 5: 
        if (paramJsonReader.nextInt() == 1) {
          i = 1;
        } else {
          i = 0;
        }
        break;
      case 4: 
        localPointF3 = JsonUtils.jsonToPoint(paramJsonReader, paramFloat);
        break;
      case 3: 
        localPointF4 = JsonUtils.jsonToPoint(paramJsonReader, paramFloat);
        break;
      case 2: 
        localObject2 = paramValueParser.parse(paramJsonReader, paramFloat);
        break;
      case 1: 
        localObject1 = paramValueParser.parse(paramJsonReader, paramFloat);
        break;
      case 0: 
        f1 = (float)paramJsonReader.nextDouble();
      }
    }
    paramJsonReader.endObject();
    if (i != 0)
    {
      paramValueParser = LINEAR_INTERPOLATOR;
      paramJsonReader = localObject1;
    }
    else if ((localPointF4 != null) && (localPointF3 != null))
    {
      float f2 = x;
      float f3 = -paramFloat;
      x = MiscUtils.clamp(f2, f3, paramFloat);
      y = MiscUtils.clamp(y, -100.0F, 100.0F);
      x = MiscUtils.clamp(x, f3, paramFloat);
      y = MiscUtils.clamp(y, -100.0F, 100.0F);
      i = Utils.hashFor(x, y, x, y);
      paramValueParser = getInterpolator(i);
      paramJsonReader = localObject3;
      if (paramValueParser != null) {
        paramJsonReader = (Interpolator)paramValueParser.get();
      }
      if (paramValueParser != null)
      {
        paramValueParser = paramJsonReader;
        if (paramJsonReader != null) {}
      }
      else
      {
        paramJsonReader = PathInterpolatorCompat.create(x / paramFloat, y / paramFloat, x / paramFloat, y / paramFloat);
        paramValueParser = paramJsonReader;
      }
    }
    try
    {
      putInterpolator(i, new WeakReference(paramJsonReader));
      paramJsonReader = localObject2;
      break label445;
      paramValueParser = LINEAR_INTERPOLATOR;
      paramJsonReader = localObject2;
      label445:
      paramLottieComposition = new Keyframe(paramLottieComposition, localObject1, paramJsonReader, paramValueParser, f1, null);
      pathCp1 = localPointF2;
      pathCp2 = localPointF1;
      return paramLottieComposition;
    }
    catch (ArrayIndexOutOfBoundsException paramJsonReader)
    {
      for (;;) {}
    }
  }
  
  private static Keyframe parseStaticValue(JsonReader paramJsonReader, float paramFloat, ValueParser paramValueParser)
    throws IOException
  {
    return new Keyframe(paramValueParser.parse(paramJsonReader, paramFloat));
  }
  
  private static SparseArrayCompat pathInterpolatorCache()
  {
    if (pathInterpolatorCache == null) {
      pathInterpolatorCache = new SparseArrayCompat();
    }
    return pathInterpolatorCache;
  }
  
  private static void putInterpolator(int paramInt, WeakReference paramWeakReference)
  {
    try
    {
      pathInterpolatorCache.put(paramInt, paramWeakReference);
      return;
    }
    catch (Throwable paramWeakReference)
    {
      throw paramWeakReference;
    }
  }
}
