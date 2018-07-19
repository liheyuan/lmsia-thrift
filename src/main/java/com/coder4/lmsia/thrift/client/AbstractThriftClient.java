package com.coder4.lmsia.thrift.client;

import com.coder4.lmsia.thrift.client.func.ThriftCallFunc;
import com.coder4.lmsia.thrift.client.func.ThriftExecFunc;
import com.coder4.sbmvt.thrift.common.TraceBinaryProtocol.Factory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author coder4
 */
public abstract class AbstractThriftClient<TCLIENT extends TServiceClient> implements ThriftClient<TCLIENT> {

    private Class<?> thriftClass;

    private static final Factory protocolFactory = new Factory();

    private TServiceClientFactory<TCLIENT> clientFactory;

    // For async call
    private ExecutorService threadPool;

    public void init() {
        try {
            clientFactory = getThriftClientFactoryClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        if (!check()) {
            throw new RuntimeException("Client config failed check!");
        }

        threadPool = new ThreadPoolExecutor(
                10, 100, 10,
                TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>());
    }

    protected boolean check() {
        if (thriftClass == null) {
            return false;
        }
        return true;
    }

    @Override
    public <TRET> Future<TRET> asyncCall(ThriftCallFunc<TCLIENT, TRET> tcall) {
        return threadPool.submit(() -> this.call(tcall));
    }

    @Override
    public <TRET> Future<?> asyncExec(ThriftExecFunc<TCLIENT> texec) {
        return threadPool.submit(() -> this.exec(texec));
    }

    protected TCLIENT createClient(TTransport transport) throws Exception {
        // Step 1: get TProtocol
        TProtocol protocol = protocolFactory.getProtocol(transport);

        // Step 2: get client
        return clientFactory.getClient(protocol);
    }

    private Class<TServiceClientFactory<TCLIENT>> getThriftClientFactoryClass() {
        Class<TCLIENT> clientClazz = getThriftClientClass();
        if (clientClazz == null) {
            return null;
        }
        for (Class<?> clazz : clientClazz.getDeclaredClasses()) {
            if (TServiceClientFactory.class.isAssignableFrom(clazz)) {
                return (Class<TServiceClientFactory<TCLIENT>>) clazz;
            }
        }
        return null;
    }

    private Class<TCLIENT> getThriftClientClass() {
        for (Class<?> clazz : thriftClass.getDeclaredClasses()) {
            if (TServiceClient.class.isAssignableFrom(clazz)) {
                return (Class<TCLIENT>) clazz;
            }
        }
        return null;
    }

    public void setThriftClass(Class<?> thriftClass) {
        this.thriftClass = thriftClass;
    }
}