package pl.jordanmruczynski.itruntestapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jordanmruczynski.itruntestapp.services.EmployeeService;
import pl.jordanmruczynski.itruntestapp.services.EmployeeServiceImpl;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Mono<Optional<Person>> findEmployee(@RequestParam String type, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String mobile) {
        return employeeService.findEmployee(type, firstName, lastName, mobile);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Mono<Void> createEmployee(@RequestParam String type, @RequestBody Person person) {
        return employeeService.createEmployee(type, person);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public Mono<Boolean> deleteEmployee(@RequestParam String type, @RequestParam String personId) {
        return employeeService.removeEmployee(type, personId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Mono<Void> modifyEmployee(@RequestParam String type, @RequestBody Person person) {
        return employeeService.modifyEmployee(type, person);
    }
}
