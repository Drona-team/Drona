package com.google.android.gms.common.package_6.internal;

import java.util.concurrent.locks.Lock;

abstract class zaau
  implements Runnable
{
  private zaau(zaak paramZaak) {}
  
  public void run()
  {
    zaak.getLock(zagj).lock();
    try
    {
      boolean bool = Thread.interrupted();
      if (bool)
      {
        zaak.getLock(zagj).unlock();
        return;
      }
      zaan();
      zaak.getLock(zagj).unlock();
      return;
    }
    catch (Throwable localThrowable) {}catch (RuntimeException localRuntimeException)
    {
      zaak.items(zagj).enqueue(localRuntimeException);
      zaak.getLock(zagj).unlock();
      return;
    }
    zaak.getLock(zagj).unlock();
    throw localRuntimeException;
  }
  
  protected abstract void zaan();
}
