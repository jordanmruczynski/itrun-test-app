package pl.jordanmruczynski.itruntestapp.services;

import pl.jordanmruczynski.itruntestapp.Person;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface EmployeeService {
    Mono<Optional<Person>> findEmployee(String type, String firstName, String lastName, String mobile);
    Mono<Void> createEmployee(String type, Person person);
    Mono<Boolean> removeEmployee(String type, String personId);
    Mono<Void> modifyEmployee(String type, Person person);
}
