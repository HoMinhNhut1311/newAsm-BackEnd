package com.hominhnhut.WMN_BackEnd.service.Interface;


import com.hominhnhut.WMN_BackEnd.domain.enity.MediaFile;
import org.springframework.web.multipart.MultipartFile;

public interface MediaFileService {

    MediaFile uploadFileToProfile(MultipartFile file, String profileId);



}

