package com.hominhnhut.WMN_BackEnd.config;

import com.cloudinary.Cloudinary;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class beginSpring {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.getConfiguration().setAmbiguityIgnored(true);
        return mapper;
    }

    @Bean
    public Cloudinary getCloudinary(){
        Map<String,Object> config = new HashMap<>();
        config.put("cloud_name", "dx1qqpmnt");
        config.put("api_key", "663518625875262");
        config.put("api_secret", "6m1uyXoUwLdfVzPQwealg893cf8");
        config.put("secure", true);
        return new Cloudinary(config);
    }




}
