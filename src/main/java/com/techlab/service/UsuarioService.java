package com.techlab.service;

import com.techlab.exception.RecursoNoEncontradoException;
import com.techlab.model.Usuario;
import com.techlab.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuario = buscarPorId(id);
        
        // Verificar si el email ya existe en otro usuario
        if (!usuario.getEmail().equals(usuarioActualizado.getEmail()) 
            && usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuarioActualizado.getEmail());
        }
        
        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setEmail(usuarioActualizado.getEmail());
        usuario.setDireccion(usuarioActualizado.getDireccion());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}

