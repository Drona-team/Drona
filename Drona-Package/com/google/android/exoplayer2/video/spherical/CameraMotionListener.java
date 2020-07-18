package com.google.android.exoplayer2.video.spherical;

public abstract interface CameraMotionListener
{
  public abstract void onCameraMotion(long paramLong, float[] paramArrayOfFloat);
  
  public abstract void onCameraMotionReset();
}
