package client.testing.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class ParametersConverter
{
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
  public static final String PROTOCOL_DATE_FORMAT = "yyyy-MM-dd";
  public static final String PROTOCOL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
  public static final String PROTOCOL_TIME_FORMAT = "HH:mm:ss";
  private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
  
  private ParametersConverter() {}
  
  public static Date parseDate(String paramString)
    throws ParseException
  {
    if (paramString != null) {
      return DATE_FORMAT.parse(paramString);
    }
    throw new IllegalArgumentException("Parameter must not be null");
  }
  
  public static Date parseDateTime(String paramString)
    throws ParseException
  {
    if (paramString != null) {
      return DATE_TIME_FORMAT.parse(paramString);
    }
    throw new IllegalArgumentException("Parameter must not be null");
  }
  
  public static float parseFloat(String paramString)
  {
    if (paramString != null) {
      return Float.parseFloat(paramString);
    }
    throw new IllegalArgumentException("Parameter must not be empty");
  }
  
  public static int parseInteger(String paramString)
    throws NumberFormatException
  {
    return Integer.parseInt(paramString);
  }
  
  private static Integer parsePart(String paramString)
  {
    if (paramString != null)
    {
      if (paramString.indexOf('u') >= 0) {
        return PartialDate.UNSPECIFIED_VALUE;
      }
      return Integer.valueOf(Integer.parseInt(paramString));
    }
    throw new IllegalArgumentException("part");
  }
  
  public static PartialDate parsePartialDate(String paramString)
    throws ParseException
  {
    if (paramString != null)
    {
      if (paramString.length() != 0)
      {
        if (paramString.contains("u"))
        {
          Object localObject = paramString.split("-");
          if (localObject.length == 3)
          {
            paramString = parsePart(localObject[0]);
            Integer localInteger = parsePart(localObject[1]);
            localObject = parsePart(localObject[2]);
            PartialDate localPartialDate = new PartialDate();
            localPartialDate.add(1, paramString);
            localPartialDate.add(2, localInteger);
            localPartialDate.add(5, (Integer)localObject);
            return localPartialDate;
          }
          throw new ParseException(String.format("Partial date must have 3 parts, but have %s: %s", new Object[] { Integer.valueOf(localObject.length), paramString }), 0);
        }
        return new PartialDate(DATE_FORMAT.parse(paramString));
      }
      throw new ParseException("Parameter must not be empty", 0);
    }
    throw new IllegalArgumentException("Parameter must not be empty");
  }
  
  public static Date parseTime(String paramString)
    throws ParseException
  {
    if (paramString != null)
    {
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.setTime(TIME_FORMAT.parse(paramString));
      paramString = Calendar.getInstance();
      paramString.set(11, localCalendar.get(11));
      paramString.set(12, localCalendar.get(12));
      paramString.set(13, localCalendar.get(13));
      return paramString.getTime();
    }
    throw new IllegalArgumentException("Parameter must not be null");
  }
}
