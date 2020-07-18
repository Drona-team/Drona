package com.facebook.react.uimanager;

import D;
import com.facebook.infer.annotation.Assertions;
import java.lang.reflect.Array;

public class MatrixMathHelper
{
  private static final double EPSILON = 1.0E-5D;
  
  public MatrixMathHelper() {}
  
  public static void applyPerspective(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[11] = (-1.0D / paramDouble);
  }
  
  public static void applyRotateX(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[5] = Math.cos(paramDouble);
    paramArrayOfDouble[6] = Math.sin(paramDouble);
    paramArrayOfDouble[9] = (-Math.sin(paramDouble));
    paramArrayOfDouble[10] = Math.cos(paramDouble);
  }
  
  public static void applyRotateY(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[0] = Math.cos(paramDouble);
    paramArrayOfDouble[2] = (-Math.sin(paramDouble));
    paramArrayOfDouble[8] = Math.sin(paramDouble);
    paramArrayOfDouble[10] = Math.cos(paramDouble);
  }
  
  public static void applyRotateZ(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[0] = Math.cos(paramDouble);
    paramArrayOfDouble[1] = Math.sin(paramDouble);
    paramArrayOfDouble[4] = (-Math.sin(paramDouble));
    paramArrayOfDouble[5] = Math.cos(paramDouble);
  }
  
  public static void applyScaleX(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[0] = paramDouble;
  }
  
  public static void applyScaleY(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[5] = paramDouble;
  }
  
  public static void applyScaleZ(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[10] = paramDouble;
  }
  
  public static void applySkewX(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[4] = Math.tan(paramDouble);
  }
  
  public static void applySkewY(double[] paramArrayOfDouble, double paramDouble)
  {
    paramArrayOfDouble[1] = Math.tan(paramDouble);
  }
  
  public static void applyTranslate2D(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2)
  {
    paramArrayOfDouble[12] = paramDouble1;
    paramArrayOfDouble[13] = paramDouble2;
  }
  
  public static void applyTranslate3D(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    paramArrayOfDouble[12] = paramDouble1;
    paramArrayOfDouble[13] = paramDouble2;
    paramArrayOfDouble[14] = paramDouble3;
  }
  
  public static double[] createIdentityMatrix()
  {
    double[] arrayOfDouble = new double[16];
    resetIdentityMatrix(arrayOfDouble);
    return arrayOfDouble;
  }
  
