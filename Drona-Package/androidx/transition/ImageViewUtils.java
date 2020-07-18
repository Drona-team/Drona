package androidx.transition;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.ImageView;
import java.lang.reflect.Field;

class ImageViewUtils
{
  private static Field sDrawMatrixField;
  private static boolean sDrawMatrixFieldFetched;
  private static boolean sTryHiddenAnimateTransform;
  
  private ImageViewUtils() {}
  
  static void animateTransform(ImageView paramImageView, Matrix paramMatrix)
  {
    if (Build.VERSION.SDK_INT >= 29)
    {
      paramImageView.animateTransform(paramMatrix);
      return;
    }
    Object localObject3;
    if (paramMatrix == null)
    {
      paramMatrix = paramImageView.getDrawable();
      if (paramMatrix != null)
      {
        paramMatrix.setBounds(0, 0, paramImageView.getWidth() - paramImageView.getPaddingLeft() - paramImageView.getPaddingRight(), paramImageView.getHeight() - paramImageView.getPaddingTop() - paramImageView.getPaddingBottom());
        paramImageView.invalidate();
      }
    }
    else
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        hiddenAnimateTransform(paramImageView, paramMatrix);
        return;
      }
      Object localObject1 = paramImageView.getDrawable();
      if (localObject1 != null)
      {
        ((Drawable)localObject1).setBounds(0, 0, ((Drawable)localObject1).getIntrinsicWidth(), ((Drawable)localObject1).getIntrinsicHeight());
        localObject3 = null;
        fetchDrawMatrixField();
        localObject1 = localObject3;
        if (sDrawMatrixField != null) {
          localObject1 = sDrawMatrixField;
        }
        for (;;)
        {
          try
          {
            localObject1 = ((Field)localObject1).get(paramImageView);
            localObject3 = (Matrix)localObject1;
            if (localObject3 != null) {}
          }
          catch (IllegalAccessException localIllegalAccessException1)
          {
            Object localObject2 = localObject3;
            continue;
          }
          try
          {
            localObject1 = new Matrix();
            localObject3 = sDrawMatrixField;
          }
          catch (IllegalAccessException localIllegalAccessException2)
          {
            continue;
          }
          try
          {
            ((Field)localObject3).set(paramImageView, localObject1);
          }
          catch (IllegalAccessException localIllegalAccessException3) {}
        }
        localObject1 = localObject3;
        if (localObject1 != null) {
          ((Matrix)localObject1).set(paramMatrix);
        }
        paramImageView.invalidate();
        return;
      }
    }
  }
  
  private static void fetchDrawMatrixField()
  {
    if (!sDrawMatrixFieldFetched)
    {
      try
      {
        Field localField = ImageView.class.getDeclaredField("mDrawMatrix");
        sDrawMatrixField = localField;
        localField = sDrawMatrixField;
        localField.setAccessible(true);
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;) {}
      }
      sDrawMatrixFieldFetched = true;
      return;
    }
  }
  
  private static void hiddenAnimateTransform(ImageView paramImageView, Matrix paramMatrix)
  {
    if (sTryHiddenAnimateTransform)
    {
      try
      {
        paramImageView.animateTransform(paramMatrix);
        return;
      }
      catch (NoSuchMethodError paramImageView)
      {
        for (;;) {}
      }
      sTryHiddenAnimateTransform = false;
      return;
    }
  }
}
