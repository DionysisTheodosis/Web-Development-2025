package gr.aegean.icsd.autorepair.user.customer;

import gr.aegean.icsd.autorepair.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper customerMapper;

    @Transactional
    public void addCustomer(CustomerRegistrationDto dto) {
        repository.save(customerMapper.mapDtoToEntity(dto));
    }

    public boolean updateCustomerFields(@NonNull Customer user,@NonNull CustomerUpdateDto dto) {
        boolean updated = false;
        updated|= updateAddressIfNeeded(user,dto.getAddress());
        updated |= updateTaxNumberIfNeeded(user,dto.getTaxNumber());
        return updated;
    }

    private boolean updateAddressIfNeeded(Customer user, String newAddress) {
        if (newAddress == null || newAddress.isBlank()) {
            return false;
        }
        if (newAddress.equals(user.getAddress())) {
            return false;
        }
        user.setAddress(newAddress);
        return true;
    }

    private boolean updateTaxNumberIfNeeded(Customer user, String newTaxNumber) {
        if (newTaxNumber == null || newTaxNumber.isBlank()) {
            return false;
        }
        if (newTaxNumber.equals(user.getTaxNumber())) {
            return false;
        }
        user.setTaxNumber(newTaxNumber);
        return true;
    }
    @Transactional
    public Customer getCustomerWithDetails(Long id) {
        Customer customer = repository.findByIdWithCars(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        customer.getAppointments().size();
        return customer;
    }

    @Transactional
    public Customer findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

}