  public static void decomposeMatrix(double[] paramArrayOfDouble, MatrixDecompositionContext paramMatrixDecompositionContext)
  {
    boolean bool;
    if (paramArrayOfDouble.length == 16) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.assertCondition(bool);
    double[] arrayOfDouble2 = perspective;
    double[] arrayOfDouble1 = scale;
    Object localObject = skew;
    double[] arrayOfDouble3 = translation;
    paramMatrixDecompositionContext = rotationDegrees;
    if (isZero(paramArrayOfDouble[15])) {
      return;
    }
    double[][] arrayOfDouble = (double[][])Array.newInstance(D.class, new int[] { 4, 4 });
    double[] arrayOfDouble4 = new double[16];
    int i = 0;
    double d1;
    while (i < 4)
    {
      int j = 0;
      while (j < 4)
      {
        int k = i * 4 + j;
        d1 = paramArrayOfDouble[k] / paramArrayOfDouble[15];
        arrayOfDouble[i][j] = d1;
        if (j == 3) {
          d1 = 0.0D;
        }
        arrayOfDouble4[k] = d1;
        j += 1;
      }
      i += 1;
    }
    arrayOfDouble4[15] = 1.0D;
    if (isZero(determinant(arrayOfDouble4))) {
      return;
    }
    if ((isZero(arrayOfDouble[0][3])) && (isZero(arrayOfDouble[1][3])) && (isZero(arrayOfDouble[2][3])))
    {
      arrayOfDouble2[2] = 0.0D;
      arrayOfDouble2[1] = 0.0D;
      arrayOfDouble2[0] = 0.0D;
      arrayOfDouble2[3] = 1.0D;
    }
    else
    {
      d1 = arrayOfDouble[0][3];
      double d2 = arrayOfDouble[1][3];
      double d3 = arrayOfDouble[2][3];
      double d4 = arrayOfDouble[3][3];
      paramArrayOfDouble = transpose(inverse(arrayOfDouble4));
      multiplyVectorByMatrix(new double[] { d1, d2, d3, d4 }, paramArrayOfDouble, arrayOfDouble2);
    }
    i = 0;
    while (i < 3)
    {
      arrayOfDouble3[i] = arrayOfDouble[3][i];
      i += 1;
    }
    paramArrayOfDouble = (double[][])Array.newInstance(D.class, new int[] { 3, 3 });
    i = 0;
    while (i < 3)
    {
      paramArrayOfDouble[i][0] = arrayOfDouble[i][0];
      paramArrayOfDouble[i][1] = arrayOfDouble[i][1];
      paramArrayOfDouble[i][2] = arrayOfDouble[i][2];
      i += 1;
    }
    arrayOfDouble1[0] = v3Length(paramArrayOfDouble[0]);
    paramArrayOfDouble[0] = v3Normalize(paramArrayOfDouble[0], arrayOfDouble1[0]);
    localObject[0] = v3Dot(paramArrayOfDouble[0], paramArrayOfDouble[1]);
    paramArrayOfDouble[1] = v3Combine(paramArrayOfDouble[1], paramArrayOfDouble[0], 1.0D, -localObject[0]);
    localObject[0] = v3Dot(paramArrayOfDouble[0], paramArrayOfDouble[1]);
    paramArrayOfDouble[1] = v3Combine(paramArrayOfDouble[1], paramArrayOfDouble[0], 1.0D, -localObject[0]);
    arrayOfDouble1[1] = v3Length(paramArrayOfDouble[1]);
    paramArrayOfDouble[1] = v3Normalize(paramArrayOfDouble[1], arrayOfDouble1[1]);
    localObject[0] /= arrayOfDouble1[1];
    localObject[1] = v3Dot(paramArrayOfDouble[0], paramArrayOfDouble[2]);
    paramArrayOfDouble[2] = v3Combine(paramArrayOfDouble[2], paramArrayOfDouble[0], 1.0D, -localObject[1]);
    localObject[2] = v3Dot(paramArrayOfDouble[1], paramArrayOfDouble[2]);
    paramArrayOfDouble[2] = v3Combine(paramArrayOfDouble[2], paramArrayOfDouble[1], 1.0D, -localObject[2]);
    arrayOfDouble1[2] = v3Length(paramArrayOfDouble[2]);
    paramArrayOfDouble[2] = v3Normalize(paramArrayOfDouble[2], arrayOfDouble1[2]);
    localObject[1] /= arrayOfDouble1[2];
    localObject[2] /= arrayOfDouble1[2];
    localObject = v3Cross(paramArrayOfDouble[1], paramArrayOfDouble[2]);
    if (v3Dot(paramArrayOfDouble[0], (double[])localObject) < 0.0D)
    {
      i = 0;
      while (i < 3)
      {
        arrayOfDouble1[i] *= -1.0D;
        localObject = paramArrayOfDouble[i];
        localObject[0] *= -1.0D;
        localObject = paramArrayOfDouble[i];
        localObject[1] *= -1.0D;
        localObject = paramArrayOfDouble[i];
        localObject[2] *= -1.0D;
        i += 1;
      }
    }
    paramMatrixDecompositionContext[0] = roundTo3Places(-Math.atan2(paramArrayOfDouble[2][1], paramArrayOfDouble[2][2]) * 57.29577951308232D);
    paramMatrixDecompositionContext[1] = roundTo3Places(-Math.atan2(-paramArrayOfDouble[2][0], Math.sqrt(paramArrayOfDouble[2][1] * paramArrayOfDouble[2][1] + paramArrayOfDouble[2][2] * paramArrayOfDouble[2][2])) * 57.29577951308232D);
    paramMatrixDecompositionContext[2] = roundTo3Places(-Math.atan2(paramArrayOfDouble[1][0], paramArrayOfDouble[0][0]) * 57.29577951308232D);
  }
  
