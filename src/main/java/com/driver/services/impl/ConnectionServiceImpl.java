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

        countryName = countryName.toUpperCase();

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();

        if(user.getConnected()==true)
        {
            throw new UserAlreadyConnected("Already connected");
        }
        if(user.getOriginalCountry().equals(countryName))
        {
            return  user;
        }

            if(serviceProviderList.isEmpty())
            {
                throw new UnableToConnect("UnableToConnect");
            }

            Boolean isProvide = false;
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
                throw new UnableToConnect("UnableToConnect");
            }

                String maskedIp = updatedCountry.getCode()+"."+updatedServiceProvider.getId()+"."+userId;

                user.setMaskedIp(maskedIp);

                user.setConnected(true);

                user.setOriginalCountry(updatedCountry);

//              userRepository2.save(user);

                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(updatedServiceProvider);

                List<Connection> providerConnectionList = updatedServiceProvider.getConnectionList();
                providerConnectionList.add(connection);
                updatedServiceProvider.setConnectionList(providerConnectionList);


                List<Connection> userConnectionList = user.getConnectionList();
                userConnectionList.add(connection);
                user.setConnectionList(userConnectionList);

                user = userRepository2.save(user);

                serviceProviderRepository2.save(updatedServiceProvider);


        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();

        if(user.getConnected()==null || user.getConnected()==true)
        {
        user.setMaskedIp(null);

        user.setConnected(false);

        userRepository2.save(user);

        return user;
        }

        throw new AlreadyDisconnected("Already disconnected");
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        User receiver = userRepository2.findById(receiverId).get();
        CountryName receiverCountryName =null;

        if(receiver.getConnected()) {
            String maskedCode = receiver.getMaskedIp().substring(0, 3);
            if (maskedCode.equals("001")) {
                receiverCountryName = CountryName.IND;
            } else if (maskedCode.equals("002")) {
                receiverCountryName = CountryName.USA;
            } else if (maskedCode.equals("003")) {
                receiverCountryName = CountryName.AUS;
            } else if (maskedCode.equals("004")) {
                receiverCountryName = CountryName.CHI;
            } else if (maskedCode.equals("005")) {
                receiverCountryName = CountryName.JPN;
            }
        }
            else {
                receiverCountryName = receiver.getOriginalCountry().getCountryName();
            }
            User user = null;
            try {
                user = connect(senderId,receiverCountryName.toString());
            }catch (Exception e)
            {
                throw new Exception("Cannot establish communication");
            }
            return user;
        }
}
