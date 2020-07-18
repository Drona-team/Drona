package com.google.android.gms.common.images;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

@SafeParcelable.Class(creator="WebImageCreator")
public final class WebImage
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<WebImage> CREATOR = new VerticalProgressBar.SavedState.1();
  @SafeParcelable.VersionField(id=1)
  private final int zalf;
  @SafeParcelable.Field(getter="getWidth", id=3)
  private final int zane;
  @SafeParcelable.Field(getter="getHeight", id=4)
  private final int zanf;
  @SafeParcelable.Field(getter="getUrl", id=2)
  private final Uri zang;
  
  WebImage(int paramInt1, Uri paramUri, int paramInt2, int paramInt3)
  {
    zalf = paramInt1;
    zang = paramUri;
    zane = paramInt2;
    zanf = paramInt3;
  }
  
  public WebImage(Uri paramUri)
    throws IllegalArgumentException
  {
    this(paramUri, 0, 0);
  }
  
  public WebImage(Uri paramUri, int paramInt1, int paramInt2)
    throws IllegalArgumentException
  {
    this(1, paramUri, paramInt1, paramInt2);
    if (paramUri != null)
    {
      if ((paramInt1 >= 0) && (paramInt2 >= 0)) {
        return;
      }
      throw new IllegalArgumentException("width and height must not be negative");
    }
    throw new IllegalArgumentException("url cannot be null");
  }
  
  public WebImage(JSONObject paramJSONObject)
    throws IllegalArgumentException
  {
    this(fromJson(paramJSONObject), paramJSONObject.optInt("width", 0), paramJSONObject.optInt("height", 0));
  }
  
  private static Uri fromJson(JSONObject paramJSONObject)
  {
    if (paramJSONObject.has("url")) {}
    try
    {
      paramJSONObject = Uri.parse(paramJSONObject.getString("url"));
      return paramJSONObject;
    }
    catch (JSONException paramJSONObject)
    {
      for (;;) {}
    }
    return null;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (!(paramObject instanceof WebImage)) {
        return false;
      }
      paramObject = (WebImage)paramObject;
      if ((Objects.equal(zang, zang)) && (zane == zane) && (zanf == zanf)) {
        return true;
      }
    }
    return false;
  }
  
  public final int getHeight()
  {
    return zanf;
  }
  
  public final Uri getUrl()
  {
    return zang;
  }
  
  public final int getWidth()
  {
    return zane;
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { zang, Integer.valueOf(zane), Integer.valueOf(zanf) });
  }
  
  public final JSONObject toJson()
  {
    JSONObject localJSONObject = new JSONObject();
    Uri localUri = zang;
    try
    {
      localJSONObject.put("url", localUri.toString());
      int i = zane;
      localJSONObject.put("width", i);
      i = zanf;
      localJSONObject.put("height", i);
      return localJSONObject;
    }
    catch (JSONException localJSONException) {}
    return localJSONObject;
  }
  
  public final String toString()
  {
    return String.format(Locale.US, "Image %dx%d %s", new Object[] { Integer.valueOf(zane), Integer.valueOf(zanf), zang.toString() });
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, zalf);
    SafeParcelWriter.writeParcelable(paramParcel, 2, getUrl(), paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 3, getWidth());
    SafeParcelWriter.writeInt(paramParcel, 4, getHeight());
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}