  public static double degreesToRadians(double paramDouble)
  {
    return paramDouble * 3.141592653589793D / 180.0D;
  }
  
  public static double determinant(double[] paramArrayOfDouble)
  {
    double d15 = paramArrayOfDouble[0];
    double d18 = paramArrayOfDouble[1];
    double d22 = paramArrayOfDouble[2];
    double d17 = paramArrayOfDouble[3];
    double d19 = paramArrayOfDouble[4];
    double d16 = paramArrayOfDouble[5];
    double d21 = paramArrayOfDouble[6];
    double d20 = paramArrayOfDouble[7];
    double d1 = paramArrayOfDouble[8];
    double d2 = paramArrayOfDouble[9];
    double d3 = paramArrayOfDouble[10];
    double d4 = paramArrayOfDouble[11];
    double d5 = paramArrayOfDouble[12];
    double d6 = paramArrayOfDouble[13];
    double d7 = paramArrayOfDouble[14];
    double d8 = paramArrayOfDouble[15];
    double d9 = d17 * d21;
    double d10 = d22 * d20;
    double d11 = d17 * d16;
    double d12 = d18 * d20;
    double d13 = d22 * d16;
    double d14 = d18 * d21;
    d17 *= d19;
    d20 *= d15;
    d22 *= d19;
    d21 *= d15;
    d18 *= d19;
    d15 *= d16;
    return d9 * d2 * d5 - d10 * d2 * d5 - d11 * d3 * d5 + d12 * d3 * d5 + d13 * d4 * d5 - d14 * d4 * d5 - d9 * d1 * d6 + d10 * d1 * d6 + d17 * d3 * d6 - d20 * d3 * d6 - d22 * d4 * d6 + d21 * d4 * d6 + d11 * d1 * d7 - d12 * d1 * d7 - d17 * d2 * d7 + d20 * d2 * d7 + d18 * d4 * d7 - d4 * d15 * d7 - d13 * d1 * d8 + d14 * d1 * d8 + d22 * d2 * d8 - d21 * d2 * d8 - d18 * d3 * d8 + d15 * d3 * d8;
  }
  
