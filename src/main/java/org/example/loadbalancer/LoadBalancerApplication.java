package org.example.loadbalancer;

import org.example.loadbalancer.service.BalancerService;
import org.example.loadbalancer.util.injector.DependencyInjector;

public class LoadBalancerApplication {

    public static void main(String[] args) {
        DependencyInjector.startApplication(LoadBalancerApplication.class);
        DependencyInjector.getService(BalancerService.class).start(System.in);
    }
}
