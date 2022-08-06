package org.example.loadbalancer.service.impl;

import org.example.loadbalancer.component.loadbalancer.LoadBalancer;
import org.example.loadbalancer.component.provider.ProviderState;
import org.example.loadbalancer.service.BalancerService;
import org.example.loadbalancer.service.InstructionService;
import org.example.loadbalancer.service.ProviderService;
import org.example.loadbalancer.util.PrinterUtil;
import org.example.loadbalancer.util.annotation.Autowired;
import org.example.loadbalancer.util.annotation.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class DefaultBalancerService implements BalancerService {

    @Autowired
    private Map<String, LoadBalancer> loadBalancerMap;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private InstructionService instructionService;

    private LoadBalancer loadbalancer;

    @Override
    public void start(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        loadbalancer = this.chooseLoadBalancer(inputStream, null);
        this.providerService.registerProvidersToLoadBalancer(inputStream, loadbalancer);
        this.checkHeartBeat();
        this.instructionService.showInstruction();
        while (scanner.hasNext()) {
            var choice = scanner.next();
            switch (choice) {
                case "1":
                    PrinterUtil.print(this.providerService.getProviderNames(loadbalancer));
                    break;
                case "2":
                    loadbalancer = this.chooseLoadBalancer(inputStream, loadbalancer);
                    break;
                case "3":
                    this.providerService.registerProvidersToLoadBalancer(inputStream, loadbalancer);
                    break;
                case "4":
                    this.routeRequest(loadbalancer);
                    break;
                case "5":
                    this.excludeProvider(inputStream, loadbalancer);
                    break;
                case "6":
                    this.includeProvider(inputStream, loadbalancer);
                    break;
                case "7":
                    this.providerService.turnOffProvider(inputStream, loadbalancer);
                    break;
                case "8":
                    this.providerService.turnOnProvider(inputStream, loadbalancer);
                    break;
                case "9":
                    this.instructionService.showInstruction();
                    break;
                case "10":
                    System.exit(1);
                    break;
                default:
                    this.instructionService.showInstruction();
            }
        }
    }

    @Override
    public LoadBalancer chooseLoadBalancer(InputStream inputStream, LoadBalancer previousLoadBalancer) {
        Scanner scanner = new Scanner(inputStream);
        String loadbalancerAlgorithm;
        do {
            PrinterUtil.print("Please enter load balancing algorithm(enter the name of the algorithm): your choices are 1)random 2)roundRobin");
            loadbalancerAlgorithm = scanner.next();
        } while (!loadBalancerMap.containsKey(loadbalancerAlgorithm));
        var loadBalancer = loadBalancerMap.get(loadbalancerAlgorithm);
        if (Objects.nonNull(previousLoadBalancer)) {
            loadBalancer.registerProviders(previousLoadBalancer.getProviders());
        }
        PrinterUtil.print("Loadbalancer initialize successfully. current load balancer is : %s", loadBalancer.getClass().getSimpleName());
        return loadBalancer;
    }

    @Override
    public void routeRequest(LoadBalancer loadBalancer) {
        var providerIP = loadBalancer.get();
        if (Objects.isNull(providerIP)) {
            PrinterUtil.print("There is no available provider");
        } else {
            PrinterUtil.print("Request route to %s", providerIP);
        }
    }

    @Override
    public void excludeProvider(InputStream inputStream, LoadBalancer loadBalancer) {
        Scanner scanner = new Scanner(inputStream);
        PrinterUtil.print("Please enter the identifier of the provider that you want to exclude");
        var providerIdentifier = scanner.next();
        var isExcludeSuccessfully = loadBalancer.excludeProvider(providerIdentifier);
        PrinterUtil.print(isExcludeSuccessfully ? "Provider excluded successfully" : "Provider can't exclude");
    }

    @Override
    public void includeProvider(InputStream inputStream, LoadBalancer loadBalancer) {
        Scanner scanner = new Scanner(inputStream);
        PrinterUtil.print("Please enter the identifier of the provider that you want to include");
        var providerIdentifier = scanner.next();
        var isExcludeSuccessfully = loadBalancer.includeProvider(providerIdentifier);
        PrinterUtil.print(isExcludeSuccessfully ? "Provider included successfully" : "Provider can't include");
    }

    @Override
    public void checkHeartBeat() {
        var scheduledExecutors = Executors.newScheduledThreadPool(1);
        scheduledExecutors.scheduleWithFixedDelay(() -> loadbalancer.getAllProviders().forEach(provider -> {
            var check = provider.check();
            if (check) {
                provider.transformState(ProviderState.READY);
            } else {
                provider.transformState(ProviderState.UNREADY);
            }
        }), 0, 2, TimeUnit.SECONDS);
    }
}
