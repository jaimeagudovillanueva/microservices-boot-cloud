package es.micro.bootclient;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableZuulProxy
@SpringBootApplication
public class BootClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootClientApplication.class, args);
    }
}


@Component
class ReservationIntegration {

    @Autowired
    private ReservationsRestClient reservationsRestClientProxy;

    public Collection<String> getReservationNamesFallback() {
        return Collections.emptyList();
    }

    @HystrixCommand(fallbackMethod = "getReservationNamesFallback")
    public Collection<String> getReservationNames() {
    	
    	Collection<Reservation> resultado = reservationsRestClientProxy.getReservations().getContent();
    	
        return resultado
                .stream()
                .map(Reservation::getReservationName)
                .collect(Collectors.toList());
    }

}

@RestController
@RequestMapping ("/reservations")
class ReservationNamesRestController {

    @RequestMapping("/names")
    Collection<String> rs() {
        return this.reservationIntegration.getReservationNames();
    }

    @Autowired
    private ReservationIntegration reservationIntegration;

}


@FeignClient("boot-service")
interface ReservationsRestClient {

    @RequestMapping(value = "/reservations", method = RequestMethod.GET)
    Resources<Reservation> getReservations();
}

class Reservation {

    private Long id;
    private String reservationName;

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationName='" + reservationName + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationName() {
        return reservationName;
    }

    public void setReservationName(String reservationName) {
        this.reservationName = reservationName;
    }
}