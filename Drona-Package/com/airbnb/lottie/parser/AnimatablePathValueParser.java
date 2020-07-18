package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableFloatValue;
import com.airbnb.lottie.model.animatable.AnimatablePathValue;
import com.airbnb.lottie.model.animatable.AnimatableSplitDimensionPathValue;
import com.airbnb.lottie.model.animatable.AnimatableValue;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Options;
import com.airbnb.lottie.parser.moshi.JsonReader.Token;
import com.airbnb.lottie.utils.Utils;
import com.airbnb.lottie.value.Keyframe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimatablePathValueParser
{
  private static JsonReader.Options NAMES = JsonReader.Options.init(new String[] { "k", "x", "y" });
  
  private AnimatablePathValueParser() {}
  
  public static AnimatablePathValue parse(JsonReader paramJsonReader, LottieComposition paramLottieComposition)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    if (paramJsonReader.peek() == JsonReader.Token.BEGIN_ARRAY)
    {
      paramJsonReader.beginArray();
      while (paramJsonReader.hasNext()) {
        localArrayList.add(PathKeyframeParser.parse(paramJsonReader, paramLottieComposition));
      }
      paramJsonReader.endArray();
      KeyframesParser.setEndFrames(localArrayList);
    }
    else
    {
      localArrayList.add(new Keyframe(JsonUtils.jsonToPoint(paramJsonReader, Utils.dpScale())));
    }
    return new AnimatablePathValue(localArrayList);
  }
  
  static AnimatableValue parseSplitPath(JsonReader paramJsonReader, LottieComposition paramLottieComposition)
    throws IOException
  {
    paramJsonReader.beginObject();
    AnimatablePathValue localAnimatablePathValue = null;
    int i = 0;
    AnimatableFloatValue localAnimatableFloatValue2 = null;
    AnimatableFloatValue localAnimatableFloatValue1 = null;
    while (paramJsonReader.peek() != JsonReader.Token.END_OBJECT) {
      switch (paramJsonReader.selectName(NAMES))
      {
      default: 
        paramJsonReader.skipName();
        paramJsonReader.skipValue();
        break;
      case 2: 
        if (paramJsonReader.peek() == JsonReader.Token.STRING) {
          paramJsonReader.skipValue();
        } else {
          localAnimatableFloatValue1 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition);
        }
        break;
      case 1: 
        if (paramJsonReader.peek() == JsonReader.Token.STRING)
        {
          paramJsonReader.skipValue();
          i = 1;
        }
        else
        {
          localAnimatableFloatValue2 = AnimatableValueParser.parseFloat(paramJsonReader, paramLottieComposition);
        }
        break;
      case 0: 
        localAnimatablePathValue = parse(paramJsonReader, paramLottieComposition);
      }
    }
    paramJsonReader.endObject();
    if (i != 0) {
      paramLottieComposition.addWarning("Lottie doesn't support expressions.");
    }
    if (localAnimatablePathValue != null) {
      return localAnimatablePathValue;
    }
    return new AnimatableSplitDimensionPathValue(localAnimatableFloatValue2, localAnimatableFloatValue1);
  }
}