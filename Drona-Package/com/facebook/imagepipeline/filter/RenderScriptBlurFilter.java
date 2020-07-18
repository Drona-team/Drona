package com.facebook.imagepipeline.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.facebook.common.internal.Preconditions;

public abstract class RenderScriptBlurFilter
{
  public static final int BLUR_MAX_RADIUS = 25;
  
  public RenderScriptBlurFilter() {}
  
  public static void blurBitmap(Bitmap paramBitmap1, Bitmap paramBitmap2, Context paramContext, int paramInt)
  {
    Preconditions.checkNotNull(paramBitmap1);
    Preconditions.checkNotNull(paramBitmap2);
    Preconditions.checkNotNull(paramContext);
    boolean bool;
    if ((paramInt > 0) && (paramInt <= 25)) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    try
    {
      RenderScript localRenderScript = RenderScript.create(paramContext);
      paramContext = localRenderScript;
      try
      {
        ScriptIntrinsicBlur localScriptIntrinsicBlur = ScriptIntrinsicBlur.create(localRenderScript, Element.U8_4(localRenderScript));
        paramBitmap2 = Allocation.createFromBitmap(localRenderScript, paramBitmap2);
        Allocation localAllocation = Allocation.createFromBitmap(localRenderScript, paramBitmap1);
        localScriptIntrinsicBlur.setRadius(paramInt);
        localScriptIntrinsicBlur.setInput(paramBitmap2);
        localScriptIntrinsicBlur.forEach(localAllocation);
        localAllocation.copyTo(paramBitmap1);
        if (localRenderScript == null) {
          return;
        }
        localRenderScript.destroy();
        return;
      }
      catch (Throwable paramBitmap1) {}
      if (paramContext == null) {
        break label127;
      }
    }
    catch (Throwable paramBitmap1)
    {
      paramContext = null;
    }
    paramContext.destroy();
    label127:
    throw paramBitmap1;
  }
  
  public static boolean canUseRenderScript()
  {
    return Build.VERSION.SDK_INT >= 17;
  }
}
