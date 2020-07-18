package com.google.android.exoplayer2.text.ClickListeners;

import android.text.Layout.Alignment;
import com.google.android.exoplayer2.text.Cue;

final class Cea708Cue
  extends Cue
  implements Comparable<com.google.android.exoplayer2.text.cea.Cea708Cue>
{
  public static final int PRIORITY_UNSET = -1;
  public final int priority;
  
  public Cea708Cue(CharSequence paramCharSequence, Layout.Alignment paramAlignment, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, int paramInt3, float paramFloat3, boolean paramBoolean, int paramInt4, int paramInt5)
  {
    super(paramCharSequence, paramAlignment, paramFloat1, paramInt1, paramInt2, paramFloat2, paramInt3, paramFloat3, paramBoolean, paramInt4);
    priority = paramInt5;
  }
  
  public int compareTo(Cea708Cue paramCea708Cue)
  {
    if (priority < priority) {
      return -1;
    }
    if (priority > priority) {
      return 1;
    }
    return 0;
  }
}
