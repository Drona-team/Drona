package androidx.versionedparcelable;

import androidx.annotation.RestrictTo;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract class CustomVersionedParcelable
  implements VersionedParcelable
{
  public CustomVersionedParcelable() {}
  
  public void onPostParceling() {}
  
  public void onPreParceling(boolean paramBoolean) {}
}
