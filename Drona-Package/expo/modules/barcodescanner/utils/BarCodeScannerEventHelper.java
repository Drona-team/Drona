package expo.modules.barcodescanner.utils;

import android.os.Bundle;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class BarCodeScannerEventHelper
{
  public BarCodeScannerEventHelper() {}
  
  public static Pair getCornerPointsAndBoundingBox(List paramList, float paramFloat)
  {
    ArrayList localArrayList = new ArrayList();
    Bundle localBundle = new Bundle();
    float f4 = Float.MAX_VALUE;
    int i = 0;
    float f3 = Float.MAX_VALUE;
    float f2 = Float.MIN_VALUE;
    float f1 = Float.MIN_VALUE;
    while (i < paramList.size())
    {
      float f5 = ((Integer)paramList.get(i)).intValue() / paramFloat;
      float f6 = ((Integer)paramList.get(i + 1)).intValue() / paramFloat;
      f4 = Math.min(f4, f5);
      f3 = Math.min(f3, f6);
      f2 = Math.max(f2, f5);
      f1 = Math.max(f1, f6);
      localArrayList.add(getPoint(f5, f6));
      i += 2;
    }
    localBundle.putParcelable("origin", getPoint(f4, f3));
    localBundle.putParcelable("size", getSize(f2 - f4, f1 - f3));
    return new Pair(localArrayList, localBundle);
  }
  
  private static Bundle getPoint(float paramFloat1, float paramFloat2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putFloat("x", paramFloat1);
    localBundle.putFloat("y", paramFloat2);
    return localBundle;
  }
  
  private static Bundle getSize(float paramFloat1, float paramFloat2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putFloat("width", paramFloat1);
    localBundle.putFloat("height", paramFloat2);
    return localBundle;
  }
}
