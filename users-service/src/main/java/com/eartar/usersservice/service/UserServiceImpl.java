package com.eartar.usersservice.service;


import com.eartar.usersservice.data.AlbumsServiceClient;
import com.eartar.usersservice.data.UserEntity;
import com.eartar.usersservice.data.UserRepository;
import com.eartar.usersservice.model.AlbumResponseModel;
import com.eartar.usersservice.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    //RestTemplate restTemplate;
    AlbumsServiceClient albumsServiceClient;
    Environment env;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AlbumsServiceClient albumsServiceClient, Environment env) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.albumsServiceClient = albumsServiceClient;
        this.env = env;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
        //userEntity.setEncryptedPassword("AddSpringSecurity, dont forget removing this :)");

        userRepository.save(userEntity);

        UserDto retVal = modelMapper.map(userEntity, UserDto.class);
        return retVal;
    }

    @Override
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(uname);
        if (userEntity == null) throw new UsernameNotFoundException(uname);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserDetailsByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException(userId);

        ///Rest template version
        /*
        //for testing, rest template can use this to load balance
        String albumsUrl = String.format(env.getProperty("albums.url"), userId);

        //do a get request on albumsUrl, without headers, with the returning array converted into a list of AlbumResponseModel
        ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(
                albumsUrl,
                HttpMethod.GET,
                null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {});
        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
        */

        ///Feign client version
        List<AlbumResponseModel> albumsList = albumsServiceClient.getAlbums(userId);

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        userDto.setAlbums(albumsList);

        return userDto;
    }
}
