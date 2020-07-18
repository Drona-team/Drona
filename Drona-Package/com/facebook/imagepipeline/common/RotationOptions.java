package com.facebook.imagepipeline.common;

import com.facebook.common.util.HashCodeUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RotationOptions
{
  private static final int DISABLE_ROTATION = -2;
  public static final int NO_ROTATION = 0;
  public static final int ROTATE_180 = 180;
  public static final int ROTATE_270 = 270;
  public static final int ROTATE_90 = 90;
  private static final RotationOptions ROTATION_OPTIONS_AUTO_ROTATE = new RotationOptions(-1, false);
  private static final RotationOptions ROTATION_OPTIONS_DISABLE_ROTATION = new RotationOptions(-2, false);
  private static final RotationOptions ROTATION_OPTIONS_ROTATE_AT_RENDER_TIME = new RotationOptions(-1, true);
  private static final int USE_EXIF_ROTATION_ANGLE = -1;
  private final boolean mDeferUntilRendered;
  private final int mRotation;
  
  private RotationOptions(int paramInt, boolean paramBoolean)
  {
    mRotation = paramInt;
    mDeferUntilRendered = paramBoolean;
  }
  
  public static RotationOptions autoRotate()
  {
    return ROTATION_OPTIONS_AUTO_ROTATE;
  }
  
  public static RotationOptions autoRotateAtRenderTime()
  {
    return ROTATION_OPTIONS_ROTATE_AT_RENDER_TIME;
  }
  
  public static RotationOptions disableRotation()
  {
    return ROTATION_OPTIONS_DISABLE_ROTATION;
  }
  
  public static RotationOptions forceRotation(int paramInt)
  {
    return new RotationOptions(paramInt, false);
  }
  
  public boolean canDeferUntilRendered()
  {
    return mDeferUntilRendered;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof RotationOptions)) {
      return false;
    }
    paramObject = (RotationOptions)paramObject;
    return (mRotation == mRotation) && (mDeferUntilRendered == mDeferUntilRendered);
  }
  
  public int getForcedAngle()
  {
    if (!useImageMetadata()) {
      return mRotation;
    }
    throw new IllegalStateException("Rotation is set to use EXIF");
  }
  
  public int hashCode()
  {
    return HashCodeUtil.hashCode(Integer.valueOf(mRotation), Boolean.valueOf(mDeferUntilRendered));
  }
  
  public boolean rotationEnabled()
  {
    return mRotation != -2;
  }
  
  public String toString()
  {
    return String.format(null, "%d defer:%b", new Object[] { Integer.valueOf(mRotation), Boolean.valueOf(mDeferUntilRendered) });
  }
  
  public boolean useImageMetadata()
  {
    return mRotation == -1;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface RotationAngle {}
}