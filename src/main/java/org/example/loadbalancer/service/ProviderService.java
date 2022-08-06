package org.example.loadbalancer.service;

import org.example.loadbalancer.component.loadbalancer.LoadBalancer;

import java.io.InputStream;

public interface ProviderService {

    String getProviderNames(LoadBalancer loadBalancer);

    void registerProvidersToLoadBalancer(InputStream inputStream, LoadBalancer loadBalancer);

    void turnOffProvider(InputStream inputStream, LoadBalancer loadBalancer);

    void turnOnProvider(InputStream inputStream, LoadBalancer loadBalancer);
}
