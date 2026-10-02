package app.service;

import app.dao.AddressDAO;
import app.entities.Address;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressService {
    AddressDAO addressDAO;

    public Address createOrFindAddress(String postalCode, String city, String address) {
        Address fullAddress;
        fullAddress = addressDAO.readAdressByPostalCodeAndAddress(postalCode, address);

        if (fullAddress == null) {
            fullAddress = Address.builder()
                    .postalCode(postalCode)
                    .city(city)
                    .address(address)
                    .build();
            fullAddress = addressDAO.create(fullAddress);
        }
        return fullAddress;
    }
}
