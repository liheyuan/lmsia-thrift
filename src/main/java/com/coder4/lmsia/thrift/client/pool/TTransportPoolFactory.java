package com.coder4.lmsia.thrift.client.pool;

import com.coder4.lmsia.thrift.client.K8ServiceKey;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * @author coder4
 */
public class TTransportPoolFactory extends BaseKeyedPooledObjectFactory<K8ServiceKey, TTransport> {

    protected static final int THRIFT_CLIENT_DEFAULT_TIMEOUT = 5000;

    protected static final int THRIFT_CLIENT_DEFAULT_MAX_FRAME_SIZE = 1024 * 1024 * 16;

    @Override
    public TTransport create(K8ServiceKey key) throws Exception {
        if (key != null) {
            String host = key.getK8ServiceHost();
            int port = key.getK8ServicePort();
            TSocket socket = new TSocket(host, port, THRIFT_CLIENT_DEFAULT_TIMEOUT);

            TTransport transport = new TFramedTransport(
                    socket, THRIFT_CLIENT_DEFAULT_MAX_FRAME_SIZE);

            transport.open();

            return transport;
        } else {
            return null;
        }
    }

    @Override
    public PooledObject<TTransport> wrap(TTransport transport) {
        return new DefaultPooledObject<>(transport);
    }

    @Override
    public void destroyObject(K8ServiceKey key, PooledObject<TTransport> obj) throws Exception {
        obj.getObject().close();
    }

    @Override
    public boolean validateObject(K8ServiceKey key, PooledObject<TTransport> obj) {
        return obj.getObject().isOpen();
    }

}