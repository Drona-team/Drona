package com.google.android.gms.common.package_6.internal;

import java.lang.ref.WeakReference;

final class zabc
  extends zabr
{
  private WeakReference<com.google.android.gms.common.api.internal.zaaw> zahm;
  
  zabc(zaaw paramZaaw)
  {
    zahm = new WeakReference(paramZaaw);
  }
  
  public final void cancel()
  {
    zaaw localZaaw = (zaaw)zahm.get();
    if (localZaaw == null) {
      return;
    }
    zaaw.access$1500(localZaaw);
  }
}
