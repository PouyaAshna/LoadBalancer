package org.example.loadbalancer.component.loadbalancer.impl;

import org.example.loadbalancer.component.provider.Provider;
import org.example.loadbalancer.component.loadbalancer.LoadBalancer;
import org.example.loadbalancer.component.provider.ProviderState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseLoadBalancer implements LoadBalancer {

    private final List<Provider> providers = new ArrayList<>();

    private int retryThreshold = 0;

    public abstract int getProviderIndex();

    @Override
    public void registerProvider(Provider provider) {
        this.providers.add(provider);
    }

    @Override
    public String get() {
        if (retryThreshold == getProviders().size() && !getProviders().isEmpty()) {
            retryThreshold = 0;
            return null;
        }
        var providerIP = getProviders().get(getProviderIndex()).get();
        if (Objects.isNull(providerIP)){
            retryThreshold++;
            return get();
        }else {
            return providerIP;
        }
    }

    @Override
    public void registerProviders(List<Provider> providers) {
        this.providers.addAll(providers);
    }

    @Override
    public List<Provider> getProviders() {
        return providers.stream().filter(Provider::isReady).toList();
    }

    @Override
    public List<Provider> getAllProviders() {
        return providers;
    }

    @Override
    public boolean excludeProvider(String providerIdentifier) {
        var providerOptional = this.providers.stream()
                .filter(provider -> providerIdentifier.equals(provider.getIdentifier()))
                .findAny();
        if (providerOptional.isPresent()) {
            providerOptional.get().transformState(ProviderState.UNREADY);
            return true;
        } else return false;
    }

    @Override
    public boolean includeProvider(String providerIdentifier) {
        var providerOptional = this.providers.stream()
                .filter(provider -> providerIdentifier.equals(provider.getIdentifier()))
                .findAny();
        if (providerOptional.isPresent()) {
            providerOptional.get().transformState(ProviderState.READY);
            return true;
        } else return false;
    }
}
