package bleshadow.dagger.releasablereferences;

import bleshadow.dagger.internal.GwtIncompatible;
import java.lang.annotation.Annotation;

@GwtIncompatible
public abstract interface TypedReleasableReferenceManager<M extends Annotation>
  extends ReleasableReferenceManager
{
  public abstract Annotation metadata();
}
