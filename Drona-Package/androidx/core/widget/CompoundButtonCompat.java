package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.Log;
import android.widget.CompoundButton;
import java.lang.reflect.Field;

public final class CompoundButtonCompat
{
  private static final String TAG = "CompoundButtonCompat";
  private static Field sButtonDrawableField;
  private static boolean sButtonDrawableFieldFetched;
  
  private CompoundButtonCompat() {}
  
  public static Drawable getButtonDrawable(CompoundButton paramCompoundButton)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return paramCompoundButton.getButtonDrawable();
    }
    if (!sButtonDrawableFieldFetched)
    {
      try
      {
        Field localField1 = CompoundButton.class.getDeclaredField("mButtonDrawable");
        sButtonDrawableField = localField1;
        localField1 = sButtonDrawableField;
        localField1.setAccessible(true);
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        Log.i("CompoundButtonCompat", "Failed to retrieve mButtonDrawable field", localNoSuchFieldException);
      }
      sButtonDrawableFieldFetched = true;
    }
    if (sButtonDrawableField != null)
    {
      Field localField2 = sButtonDrawableField;
      try
      {
        paramCompoundButton = localField2.get(paramCompoundButton);
        return (Drawable)paramCompoundButton;
      }
      catch (IllegalAccessException paramCompoundButton)
      {
        Log.i("CompoundButtonCompat", "Failed to get button drawable via reflection", paramCompoundButton);
        sButtonDrawableField = null;
      }
    }
    return null;
  }
  
  public static ColorStateList getButtonTintList(CompoundButton paramCompoundButton)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramCompoundButton.getButtonTintList();
    }
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      return ((TintableCompoundButton)paramCompoundButton).getSupportButtonTintList();
    }
    return null;
  }
  
  public static PorterDuff.Mode getButtonTintMode(CompoundButton paramCompoundButton)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return paramCompoundButton.getButtonTintMode();
    }
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      return ((TintableCompoundButton)paramCompoundButton).getSupportButtonTintMode();
    }
    return null;
  }
  
  public static void setButtonTintList(CompoundButton paramCompoundButton, ColorStateList paramColorStateList)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramCompoundButton.setButtonTintList(paramColorStateList);
      return;
    }
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      ((TintableCompoundButton)paramCompoundButton).setSupportButtonTintList(paramColorStateList);
    }
  }
  
  public static void setButtonTintMode(CompoundButton paramCompoundButton, PorterDuff.Mode paramMode)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramCompoundButton.setButtonTintMode(paramMode);
      return;
    }
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      ((TintableCompoundButton)paramCompoundButton).setSupportButtonTintMode(paramMode);
    }
  }
}