  public static double[] inverse(double[] paramArrayOfDouble)
  {
    double d1 = determinant(paramArrayOfDouble);
    if (isZero(d1)) {
      return paramArrayOfDouble;
    }
    double d24 = paramArrayOfDouble[0];
    double d26 = paramArrayOfDouble[1];
    double d42 = paramArrayOfDouble[2];
    double d36 = paramArrayOfDouble[3];
    double d27 = paramArrayOfDouble[4];
    double d25 = paramArrayOfDouble[5];
    double d43 = paramArrayOfDouble[6];
    double d41 = paramArrayOfDouble[7];
    double d2 = paramArrayOfDouble[8];
    double d3 = paramArrayOfDouble[9];
    double d4 = paramArrayOfDouble[10];
    double d5 = paramArrayOfDouble[11];
    double d6 = paramArrayOfDouble[12];
    double d7 = paramArrayOfDouble[13];
    double d8 = paramArrayOfDouble[14];
    double d9 = paramArrayOfDouble[15];
    double d28 = d43 * d5;
    double d29 = d41 * d4;
    double d37 = d41 * d3;
    double d38 = d25 * d5;
    double d10 = d43 * d3;
    double d11 = d25 * d4;
    double d12 = (d28 * d7 - d29 * d7 + d37 * d8 - d38 * d8 - d10 * d9 + d11 * d9) / d1;
    double d33 = d36 * d4;
    double d44 = d42 * d5;
    double d31 = d36 * d3;
    double d32 = d26 * d5;
    double d13 = d42 * d3;
    double d14 = d26 * d4;
    double d15 = (d33 * d7 - d44 * d7 - d31 * d8 + d32 * d8 + d13 * d9 - d14 * d9) / d1;
    double d45 = d42 * d41;
    double d46 = d36 * d43;
    double d16 = d36 * d25;
    double d17 = d26 * d41;
    double d18 = d42 * d25;
    double d19 = d26 * d43;
    double d20 = (d45 * d7 - d46 * d7 + d16 * d8 - d17 * d8 - d18 * d9 + d19 * d9) / d1;
    double d21 = (d46 * d3 - d45 * d3 - d16 * d4 + d17 * d4 + d18 * d5 - d19 * d5) / d1;
    double d39 = d41 * d2;
    double d40 = d27 * d5;
    double d22 = d43 * d2;
    double d23 = d27 * d4;
    d28 = (d29 * d6 - d28 * d6 - d39 * d8 + d40 * d8 + d22 * d9 - d23 * d9) / d1;
    double d34 = d36 * d2;
    double d35 = d24 * d5;
    d29 = d42 * d2;
    double d30 = d24 * d4;
    d33 = (d44 * d6 - d33 * d6 + d34 * d8 - d35 * d8 - d29 * d9 + d30 * d9) / d1;
    d36 *= d27;
    d41 *= d24;
    d42 *= d27;
    d43 *= d24;
    d44 = (d46 * d6 - d45 * d6 - d36 * d8 + d41 * d8 + d42 * d9 - d43 * d9) / d1;
    d45 = (d45 * d2 - d46 * d2 + d36 * d4 - d41 * d4 - d42 * d5 + d43 * d5) / d1;
    d46 = d25 * d2;
    double d47 = d27 * d3;
    d37 = (d38 * d6 - d37 * d6 + d39 * d7 - d40 * d7 - d46 * d9 + d47 * d9) / d1;
    d38 = d26 * d2;
    d39 = d24 * d3;
    d31 = (d31 * d6 - d32 * d6 - d34 * d7 + d35 * d7 + d38 * d9 - d39 * d9) / d1;
    d26 *= d27;
    d24 *= d25;
    return new double[] { d12, d15, d20, d21, d28, d33, d44, d45, d37, d31, (d17 * d6 - d16 * d6 + d36 * d7 - d41 * d7 - d26 * d9 + d9 * d24) / d1, (d16 * d2 - d17 * d2 - d36 * d3 + d41 * d3 + d26 * d5 - d5 * d24) / d1, (d10 * d6 - d11 * d6 - d22 * d7 + d23 * d7 + d46 * d8 - d47 * d8) / d1, (d14 * d6 - d13 * d6 + d29 * d7 - d30 * d7 - d38 * d8 + d39 * d8) / d1, (d18 * d6 - d6 * d19 - d42 * d7 + d7 * d43 + d26 * d8 - d8 * d24) / d1, (d19 * d2 - d18 * d2 + d42 * d3 - d43 * d3 - d26 * d4 + d24 * d4) / d1 };
  }
  
  private static boolean isZero(double paramDouble)
  {
    if (Double.isNaN(paramDouble)) {
      return false;
    }
    return Math.abs(paramDouble) < 1.0E-5D;
  }
  
