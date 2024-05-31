package org.example.lab7gticsniupay.repository;

import org.example.lab7gticsniupay.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    @Query(nativeQuery=true, value="select * from users where type=?1")
    List<Users> findByType(String type);

    @Query(nativeQuery=true, value="select * from users where type=?1 and authorizedResource = ?2")
    List<Users> listaUsuariosAutorizados(String type, int authorizedResource);
}
