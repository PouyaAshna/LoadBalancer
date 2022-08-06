package org.example.loadbalancer.component.provider;

import org.example.loadbalancer.service.dto.RequestDTO;
import org.example.loadbalancer.util.RandomUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Provider {

    public static final int REQUEST_TTL = 10;
    private final String identifier;
    private int heartCheckThreshold = 0;
    private boolean heartBeatCheck = true;
    private ProviderState state;
    private ProviderHeartBeetState heartBeetState;
    private final int capacity;

    private List<RequestDTO> requests = new ArrayList<>();

    public Provider(int capacity) {
        var randomUtil = RandomUtil.getRandom();
        identifier = randomUtil.nextInt(256) + "." + randomUtil.nextInt(256) + "." + randomUtil.nextInt(256) + "." + randomUtil.nextInt(256);
        state = ProviderState.READY;
        heartBeetState = ProviderHeartBeetState.UP;
        this.capacity = capacity;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String get() {
        requests = requests.stream()
                .filter(request -> Instant.now().isBefore(request.getCreatedAt().plusSeconds(REQUEST_TTL)))
                .collect(Collectors.toList());
        if (requests.size() < capacity) {
            requests.add(new RequestDTO());
            return identifier;
        } else {
            return null;
        }
    }

    public boolean isReady() {
        return ProviderState.READY.equals(state);
    }

    public boolean check() {
        if (ProviderHeartBeetState.UP.equals(heartBeetState) && !heartBeatCheck) {
            heartCheckThreshold++;
        }
        if (heartCheckThreshold == 2) {
            heartBeatCheck = true;
            heartCheckThreshold = 0;
        }
        return heartBeatCheck;
    }

    public void transformState(ProviderState state) {
        this.state = state;
    }

    public void setHeartBeet(ProviderHeartBeetState heartBeetState) {
        this.heartBeetState = heartBeetState;
        if (ProviderHeartBeetState.DOWN.equals(heartBeetState)) {
            heartBeatCheck = false;
            heartCheckThreshold = 0;
        }
    }
}
