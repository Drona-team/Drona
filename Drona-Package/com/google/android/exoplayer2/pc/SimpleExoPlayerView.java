package com.google.android.exoplayer2.pc;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import com.google.android.exoplayer2.SimpleExoPlayer;

@Deprecated
@TargetApi(16)
public final class SimpleExoPlayerView
  extends PlayerView
{
  public SimpleExoPlayerView(Context paramContext)
  {
    super(paramContext);
  }
  
  public SimpleExoPlayerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public SimpleExoPlayerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public static void switchTargetView(SimpleExoPlayer paramSimpleExoPlayer, SimpleExoPlayerView paramSimpleExoPlayerView1, SimpleExoPlayerView paramSimpleExoPlayerView2)
  {
    PlayerView.switchTargetView(paramSimpleExoPlayer, paramSimpleExoPlayerView1, paramSimpleExoPlayerView2);
  }
}
