package client.testing.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

public class PartialDate
{
  private static final String UNSPECIFIED_DATE = "uu";
  private static final String UNSPECIFIED_HOUR = "uu";
  private static final String UNSPECIFIED_MINUTE = "uu";
  private static final String UNSPECIFIED_MONTH = "uu";
  public static final Integer UNSPECIFIED_VALUE;
  private static final String UNSPECIFIED_YEAR = "uuuu";
  private final Calendar calendar;
  private final Set<Integer> unspecifiedFields = new HashSet();
  
  public PartialDate()
  {
    calendar = Calendar.getInstance();
  }
  
  public PartialDate(Calendar paramCalendar)
  {
    calendar = paramCalendar;
  }
  
  public PartialDate(Date paramDate)
  {
    calendar = new GregorianCalendar();
    calendar.setTime(paramDate);
  }
  
  private String getFieldAsString(int paramInt)
  {
    if (paramInt == 1)
    {
      if (unspecifiedFields.contains(Integer.valueOf(1))) {
        return "uuuu";
      }
      return String.format("%4d", new Object[] { Integer.valueOf(calendar.get(paramInt)) });
    }
    if (paramInt == 2)
    {
      if (unspecifiedFields.contains(Integer.valueOf(2))) {
        return "uu";
      }
      return String.format("%02d", new Object[] { Integer.valueOf(calendar.get(paramInt)) });
    }
    if ((paramInt >= 3) && (paramInt <= 8))
    {
      if (unspecifiedFields.contains(Integer.valueOf(5))) {
        return "uu";
      }
      return String.format("%02d", new Object[] { Integer.valueOf(calendar.get(paramInt)) });
    }
    if ((paramInt >= 10) && (paramInt <= 11))
    {
      if (unspecifiedFields.contains(Integer.valueOf(11))) {
        return "uu";
      }
      return String.format("%02d", new Object[] { Integer.valueOf(calendar.get(paramInt)) });
    }
    if (paramInt == 12)
    {
      if (unspecifiedFields.contains(Integer.valueOf(12))) {
        return "uu";
      }
      return String.format("%02d", new Object[] { Integer.valueOf(calendar.get(paramInt)) });
    }
    return String.format("%s", new Object[] { Integer.valueOf(calendar.get(paramInt)) });
  }
  
  public void add(int paramInt, Integer paramInteger)
  {
    if (paramInteger == UNSPECIFIED_VALUE)
    {
      if (paramInt == 1)
      {
        unspecifiedFields.add(Integer.valueOf(1));
        return;
      }
      if (paramInt == 2)
      {
        unspecifiedFields.add(Integer.valueOf(2));
        return;
      }
      if ((paramInt >= 3) && (paramInt <= 8))
      {
        unspecifiedFields.add(Integer.valueOf(5));
        return;
      }
      if ((paramInt >= 10) && (paramInt <= 11))
      {
        unspecifiedFields.add(Integer.valueOf(11));
        return;
      }
      if (paramInt == 12) {
        unspecifiedFields.add(Integer.valueOf(12));
      }
    }
    else
    {
      unspecifiedFields.remove(Integer.valueOf(paramInt));
      calendar.set(paramInt, paramInteger.intValue());
    }
  }
  
  public Integer parse(int paramInt)
  {
    if (paramInt == 1)
    {
      if (!unspecifiedFields.contains(Integer.valueOf(1))) {
        return Integer.valueOf(calendar.get(paramInt));
      }
      return UNSPECIFIED_VALUE;
    }
    if (paramInt == 2)
    {
      if (!unspecifiedFields.contains(Integer.valueOf(2))) {
        return Integer.valueOf(calendar.get(paramInt));
      }
      return UNSPECIFIED_VALUE;
    }
    if ((paramInt >= 3) && (paramInt <= 8))
    {
      if (!unspecifiedFields.contains(Integer.valueOf(5))) {
        return Integer.valueOf(calendar.get(paramInt));
      }
      return UNSPECIFIED_VALUE;
    }
    if ((paramInt >= 10) && (paramInt <= 11))
    {
      if (!unspecifiedFields.contains(Integer.valueOf(11))) {
        return Integer.valueOf(calendar.get(paramInt));
      }
      return UNSPECIFIED_VALUE;
    }
    if (paramInt == 12)
    {
      if (!unspecifiedFields.contains(Integer.valueOf(12))) {
        return Integer.valueOf(calendar.get(12));
      }
      return UNSPECIFIED_VALUE;
    }
    return Integer.valueOf(calendar.get(paramInt));
  }
  
  public String toString()
  {
    return String.format("%s-%s-%s", new Object[] { getFieldAsString(1), getFieldAsString(2), getFieldAsString(5) });
  }
}
