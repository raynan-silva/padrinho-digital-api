package com.dnnr.padrinho_digital_api.repositories.photo;

import com.dnnr.padrinho_digital_api.entities.photo.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
