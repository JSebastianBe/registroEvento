package jsbm.registroEvento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EventoController {
    @Autowired
    private RegistroEventoApplication re;
    @PostMapping("/enviarEvento")
    public  ResponseEntity<Map<String, String>> enviarEvento(@RequestBody Evento request){
        Map<String, String> response = new HashMap<>();
        try{
            Respuesta res = re.guardarEvento(request);
            response.put("error", res.isError() ? "true": "false");
            response.put("msg",res.getMsg());
            if(!res.isError()){
                return ResponseEntity.ok(response);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        }catch (Exception e) {
            response.put("error", "true");
            response.put("msg", "No se puede enviar la petición: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
        }
    }
}


