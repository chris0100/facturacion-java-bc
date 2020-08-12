package com.bolsadeideas.springboot.backend.apirest.repositories;

import com.bolsadeideas.springboot.backend.apirest.models.Factura;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends CrudRepository<Factura, Long> {
}
