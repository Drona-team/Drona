package com.airbnb.lottie.parser;

import android.graphics.Color;
import com.airbnb.lottie.model.content.GradientColor;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Token;
import com.airbnb.lottie.utils.MiscUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GradientColorParser
  implements ValueParser<GradientColor>
{
  private int colorPoints;
  
  public GradientColorParser(int paramInt)
  {
    colorPoints = paramInt;
  }
  
  private void addOpacityStopsToGradientIfNeeded(GradientColor paramGradientColor, List paramList)
  {
    int i = colorPoints * 4;
    if (paramList.size() <= i) {
      return;
    }
    int j = (paramList.size() - i) / 2;
    double[] arrayOfDouble1 = new double[j];
    double[] arrayOfDouble2 = new double[j];
    int m = 0;
    int k = 0;
    for (;;)
    {
      j = m;
      if (i >= paramList.size()) {
        break;
      }
      if (i % 2 == 0)
      {
        arrayOfDouble1[k] = ((Float)paramList.get(i)).floatValue();
      }
      else
      {
        arrayOfDouble2[k] = ((Float)paramList.get(i)).floatValue();
        k += 1;
      }
      i += 1;
    }
    while (j < paramGradientColor.getSize())
    {
      i = paramGradientColor.getColors()[j];
      i = Color.argb(getOpacityAtPosition(paramGradientColor.getPositions()[j], arrayOfDouble1, arrayOfDouble2), Color.red(i), Color.green(i), Color.blue(i));
      paramGradientColor.getColors()[j] = i;
      j += 1;
    }
  }
  
  private int getOpacityAtPosition(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    int i = 1;
    while (i < paramArrayOfDouble1.length)
    {
      int j = i - 1;
      double d1 = paramArrayOfDouble1[j];
      double d2 = paramArrayOfDouble1[i];
      if (paramArrayOfDouble1[i] >= paramDouble)
      {
        paramDouble = (paramDouble - d1) / (d2 - d1);
        return (int)(MiscUtils.lerp(paramArrayOfDouble2[j], paramArrayOfDouble2[i], paramDouble) * 255.0D);
      }
      i += 1;
    }
    return (int)(paramArrayOfDouble2[(paramArrayOfDouble2.length - 1)] * 255.0D);
  }
  
  public GradientColor parse(JsonReader paramJsonReader, float paramFloat)
    throws IOException
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = paramJsonReader.peek();
    JsonReader.Token localToken = JsonReader.Token.BEGIN_ARRAY;
    int m = 0;
    if (localObject == localToken) {
      i = 1;
    } else {
      i = 0;
    }
    if (i != 0) {
      paramJsonReader.beginArray();
    }
    while (paramJsonReader.hasNext()) {
      localArrayList.add(Float.valueOf((float)paramJsonReader.nextDouble()));
    }
    if (i != 0) {
      paramJsonReader.endArray();
    }
    if (colorPoints == -1) {
      colorPoints = (localArrayList.size() / 4);
    }
    paramJsonReader = new float[colorPoints];
    localObject = new int[colorPoints];
    int k = 0;
    int j = 0;
    int i = m;
    while (i < colorPoints * 4)
    {
      m = i / 4;
      double d = ((Float)localArrayList.get(i)).floatValue();
      switch (i % 4)
      {
      default: 
        break;
      case 3: 
        localObject[m] = Color.argb(255, k, j, (int)(d * 255.0D));
        break;
      case 2: 
        j = (int)(d * 255.0D);
        break;
      case 1: 
        k = (int)(d * 255.0D);
        break;
      case 0: 
        paramJsonReader[m] = ((float)d);
      }
      i += 1;
    }
    paramJsonReader = new GradientColor(paramJsonReader, (int[])localObject);
    addOpacityStopsToGradientIfNeeded(paramJsonReader, localArrayList);
    return paramJsonReader;
  }
}