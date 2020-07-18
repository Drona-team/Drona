package com.google.android.exoplayer2.text.ClickListeners;

import java.util.Collections;
import java.util.List;

public final class Cea708InitializationData
{
  public final boolean isWideAspectRatio;
  
  private Cea708InitializationData(List paramList)
  {
    boolean bool = false;
    if (((byte[])paramList.get(0))[0] != 0) {
      bool = true;
    }
    isWideAspectRatio = bool;
  }
  
  public static List buildData(boolean paramBoolean)
  {
    return Collections.singletonList(new byte[] { (byte)paramBoolean });
  }
  
  public static Cea708InitializationData fromData(List paramList)
  {
    return new Cea708InitializationData(paramList);
  }
}
