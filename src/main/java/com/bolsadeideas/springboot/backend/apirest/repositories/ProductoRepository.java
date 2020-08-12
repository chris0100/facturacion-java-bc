package com.bolsadeideas.springboot.backend.apirest.repositories;

import com.bolsadeideas.springboot.backend.apirest.models.Producto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends CrudRepository<Producto,Long> {

    //@Query("select p from Producto p where p.nombre like %?1%")
    //public List<Producto> findByNombre(String nombre);

    //Funciona como el query de arriba, solo que ignora mayusculas o minusculas
    public List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
