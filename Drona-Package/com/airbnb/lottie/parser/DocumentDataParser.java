package com.airbnb.lottie.parser;

import com.airbnb.lottie.model.DocumentData;
import com.airbnb.lottie.model.DocumentData.Justification;
import com.airbnb.lottie.parser.moshi.JsonReader;
import com.airbnb.lottie.parser.moshi.JsonReader.Options;
import java.io.IOException;

public class DocumentDataParser
  implements ValueParser<DocumentData>
{
  public static final DocumentDataParser INSTANCE = new DocumentDataParser();
  private static final JsonReader.Options NAMES = JsonReader.Options.init(new String[] { "t", "f", "s", "j", "tr", "lh", "ls", "fc", "sc", "sw", "of" });
  
  private DocumentDataParser() {}
  
  public DocumentData parse(JsonReader paramJsonReader, float paramFloat)
    throws IOException
  {
    DocumentData.Justification localJustification = DocumentData.Justification.CENTER;
    paramJsonReader.beginObject();
    String str2 = null;
    String str1 = null;
    double d4 = 0.0D;
    double d3 = 0.0D;
    double d2 = 0.0D;
    double d1 = 0.0D;
    int k = 0;
    int j = 0;
    int i = 0;
    boolean bool = true;
    while (paramJsonReader.hasNext()) {
      switch (paramJsonReader.selectName(NAMES))
      {
      default: 
        paramJsonReader.skipName();
        paramJsonReader.skipValue();
        break;
      case 10: 
        bool = paramJsonReader.nextBoolean();
        break;
      case 9: 
        d1 = paramJsonReader.nextDouble();
        break;
      case 8: 
        i = JsonUtils.jsonToColor(paramJsonReader);
        break;
      case 7: 
        j = JsonUtils.jsonToColor(paramJsonReader);
        break;
      case 6: 
        d2 = paramJsonReader.nextDouble();
        break;
      case 5: 
        d3 = paramJsonReader.nextDouble();
        break;
      case 4: 
        k = paramJsonReader.nextInt();
        break;
      case 3: 
        int m = paramJsonReader.nextInt();
        if ((m <= DocumentData.Justification.CENTER.ordinal()) && (m >= 0)) {
          localJustification = DocumentData.Justification.values()[m];
        } else {
          localJustification = DocumentData.Justification.CENTER;
        }
        break;
      case 2: 
        d4 = paramJsonReader.nextDouble();
        break;
      case 1: 
        str1 = paramJsonReader.nextString();
        break;
      case 0: 
        str2 = paramJsonReader.nextString();
      }
    }
    paramJsonReader.endObject();
    return new DocumentData(str2, str1, d4, localJustification, k, d3, d2, j, i, d1, bool);
  }
}
