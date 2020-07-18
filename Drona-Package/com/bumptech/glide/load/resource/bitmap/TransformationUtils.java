package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Build.VERSION;
import android.util.Log;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class TransformationUtils
{
  private static final Lock BITMAP_DRAWABLE_LOCK;
  private static final Paint CIRCLE_CROP_BITMAP_PAINT;
  private static final int CIRCLE_CROP_PAINT_FLAGS = 7;
  private static final Paint CIRCLE_CROP_SHAPE_PAINT;
  private static final Paint DEFAULT_PAINT;
  private static final Set<String> MODELS_REQUIRING_BITMAP_LOCK;
  public static final int PAINT_FLAGS = 6;
  private static final String TAG = "TransformationUtils";
  
  static
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a1 = a0\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private TransformationUtils() {}
  
  private static void applyMatrix(Bitmap paramBitmap1, Bitmap paramBitmap2, Matrix paramMatrix)
  {
    BITMAP_DRAWABLE_LOCK.lock();
    try
    {
      paramBitmap2 = new Canvas(paramBitmap2);
      paramBitmap2.drawBitmap(paramBitmap1, paramMatrix, DEFAULT_PAINT);
      clear(paramBitmap2);
      BITMAP_DRAWABLE_LOCK.unlock();
      return;
    }
    catch (Throwable paramBitmap1)
    {
      BITMAP_DRAWABLE_LOCK.unlock();
      throw paramBitmap1;
    }
  }
  
  public static Bitmap centerCrop(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if ((paramBitmap.getWidth() == paramInt1) && (paramBitmap.getHeight() == paramInt2)) {
      return paramBitmap;
    }
    Matrix localMatrix = new Matrix();
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    float f1 = 0.0F;
    float f2;
    float f3;
    if (i * paramInt2 > j * paramInt1)
    {
      f2 = paramInt2 / paramBitmap.getHeight();
      f3 = (paramInt1 - paramBitmap.getWidth() * f2) * 0.5F;
    }
    else
    {
      f2 = paramInt1 / paramBitmap.getWidth();
      f1 = (paramInt2 - paramBitmap.getHeight() * f2) * 0.5F;
      f3 = 0.0F;
    }
    localMatrix.setScale(f2, f2);
    localMatrix.postTranslate((int)(f3 + 0.5F), (int)(f1 + 0.5F));
    paramBitmapPool = paramBitmapPool.get(paramInt1, paramInt2, getNonNullConfig(paramBitmap));
    setAlpha(paramBitmap, paramBitmapPool);
    applyMatrix(paramBitmap, paramBitmapPool, localMatrix);
    return paramBitmapPool;
  }
  
  public static Bitmap centerInside(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if ((paramBitmap.getWidth() <= paramInt1) && (paramBitmap.getHeight() <= paramInt2))
    {
      paramBitmapPool = paramBitmap;
      if (Log.isLoggable("TransformationUtils", 2))
      {
        Log.v("TransformationUtils", "requested target size larger or equal to input, returning input");
        return paramBitmap;
      }
    }
    else
    {
      if (Log.isLoggable("TransformationUtils", 2)) {
        Log.v("TransformationUtils", "requested target size too big for input, fit centering instead");
      }
      paramBitmapPool = fitCenter(paramBitmapPool, paramBitmap, paramInt1, paramInt2);
    }
    return paramBitmapPool;
  }
  
  public static Bitmap circleCrop(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    paramInt1 = Math.min(paramInt1, paramInt2);
    float f2 = paramInt1;
    float f1 = f2 / 2.0F;
    paramInt2 = paramBitmap.getWidth();
    int i = paramBitmap.getHeight();
    float f3 = paramInt2;
    float f5 = f2 / f3;
    float f4 = i;
    f5 = Math.max(f5, f2 / f4);
    f3 *= f5;
    f4 = f5 * f4;
    f5 = (f2 - f3) / 2.0F;
    f2 = (f2 - f4) / 2.0F;
    RectF localRectF = new RectF(f5, f2, f3 + f5, f4 + f2);
    Bitmap localBitmap1 = getAlphaSafeBitmap(paramBitmapPool, paramBitmap);
    Bitmap localBitmap2 = paramBitmapPool.get(paramInt1, paramInt1, getAlphaSafeConfig(paramBitmap));
    localBitmap2.setHasAlpha(true);
    BITMAP_DRAWABLE_LOCK.lock();
    try
    {
      Canvas localCanvas = new Canvas(localBitmap2);
      localCanvas.drawCircle(f1, f1, f1, CIRCLE_CROP_SHAPE_PAINT);
      localCanvas.drawBitmap(localBitmap1, null, localRectF, CIRCLE_CROP_BITMAP_PAINT);
      clear(localCanvas);
      BITMAP_DRAWABLE_LOCK.unlock();
      if (!localBitmap1.equals(paramBitmap))
      {
        paramBitmapPool.put(localBitmap1);
        return localBitmap2;
      }
    }
    catch (Throwable paramBitmapPool)
    {
      BITMAP_DRAWABLE_LOCK.unlock();
      throw paramBitmapPool;
    }
    return localBitmap2;
  }
  
  private static void clear(Canvas paramCanvas)
  {
    paramCanvas.setBitmap(null);
  }
  
  public static Bitmap fitCenter(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    if ((paramBitmap.getWidth() == paramInt1) && (paramBitmap.getHeight() == paramInt2))
    {
      if (Log.isLoggable("TransformationUtils", 2))
      {
        Log.v("TransformationUtils", "requested target size matches input, returning input");
        return paramBitmap;
      }
    }
    else
    {
      float f = Math.min(paramInt1 / paramBitmap.getWidth(), paramInt2 / paramBitmap.getHeight());
      int i = Math.round(paramBitmap.getWidth() * f);
      int j = Math.round(paramBitmap.getHeight() * f);
      if ((paramBitmap.getWidth() == i) && (paramBitmap.getHeight() == j))
      {
        if (Log.isLoggable("TransformationUtils", 2))
        {
          Log.v("TransformationUtils", "adjusted target size matches input, returning input");
          return paramBitmap;
        }
      }
      else
      {
        paramBitmapPool = paramBitmapPool.get((int)(paramBitmap.getWidth() * f), (int)(paramBitmap.getHeight() * f), getNonNullConfig(paramBitmap));
        setAlpha(paramBitmap, paramBitmapPool);
        if (Log.isLoggable("TransformationUtils", 2))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("request: ");
          ((StringBuilder)localObject).append(paramInt1);
          ((StringBuilder)localObject).append("x");
          ((StringBuilder)localObject).append(paramInt2);
          Log.v("TransformationUtils", ((StringBuilder)localObject).toString());
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("toFit:   ");
          ((StringBuilder)localObject).append(paramBitmap.getWidth());
          ((StringBuilder)localObject).append("x");
          ((StringBuilder)localObject).append(paramBitmap.getHeight());
          Log.v("TransformationUtils", ((StringBuilder)localObject).toString());
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("toReuse: ");
          ((StringBuilder)localObject).append(paramBitmapPool.getWidth());
          ((StringBuilder)localObject).append("x");
          ((StringBuilder)localObject).append(paramBitmapPool.getHeight());
          Log.v("TransformationUtils", ((StringBuilder)localObject).toString());
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("minPct:   ");
          ((StringBuilder)localObject).append(f);
          Log.v("TransformationUtils", ((StringBuilder)localObject).toString());
        }
        Object localObject = new Matrix();
        ((Matrix)localObject).setScale(f, f);
        applyMatrix(paramBitmap, paramBitmapPool, (Matrix)localObject);
        return paramBitmapPool;
      }
    }
    return paramBitmap;
  }
  
  private static Bitmap getAlphaSafeBitmap(BitmapPool paramBitmapPool, Bitmap paramBitmap)
  {
    Bitmap.Config localConfig = getAlphaSafeConfig(paramBitmap);
    if (localConfig.equals(paramBitmap.getConfig())) {
      return paramBitmap;
    }
    paramBitmapPool = paramBitmapPool.get(paramBitmap.getWidth(), paramBitmap.getHeight(), localConfig);
    new Canvas(paramBitmapPool).drawBitmap(paramBitmap, 0.0F, 0.0F, null);
    return paramBitmapPool;
  }
  
  private static Bitmap.Config getAlphaSafeConfig(Bitmap paramBitmap)
  {
    if ((Build.VERSION.SDK_INT >= 26) && (Enum.RGBA_F16.equals(paramBitmap.getConfig()))) {
      return Enum.RGBA_F16;
    }
    return Bitmap.Config.ARGB_8888;
  }
  
  public static Lock getBitmapDrawableLock()
  {
    return BITMAP_DRAWABLE_LOCK;
  }
  
  public static int getExifOrientationDegrees(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 7: 
    case 8: 
      return 270;
    case 5: 
    case 6: 
      return 90;
    }
    return 180;
  }
  
  private static Bitmap.Config getNonNullConfig(Bitmap paramBitmap)
  {
    if (paramBitmap.getConfig() != null) {
      return paramBitmap.getConfig();
    }
    return Bitmap.Config.ARGB_8888;
  }
  
  static void initializeMatrixForRotation(int paramInt, Matrix paramMatrix)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 8: 
      paramMatrix.setRotate(-90.0F);
      return;
    case 7: 
      paramMatrix.setRotate(-90.0F);
      paramMatrix.postScale(-1.0F, 1.0F);
      return;
    case 6: 
      paramMatrix.setRotate(90.0F);
      return;
    case 5: 
      paramMatrix.setRotate(90.0F);
      paramMatrix.postScale(-1.0F, 1.0F);
      return;
    case 4: 
      paramMatrix.setRotate(180.0F);
      paramMatrix.postScale(-1.0F, 1.0F);
      return;
    case 3: 
      paramMatrix.setRotate(180.0F);
      return;
    }
    paramMatrix.setScale(-1.0F, 1.0F);
  }
  
  public static boolean isExifOrientationRequired(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public static Bitmap rotateImage(Bitmap paramBitmap, int paramInt)
  {
    if (paramInt != 0) {
      try
      {
        Object localObject = new Matrix();
        float f = paramInt;
        ((Matrix)localObject).setRotate(f);
        localObject = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), (Matrix)localObject, true);
        return localObject;
      }
      catch (Exception localException)
      {
        if (Log.isLoggable("TransformationUtils", 6)) {
          Log.e("TransformationUtils", "Exception when trying to orient image", localException);
        }
      }
    }
    return paramBitmap;
  }
  
  public static Bitmap rotateImageExif(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt)
  {
    if (!isExifOrientationRequired(paramInt)) {
      return paramBitmap;
    }
    Matrix localMatrix = new Matrix();
    initializeMatrixForRotation(paramInt, localMatrix);
    RectF localRectF = new RectF(0.0F, 0.0F, paramBitmap.getWidth(), paramBitmap.getHeight());
    localMatrix.mapRect(localRectF);
    paramBitmapPool = paramBitmapPool.get(Math.round(localRectF.width()), Math.round(localRectF.height()), getNonNullConfig(paramBitmap));
    localMatrix.postTranslate(-left, -top);
    paramBitmapPool.setHasAlpha(paramBitmap.hasAlpha());
    applyMatrix(paramBitmap, paramBitmapPool, localMatrix);
    return paramBitmapPool;
  }
  
  public static Bitmap roundedCorners(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "roundingRadius must be greater than 0.");
    Object localObject1 = getAlphaSafeConfig(paramBitmap);
    Bitmap localBitmap = getAlphaSafeBitmap(paramBitmapPool, paramBitmap);
    localObject1 = paramBitmapPool.get(localBitmap.getWidth(), localBitmap.getHeight(), (Bitmap.Config)localObject1);
    ((Bitmap)localObject1).setHasAlpha(true);
    Object localObject2 = new BitmapShader(localBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Paint localPaint = new Paint();
    localPaint.setAntiAlias(true);
    localPaint.setShader((Shader)localObject2);
    localObject2 = new RectF(0.0F, 0.0F, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight());
    BITMAP_DRAWABLE_LOCK.lock();
    try
    {
      Canvas localCanvas = new Canvas((Bitmap)localObject1);
      localCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
      float f = paramInt;
      localCanvas.drawRoundRect((RectF)localObject2, f, f, localPaint);
      clear(localCanvas);
      BITMAP_DRAWABLE_LOCK.unlock();
      if (!localBitmap.equals(paramBitmap))
      {
        paramBitmapPool.put(localBitmap);
        return localObject1;
      }
    }
    catch (Throwable paramBitmapPool)
    {
      BITMAP_DRAWABLE_LOCK.unlock();
      throw paramBitmapPool;
    }
    return localObject1;
  }
  
  public static Bitmap roundedCorners(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2, int paramInt3)
  {
    return roundedCorners(paramBitmapPool, paramBitmap, paramInt3);
  }
  
  public static void setAlpha(Bitmap paramBitmap1, Bitmap paramBitmap2)
  {
    paramBitmap2.setHasAlpha(paramBitmap1.hasAlpha());
  }
  
  private static final class NoLock
    implements Lock
  {
    NoLock() {}
    
    public void lock() {}
    
    public void lockInterruptibly()
      throws InterruptedException
    {}
    
    public Condition newCondition()
    {
      throw new UnsupportedOperationException("Should not be called");
    }
    
    public boolean tryLock()
    {
      return true;
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return true;
    }
    
    public void unlock() {}
  }
}
