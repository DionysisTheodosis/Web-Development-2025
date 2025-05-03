package gr.aegean.icsd.autorepair.user.customer;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private boolean updateAddressIfNeeded(@NonNull Customer user,@NonNull String address) {
        if(null!=user.getAddress() && user.getAddress().equals(address)) {
            return false;
        }
        user.setAddress(address);
        return true;
    }

    private boolean updateTaxNumberIfNeeded(@NonNull  Customer user,@NonNull String taxNumber) {
        if(null!= user.getTaxNumber() && user.getTaxNumber().equals(taxNumber)) {
            return false;
        }
        user.setTaxNumber(taxNumber);
        return true;
    }
}
