package es.micro.bootservice;

import java.util.Collection;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
public class BootServiceApplication {

	@Bean
	CommandLineRunner commandLineRunner(ReservationRepository rr) {
		return strings -> {
			Stream.of("Josh", "Pieter", "Tasha", "Eric", "Susie", "Max")
			.forEach(n -> rr.save(new Reservation(n)));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BootServiceApplication.class, args);
	}

}

@RefreshScope
@RestController
class MessageRestController {
	
	@Value("${message}")
	private String msg;
	
	@RequestMapping("/message")
	String message() {
		return this.msg;
	}
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@RestResource(path = "by-name")
	Collection<Reservation> findByReservationName(@Param("rn") String rn);
}

@Entity
class Reservation {

	@Id
	@GeneratedValue
	private Long id;
	private String reservationName;

	public Reservation() {
		super();
	}

	public Reservation(String n) {
		this.reservationName = n;
	}

	@Override
	public String toString() {
		return "Reservation [id=" + id + ", reservationName=" + reservationName + "]";
	}

	public Long getId() {
		return id;
	}

	public String getReservationName() {
		return reservationName;
	}
}
