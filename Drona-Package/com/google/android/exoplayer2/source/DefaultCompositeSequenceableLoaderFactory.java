package com.google.android.exoplayer2.source;

public final class DefaultCompositeSequenceableLoaderFactory
  implements CompositeSequenceableLoaderFactory
{
  public DefaultCompositeSequenceableLoaderFactory() {}
  
  public SequenceableLoader createCompositeSequenceableLoader(SequenceableLoader... paramVarArgs)
  {
    return new CompositeSequenceableLoader(paramVarArgs);
  }
}
