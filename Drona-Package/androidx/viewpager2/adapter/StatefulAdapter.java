package androidx.viewpager2.adapter;

import android.os.Parcelable;

public abstract interface StatefulAdapter
{
  public abstract void restoreState(Parcelable paramParcelable);
  
  public abstract Parcelable saveState();
}
