package org.example.loadbalancer.component.loadbalancer.impl;

import org.example.loadbalancer.util.RandomUtil;
import org.example.loadbalancer.util.annotation.Component;

@Component("random")
public class RandomLoadBalancer extends BaseLoadBalancer {

    @Override
    public int getProviderIndex() {
        var random = RandomUtil.getRandom();
        return random.nextInt(getProviders().size());
    }
}
