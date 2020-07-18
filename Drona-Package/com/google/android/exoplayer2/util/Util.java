package com.google.android.exoplayer2.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Parcel;
import android.security.NetworkSecurityPolicy;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Display.Mode;
import android.view.WindowManager;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.upstream.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public final class Util
{
  private static final int[] CRC32_BYTES_MSBF = { 0, 79764919, 159529838, 222504665, 319059676, 398814059, 445009330, 507990021, 638119352, 583659535, 797628118, 726387553, 890018660, 835552979, 1015980042, 944750013, 1276238704, 1221641927, 1167319070, 1095957929, 1595256236, 1540665371, 1452775106, 1381403509, 1780037320, 1859660671, 1671105958, 1733955601, 2031960084, 2111593891, 1889500026, 1952343757, -1742489888, -1662866601, -1851683442, -1788833735, -1960329156, -1880695413, -2103051438, -2040207643, -1104454824, -1159051537, -1213636554, -1284997759, -1389417084, -1444007885, -1532160278, -1603531939, -734892656, -789352409, -575645954, -646886583, -952755380, -1007220997, -827056094, -898286187, -231047128, -151282273, -71779514, -8804623, -515967244, -436212925, -390279782, -327299027, 881225847, 809987520, 1023691545, 969234094, 662832811, 591600412, 771767749, 717299826, 311336399, 374308984, 453813921, 533576470, 25881363, 88864420, 134795389, 214552010, 2023205639, 2086057648, 1897238633, 1976864222, 1804852699, 1867694188, 1645340341, 1724971778, 1587496639, 1516133128, 1461550545, 1406951526, 1302016099, 1230646740, 1142491917, 1087903418, -1398421865, -1469785312, -1524105735, -1578704818, -1079922613, -1151291908, -1239184603, -1293773166, -1968362705, -1905510760, -2094067647, -2014441994, -1716953613, -1654112188, -1876203875, -1796572374, -525066777, -462094256, -382327159, -302564546, -206542021, -143559028, -97365931, -17609246, -960696225, -1031934488, -817968335, -872425850, -709327229, -780559564, -600130067, -654598054, 1762451694, 1842216281, 1619975040, 1682949687, 2047383090, 2127137669, 1938468188, 2001449195, 1325665622, 1271206113, 1183200824, 1111960463, 1543535498, 1489069629, 1434599652, 1363369299, 622672798, 568075817, 748617968, 677256519, 907627842, 853037301, 1067152940, 995781531, 51762726, 131386257, 177728840, 240578815, 269590778, 349224269, 429104020, 491947555, -248556018, -168932423, -122852000, -60002089, -500490030, -420856475, -341238852, -278395381, -685261898, -739858943, -559578920, -630940305, -1004286614, -1058877219, -845023740, -916395085, -1119974018, -1174433591, -1262701040, -1333941337, -1371866206, -1426332139, -1481064244, -1552294533, -1690935098, -1611170447, -1833673816, -1770699233, -2009983462, -1930228819, -2119160460, -2056179517, 1569362073, 1498123566, 1409854455, 1355396672, 1317987909, 1246755826, 1192025387, 1137557660, 2072149281, 2135122070, 1912620623, 1992383480, 1753615357, 1816598090, 1627664531, 1707420964, 295390185, 358241886, 404320391, 483945776, 43990325, 106832002, 186451547, 266083308, 932423249, 861060070, 1041341759, 986742920, 613929101, 542559546, 756411363, 701822548, -978770311, -1050133554, -869589737, -924188512, -693284699, -764654318, -550540341, -605129092, -475935807, -413084042, -366743377, -287118056, -257573603, -194731862, -114850189, -35218492, -1984365303, -1921392450, -2143631769, -2063868976, -1698919467, -1635936670, -1824608069, -1744851700, -1347415887, -1418654458, -1506661409, -1561119128, -1129027987, -1200260134, -1254728445, -1309196108 };
  public static final String DEVICE;
  public static final String DEVICE_DEBUG_INFO;
  public static final byte[] EMPTY_BYTE_ARRAY;
  private static final Pattern ESCAPED_CHARACTER_PATTERN;
  public static final String MANUFACTURER;
  public static final String MODEL;
  public static final int SDK_INT = Build.VERSION.SDK_INT;
  private static final String TAG = "Util";
  private static final Pattern XS_DATE_TIME_PATTERN;
  private static final Pattern XS_DURATION_PATTERN;
  
  static
  {
    DEVICE = Build.DEVICE;
    MANUFACTURER = Build.MANUFACTURER;
    MODEL = Build.MODEL;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(DEVICE);
    localStringBuilder.append(", ");
    localStringBuilder.append(MODEL);
    localStringBuilder.append(", ");
    localStringBuilder.append(MANUFACTURER);
    localStringBuilder.append(", ");
    localStringBuilder.append(SDK_INT);
    DEVICE_DEBUG_INFO = localStringBuilder.toString();
    EMPTY_BYTE_ARRAY = new byte[0];
    XS_DATE_TIME_PATTERN = Pattern.compile("(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt](\\d\\d):(\\d\\d):(\\d\\d)([\\.,](\\d+))?([Zz]|((\\+|\\-)(\\d?\\d):?(\\d\\d)))?");
    XS_DURATION_PATTERN = Pattern.compile("^(-)?P(([0-9]*)Y)?(([0-9]*)M)?(([0-9]*)D)?(T(([0-9]*)H)?(([0-9]*)M)?(([0-9.]*)S)?)?$");
    ESCAPED_CHARACTER_PATTERN = Pattern.compile("%([A-Fa-f0-9]{2})");
  }
  
  private Util() {}
  
  public static long addWithOverflowDefault(long paramLong1, long paramLong2, long paramLong3)
  {
    long l = paramLong1 + paramLong2;
    if (((paramLong1 ^ l) & (paramLong2 ^ l)) < 0L) {
      return paramLong3;
    }
    return l;
  }
  
  public static boolean areEqual(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == null) {
      return paramObject2 == null;
    }
    return paramObject1.equals(paramObject2);
  }
  
  public static int binarySearchCeil(List paramList, Comparable paramComparable, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Collections.binarySearch(paramList, paramComparable);
    int i = j;
    if (j < 0)
    {
      i = j;
    }
    else
    {
      int k = paramList.size();
      for (;;)
      {
        j = i + 1;
        if ((j >= k) || (((Comparable)paramList.get(j)).compareTo(paramComparable) != 0)) {
          break;
        }
        i = j;
      }
      i = j;
      if (paramBoolean1) {
        i = j - 1;
      }
    }
    j = i;
    if (paramBoolean2) {
      j = Math.min(paramList.size() - 1, i);
    }
    return j;
  }
  
  public static int binarySearchCeil(long[] paramArrayOfLong, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Arrays.binarySearch(paramArrayOfLong, paramLong);
    int i = j;
    if (j < 0)
    {
      i = j;
    }
    else
    {
      for (;;)
      {
        j = i + 1;
        if ((j >= paramArrayOfLong.length) || (paramArrayOfLong[j] != paramLong)) {
          break;
        }
        i = j;
      }
      i = j;
      if (paramBoolean1) {
        i = j - 1;
      }
    }
    j = i;
    if (paramBoolean2) {
      j = Math.min(paramArrayOfLong.length - 1, i);
    }
    return j;
  }
  
  public static int binarySearchFloor(List paramList, Comparable paramComparable, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Collections.binarySearch(paramList, paramComparable);
    int i = j;
    if (j < 0)
    {
      i = -(j + 2);
    }
    else
    {
      for (;;)
      {
        j = i - 1;
        if ((j < 0) || (((Comparable)paramList.get(j)).compareTo(paramComparable) != 0)) {
          break;
        }
        i = j;
      }
      i = j;
      if (paramBoolean1) {
        i = j + 1;
      }
    }
    j = i;
    if (paramBoolean2) {
      j = Math.max(0, i);
    }
    return j;
  }
  
  public static int binarySearchFloor(int[] paramArrayOfInt, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Arrays.binarySearch(paramArrayOfInt, paramInt);
    int i = j;
    if (j < 0)
    {
      paramInt = -(j + 2);
    }
    else
    {
      do
      {
        i -= 1;
      } while ((i >= 0) && (paramArrayOfInt[i] == paramInt));
      if (paramBoolean1) {
        paramInt = i + 1;
      } else {
        paramInt = i;
      }
    }
    i = paramInt;
    if (paramBoolean2) {
      i = Math.max(0, paramInt);
    }
    return i;
  }
  
  public static int binarySearchFloor(long[] paramArrayOfLong, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = Arrays.binarySearch(paramArrayOfLong, paramLong);
    int i = j;
    if (j < 0)
    {
      i = -(j + 2);
    }
    else
    {
      do
      {
        i -= 1;
      } while ((i >= 0) && (paramArrayOfLong[i] == paramLong));
      if (paramBoolean1) {
        i += 1;
      }
    }
    j = i;
    if (paramBoolean2) {
      j = Math.max(0, i);
    }
    return j;
  }
  
  public static Object castNonNull(Object paramObject)
  {
    return paramObject;
  }
  
  public static Object[] castNonNullTypeArray(Object[] paramArrayOfObject)
  {
    return paramArrayOfObject;
  }
  
  public static int ceilDivide(int paramInt1, int paramInt2)
  {
    return (paramInt1 + paramInt2 - 1) / paramInt2;
  }
  
  public static long ceilDivide(long paramLong1, long paramLong2)
  {
    return (paramLong1 + paramLong2 - 1L) / paramLong2;
  }
  
  public static boolean checkCleartextTrafficPermitted(Uri... paramVarArgs)
  {
    if (SDK_INT < 24) {
      return true;
    }
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      Uri localUri = paramVarArgs[i];
      if (("http".equals(localUri.getScheme())) && (!NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(localUri.getHost()))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static void closeQuietly(DataSource paramDataSource)
  {
    if (paramDataSource != null) {
      try
      {
        paramDataSource.close();
        return;
      }
      catch (IOException paramDataSource) {}
    }
  }
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
        return;
      }
      catch (IOException paramCloseable) {}
    }
  }
  
  public static int compareLong(long paramLong1, long paramLong2)
  {
    boolean bool = paramLong1 < paramLong2;
    if (bool) {
      return -1;
    }
    if (!bool) {
      return 0;
    }
    return 1;
  }
  
  public static float constrainValue(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return Math.max(paramFloat2, Math.min(paramFloat1, paramFloat3));
  }
  
  public static int constrainValue(int paramInt1, int paramInt2, int paramInt3)
  {
    return Math.max(paramInt2, Math.min(paramInt1, paramInt3));
  }
  
  public static long constrainValue(long paramLong1, long paramLong2, long paramLong3)
  {
    return Math.max(paramLong2, Math.min(paramLong1, paramLong3));
  }
  
  public static boolean contains(Object[] paramArrayOfObject, Object paramObject)
  {
    int j = paramArrayOfObject.length;
    int i = 0;
    while (i < j)
    {
      if (areEqual(paramArrayOfObject[i], paramObject)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static Handler createHandler(Handler.Callback paramCallback)
  {
    return createHandler(getLooper(), paramCallback);
  }
  
  public static Handler createHandler(Looper paramLooper, Handler.Callback paramCallback)
  {
    return new Handler(paramLooper, paramCallback);
  }
  
  public static File createTempDirectory(Context paramContext, String paramString)
    throws IOException
  {
    paramContext = createTempFile(paramContext, paramString);
    paramContext.delete();
    paramContext.mkdir();
    return paramContext;
  }
  
  public static File createTempFile(Context paramContext, String paramString)
    throws IOException
  {
    return File.createTempFile(paramString, null, paramContext.getCacheDir());
  }
  
  public static String escapeFileName(String paramString)
  {
    int n = paramString.length();
    int m = 0;
    int j = 0;
    int k;
    for (int i = 0; j < n; i = k)
    {
      k = i;
      if (shouldEscapeCharacter(paramString.charAt(j))) {
        k = i + 1;
      }
      j += 1;
    }
    if (i == 0) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(i * 2 + n);
    j = i;
    i = m;
    while (j > 0)
    {
      char c = paramString.charAt(i);
      if (shouldEscapeCharacter(c))
      {
        localStringBuilder.append('%');
        localStringBuilder.append(Integer.toHexString(c));
        j -= 1;
      }
      else
      {
        localStringBuilder.append(c);
      }
      i += 1;
    }
    if (i < n) {
      localStringBuilder.append(paramString, i, n);
    }
    return localStringBuilder.toString();
  }
  
  public static String formatInvariant(String paramString, Object... paramVarArgs)
  {
    return String.format(Locale.US, paramString, paramVarArgs);
  }
  
  public static String fromUtf8Bytes(byte[] paramArrayOfByte)
  {
    return new String(paramArrayOfByte, Charset.forName("UTF-8"));
  }
  
  public static String fromUtf8Bytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return new String(paramArrayOfByte, paramInt1, paramInt2, Charset.forName("UTF-8"));
  }
  
  public static int getAudioContentTypeForStreamType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      break;
    case 3: 
    case 6: 
    case 7: 
      return 2;
    case 1: 
    case 2: 
    case 4: 
    case 5: 
    case 8: 
      return 4;
    }
    return 1;
  }
  
  public static int getAudioTrackChannelConfig(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 8: 
      if (SDK_INT >= 23) {
        return 6396;
      }
      if (SDK_INT >= 21) {
        return 6396;
      }
      return 0;
    case 7: 
      return 1276;
    case 6: 
      return 252;
    case 5: 
      return 220;
    case 4: 
      return 204;
    case 3: 
      return 28;
    case 2: 
      return 12;
    }
    return 4;
  }
  
  public static int getAudioUsageForStreamType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      break;
    case 3: 
    case 6: 
    case 7: 
      return 1;
    case 8: 
      return 3;
    case 5: 
      return 5;
    case 4: 
      return 4;
    case 2: 
      return 6;
    case 1: 
      return 13;
    }
    return 2;
  }
  
  public static byte[] getBytesFromHexString(String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    int i = 0;
    while (i < arrayOfByte.length)
    {
      int j = i * 2;
      arrayOfByte[i] = ((byte)((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16)));
      i += 1;
    }
    return arrayOfByte;
  }
  
  public static String getCodecsOfType(String paramString, int paramInt)
  {
    paramString = splitCodecs(paramString);
    if (paramString.length == 0) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int j = paramString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramString[i];
      if (paramInt == MimeTypes.getTrackTypeOfCodec(str))
      {
        if (localStringBuilder.length() > 0) {
          localStringBuilder.append(",");
        }
        localStringBuilder.append(str);
      }
      i += 1;
    }
    if (localStringBuilder.length() > 0) {
      return localStringBuilder.toString();
    }
    return null;
  }
  
  public static String getCommaDelimitedSimpleClassNames(Object[] paramArrayOfObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < paramArrayOfObject.length)
    {
      localStringBuilder.append(paramArrayOfObject[i].getClass().getSimpleName());
      if (i < paramArrayOfObject.length - 1) {
        localStringBuilder.append(", ");
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static String getCountryCode(Context paramContext)
  {
    if (paramContext != null)
    {
      paramContext = (TelephonyManager)paramContext.getSystemService("phone");
      if (paramContext != null)
      {
        paramContext = paramContext.getNetworkCountryIso();
        if (!TextUtils.isEmpty(paramContext)) {
          return toUpperInvariant(paramContext);
        }
      }
    }
    return toUpperInvariant(Locale.getDefault().getCountry());
  }
  
  public static int getDefaultBufferSize(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalStateException();
    case 3: 
    case 4: 
    case 5: 
      return 131072;
    case 2: 
      return 13107200;
    case 1: 
      return 3538944;
    }
    return 16777216;
  }
  
  private static void getDisplaySizeV16(Display paramDisplay, Point paramPoint)
  {
    paramDisplay.getSize(paramPoint);
  }
  
  private static void getDisplaySizeV17(Display paramDisplay, Point paramPoint)
  {
    paramDisplay.getRealSize(paramPoint);
  }
  
  private static void getDisplaySizeV23(Display paramDisplay, Point paramPoint)
  {
    paramDisplay = paramDisplay.getMode();
    x = paramDisplay.getPhysicalWidth();
    y = paramDisplay.getPhysicalHeight();
  }
  
  private static void getDisplaySizeV9(Display paramDisplay, Point paramPoint)
  {
    x = paramDisplay.getWidth();
    y = paramDisplay.getHeight();
  }
  
  public static UUID getDrmUuid(String paramString)
  {
    String str = toLowerInvariant(paramString);
    int i = str.hashCode();
    if (i != -1860423953)
    {
      if (i != -1400551171)
      {
        if ((i == 790309106) && (str.equals("clearkey")))
        {
          i = 2;
          break label81;
        }
      }
      else if (str.equals("widevine"))
      {
        i = 0;
        break label81;
      }
    }
    else if (str.equals("playready"))
    {
      i = 1;
      break label81;
    }
    i = -1;
    switch (i)
    {
    default: 
      label81:
      break;
    }
    try
    {
      paramString = UUID.fromString(paramString);
      return paramString;
    }
    catch (RuntimeException paramString)
    {
      for (;;) {}
    }
    return IpAddress.CLEARKEY_UUID;
    return IpAddress.PLAYREADY_UUID;
    return IpAddress.WIDEVINE_UUID;
    return null;
  }
  
  public static int getIntegerCodeForString(String paramString)
  {
    int k = paramString.length();
    int i = 0;
    boolean bool;
    if (k <= 4) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    int j = 0;
    while (i < k)
    {
      j = j << 8 | paramString.charAt(i);
      i += 1;
    }
    return j;
  }
  
  public static Looper getLooper()
  {
    Looper localLooper = Looper.myLooper();
    if (localLooper != null) {
      return localLooper;
    }
    return Looper.getMainLooper();
  }
  
  public static long getMediaDurationForPlayoutDuration(long paramLong, float paramFloat)
  {
    if (paramFloat == 1.0F) {
      return paramLong;
    }
    return Math.round(paramLong * paramFloat);
  }
  
  private static int getMobileNetworkType(NetworkInfo paramNetworkInfo)
  {
    switch (paramNetworkInfo.getSubtype())
    {
    default: 
      break;
    case 16: 
      return 6;
    case 18: 
      return 2;
    case 13: 
      return 5;
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 14: 
    case 15: 
    case 17: 
      return 4;
    }
    return 3;
  }
  
  public static int getNetworkType(Context paramContext)
  {
    if (paramContext == null) {
      return 0;
    }
    try
    {
      paramContext = paramContext.getSystemService("connectivity");
      paramContext = (ConnectivityManager)paramContext;
      if (paramContext == null) {
        return 0;
      }
      paramContext = paramContext.getActiveNetworkInfo();
      if ((paramContext != null) && (paramContext.isConnected()))
      {
        switch (paramContext.getType())
        {
        default: 
          break;
        case 2: 
        case 3: 
        case 7: 
        case 8: 
          return 8;
        case 9: 
          return 7;
        case 6: 
          return 5;
        case 1: 
          return 2;
        }
        return getMobileNetworkType(paramContext);
      }
      return 1;
    }
    catch (SecurityException paramContext) {}
    return 0;
  }
  
  public static int getPcmEncoding(int paramInt)
  {
    if (paramInt != 8)
    {
      if (paramInt != 16)
      {
        if (paramInt != 24)
        {
          if (paramInt != 32) {
            return 0;
          }
          return 1073741824;
        }
        return Integer.MIN_VALUE;
      }
      return 2;
    }
    return 3;
  }
  
  public static int getPcmFrameSize(int paramInt1, int paramInt2)
  {
    if (paramInt1 != Integer.MIN_VALUE)
    {
      if (paramInt1 != 1073741824) {
        switch (paramInt1)
        {
        default: 
          throw new IllegalArgumentException();
        case 3: 
          return paramInt2;
        case 2: 
          return paramInt2 * 2;
        }
      }
      return paramInt2 * 4;
    }
    return paramInt2 * 3;
  }
  
  public static Point getPhysicalDisplaySize(Context paramContext)
  {
    return getPhysicalDisplaySize(paramContext, ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay());
  }
  
  public static Point getPhysicalDisplaySize(Context paramContext, Display paramDisplay)
  {
    Method localMethod;
    if ((SDK_INT < 25) && (paramDisplay.getDisplayId() == 0))
    {
      if (("Sony".equals(MANUFACTURER)) && (MODEL.startsWith("BRAVIA")) && (paramContext.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd"))) {
        return new Point(3840, 2160);
      }
      if ((("NVIDIA".equals(MANUFACTURER)) && (MODEL.contains("SHIELD"))) || (("philips".equals(toLowerInvariant(MANUFACTURER))) && ((MODEL.startsWith("QM1")) || (MODEL.equals("QV151E")) || (MODEL.equals("TPM171E")))))
      {
        paramContext = null;
        try
        {
          Object localObject1 = Class.forName("android.os.SystemProperties");
          localMethod = ((Class)localObject1).getMethod("get", new Class[] { String.class });
          localObject1 = localMethod.invoke(localObject1, new Object[] { "sys.display-size" });
          paramContext = (String)localObject1;
        }
        catch (Exception localException)
        {
          Log.e("Util", "Failed to read sys.display-size", localException);
        }
        if (TextUtils.isEmpty(paramContext)) {}
      }
    }
    try
    {
      localObject2 = split(paramContext.trim(), "x");
      if (localObject2.length == 2)
      {
        localMethod = localObject2[0];
        int i = Integer.parseInt(localMethod);
        localObject2 = localObject2[1];
        int j = Integer.parseInt((String)localObject2);
        if ((i > 0) && (j > 0))
        {
          localObject2 = new Point(i, j);
          return localObject2;
        }
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Object localObject2;
      for (;;) {}
    }
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("Invalid sys.display-size: ");
    ((StringBuilder)localObject2).append(paramContext);
    Log.e("Util", ((StringBuilder)localObject2).toString());
    paramContext = new Point();
    if (SDK_INT >= 23)
    {
      getDisplaySizeV23(paramDisplay, paramContext);
      return paramContext;
    }
    if (SDK_INT >= 17)
    {
      getDisplaySizeV17(paramDisplay, paramContext);
      return paramContext;
    }
    if (SDK_INT >= 16)
    {
      getDisplaySizeV16(paramDisplay, paramContext);
      return paramContext;
    }
    getDisplaySizeV9(paramDisplay, paramContext);
    return paramContext;
  }
  
  public static long getPlayoutDurationForMediaDuration(long paramLong, float paramFloat)
  {
    if (paramFloat == 1.0F) {
      return paramLong;
    }
    return Math.round(paramLong / paramFloat);
  }
  
  public static int getStreamTypeForAudioUsage(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      break;
    case 11: 
      return 3;
    case 13: 
      return 1;
    case 6: 
      return 2;
    case 5: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
      return 5;
    case 4: 
      return 4;
    case 3: 
      return 8;
    case 2: 
      return 0;
    }
    return 3;
  }
  
  public static String getStringForTime(StringBuilder paramStringBuilder, Formatter paramFormatter, long paramLong)
  {
    long l1 = paramLong;
    if (paramLong == -9223372036854775807L) {
      l1 = 0L;
    }
    long l2 = (l1 + 500L) / 1000L;
    paramLong = l2 % 60L;
    l1 = l2 / 60L % 60L;
    l2 /= 3600L;
    paramStringBuilder.setLength(0);
    if (l2 > 0L) {
      return paramFormatter.format("%d:%02d:%02d", new Object[] { Long.valueOf(l2), Long.valueOf(l1), Long.valueOf(paramLong) }).toString();
    }
    return paramFormatter.format("%02d:%02d", new Object[] { Long.valueOf(l1), Long.valueOf(paramLong) }).toString();
  }
  
  public static String getUserAgent(Context paramContext, String paramString)
  {
    try
    {
      localObject = paramContext.getPackageName();
      paramContext = paramContext.getPackageManager().getPackageInfo((String)localObject, 0);
      paramContext = versionName;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Object localObject;
      for (;;) {}
    }
    paramContext = "?";
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("/");
    ((StringBuilder)localObject).append(paramContext);
    ((StringBuilder)localObject).append(" (Linux;Android ");
    ((StringBuilder)localObject).append(Build.VERSION.RELEASE);
    ((StringBuilder)localObject).append(") ");
    ((StringBuilder)localObject).append("ExoPlayerLib/2.9.2");
    return ((StringBuilder)localObject).toString();
  }
  
  public static byte[] getUtf8Bytes(String paramString)
  {
    return paramString.getBytes(Charset.forName("UTF-8"));
  }
  
  public static int inferContentType(Uri paramUri)
  {
    paramUri = paramUri.getPath();
    if (paramUri == null) {
      return 3;
    }
    return inferContentType(paramUri);
  }
  
  public static int inferContentType(Uri paramUri, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return inferContentType(paramUri);
    }
    paramUri = new StringBuilder();
    paramUri.append(".");
    paramUri.append(paramString);
    return inferContentType(paramUri.toString());
  }
  
  public static int inferContentType(String paramString)
  {
    paramString = toLowerInvariant(paramString);
    if (paramString.endsWith(".mpd")) {
      return 0;
    }
    if (paramString.endsWith(".m3u8")) {
      return 2;
    }
    if (paramString.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
      return 1;
    }
    return 3;
  }
  
  public static boolean inflate(ParsableByteArray paramParsableByteArray1, ParsableByteArray paramParsableByteArray2, Inflater paramInflater)
  {
    if (paramParsableByteArray1.bytesLeft() <= 0) {
      return false;
    }
    Object localObject2 = data;
    Object localObject1 = localObject2;
    if (localObject2.length < paramParsableByteArray1.bytesLeft()) {
      localObject1 = new byte[paramParsableByteArray1.bytesLeft() * 2];
    }
    localObject2 = paramInflater;
    if (paramInflater == null) {
      localObject2 = new Inflater();
    }
    ((Inflater)localObject2).setInput(data, paramParsableByteArray1.getPosition(), paramParsableByteArray1.bytesLeft());
    int i = 0;
    try
    {
      j = localObject1.length;
    }
    catch (Throwable paramParsableByteArray1)
    {
      for (;;)
      {
        try
        {
          j = ((Inflater)localObject2).inflate((byte[])localObject1, i, j - i);
          j = i + j;
          bool = ((Inflater)localObject2).finished();
          if (bool)
          {
            paramParsableByteArray2.reset((byte[])localObject1, j);
            ((Inflater)localObject2).reset();
            return true;
          }
        }
        catch (DataFormatException paramParsableByteArray1)
        {
          int j;
          boolean bool;
          continue;
        }
        try
        {
          bool = ((Inflater)localObject2).needsDictionary();
          if (!bool)
          {
            bool = ((Inflater)localObject2).needsInput();
            if (!bool)
            {
              int k = localObject1.length;
              i = j;
              if (j != k) {
                continue;
              }
              i = localObject1.length;
              localObject1 = Arrays.copyOf((byte[])localObject1, i * 2);
              i = j;
              continue;
            }
          }
          ((Inflater)localObject2).reset();
          return false;
        }
        catch (DataFormatException paramParsableByteArray1) {}
      }
      paramParsableByteArray1 = paramParsableByteArray1;
      ((Inflater)localObject2).reset();
      throw paramParsableByteArray1;
    }
    ((Inflater)localObject2).reset();
    return false;
  }
  
  public static boolean isEncodingHighResolutionIntegerPcm(int paramInt)
  {
    return (paramInt == Integer.MIN_VALUE) || (paramInt == 1073741824);
  }
  
  public static boolean isEncodingLinearPcm(int paramInt)
  {
    return (paramInt == 3) || (paramInt == 2) || (paramInt == Integer.MIN_VALUE) || (paramInt == 1073741824) || (paramInt == 4);
  }
  
  public static boolean isLinebreak(int paramInt)
  {
    return (paramInt == 10) || (paramInt == 13);
  }
  
  public static boolean isLocalFileUri(Uri paramUri)
  {
    paramUri = paramUri.getScheme();
    return (TextUtils.isEmpty(paramUri)) || ("file".equals(paramUri));
  }
  
  public static boolean maybeRequestReadExternalStoragePermission(Activity paramActivity, Uri... paramVarArgs)
  {
    if (SDK_INT < 23) {
      return false;
    }
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      if (isLocalFileUri(paramVarArgs[i]))
      {
        if (paramActivity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
          break;
        }
        paramActivity.requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 0);
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public static ExecutorService newSingleThreadExecutor(String paramString)
  {
    return Executors.newSingleThreadExecutor(new -..Lambda.Util.MRC4FgxCpRGDforKj-F0m_7VaCA(paramString));
  }
  
  public static String normalizeLanguageCode(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      String str = new Locale(paramString).getISO3Language();
      return str;
    }
    catch (MissingResourceException localMissingResourceException)
    {
      for (;;) {}
    }
    return toLowerInvariant(paramString);
  }
  
  public static Object[] nullSafeArrayCopy(Object[] paramArrayOfObject, int paramInt)
  {
    boolean bool;
    if (paramInt <= paramArrayOfObject.length) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    return Arrays.copyOf(paramArrayOfObject, paramInt);
  }
  
  public static long parseXsDateTime(String paramString)
    throws ParserException
  {
    Object localObject = XS_DATE_TIME_PATTERN.matcher(paramString);
    long l;
    if (((Matcher)localObject).matches())
    {
      paramString = ((Matcher)localObject).group(9);
      int i = 0;
      if ((paramString != null) && (!((Matcher)localObject).group(9).equalsIgnoreCase("Z")))
      {
        int j = Integer.parseInt(((Matcher)localObject).group(12)) * 60 + Integer.parseInt(((Matcher)localObject).group(13));
        i = j;
        if ("-".equals(((Matcher)localObject).group(11))) {
          i = j * -1;
        }
      }
      paramString = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
      paramString.clear();
      paramString.set(Integer.parseInt(((Matcher)localObject).group(1)), Integer.parseInt(((Matcher)localObject).group(2)) - 1, Integer.parseInt(((Matcher)localObject).group(3)), Integer.parseInt(((Matcher)localObject).group(4)), Integer.parseInt(((Matcher)localObject).group(5)), Integer.parseInt(((Matcher)localObject).group(6)));
      if (!TextUtils.isEmpty(((Matcher)localObject).group(8)))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("0.");
        localStringBuilder.append(((Matcher)localObject).group(8));
        paramString.set(14, new BigDecimal(localStringBuilder.toString()).movePointRight(3).intValue());
      }
      l = paramString.getTimeInMillis();
      if (i != 0) {
        return l - i * 60000;
      }
    }
    else
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Invalid date/time format: ");
      ((StringBuilder)localObject).append(paramString);
      throw new ParserException(((StringBuilder)localObject).toString());
    }
    return l;
  }
  
  public static long parseXsDuration(String paramString)
  {
    Matcher localMatcher = XS_DURATION_PATTERN.matcher(paramString);
    long l1;
    if (localMatcher.matches())
    {
      boolean bool = TextUtils.isEmpty(localMatcher.group(1));
      paramString = localMatcher.group(3);
      double d6 = 0.0D;
      double d1;
      if (paramString != null) {
        d1 = Double.parseDouble(paramString) * 3.1556908E7D;
      } else {
        d1 = 0.0D;
      }
      paramString = localMatcher.group(5);
      double d2;
      if (paramString != null) {
        d2 = Double.parseDouble(paramString) * 2629739.0D;
      } else {
        d2 = 0.0D;
      }
      paramString = localMatcher.group(7);
      double d3;
      if (paramString != null) {
        d3 = Double.parseDouble(paramString) * 86400.0D;
      } else {
        d3 = 0.0D;
      }
      paramString = localMatcher.group(10);
      double d4;
      if (paramString != null) {
        d4 = 3600.0D * Double.parseDouble(paramString);
      } else {
        d4 = 0.0D;
      }
      paramString = localMatcher.group(12);
      double d5;
      if (paramString != null) {
        d5 = Double.parseDouble(paramString) * 60.0D;
      } else {
        d5 = 0.0D;
      }
      paramString = localMatcher.group(14);
      if (paramString != null) {
        d6 = Double.parseDouble(paramString);
      }
      long l2 = ((d1 + d2 + d3 + d4 + d5 + d6) * 1000.0D);
      l1 = l2;
      if ((true ^ bool)) {
        return -l2;
      }
    }
    else
    {
      l1 = (Double.parseDouble(paramString) * 3600.0D * 1000.0D);
    }
    return l1;
  }
  
  public static int process(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    while (paramInt1 < paramInt2)
    {
      paramInt3 = CRC32_BYTES_MSBF[((paramInt3 >>> 24 ^ paramArrayOfByte[paramInt1] & 0xFF) & 0xFF)] ^ paramInt3 << 8;
      paramInt1 += 1;
    }
    return paramInt3;
  }
  
  public static boolean readBoolean(Parcel paramParcel)
  {
    return paramParcel.readInt() != 0;
  }
  
  public static void recursiveDelete(File paramFile)
  {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile != null)
    {
      int j = arrayOfFile.length;
      int i = 0;
      while (i < j)
      {
        recursiveDelete(arrayOfFile[i]);
        i += 1;
      }
    }
    paramFile.delete();
  }
  
  public static void removeRange(List paramList, int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt2 <= paramList.size()) && (paramInt1 <= paramInt2))
    {
      if (paramInt1 != paramInt2) {
        paramList.subList(paramInt1, paramInt2).clear();
      }
    }
    else {
      throw new IllegalArgumentException();
    }
  }
  
  public static long resolveSeekPositionUs(long paramLong1, SeekParameters paramSeekParameters, long paramLong2, long paramLong3)
  {
    if (SeekParameters.EXACT.equals(paramSeekParameters)) {
      return paramLong1;
    }
    long l1 = subtractWithOverflowDefault(paramLong1, toleranceBeforeUs, Long.MIN_VALUE);
    long l2 = addWithOverflowDefault(paramLong1, toleranceAfterUs, Long.MAX_VALUE);
    int j = 1;
    int i;
    if ((l1 <= paramLong2) && (paramLong2 <= l2)) {
      i = 1;
    } else {
      i = 0;
    }
    if ((l1 > paramLong3) || (paramLong3 > l2)) {
      j = 0;
    }
    if ((i != 0) && (j != 0))
    {
      if (Math.abs(paramLong2 - paramLong1) <= Math.abs(paramLong3 - paramLong1)) {
        return paramLong2;
      }
      return paramLong3;
    }
    if (i != 0) {
      return paramLong2;
    }
    if (j != 0) {
      return paramLong3;
    }
    return l1;
  }
  
  public static long scaleLargeTimestamp(long paramLong1, long paramLong2, long paramLong3)
  {
    boolean bool = paramLong3 < paramLong2;
    if ((!bool) && (paramLong3 % paramLong2 == 0L)) {
      return paramLong1 / (paramLong3 / paramLong2);
    }
    if ((bool) && (paramLong2 % paramLong3 == 0L)) {
      return paramLong1 * (paramLong2 / paramLong3);
    }
    double d = paramLong2 / paramLong3;
    return (paramLong1 * d);
  }
  
  public static long[] scaleLargeTimestamps(List paramList, long paramLong1, long paramLong2)
  {
    long[] arrayOfLong = new long[paramList.size()];
    int j = 0;
    int k = 0;
    int i = 0;
    boolean bool = paramLong2 < paramLong1;
    if ((!bool) && (paramLong2 % paramLong1 == 0L)) {
      paramLong1 = paramLong2 / paramLong1;
    }
    while (i < arrayOfLong.length)
    {
      arrayOfLong[i] = (((Long)paramList.get(i)).longValue() / paramLong1);
      i += 1;
      continue;
      if ((bool) && (paramLong1 % paramLong2 == 0L))
      {
        paramLong1 /= paramLong2;
        i = j;
      }
      while (i < arrayOfLong.length)
      {
        arrayOfLong[i] = (((Long)paramList.get(i)).longValue() * paramLong1);
        i += 1;
        continue;
        double d = paramLong1 / paramLong2;
        i = k;
        while (i < arrayOfLong.length)
        {
          arrayOfLong[i] = ((((Long)paramList.get(i)).longValue() * d));
          i += 1;
        }
      }
    }
    return arrayOfLong;
  }
  
  public static void scaleLargeTimestampsInPlace(long[] paramArrayOfLong, long paramLong1, long paramLong2)
  {
    int j = 0;
    int k = 0;
    int i = 0;
    boolean bool = paramLong2 < paramLong1;
    if ((!bool) && (paramLong2 % paramLong1 == 0L)) {
      paramLong1 = paramLong2 / paramLong1;
    }
    while (i < paramArrayOfLong.length)
    {
      paramArrayOfLong[i] /= paramLong1;
      i += 1;
      continue;
      if ((bool) && (paramLong1 % paramLong2 == 0L))
      {
        paramLong1 /= paramLong2;
        i = j;
      }
      while (i < paramArrayOfLong.length)
      {
        paramArrayOfLong[i] *= paramLong1;
        i += 1;
        continue;
        double d = paramLong1 / paramLong2;
        i = k;
        while (i < paramArrayOfLong.length)
        {
          paramArrayOfLong[i] = ((paramArrayOfLong[i] * d));
          i += 1;
        }
      }
    }
  }
  
  private static boolean shouldEscapeCharacter(char paramChar)
  {
    if ((paramChar != '"') && (paramChar != '%') && (paramChar != '*') && (paramChar != '/') && (paramChar != ':') && (paramChar != '<') && (paramChar != '\\') && (paramChar != '|')) {
      switch (paramChar)
      {
      default: 
        return false;
      }
    }
    return true;
  }
  
  public static void sneakyThrow(Throwable paramThrowable)
  {
    sneakyThrowInternal(paramThrowable);
  }
  
  private static void sneakyThrowInternal(Throwable paramThrowable)
    throws Throwable
  {
    throw paramThrowable;
  }
  
  public static String[] split(String paramString1, String paramString2)
  {
    return paramString1.split(paramString2, -1);
  }
  
  public static String[] splitAtFirst(String paramString1, String paramString2)
  {
    return paramString1.split(paramString2, 2);
  }
  
  public static String[] splitCodecs(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return new String[0];
    }
    return split(paramString.trim(), "(\\s*,\\s*)");
  }
  
  public static ComponentName startForegroundService(Context paramContext, Intent paramIntent)
  {
    if (SDK_INT >= 26) {
      return paramContext.startForegroundService(paramIntent);
    }
    return paramContext.startService(paramIntent);
  }
  
  public static long subtractWithOverflowDefault(long paramLong1, long paramLong2, long paramLong3)
  {
    long l = paramLong1 - paramLong2;
    if (((paramLong1 ^ l) & (paramLong2 ^ paramLong1)) < 0L) {
      return paramLong3;
    }
    return l;
  }
  
  public static int[] toArray(List paramList)
  {
    if (paramList == null) {
      return null;
    }
    int j = paramList.size();
    int[] arrayOfInt = new int[j];
    int i = 0;
    while (i < j)
    {
      arrayOfInt[i] = ((Integer)paramList.get(i)).intValue();
      i += 1;
    }
    return arrayOfInt;
  }
  
  public static byte[] toByteArray(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['?'];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static String toLowerInvariant(String paramString)
  {
    if (paramString == null) {
      return paramString;
    }
    return paramString.toLowerCase(Locale.US);
  }
  
  public static String toUpperInvariant(String paramString)
  {
    if (paramString == null) {
      return paramString;
    }
    return paramString.toUpperCase(Locale.US);
  }
  
  public static String unescapeFileName(String paramString)
  {
    int n = paramString.length();
    int m = 0;
    int j = 0;
    for (int i = 0; j < n; i = k)
    {
      k = i;
      if (paramString.charAt(j) == '%') {
        k = i + 1;
      }
      j += 1;
    }
    if (i == 0) {
      return paramString;
    }
    int k = n - i * 2;
    StringBuilder localStringBuilder = new StringBuilder(k);
    Matcher localMatcher = ESCAPED_CHARACTER_PATTERN.matcher(paramString);
    j = m;
    while ((i > 0) && (localMatcher.find()))
    {
      char c = (char)Integer.parseInt(localMatcher.group(1), 16);
      localStringBuilder.append(paramString, j, localMatcher.start());
      localStringBuilder.append(c);
      j = localMatcher.end();
      i -= 1;
    }
    if (j < n) {
      localStringBuilder.append(paramString, j, n);
    }
    if (localStringBuilder.length() != k) {
      return null;
    }
    return localStringBuilder.toString();
  }
  
  public static void writeBoolean(Parcel paramParcel, boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
}
