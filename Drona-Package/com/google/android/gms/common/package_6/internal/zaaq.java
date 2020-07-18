package com.google.android.gms.common.package_6.internal;

import java.util.ArrayList;

final class zaaq
  extends zaau
{
  private final ArrayList<com.google.android.gms.common.api.Api.Client> zagp;
  
  public zaaq(zaak paramZaak, ArrayList paramArrayList)
  {
    super(paramZaak, null);
    zagp = paramArrayList;
  }
  
  public final void zaan()
  {
    itemszagj).zaee.zaha = zaak.getSubscriptions(zagj);
    ArrayList localArrayList = (ArrayList)zagp;
    int j = localArrayList.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = localArrayList.get(i);
      i += 1;
      ((com.google.android.gms.common.package_6.Api.Client)localObject).getRemoteService(zaak.toJsonString(zagj), itemszagj).zaee.zaha);
    }
  }
}
