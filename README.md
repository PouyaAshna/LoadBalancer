# LoadBalancer Service
## Purpose
This project aims to create a command line application to implement different load balancing strategies.

## How it works
You must provide some inputs to configure the application when you run the application. After you config your application, you will see an instruction list of how the application works.

## Instruction Menu
1. Provider lists: when you enter one, you will see the list of provider identifiers that registered to the load balancer
2. Change load balancer: by pressing number 2, you can change the algorithm of the load balancer. Currently, there are two algorithms for load balancing 1)RoundRobin and 2)Random
3. Add more providers: by pressing this number, you can add more providers to your load balancer
4. Route: when you press four, the application simulates a request that load balanced from the load balancer to the provider
5. Manual exclude provider: with this option, you will be able to exclude a provider that registered to the load balancer
6. Manual include provider: with this option, you will be able to include a provider that registered to the load balancer
7. Turn-off provider: this option is used to power off a specific provider, and it's used for heartbeat checker
8. Turn-on provider: this option is used to power on a specific provider, and it's used for heartbeat checker
9. Show instruction: when you press nine, you will see the instruction menu again
10. Exit: by pressing this button the application will be shutdown

## Solution
### Dependency Injection
I implement different annotations and use those annotations to bind objects together and also provide a mechanism same as
spring IOC to inject all implementation of an interface into a map for choosing different load balancing strategies.
### Load Balancer Component
I create a load balancer interface and also a BaseLoadBalancer abstract class to implement that interface and manage some
common use cases that different strategies have. For example, for routing the request to providers, I create an abstract
method called getProviderIndex that each implementation overrides this method and add a specific logic to it so I can 
implement the get method on the base load balancer class and just use that method for the provider index. It gives me 
outstanding flexibility to add more strategies.
### Provider Component
I bind the provider component to the load balancer and add two states for it, one for being ready and unready and one 
for a heartbeat checker. So when you want to exclude a provider, you change the provider's state, and after that, 
it will not return to the load balancer. Still, we have another scenario where maybe the provider becomes turned off. 
Also, there is a mechanism called a heartbeat checker on the load balancer to automatically include and exclude 
providers based on their heartbeat.
