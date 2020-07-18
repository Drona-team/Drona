package com.google.android.exoplayer2.video.spherical;

import android.opengl.Matrix;
import com.google.android.exoplayer2.util.TimedValueQueue;

public final class FrameRotationQueue
{
  private final float[] recenterMatrix = new float[16];
  private boolean recenterMatrixComputed;
  private final float[] rotationMatrix = new float[16];
  private final TimedValueQueue<float[]> rotations = new TimedValueQueue();
  
  public FrameRotationQueue() {}
  
  private static void computeRecenterMatrix(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    Matrix.setIdentityM(paramArrayOfFloat1, 0);
    float f = (float)Math.sqrt(paramArrayOfFloat2[10] * paramArrayOfFloat2[10] + paramArrayOfFloat2[8] * paramArrayOfFloat2[8]);
    paramArrayOfFloat1[0] = (paramArrayOfFloat2[10] / f);
    paramArrayOfFloat1[2] = (paramArrayOfFloat2[8] / f);
    paramArrayOfFloat1[8] = (-paramArrayOfFloat2[8] / f);
    paramArrayOfFloat2[10] /= f;
  }
  
  private static void getRotationMatrixFromAngleAxis(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float f1 = paramArrayOfFloat2[0];
    float f2 = -paramArrayOfFloat2[1];
    float f3 = -paramArrayOfFloat2[2];
    float f4 = Matrix.length(f1, f2, f3);
    if (f4 != 0.0F)
    {
      Matrix.setRotateM(paramArrayOfFloat1, 0, (float)Math.toDegrees(f4), f1 / f4, f2 / f4, f3 / f4);
      return;
    }
    Matrix.setIdentityM(paramArrayOfFloat1, 0);
  }
  
  public boolean pollRotationMatrix(float[] paramArrayOfFloat, long paramLong)
  {
    float[] arrayOfFloat = (float[])rotations.pollFloor(paramLong);
    if (arrayOfFloat == null) {
      return false;
    }
    getRotationMatrixFromAngleAxis(rotationMatrix, arrayOfFloat);
    if (!recenterMatrixComputed)
    {
      computeRecenterMatrix(recenterMatrix, rotationMatrix);
      recenterMatrixComputed = true;
    }
    Matrix.multiplyMM(paramArrayOfFloat, 0, recenterMatrix, 0, rotationMatrix, 0);
    return true;
  }
  
  public void reset()
  {
    rotations.clear();
    recenterMatrixComputed = false;
  }
  
  public void setRotation(long paramLong, float[] paramArrayOfFloat)
  {
    rotations.concatenate(paramLong, paramArrayOfFloat);
  }
}