  public static void multiplyInto(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3)
  {
    double d1 = paramArrayOfDouble2[0];
    double d2 = paramArrayOfDouble2[1];
    double d3 = paramArrayOfDouble2[2];
    double d4 = paramArrayOfDouble2[3];
    double d5 = paramArrayOfDouble2[4];
    double d6 = paramArrayOfDouble2[5];
    double d7 = paramArrayOfDouble2[6];
    double d8 = paramArrayOfDouble2[7];
    double d9 = paramArrayOfDouble2[8];
    double d10 = paramArrayOfDouble2[9];
    double d11 = paramArrayOfDouble2[10];
    double d12 = paramArrayOfDouble2[11];
    double d13 = paramArrayOfDouble2[12];
    double d14 = paramArrayOfDouble2[13];
    double d15 = paramArrayOfDouble2[14];
    double d16 = paramArrayOfDouble2[15];
    double d17 = paramArrayOfDouble3[0];
    double d18 = paramArrayOfDouble3[1];
    double d19 = paramArrayOfDouble3[2];
    double d20 = paramArrayOfDouble3[3];
    paramArrayOfDouble1[0] = (d17 * d1 + d18 * d5 + d19 * d9 + d20 * d13);
    paramArrayOfDouble1[1] = (d17 * d2 + d18 * d6 + d19 * d10 + d20 * d14);
    paramArrayOfDouble1[2] = (d17 * d3 + d18 * d7 + d19 * d11 + d20 * d15);
    paramArrayOfDouble1[3] = (d17 * d4 + d18 * d8 + d19 * d12 + d20 * d16);
    d17 = paramArrayOfDouble3[4];
    d18 = paramArrayOfDouble3[5];
    d19 = paramArrayOfDouble3[6];
    d20 = paramArrayOfDouble3[7];
    paramArrayOfDouble1[4] = (d17 * d1 + d18 * d5 + d19 * d9 + d20 * d13);
    paramArrayOfDouble1[5] = (d17 * d2 + d18 * d6 + d19 * d10 + d20 * d14);
    paramArrayOfDouble1[6] = (d17 * d3 + d18 * d7 + d19 * d11 + d20 * d15);
    paramArrayOfDouble1[7] = (d17 * d4 + d18 * d8 + d19 * d12 + d20 * d16);
    d17 = paramArrayOfDouble3[8];
    d18 = paramArrayOfDouble3[9];
    d19 = paramArrayOfDouble3[10];
    d20 = paramArrayOfDouble3[11];
    paramArrayOfDouble1[8] = (d17 * d1 + d18 * d5 + d19 * d9 + d20 * d13);
    paramArrayOfDouble1[9] = (d17 * d2 + d18 * d6 + d19 * d10 + d20 * d14);
    paramArrayOfDouble1[10] = (d17 * d3 + d18 * d7 + d19 * d11 + d20 * d15);
    paramArrayOfDouble1[11] = (d17 * d4 + d18 * d8 + d19 * d12 + d20 * d16);
    d17 = paramArrayOfDouble3[12];
    d18 = paramArrayOfDouble3[13];
    d19 = paramArrayOfDouble3[14];
    d20 = paramArrayOfDouble3[15];
    paramArrayOfDouble1[12] = (d1 * d17 + d5 * d18 + d9 * d19 + d13 * d20);
    paramArrayOfDouble1[13] = (d2 * d17 + d6 * d18 + d10 * d19 + d14 * d20);
    paramArrayOfDouble1[14] = (d3 * d17 + d7 * d18 + d11 * d19 + d15 * d20);
    paramArrayOfDouble1[15] = (d17 * d4 + d18 * d8 + d19 * d12 + d20 * d16);
  }
  
  public static void multiplyVectorByMatrix(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3)
  {
    double d1 = paramArrayOfDouble1[0];
    double d2 = paramArrayOfDouble1[1];
    double d3 = paramArrayOfDouble1[2];
    double d4 = paramArrayOfDouble1[3];
    paramArrayOfDouble3[0] = (paramArrayOfDouble2[0] * d1 + paramArrayOfDouble2[4] * d2 + paramArrayOfDouble2[8] * d3 + paramArrayOfDouble2[12] * d4);
    paramArrayOfDouble3[1] = (paramArrayOfDouble2[1] * d1 + paramArrayOfDouble2[5] * d2 + paramArrayOfDouble2[9] * d3 + paramArrayOfDouble2[13] * d4);
    paramArrayOfDouble3[2] = (paramArrayOfDouble2[2] * d1 + paramArrayOfDouble2[6] * d2 + paramArrayOfDouble2[10] * d3 + paramArrayOfDouble2[14] * d4);
    paramArrayOfDouble3[3] = (d1 * paramArrayOfDouble2[3] + d2 * paramArrayOfDouble2[7] + d3 * paramArrayOfDouble2[11] + d4 * paramArrayOfDouble2[15]);
  }
  
