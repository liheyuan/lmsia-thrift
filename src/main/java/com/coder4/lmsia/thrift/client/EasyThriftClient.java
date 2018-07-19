package com.coder4.lmsia.thrift.client;

import com.coder4.lmsia.thrift.client.func.ThriftCallFunc;
import com.coder4.lmsia.thrift.client.func.ThriftExecFunc;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import static com.coder4.sbmvt.thrift.common.Constants.THRIFT_MAX_FRAME_SIZE;
import static com.coder4.sbmvt.thrift.common.Constants.THRIFT_TIMEOUT;

/**
 * @author coder4
 */
public class EasyThriftClient<TCLIENT extends TServiceClient> extends AbstractThriftClient<TCLIENT> {

    protected String thriftServerHost;

    protected int thriftServerPort;

    @Override
    protected boolean check() {
        if (thriftServerHost == null || thriftServerHost.isEmpty()) {
            return false;
        }
        if (thriftServerPort <= 0) {
            return false;
        }
        return super.check();
    }

    private TTransport borrowTransport() throws Exception {
        TSocket socket = new TSocket(thriftServerHost, thriftServerPort, THRIFT_TIMEOUT);

        TTransport transport = new TFramedTransport(
                socket, THRIFT_MAX_FRAME_SIZE);

        transport.open();

        return transport;
    }

    private void returnTransport(TTransport transport) {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }

    private void returnBrokenTransport(TTransport transport) {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }

    @Override
    public <TRET> TRET call(ThriftCallFunc<TCLIENT, TRET> tcall) {

        // Step 1: get TTransport
        TTransport tpt = null;
        try {
            tpt = borrowTransport();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Step 2: get client & call
        try {
            TCLIENT tcli = createClient(tpt);
            TRET ret = tcall.call(tcli);
            returnTransport(tpt);
            return ret;
        } catch (Exception e) {
            returnBrokenTransport(tpt);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exec(ThriftExecFunc<TCLIENT> texec) {
        // Step 1: get TTransport
        TTransport tpt = null;
        try {
            tpt = borrowTransport();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Step 2: get client & exec
        try {
            TCLIENT tcli = createClient(tpt);
            texec.exec(tcli);
            returnTransport(tpt);
        } catch (Exception e) {
            returnBrokenTransport(tpt);
            throw new RuntimeException(e);
        }
    }

    public String getThriftServerHost() {
        return thriftServerHost;
    }

    public void setThriftServerHost(String thriftServerHost) {
        this.thriftServerHost = thriftServerHost;
    }

    public int getThriftServerPort() {
        return thriftServerPort;
    }

    public void setThriftServerPort(int thriftServerPort) {
        this.thriftServerPort = thriftServerPort;
    }

}