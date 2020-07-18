package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.util.Util;

public final class TrackSelectorResult
{
  public final Object info;
  public final int length;
  public final RendererConfiguration[] rendererConfigurations;
  public final TrackSelectionArray selections;
  
  public TrackSelectorResult(RendererConfiguration[] paramArrayOfRendererConfiguration, TrackSelection[] paramArrayOfTrackSelection, Object paramObject)
  {
    rendererConfigurations = paramArrayOfRendererConfiguration;
    selections = new TrackSelectionArray(paramArrayOfTrackSelection);
    info = paramObject;
    length = paramArrayOfRendererConfiguration.length;
  }
  
  public boolean isEquivalent(TrackSelectorResult paramTrackSelectorResult)
  {
    if (paramTrackSelectorResult != null)
    {
      if (selections.length != selections.length) {
        return false;
      }
      int i = 0;
      while (i < selections.length)
      {
        if (!isEquivalent(paramTrackSelectorResult, i)) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    return false;
  }
  
  public boolean isEquivalent(TrackSelectorResult paramTrackSelectorResult, int paramInt)
  {
    if (paramTrackSelectorResult == null) {
      return false;
    }
    return (Util.areEqual(rendererConfigurations[paramInt], rendererConfigurations[paramInt])) && (Util.areEqual(selections.getChapters(paramInt), selections.getChapters(paramInt)));
  }
  
  public boolean isRendererEnabled(int paramInt)
  {
    return rendererConfigurations[paramInt] != null;
  }
}
