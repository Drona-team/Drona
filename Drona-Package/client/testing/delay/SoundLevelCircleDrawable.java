package client.testing.delay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class SoundLevelCircleDrawable
  extends Drawable
{
  public static final int CENTER_COLOR_DEF = -889815;
  public static final int HALO_COLOR_DEF = Color.argb(16, 0, 0, 0);
  private static final float INITIAL_VALUE = 2.5F;
  private static final float MAX_VALUE = 10.0F;
  private static final float MIN_VALUE = 0.5F;
  private final Rect bounds = new Rect();
  private final float circleCenterX;
  private final float circleCenterY;
  private boolean drawCenter = false;
  private boolean drawSoundLevel = false;
  private float maxMicLevel = 10.0F;
  private final float maxRadius;
  private float minMicLevel = 0.5F;
  private final float minRadius;
  private final Paint paintIndicatorCenter;
  private final Paint paintIndicatorHalo;
  private float smoothedLevel = 2.5F;
  
  public SoundLevelCircleDrawable()
  {
    this(null);
  }
  
  public SoundLevelCircleDrawable(Params paramParams)
  {
    int i;
    int j;
    if (paramParams != null)
    {
      maxRadius = maxRadius;
      minRadius = minRadius;
      circleCenterX = circleCenterX;
      circleCenterY = circleCenterY;
      i = centerColor;
      j = haloColor;
    }
    else
    {
      maxRadius = -1.0F;
      minRadius = -1.0F;
      circleCenterX = -1.0F;
      circleCenterY = -1.0F;
      i = -889815;
      j = HALO_COLOR_DEF;
    }
    paintIndicatorHalo = newColorPaint(j);
    paintIndicatorCenter = newColorPaint(i);
  }
  
  private static Paint newColorPaint(int paramInt)
  {
    Paint localPaint = new Paint();
    localPaint.setStyle(Paint.Style.FILL);
    localPaint.setAntiAlias(true);
    localPaint.setColor(paramInt);
    return localPaint;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if ((drawSoundLevel) || (drawCenter))
    {
      paramCanvas.save();
      try
      {
        float f1 = maxRadius;
        if (f1 >= 0.0F)
        {
          f1 = circleCenterX;
          if (f1 >= 0.0F)
          {
            f1 = circleCenterY;
            if (f1 >= 0.0F) {
              break label61;
            }
          }
        }
        paramCanvas.getClipBounds(bounds);
        label61:
        paramCanvas.drawColor(0);
        f1 = smoothedLevel;
        float f2 = minMicLevel;
        float f3 = maxMicLevel;
        float f4 = minMicLevel;
        float f5 = (f1 - f2) / (f3 - f4);
        f1 = maxRadius;
        if (f1 < 0.0F)
        {
          f1 = bounds.width();
          f1 /= 2.0F;
        }
        else
        {
          f1 = maxRadius;
        }
        f2 = minRadius;
        if (f2 < 0.0F) {
          f2 = 0.5777778F * f1;
        } else {
          f2 = minRadius;
        }
        float f6 = 0.8F * f2;
        f3 = circleCenterX;
        if (f3 < 0.0F)
        {
          f3 = bounds.width();
          f3 /= 2.0F;
        }
        else
        {
          f3 = circleCenterX;
        }
        f4 = circleCenterY;
        if (f4 < 0.0F)
        {
          f4 = bounds.height();
          f4 /= 2.0F;
        }
        else
        {
          f4 = circleCenterY;
        }
        boolean bool = drawSoundLevel;
        if (bool) {
          paramCanvas.drawCircle(f3, f4, f6 + (f1 - f6) * f5, paintIndicatorHalo);
        }
        bool = drawCenter;
        if (!bool)
        {
          bool = drawSoundLevel;
          if (!bool) {}
        }
        else
        {
          paramCanvas.drawCircle(f3, f4, f2, paintIndicatorCenter);
        }
        paramCanvas.restore();
        return;
      }
      catch (Throwable localThrowable)
      {
        paramCanvas.restore();
        throw localThrowable;
      }
    }
  }
  
  public float getMinRadius()
  {
    return minRadius;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setDrawCenter(boolean paramBoolean)
  {
    drawCenter = paramBoolean;
  }
  
  public boolean setDrawSoundLevel(boolean paramBoolean)
  {
    if (drawSoundLevel != paramBoolean)
    {
      drawSoundLevel = paramBoolean;
      if (paramBoolean)
      {
        minMicLevel = 0.5F;
        maxMicLevel = 10.0F;
        smoothedLevel = 2.5F;
      }
      return true;
    }
    return false;
  }
  
  public void setSoundLevel(float paramFloat)
  {
    paramFloat = Math.abs(paramFloat);
    if (paramFloat < minMicLevel) {
      minMicLevel = ((minMicLevel + paramFloat) / 2.0F);
    }
    if (paramFloat > maxMicLevel) {
      maxMicLevel = ((maxMicLevel + paramFloat) / 2.0F);
    }
    smoothedLevel = (smoothedLevel * 0.8F + paramFloat * 0.2F);
    if (smoothedLevel > maxMicLevel)
    {
      smoothedLevel = maxMicLevel;
      return;
    }
    if (smoothedLevel < minMicLevel) {
      smoothedLevel = minMicLevel;
    }
  }
  
  public class Params
  {
    private final int centerColor;
    public final float circleCenterX;
    public final float circleCenterY;
    private final int haloColor;
    public final float maxRadius;
    public final float minRadius;
    
    public Params(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt1, int paramInt2)
    {
      maxRadius = this$1;
      minRadius = paramFloat1;
      circleCenterX = paramFloat2;
      circleCenterY = paramFloat3;
      centerColor = paramInt1;
      haloColor = paramInt2;
    }
  }
}
