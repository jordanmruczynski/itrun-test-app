package pl.jordanmruczynski.itruntestapp.services;

import org.springframework.stereotype.Service;
import pl.jordanmruczynski.itruntestapp.Person;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final String INTERNAL_DIR = "Internal";
    private static final String EXTERNAL_DIR = "External";
    private final JAXBContext jaxbContext;

    public EmployeeServiceImpl() throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(Person.class);
    }

    public Mono<Optional<Person>> findEmployee(String type, String firstName, String lastName, String mobile) {
        return Mono.fromCallable(() -> {
            File directory = new File(getDirectoryPath(type));
            return Stream.of(Optional.ofNullable(directory.listFiles()).orElse(new File[0]))
                    .filter(file -> file.getName().endsWith(".xml"))
                    .map(file -> {
                        try {
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                            Person person = (Person) unmarshaller.unmarshal(file);
                            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName) && person.getMobile().equals(mobile)) {
                                return Optional.of(person);
                            }
                        } catch (JAXBException e) {
                            e.printStackTrace();
                        }
                        return Optional.<Person>empty();
                    })
                    .filter(Optional::isPresent)
                    .findFirst()
                    .orElse(Optional.empty());
        });
    }

    @Override
    public Mono<Void> createEmployee(String type, Person person) {
        return Mono.fromRunnable(() -> {
            try {
                Marshaller marshaller = jaxbContext.createMarshaller();
                File directory = new File(getDirectoryPath(type));
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, person.getPersonId() + ".xml");
                marshaller.marshal(person, file);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Mono<Boolean> removeEmployee(String type, String personId) {
        return Mono.fromSupplier(() -> {
            File file = new File(getDirectoryPath(type), personId + ".xml");
            return file.exists() && file.delete();
        });
    }

    @Override
    public Mono<Void> modifyEmployee(String type, Person person) {
        return Mono.fromRunnable(() -> {
            try {
                removeEmployee(type, person.getPersonId()).block();
                createEmployee(type, person).block();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getDirectoryPath(String type) {
        return Paths.get(type.equals("INTERNAL") ? INTERNAL_DIR : EXTERNAL_DIR).toAbsolutePath().toString();
    }
}
