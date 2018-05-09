package com.coder4.lmsia.thrift.client.builder;

import com.coder4.lmsia.thrift.client.EasyThriftClient;
import org.apache.thrift.TServiceClient;

/**
 * @author coder4
 */
public class EasyThriftClientBuilder<TCLIENT extends TServiceClient> {

    private final EasyThriftClient<TCLIENT> client = new EasyThriftClient<>();

    protected EasyThriftClient<TCLIENT> build() {
        client.init();
        return client;
    }

    protected EasyThriftClientBuilder<TCLIENT> setHost(String host) {
        client.setThriftServerHost(host);
        return this;
    }

    protected EasyThriftClientBuilder<TCLIENT> setPort(int port) {
        client.setThriftServerPort(port);
        return this;
    }

    protected EasyThriftClientBuilder<TCLIENT> setThriftClass(Class<?> thriftClass) {
        client.setThriftClass(thriftClass);
        return this;
    }
}