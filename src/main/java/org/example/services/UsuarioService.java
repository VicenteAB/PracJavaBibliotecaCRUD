package org.example.services;

import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;
import org.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public boolean registrar(Usuario usuario){

        if(usuarioRepository.findByUsername(usuario.getUsername()).isPresent()){
            return false;
        }else{

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            String contrasenaCifrada = encoder.encode(usuario.getPassword());

            usuario.setPassword(contrasenaCifrada);

            usuarioRepository.save(usuario);
            return true;
        }

    }

    public String login(String username, String password){

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        if(usuario != null){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if(encoder.matches(password, usuario.getPassword())){

                return jwtUtil.generarToken(usuario.getUsername());
            }else{

                return null;
            }

        }else{

            return null;
        }

    }
}
