package com.airbnb.lottie.parser;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.model.animatable.AnimatableIntegerValue;
import com.airbnb.lottie.model.animatable.AnimatableShapeValue;
import com.airbnb.lottie.model.content.Mask;
import com.airbnb.lottie.model.content.Mask.MaskMode;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.utils.Logger;
import java.io.IOException;

class MaskParser
{
  private MaskParser() {}
  
  static Mask parse(JsonReader paramJsonReader, LottieComposition paramLottieComposition)
    throws IOException
  {
    paramJsonReader.beginObject();
    Object localObject = null;
    AnimatableShapeValue localAnimatableShapeValue = null;
    AnimatableIntegerValue localAnimatableIntegerValue = null;
    boolean bool = false;
    while (paramJsonReader.hasNext())
    {
      String str = paramJsonReader.nextName();
      int i = str.hashCode();
      int j = 1;
      if (i != 111)
      {
        if (i != 3588)
        {
          if (i != 104433)
          {
            if ((i == 3357091) && (str.equals("mode")))
            {
              i = 0;
              break label127;
            }
          }
          else if (str.equals("inv"))
          {
            i = 3;
            break label127;
          }
        }
        else if (str.equals("pt"))
        {
          i = 1;
          break label127;
        }
      }
      else if (str.equals("o"))
      {
        i = 2;
        break label127;
      }
      i = -1;
      switch (i)
      {
      default: 
        paramJsonReader.skipValue();
        break;
      case 3: 
        bool = paramJsonReader.nextBoolean();
        break;
      case 2: 
        localAnimatableIntegerValue = AnimatableValueParser.parseInteger(paramJsonReader, paramLottieComposition);
        break;
      case 1: 
        localAnimatableShapeValue = AnimatableValueParser.parseShapeData(paramJsonReader, paramLottieComposition);
        break;
      case 0: 
        label127:
        localObject = paramJsonReader.nextString();
        i = ((String)localObject).hashCode();
        if (i != 97)
        {
          if (i != 105)
          {
            if ((i == 115) && (((String)localObject).equals("s")))
            {
              i = j;
              break label291;
            }
          }
          else if (((String)localObject).equals("i"))
          {
            i = 2;
            break label291;
          }
        }
        else if (((String)localObject).equals("a"))
        {
          i = 0;
          break label291;
        }
        i = -1;
        switch (i)
        {
        default: 
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Unknown mask mode ");
          ((StringBuilder)localObject).append(str);
          ((StringBuilder)localObject).append(". Defaulting to Add.");
          Logger.warning(((StringBuilder)localObject).toString());
          localObject = Mask.MaskMode.MASK_MODE_ADD;
          break;
        case 2: 
          paramLottieComposition.addWarning("Animation contains intersect masks. They are not supported but will be treated like add masks.");
          localObject = Mask.MaskMode.MASK_MODE_INTERSECT;
          break;
        case 1: 
          localObject = Mask.MaskMode.MASK_MODE_SUBTRACT;
          break;
        case 0: 
          label291:
          localObject = Mask.MaskMode.MASK_MODE_ADD;
        }
        break;
      }
    }
    paramJsonReader.endObject();
    return new Mask((Mask.MaskMode)localObject, localAnimatableShapeValue, localAnimatableIntegerValue, bool);
  }
}
