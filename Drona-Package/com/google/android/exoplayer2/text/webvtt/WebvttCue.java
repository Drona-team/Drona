package com.google.android.exoplayer2.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Log;

public final class WebvttCue
  extends Cue
{
  public final long endTime;
  public final long startTime;
  
  public WebvttCue(long paramLong1, long paramLong2, CharSequence paramCharSequence)
  {
    this(paramLong1, paramLong2, paramCharSequence, null, Float.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE);
  }
  
  public WebvttCue(long paramLong1, long paramLong2, CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3)
  {
    super(paramCharSequence, paramAlignment, paramFloat1, paramInt1, paramInt2, paramFloat2, paramInt3, paramFloat3);
    startTime = paramLong1;
    endTime = paramLong2;
  }
  
  public WebvttCue(CharSequence paramCharSequence)
  {
    this(0L, 0L, paramCharSequence);
  }
  
  public boolean isNormalCue()
  {
    return (line == Float.MIN_VALUE) && (position == Float.MIN_VALUE);
  }
  
  public static class Builder
  {
    private static final String TAG = "WebvttCueBuilder";
    private long endTime;
    private float line;
    private int lineAnchor;
    private int lineType;
    private float position;
    private int positionAnchor;
    private long startTime;
    private SpannableStringBuilder text;
    private Layout.Alignment textAlignment;
    private float width;
    
    public Builder()
    {
      reset();
    }
    
    private Builder derivePositionAnchorFromAlignment()
    {
      if (textAlignment == null)
      {
        positionAnchor = Integer.MIN_VALUE;
        return this;
      }
      switch (WebvttCue.1.$SwitchMap$android$text$Layout$Alignment[textAlignment.ordinal()])
      {
      default: 
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unrecognized alignment: ");
        localStringBuilder.append(textAlignment);
        Log.w("WebvttCueBuilder", localStringBuilder.toString());
        positionAnchor = 0;
        return this;
      case 3: 
        positionAnchor = 2;
        return this;
      case 2: 
        positionAnchor = 1;
        return this;
      }
      positionAnchor = 0;
      return this;
    }
    
    public WebvttCue build()
    {
      if ((position != Float.MIN_VALUE) && (positionAnchor == Integer.MIN_VALUE)) {
        derivePositionAnchorFromAlignment();
      }
      return new WebvttCue(startTime, endTime, text, textAlignment, line, lineType, lineAnchor, position, positionAnchor, width);
    }
    
    public void reset()
    {
      startTime = 0L;
      endTime = 0L;
      text = null;
      textAlignment = null;
      line = Float.MIN_VALUE;
      lineType = Integer.MIN_VALUE;
      lineAnchor = Integer.MIN_VALUE;
      position = Float.MIN_VALUE;
      positionAnchor = Integer.MIN_VALUE;
      width = Float.MIN_VALUE;
    }
    
    public Builder setEndTime(long paramLong)
    {
      endTime = paramLong;
      return this;
    }
    
    public Builder setLine(float paramFloat)
    {
      line = paramFloat;
      return this;
    }
    
    public Builder setLineAnchor(int paramInt)
    {
      lineAnchor = paramInt;
      return this;
    }
    
    public Builder setLineType(int paramInt)
    {
      lineType = paramInt;
      return this;
    }
    
    public Builder setPosition(float paramFloat)
    {
      position = paramFloat;
      return this;
    }
    
    public Builder setPositionAnchor(int paramInt)
    {
      positionAnchor = paramInt;
      return this;
    }
    
    public Builder setStartTime(long paramLong)
    {
      startTime = paramLong;
      return this;
    }
    
    public Builder setText(SpannableStringBuilder paramSpannableStringBuilder)
    {
      text = paramSpannableStringBuilder;
      return this;
    }
    
    public Builder setTextAlignment(Layout.Alignment paramAlignment)
    {
      textAlignment = paramAlignment;
      return this;
    }
    
    public Builder setWidth(float paramFloat)
    {
      width = paramFloat;
      return this;
    }
  }
}
