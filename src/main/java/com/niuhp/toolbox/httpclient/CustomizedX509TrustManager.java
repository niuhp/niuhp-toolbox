/**
 *
 */
package com.niuhp.toolbox.httpclient;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by niuhp on 2016/4/13.
 */
public class CustomizedX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
