package com.google.android.exoplayer2.offline;

import java.util.List;

public abstract interface FilterableManifest<T>
{
  public abstract Object copy(List paramList);
}