package org.peng.sucicada;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class MyProxy {
    static String proxyHost = "127.0.0.1";
    static int proxyPort = 10809;
    static void setProxy() {
        ProxySelector.setDefault(new ProxySelector() {

            @Override
            public List<Proxy> select(URI uri) {
                return Collections.singletonList(new Proxy(Proxy.Type.HTTP, InetSocketAddress
                        .createUnresolved(proxyHost, proxyPort)));
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                if (uri == null || sa == null || ioe == null) {
                    throw new IllegalArgumentException(
                            "Arguments can't be null.");
                }
            }
        });
    }
}
