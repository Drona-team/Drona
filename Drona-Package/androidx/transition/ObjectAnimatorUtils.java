package androidx.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.os.Build.VERSION;
import android.util.Property;

class ObjectAnimatorUtils
{
  private ObjectAnimatorUtils() {}
  
  static ObjectAnimator ofPointF(Object paramObject, Property paramProperty, Path paramPath)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return ObjectAnimator.ofObject(paramObject, paramProperty, null, paramPath);
    }
    return ObjectAnimator.ofFloat(paramObject, new PathProperty(paramProperty, paramPath), new float[] { 0.0F, 1.0F });
  }
}
