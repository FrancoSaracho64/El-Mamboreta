package com.mamboreta.backend.repository;

import com.mamboreta.backend.entity.RedSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedSocialRepository extends JpaRepository<RedSocial, Long> {
    
    List<RedSocial> findByActivoTrue();
    
    List<RedSocial> findByRed(String red);
    
    List<RedSocial> findByUsuarioContaining(String usuario);
    
    List<RedSocial> findByRedAndUsuarioContaining(String red, String usuario);
}
