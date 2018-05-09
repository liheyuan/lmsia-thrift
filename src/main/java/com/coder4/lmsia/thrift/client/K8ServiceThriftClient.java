package com.coder4.lmsia.thrift.client;

import com.coder4.lmsia.thrift.client.func.ThriftCallFunc;
import com.coder4.lmsia.thrift.client.func.ThriftExecFunc;
import com.coder4.lmsia.thrift.client.pool.TTransportPool;
import com.coder4.lmsia.thrift.client.pool.TTransportPoolFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;

/**
 * @author coder4
 */
public class K8ServiceThriftClient<TCLIENT extends TServiceClient>
        extends AbstractThriftClient<TCLIENT> {

    private String k8ServiceName;

    private int k8ServicePort;

    private TTransportPool connPool;

    @Override
    public void init() {
        super.init();
        // check
        if (k8ServiceName == null || k8ServiceName.isEmpty()) {
            throw new RuntimeException("invalid k8ServiceName");
        }
        if (k8ServicePort == 0) {
            throw new RuntimeException("invalid k8ServicePort");
        }
        // init pool
        connPool = new TTransportPool(new TTransportPoolFactory());
    }

    @Override
    public <TRET> TRET call(ThriftCallFunc<TCLIENT, TRET> tcall) {

        // Step 1: get TTransport
        TTransport tpt = null;
        String key = getConnBorrowKey();
        try {
            tpt = connPool.borrowObject(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Step 2: get client & call
        try {
            TCLIENT tcli = createClient(tpt);
            TRET ret = tcall.call(tcli);
            returnTransport(key, tpt);
            return ret;
        } catch (Exception e) {
            returnBrokenTransport(key, tpt);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exec(ThriftExecFunc<TCLIENT> texec) {
        // Step 1: get TTransport
        TTransport tpt = null;
        String key = getConnBorrowKey();
        try {

            // borrow transport
            tpt = connPool.borrowObject(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Step 2: get client & exec
        try {
            TCLIENT tcli = createClient(tpt);
            texec.exec(tcli);
            returnTransport(key, tpt);
        } catch (Exception e) {
            returnBrokenTransport(key, tpt);
            throw new RuntimeException(e);
        }
    }

    private String getConnBorrowKey() {

        // Get ip and port
        String ip = k8ServiceName;
        int port = k8ServicePort;
        String key = getConnPoolKey(ip, port);
        return key;
    }

    private void returnTransport(String key, TTransport transport) {
        connPool.returnObject(key, transport);
    }

    private void returnBrokenTransport(String key, TTransport transport) {
        connPool.returnBrokenObject(key, transport);
    }

    private String getConnPoolKey(String host, int port) {
        return host + ":" + port;
    }

    public String getK8ServiceName() {
        return k8ServiceName;
    }

    public void setK8ServiceName(String k8ServiceName) {
        this.k8ServiceName = k8ServiceName;
    }

    public int getK8ServicePort() {
        return k8ServicePort;
    }

    public void setK8ServicePort(int k8ServicePort) {
        this.k8ServicePort = k8ServicePort;
    }
}