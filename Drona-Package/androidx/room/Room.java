package androidx.room;

import android.content.Context;

public class Room
{
  private static final String CURSOR_CONV_SUFFIX = "_CursorConverter";
  static final String LOG_TAG = "ROOM";
  public static final String MASTER_TABLE_NAME = "room_master_table";
  
  public Room() {}
  
  public static RoomDatabase.Builder databaseBuilder(Context paramContext, Class paramClass, String paramString)
  {
    if ((paramString != null) && (paramString.trim().length() != 0)) {
      return new RoomDatabase.Builder(paramContext, paramClass, paramString);
    }
    throw new IllegalArgumentException("Cannot build a database with null or empty name. If you are trying to create an in memory database, use Room.inMemoryDatabaseBuilder");
  }
  
  static Object getGeneratedImplementation(Class paramClass, String paramString)
  {
    String str = paramClass.getPackage().getName();
    Object localObject2 = paramClass.getCanonicalName();
    Object localObject1 = localObject2;
    if (!str.isEmpty()) {
      localObject1 = ((String)localObject2).substring(str.length() + 1);
    }
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append(((String)localObject1).replace('.', '_'));
    ((StringBuilder)localObject2).append(paramString);
    localObject1 = ((StringBuilder)localObject2).toString();
    try
    {
      boolean bool = str.isEmpty();
      if (bool)
      {
        paramString = (String)localObject1;
      }
      else
      {
        paramString = new StringBuilder();
        paramString.append(str);
        paramString.append(".");
        paramString.append((String)localObject1);
        paramString = paramString.toString();
      }
      paramString = Class.forName(paramString).newInstance();
      return paramString;
    }
    catch (ClassNotFoundException paramString)
    {
      for (;;) {}
    }
    catch (IllegalAccessException paramString)
    {
      for (;;) {}
    }
    catch (InstantiationException paramString)
    {
      for (;;) {}
    }
    paramString = new StringBuilder();
    paramString.append("Failed to create an instance of ");
    paramString.append(paramClass.getCanonicalName());
    throw new RuntimeException(paramString.toString());
    paramString = new StringBuilder();
    paramString.append("Cannot access the constructor");
    paramString.append(paramClass.getCanonicalName());
    throw new RuntimeException(paramString.toString());
    paramString = new StringBuilder();
    paramString.append("cannot find implementation for ");
    paramString.append(paramClass.getCanonicalName());
    paramString.append(". ");
    paramString.append((String)localObject1);
    paramString.append(" does not exist");
    throw new RuntimeException(paramString.toString());
  }
  
  public static RoomDatabase.Builder inMemoryDatabaseBuilder(Context paramContext, Class paramClass)
  {
    return new RoomDatabase.Builder(paramContext, paramClass, null);
  }
}
