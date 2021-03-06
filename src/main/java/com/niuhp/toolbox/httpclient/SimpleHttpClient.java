/**
 *
 */
package com.niuhp.toolbox.httpclient;

import com.niuhp.core.util.IoUtil;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by niuhp on 2016/4/13.
 */
public class SimpleHttpClient {
  private static final Logger logger = Logger.getLogger(SimpleHttpClient.class);

  private HttpClient httpClient;
  private Object lockObj = new Object();

  private int connectionRequestTimeout = 10000;
  private int connectTimeout = 10000;
  private int defaultMaxPerRoute = 20;
  private int maxTotal = 100;
  /**
   * 请求自动关闭时间，防止调用者长期持有请求
   */
  private long autoCloseTimeout = 3000;
  private final ExecutorService executorService = Executors.newFixedThreadPool(50);

  private void checkInit(boolean https) {
    if (httpClient != null) {
      return;
    }
    synchronized (lockObj) {
      if (httpClient != null) {
        return;
      }
      Builder builder = RequestConfig.custom();
      builder.setCookieSpec(CookieSpecs.STANDARD_STRICT);
      builder.setConnectionRequestTimeout(connectionRequestTimeout);
      builder.setConnectTimeout(connectTimeout);

      if (https) {
        builder.setExpectContinueEnabled(true);
        Collection<String> targetPreferredAuthSchemes = Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST);
        builder.setTargetPreferredAuthSchemes(targetPreferredAuthSchemes);
        Collection<String> proxyPreferredAuthSchemes = Arrays.asList(AuthSchemes.BASIC);
        builder.setProxyPreferredAuthSchemes(proxyPreferredAuthSchemes);
      }
      RequestConfig config = builder.build();

      HttpClientBuilder httpClientBuilder = HttpClients.custom();
      httpClientBuilder.setDefaultRequestConfig(config);

      if (https) {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        TrustManager trustManager = new CustomizedX509TrustManager();

        try {
          SSLContext context = SSLContext.getInstance("TLS");
          context.init(null, new TrustManager[]{trustManager}, null);
          ConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(context,
                  NoopHostnameVerifier.INSTANCE);
          registryBuilder.register("https", connectionSocketFactory);
        } catch (Exception e) {
          logger.error("register connectionSocketFactory error", e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        httpClientBuilder.setConnectionManager(connManager);
      }
      httpClient = httpClientBuilder.build();
    }
  }

  public String doGet2String(String url) {
    return doGet2String(url, null);
  }

  public String doGet2String(String url, Map<String, String> paraMap) {
    String wholeUrl = toGetUrl(url, paraMap);
    HttpResponse reponse = doGet(wholeUrl, false);
    return getContent(reponse);
  }

  public HttpResponse doGet(String url, Map<String, String> paraMap) {
    String wholeUrl = toGetUrl(url, paraMap);
    return doGet(wholeUrl, true);
  }


  private HttpResponse doGet(String url, boolean autoClose) {
    boolean https = url != null && url.startsWith("https");
    checkInit(https);
    HttpGet get = new HttpGet(url);
    return doRequest(httpClient, get, autoClose);
  }

  public String doPost2String(String url) {
    return doPost2String(url, null);
  }

  public String doPost2String(String url, Map<String, String> paraMap) {
    HttpResponse response = doPost(url, paraMap, false);
    return getContent(response);
  }

  public HttpResponse doPost(String url, Map<String, String> paraMap) {
    return doPost(url, paraMap, true);
  }

  private HttpResponse doPost(String url, Map<String, String> paraMap, boolean autoClose) {
    boolean https = url != null && url.startsWith("https");
    checkInit(https);
    HttpPost post = new HttpPost(url);
    List<NameValuePair> values = toValues(paraMap);
    if (values != null && !values.isEmpty()) {
      HttpEntity entity = new UrlEncodedFormEntity(values, Consts.UTF_8);
      post.setEntity(entity);
    }
    return doRequest(httpClient, post, autoClose);
  }

  private void autoClose(HttpUriRequest request, HttpResponse response) {
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(autoCloseTimeout);
        } catch (InterruptedException e) {
          logger.error("sleep error", e);
        }
        if (request instanceof HttpRequestBase) {
          HttpRequestBase httpRequestBase = (HttpRequestBase) request;
          httpRequestBase.releaseConnection();
        }
        closeHttpResponse(response);
        logger.info(String.format("autoClose request[%s] and reponse[%s]", request, response));
      }
    };

    executorService.execute(runnable);
  }

  private HttpResponse doRequest(HttpClient httpClient, HttpUriRequest httpRequest, boolean autoClose) {
    synchronized (httpClient) {
      try {
        HttpResponse response = httpClient.execute(httpRequest);
        if (autoClose) {
          autoClose(httpRequest, response);
        }
        return response;
      } catch (Exception e) {
        logger.error("execute error", e);
      }
    }
    return null;
  }

  public void closeHttpClient() {
    closeHttpClient(httpClient);
  }

  private static List<NameValuePair> toValues(Map<String, String> paraMap) {
    if (paraMap == null) {
      return null;
    }
    List<NameValuePair> values = new ArrayList<NameValuePair>(paraMap.size());
    Set<String> keys = paraMap.keySet();
    for (String key : keys) {
      NameValuePair pair = new BasicNameValuePair(key, paraMap.get(key));
      values.add(pair);
    }
    return values;
  }

  private String toGetUrl(String url, Map<String, String> paraMap) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(url);
    if (paraMap != null && !paraMap.isEmpty()) {
      urlBuilder.append("?");
      Set<String> keys = paraMap.keySet();
      Iterator<String> it = keys.iterator();
      String key = it.next();
      urlBuilder.append(key).append("=").append(paraMap.get(key));
      while (it.hasNext()) {
        key = it.next();
        urlBuilder.append("&").append(key).append("=").append(paraMap.get(key));
      }
    }
    return urlBuilder.toString();
  }

  private String getContent(HttpResponse response) {
    if (response == null) {
      return null;
    }
    HttpEntity httpEntity = response.getEntity();
    if (httpEntity == null) {
      return null;
    }
    String content = null;

    try {
      Charset defaultCharset = Charset.forName("utf-8");
      content = EntityUtils.toString(httpEntity, defaultCharset);
    } catch (IOException e) {
      logger.error("getContent error", e);
    } finally {
      closeHttpResponse(response);
    }
    return content;
  }

  public int getConnectionRequestTimeout() {
    return connectionRequestTimeout;
  }

  public void setConnectionRequestTimeout(int connectionRequestTimeout) {
    this.connectionRequestTimeout = connectionRequestTimeout;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getDefaultMaxPerRoute() {
    return defaultMaxPerRoute;
  }

  public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
    this.defaultMaxPerRoute = defaultMaxPerRoute;
  }

  public int getMaxTotal() {
    return maxTotal;
  }

  public void setMaxTotal(int maxTotal) {
    this.maxTotal = maxTotal;
  }

  public long getAutoCloseTimeout() {
    return autoCloseTimeout;
  }

  public void setAutoCloseTimeout(long autoCloseTimeout) {
    if (autoCloseTimeout <= 0 || autoCloseTimeout > 5000) {
      logger.warn(String.format("autoCloseTimeout must be from %s to %s", 0, 5000));
      return;
    }
    this.autoCloseTimeout = autoCloseTimeout;
  }

  protected void closeHttpResponse(HttpResponse response) {
    if (response instanceof CloseableHttpResponse) {
      CloseableHttpResponse closeResponse = (CloseableHttpResponse) response;

      HttpEntity httpEntity = closeResponse.getEntity();
      if (httpEntity != null) {
        InputStream is = null;
        try {
          is = httpEntity.getContent();
        } catch (IOException e) {
          logger.error("closeHttpResponse error", e);
        }
        IoUtil.close(is);
      }
      IoUtil.close(closeResponse);

    }
  }

  protected void closeHttpClient(HttpClient client) {
    if (client instanceof CloseableHttpClient) {
      CloseableHttpClient closeClient = (CloseableHttpClient) client;
      IoUtil.close(closeClient);
    }
  }

  protected String toString(InputStream is, String charsetName) {
    if (is == null) {
      return null;
    }
    BufferedReader reader = null;
    StringBuilder builder = new StringBuilder();
    String line = null;
    try {
      reader = new BufferedReader(new InputStreamReader(is, charsetName));
      while ((line = reader.readLine()) != null) {
        line = new String(line);
        builder.append(line + "/n");
      }
    } catch (IOException e) {
      logger.error("InputStream toString error", e);
    } finally {
      IoUtil.close(is);
      IoUtil.close(reader);
    }
    return builder.toString();
  }
}
