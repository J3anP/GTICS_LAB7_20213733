package org.example.lab7gticsniupay.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.example.lab7gticsniupay.entity.Users;
import org.example.lab7gticsniupay.repository.ResourcesRepository;
import org.example.lab7gticsniupay.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController("/niupay")
public class MainController {
    final UsersRepository usersRepository;
    final ResourcesRepository resourcesRepository;

    public MainController(UsersRepository usersRepository, ResourcesRepository resourcesRepository) {
        this.usersRepository = usersRepository;
        this.resourcesRepository = resourcesRepository;
    }

    //Agregar usuario contador y que este autorizado
    @PostMapping(value = "/agregar", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<HashMap<String, Object>> createUser(Users user,
                                                                @RequestParam(value="fetchId", required = false) boolean fetchId){
        HashMap<String, Object> responseJson = new HashMap<>();
        String idResourceStr = user.getAuthorizedResource();
        int idResource = Integer.parseInt(idResourceStr);
        //Si se trata de un usuario tipo contador, en la bd id=5 está para el recurso de servidor de contabilidad
        if(idResource== 5){
            if(user.getType()!="Contador"){
                responseJson.put("estado", "no está autorizado");
            }else{
                responseJson.put("estado", "está autorizado");
            }
        }
        usersRepository.save(user);
        if (fetchId) {
            responseJson.put("userId", user.getUserId());
        }
        responseJson.put("estado", "creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
    }

    //Listado de usuarios que puedan autenticarse y autorizarse
    @GetMapping(value="/{type}")
    public ResponseEntity<HashMap<String,Object>> listarPlayersRegion(@PathVariable("type") String type){
        HashMap<String, Object> responseJson = new HashMap<>();

        try{
            List<Users> listaUsuariosPorTipo = usersRepository.findByType(type);

            if (!listaUsuariosPorTipo.isEmpty()){
                if(type.equals("Contador")){
                    responseJson.put("result","success");
                    responseJson.put("usuarios", usersRepository.listaUsuariosAutorizados(type,5));
                }
                if(type.equals("Cliente")){
                    responseJson.put("result","success");
                    responseJson.put("usuarios", usersRepository.listaUsuariosAutorizados(type,6));
                }
                if(type.equals("Analista de promociones")){
                    responseJson.put("result","success");
                    responseJson.put("usuarios", usersRepository.listaUsuariosAutorizados(type,7));
                }
                if(type.equals("Analista logico")){
                    responseJson.put("result","success");
                    responseJson.put("usuarios", usersRepository.listaUsuariosAutorizados(type,8));
                }
                return ResponseEntity.ok().body(responseJson);
            } else {
                responseJson.put("result","failure");
                responseJson.put("msg","usuarios no encontrados");
                return ResponseEntity.badRequest().body(responseJson);
            }
        }catch (NumberFormatException e) {
            responseJson.put("result","failure");
            responseJson.put("msg","Usuarios  no encontrados");
            return ResponseEntity.badRequest().body(responseJson);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String,String>> gestionExcepcion(HttpServletRequest request){

        HashMap<String, String> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")){
            responseMap.put("estado","error");
            responseMap.put("msg","Debe enviar un player");
        }
        return ResponseEntity.badRequest().body(responseMap);

    }

}
