package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.signin.internal.zac;
import com.google.android.gms.signin.internal.zaj;
import java.lang.ref.WeakReference;

final class zaar
  extends zac
{
  private final WeakReference<com.google.android.gms.common.api.internal.zaak> zagk;
  
  zaar(zaak paramZaak)
  {
    zagk = new WeakReference(paramZaak);
  }
  
  public final void items(zaj paramZaj)
  {
    zaak localZaak = (zaak)zagk.get();
    if (localZaak == null) {
      return;
    }
    zaak.items(localZaak).enqueue(new zaas(this, localZaak, localZaak, paramZaj));
  }
}
