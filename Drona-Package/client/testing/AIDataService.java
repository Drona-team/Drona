package client.testing;

import client.testing.model.AIRequest;
import client.testing.model.Entity;
import client.testing.model.QuestionMetadata;
import client.testing.model.Status;
import client.testing.util.IOUtils;
import client.testing.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AIDataService
{
  private static final String DEFAULT_REQUEST_METHOD = "POST";
  private static final Gson GSON = GsonFactory.getDefaultFactory().getGson();
  private static final String REQUEST_METHOD_DELETE = "DELETE";
  private static final String REQUEST_METHOD_GET = "GET";
  private static final String REQUEST_METHOD_POST = "POST";
  private static final AIServiceContext UNDEFINED_SERVICE_CONTEXT;
  private static final Logger logger = LogManager.getLogger(ai.api.AIDataService.class);
  private final AIConfiguration config;
  private final AIServiceContext defaultServiceContext;
  
  static
  {
    UNDEFINED_SERVICE_CONTEXT = null;
  }
  
  public AIDataService(AIConfiguration paramAIConfiguration)
  {
    this(paramAIConfiguration, null);
  }
  
  public AIDataService(AIConfiguration paramAIConfiguration, AIServiceContext paramAIServiceContext)
  {
    if (paramAIConfiguration != null)
    {
      config = paramAIConfiguration.clone();
      if (paramAIServiceContext == null)
      {
        defaultServiceContext = new AIServiceContextBuilder().generateSessionId().build();
        return;
      }
      defaultServiceContext = paramAIServiceContext;
      return;
    }
    throw new IllegalArgumentException("config should not be null");
  }
  
  private void fillRequest(AIRequest paramAIRequest, RequestExtras paramRequestExtras)
  {
    if (paramRequestExtras.hasContexts()) {
      paramAIRequest.setContexts(paramRequestExtras.getContexts());
    }
    if (paramRequestExtras.hasEntities()) {
      paramAIRequest.setEntities(paramRequestExtras.getEntities());
    }
    if (paramRequestExtras.getLocation() != null) {
      paramAIRequest.setLocation(paramRequestExtras.getLocation());
    }
  }
  
  private String getSessionId(AIServiceContext paramAIServiceContext)
  {
    if (paramAIServiceContext != null) {
      return paramAIServiceContext.getSessionId();
    }
    return defaultServiceContext.getSessionId();
  }
  
  private String getTimeZone(AIServiceContext paramAIServiceContext)
  {
    if (paramAIServiceContext != null) {
      paramAIServiceContext = paramAIServiceContext.getTimeZone();
    } else {
      paramAIServiceContext = defaultServiceContext.getTimeZone();
    }
    if (paramAIServiceContext == null) {
      paramAIServiceContext = Calendar.getInstance().getTimeZone();
    }
    return paramAIServiceContext.getID();
  }
  
  public String addActiveContext(client.testing.model.AIContext paramAIContext)
    throws AIServiceException
  {
    return addActiveContext(paramAIContext, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public String addActiveContext(client.testing.model.AIContext paramAIContext, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    AIConfiguration localAIConfiguration = config;
    try
    {
      paramAIContext = doRequest(paramAIContext, ai.api.AIDataService.ApiActiveContextNamesResponse.class, localAIConfiguration.getContextsUrl(getSessionId(paramAIServiceContext)), "POST");
      paramAIContext = (ApiActiveContextNamesResponse)paramAIContext;
      if (names != null)
      {
        paramAIServiceContext = names;
        int i = paramAIServiceContext.size();
        if (i > 0)
        {
          paramAIContext = names;
          paramAIContext = paramAIContext.get(0);
          return (String)paramAIContext;
        }
      }
      return null;
    }
    catch (BadResponseStatusException paramAIContext)
    {
      throw new AIServiceException(response);
    }
  }
  
  public List addActiveContext(Iterable paramIterable)
    throws AIServiceException
  {
    return addActiveContext(paramIterable, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public List addActiveContext(Iterable paramIterable, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    AIConfiguration localAIConfiguration = config;
    try
    {
      paramIterable = doRequest(paramIterable, ai.api.AIDataService.ApiActiveContextNamesResponse.class, localAIConfiguration.getContextsUrl(getSessionId(paramAIServiceContext)), "POST");
      return names;
    }
    catch (BadResponseStatusException paramIterable)
    {
      throw new AIServiceException(response);
    }
  }
  
  protected Object doRequest(Object paramObject, Type paramType, String paramString1, String paramString2)
    throws AIServiceException, AIDataService.BadResponseStatusException
  {
    return doRequest(paramObject, paramType, paramString1, paramString2, null);
  }
  
  protected Object doRequest(Object paramObject, Type paramType, String paramString1, String paramString2, Map paramMap)
    throws AIServiceException, AIDataService.BadResponseStatusException
  {
    Object localObject4 = null;
    Object localObject3 = null;
    Object localObject1 = localObject3;
    Object localObject2 = localObject4;
    for (;;)
    {
      try
      {
        URL localURL = new URL(paramString1);
        if (paramObject != null)
        {
          paramString1 = GSON;
          localObject1 = localObject3;
          localObject2 = localObject4;
          paramString1 = paramString1.toJson(paramObject);
        }
        else
        {
          paramString1 = null;
        }
        if (paramString2 == null) {
          paramString2 = "POST";
        }
        localObject1 = localObject3;
        paramObject = logger;
        localObject1 = localObject3;
        localObject2 = localObject4;
        StringBuilder localStringBuilder = new StringBuilder();
        localObject1 = localObject3;
        localObject2 = localObject4;
        localStringBuilder.append("Request json: ");
        localObject1 = localObject3;
        localObject2 = localObject4;
        localStringBuilder.append(paramString1);
        localObject1 = localObject3;
        localObject2 = localObject4;
        paramObject.debug(localStringBuilder.toString());
        paramObject = config;
        localObject1 = localObject3;
        localObject2 = localObject4;
        paramObject = paramObject.getProxy();
        if (paramObject != null)
        {
          localObject1 = localObject3;
          paramObject = config;
          localObject1 = localObject3;
          localObject2 = localObject4;
          paramObject = localURL.openConnection(paramObject.getProxy());
          localObject1 = localObject3;
          paramObject = (HttpURLConnection)paramObject;
        }
        else
        {
          localObject1 = localObject3;
          localObject2 = localObject4;
          paramObject = localURL.openConnection();
          localObject1 = localObject3;
          paramObject = (HttpURLConnection)paramObject;
        }
        if (paramString1 != null)
        {
          localObject1 = paramObject;
          localObject2 = paramObject;
          bool = "POST".equals(paramString2);
          if (!bool)
          {
            localObject1 = paramObject;
            localObject2 = paramObject;
            paramObject = new AIServiceException("Non-empty request should be sent using POST method");
            throw paramObject;
          }
        }
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramObject.setRequestMethod(paramString2);
        localObject1 = paramObject;
        localObject2 = paramObject;
        boolean bool = "POST".equals(paramString2);
        if (bool)
        {
          localObject1 = paramObject;
          localObject2 = paramObject;
          paramObject.setDoOutput(true);
        }
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramString2 = new StringBuilder();
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramString2.append("Bearer ");
        localObject3 = config;
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramString2.append(((AIConfiguration)localObject3).getApiKey());
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramObject.addRequestProperty("Authorization", paramString2.toString());
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramObject.addRequestProperty("Content-Type", "application/json; charset=utf-8");
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramObject.addRequestProperty("Accept", "application/json");
        if (paramMap != null)
        {
          localObject1 = paramObject;
          localObject2 = paramObject;
          paramString2 = paramMap.entrySet().iterator();
          localObject1 = paramObject;
          localObject2 = paramObject;
          bool = paramString2.hasNext();
          if (bool)
          {
            localObject1 = paramObject;
            localObject2 = paramObject;
            paramMap = paramString2.next();
            localObject1 = paramObject;
            localObject3 = (Map.Entry)paramMap;
            localObject1 = paramObject;
            localObject2 = paramObject;
            paramMap = ((Map.Entry)localObject3).getKey();
            localObject1 = paramObject;
            paramMap = (String)paramMap;
            localObject1 = paramObject;
            localObject2 = paramObject;
            localObject3 = ((Map.Entry)localObject3).getValue();
            localObject3 = (String)localObject3;
            localObject1 = paramObject;
            localObject2 = paramObject;
            paramObject.addRequestProperty(paramMap, (String)localObject3);
            continue;
          }
        }
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramObject.connect();
        if (paramString1 != null)
        {
          localObject1 = paramObject;
          localObject2 = paramObject;
          paramString2 = new BufferedOutputStream(paramObject.getOutputStream());
          localObject1 = paramObject;
          localObject2 = paramObject;
          IOUtils.writeAll(paramString1, paramString2);
          localObject1 = paramObject;
          localObject2 = paramObject;
          paramString2.close();
        }
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramString2 = new BufferedInputStream(paramObject.getInputStream());
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramString1 = IOUtils.readAll(paramString2);
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramString2.close();
        localObject1 = paramObject;
        paramString2 = GSON;
        localObject1 = paramObject;
        localObject2 = paramObject;
      }
      catch (Throwable paramObject) {}catch (IOException paramObject)
      {
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          try
          {
            paramType = localObject2.getErrorStream();
            if (paramType != null)
            {
              localObject1 = localObject2;
              paramType = IOUtils.readAll(paramType);
              localObject1 = localObject2;
              paramString1 = logger;
              localObject1 = localObject2;
              paramString1.debug(paramType);
              localObject1 = localObject2;
              paramType = new AIServiceException(paramType, paramObject);
              localObject1 = localObject2;
              throw paramType;
            }
            localObject1 = localObject2;
            paramType = new AIServiceException("Can't connect to the api.ai service.", paramObject);
            localObject1 = localObject2;
            throw paramType;
          }
          catch (IOException paramType)
          {
            localObject1 = localObject2;
            logger.warn("Can't read error response", paramType);
          }
        }
        localObject1 = localObject2;
        logger.error("Can't make request to the API.AI service. Please, check connection settings and API access token.", paramObject);
        localObject1 = localObject2;
        throw new AIServiceException("Can't make request to the API.AI service. Please, check connection settings and API access token.", paramObject);
      }
      try
      {
        paramString2 = paramString2.fromJson(paramString1, ai.api.model.AIResponse.class);
        paramString2 = (client.testing.model.AIResponse)paramString2;
        localObject1 = paramObject;
        localObject2 = paramObject;
        paramMap = paramString2.getStatus();
        if (paramMap != null)
        {
          localObject1 = paramObject;
          localObject2 = paramObject;
          int i = paramString2.getStatus().getCode().intValue();
          if (i != 200)
          {
            localObject1 = paramObject;
            localObject2 = paramObject;
            paramString2 = new BadResponseStatusException(paramString2);
            localObject1 = paramObject;
            throw paramString2;
          }
        }
      }
      catch (JsonParseException paramString2) {}
    }
    paramString2 = GSON;
    localObject1 = paramObject;
    localObject2 = paramObject;
    paramType = paramString2.fromJson(paramString1, paramType);
    if (paramObject != null)
    {
      paramObject.disconnect();
      return paramType;
      if (localObject1 != null) {
        localObject1.disconnect();
      }
      throw paramObject;
    }
    return paramType;
  }
  
  protected Object doRequest(Type paramType, String paramString1, String paramString2)
    throws AIServiceException, AIDataService.BadResponseStatusException
  {
    return doRequest(paramType, paramString1, paramString2, null);
  }
  
  protected Object doRequest(Type paramType, String paramString1, String paramString2, Map paramMap)
    throws AIServiceException, AIDataService.BadResponseStatusException
  {
    return doRequest(null, paramType, paramString1, paramString2, paramMap);
  }
  
  protected String doSoundRequest(InputStream paramInputStream, String paramString)
    throws MalformedURLException, AIServiceException
  {
    return doSoundRequest(paramInputStream, paramString, null, UNDEFINED_SERVICE_CONTEXT);
  }
  
  protected String doSoundRequest(InputStream paramInputStream, String paramString, Map paramMap)
    throws MalformedURLException, AIServiceException
  {
    return doSoundRequest(paramInputStream, paramString, paramMap, UNDEFINED_SERVICE_CONTEXT);
  }
  
  /* Error */
  protected String doSoundRequest(InputStream paramInputStream, String paramString, Map paramMap, AIServiceContext paramAIServiceContext)
    throws MalformedURLException, AIServiceException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_0
    //   4: getfield 75	client/testing/AIDataService:config	Lclient/testing/AIConfiguration;
    //   7: astore 6
    //   9: new 213	java/net/URL
    //   12: dup
    //   13: aload 6
    //   15: aload_0
    //   16: aload 4
    //   18: invokespecial 167	client/testing/AIDataService:getSessionId	(Lclient/testing/AIServiceContext;)Ljava/lang/String;
    //   21: invokevirtual 412	client/testing/AIConfiguration:getQuestionUrl	(Ljava/lang/String;)Ljava/lang/String;
    //   24: invokespecial 214	java/net/URL:<init>	(Ljava/lang/String;)V
    //   27: astore 4
    //   29: getstatic 45	client/testing/AIDataService:logger	Lorg/apache/logging/log4j/Logger;
    //   32: astore 6
    //   34: aload 6
    //   36: ldc_w 414
    //   39: iconst_1
    //   40: anewarray 4	java/lang/Object
    //   43: dup
    //   44: iconst_0
    //   45: aload 4
    //   47: aastore
    //   48: invokeinterface 417 3 0
    //   53: aload_0
    //   54: getfield 75	client/testing/AIDataService:config	Lclient/testing/AIConfiguration;
    //   57: astore 6
    //   59: aload 6
    //   61: invokevirtual 241	client/testing/AIConfiguration:getProxy	()Ljava/net/Proxy;
    //   64: astore 6
    //   66: aload 6
    //   68: ifnull +31 -> 99
    //   71: aload_0
    //   72: getfield 75	client/testing/AIDataService:config	Lclient/testing/AIConfiguration;
    //   75: astore 6
    //   77: aload 4
    //   79: aload 6
    //   81: invokevirtual 241	client/testing/AIConfiguration:getProxy	()Ljava/net/Proxy;
    //   84: invokevirtual 245	java/net/URL:openConnection	(Ljava/net/Proxy;)Ljava/net/URLConnection;
    //   87: astore 4
    //   89: aload 4
    //   91: checkcast 247	java/net/HttpURLConnection
    //   94: astore 4
    //   96: goto +17 -> 113
    //   99: aload 4
    //   101: invokevirtual 250	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   104: astore 4
    //   106: aload 4
    //   108: checkcast 247	java/net/HttpURLConnection
    //   111: astore 4
    //   113: aload 4
    //   115: astore 6
    //   117: new 222	java/lang/StringBuilder
    //   120: dup
    //   121: invokespecial 223	java/lang/StringBuilder:<init>	()V
    //   124: astore 8
    //   126: aload 4
    //   128: astore 6
    //   130: aload 8
    //   132: ldc_w 266
    //   135: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: pop
    //   139: aload_0
    //   140: getfield 75	client/testing/AIDataService:config	Lclient/testing/AIConfiguration;
    //   143: astore 9
    //   145: aload 4
    //   147: astore 6
    //   149: aload 8
    //   151: aload 9
    //   153: invokevirtual 269	client/testing/AIConfiguration:getApiKey	()Ljava/lang/String;
    //   156: invokevirtual 229	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: pop
    //   160: aload 4
    //   162: astore 6
    //   164: aload 4
    //   166: ldc_w 271
    //   169: aload 8
    //   171: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   174: invokevirtual 275	java/net/HttpURLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   177: aload 4
    //   179: astore 6
    //   181: aload 4
    //   183: ldc_w 281
    //   186: ldc_w 283
    //   189: invokevirtual 275	java/net/HttpURLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   192: aload_3
    //   193: ifnull +119 -> 312
    //   196: aload 4
    //   198: astore 6
    //   200: aload_3
    //   201: invokeinterface 289 1 0
    //   206: invokeinterface 295 1 0
    //   211: astore_3
    //   212: aload 4
    //   214: astore 6
    //   216: aload_3
    //   217: invokeinterface 300 1 0
    //   222: istore 5
    //   224: iload 5
    //   226: ifeq +86 -> 312
    //   229: aload 4
    //   231: astore 6
    //   233: aload_3
    //   234: invokeinterface 304 1 0
    //   239: astore 8
    //   241: aload 4
    //   243: astore 6
    //   245: aload 8
    //   247: checkcast 306	java/util/Map$Entry
    //   250: astore 9
    //   252: aload 4
    //   254: astore 6
    //   256: aload 9
    //   258: invokeinterface 309 1 0
    //   263: astore 8
    //   265: aload 4
    //   267: astore 6
    //   269: aload 8
    //   271: checkcast 191	java/lang/String
    //   274: astore 8
    //   276: aload 4
    //   278: astore 6
    //   280: aload 9
    //   282: invokeinterface 312 1 0
    //   287: astore 9
    //   289: aload 9
    //   291: checkcast 191	java/lang/String
    //   294: astore 9
    //   296: aload 4
    //   298: astore 6
    //   300: aload 4
    //   302: aload 8
    //   304: aload 9
    //   306: invokevirtual 275	java/net/HttpURLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   309: goto -97 -> 212
    //   312: aload 4
    //   314: astore 6
    //   316: aload 4
    //   318: ldc 17
    //   320: invokevirtual 260	java/net/HttpURLConnection:setRequestMethod	(Ljava/lang/String;)V
    //   323: aload 4
    //   325: astore 6
    //   327: aload 4
    //   329: iconst_1
    //   330: invokevirtual 420	java/net/HttpURLConnection:setDoInput	(Z)V
    //   333: aload 4
    //   335: astore 6
    //   337: aload 4
    //   339: iconst_1
    //   340: invokevirtual 264	java/net/HttpURLConnection:setDoOutput	(Z)V
    //   343: aload 4
    //   345: astore 6
    //   347: new 422	client/testing/http/HttpClient
    //   350: dup
    //   351: aload 4
    //   353: invokespecial 425	client/testing/http/HttpClient:<init>	(Ljava/net/HttpURLConnection;)V
    //   356: astore_3
    //   357: aload_0
    //   358: getfield 75	client/testing/AIDataService:config	Lclient/testing/AIConfiguration;
    //   361: astore 7
    //   363: aload 4
    //   365: astore 6
    //   367: aload_3
    //   368: aload 7
    //   370: invokevirtual 428	client/testing/AIConfiguration:isWriteSoundLog	()Z
    //   373: invokevirtual 431	client/testing/http/HttpClient:setWriteSoundLog	(Z)V
    //   376: aload 4
    //   378: astore 6
    //   380: aload_3
    //   381: invokevirtual 434	client/testing/http/HttpClient:connectForMultipart	()V
    //   384: aload 4
    //   386: astore 6
    //   388: aload_3
    //   389: ldc_w 436
    //   392: aload_2
    //   393: invokevirtual 439	client/testing/http/HttpClient:addFormPart	(Ljava/lang/String;Ljava/lang/String;)V
    //   396: aload 4
    //   398: astore 6
    //   400: aload_3
    //   401: ldc_w 441
    //   404: ldc_w 443
    //   407: aload_1
    //   408: invokevirtual 447	client/testing/http/HttpClient:addFilePart	(Ljava/lang/String;Ljava/lang/String;Ljava/io/InputStream;)V
    //   411: aload 4
    //   413: astore 6
    //   415: aload_3
    //   416: invokevirtual 450	client/testing/http/HttpClient:finishMultipart	()V
    //   419: aload 4
    //   421: astore 6
    //   423: aload_3
    //   424: invokevirtual 453	client/testing/http/HttpClient:getResponse	()Ljava/lang/String;
    //   427: astore_2
    //   428: aload_2
    //   429: astore_1
    //   430: aload 4
    //   432: ifnull +219 -> 651
    //   435: aload 4
    //   437: invokevirtual 379	java/net/HttpURLConnection:disconnect	()V
    //   440: aload_2
    //   441: areturn
    //   442: astore_1
    //   443: aload_3
    //   444: astore_2
    //   445: goto +24 -> 469
    //   448: astore_1
    //   449: aload 7
    //   451: astore_2
    //   452: goto +17 -> 469
    //   455: astore_1
    //   456: aconst_null
    //   457: astore 6
    //   459: goto +180 -> 639
    //   462: astore_1
    //   463: aconst_null
    //   464: astore 4
    //   466: aload 7
    //   468: astore_2
    //   469: aload_2
    //   470: ifnull +137 -> 607
    //   473: aload 4
    //   475: astore 6
    //   477: aload_2
    //   478: invokevirtual 456	client/testing/http/HttpClient:getErrorString	()Ljava/lang/String;
    //   481: astore_2
    //   482: aload 4
    //   484: astore 6
    //   486: getstatic 45	client/testing/AIDataService:logger	Lorg/apache/logging/log4j/Logger;
    //   489: aload_2
    //   490: invokeinterface 237 2 0
    //   495: aload 4
    //   497: astore 6
    //   499: aload_2
    //   500: invokestatic 462	client/testing/util/StringUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   503: istore 5
    //   505: iload 5
    //   507: ifne +17 -> 524
    //   510: aload_2
    //   511: astore_1
    //   512: aload 4
    //   514: ifnull +137 -> 651
    //   517: aload 4
    //   519: invokevirtual 379	java/net/HttpURLConnection:disconnect	()V
    //   522: aload_2
    //   523: areturn
    //   524: aload 4
    //   526: astore 6
    //   528: aload_1
    //   529: instanceof 464
    //   532: istore 5
    //   534: iload 5
    //   536: ifeq +71 -> 607
    //   539: aload 4
    //   541: astore 6
    //   543: new 357	client/testing/model/AIResponse
    //   546: dup
    //   547: invokespecial 465	client/testing/model/AIResponse:<init>	()V
    //   550: astore_2
    //   551: aload 4
    //   553: astore 6
    //   555: aload_1
    //   556: checkcast 464	java/net/HttpRetryException
    //   559: invokevirtual 468	java/net/HttpRetryException:responseCode	()I
    //   562: invokestatic 472	client/testing/model/Status:fromResponseCode	(I)Lclient/testing/model/Status;
    //   565: astore_3
    //   566: aload 4
    //   568: astore 6
    //   570: aload_3
    //   571: aload_1
    //   572: checkcast 464	java/net/HttpRetryException
    //   575: invokevirtual 475	java/net/HttpRetryException:getReason	()Ljava/lang/String;
    //   578: invokevirtual 478	client/testing/model/Status:setErrorDetails	(Ljava/lang/String;)V
    //   581: aload 4
    //   583: astore 6
    //   585: aload_2
    //   586: aload_3
    //   587: invokevirtual 482	client/testing/model/AIResponse:setStatus	(Lclient/testing/model/Status;)V
    //   590: aload 4
    //   592: astore 6
    //   594: new 159	client/testing/AIServiceException
    //   597: dup
    //   598: aload_2
    //   599: invokespecial 198	client/testing/AIServiceException:<init>	(Lclient/testing/model/AIResponse;)V
    //   602: athrow
    //   603: astore_1
    //   604: goto +35 -> 639
    //   607: aload 4
    //   609: astore 6
    //   611: getstatic 45	client/testing/AIDataService:logger	Lorg/apache/logging/log4j/Logger;
    //   614: ldc_w 484
    //   617: aload_1
    //   618: invokeinterface 397 3 0
    //   623: aload 4
    //   625: astore 6
    //   627: new 159	client/testing/AIServiceException
    //   630: dup
    //   631: ldc_w 484
    //   634: aload_1
    //   635: invokespecial 385	client/testing/AIServiceException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   638: athrow
    //   639: aload 6
    //   641: ifnull +8 -> 649
    //   644: aload 6
    //   646: invokevirtual 379	java/net/HttpURLConnection:disconnect	()V
    //   649: aload_1
    //   650: athrow
    //   651: aload_1
    //   652: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	653	0	this	AIDataService
    //   0	653	1	paramInputStream	InputStream
    //   0	653	2	paramString	String
    //   0	653	3	paramMap	Map
    //   0	653	4	paramAIServiceContext	AIServiceContext
    //   222	313	5	bool	boolean
    //   7	638	6	localObject1	Object
    //   1	466	7	localAIConfiguration	AIConfiguration
    //   124	179	8	localObject2	Object
    //   143	162	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   367	376	442	java/io/IOException
    //   380	384	442	java/io/IOException
    //   388	396	442	java/io/IOException
    //   400	411	442	java/io/IOException
    //   415	419	442	java/io/IOException
    //   423	428	442	java/io/IOException
    //   117	126	448	java/io/IOException
    //   130	139	448	java/io/IOException
    //   149	160	448	java/io/IOException
    //   164	177	448	java/io/IOException
    //   181	192	448	java/io/IOException
    //   200	212	448	java/io/IOException
    //   216	224	448	java/io/IOException
    //   233	241	448	java/io/IOException
    //   256	265	448	java/io/IOException
    //   280	289	448	java/io/IOException
    //   300	309	448	java/io/IOException
    //   316	323	448	java/io/IOException
    //   327	333	448	java/io/IOException
    //   337	343	448	java/io/IOException
    //   347	357	448	java/io/IOException
    //   3	9	455	java/lang/Throwable
    //   9	29	455	java/lang/Throwable
    //   29	34	455	java/lang/Throwable
    //   34	53	455	java/lang/Throwable
    //   59	66	455	java/lang/Throwable
    //   71	77	455	java/lang/Throwable
    //   77	89	455	java/lang/Throwable
    //   89	96	455	java/lang/Throwable
    //   99	106	455	java/lang/Throwable
    //   106	113	455	java/lang/Throwable
    //   9	29	462	java/io/IOException
    //   34	53	462	java/io/IOException
    //   59	66	462	java/io/IOException
    //   77	89	462	java/io/IOException
    //   99	106	462	java/io/IOException
    //   117	126	603	java/lang/Throwable
    //   130	139	603	java/lang/Throwable
    //   149	160	603	java/lang/Throwable
    //   164	177	603	java/lang/Throwable
    //   181	192	603	java/lang/Throwable
    //   200	212	603	java/lang/Throwable
    //   216	224	603	java/lang/Throwable
    //   233	241	603	java/lang/Throwable
    //   245	252	603	java/lang/Throwable
    //   256	265	603	java/lang/Throwable
    //   269	276	603	java/lang/Throwable
    //   280	289	603	java/lang/Throwable
    //   300	309	603	java/lang/Throwable
    //   316	323	603	java/lang/Throwable
    //   327	333	603	java/lang/Throwable
    //   337	343	603	java/lang/Throwable
    //   347	357	603	java/lang/Throwable
    //   367	376	603	java/lang/Throwable
    //   380	384	603	java/lang/Throwable
    //   388	396	603	java/lang/Throwable
    //   400	411	603	java/lang/Throwable
    //   415	419	603	java/lang/Throwable
    //   423	428	603	java/lang/Throwable
    //   477	482	603	java/lang/Throwable
    //   486	495	603	java/lang/Throwable
    //   499	505	603	java/lang/Throwable
    //   528	534	603	java/lang/Throwable
    //   543	551	603	java/lang/Throwable
    //   555	566	603	java/lang/Throwable
    //   570	581	603	java/lang/Throwable
    //   585	590	603	java/lang/Throwable
    //   594	603	603	java/lang/Throwable
    //   611	623	603	java/lang/Throwable
    //   627	639	603	java/lang/Throwable
  }
  
  protected String doTextRequest(String paramString)
    throws MalformedURLException, AIServiceException
  {
    return doTextRequest(paramString, UNDEFINED_SERVICE_CONTEXT);
  }
  
  protected String doTextRequest(String paramString, AIServiceContext paramAIServiceContext)
    throws MalformedURLException, AIServiceException
  {
    return doTextRequest(config.getQuestionUrl(getSessionId(paramAIServiceContext)), paramString);
  }
  
  protected String doTextRequest(String paramString1, String paramString2)
    throws MalformedURLException, AIServiceException
  {
    return doTextRequest(paramString1, paramString2, null);
  }
  
  protected String doTextRequest(String paramString1, String paramString2, Map paramMap)
    throws MalformedURLException, AIServiceException
  {
    Object localObject4 = null;
    Object localObject3 = null;
    Object localObject1 = localObject3;
    Object localObject2 = localObject4;
    try
    {
      paramString1 = new URL(paramString1);
      localObject1 = localObject3;
      Object localObject5 = logger;
      localObject1 = localObject3;
      localObject2 = localObject4;
      StringBuilder localStringBuilder = new StringBuilder();
      localObject1 = localObject3;
      localObject2 = localObject4;
      localStringBuilder.append("Request json: ");
      localObject1 = localObject3;
      localObject2 = localObject4;
      localStringBuilder.append(paramString2);
      localObject1 = localObject3;
      localObject2 = localObject4;
      ((Logger)localObject5).debug(localStringBuilder.toString());
      localObject5 = config;
      localObject1 = localObject3;
      localObject2 = localObject4;
      localObject5 = ((AIConfiguration)localObject5).getProxy();
      if (localObject5 != null)
      {
        localObject1 = localObject3;
        localObject5 = config;
        localObject1 = localObject3;
        localObject2 = localObject4;
        paramString1 = paramString1.openConnection(((AIConfiguration)localObject5).getProxy());
        localObject1 = localObject3;
        localObject3 = (HttpURLConnection)paramString1;
      }
      else
      {
        localObject1 = localObject3;
        localObject2 = localObject4;
        paramString1 = paramString1.openConnection();
        localObject1 = localObject3;
        localObject3 = (HttpURLConnection)paramString1;
      }
      paramString1 = (String)localObject3;
      localObject1 = paramString1;
      localObject2 = paramString1;
      ((HttpURLConnection)localObject3).setRequestMethod("POST");
      localObject1 = paramString1;
      localObject2 = paramString1;
      ((HttpURLConnection)localObject3).setDoOutput(true);
      localObject1 = paramString1;
      localObject2 = paramString1;
      localObject4 = new StringBuilder();
      localObject1 = paramString1;
      localObject2 = paramString1;
      ((StringBuilder)localObject4).append("Bearer ");
      localObject5 = config;
      localObject1 = paramString1;
      localObject2 = paramString1;
      ((StringBuilder)localObject4).append(((AIConfiguration)localObject5).getApiKey());
      localObject1 = paramString1;
      localObject2 = paramString1;
      ((HttpURLConnection)localObject3).addRequestProperty("Authorization", ((StringBuilder)localObject4).toString());
      localObject1 = paramString1;
      localObject2 = paramString1;
      ((HttpURLConnection)localObject3).addRequestProperty("Content-Type", "application/json; charset=utf-8");
      localObject1 = paramString1;
      localObject2 = paramString1;
      ((HttpURLConnection)localObject3).addRequestProperty("Accept", "application/json");
      if (paramMap != null)
      {
        localObject1 = paramString1;
        localObject2 = paramString1;
        paramMap = paramMap.entrySet().iterator();
        for (;;)
        {
          localObject1 = paramString1;
          localObject2 = paramString1;
          boolean bool = paramMap.hasNext();
          if (!bool) {
            break;
          }
          localObject1 = paramString1;
          localObject2 = paramString1;
          localObject3 = paramMap.next();
          localObject1 = paramString1;
          localObject4 = (Map.Entry)localObject3;
          localObject1 = paramString1;
          localObject2 = paramString1;
          localObject3 = ((Map.Entry)localObject4).getKey();
          localObject1 = paramString1;
          localObject3 = (String)localObject3;
          localObject1 = paramString1;
          localObject2 = paramString1;
          localObject4 = ((Map.Entry)localObject4).getValue();
          localObject4 = (String)localObject4;
          localObject1 = paramString1;
          localObject2 = paramString1;
          paramString1.addRequestProperty((String)localObject3, (String)localObject4);
        }
      }
      localObject1 = paramString1;
      localObject2 = paramString1;
      paramString1.connect();
      localObject1 = paramString1;
      localObject2 = paramString1;
      paramMap = new BufferedOutputStream(paramString1.getOutputStream());
      localObject1 = paramString1;
      localObject2 = paramString1;
      IOUtils.writeAll(paramString2, paramMap);
      localObject1 = paramString1;
      localObject2 = paramString1;
      paramMap.close();
      localObject1 = paramString1;
      localObject2 = paramString1;
      paramString2 = new BufferedInputStream(paramString1.getInputStream());
      localObject1 = paramString1;
      localObject2 = paramString1;
      paramMap = IOUtils.readAll(paramString2);
      localObject1 = paramString1;
      localObject2 = paramString1;
      paramString2.close();
      paramString2 = paramMap;
      if (paramString1 == null) {
        return paramString2;
      }
      paramString1.disconnect();
      return paramMap;
    }
    catch (Throwable paramString1) {}catch (IOException paramString2)
    {
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        try
        {
          paramString1 = ((HttpURLConnection)localObject2).getErrorStream();
          if (paramString1 != null)
          {
            localObject1 = localObject2;
            paramString1 = IOUtils.readAll(paramString1);
            paramMap = logger;
            localObject1 = localObject2;
            paramMap.debug(paramString1);
            paramString2 = paramString1;
            if (localObject2 == null) {
              return paramString2;
            }
            ((HttpURLConnection)localObject2).disconnect();
            return paramString1;
          }
          localObject1 = localObject2;
          paramString1 = new AIServiceException("Can't connect to the api.ai service.", paramString2);
          localObject1 = localObject2;
          throw paramString1;
        }
        catch (IOException paramString1)
        {
          localObject1 = localObject2;
          logger.warn("Can't read error response", paramString1);
        }
      }
      localObject1 = localObject2;
      logger.error("Can't make request to the API.AI service. Please, check connection settings and API access token.", paramString2);
      localObject1 = localObject2;
      throw new AIServiceException("Can't make request to the API.AI service. Please, check connection settings and API access token.", paramString2);
    }
    if (localObject1 != null) {
      ((HttpURLConnection)localObject1).disconnect();
    }
    throw paramString1;
    return paramString2;
  }
  
  public client.testing.model.AIContext getActiveContext(String paramString)
    throws AIServiceException
  {
    return getActiveContext(paramString, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public client.testing.model.AIContext getActiveContext(String paramString, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    AIConfiguration localAIConfiguration = config;
    try
    {
      paramString = doRequest(ai.api.model.AIContext.class, localAIConfiguration.getContextsUrl(getSessionId(paramAIServiceContext), paramString), "GET");
      return (client.testing.model.AIContext)paramString;
    }
    catch (BadResponseStatusException paramString)
    {
      if (response.getStatus().getCode().intValue() == 404) {
        return null;
      }
      throw new AIServiceException(response);
    }
  }
  
  public List getActiveContexts()
    throws AIServiceException
  {
    return getActiveContexts(UNDEFINED_SERVICE_CONTEXT);
  }
  
  public List getActiveContexts(AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    AIConfiguration localAIConfiguration = config;
    try
    {
      paramAIServiceContext = doRequest(ai.api.AIDataService.ApiActiveContextListResponse.class, localAIConfiguration.getContextsUrl(getSessionId(paramAIServiceContext)), "GET");
      return (List)paramAIServiceContext;
    }
    catch (BadResponseStatusException paramAIServiceContext)
    {
      throw new AIServiceException(response);
    }
  }
  
  public AIServiceContext getContext()
  {
    return defaultServiceContext;
  }
  
  public boolean removeActiveContext(String paramString)
    throws AIServiceException
  {
    return removeActiveContext(paramString, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public boolean removeActiveContext(String paramString, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    AIConfiguration localAIConfiguration = config;
    try
    {
      doRequest(ai.api.model.AIResponse.class, localAIConfiguration.getContextsUrl(getSessionId(paramAIServiceContext), paramString), "DELETE");
      return true;
    }
    catch (BadResponseStatusException paramString)
    {
      if (response.getStatus().getCode().intValue() == 404) {
        return false;
      }
      throw new AIServiceException(response);
    }
  }
  
  public client.testing.model.AIResponse request(AIRequest paramAIRequest)
    throws AIServiceException
  {
    return request(paramAIRequest, null);
  }
  
  public client.testing.model.AIResponse request(AIRequest paramAIRequest, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    return request(paramAIRequest, null, paramAIServiceContext);
  }
  
  public client.testing.model.AIResponse request(AIRequest paramAIRequest, RequestExtras paramRequestExtras)
    throws AIServiceException
  {
    return request(paramAIRequest, paramRequestExtras, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public client.testing.model.AIResponse request(AIRequest paramAIRequest, RequestExtras paramRequestExtras, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    if (paramAIRequest != null)
    {
      logger.debug("Start request");
      Object localObject = config;
      try
      {
        paramAIRequest.setLanguage(((AIConfiguration)localObject).getApiAiLanguage());
        paramAIRequest.setSessionId(getSessionId(paramAIServiceContext));
        boolean bool = StringUtils.isEmpty(paramAIRequest.getTimezone());
        if (bool) {
          paramAIRequest.setTimezone(getTimeZone(paramAIServiceContext));
        }
        localObject = null;
        if (paramRequestExtras != null)
        {
          fillRequest(paramAIRequest, paramRequestExtras);
          localObject = paramRequestExtras.getAdditionalHeaders();
        }
        paramRequestExtras = GSON;
        paramAIRequest = paramRequestExtras.toJson(paramAIRequest);
        paramRequestExtras = config;
        paramAIRequest = doTextRequest(paramRequestExtras.getQuestionUrl(getSessionId(paramAIServiceContext)), paramAIRequest, (Map)localObject);
        bool = StringUtils.isEmpty(paramAIRequest);
        if (!bool)
        {
          paramRequestExtras = logger;
          paramAIServiceContext = new StringBuilder();
          paramAIServiceContext.append("Response json: ");
          paramAIServiceContext.append(paramAIRequest.replaceAll("[\r\n]+", " "));
          paramRequestExtras.debug(paramAIServiceContext.toString());
          paramRequestExtras = GSON;
          paramAIRequest = paramRequestExtras.fromJson(paramAIRequest, ai.api.model.AIResponse.class);
          paramAIRequest = (client.testing.model.AIResponse)paramAIRequest;
          if (paramAIRequest != null)
          {
            bool = paramAIRequest.isError();
            if (!bool)
            {
              paramAIRequest.cleanup();
              return paramAIRequest;
            }
            paramAIRequest = new AIServiceException(paramAIRequest);
            throw paramAIRequest;
          }
          paramAIRequest = new AIServiceException("API.AI response parsed as null. Check debug log for details.");
          throw paramAIRequest;
        }
        paramAIRequest = new AIServiceException("Empty response from ai service. Please check configuration and Internet connection.");
        throw paramAIRequest;
      }
      catch (JsonSyntaxException paramAIRequest)
      {
        throw new AIServiceException("Wrong service answer format. Please, connect to API.AI Service support", (Throwable)paramAIRequest);
      }
      catch (MalformedURLException paramAIRequest)
      {
        logger.error("Malformed url should not be raised", paramAIRequest);
        throw new AIServiceException("Wrong configuration. Please, connect to API.AI Service support", paramAIRequest);
      }
    }
    throw new IllegalArgumentException("Request argument must not be null");
  }
  
  public void resetActiveContexts()
    throws AIServiceException
  {
    resetActiveContexts(UNDEFINED_SERVICE_CONTEXT);
  }
  
  public void resetActiveContexts(AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    AIConfiguration localAIConfiguration = config;
    try
    {
      doRequest(ai.api.model.AIResponse.class, localAIConfiguration.getContextsUrl(getSessionId(paramAIServiceContext)), "DELETE");
      return;
    }
    catch (BadResponseStatusException paramAIServiceContext)
    {
      throw new AIServiceException(response);
    }
  }
  
  public boolean resetContexts()
  {
    AIRequest localAIRequest = new AIRequest();
    localAIRequest.setQuery("empty_query_for_resetting_contexts");
    localAIRequest.setResetContexts(Boolean.valueOf(true));
    try
    {
      boolean bool = request(localAIRequest).isError();
      return bool ^ true;
    }
    catch (AIServiceException localAIServiceException)
    {
      logger.error("Exception while contexts clean.", localAIServiceException);
    }
    return false;
  }
  
  public client.testing.model.AIResponse uploadUserEntities(Collection paramCollection)
    throws AIServiceException
  {
    return uploadUserEntities(paramCollection, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public client.testing.model.AIResponse uploadUserEntities(Collection paramCollection, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    if ((paramCollection != null) && (paramCollection.size() != 0))
    {
      paramCollection = GSON.toJson(paramCollection);
      Object localObject = config;
      try
      {
        paramCollection = doTextRequest(((AIConfiguration)localObject).getUserEntitiesEndpoint(getSessionId(paramAIServiceContext)), paramCollection);
        boolean bool = StringUtils.isEmpty(paramCollection);
        if (!bool)
        {
          paramAIServiceContext = logger;
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Response json: ");
          ((StringBuilder)localObject).append(paramCollection);
          paramAIServiceContext.debug(((StringBuilder)localObject).toString());
          paramAIServiceContext = GSON;
          paramCollection = paramAIServiceContext.fromJson(paramCollection, ai.api.model.AIResponse.class);
          paramCollection = (client.testing.model.AIResponse)paramCollection;
          if (paramCollection != null)
          {
            bool = paramCollection.isError();
            if (!bool)
            {
              paramCollection.cleanup();
              return paramCollection;
            }
            paramCollection = new AIServiceException(paramCollection);
            throw paramCollection;
          }
          paramCollection = new AIServiceException("API.AI response parsed as null. Check debug log for details.");
          throw paramCollection;
        }
        paramCollection = new AIServiceException("Empty response from ai service. Please check configuration and Internet connection.");
        throw paramCollection;
      }
      catch (JsonSyntaxException paramCollection)
      {
        throw new AIServiceException("Wrong service answer format. Please, connect to API.AI Service support", (Throwable)paramCollection);
      }
      catch (MalformedURLException paramCollection)
      {
        logger.error("Malformed url should not be raised", paramCollection);
        throw new AIServiceException("Wrong configuration. Please, connect to AI Service support", paramCollection);
      }
    }
    throw new AIServiceException("Empty entities list");
  }
  
  public client.testing.model.AIResponse uploadUserEntity(Entity paramEntity)
    throws AIServiceException
  {
    return uploadUserEntity(paramEntity, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public client.testing.model.AIResponse uploadUserEntity(Entity paramEntity, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    return uploadUserEntities(Collections.singleton(paramEntity), paramAIServiceContext);
  }
  
  public client.testing.model.AIResponse voiceRequest(InputStream paramInputStream)
    throws AIServiceException
  {
    return voiceRequest(paramInputStream, new RequestExtras());
  }
  
  public client.testing.model.AIResponse voiceRequest(InputStream paramInputStream, RequestExtras paramRequestExtras)
    throws AIServiceException
  {
    return voiceRequest(paramInputStream, paramRequestExtras, UNDEFINED_SERVICE_CONTEXT);
  }
  
  public client.testing.model.AIResponse voiceRequest(InputStream paramInputStream, RequestExtras paramRequestExtras, AIServiceContext paramAIServiceContext)
    throws AIServiceException
  {
    logger.debug("Start voice request");
    try
    {
      Object localObject1 = new AIRequest();
      Object localObject2 = config;
      ((QuestionMetadata)localObject1).setLanguage(((AIConfiguration)localObject2).getApiAiLanguage());
      ((QuestionMetadata)localObject1).setSessionId(getSessionId(paramAIServiceContext));
      ((QuestionMetadata)localObject1).setTimezone(getTimeZone(paramAIServiceContext));
      paramAIServiceContext = null;
      if (paramRequestExtras != null)
      {
        fillRequest((AIRequest)localObject1, paramRequestExtras);
        paramAIServiceContext = paramRequestExtras.getAdditionalHeaders();
      }
      paramRequestExtras = GSON;
      paramRequestExtras = paramRequestExtras.toJson(localObject1);
      localObject1 = logger;
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("Request json: ");
      ((StringBuilder)localObject2).append(paramRequestExtras);
      ((Logger)localObject1).debug(((StringBuilder)localObject2).toString());
      paramInputStream = doSoundRequest(paramInputStream, paramRequestExtras, paramAIServiceContext);
      boolean bool = StringUtils.isEmpty(paramInputStream);
      if (!bool)
      {
        paramRequestExtras = logger;
        paramAIServiceContext = new StringBuilder();
        paramAIServiceContext.append("Response json: ");
        paramAIServiceContext.append(paramInputStream);
        paramRequestExtras.debug(paramAIServiceContext.toString());
        paramRequestExtras = GSON;
        paramInputStream = paramRequestExtras.fromJson(paramInputStream, ai.api.model.AIResponse.class);
        paramInputStream = (client.testing.model.AIResponse)paramInputStream;
        if (paramInputStream != null)
        {
          bool = paramInputStream.isError();
          if (!bool)
          {
            paramInputStream.cleanup();
            return paramInputStream;
          }
          paramInputStream = new AIServiceException(paramInputStream);
          throw paramInputStream;
        }
        paramInputStream = new AIServiceException("API.AI response parsed as null. Check debug log for details.");
        throw paramInputStream;
      }
      paramInputStream = new AIServiceException("Empty response from ai service. Please check configuration.");
      throw paramInputStream;
    }
    catch (JsonSyntaxException paramInputStream)
    {
      throw new AIServiceException("Wrong service answer format. Please, connect to API.AI Service support", (Throwable)paramInputStream);
    }
    catch (MalformedURLException paramInputStream)
    {
      logger.error("Malformed url should not be raised", paramInputStream);
      throw new AIServiceException("Wrong configuration. Please, connect to AI Service support", paramInputStream);
    }
  }
  
  public client.testing.model.AIResponse voiceRequest(InputStream paramInputStream, List paramList)
    throws AIServiceException
  {
    return voiceRequest(paramInputStream, new RequestExtras(paramList, null));
  }
  
  abstract interface ApiActiveContextListResponse
    extends List<ai.api.model.AIContext>
  {}
  
  class ApiActiveContextNamesResponse
    extends client.testing.model.AIResponse
  {
    private static final long serialVersionUID = 1L;
    public List<String> names;
    
    private ApiActiveContextNamesResponse() {}
  }
  
  class BadResponseStatusException
    extends Exception
  {
    private static final long serialVersionUID = 1L;
    
    public BadResponseStatusException() {}
  }
}
