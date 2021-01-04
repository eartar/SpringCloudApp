package com.eartar.albumsservice.service;


import com.eartar.albumsservice.data.AlbumEntity;
import java.util.List;

public interface AlbumsService {
    List<AlbumEntity> getAlbums(String userId);
}
