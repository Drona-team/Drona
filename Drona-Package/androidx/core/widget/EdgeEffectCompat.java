package androidx.core.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.widget.EdgeEffect;

public final class EdgeEffectCompat
{
  private EdgeEffect mEdgeEffect;
  
  public EdgeEffectCompat(Context paramContext)
  {
    mEdgeEffect = new EdgeEffect(paramContext);
  }
  
  public static void onPull(EdgeEffect paramEdgeEffect, float paramFloat1, float paramFloat2)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramEdgeEffect.onPull(paramFloat1, paramFloat2);
      return;
    }
    paramEdgeEffect.onPull(paramFloat1);
  }
  
  public boolean draw(Canvas paramCanvas)
  {
    return mEdgeEffect.draw(paramCanvas);
  }
  
  public void finish()
  {
    mEdgeEffect.finish();
  }
  
  public boolean isFinished()
  {
    return mEdgeEffect.isFinished();
  }
  
  public boolean onAbsorb(int paramInt)
  {
    mEdgeEffect.onAbsorb(paramInt);
    return true;
  }
  
  public boolean onPull(float paramFloat)
  {
    mEdgeEffect.onPull(paramFloat);
    return true;
  }
  
  public boolean onPull(float paramFloat1, float paramFloat2)
  {
    onPull(mEdgeEffect, paramFloat1, paramFloat2);
    return true;
  }
  
  public boolean onRelease()
  {
    mEdgeEffect.onRelease();
    return mEdgeEffect.isFinished();
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    mEdgeEffect.setSize(paramInt1, paramInt2);
  }
}
