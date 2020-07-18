package android.support.v4.app;

import androidx.annotation.RestrictTo;
import androidx.core.package_4.RemoteActionCompat;
import androidx.versionedparcelable.VersionedParcel;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
public final class RemoteActionCompatParcelizer
  extends androidx.core.package_4.RemoteActionCompatParcelizer
{
  public RemoteActionCompatParcelizer() {}
  
  public static RemoteActionCompat read(VersionedParcel paramVersionedParcel)
  {
    return androidx.core.package_4.RemoteActionCompatParcelizer.read(paramVersionedParcel);
  }
  
  public static void write(RemoteActionCompat paramRemoteActionCompat, VersionedParcel paramVersionedParcel)
  {
    androidx.core.package_4.RemoteActionCompatParcelizer.write(paramRemoteActionCompat, paramVersionedParcel);
  }
}
