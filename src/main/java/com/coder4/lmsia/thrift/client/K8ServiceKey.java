package com.coder4.lmsia.thrift.client;

/**
 * @author coder4
 */
public class K8ServiceKey {

    private String k8ServiceHost;

    private int k8ServicePort;

    public K8ServiceKey(String k8ServiceHost, int k8ServicePort) {
        this.k8ServiceHost = k8ServiceHost;
        this.k8ServicePort = k8ServicePort;
    }

    public String getK8ServiceHost() {
        return k8ServiceHost;
    }

    public void setK8ServiceHost(String k8ServiceHost) {
        this.k8ServiceHost = k8ServiceHost;
    }

    public int getK8ServicePort() {
        return k8ServicePort;
    }

    public void setK8ServicePort(int k8ServicePort) {
        this.k8ServicePort = k8ServicePort;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("K8ServiceKey{");
        sb.append("k8ServiceHost='").append(k8ServiceHost).append('\'');
        sb.append(", k8ServicePort=").append(k8ServicePort);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        K8ServiceKey that = (K8ServiceKey) o;

        if (k8ServicePort != that.k8ServicePort) return false;
        return k8ServiceHost != null ? k8ServiceHost.equals(that.k8ServiceHost) : that.k8ServiceHost == null;
    }

    @Override
    public int hashCode() {
        int result = k8ServiceHost != null ? k8ServiceHost.hashCode() : 0;
        result = 31 * result + k8ServicePort;
        return result;
    }

}