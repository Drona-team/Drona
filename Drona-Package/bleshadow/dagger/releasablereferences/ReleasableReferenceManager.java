package bleshadow.dagger.releasablereferences;

import bleshadow.dagger.internal.GwtIncompatible;

@GwtIncompatible
public abstract interface ReleasableReferenceManager
{
  public abstract void releaseStrongReferences();
  
  public abstract void restoreStrongReferences();
  
  public abstract Class scope();
}
