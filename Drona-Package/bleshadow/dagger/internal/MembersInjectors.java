package bleshadow.dagger.internal;

import bleshadow.dagger.MembersInjector;

public final class MembersInjectors
{
  private MembersInjectors() {}
  
  public static MembersInjector noOp()
  {
    return NoOpMembersInjector.INSTANCE;
  }
  
  private static enum NoOpMembersInjector
    implements MembersInjector<Object>
  {
    INSTANCE;
    
    public void injectMembers(Object paramObject)
    {
      Preconditions.checkNotNull(paramObject, "Cannot inject members into a null reference");
    }
  }
}
