package client.testing.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Location
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("latitude")
  private final double latitude;
  @SerializedName("longitude")
  private final double longitude;
  
  public Location(double paramDouble1, double paramDouble2)
  {
    latitude = paramDouble1;
    longitude = paramDouble2;
  }
  
  public double getLatitude()
  {
    return latitude;
  }
  
  public double getLongitude()
  {
    return longitude;
  }
}
