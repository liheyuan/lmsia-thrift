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

    private K8ServiceKey k8ServiceKey;

    private TTransportPool connPool;

    @Override
    public void init() {
        super.init();
        // check
        if (k8ServiceKey == null) {
            throw new RuntimeException("invalid k8ServiceName or k8Serviceport");
        }
        // init pool
        connPool = new TTransportPool(new TTransportPoolFactory());
    }

    @Override
    public <TRET> TRET call(ThriftCallFunc<TCLIENT, TRET> tcall) {

        // Step 1: get TTransport
        TTransport tpt = null;
        K8ServiceKey key = getConnBorrowKey();
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
        K8ServiceKey key = getConnBorrowKey();
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

    private K8ServiceKey getConnBorrowKey() {
        return k8ServiceKey;
    }

    private void returnTransport(K8ServiceKey key, TTransport transport) {
        connPool.returnObject(key, transport);
    }

    private void returnBrokenTransport(K8ServiceKey key, TTransport transport) {
        connPool.returnBrokenObject(key, transport);
    }

    public K8ServiceKey getK8ServiceKey() {
        return k8ServiceKey;
    }

    public void setK8ServiceKey(K8ServiceKey k8ServiceKey) {
        this.k8ServiceKey = k8ServiceKey;
    }

}