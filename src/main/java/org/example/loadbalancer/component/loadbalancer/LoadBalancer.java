package org.example.loadbalancer.component.loadbalancer;

import org.example.loadbalancer.component.provider.Provider;

import java.util.List;

public interface LoadBalancer {

    String get();

    void registerProvider(Provider provider);

    void registerProviders(List<Provider> providers);

    List<Provider> getProviders();

    List<Provider> getAllProviders();

    boolean excludeProvider(String providerIdentifier);

    boolean includeProvider(String providerIdentifier);
}
