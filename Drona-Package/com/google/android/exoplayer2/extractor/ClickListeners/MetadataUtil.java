package com.google.android.exoplayer2.extractor.ClickListeners;

import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.configurations.ApicFrame;
import com.google.android.exoplayer2.metadata.configurations.CommentFrame;
import com.google.android.exoplayer2.metadata.configurations.Id3Frame;
import com.google.android.exoplayer2.metadata.configurations.InternalFrame;
import com.google.android.exoplayer2.metadata.configurations.TextInformationFrame;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class MetadataUtil
{
  private static final String LANGUAGE_UNDEFINED = "und";
  private static final String PAGE_KEY = "MetadataUtil";
  private static final int PICTURE_TYPE_FRONT_COVER = 3;
  private static final int SHORT_TYPE_ALBUM;
  private static final int SHORT_TYPE_ARTIST;
  private static final int SHORT_TYPE_COMMENT;
  private static final int SHORT_TYPE_COMPOSER_1;
  private static final int SHORT_TYPE_COMPOSER_2;
  private static final int SHORT_TYPE_ENCODER;
  private static final int SHORT_TYPE_GENRE;
  private static final int SHORT_TYPE_LYRICS;
  private static final int SHORT_TYPE_NAME_1 = Util.getIntegerCodeForString("nam");
  private static final int SHORT_TYPE_NAME_2 = Util.getIntegerCodeForString("trk");
  private static final int SHORT_TYPE_YEAR;
  private static final String[] STANDARD_GENRES = { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop" };
  private static final int TYPE_ALBUM_ARTIST;
  private static final int TYPE_COMPILATION;
  private static final int TYPE_COVER_ART;
  private static final int TYPE_DISK_NUMBER;
  private static final int TYPE_GAPLESS_ALBUM;
  private static final int TYPE_GENRE;
  private static final int TYPE_GROUPING;
  private static final int TYPE_INTERNAL;
  private static final int TYPE_RATING;
  private static final int TYPE_SORT_ALBUM;
  private static final int TYPE_SORT_ALBUM_ARTIST;
  private static final int TYPE_SORT_ARTIST;
  private static final int TYPE_SORT_COMPOSER;
  private static final int TYPE_SORT_TRACK_NAME;
  private static final int TYPE_TEMPO;
  private static final int TYPE_TRACK_NUMBER;
  private static final int TYPE_TV_SHOW;
  private static final int TYPE_TV_SORT_SHOW;
  
  static
  {
    SHORT_TYPE_COMMENT = Util.getIntegerCodeForString("cmt");
    SHORT_TYPE_YEAR = Util.getIntegerCodeForString("day");
    SHORT_TYPE_ARTIST = Util.getIntegerCodeForString("ART");
    SHORT_TYPE_ENCODER = Util.getIntegerCodeForString("too");
    SHORT_TYPE_ALBUM = Util.getIntegerCodeForString("alb");
    SHORT_TYPE_COMPOSER_1 = Util.getIntegerCodeForString("com");
    SHORT_TYPE_COMPOSER_2 = Util.getIntegerCodeForString("wrt");
    SHORT_TYPE_LYRICS = Util.getIntegerCodeForString("lyr");
    SHORT_TYPE_GENRE = Util.getIntegerCodeForString("gen");
    TYPE_COVER_ART = Util.getIntegerCodeForString("covr");
    TYPE_GENRE = Util.getIntegerCodeForString("gnre");
    TYPE_GROUPING = Util.getIntegerCodeForString("grp");
    TYPE_DISK_NUMBER = Util.getIntegerCodeForString("disk");
    TYPE_TRACK_NUMBER = Util.getIntegerCodeForString("trkn");
    TYPE_TEMPO = Util.getIntegerCodeForString("tmpo");
    TYPE_COMPILATION = Util.getIntegerCodeForString("cpil");
    TYPE_ALBUM_ARTIST = Util.getIntegerCodeForString("aART");
    TYPE_SORT_TRACK_NAME = Util.getIntegerCodeForString("sonm");
    TYPE_SORT_ALBUM = Util.getIntegerCodeForString("soal");
    TYPE_SORT_ARTIST = Util.getIntegerCodeForString("soar");
    TYPE_SORT_ALBUM_ARTIST = Util.getIntegerCodeForString("soaa");
    TYPE_SORT_COMPOSER = Util.getIntegerCodeForString("soco");
    TYPE_RATING = Util.getIntegerCodeForString("rtng");
    TYPE_GAPLESS_ALBUM = Util.getIntegerCodeForString("pgap");
    TYPE_TV_SORT_SHOW = Util.getIntegerCodeForString("sosn");
    TYPE_TV_SHOW = Util.getIntegerCodeForString("tvsh");
    TYPE_INTERNAL = Util.getIntegerCodeForString("----");
  }
  
  private MetadataUtil() {}
  
  private static CommentFrame parseCommentAttribute(int paramInt, ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readInt();
    if (paramParsableByteArray.readInt() == Atom.TYPE_data)
    {
      paramParsableByteArray.skipBytes(8);
      paramParsableByteArray = paramParsableByteArray.readNullTerminatedString(i - 16);
      return new CommentFrame("und", paramParsableByteArray, paramParsableByteArray);
    }
    paramParsableByteArray = new StringBuilder();
    paramParsableByteArray.append("Failed to parse comment attribute: ");
    paramParsableByteArray.append(Atom.getAtomTypeString(paramInt));
    Log.w("MetadataUtil", paramParsableByteArray.toString());
    return null;
  }
  
  private static ApicFrame parseCoverArt(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readInt();
    if (paramParsableByteArray.readInt() == Atom.TYPE_data)
    {
      int j = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
      String str;
      if (j == 13) {
        str = "image/jpeg";
      } else if (j == 14) {
        str = "image/png";
      } else {
        str = null;
      }
      if (str == null)
      {
        paramParsableByteArray = new StringBuilder();
        paramParsableByteArray.append("Unrecognized cover art flags: ");
        paramParsableByteArray.append(j);
        Log.w("MetadataUtil", paramParsableByteArray.toString());
        return null;
      }
      paramParsableByteArray.skipBytes(4);
      byte[] arrayOfByte = new byte[i - 16];
      paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
      return new ApicFrame(str, null, 3, arrayOfByte);
    }
    Log.w("MetadataUtil", "Failed to parse cover art attribute");
    return null;
  }
  
  public static Metadata.Entry parseIlstElement(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.getPosition() + paramParsableByteArray.readInt();
    int j = paramParsableByteArray.readInt();
    int k = j >> 24 & 0xFF;
    if ((k != 169) && (k != 65533)) {}
    try
    {
      k = TYPE_GENRE;
      if (j == k)
      {
        localObject = parseStandardGenreAttribute(paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_DISK_NUMBER;
      if (j == k)
      {
        localObject = parseIndexAndCountAttribute(j, "TPOS", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_TRACK_NUMBER;
      if (j == k)
      {
        localObject = parseIndexAndCountAttribute(j, "TRCK", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_TEMPO;
      if (j == k)
      {
        localObject = parseUint8Attribute(j, "TBPM", paramParsableByteArray, true, false);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_COMPILATION;
      if (j == k)
      {
        localObject = parseUint8Attribute(j, "TCMP", paramParsableByteArray, true, true);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_COVER_ART;
      if (j == k)
      {
        localObject = parseCoverArt(paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_ALBUM_ARTIST;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TPE2", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_SORT_TRACK_NAME;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TSOT", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_SORT_ALBUM;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TSO2", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_SORT_ARTIST;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TSOA", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_SORT_ALBUM_ARTIST;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TSOP", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_SORT_COMPOSER;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TSOC", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_RATING;
      if (j == k)
      {
        localObject = parseUint8Attribute(j, "ITUNESADVISORY", paramParsableByteArray, false, false);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_GAPLESS_ALBUM;
      if (j == k)
      {
        localObject = parseUint8Attribute(j, "ITUNESGAPLESS", paramParsableByteArray, false, true);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_TV_SORT_SHOW;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TVSHOWSORT", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_TV_SHOW;
      if (j == k)
      {
        localObject = parseTextAttribute(j, "TVSHOW", paramParsableByteArray);
        paramParsableByteArray.setPosition(i);
        return localObject;
      }
      k = TYPE_INTERNAL;
      if (j == k)
      {
        localObject = parseInternalAttribute(paramParsableByteArray, i);
        paramParsableByteArray.setPosition(i);
        return localObject;
        k = 0xFFFFFF & j;
        int m = SHORT_TYPE_COMMENT;
        if (k == m)
        {
          localObject = parseCommentAttribute(j, paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
        m = SHORT_TYPE_NAME_1;
        if (k == m) {
          break label845;
        }
        m = SHORT_TYPE_NAME_2;
        if (k == m) {
          break label845;
        }
        m = SHORT_TYPE_COMPOSER_1;
        if (k == m) {
          break label827;
        }
        m = SHORT_TYPE_COMPOSER_2;
        if (k == m) {
          break label827;
        }
        m = SHORT_TYPE_YEAR;
        if (k == m)
        {
          localObject = parseTextAttribute(j, "TDRC", paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
        m = SHORT_TYPE_ARTIST;
        if (k == m)
        {
          localObject = parseTextAttribute(j, "TPE1", paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
        m = SHORT_TYPE_ENCODER;
        if (k == m)
        {
          localObject = parseTextAttribute(j, "TSSE", paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
        m = SHORT_TYPE_ALBUM;
        if (k == m)
        {
          localObject = parseTextAttribute(j, "TALB", paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
        m = SHORT_TYPE_LYRICS;
        if (k == m)
        {
          localObject = parseTextAttribute(j, "USLT", paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
        m = SHORT_TYPE_GENRE;
        if (k == m)
        {
          localObject = parseTextAttribute(j, "TCON", paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
        m = TYPE_GROUPING;
        if (k == m)
        {
          localObject = parseTextAttribute(j, "TIT1", paramParsableByteArray);
          paramParsableByteArray.setPosition(i);
          return localObject;
        }
      }
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Skipped unknown metadata entry: ");
      ((StringBuilder)localObject).append(Atom.getAtomTypeString(j));
      Log.d("MetadataUtil", ((StringBuilder)localObject).toString());
      paramParsableByteArray.setPosition(i);
      return null;
      label827:
      localObject = parseTextAttribute(j, "TCOM", paramParsableByteArray);
      paramParsableByteArray.setPosition(i);
      return localObject;
      label845:
      localObject = parseTextAttribute(j, "TIT2", paramParsableByteArray);
      paramParsableByteArray.setPosition(i);
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      paramParsableByteArray.setPosition(i);
      throw localThrowable;
    }
  }
  
  private static TextInformationFrame parseIndexAndCountAttribute(int paramInt, String paramString, ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readInt();
    if ((paramParsableByteArray.readInt() == Atom.TYPE_data) && (i >= 22))
    {
      paramParsableByteArray.skipBytes(10);
      i = paramParsableByteArray.readUnsignedShort();
      if (i > 0)
      {
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append("");
        ((StringBuilder)localObject).append(i);
        String str = ((StringBuilder)localObject).toString();
        localObject = str;
        paramInt = paramParsableByteArray.readUnsignedShort();
        if (paramInt > 0)
        {
          paramParsableByteArray = new StringBuilder();
          paramParsableByteArray.append(str);
          paramParsableByteArray.append("/");
          paramParsableByteArray.append(paramInt);
          localObject = paramParsableByteArray.toString();
        }
        return new TextInformationFrame(paramString, null, (String)localObject);
      }
    }
    paramString = new StringBuilder();
    paramString.append("Failed to parse index/count attribute: ");
    paramString.append(Atom.getAtomTypeString(paramInt));
    Log.w("MetadataUtil", paramString.toString());
    return null;
  }
  
  private static Id3Frame parseInternalAttribute(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    String str2 = null;
    String str1 = null;
    int j = -1;
    int i = -1;
    while (paramParsableByteArray.getPosition() < paramInt)
    {
      int m = paramParsableByteArray.getPosition();
      int k = paramParsableByteArray.readInt();
      int n = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      if (n == Atom.TYPE_mean)
      {
        str2 = paramParsableByteArray.readNullTerminatedString(k - 12);
      }
      else if (n == Atom.TYPE_name)
      {
        str1 = paramParsableByteArray.readNullTerminatedString(k - 12);
      }
      else
      {
        if (n == Atom.TYPE_data)
        {
          j = m;
          i = k;
        }
        paramParsableByteArray.skipBytes(k - 12);
      }
    }
    if ((str2 != null) && (str1 != null))
    {
      if (j == -1) {
        return null;
      }
      paramParsableByteArray.setPosition(j);
      paramParsableByteArray.skipBytes(16);
      return new InternalFrame(str2, str1, paramParsableByteArray.readNullTerminatedString(i - 16));
    }
    return null;
  }
  
  private static TextInformationFrame parseStandardGenreAttribute(ParsableByteArray paramParsableByteArray)
  {
    int i = parseUint8AttributeValue(paramParsableByteArray);
    if ((i > 0) && (i <= STANDARD_GENRES.length)) {
      paramParsableByteArray = STANDARD_GENRES[(i - 1)];
    } else {
      paramParsableByteArray = null;
    }
    if (paramParsableByteArray != null) {
      return new TextInformationFrame("TCON", null, paramParsableByteArray);
    }
    Log.w("MetadataUtil", "Failed to parse standard genre code");
    return null;
  }
  
  private static TextInformationFrame parseTextAttribute(int paramInt, String paramString, ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readInt();
    if (paramParsableByteArray.readInt() == Atom.TYPE_data)
    {
      paramParsableByteArray.skipBytes(8);
      return new TextInformationFrame(paramString, null, paramParsableByteArray.readNullTerminatedString(i - 16));
    }
    paramString = new StringBuilder();
    paramString.append("Failed to parse text attribute: ");
    paramString.append(Atom.getAtomTypeString(paramInt));
    Log.w("MetadataUtil", paramString.toString());
    return null;
  }
  
  private static Id3Frame parseUint8Attribute(int paramInt, String paramString, ParsableByteArray paramParsableByteArray, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = parseUint8AttributeValue(paramParsableByteArray);
    int i = j;
    if (paramBoolean2) {
      i = Math.min(1, j);
    }
    if (i >= 0)
    {
      if (paramBoolean1) {
        return new TextInformationFrame(paramString, null, Integer.toString(i));
      }
      return new CommentFrame("und", paramString, Integer.toString(i));
    }
    paramString = new StringBuilder();
    paramString.append("Failed to parse uint8 attribute: ");
    paramString.append(Atom.getAtomTypeString(paramInt));
    Log.w("MetadataUtil", paramString.toString());
    return null;
  }
  
  private static int parseUint8AttributeValue(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.skipBytes(4);
    if (paramParsableByteArray.readInt() == Atom.TYPE_data)
    {
      paramParsableByteArray.skipBytes(8);
      return paramParsableByteArray.readUnsignedByte();
    }
    Log.w("MetadataUtil", "Failed to parse uint8 attribute value");
    return -1;
  }
}
