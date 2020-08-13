package com.bolsadeideas.springboot.backend.apirest.controllers;

import com.bolsadeideas.springboot.backend.apirest.models.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.Region;
import com.bolsadeideas.springboot.backend.apirest.services.ClienteService;
import com.bolsadeideas.springboot.backend.apirest.services.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private ClienteService clienteServiceObj;

	@Autowired
	private UploadFileService uploadFileServiceObj;


	// Obtener el listado de clientes en json
	@GetMapping("/clientes")
	public List<Cliente> obtenerListadoClientes() {
		return clienteServiceObj.findAll();
	}


	//Envia el listado por Page(4 por pagina) en formato json
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> obtenerListadoClientesPageable(@PathVariable("page") Integer page) {
		return clienteServiceObj.findAllPage(PageRequest.of(page, 4));
	}



	// Obtener un cliente por el id
	@Secured({"ROLE_ADMIN","ROLE_USER"})
	@GetMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> obtenerUnCliente(@PathVariable Long id) {

		//Crea los respectivos objetos de Cliente y Response
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();


		//Busca el cliente por el ID en la BD a traves del servicio
		try {
			cliente = clienteServiceObj.findById(id);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		//Si el cliente no aparece en la BD, envia response
		if(cliente == null) {
			response.put("mensaje", "El cliente ID: " + id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		//Si el Cliente aparece, envia el objeto cliente en el response.
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}




	// Crear un cliente a partir de un body(cliente)
	@Secured("ROLE_ADMIN")
	@PostMapping("/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			System.out.println("ha habido un error");

			//Crea una lista de errores
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());

			System.out.println(errors);
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
		    //Se guarda el cliente.
			clienteNew = clienteServiceObj.save(cliente);
		}
		catch(DataAccessException e) {
			System.out.println("ha habido un error 2");
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}



	// Editar un cliente por el id y el cliente(body)
	@Secured("ROLE_ADMIN")
	@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {

		Cliente clienteActual = clienteServiceObj.findById(id);

		Cliente clienteUpdated = null;

		Map<String, Object> response = new HashMap<>();


		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		//Si el cliente que se quiere editar con el id buscado no existe en la BD
		if (clienteActual == null) {
			response.put("mensaje", "Error: no se pudo editar, el cliente ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
		    //Se pasan los datos al cliente localizado para editar
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			clienteActual.setRegion(cliente.getRegion());

			clienteUpdated = clienteServiceObj.save(clienteActual);
		}
		catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido actualizado con éxito!");
		response.put("cliente", clienteUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}




	//Eliminar cliente por id
	@Secured("ROLE_ADMIN")
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();

		//Se localiza al cliente
		Cliente cliente = clienteServiceObj.findById(id);

		//Se revisa si el cliente ya tiene una foto existente, para proceder a eliminar
		String nombreFotoAnterior = cliente.getFoto();
		uploadFileServiceObj.eliminar(nombreFotoAnterior);
		
		try {
		    clienteServiceObj.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente de la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido eliminado con éxito!");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}



	//Carga de imagenes
	@Secured({"ROLE_ADMIN","ROLE_USER"})
    @PostMapping("/clientes/upload")
    public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id)  {
	    //Definicion de variable de respuesta
        Map<String, Object> response = new HashMap<>();

        //Se localiza al cliente
        Cliente cliente = clienteServiceObj.findById(id);


        if (!archivo.isEmpty()){

        	String nombreArchivo = null;

            try {

            	//Se realiza la carga y devuelve el nombre de la foto String.
            	nombreArchivo = uploadFileServiceObj.copiar(archivo);
            }
            catch (IOException e) {
                response.put("mensaje", "Error al cargar la imagen");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //Se revisa si el cliente ya tiene una foto existente, para proceder a eliminar
			String nombreFotoAnterior = cliente.getFoto();
            uploadFileServiceObj.eliminar(nombreFotoAnterior);

            //Se gestiona el guardado de la imagen en string en la BD
            cliente.setFoto(nombreArchivo);
            clienteServiceObj.save(cliente);
            response.put("cliente", cliente);
            response.put("mensaje","Has subido correctamente la imagen " + nombreArchivo);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }



    //Visualizar foto
	@GetMapping("/uploads/img/{nombreFoto:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable("nombreFoto") String nombreFoto){
		Resource recurso = null;

		try {
			recurso = uploadFileServiceObj.cargar(nombreFoto);
		}
		catch (MalformedURLException e){
			e.printStackTrace();
		}

		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"" );

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}


	@Secured("ROLE_ADMIN")
	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones(){
		return clienteServiceObj.findAllRegiones();
	}
}
