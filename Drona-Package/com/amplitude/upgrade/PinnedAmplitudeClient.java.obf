package com.amplitude.api;

import android.content.Context;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import okhttp3.OkHttpClient.Builder;
import okio.Buffer;
import okio.ByteString;

public class PinnedAmplitudeClient
  extends AmplitudeClient
{
  protected static final SSLContextBuilder SSL_CONTEXT_API_AMPLITUDE_COM = new SSLContextBuilder().addCertificate("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").addCertificate("MIIFdDCCBFygAwIBAgIQJ2buVutJ846r13Ci/ITeIjANBgkqhkiG9w0BAQwFADBvMQswCQYDVQQGEwJTRTEUMBIGA1UEChMLQWRkVHJ1c3QgQUIxJjAkBgNVBAsTHUFkZFRydXN0IEV4dGVybmFsIFRUUCBOZXR3b3JrMSIwIAYDVQQDExlBZGRUcnVzdCBFeHRlcm5hbCBDQSBSb290MB4XDTAwMDUzMDEwNDgzOFoXDTIwMDUzMDEwNDgzOFowgYUxCzAJBgNVBAYTAkdCMRswGQYDVQQIExJHcmVhdGVyIE1hbmNoZXN0ZXIxEDAOBgNVBAcTB1NhbGZvcmQxGjAYBgNVBAoTEUNPTU9ETyBDQSBMaW1pdGVkMSswKQYDVQQDEyJDT01PRE8gUlNBIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAkehUktIKVrGsDSTdxc9EZ3SZKzejfSNwAHG8U9/E+ioSj0t/EFa9n3Byt2F/yUsPF6c947AEYe7/EZfH9IY+Cvo+XPmT5jR62RRr55yzhaCCenavcZDX7P0N+pxs+t+wgvQUfvm+xKYvT3+Zf7X8Z0NyvQwA1onrayzT7Y+YHBSrfuXjbvzYqOSSJNpDa2K4Vf3qwbxstovzDo2a5JtsaZn4eEgwRdWt4Q08RWD8MpZRJ7xnw8outmvqRsfHIKCxH2XeSAi6pE6p8oNGN4Tr6MyBSENnTnIqm1y9TBsoilwie7SrmNnu4FGDwwlGTm0+mfqVF9p8M1dBPI1R7Qu2XK8sYxrfV8g/vOldxJuvRZnio1oktLqpVj3Pb6r/SVi+8Kj/9Lit6Tf7urj0Czr56ENCHonYhMsT8dm74YlguIwoVqwUHZwK53Hrzw7dPamWoUi9PPevtQ0iTMARgexWO/bTouJbt7IEIlKVgJNp6I5MZfGRAy1wdALqi2cVKWlSArvX31BqVUa/oKMoYX9w0MOiqiwhqkfOKJwGRXa/ghgntNWutMtQ5mv0TIZxMOmm3xaG4Nj/QN370EKIf6MzOi5cHkERgWPOGHFrK+ymircxXDpqR+DDeVnWIBqv8mqYqnK8V0rSS527EPywTEHl7R09XiidnMy/s1Hap0flhFMCAwEAAaOB9DCB8TAfBgNVHSMEGDAWgBStvZh6NLQm9/rEJlTvA73gJMtUGjAdBgNVHQ4EFgQUu69+Aj36pvE8hI6t7jiY7NkyMtQwDgYDVR0PAQH/BAQDAgGGMA8GA1UdEwEB/wQFMAMBAf8wEQYDVR0gBAowCDAGBgRVHSAAMEQGA1UdHwQ9MDswOaA3oDWGM2h0dHA6Ly9jcmwudXNlcnRydXN0LmNvbS9BZGRUcnVzdEV4dGVybmFsQ0FSb290LmNybDA1BggrBgEFBQcBAQQpMCcwJQYIKwYBBQUHMAGGGWh0dHA6Ly9vY3NwLnVzZXJ0cnVzdC5jb20wDQYJKoZIhvcNAQEMBQADggEBAGS/g/FfmoXQzbihKVcN6Fr30ek+8nYEbvFScLsePP9NDXRqzIGCJdPDoCpdTPW6i6FtxFQJdcfjJw5dhHk3QBN39bSsHNA7qxcS1u80GH4r6XnTq1dFDK8o+tDb5VCViLvfhVdpfZLYUspzgb8c8+a4bmYRBbMelC1/kZWSWfFMzqORcUx8Rww7Cxn2obFshj5cqsQugsv5B5a6SE2Q8pTIqXOi6wZ7I53eovNNVZ96YUWYGGjHXkBrI/V5eu+MtWuLt29G9HvxPUsE2JOAWVrgQSQdso8VYFhH2+9uRv0V9dlfmrPb2LjkQLPNlzmuhbsdjrzch5vRpu/xO28QOG8=");
  public static final String TAG = "com.amplitude.api.PinnedAmplitudeClient";
  static Map<String, PinnedAmplitudeClient> instances = new HashMap();
  private static final AmplitudeLog logger = ;
  protected boolean initializedSSLSocketFactory = false;
  protected SSLSocketFactory sslSocketFactory;
  
  public PinnedAmplitudeClient(String paramString)
  {
    super(paramString);
  }
  
  public static PinnedAmplitudeClient getInstance()
  {
    return getInstance(null);
  }
  
  public static PinnedAmplitudeClient getInstance(String paramString)
  {
    try
    {
      String str = Utils.normalizeInstanceName(paramString);
      PinnedAmplitudeClient localPinnedAmplitudeClient = (PinnedAmplitudeClient)instances.get(str);
      paramString = localPinnedAmplitudeClient;
      if (localPinnedAmplitudeClient == null)
      {
        paramString = new PinnedAmplitudeClient(str);
        instances.put(str, paramString);
      }
      return paramString;
    }
    finally {}
  }
  
  protected SSLSocketFactory getPinnedCertSslSocketFactory()
  {
    return getPinnedCertSslSocketFactory(SSL_CONTEXT_API_AMPLITUDE_COM);
  }
  
  protected SSLSocketFactory getPinnedCertSslSocketFactory(SSLContextBuilder paramSSLContextBuilder)
  {
    if (paramSSLContextBuilder == null) {
      return null;
    }
    if (sslSocketFactory == null) {
      try
      {
        sslSocketFactory = paramSSLContextBuilder.build().getSocketFactory();
        logger.i("com.amplitude.api.PinnedAmplitudeClient", "Pinning SSL session using Comodo CA Cert");
      }
      catch (Exception paramSSLContextBuilder)
      {
        logger.e("com.amplitude.api.PinnedAmplitudeClient", paramSSLContextBuilder.getMessage(), paramSSLContextBuilder);
      }
    }
    return sslSocketFactory;
  }
  
  public AmplitudeClient initialize(Context paramContext, String paramString1, String paramString2)
  {
    try
    {
      super.initialize(paramContext, paramString1, paramString2);
      runOnLogThread(new Runnable()
      {
        public void run()
        {
          if (!jdField_thisinitializedSSLSocketFactory)
          {
            Object localObject = getPinnedCertSslSocketFactory();
            if (localObject != null)
            {
              jdField_thishttpClient = new OkHttpClient.Builder().sslSocketFactory((SSLSocketFactory)localObject).build();
            }
            else
            {
              localObject = jdField_this;
              PinnedAmplitudeClient.logger.e("com.amplitude.api.PinnedAmplitudeClient", "Unable to pin SSL as requested. Will send data without SSL pinning.");
            }
            jdField_thisinitializedSSLSocketFactory = true;
          }
        }
      });
      return this;
    }
    finally
    {
      paramContext = finally;
      throw paramContext;
    }
  }
  
  protected static class SSLContextBuilder
  {
    private final List<String> certificateBase64s = new ArrayList();
    
    protected SSLContextBuilder() {}
    
    public SSLContextBuilder addCertificate(String paramString)
    {
      certificateBase64s.add(paramString);
      return this;
    }
    
    public SSLContext build()
    {
      try
      {
        Object localObject1 = CertificateFactory.getInstance("X.509");
        TrustManagerFactory localTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore localKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        localKeyStore.load(null, null);
        Iterator localIterator = certificateBase64s.iterator();
        int i = 1;
        while (localIterator.hasNext())
        {
          Object localObject2 = (String)localIterator.next();
          localObject2 = (X509Certificate)((CertificateFactory)localObject1).generateCertificate(new Buffer().write(ByteString.decodeBase64((String)localObject2)).inputStream());
          localKeyStore.setCertificateEntry(Integer.toString(i), (Certificate)localObject2);
          i += 1;
        }
        localTrustManagerFactory.init(localKeyStore);
        localObject1 = SSLContext.getInstance("TLS");
        ((SSLContext)localObject1).init(null, localTrustManagerFactory.getTrustManagers(), null);
        return localObject1;
      }
      catch (IOException localIOException)
      {
        PinnedAmplitudeClient.logger.e("com.amplitude.api.PinnedAmplitudeClient", localIOException.getMessage(), localIOException);
        return null;
      }
      catch (GeneralSecurityException localGeneralSecurityException)
      {
        PinnedAmplitudeClient.logger.e("com.amplitude.api.PinnedAmplitudeClient", localGeneralSecurityException.getMessage(), localGeneralSecurityException);
      }
      return null;
    }
  }
}
