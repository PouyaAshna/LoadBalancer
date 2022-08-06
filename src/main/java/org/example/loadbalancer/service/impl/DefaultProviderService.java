package org.example.loadbalancer.service.impl;

import org.example.loadbalancer.component.loadbalancer.LoadBalancer;
import org.example.loadbalancer.component.provider.Provider;
import org.example.loadbalancer.component.provider.ProviderHeartBeetState;
import org.example.loadbalancer.service.ProviderService;
import org.example.loadbalancer.util.PrinterUtil;
import org.example.loadbalancer.util.annotation.Component;

import java.io.InputStream;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class DefaultProviderService implements ProviderService {
    @Override
    public String getProviderNames(LoadBalancer loadBalancer) {
        return loadBalancer.getProviders().stream().map(Provider::getIdentifier).collect(Collectors.joining(", "));
    }

    @Override
    public void registerProvidersToLoadBalancer(InputStream inputStream, LoadBalancer loadBalancer) {
        var scanner = new Scanner(inputStream);
        PrinterUtil.print("Please enter how many provider (maximum is 10) and the capacity of each provider that you want: for example (5,3) it means that 5 provider and each provider can handle 3 request");
        var line = scanner.next();
        var inputs = line.trim().split(",");
        for (int i = 0; i < Math.min(Integer.parseInt(inputs[0]), 10); i++) {
            loadBalancer.registerProvider(new Provider(Integer.parseInt(inputs[1])));
        }
        PrinterUtil.print("Providers registered to load balancer. current provider list size is : %s", loadBalancer.getProviders().size());
    }

    @Override
    public void turnOffProvider(InputStream inputStream, LoadBalancer loadBalancer) {
        Scanner scanner = new Scanner(inputStream);
        PrinterUtil.print("Please enter the identifier of the provider that you want to turn off");
        var providerIdentifier = scanner.next();
        var providerOptional = loadBalancer.getAllProviders().stream()
                .filter(provider -> providerIdentifier.equals(provider.getIdentifier()))
                .findAny();
        boolean isTurnOffSuccessfully;
        if (providerOptional.isPresent()) {
            providerOptional.get().setHeartBeet(ProviderHeartBeetState.DOWN);
            isTurnOffSuccessfully = true;
        } else {
            isTurnOffSuccessfully = false;
        }
        PrinterUtil.print(isTurnOffSuccessfully ? "Provider turned off successfully" : "Provider can't turn off");
    }

    @Override
    public void turnOnProvider(InputStream inputStream, LoadBalancer loadBalancer) {
        Scanner scanner = new Scanner(inputStream);
        PrinterUtil.print("Please enter the identifier of the provider that you want to turn on");
        var providerIdentifier = scanner.next();
        var providerOptional = loadBalancer.getAllProviders().stream()
                .filter(provider -> providerIdentifier.equals(provider.getIdentifier()))
                .findAny();
        boolean isTurnOnSuccessfully;
        if (providerOptional.isPresent()) {
            providerOptional.get().setHeartBeet(ProviderHeartBeetState.UP);
            isTurnOnSuccessfully = true;
        } else {
            isTurnOnSuccessfully = false;
        }
        PrinterUtil.print(isTurnOnSuccessfully ? "Provider turned on successfully" : "Provider can't turn on");

    }
}