  public static void resetIdentityMatrix(double[] paramArrayOfDouble)
  {
    paramArrayOfDouble[14] = 0L;
    paramArrayOfDouble[13] = 0.0D;
    paramArrayOfDouble[12] = 0.0D;
    paramArrayOfDouble[11] = 0.0D;
    paramArrayOfDouble[9] = 0.0D;
    paramArrayOfDouble[8] = 0.0D;
    paramArrayOfDouble[7] = 0.0D;
    paramArrayOfDouble[6] = 0.0D;
    paramArrayOfDouble[4] = 0.0D;
    paramArrayOfDouble[3] = 0.0D;
    paramArrayOfDouble[2] = 0.0D;
    paramArrayOfDouble[1] = 0.0D;
    paramArrayOfDouble[15] = 4607182418800017408L;
    paramArrayOfDouble[10] = 1.0D;
    paramArrayOfDouble[5] = 1.0D;
    paramArrayOfDouble[0] = 1.0D;
  }
  
  public static double roundTo3Places(double paramDouble)
  {
    return Math.round(paramDouble * 1000.0D) * 0.001D;
  }
  
  public static double[] transpose(double[] paramArrayOfDouble)
  {
    return new double[] { paramArrayOfDouble[0], paramArrayOfDouble[4], paramArrayOfDouble[8], paramArrayOfDouble[12], paramArrayOfDouble[1], paramArrayOfDouble[5], paramArrayOfDouble[9], paramArrayOfDouble[13], paramArrayOfDouble[2], paramArrayOfDouble[6], paramArrayOfDouble[10], paramArrayOfDouble[14], paramArrayOfDouble[3], paramArrayOfDouble[7], paramArrayOfDouble[11], paramArrayOfDouble[15] };
  }
  
  public static double[] v3Combine(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double paramDouble1, double paramDouble2)
  {
    return new double[] { paramArrayOfDouble1[0] * paramDouble1 + paramArrayOfDouble2[0] * paramDouble2, paramArrayOfDouble1[1] * paramDouble1 + paramArrayOfDouble2[1] * paramDouble2, paramDouble1 * paramArrayOfDouble1[2] + paramDouble2 * paramArrayOfDouble2[2] };
  }
  
  public static double[] v3Cross(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    return new double[] { paramArrayOfDouble1[1] * paramArrayOfDouble2[2] - paramArrayOfDouble1[2] * paramArrayOfDouble2[1], paramArrayOfDouble1[2] * paramArrayOfDouble2[0] - paramArrayOfDouble1[0] * paramArrayOfDouble2[2], paramArrayOfDouble1[0] * paramArrayOfDouble2[1] - paramArrayOfDouble1[1] * paramArrayOfDouble2[0] };
  }
  
  public static double v3Dot(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    return paramArrayOfDouble1[0] * paramArrayOfDouble2[0] + paramArrayOfDouble1[1] * paramArrayOfDouble2[1] + paramArrayOfDouble1[2] * paramArrayOfDouble2[2];
  }
  
  public static double v3Length(double[] paramArrayOfDouble)
  {
    return Math.sqrt(paramArrayOfDouble[0] * paramArrayOfDouble[0] + paramArrayOfDouble[1] * paramArrayOfDouble[1] + paramArrayOfDouble[2] * paramArrayOfDouble[2]);
  }
  
  public static double[] v3Normalize(double[] paramArrayOfDouble, double paramDouble)
  {
    double d = paramDouble;
    if (isZero(paramDouble)) {
      d = v3Length(paramArrayOfDouble);
    }
    paramDouble = 1.0D / d;
    return new double[] { paramArrayOfDouble[0] * paramDouble, paramArrayOfDouble[1] * paramDouble, paramArrayOfDouble[2] * paramDouble };
  }
  
  public static class MatrixDecompositionContext
  {
    double[] perspective = new double[4];
    double[] rotationDegrees = new double[3];
    double[] scale = new double[3];
    double[] skew = new double[3];
    double[] translation = new double[3];
    
    public MatrixDecompositionContext() {}
    
    private static void resetArray(double[] paramArrayOfDouble)
    {
      int i = 0;
      while (i < paramArrayOfDouble.length)
      {
        paramArrayOfDouble[i] = 0.0D;
        i += 1;
      }
    }
    
    public void reset()
    {
      resetArray(perspective);
      resetArray(scale);
      resetArray(skew);
      resetArray(translation);
      resetArray(rotationDegrees);
    }
  }
}
