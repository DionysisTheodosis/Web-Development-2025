package gr.aegean.icsd.autorepair.demo;

import gr.aegean.icsd.autorepair.car.Car;
import gr.aegean.icsd.autorepair.car.CarRepository;
import gr.aegean.icsd.autorepair.car.CarType;
import gr.aegean.icsd.autorepair.car.FuelType;
import gr.aegean.icsd.autorepair.user.User;
import gr.aegean.icsd.autorepair.user.UserRepository;
import gr.aegean.icsd.autorepair.user.UserRole;
import gr.aegean.icsd.autorepair.user.customer.Customer;
import gr.aegean.icsd.autorepair.user.mechanic.Mechanic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data, skipping initialization");
            return;
        }

        log.info("Initializing development data...");

        String validPassword = passwordEncoder.encode("Password123!");

        List<User> users = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        User secretary = User.builder()
                .username("secretaryadmin")
                .email("secretary@autorepair.gr")
                .password(validPassword)
                .firstName("Maria")
                .lastName("Papadopoulou")
                .identityNumber("AB123456")
                .active(true)
                .role(UserRole.SECRETARY)
                .build();
        users.add(secretary);

        Customer customer1 = Customer.builder()
                .username("johndoe")
                .email("john.doe@email.com")
                .password(validPassword)
                .firstName("John")
                .lastName("Doe")
                .identityNumber("AB234567")
                .active(true)
                .role(UserRole.CUSTOMER)
                .taxNumber("123456789")
                .address("123 Main Street, Athens")
                .build();
        users.add(customer1);
        customers.add(customer1);

        Customer customer2 = Customer.builder()
                .username("mariasmith")
                .email("maria.smith@email.com")
                .password(validPassword)
                .firstName("Maria")
                .lastName("Smith")
                .identityNumber("AB345678")
                .active(true)
                .role(UserRole.CUSTOMER)
                .taxNumber("234567890")
                .address("456 Oak Avenue, Thessaloniki")
                .build();
        users.add(customer2);
        customers.add(customer2);

        Customer customer3 = Customer.builder()
                .username("georgebrown")
                .email("george.brown@email.com")
                .password(validPassword)
                .firstName("George")
                .lastName("Brown")
                .identityNumber("AB456789")
                .active(true)
                .role(UserRole.CUSTOMER)
                .taxNumber("345678901")
                .address("789 Pine Road, Patras")
                .build();
        users.add(customer3);
        customers.add(customer3);

        Customer customer4 = Customer.builder()
                .username("annawilson")
                .email("anna.wilson@email.com")
                .password(validPassword)
                .firstName("Anna")
                .lastName("Wilson")
                .identityNumber("AB567890")
                .active(true)
                .role(UserRole.CUSTOMER)
                .taxNumber("456789012")
                .address("321 Elm Street, Heraklion")
                .build();
        users.add(customer4);
        customers.add(customer4);

        Customer customer5 = Customer.builder()
                .username("nicktaylor")
                .email("nick.taylor@email.com")
                .password(validPassword)
                .firstName("Nick")
                .lastName("Taylor")
                .identityNumber("AB678901")
                .active(true)
                .role(UserRole.CUSTOMER)
                .taxNumber("567890123")
                .address("654 Maple Drive, Larissa")
                .build();
        users.add(customer5);
        customers.add(customer5);

        Customer customer6 = Customer.builder()
                .username("sophiaanderson")
                .email("sophia.anderson@email.com")
                .password(validPassword)
                .firstName("Sophia")
                .lastName("Anderson")
                .identityNumber("AB789012")
                .active(true)
                .role(UserRole.CUSTOMER)
                .taxNumber("678901234")
                .address("987 Cedar Lane, Volos")
                .build();
        users.add(customer6);
        customers.add(customer6);

        // 3 Mechanics
        Mechanic mechanic1 = Mechanic.builder()
                .username("mikemechanic")
                .email("mike.mechanic@autorepair.gr")
                .password(validPassword)
                .firstName("Mike")
                .lastName("Johnson")
                .identityNumber("AB890123")
                .active(true)
                .role(UserRole.MECHANIC)
                .specialty("Engine Repair")
                .build();
        users.add(mechanic1);

        Mechanic mechanic2 = Mechanic.builder()
                .username("alextech")
                .email("alex.tech@autorepair.gr")
                .password(validPassword)
                .firstName("Alex")
                .lastName("Thompson")
                .identityNumber("AB901234")
                .active(true)
                .role(UserRole.MECHANIC)
                .specialty("Electrical Systems")
                .build();
        users.add(mechanic2);

        Mechanic mechanic3 = Mechanic.builder()
                .username("chrisexpert")
                .email("chris.expert@autorepair.gr")
                .password(validPassword)
                .firstName("Chris")
                .lastName("Martinez")
                .identityNumber("AB012345")
                .active(true)
                .role(UserRole.MECHANIC)
                .specialty("Body Work and Painting")
                .build();
        users.add(mechanic3);

        userRepository.saveAll(users);
        log.info("Created {} users (1 secretary, 6 customers, 3 mechanics)", users.size());

        List<Car> cars = new ArrayList<>();

        cars.add(Car.builder()
                .serialNumber("1HGBH41JXMN109186")
                .model("Civic")
                .brand("Honda")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.HYBRID)
                .doorCount(4)
                .wheelCount(4)
                .productionDate(LocalDate.of(2020, 5, 15))
                .acquisitionYear(2020)
                .owner(customer1)
                .build());

        cars.add(Car.builder()
                .serialNumber("2FMDK3GC8BBA12345")
                .model("Focus")
                .brand("Ford")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.DIESEL)
                .doorCount(5)
                .wheelCount(4)
                .productionDate(LocalDate.of(2019, 8, 20))
                .acquisitionYear(2019)
                .owner(customer1)
                .build());

        cars.add(Car.builder()
                .serialNumber("3VWFE21C04M000001")
                .model("Golf")
                .brand("Volkswagen")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.HYBRID)
                .doorCount(5)
                .wheelCount(4)
                .productionDate(LocalDate.of(2021, 3, 10))
                .acquisitionYear(2021)
                .owner(customer2)
                .build());

        cars.add(Car.builder()
                .serialNumber("4T1BF1FK5CU123456")
                .model("Corolla")
                .brand("Toyota")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.HYBRID)
                .doorCount(4)
                .wheelCount(4)
                .productionDate(LocalDate.of(2018, 11, 5))
                .acquisitionYear(2019)
                .owner(customer3)
                .build());

        cars.add(Car.builder()
                .serialNumber("5UXWX7C5XBA789012")
                .model("X3")
                .brand("BMW")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.DIESEL)
                .doorCount(5)
                .wheelCount(4)
                .productionDate(LocalDate.of(2022, 1, 15))
                .acquisitionYear(2022)
                .owner(customer4)
                .build());

        cars.add(Car.builder()
                .serialNumber("WBA3A5C50CF234567")
                .model("Model S")
                .brand("Tesla")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.ELECTRIC)
                .doorCount(4)
                .wheelCount(4)
                .productionDate(LocalDate.of(2023, 6, 20))
                .acquisitionYear(2023)
                .owner(customer4)
                .build());

        cars.add(Car.builder()
                .serialNumber("WAUZZZ8V8DA345678")
                .model("A4")
                .brand("Audi")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.LPG)
                .doorCount(4)
                .wheelCount(4)
                .productionDate(LocalDate.of(2020, 9, 12))
                .acquisitionYear(2021)
                .owner(customer5)
                .build());

        cars.add(Car.builder()
                .serialNumber("JTHBF5C25A5456789")
                .model("IS300")
                .brand("Lexus")
                .carType(CarType.PASSENGER)
                .fuelType(FuelType.HYBRID)
                .doorCount(4)
                .wheelCount(4)
                .productionDate(LocalDate.of(2021, 7, 22))
                .acquisitionYear(2021)
                .owner(customer6)
                .build());

        cars.add(Car.builder()
                .serialNumber("1FTFW1ET5BFC67890")
                .model("F150")
                .brand("Ford")
                .carType(CarType.TRUCK)
                .fuelType(FuelType.DIESEL)
                .doorCount(4)
                .wheelCount(4)
                .productionDate(LocalDate.of(2019, 4, 18))
                .acquisitionYear(2020)
                .owner(customer6)
                .build());

        cars.add(Car.builder()
                .serialNumber("5FNRL6H78KB123789")
                .model("Odyssey")
                .brand("Honda")
                .carType(CarType.BUS)
                .fuelType(FuelType.DIESEL)
                .doorCount(5)
                .wheelCount(4)
                .productionDate(LocalDate.of(2020, 12, 3))
                .acquisitionYear(2021)
                .owner(customer2)
                .build());

        carRepository.saveAll(cars);
        log.info("Created {} cars", cars.size());

        log.info("Development data initialization completed successfully!");
    }
}
