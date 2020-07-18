package com.airbnb.lottie.parser;

import android.graphics.Path.FillType;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableColorValue;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.content.ShapeFill;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Options;
import java.io.IOException;

class ShapeFillParser
{
  private static final JsonReader.Options NAMES = JsonReader.Options.init(new String[] { "nm", "c", "o", "fillEnabled", "r", "hd" });
  
  private ShapeFillParser() {}
  
  static ShapeFill parse(JsonReader paramJsonReader, LottieComposition paramLottieComposition)
    throws IOException
  {
    String str = null;
    AnimatableColorValue localAnimatableColorValue = null;
    AnimatableIntegerValue localAnimatableIntegerValue = null;
    int i = 1;
    boolean bool2 = false;
    boolean bool1 = false;
    while (paramJsonReader.hasNext()) {
      switch (paramJsonReader.selectName(NAMES))
      {
      default: 
        paramJsonReader.skipName();
        paramJsonReader.skipValue();
        break;
      case 5: 
        bool1 = paramJsonReader.nextBoolean();
        break;
      case 4: 
        i = paramJsonReader.nextInt();
        break;
      case 3: 
        bool2 = paramJsonReader.nextBoolean();
        break;
      case 2: 
        localAnimatableIntegerValue = AnimatableValueParser.parseInteger(paramJsonReader, paramLottieComposition);
        break;
      case 1: 
        localAnimatableColorValue = AnimatableValueParser.parseColor(paramJsonReader, paramLottieComposition);
        break;
      case 0: 
        str = paramJsonReader.nextString();
      }
    }
    if (i == 1) {}
    for (paramJsonReader = Path.FillType.WINDING;; paramJsonReader = Path.FillType.EVEN_ODD) {
      break;
    }
    return new ShapeFill(str, bool2, paramJsonReader, localAnimatableColorValue, localAnimatableIntegerValue, bool1);
  }
}