package pl.jordanmruczynski.itruntestapp;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class EmployeeService {
    private static final String INTERNAL_DIR = "Internal";
    private static final String EXTERNAL_DIR = "External";
    private final JAXBContext jaxbContext;

    public EmployeeService() throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(Person.class);
    }

    public Mono<Person> findEmployee(String type, String firstName, String lastName, String mobile) {
        return Mono.fromCallable(() -> {
            File directory = new File(getDirectoryPath(type));
            return Stream.of(directory.listFiles())
                    .filter(file -> file.getName().endsWith(".xml"))
                    .map(file -> {
                        try {
                            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                            Person person = (Person) unmarshaller.unmarshal(file);
                            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName) && person.getMobile().equals(mobile)) {
                                return person;
                            }
                        } catch (JAXBException e) {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        });
    }

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

    public Mono<Boolean> removeEmployee(String type, String personId) {
        return Mono.fromSupplier(() -> {
            File file = new File(getDirectoryPath(type), personId + ".xml");
            return file.exists() && file.delete();
        });
    }

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
