package com.coder4.lmsia.thrift.client.builder;

import com.coder4.lmsia.thrift.client.K8ServiceKey;
import com.coder4.lmsia.thrift.client.K8ServiceThriftClient;
import org.apache.thrift.TServiceClient;

/**
 * @author coder4
 */
public class K8ServiceThriftClientBuilder<TCLIENT extends TServiceClient> {

    private final K8ServiceThriftClient<TCLIENT> client = new K8ServiceThriftClient<>();

    protected K8ServiceThriftClient<TCLIENT> build() {
        client.init();
        return client;
    }

    protected K8ServiceThriftClientBuilder<TCLIENT> setK8ServiceKey(K8ServiceKey key) {
        client.setK8ServiceKey(key);
        return this;
    }

    protected K8ServiceThriftClientBuilder<TCLIENT> setThriftClass(Class<?> thriftClass) {
        client.setThriftClass(thriftClass);
        return this;
    }
}