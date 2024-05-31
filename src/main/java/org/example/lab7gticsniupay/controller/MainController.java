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

@RestController()
@RequestMapping("/niupay")
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

        if(user.isActive()){
            if(idResource==5){
                if(user.getType().equalsIgnoreCase("contador")){
                    responseJson.put("estado", "está autorizado");
                }else{
                    responseJson.put("estado", "no está autorizado");
                }
            }
        }else{
            responseJson.put("estado", "no está autorizado");
        }
        usersRepository.save(user);
        if (fetchId) {
            responseJson.put("userId", user.getUserId());
        }
        responseJson.put("estado", "creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
    }

    //Listado de usuarios que puedan autenticarse y autorizarse
    @GetMapping(value="/{resourceId}")
    public ResponseEntity<HashMap<String,Object>> listarPlayersRegion(@PathVariable("resourceId") String rscIdStr){
        HashMap<String, Object> responseJson = new HashMap<>();
        int rscId = Integer.parseInt(rscIdStr);

        try{
            List<Users> listaUsuariosPorRsc = usersRepository.listUsersRsc(rscId);

            if (!listaUsuariosPorRsc.isEmpty()){
                String typeUser = null;
                if(rscId==5){
                    typeUser = "Contador";
                }
                if(rscId==6){
                    typeUser = "Cliente";
                }
                if(rscId==7){
                    typeUser = "Analista de promociones";
                }
                if(rscId==8){
                    typeUser = "Analista logico";
                }
                List<Users> listaUsuariosAutorizadosPorRsc = usersRepository.listaUsuariosAutorizados(typeUser,rscId);
                responseJson.put("result","success");
                responseJson.put("users", listaUsuariosPorRsc);
                return ResponseEntity.ok().body(responseJson);
            } else {
                responseJson.put("result","failure");
                responseJson.put("msg","usuarios no encontrados o no hay usuarios");
                return ResponseEntity.badRequest().body(responseJson);
            }
        }catch (NumberFormatException e) {
            responseJson.put("result","failure");
            responseJson.put("msg","Usuarios  no encontrados o no existe el idResource");
            return ResponseEntity.badRequest().body(responseJson);
        }


    }

    @DeleteMapping(value = "/delete/{userId}", consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<HashMap<String,Object>> deletePlayer(@PathVariable("userId") String idStr){

        HashMap<String, Object> responseMap = new HashMap<>();
        try{
            int id = Integer.parseInt(idStr);
            Optional<Users> optById = usersRepository.findById(id);
            if(optById.isPresent()){
                usersRepository.deleteById(id);
                responseMap.put("estado","borrado exitoso");
            }else{
                responseMap.put("estado","error");
                responseMap.put("msg","el ID enviado no existe");
            }
            return ResponseEntity.ok(responseMap);
        }catch (NumberFormatException e){
            responseMap.put("estado","error");
            responseMap.put("msg","El ID debe ser un número");
            return ResponseEntity.badRequest().body(responseMap);
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
