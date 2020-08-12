package com.bolsadeideas.springboot.backend.apirest.services;

import com.bolsadeideas.springboot.backend.apirest.models.Factura;
import com.bolsadeideas.springboot.backend.apirest.models.Producto;
import com.bolsadeideas.springboot.backend.apirest.models.Region;
import com.bolsadeideas.springboot.backend.apirest.repositories.ClienteRepository;
import com.bolsadeideas.springboot.backend.apirest.models.Cliente;
import com.bolsadeideas.springboot.backend.apirest.repositories.FacturaRepository;
import com.bolsadeideas.springboot.backend.apirest.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteRepository clienteRepositoryObj;

	@Autowired
	private FacturaRepository facturaRepositoryObj;

	@Autowired
	private ProductoRepository productoRepositoryObj;

	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteRepositoryObj.findAll();
	}


	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> findAllPage(Pageable pageable) {
		return clienteRepositoryObj.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Cliente findById(Long id) {
		return clienteRepositoryObj.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Cliente save(Cliente cliente) {
		return clienteRepositoryObj.save(cliente);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteRepositoryObj.deleteById(id);
	}


	@Override
	@Transactional(readOnly = true)
	public List<Region> findAllRegiones() {
		return clienteRepositoryObj.findAllRegiones();
	}

	@Override
	@Transactional(readOnly = true)
	public Factura findFacturaById(Long id) {
		return facturaRepositoryObj.findById(id).get();
	}

	@Override
	@Transactional
	public Factura saveFactura(Factura factura) {
		return facturaRepositoryObj.save(factura);
	}

	@Override
	@Transactional
	public void deleteFacturaById(Long id) {
		facturaRepositoryObj.deleteById(id);
	}


	@Override
	@Transactional(readOnly = true)
	public List<Producto> findProductoByNombreContainingIgnoreCase(String nombre) {
		return productoRepositoryObj.findByNombreContainingIgnoreCase(nombre);
	}


}
