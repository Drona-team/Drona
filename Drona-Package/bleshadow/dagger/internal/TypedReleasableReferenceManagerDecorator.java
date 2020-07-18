package bleshadow.dagger.internal;

import bleshadow.dagger.releasablereferences.ReleasableReferenceManager;
import bleshadow.dagger.releasablereferences.TypedReleasableReferenceManager;
import java.lang.annotation.Annotation;

@GwtIncompatible
public final class TypedReleasableReferenceManagerDecorator<M extends Annotation>
  implements TypedReleasableReferenceManager<M>
{
  private final ReleasableReferenceManager delegate;
  private final M metadata;
  
  public TypedReleasableReferenceManagerDecorator(ReleasableReferenceManager paramReleasableReferenceManager, Annotation paramAnnotation)
  {
    delegate = ((ReleasableReferenceManager)Preconditions.checkNotNull(paramReleasableReferenceManager));
    metadata = ((Annotation)Preconditions.checkNotNull(paramAnnotation));
  }
  
  public Annotation metadata()
  {
    return metadata;
  }
  
  public void releaseStrongReferences()
  {
    delegate.releaseStrongReferences();
  }
  
  public void restoreStrongReferences()
  {
    delegate.restoreStrongReferences();
  }
  
  public Class scope()
  {
    return delegate.scope();
  }
}
