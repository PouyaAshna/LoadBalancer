package org.example.loadbalancer.component.loadbalancer.impl;

import org.example.loadbalancer.util.annotation.Component;

@Component("roundRobin")
public class RoundRobinLoadBalancer extends BaseLoadBalancer {

    private int providerIndex = 0;

    @Override
    public int getProviderIndex() {
        if (providerIndex >= getProviders().size()) {
            providerIndex = 0;
        }
        return providerIndex++;
    }
}
