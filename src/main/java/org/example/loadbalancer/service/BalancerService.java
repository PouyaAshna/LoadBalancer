package org.example.loadbalancer.service;

import org.example.loadbalancer.component.loadbalancer.LoadBalancer;

import java.io.InputStream;

public interface BalancerService {

    void start(InputStream inputStream);

    LoadBalancer chooseLoadBalancer(InputStream inputStream, LoadBalancer previousLoadBalancer);

    void routeRequest(LoadBalancer loadBalancer);

    void excludeProvider(InputStream inputStream, LoadBalancer loadBalancer);

    void includeProvider(InputStream inputStream, LoadBalancer loadBalancer);

    void checkHeartBeat();
}
