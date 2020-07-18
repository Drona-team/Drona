package client.testing.delay;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import client.testing.R.attr;
import client.testing.R.styleable;

public class SoundLevelButton
  extends MaskedColorView
{
  private static final String PAGE_KEY = "ai.api.ui.SoundLevelButton";
  protected static final int[] STATE_LISTENING = { R.attr.state_listening };
  private final SoundLevelCircleDrawable backgroundDrawable;
  protected boolean listening = false;
  
  public SoundLevelButton(Context paramContext)
  {
    super(paramContext);
    backgroundDrawable = new SoundLevelCircleDrawable(getParams(paramContext, null));
    setCircleBackground(backgroundDrawable);
    init();
  }
  
  public SoundLevelButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    backgroundDrawable = new SoundLevelCircleDrawable(getParams(paramContext, paramAttributeSet));
    setCircleBackground(backgroundDrawable);
    init();
  }
  
  public SoundLevelButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    backgroundDrawable = new SoundLevelCircleDrawable(getParams(paramContext, paramAttributeSet));
    setCircleBackground(backgroundDrawable);
    init();
  }
  
  private SoundLevelCircleDrawable.Params getParams(Context paramContext, AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet != null)
    {
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SoundLevelButton);
      try
      {
        listening = paramContext.getBoolean(R.styleable.SoundLevelButton_state_listening, false);
        paramAttributeSet = new SoundLevelCircleDrawable.Params(paramContext.getDimension(R.styleable.SoundLevelButton_maxRadius, -1.0F), paramContext.getDimension(R.styleable.SoundLevelButton_minRadius, -1.0F), paramContext.getDimension(R.styleable.SoundLevelButton_circleCenterX, -1.0F), paramContext.getDimension(R.styleable.SoundLevelButton_circleCenterY, -1.0F), paramContext.getColor(R.styleable.SoundLevelButton_centerColor, -889815), paramContext.getColor(R.styleable.SoundLevelButton_haloColor, SoundLevelCircleDrawable.HALO_COLOR_DEF));
        paramContext.recycle();
        return paramAttributeSet;
      }
      catch (Throwable paramAttributeSet)
      {
        paramContext.recycle();
        throw paramAttributeSet;
      }
    }
    return null;
  }
  
  private void init()
  {
    setOnClickListener(new SoundLevelButton.1(this));
  }
  
  private void setCircleBackground(SoundLevelCircleDrawable paramSoundLevelCircleDrawable)
  {
    if (Build.VERSION.SDK_INT < 16)
    {
      setBackgroundDrawable(paramSoundLevelCircleDrawable);
      return;
    }
    setBackground(paramSoundLevelCircleDrawable);
  }
  
  protected String getDebugState()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.getDebugState());
    localStringBuilder.append("\ndrawSL: ");
    localStringBuilder.append(listening);
    return localStringBuilder.toString();
  }
  
  protected float getMinRadius()
  {
    return backgroundDrawable.getMinRadius();
  }
  
  protected void onClick(View paramView) {}
  
  public int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (listening) {
      View.mergeDrawableStates(arrayOfInt, STATE_LISTENING);
    }
    return arrayOfInt;
  }
  
  protected void setDrawCenter(boolean paramBoolean)
  {
    backgroundDrawable.setDrawCenter(paramBoolean);
  }
  
  public void setDrawSoundLevel(boolean paramBoolean)
  {
    listening = paramBoolean;
    backgroundDrawable.setDrawSoundLevel(paramBoolean);
    refreshDrawableState();
    postInvalidate();
  }
  
  public void setSoundLevel(float paramFloat)
  {
    backgroundDrawable.setSoundLevel(paramFloat);
    postInvalidate();
  }
}
