package com.bolsadeideas.springboot.backend.apirest.repositories;

import com.bolsadeideas.springboot.backend.apirest.models.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    //con jpa de se puede hacer paginacion y ordenamiento
    //crud consultas simples

    public Usuario findByUsername(String username);

    @Query("select u from Usuario u where u.username=?1")
    public Usuario findByUsername2(String username);


}
