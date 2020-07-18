package com.google.android.gms.common.package_6.internal;

import java.util.concurrent.locks.Lock;

abstract class zabf
{
  private final zabd zahu;
  
  protected zabf(zabd paramZabd)
  {
    zahu = paramZabd;
  }
  
  public final void removeFile(zabe paramZabe)
  {
    zabe.getLock(paramZabe).lock();
    try
    {
      zabd localZabd1 = zabe.saveFile(paramZabe);
      zabd localZabd2 = zahu;
      if (localZabd1 != localZabd2)
      {
        zabe.getLock(paramZabe).unlock();
        return;
      }
      zaan();
      zabe.getLock(paramZabe).unlock();
      return;
    }
    catch (Throwable localThrowable)
    {
      zabe.getLock(paramZabe).unlock();
      throw localThrowable;
    }
  }
  
  protected abstract void zaan();
}
