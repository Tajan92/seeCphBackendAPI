package app.service;

import app.dao.AddressDAO;
import app.entities.Address;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressService {
    private final AddressDAO addressDAO;

    public Address createOrFindAddress(String postalCode, String city, String address) {
        String checkedPostalCode = trimOrNull(postalCode);
        String checkedCity = trimOrNull(city);
        String checkedAddress = trimOrNull(address);

        if (checkedAddress == null) {
            return null;
        }

        Address addressExisting = addressDAO.readAdressByPostalCodeAndAddress(checkedPostalCode, checkedAddress);
        if (addressExisting != null) {
            return addressExisting;
        }

        Address fullAddress = Address.builder()
                .postalCode(checkedPostalCode)
                .city(checkedCity)
                .address(checkedAddress)
                .build();

        return addressDAO.create(fullAddress);
    }

    private static String trimOrNull(String input) {
        return input == null || input.isBlank() ? null : input.trim();
    }
}
