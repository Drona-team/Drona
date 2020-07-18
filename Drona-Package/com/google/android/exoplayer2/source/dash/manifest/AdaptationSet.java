package com.google.android.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;

public class AdaptationSet
{
  public static final int ID_UNSET = -1;
  public final List<Descriptor> accessibilityDescriptors;
  public final int nodeId;
  public final List<Representation> representations;
  public final List<Descriptor> supplementalProperties;
  public final int type;
  
  public AdaptationSet(int paramInt1, int paramInt2, List paramList1, List paramList2, List paramList3)
  {
    nodeId = paramInt1;
    type = paramInt2;
    representations = Collections.unmodifiableList(paramList1);
    if (paramList2 == null) {
      paramList1 = Collections.emptyList();
    } else {
      paramList1 = Collections.unmodifiableList(paramList2);
    }
    accessibilityDescriptors = paramList1;
    if (paramList3 == null) {
      paramList1 = Collections.emptyList();
    } else {
      paramList1 = Collections.unmodifiableList(paramList3);
    }
    supplementalProperties = paramList1;
  }
}
