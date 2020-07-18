package androidx.transition;

import android.animation.TypeEvaluator;

class FloatArrayEvaluator
  implements TypeEvaluator<float[]>
{
  private float[] mArray;
  
  FloatArrayEvaluator(float[] paramArrayOfFloat)
  {
    mArray = paramArrayOfFloat;
  }
  
  public float[] evaluate(float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float[] arrayOfFloat2 = mArray;
    float[] arrayOfFloat1 = arrayOfFloat2;
    if (arrayOfFloat2 == null) {
      arrayOfFloat1 = new float[paramArrayOfFloat1.length];
    }
    int i = 0;
    while (i < arrayOfFloat1.length)
    {
      float f = paramArrayOfFloat1[i];
      arrayOfFloat1[i] = (f + (paramArrayOfFloat2[i] - f) * paramFloat);
      i += 1;
    }
    return arrayOfFloat1;
  }
}
