package com.chilecompra.springboot.backend.apirest.controllers;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.chilecompra.springboot.backend.apirest.models.entity.Cliente;
import com.chilecompra.springboot.backend.apirest.models.services.IClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@CrossOrigin(origins = { "http://localhost:4200" }) //se especifica el dominio, en este caso para la transferencia y envío de datos al puerto de angular, es un arreglo
@RestController //mapeo siempre como restcontroller
@RequestMapping("/api") //la ruta raíz
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService; //inyectamos el servicio

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll(); //listamos los clientes
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			cliente = clienteService.findById(id);
		} catch(DataAccessException e) { //errores por lados del servidor
			response.put("mensaje", "Error al realizar la consulta en la base de datos!"); //colocamos el mensaje de error
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())); //error mas especifico
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);//error en la bdd
		}
		
		clienteService.findById(id);
		
		if(cliente==null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!"))); //colocamos el mensaje de error
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); //el codigo de la respuesta
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK); //cuando hay clientes
	}
	
	/*@PostMapping("/clientes")
	//@ResponseStatus(HttpStatus.CREATED) //codigo 201 de contenido creado
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) { //requestbody ya que viene en formato json
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors() //un listado con errores
					.stream() //flujo de items, por cada fieldError
					.map(err -> "El campo '" + err.getField() +"' "+ err.getDefaultMessage()) //denota el error en el campo
					.collect(Collectors.toList()); //lo volvemos a pasar string a lista
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew = clienteService.save(clienteNew);
			
		} catch(DataAccessException e) { //errores por lados del servidor
			response.put("mensaje", "Error al realizar el insert en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())); 
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("", "El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); //cuando hay clientes
	}*/
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() +"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew = clienteService.save(cliente);
		} catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	/*@PutMapping("/clientes/{id}") //para actualizar
	//@ResponseStatus(HttpStatus.CREATED) 
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) { //cliente en el cuerpo del request
		
		Cliente clienteActual = clienteService.findById(id); //obtenemos primero el cliente por su id
		
		Cliente clienteUpdated = null;
		
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {

			List<String> errors = result.getFieldErrors() //un listado con errores
					.stream() //flujo de items, por cada fieldError
					.map(err -> "El campo '" + err.getField() +"' "+ err.getDefaultMessage()) //denota el error en el campo
					.collect(Collectors.toList()); //lo volvemos a pasar string a lista
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if(clienteActual==null) {
			response.put("mensaje", "Error: no se pudo editar, el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!"))); //colocamos el mensaje de error
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND); //el codigo de la respuesta
		}
		
		try {
			clienteActual.setNombre(cliente.getNombre()); //obtenemos sus datos
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			
			clienteUpdated = clienteService.save(clienteActual);
			
		} catch(DataAccessException e) { //errores por lados del servidor
			response.put("mensaje", "Error al actualizar cliente en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())); 
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("", "El cliente ha sido actualizado con éxito!");
		response.put("cliente", clienteUpdated);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); //cuando hay clientes
		
		
	}*/
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {

		Cliente clienteActual = clienteService.findById(id);

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
		
		if (clienteActual == null) {
			response.put("mensaje", "Error: no se pudo editar, el cliente ID: "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());

			clienteUpdated = clienteService.save(clienteActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido actualizado con éxito!");
		response.put("cliente", clienteUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.NO_CONTENT) //no_content cuando no retornamos nada, 204 status
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
		clienteService.delete(id);
	}catch(DataAccessException e) { //errores por lados del servidor
		response.put("mensaje", "Error al eliminar cliente en la base de datos!");
		response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage())); 
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
		response.put("mensaje", "El cliente eliminado con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK); 
	}
}
