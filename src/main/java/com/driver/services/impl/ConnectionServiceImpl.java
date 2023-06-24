package com.driver.services.impl;

import com.driver.Exceptions.AlreadyDisconnected;
import com.driver.Exceptions.UnableToConnect;
import com.driver.Exceptions.UserAlreadyConnected;
import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        User user = userRepository2.findById(userId).get();

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();

        if(user.getConnected())
        {
            throw new UserAlreadyConnected("Already connected");
        }
        else if(user.getOriginalCountry().equals(countryName))
        {
            return  user;
        }
        else{
            if(serviceProviderList.isEmpty())
            {
                throw new UnableToConnect("Unable to connect");
            }

            boolean isProvide = false;
            ServiceProvider updatedServiceProvider=null;
            int minId = Integer.MAX_VALUE;
            Country updatedCountry = null;

            for(ServiceProvider serviceProvider  : serviceProviderList)
            {
                List<Country> countryList = serviceProvider.getCountryList();
                for(Country country  : countryList)
                {
                    if(country.getCountryName().equals(countryName))
                    {
                        isProvide = true;
                        if(serviceProvider.getId()< minId)
                        {
                            updatedServiceProvider = serviceProvider;
                            minId = serviceProvider.getId();
                            updatedCountry = country;
                        }
                    }
                }
            }

            if(!isProvide)
            {
                throw new UnableToConnect("Unable to connect");
            }
            else {

                String maskedIp = updatedCountry.getCountryName()+"."+updatedServiceProvider.getId()+"."+userId;

                user.setMaskedIp(maskedIp);

                user.setConnected(true);

                user.setOriginalCountry(updatedCountry);

//              userRepository2.save(user);

                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(updatedServiceProvider);

                updatedServiceProvider.getConnectionList().add(connection);
                user.getConnectionList().add(connection);

                user = userRepository2.save(user);

                serviceProviderRepository2.save(updatedServiceProvider);

            }

        }

        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();

        if(user.getConnected())
            throw new AlreadyDisconnected("Already disconnected");

        user.setMaskedIp(null);

        user.setConnected(false);

        user = userRepository2.save(user);

        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        return null;
    }
}
