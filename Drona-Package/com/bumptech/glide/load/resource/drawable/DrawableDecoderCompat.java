package com.bumptech.glide.load.resource.drawable;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import androidx.appcompat.content.wiki.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.delay.ResourcesCompat;

public final class DrawableDecoderCompat
{
  private static volatile boolean shouldCallAppCompatResources;
  
  private DrawableDecoderCompat() {}
  
  public static Drawable getDrawable(Context paramContext, int paramInt, Resources.Theme paramTheme)
  {
    return getDrawable(paramContext, paramContext, paramInt, paramTheme);
  }
  
  public static Drawable getDrawable(Context paramContext1, Context paramContext2, int paramInt)
  {
    return getDrawable(paramContext1, paramContext2, paramInt, null);
  }
  
  /* Error */
  private static Drawable getDrawable(Context paramContext1, Context paramContext2, int paramInt, Resources.Theme paramTheme)
  {
    // Byte code:
    //   0: getstatic 26	com/bumptech/glide/load/resource/drawable/DrawableDecoderCompat:shouldCallAppCompatResources	Z
    //   3: istore 4
    //   5: iload 4
    //   7: ifeq +44 -> 51
    //   10: aload_1
    //   11: iload_2
    //   12: aload_3
    //   13: invokestatic 29	com/bumptech/glide/load/resource/drawable/DrawableDecoderCompat:loadDrawableV7	(Landroid/content/Context;ILandroid/content/res/Resources$Theme;)Landroid/graphics/drawable/Drawable;
    //   16: astore 5
    //   18: aload 5
    //   20: areturn
    //   21: goto +30 -> 51
    //   24: astore_3
    //   25: aload_0
    //   26: invokevirtual 35	android/content/Context:getPackageName	()Ljava/lang/String;
    //   29: aload_1
    //   30: invokevirtual 35	android/content/Context:getPackageName	()Ljava/lang/String;
    //   33: invokevirtual 41	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   36: ifne +9 -> 45
    //   39: aload_1
    //   40: iload_2
    //   41: invokestatic 46	androidx/core/content/ContextCompat:getDrawable	(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;
    //   44: areturn
    //   45: aload_3
    //   46: athrow
    //   47: iconst_0
    //   48: putstatic 26	com/bumptech/glide/load/resource/drawable/DrawableDecoderCompat:shouldCallAppCompatResources	Z
    //   51: aload_3
    //   52: ifnull +6 -> 58
    //   55: goto +8 -> 63
    //   58: aload_1
    //   59: invokevirtual 50	android/content/Context:getTheme	()Landroid/content/res/Resources$Theme;
    //   62: astore_3
    //   63: aload_1
    //   64: iload_2
    //   65: aload_3
    //   66: invokestatic 53	com/bumptech/glide/load/resource/drawable/DrawableDecoderCompat:loadDrawableV4	(Landroid/content/Context;ILandroid/content/res/Resources$Theme;)Landroid/graphics/drawable/Drawable;
    //   69: areturn
    //   70: astore_0
    //   71: goto -24 -> 47
    //   74: astore_0
    //   75: goto -54 -> 21
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	78	0	paramContext1	Context
    //   0	78	1	paramContext2	Context
    //   0	78	2	paramInt	int
    //   0	78	3	paramTheme	Resources.Theme
    //   3	3	4	bool	boolean
    //   16	3	5	localDrawable	Drawable
    // Exception table:
    //   from	to	target	type
    //   10	18	24	java/lang/IllegalStateException
    //   0	5	70	java/lang/NoClassDefFoundError
    //   10	18	70	java/lang/NoClassDefFoundError
    //   10	18	74	android/content/res/Resources$NotFoundException
  }
  
  private static Drawable loadDrawableV4(Context paramContext, int paramInt, Resources.Theme paramTheme)
  {
    return ResourcesCompat.getDrawable(paramContext.getResources(), paramInt, paramTheme);
  }
  
  private static Drawable loadDrawableV7(Context paramContext, int paramInt, Resources.Theme paramTheme)
  {
    Object localObject = paramContext;
    if (paramTheme != null) {
      localObject = new ContextThemeWrapper(paramContext, paramTheme);
    }
    return AppCompatResources.getDrawable((Context)localObject, paramInt);
  }
}
