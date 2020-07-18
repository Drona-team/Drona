package com.airbnb.lottie.parser;

import android.graphics.PointF;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatablePathValue;
import com.airbnb.lottie.model.animatable.AnimatableScaleValue;
import com.airbnb.lottie.model.animatable.AnimatableSplitDimensionPathValue;
import com.airbnb.lottie.model.animatable.AnimatableTransform;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Options;
import com.airbnb.lottie.parser.moshi.JsonReader.Token;
import com.airbnb.lottie.value.Keyframe;
import com.airbnb.lottie.value.ScaleXY;
import java.io.IOException;
import java.util.List;

public class AnimatableTransformParser
{
  private static JsonReader.Options ANIMATABLE_NAMES = JsonReader.Options.init(new String[] { "k" });
  private static JsonReader.Options NAMES = JsonReader.Options.init(new String[] { "a", "p", "s", "rz", "r", "o", "so", "eo", "sk", "sa" });
  
  private AnimatableTransformParser() {}
  
  private static boolean isAnchorPointIdentity(AnimatablePathValue paramAnimatablePathValue)
  {
    return (paramAnimatablePathValue == null) || ((paramAnimatablePathValue.isStatic()) && (((PointF)getKeyframesget0startValue).equals(0.0F, 0.0F)));
  }
  
  private static boolean isPositionIdentity(AnimatableValue paramAnimatableValue)
  {
    return (paramAnimatableValue == null) || ((!(paramAnimatableValue instanceof AnimatableSplitDimensionPathValue)) && (paramAnimatableValue.isStatic()) && (((PointF)getKeyframesget0startValue).equals(0.0F, 0.0F)));
  }
  
  private static boolean isRotationIdentity(AnimatableFloatValue paramAnimatableFloatValue)
  {
    return (paramAnimatableFloatValue == null) || ((paramAnimatableFloatValue.isStatic()) && (((Float)getKeyframesget0startValue).floatValue() == 0.0F));
  }
  
  private static boolean isScaleIdentity(AnimatableScaleValue paramAnimatableScaleValue)
  {
    return (paramAnimatableScaleValue == null) || ((paramAnimatableScaleValue.isStatic()) && (((ScaleXY)getKeyframesget0startValue).equals(1.0F, 1.0F)));
  }
  
  private static boolean isSkewAngleIdentity(AnimatableFloatValue paramAnimatableFloatValue)
  {
    return (paramAnimatableFloatValue == null) || ((paramAnimatableFloatValue.isStatic()) && (((Float)getKeyframesget0startValue).floatValue() == 0.0F));
  }
  
  private static boolean isSkewIdentity(AnimatableFloatValue paramAnimatableFloatValue)
  {
    return (paramAnimatableFloatValue == null) || ((paramAnimatableFloatValue.isStatic()) && (((Float)getKeyframesget0startValue).floatValue() == 0.0F));
  }
  
  public static AnimatableTransform parse(JsonReader paramJsonReader, LottieComposition paramLottieComposition)
    throws IOException
  {
    int i;
    if (paramJsonReader.peek() == JsonReader.Token.BEGIN_OBJECT) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      paramJsonReader.beginObject();
    }
    Object localObject4 = null;
    Object localObject1 = null;
    AnimatableValue localAnimatableValue = null;
    Object localObject3 = null;
    Object localObject2 = null;
    AnimatableFloatValue localAnimatableFloatValue1 = null;
    AnimatableIntegerValue localAnimatableIntegerValue = null;
    AnimatableFloatValue localAnimatableFloatValue3 = null;
    AnimatableFloatValue localAnimatableFloatValue2 = null;
    if (paramJsonReader.hasNext())
    {
      switch (paramJsonReader.selectName(NAMES))
      {
      default: 
        paramJsonReader.skipName();
        paramJsonReader.skipValue();
      }
      for (;;)
      {
        break;
        localAnimatableFloatValue1 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition, false);
        break;
        localObject2 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition, false);
        break;
        localAnimatableFloatValue2 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition, false);
        break;
        localAnimatableFloatValue3 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition, false);
        break;
        localAnimatableIntegerValue = AnimatableValueParser.parseInteger(paramJsonReader, paramLottieComposition);
        break;
        paramLottieComposition.addWarning("Lottie doesn't support 3D layers.");
        localObject4 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition, false);
        if (((AnimatableFloatValue)localObject4).getKeyframes().isEmpty()) {
          ((AnimatableFloatValue)localObject4).getKeyframes().add(new Keyframe(paramLottieComposition, Float.valueOf(0.0F), Float.valueOf(0.0F), null, 0.0F, Float.valueOf(paramLottieComposition.getEndFrame())));
        }
        for (;;)
        {
          break;
          if (getKeyframesget0startValue == null) {
            ((AnimatableFloatValue)localObject4).getKeyframes().set(0, new Keyframe(paramLottieComposition, Float.valueOf(0.0F), Float.valueOf(0.0F), null, 0.0F, Float.valueOf(paramLottieComposition.getEndFrame())));
          }
        }
        continue;
        localObject3 = AnimatableValueParser.parseScale(paramJsonReader, paramLottieComposition);
        continue;
        localAnimatableValue = AnimatablePathValueParser.parseSplitPath(paramJsonReader, paramLottieComposition);
        continue;
        paramJsonReader.beginObject();
        while (paramJsonReader.hasNext()) {
          if (paramJsonReader.selectName(ANIMATABLE_NAMES) != 0)
          {
            paramJsonReader.skipName();
            paramJsonReader.skipValue();
          }
          else
          {
            localObject1 = AnimatablePathValueParser.parse(paramJsonReader, paramLottieComposition);
          }
        }
        paramJsonReader.endObject();
      }
    }
    if (i != 0) {
      paramJsonReader.endObject();
    }
    paramJsonReader = (JsonReader)localObject1;
    if (isAnchorPointIdentity((AnimatablePathValue)localObject1)) {
      paramJsonReader = null;
    }
    paramLottieComposition = localAnimatableValue;
    if (isPositionIdentity(localAnimatableValue)) {
      paramLottieComposition = null;
    }
    localObject1 = localObject4;
    if (isRotationIdentity((AnimatableFloatValue)localObject4)) {
      localObject1 = null;
    }
    localObject4 = localObject3;
    if (isScaleIdentity((AnimatableScaleValue)localObject3)) {
      localObject4 = null;
    }
    localObject3 = localObject2;
    if (isSkewIdentity((AnimatableFloatValue)localObject2)) {
      localObject3 = null;
    }
    localObject2 = localAnimatableFloatValue1;
    if (isSkewAngleIdentity(localAnimatableFloatValue1)) {
      localObject2 = null;
    }
    return new AnimatableTransform(paramJsonReader, paramLottieComposition, (AnimatableScaleValue)localObject4, (AnimatableFloatValue)localObject1, localAnimatableIntegerValue, localAnimatableFloatValue3, localAnimatableFloatValue2, (AnimatableFloatValue)localObject3, (AnimatableFloatValue)localObject2);
  }
}
