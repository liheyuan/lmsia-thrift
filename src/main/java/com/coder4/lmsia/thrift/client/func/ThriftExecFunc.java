package com.coder4.lmsia.thrift.client.func;

import org.apache.thrift.TServiceClient;

/**
 * @author coder4
 */
public interface ThriftExecFunc<TCLIENT extends TServiceClient> {

    void exec(TCLIENT tclient) throws Exception;

}