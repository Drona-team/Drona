package com.google.android.gms.clearcut;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved;
import com.google.android.gms.internal.clearcut.zzha;
import com.google.android.gms.internal.clearcut.zzr;
import com.google.android.gms.phenotype.ExperimentTokens;
import java.util.Arrays;

@SafeParcelable.Class(creator="LogEventParcelableCreator")
@SafeParcelable.Reserved({1})
public final class Departure
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zze> CREATOR = new DiscreteSeekBar.CustomState.1();
  public final ClearcutLogger.zzb destination;
  @SafeParcelable.Field(defaultValue="true", id=8)
  private boolean isVisible;
  public final zzha zzaa;
  @SafeParcelable.Field(id=2)
  public zzr zzag;
  @SafeParcelable.Field(id=3)
  public byte[] zzah;
  @SafeParcelable.Field(id=4)
  private int[] zzai;
  @SafeParcelable.Field(id=5)
  private String[] zzaj;
  @SafeParcelable.Field(id=6)
  private int[] zzak;
  @SafeParcelable.Field(id=7)
  private byte[][] zzal;
  @SafeParcelable.Field(id=9)
  private ExperimentTokens[] zzam;
  public final ClearcutLogger.zzb zzan;
  
  public Departure(zzr paramZzr, zzha paramZzha, ClearcutLogger.zzb paramZzb1, ClearcutLogger.zzb paramZzb2, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, byte[][] paramArrayOfByte, ExperimentTokens[] paramArrayOfExperimentTokens, boolean paramBoolean)
  {
    zzag = paramZzr;
    zzaa = paramZzha;
    destination = paramZzb1;
    zzan = null;
    zzai = paramArrayOfInt1;
    zzaj = null;
    zzak = paramArrayOfInt2;
    zzal = null;
    zzam = null;
    isVisible = paramBoolean;
  }
  
  Departure(zzr paramZzr, byte[] paramArrayOfByte, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, byte[][] paramArrayOfByte1, boolean paramBoolean, ExperimentTokens[] paramArrayOfExperimentTokens)
  {
    zzag = paramZzr;
    zzah = paramArrayOfByte;
    zzai = paramArrayOfInt1;
    zzaj = paramArrayOfString;
    zzaa = null;
    destination = null;
    zzan = null;
    zzak = paramArrayOfInt2;
    zzal = paramArrayOfByte1;
    zzam = paramArrayOfExperimentTokens;
    isVisible = paramBoolean;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Departure))
    {
      paramObject = (Departure)paramObject;
      if ((Objects.equal(zzag, zzag)) && (Arrays.equals(zzah, zzah)) && (Arrays.equals(zzai, zzai)) && (Arrays.equals(zzaj, zzaj)) && (Objects.equal(zzaa, zzaa)) && (Objects.equal(destination, destination)) && (Objects.equal(zzan, zzan)) && (Arrays.equals(zzak, zzak)) && (Arrays.deepEquals(zzal, zzal)) && (Arrays.equals(zzam, zzam)) && (isVisible == isVisible)) {
        return true;
      }
    }
    return false;
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { zzag, zzah, zzai, zzaj, zzaa, destination, zzan, zzak, zzal, zzam, Boolean.valueOf(isVisible) });
  }
  
  public final String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("LogEventParcelable[");
    localStringBuilder.append(zzag);
    localStringBuilder.append(", LogEventBytes: ");
    String str;
    if (zzah == null) {
      str = null;
    } else {
      str = new String(zzah);
    }
    localStringBuilder.append(str);
    localStringBuilder.append(", TestCodes: ");
    localStringBuilder.append(Arrays.toString(zzai));
    localStringBuilder.append(", MendelPackages: ");
    localStringBuilder.append(Arrays.toString(zzaj));
    localStringBuilder.append(", LogEvent: ");
    localStringBuilder.append(zzaa);
    localStringBuilder.append(", ExtensionProducer: ");
    localStringBuilder.append(destination);
    localStringBuilder.append(", VeProducer: ");
    localStringBuilder.append(zzan);
    localStringBuilder.append(", ExperimentIDs: ");
    localStringBuilder.append(Arrays.toString(zzak));
    localStringBuilder.append(", ExperimentTokens: ");
    localStringBuilder.append(Arrays.toString(zzal));
    localStringBuilder.append(", ExperimentTokensParcelables: ");
    localStringBuilder.append(Arrays.toString(zzam));
    localStringBuilder.append(", AddPhenotypeExperimentTokens: ");
    localStringBuilder.append(isVisible);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeParcelable(paramParcel, 2, (Parcelable)zzag, paramInt, false);
    SafeParcelWriter.writeByteArray(paramParcel, 3, zzah, false);
    SafeParcelWriter.writeIntArray(paramParcel, 4, zzai, false);
    SafeParcelWriter.writeStringArray(paramParcel, 5, zzaj, false);
    SafeParcelWriter.writeIntArray(paramParcel, 6, zzak, false);
    SafeParcelWriter.writeByteArrayArray(paramParcel, 7, zzal, false);
    SafeParcelWriter.writeBoolean(paramParcel, 8, isVisible);
    SafeParcelWriter.writeTypedArray(paramParcel, 9, (Parcelable[])zzam, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
