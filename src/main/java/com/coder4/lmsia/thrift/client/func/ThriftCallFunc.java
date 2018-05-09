package com.coder4.lmsia.thrift.client.func;

import org.apache.thrift.TServiceClient;

/**
 * @author coder4
 */
public interface ThriftCallFunc<TCLIENT extends TServiceClient, TRET> {

    TRET call(TCLIENT tcall) throws Exception;

}