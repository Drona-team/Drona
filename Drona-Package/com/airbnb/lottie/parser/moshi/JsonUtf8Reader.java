package com.airbnb.lottie.parser.moshi;

import androidx.annotation.Nullable;
import java.io.EOFException;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

final class JsonUtf8Reader
  extends JsonReader
{
  private static final ByteString CLOSING_BLOCK_COMMENT = ByteString.encodeUtf8("*/");
  private static final ByteString DOUBLE_QUOTE_OR_SLASH;
  private static final ByteString LINEFEED_OR_CARRIAGE_RETURN;
  private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
  private static final int NUMBER_CHAR_DECIMAL = 3;
  private static final int NUMBER_CHAR_DIGIT = 2;
  private static final int NUMBER_CHAR_EXP_DIGIT = 7;
  private static final int NUMBER_CHAR_EXP_E = 5;
  private static final int NUMBER_CHAR_EXP_SIGN = 6;
  private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
  private static final int NUMBER_CHAR_NONE = 0;
  private static final int NUMBER_CHAR_SIGN = 1;
  private static final int PEEKED_BEGIN_ARRAY = 3;
  private static final int PEEKED_BEGIN_OBJECT = 1;
  private static final int PEEKED_BUFFERED = 11;
  private static final int PEEKED_BUFFERED_NAME = 15;
  private static final int PEEKED_DOUBLE_QUOTED = 9;
  private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
  private static final int PEEKED_END_ARRAY = 4;
  private static final int PEEKED_END_OBJECT = 2;
  private static final int PEEKED_EOF = 18;
  private static final int PEEKED_FALSE = 6;
  private static final int PEEKED_LONG = 16;
  private static final int PEEKED_NONE = 0;
  private static final int PEEKED_NULL = 7;
  private static final int PEEKED_NUMBER = 17;
  private static final int PEEKED_SINGLE_QUOTED = 8;
  private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
  private static final int PEEKED_TRUE = 5;
  private static final int PEEKED_UNQUOTED = 10;
  private static final int PEEKED_UNQUOTED_NAME = 14;
  private static final ByteString SINGLE_QUOTE_OR_SLASH = ByteString.encodeUtf8("'\\");
  private static final ByteString UNQUOTED_STRING_TERMINALS;
  private final Buffer buffer;
  private int peeked = 0;
  private long peekedLong;
  private int peekedNumberLength;
  @Nullable
  private String peekedString;
  private final BufferedSource source;
  
  static
  {
    DOUBLE_QUOTE_OR_SLASH = ByteString.encodeUtf8("\"\\");
    UNQUOTED_STRING_TERMINALS = ByteString.encodeUtf8("{}[]:, \n\t\r\f/\\;#=");
    LINEFEED_OR_CARRIAGE_RETURN = ByteString.encodeUtf8("\n\r");
  }
  
  JsonUtf8Reader(BufferedSource paramBufferedSource)
  {
    if (paramBufferedSource != null)
    {
      source = paramBufferedSource;
      buffer = paramBufferedSource.getBuffer();
      pushScope(6);
      return;
    }
    throw new NullPointerException("source == null");
  }
  
  private void checkLenient()
    throws IOException
  {
    if (lenient) {
      return;
    }
    throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
  }
  
  private int doPeek()
    throws IOException
  {
    int i = scopes[(stackSize - 1)];
    if (i == 1)
    {
      scopes[(stackSize - 1)] = 2;
    }
    else if (i == 2)
    {
      j = nextNonWhitespace(true);
      buffer.readByte();
      if (j != 44)
      {
        if (j != 59)
        {
          if (j == 93)
          {
            peeked = 4;
            return 4;
          }
          throw syntaxError("Unterminated array");
        }
        checkLenient();
      }
    }
    else
    {
      if ((i == 3) || (i == 5)) {
        break label489;
      }
      if (i == 4)
      {
        scopes[(stackSize - 1)] = 5;
        j = nextNonWhitespace(true);
        buffer.readByte();
        if (j != 58) {
          if (j == 61)
          {
            checkLenient();
            if ((source.request(1L)) && (buffer.getByte(0L) == 62)) {
              buffer.readByte();
            }
          }
          else
          {
            throw syntaxError("Expected ':'");
          }
        }
      }
      else if (i == 6)
      {
        scopes[(stackSize - 1)] = 7;
      }
      else if (i == 7)
      {
        if (nextNonWhitespace(false) == -1)
        {
          peeked = 18;
          return 18;
        }
        checkLenient();
      }
      else
      {
        if (i == 8) {
          break label479;
        }
      }
    }
    int j = nextNonWhitespace(true);
    if (j != 34)
    {
      if (j != 39)
      {
        if ((j != 44) && (j != 59)) {
          if (j != 91)
          {
            if (j != 93)
            {
              if (j != 123)
              {
                i = peekKeyword();
                if (i != 0) {
                  return i;
                }
                i = peekNumber();
                if (i != 0) {
                  return i;
                }
                if (isLiteral(buffer.getByte(0L)))
                {
                  checkLenient();
                  peeked = 10;
                  return 10;
                }
                throw syntaxError("Expected value");
              }
              buffer.readByte();
              peeked = 1;
              return 1;
            }
            if (i == 1)
            {
              buffer.readByte();
              peeked = 4;
              return 4;
            }
          }
          else
          {
            buffer.readByte();
            peeked = 3;
            return 3;
          }
        }
        if ((i != 1) && (i != 2)) {
          throw syntaxError("Unexpected value");
        }
        checkLenient();
        peeked = 7;
        return 7;
      }
      checkLenient();
      buffer.readByte();
      peeked = 8;
      return 8;
    }
    buffer.readByte();
    peeked = 9;
    return 9;
    label479:
    throw new IllegalStateException("JsonReader is closed");
    label489:
    scopes[(stackSize - 1)] = 4;
    if (i == 5)
    {
      j = nextNonWhitespace(true);
      buffer.readByte();
      if (j != 44)
      {
        if (j != 59)
        {
          if (j == 125)
          {
            peeked = 2;
            return 2;
          }
          throw syntaxError("Unterminated object");
        }
        checkLenient();
      }
    }
    j = nextNonWhitespace(true);
    if (j != 34)
    {
      if (j != 39)
      {
        if (j != 125)
        {
          checkLenient();
          if (isLiteral((char)j))
          {
            peeked = 14;
            return 14;
          }
          throw syntaxError("Expected name");
        }
        if (i != 5)
        {
          buffer.readByte();
          peeked = 2;
          return 2;
        }
        throw syntaxError("Expected name");
      }
      buffer.readByte();
      checkLenient();
      peeked = 12;
      return 12;
    }
    buffer.readByte();
    peeked = 13;
    return 13;
  }
  
  private int findName(String paramString, JsonReader.Options paramOptions)
  {
    int j = strings.length;
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(strings[i]))
      {
        peeked = 0;
        pathNames[(stackSize - 1)] = paramString;
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private int findString(String paramString, JsonReader.Options paramOptions)
  {
    int j = strings.length;
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(strings[i]))
      {
        peeked = 0;
        paramString = pathIndices;
        j = stackSize - 1;
        paramString[j] += 1;
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private boolean isLiteral(int paramInt)
    throws IOException
  {
    switch (paramInt)
    {
    default: 
      return true;
    case 35: 
    case 47: 
    case 59: 
    case 61: 
    case 92: 
      checkLenient();
    }
    return false;
  }
  
  private int nextNonWhitespace(boolean paramBoolean)
    throws IOException
  {
    int j;
    for (int i = 0;; i = j)
    {
      BufferedSource localBufferedSource = source;
      j = i + 1;
      if (!localBufferedSource.request(j)) {
        break label202;
      }
      i = buffer.getByte(i);
      if ((i != 10) && (i != 32) && (i != 13) && (i != 9))
      {
        buffer.skip(j - 1);
        if (i == 47)
        {
          if (!source.request(2L)) {
            return i;
          }
          checkLenient();
          j = buffer.getByte(1L);
          if (j != 42)
          {
            if (j != 47) {
              return i;
            }
            buffer.readByte();
            buffer.readByte();
            skipToEndOfLine();
            break;
          }
          buffer.readByte();
          buffer.readByte();
          if (skipToEndOfBlockComment()) {
            break;
          }
          throw syntaxError("Unterminated comment");
        }
        if (i == 35)
        {
          checkLenient();
          skipToEndOfLine();
          break;
        }
        return i;
      }
    }
    label202:
    if (!paramBoolean) {
      return -1;
    }
    throw new EOFException("End of input");
  }
  
  private String nextQuotedValue(ByteString paramByteString)
    throws IOException
  {
    long l;
    Object localObject2;
    for (Object localObject1 = null;; localObject1 = localObject2)
    {
      l = source.indexOfElement(paramByteString);
      if (l == -1L) {
        break label144;
      }
      if (buffer.getByte(l) != 92) {
        break;
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = new StringBuilder();
      }
      ((StringBuilder)localObject2).append(buffer.readUtf8(l));
      buffer.readByte();
      ((StringBuilder)localObject2).append(readEscapeCharacter());
    }
    if (localObject1 == null)
    {
      paramByteString = buffer.readUtf8(l);
      buffer.readByte();
      return paramByteString;
    }
    localObject1.append(buffer.readUtf8(l));
    buffer.readByte();
    return localObject1.toString();
    label144:
    throw syntaxError("Unterminated string");
  }
  
  private String nextUnquotedValue()
    throws IOException
  {
    long l = source.indexOfElement(UNQUOTED_STRING_TERMINALS);
    if (l != -1L) {
      return buffer.readUtf8(l);
    }
    return buffer.readUtf8();
  }
  
  private int peekKeyword()
    throws IOException
  {
    Object localObject1 = buffer;
    JsonUtf8Reader localJsonUtf8Reader = this;
    int i = ((Buffer)localObject1).getByte(0L);
    Object localObject2;
    if ((i != 116) && (i != 84))
    {
      if ((i != 102) && (i != 70))
      {
        if ((i != 110) && (i != 78)) {
          return 0;
        }
        localObject1 = "null";
        localObject2 = "NULL";
        i = 7;
      }
      else
      {
        localObject1 = "false";
        localObject2 = "FALSE";
        i = 6;
      }
    }
    else
    {
      localObject1 = "true";
      localObject2 = "TRUE";
      i = 5;
    }
    int m = ((String)localObject1).length();
    int k;
    for (int j = 1; j < m; j = k)
    {
      Object localObject3 = source;
      k = j + 1;
      if (!((BufferedSource)localObject3).request(k)) {
        return 0;
      }
      localObject3 = buffer;
      int n = ((Buffer)localObject3).getByte(j);
      if ((n != ((String)localObject1).charAt(j)) && (n != ((String)localObject2).charAt(j))) {
        return 0;
      }
    }
    localObject1 = localJsonUtf8Reader;
    if (source.request(m + 1))
    {
      localObject2 = buffer;
      localObject1 = localJsonUtf8Reader;
      if (localJsonUtf8Reader.isLiteral(((Buffer)localObject2).getByte(m))) {
        return 0;
      }
    }
    buffer.skip(m);
    peeked = i;
    return i;
  }
  
  private int peekNumber()
    throws IOException
  {
    long l1 = 0L;
    int k = 0;
    int i = 0;
    int j = 1;
    int m = 0;
    for (;;)
    {
      BufferedSource localBufferedSource = source;
      int n = k + 1;
      int i1;
      if (localBufferedSource.request(n))
      {
        i1 = buffer.getByte(k);
        if (i1 == 43) {
          break label432;
        }
        if ((i1 == 69) || (i1 == 101)) {
          break label412;
        }
      }
      switch (i1)
      {
      default: 
        if ((i1 >= 48) && (i1 <= 57))
        {
          if ((i != 1) && (i != 0))
          {
            long l2;
            if (i == 2)
            {
              if (l1 == 0L) {
                return 0;
              }
              l2 = 10L * l1 - (i1 - 48);
              k = l1 < -922337203685477580L;
              if ((k <= 0) && ((k != 0) || (l2 >= l1))) {
                k = 0;
              } else {
                k = 1;
              }
              k &= j;
            }
            else
            {
              if (i == 3)
              {
                i = 4;
                break;
              }
              if (i == 5) {
                break label240;
              }
              l2 = l1;
              k = j;
              if (i == 6) {
                break label240;
              }
            }
            l1 = l2;
            j = k;
            break;
            i = 7;
            break;
          }
          l1 = -(i1 - 48);
          i = 2;
          break;
        }
        if (!isLiteral(i1))
        {
          if ((i == 2) && (j != 0) && ((l1 != Long.MIN_VALUE) || (m != 0)) && ((l1 != 0L) || (m == 0)))
          {
            if (m == 0) {
              l1 = -l1;
            }
            peekedLong = l1;
            buffer.skip(k);
            peeked = 16;
            return 16;
          }
          if ((i != 2) && (i != 4) && (i != 7)) {
            return 0;
          }
          peekedNumberLength = k;
          peeked = 17;
          return 17;
        }
        return 0;
      case 46: 
        if (i == 2)
        {
          i = 3;
          break;
        }
        return 0;
      case 45: 
        label240:
        if (i == 0)
        {
          i = 1;
          m = 1;
        }
        else if (i != 5)
        {
          return 0;
          label412:
          if ((i != 2) && (i != 4)) {
            return 0;
          }
          i = 5;
        }
        break;
      }
      label432:
      if (i != 5) {
        break;
      }
      i = 6;
      k = n;
    }
    return 0;
  }
  
  private char readEscapeCharacter()
    throws IOException
  {
    if (source.request(1L))
    {
      int i = buffer.readByte();
      if ((i != 10) && (i != 34) && (i != 39) && (i != 47) && (i != 92))
      {
        if (i != 98)
        {
          if (i != 102)
          {
            if (i != 110)
            {
              if (i != 114)
              {
                StringBuilder localStringBuilder;
                switch (i)
                {
                default: 
                  if (lenient) {
                    return (char)i;
                  }
                  localStringBuilder = new StringBuilder();
                  localStringBuilder.append("Invalid escape sequence: \\");
                  localStringBuilder.append((char)i);
                  throw syntaxError(localStringBuilder.toString());
                case 117: 
                  if (source.request(4L))
                  {
                    i = 0;
                    char c = '\000';
                    while (i < 4)
                    {
                      int j = buffer.getByte(i);
                      int k = (char)(c << '\004');
                      if ((j >= 48) && (j <= 57))
                      {
                        c = (char)(k + (j - 48));
                      }
                      else if ((j >= 97) && (j <= 102))
                      {
                        c = (char)(k + (j - 97 + 10));
                      }
                      else
                      {
                        if ((j < 65) || (j > 70)) {
                          break label275;
                        }
                        c = (char)(k + (j - 65 + 10));
                      }
                      i += 1;
                      continue;
                      label275:
                      localStringBuilder = new StringBuilder();
                      localStringBuilder.append("\\u");
                      localStringBuilder.append(buffer.readUtf8(4L));
                      throw syntaxError(localStringBuilder.toString());
                    }
                    buffer.skip(4L);
                    return c;
                  }
                  localStringBuilder = new StringBuilder();
                  localStringBuilder.append("Unterminated escape sequence at path ");
                  localStringBuilder.append(getPath());
                  throw new EOFException(localStringBuilder.toString());
                }
                return '\t';
              }
              return '\r';
            }
            return '\n';
          }
          return '\f';
        }
        return '\b';
      }
      return (char)i;
    }
    throw syntaxError("Unterminated escape sequence");
  }
  
  private void skipQuotedValue(ByteString paramByteString)
    throws IOException
  {
    long l;
    for (;;)
    {
      l = source.indexOfElement(paramByteString);
      if (l == -1L) {
        break label61;
      }
      if (buffer.getByte(l) != 92) {
        break;
      }
      buffer.skip(l + 1L);
      readEscapeCharacter();
    }
    buffer.skip(l + 1L);
    return;
    label61:
    throw syntaxError("Unterminated string");
  }
  
  private boolean skipToEndOfBlockComment()
    throws IOException
  {
    long l = source.indexOf(CLOSING_BLOCK_COMMENT);
    boolean bool;
    if (l != -1L) {
      bool = true;
    } else {
      bool = false;
    }
    Buffer localBuffer = buffer;
    if (bool) {
      l += CLOSING_BLOCK_COMMENT.size();
    } else {
      l = buffer.size();
    }
    localBuffer.skip(l);
    return bool;
  }
  
  private void skipToEndOfLine()
    throws IOException
  {
    long l = source.indexOfElement(LINEFEED_OR_CARRIAGE_RETURN);
    Buffer localBuffer = buffer;
    if (l != -1L) {
      l += 1L;
    } else {
      l = buffer.size();
    }
    localBuffer.skip(l);
  }
  
  private void skipUnquotedValue()
    throws IOException
  {
    long l2 = source.indexOfElement(UNQUOTED_STRING_TERMINALS);
    long l1 = l2;
    Buffer localBuffer = buffer;
    if (l2 == -1L) {
      l1 = buffer.size();
    }
    localBuffer.skip(l1);
  }
  
  public void beginArray()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 3)
    {
      pushScope(1);
      pathIndices[(stackSize - 1)] = 0;
      peeked = 0;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Expected BEGIN_ARRAY but was ");
    localStringBuilder.append(peek());
    localStringBuilder.append(" at path ");
    localStringBuilder.append(getPath());
    throw new JsonDataException(localStringBuilder.toString());
  }
  
  public void beginObject()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 1)
    {
      pushScope(3);
      peeked = 0;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Expected BEGIN_OBJECT but was ");
    localStringBuilder.append(peek());
    localStringBuilder.append(" at path ");
    localStringBuilder.append(getPath());
    throw new JsonDataException(localStringBuilder.toString());
  }
  
  public void close()
    throws IOException
  {
    peeked = 0;
    scopes[0] = 8;
    stackSize = 1;
    buffer.clear();
    source.close();
  }
  
  public void endArray()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 4)
    {
      stackSize -= 1;
      localObject = pathIndices;
      i = stackSize - 1;
      localObject[i] += 1;
      peeked = 0;
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected END_ARRAY but was ");
    ((StringBuilder)localObject).append(peek());
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public void endObject()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 2)
    {
      stackSize -= 1;
      pathNames[stackSize] = null;
      localObject = pathIndices;
      i = stackSize - 1;
      localObject[i] += 1;
      peeked = 0;
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected END_OBJECT but was ");
    ((StringBuilder)localObject).append(peek());
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public boolean hasNext()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    return (i != 2) && (i != 4) && (i != 18);
  }
  
  public boolean nextBoolean()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 5)
    {
      peeked = 0;
      localObject = pathIndices;
      i = stackSize - 1;
      localObject[i] += 1;
      return true;
    }
    if (i == 6)
    {
      peeked = 0;
      localObject = pathIndices;
      i = stackSize - 1;
      localObject[i] += 1;
      return false;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected a boolean but was ");
    ((StringBuilder)localObject).append(peek());
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public double nextDouble()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 16)
    {
      peeked = 0;
      localObject = pathIndices;
      i = stackSize - 1;
      localObject[i] += 1;
      return peekedLong;
    }
    if (i == 17) {
      peekedString = buffer.readUtf8(peekedNumberLength);
    } else if (i == 9) {
      peekedString = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
    } else if (i == 8) {
      peekedString = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
    } else if (i == 10) {
      peekedString = nextUnquotedValue();
    } else {
      if (i != 11) {
        break label341;
      }
    }
    peeked = 11;
    Object localObject = peekedString;
    try
    {
      double d = Double.parseDouble((String)localObject);
      if ((!lenient) && ((Double.isNaN(d)) || (Double.isInfinite(d))))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("JSON forbids NaN and infinities: ");
        ((StringBuilder)localObject).append(d);
        ((StringBuilder)localObject).append(" at path ");
        ((StringBuilder)localObject).append(getPath());
        throw new JsonEncodingException(((StringBuilder)localObject).toString());
      }
      peekedString = null;
      peeked = 0;
      localObject = pathIndices;
      i = stackSize - 1;
      localObject[i] += 1;
      return d;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected a double but was ");
    ((StringBuilder)localObject).append(peekedString);
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
    label341:
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected a double but was ");
    ((StringBuilder)localObject).append(peek());
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public int nextInt()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 16)
    {
      i = (int)peekedLong;
      if (peekedLong == i)
      {
        peeked = 0;
        localObject = pathIndices;
        j = stackSize - 1;
        localObject[j] += 1;
        return i;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Expected an int but was ");
      ((StringBuilder)localObject).append(peekedLong);
      ((StringBuilder)localObject).append(" at path ");
      ((StringBuilder)localObject).append(getPath());
      throw new JsonDataException(((StringBuilder)localObject).toString());
    }
    if (i == 17)
    {
      peekedString = buffer.readUtf8(peekedNumberLength);
    }
    else if ((i != 9) && (i != 8))
    {
      if (i != 11)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Expected an int but was ");
        ((StringBuilder)localObject).append(peek());
        ((StringBuilder)localObject).append(" at path ");
        ((StringBuilder)localObject).append(getPath());
        throw new JsonDataException(((StringBuilder)localObject).toString());
      }
    }
    else
    {
      if (i == 9) {
        localObject = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
      } else {
        localObject = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
      }
      peekedString = ((String)localObject);
      localObject = peekedString;
    }
    try
    {
      i = Integer.parseInt((String)localObject);
      peeked = 0;
      localObject = pathIndices;
      j = stackSize - 1;
      localObject[j] += 1;
      return i;
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      for (;;) {}
    }
    peeked = 11;
    Object localObject = peekedString;
    try
    {
      double d = Double.parseDouble((String)localObject);
      i = (int)d;
      if (i == d)
      {
        peekedString = null;
        peeked = 0;
        localObject = pathIndices;
        j = stackSize - 1;
        localObject[j] += 1;
        return i;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Expected an int but was ");
      ((StringBuilder)localObject).append(peekedString);
      ((StringBuilder)localObject).append(" at path ");
      ((StringBuilder)localObject).append(getPath());
      throw new JsonDataException(((StringBuilder)localObject).toString());
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      for (;;) {}
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected an int but was ");
    ((StringBuilder)localObject).append(peekedString);
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public String nextName()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 14)
    {
      localObject = nextUnquotedValue();
    }
    else if (i == 13)
    {
      localObject = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
    }
    else if (i == 12)
    {
      localObject = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
    }
    else
    {
      if (i != 15) {
        break label94;
      }
      localObject = peekedString;
    }
    peeked = 0;
    pathNames[(stackSize - 1)] = localObject;
    return localObject;
    label94:
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected a name but was ");
    ((StringBuilder)localObject).append(peek());
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public String nextString()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i == 10)
    {
      localObject = nextUnquotedValue();
    }
    else if (i == 9)
    {
      localObject = nextQuotedValue(DOUBLE_QUOTE_OR_SLASH);
    }
    else if (i == 8)
    {
      localObject = nextQuotedValue(SINGLE_QUOTE_OR_SLASH);
    }
    else if (i == 11)
    {
      localObject = peekedString;
      peekedString = null;
    }
    else if (i == 16)
    {
      localObject = Long.toString(peekedLong);
    }
    else
    {
      if (i != 17) {
        break label149;
      }
      localObject = buffer.readUtf8(peekedNumberLength);
    }
    peeked = 0;
    int[] arrayOfInt = pathIndices;
    i = stackSize - 1;
    arrayOfInt[i] += 1;
    return localObject;
    label149:
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Expected a string but was ");
    ((StringBuilder)localObject).append(peek());
    ((StringBuilder)localObject).append(" at path ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public JsonReader.Token peek()
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    switch (i)
    {
    default: 
      throw new AssertionError();
    case 18: 
      return JsonReader.Token.END_DOCUMENT;
    case 16: 
    case 17: 
      return JsonReader.Token.NUMBER;
    case 12: 
    case 13: 
    case 14: 
    case 15: 
      return JsonReader.Token.NAME;
    case 8: 
    case 9: 
    case 10: 
    case 11: 
      return JsonReader.Token.STRING;
    case 7: 
      return JsonReader.Token.NULL;
    case 5: 
    case 6: 
      return JsonReader.Token.BOOLEAN;
    case 4: 
      return JsonReader.Token.END_ARRAY;
    case 3: 
      return JsonReader.Token.BEGIN_ARRAY;
    case 2: 
      return JsonReader.Token.END_OBJECT;
    }
    return JsonReader.Token.BEGIN_OBJECT;
  }
  
  public int selectName(JsonReader.Options paramOptions)
    throws IOException
  {
    int j = peeked;
    int i = j;
    if (j == 0) {
      i = doPeek();
    }
    if (i >= 12)
    {
      if (i > 15) {
        return -1;
      }
      if (i == 15) {
        return findName(peekedString, paramOptions);
      }
      i = source.select(doubleQuoteSuffix);
      if (i != -1)
      {
        peeked = 0;
        pathNames[(stackSize - 1)] = strings[i];
        return i;
      }
      String str1 = pathNames[(stackSize - 1)];
      String str2 = nextName();
      i = findName(str2, paramOptions);
      if (i == -1)
      {
        peeked = 15;
        peekedString = str2;
        pathNames[(stackSize - 1)] = str1;
        return i;
      }
    }
    else
    {
      return -1;
    }
    return i;
  }
  
  public void skipName()
    throws IOException
  {
    if (!failOnUnknown)
    {
      int j = peeked;
      int i = j;
      if (j == 0) {
        i = doPeek();
      }
      if (i == 14) {
        skipUnquotedValue();
      } else if (i == 13) {
        skipQuotedValue(DOUBLE_QUOTE_OR_SLASH);
      } else if (i == 12) {
        skipQuotedValue(SINGLE_QUOTE_OR_SLASH);
      } else {
        if (i != 15) {
          break label94;
        }
      }
      peeked = 0;
      pathNames[(stackSize - 1)] = "null";
      return;
      label94:
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Expected a name but was ");
      localStringBuilder.append(peek());
      localStringBuilder.append(" at path ");
      localStringBuilder.append(getPath());
      throw new JsonDataException(localStringBuilder.toString());
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Cannot skip unexpected ");
    localStringBuilder.append(peek());
    localStringBuilder.append(" at ");
    localStringBuilder.append(getPath());
    throw new JsonDataException(localStringBuilder.toString());
  }
  
  public void skipValue()
    throws IOException
  {
    if (!failOnUnknown)
    {
      int j = 0;
      do
      {
        int k = peeked;
        i = k;
        if (k == 0) {
          i = doPeek();
        }
        if (i == 3)
        {
          pushScope(1);
          i = j + 1;
        }
        else if (i == 1)
        {
          pushScope(3);
          i = j + 1;
        }
        else if (i == 4)
        {
          i = j - 1;
          if (i >= 0)
          {
            stackSize -= 1;
          }
          else
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("Expected a value but was ");
            ((StringBuilder)localObject).append(peek());
            ((StringBuilder)localObject).append(" at path ");
            ((StringBuilder)localObject).append(getPath());
            throw new JsonDataException(((StringBuilder)localObject).toString());
          }
        }
        else if (i == 2)
        {
          i = j - 1;
          if (i >= 0)
          {
            stackSize -= 1;
          }
          else
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("Expected a value but was ");
            ((StringBuilder)localObject).append(peek());
            ((StringBuilder)localObject).append(" at path ");
            ((StringBuilder)localObject).append(getPath());
            throw new JsonDataException(((StringBuilder)localObject).toString());
          }
        }
        else if ((i != 14) && (i != 10))
        {
          if ((i != 9) && (i != 13))
          {
            if ((i != 8) && (i != 12))
            {
              if (i == 17)
              {
                buffer.skip(peekedNumberLength);
                i = j;
              }
              else if (i != 18)
              {
                i = j;
              }
              else
              {
                localObject = new StringBuilder();
                ((StringBuilder)localObject).append("Expected a value but was ");
                ((StringBuilder)localObject).append(peek());
                ((StringBuilder)localObject).append(" at path ");
                ((StringBuilder)localObject).append(getPath());
                throw new JsonDataException(((StringBuilder)localObject).toString());
              }
            }
            else
            {
              skipQuotedValue(SINGLE_QUOTE_OR_SLASH);
              i = j;
            }
          }
          else
          {
            skipQuotedValue(DOUBLE_QUOTE_OR_SLASH);
            i = j;
          }
        }
        else
        {
          skipUnquotedValue();
          i = j;
        }
        peeked = 0;
        j = i;
      } while (i != 0);
      localObject = pathIndices;
      int i = stackSize - 1;
      localObject[i] += 1;
      pathNames[(stackSize - 1)] = "null";
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Cannot skip unexpected ");
    ((StringBuilder)localObject).append(peek());
    ((StringBuilder)localObject).append(" at ");
    ((StringBuilder)localObject).append(getPath());
    throw new JsonDataException(((StringBuilder)localObject).toString());
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("JsonReader(");
    localStringBuilder.append(source);
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
}
