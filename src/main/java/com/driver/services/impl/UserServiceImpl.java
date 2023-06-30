package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Country country = new Country();
        countryName = countryName.toUpperCase();

        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());

        country.setUser(user);
        user.setOriginalCountry(country);

        user.setConnected(false);
        user.setMaskedIp(null);

       // userRepository3.save(user);

        user = userRepository3.save(user);
//        String originalIP = CountryName.valueOf(countryName).toCode()+"."+user.getId();
       String originalIP = user.getOriginalCountry().getCode()+"."+user.getId();
        user.setOriginalIp(originalIP);

        user = userRepository3.save(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId)
    {
        User user = userRepository3.findById(userId).get();

        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();

        List<User> userList = serviceProvider.getUsers();
        userList.add(user);

        serviceProviderList.add(serviceProvider);

        user.setServiceProviderList(serviceProviderList);

        serviceProvider.setUsers(userList);
        user = userRepository3.save(user);

        return user;
    }
}
