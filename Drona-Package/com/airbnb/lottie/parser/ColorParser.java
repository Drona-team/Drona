package com.airbnb.lottie.parser;

import android.graphics.Color;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Token;
import java.io.IOException;

public class ColorParser
  implements ValueParser<Integer>
{
  public static final ColorParser INSTANCE = new ColorParser();
  
  private ColorParser() {}
  
  public Integer parse(JsonReader paramJsonReader, float paramFloat)
    throws IOException
  {
    int i;
    if (paramJsonReader.peek() == JsonReader.Token.BEGIN_ARRAY) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      paramJsonReader.beginArray();
    }
    double d12 = paramJsonReader.nextDouble();
    double d4 = d12;
    double d11 = paramJsonReader.nextDouble();
    double d5 = d11;
    double d10 = paramJsonReader.nextDouble();
    double d6 = d10;
    double d9 = paramJsonReader.nextDouble();
    double d7 = d9;
    if (i != 0) {
      paramJsonReader.endArray();
    }
    double d8 = d4;
    double d3 = d5;
    double d2 = d6;
    double d1 = d7;
    if (d12 <= 1.0D)
    {
      d8 = d4;
      d3 = d5;
      d2 = d6;
      d1 = d7;
      if (d11 <= 1.0D)
      {
        d8 = d4;
        d3 = d5;
        d2 = d6;
        d1 = d7;
        if (d10 <= 1.0D)
        {
          d8 = d4;
          d3 = d5;
          d2 = d6;
          d1 = d7;
          if (d9 <= 1.0D)
          {
            d8 = d12 * 255.0D;
            d3 = d11 * 255.0D;
            d2 = d10 * 255.0D;
            d1 = d9 * 255.0D;
          }
        }
      }
    }
    return Integer.valueOf(Color.argb((int)d1, (int)d8, (int)d3, (int)d2));
  }
}
