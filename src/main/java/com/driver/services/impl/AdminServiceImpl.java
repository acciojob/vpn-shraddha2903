package com.driver.services.impl;

import com.driver.Exceptions.CountryNotFound;
import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {

        Admin admin = new Admin();
        admin.setPassword(password);
        admin.setUsername(username);

        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        ServiceProvider serviceProvider = new ServiceProvider();

        Admin admin =adminRepository1.findById(adminId).get();

        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);

        admin.getServiceProviders().add(serviceProvider);

        adminRepository1.save(admin);

        return admin;

    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {

        //ind, aus, usa, chi, jpn
        countryName = countryName.toLowerCase();
        if (countryName.equals("ind") || countryName.equals("aus") ||
                countryName.equals("usa") || countryName.equals("chi")||
            countryName.equals("jpn"))
        {
        Country country = new Country();

        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());

        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();

        List<Country> countryList = serviceProvider.getCountryList();

        countryList.add(country);

        serviceProvider.setCountryList(countryList);

        serviceProviderRepository1.save(serviceProvider);
        return serviceProvider;
      }

        throw new CountryNotFound("Country not found");
    }
}
