package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatableGradientColorValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatablePointValue;
import com.airbnb.lottie.model.content.GradientStroke;
import com.airbnb.lottie.model.content.GradientType;
import com.airbnb.lottie.model.content.ShapeStroke.LineCapType;
import com.airbnb.lottie.model.content.ShapeStroke.LineJoinType;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Options;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GradientStrokeParser
{
  private static final JsonReader.Options DASH_PATTERN_NAMES = JsonReader.Options.init(new String[] { "n", "v" });
  private static final JsonReader.Options GRADIENT_NAMES;
  private static JsonReader.Options NAMES = JsonReader.Options.init(new String[] { "nm", "g", "o", "t", "s", "e", "w", "lc", "lj", "ml", "hd", "d" });
  
  static
  {
    GRADIENT_NAMES = JsonReader.Options.init(new String[] { "p", "k" });
  }
  
  private GradientStrokeParser() {}
  
  static GradientStroke parse(JsonReader paramJsonReader, LottieComposition paramLottieComposition)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    String str1 = null;
    Object localObject3 = null;
    AnimatableGradientColorValue localAnimatableGradientColorValue = null;
    AnimatableIntegerValue localAnimatableIntegerValue = null;
    AnimatablePointValue localAnimatablePointValue2 = null;
    AnimatablePointValue localAnimatablePointValue1 = null;
    AnimatableFloatValue localAnimatableFloatValue = null;
    ShapeStroke.LineCapType localLineCapType = null;
    ShapeStroke.LineJoinType localLineJoinType = null;
    float f = 0.0F;
    Object localObject2 = null;
    boolean bool = false;
    while (paramJsonReader.hasNext())
    {
      Object localObject1;
      switch (paramJsonReader.selectName(NAMES))
      {
      default: 
        paramJsonReader.skipName();
        paramJsonReader.skipValue();
        break;
      case 11: 
        paramJsonReader.beginArray();
        while (paramJsonReader.hasNext())
        {
          paramJsonReader.beginObject();
          String str2 = null;
          localObject1 = null;
          while (paramJsonReader.hasNext()) {
            switch (paramJsonReader.selectName(DASH_PATTERN_NAMES))
            {
            default: 
              paramJsonReader.skipName();
              paramJsonReader.skipValue();
              break;
            case 1: 
              localObject1 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition);
              break;
            case 0: 
              str2 = paramJsonReader.nextString();
            }
          }
          paramJsonReader.endObject();
          Object localObject4;
          if (str2.equals("o"))
          {
            localObject4 = localObject1;
          }
          else
          {
            if (str2.equals("d")) {
              break label328;
            }
            localObject4 = localObject2;
            if (str2.equals("g")) {
              break label328;
            }
          }
          localObject2 = localObject4;
          continue;
          paramLottieComposition.setHasDashPattern(true);
          localArrayList.add(localObject1);
        }
        paramJsonReader.endArray();
        if (localArrayList.size() == 1) {
          localArrayList.add(localArrayList.get(0));
        }
        break;
      case 10: 
        bool = paramJsonReader.nextBoolean();
        break;
      case 9: 
        f = (float)paramJsonReader.nextDouble();
        break;
      case 8: 
        localLineJoinType = ShapeStroke.LineJoinType.values()[(paramJsonReader.nextInt() - 1)];
        break;
      case 7: 
        localLineCapType = ShapeStroke.LineCapType.values()[(paramJsonReader.nextInt() - 1)];
        break;
      case 6: 
        localAnimatableFloatValue = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition);
        break;
      case 5: 
        localAnimatablePointValue1 = AnimatableValueParser.parsePoint(paramJsonReader, paramLottieComposition);
        break;
      case 4: 
        localAnimatablePointValue2 = AnimatableValueParser.parsePoint(paramJsonReader, paramLottieComposition);
        break;
      case 3: 
        if (paramJsonReader.nextInt() == 1) {}
        for (localObject1 = GradientType.LINEAR;; localObject1 = GradientType.RADIAL)
        {
          localObject3 = localObject1;
          break;
        }
      case 2: 
        localAnimatableIntegerValue = AnimatableValueParser.parseInteger(paramJsonReader, paramLottieComposition);
        break;
      case 1: 
        int i = -1;
        paramJsonReader.beginObject();
        while (paramJsonReader.hasNext()) {
          switch (paramJsonReader.selectName(GRADIENT_NAMES))
          {
          default: 
            paramJsonReader.skipName();
            paramJsonReader.skipValue();
            break;
          case 1: 
            localAnimatableGradientColorValue = AnimatableValueParser.parseGradientColor(paramJsonReader, paramLottieComposition, i);
            break;
          case 0: 
            i = paramJsonReader.nextInt();
          }
        }
        paramJsonReader.endObject();
        break;
      case 0: 
        label328:
        str1 = paramJsonReader.nextString();
      }
    }
    return new GradientStroke(str1, localObject3, localAnimatableGradientColorValue, localAnimatableIntegerValue, localAnimatablePointValue2, localAnimatablePointValue1, localAnimatableFloatValue, localLineCapType, localLineJoinType, f, localArrayList, localObject2, bool);
  }
}