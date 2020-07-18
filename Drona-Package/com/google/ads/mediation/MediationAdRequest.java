package com.google.ads.mediation;

import android.location.Location;
import com.google.ads.AdRequest.Gender;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Deprecated
public class MediationAdRequest
{
  private final Date zzms;
  private final AdRequest.Gender zzmt;
  private final Set<String> zzmu;
  private final boolean zzmv;
  private final Location zzmw;
  
  public MediationAdRequest(Date paramDate, AdRequest.Gender paramGender, Set paramSet, boolean paramBoolean, Location paramLocation)
  {
    zzms = paramDate;
    zzmt = paramGender;
    zzmu = paramSet;
    zzmv = paramBoolean;
    zzmw = paramLocation;
  }
  
  public Integer getAgeInYears()
  {
    Integer localInteger;
    if (zzms != null)
    {
      Calendar localCalendar1 = Calendar.getInstance();
      Calendar localCalendar2 = Calendar.getInstance();
      localCalendar1.setTime(zzms);
      localInteger = Integer.valueOf(localCalendar2.get(1) - localCalendar1.get(1));
      if ((localCalendar2.get(2) < localCalendar1.get(2)) || ((localCalendar2.get(2) == localCalendar1.get(2)) && (localCalendar2.get(5) < localCalendar1.get(5)))) {
        return Integer.valueOf(localInteger.intValue() - 1);
      }
    }
    else
    {
      return null;
    }
    return localInteger;
  }
  
  public Date getBirthday()
  {
    return zzms;
  }
  
  public AdRequest.Gender getGender()
  {
    return zzmt;
  }
  
  public Set getKeywords()
  {
    return zzmu;
  }
  
  public Location getLocation()
  {
    return zzmw;
  }
  
  public boolean isTesting()
  {
    return zzmv;
  }
}
