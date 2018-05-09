package com.coder4.lmsia.thrift.client.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author coder4
 */
public class TTransportPool extends GenericKeyedObjectPool<String, TTransport> {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private static int MAX_CONN = 1024;
    private static int MIN_IDLE_CONN = 8;
    private static int MAX_IDLE_CONN = 32;

    public TTransportPool(TTransportPoolFactory factory) {
        super(factory);

        setTimeBetweenEvictionRunsMillis(45 * 1000);
        setNumTestsPerEvictionRun(5);
        setMaxWaitMillis(30 * 1000);

        setMaxTotal(MAX_CONN);
        setMaxTotalPerKey(MAX_CONN);
        setMinIdlePerKey(MIN_IDLE_CONN);
        setMaxTotalPerKey(MAX_IDLE_CONN);

        setTestOnCreate(true);
        setTestOnBorrow(true);
        setTestWhileIdle(true);
    }

    @Override
    public TTransportPoolFactory getFactory() {
        return (TTransportPoolFactory) super.getFactory();
    }

    public void returnBrokenObject(String url, TTransport transport) {
        try {
            invalidateObject(url, transport);
        } catch (Exception e) {
            LOG.warn("return broken url " + url);
            e.printStackTrace();
        }
    }
}