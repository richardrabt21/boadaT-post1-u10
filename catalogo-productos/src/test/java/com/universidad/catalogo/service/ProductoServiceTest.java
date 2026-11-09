package com.universidad.catalogo.service;

import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repo;

    @InjectMocks
    private ProductoService service;

    @Test
    void crear_conDatosValidos_guardaYRetorna() {
        Producto p = new Producto();
        p.setNombre("Teclado mecanico");
        p.setPrecio(120.0);
        p.setCategoria("ELECTRONICA");

        when(repo.save(any(Producto.class))).thenReturn(p);

        Producto creado = service.crear(p);
        assertThat(creado.getNombre()).isEqualTo("Teclado mecanico");
        verify(repo).save(p);
    }

    @Test
    void crear_conPrecioNegativo_lanzaIllegalArgumentException() {
        Producto p = new Producto();
        p.setNombre("Mouse");
        p.setPrecio(-10.0);

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void crear_conPrecioCero_lanzaIllegalArgumentException() {
        Producto p = new Producto();
        p.setNombre("Mouse");
        p.setPrecio(0.0);

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void crear_conNombreNulo_lanzaIllegalArgumentException() {
        Producto p = new Producto();
        p.setNombre(null);
        p.setPrecio(50.0);

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void crear_conNombreVacio_lanzaIllegalArgumentException() {
        Producto p = new Producto();
        p.setNombre("   ");
        p.setPrecio(50.0);

        assertThrows(IllegalArgumentException.class, () -> service.crear(p));
        verify(repo, never()).save(any());
    }

    @Test
    void buscarPorId_existe_retornaProducto() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Monitor");
        p.setPrecio(300.0);

        when(repo.findById(1L)).thenReturn(Optional.of(p));

        Producto encontrado = service.buscarPorId(1L);
        assertThat(encontrado.getNombre()).isEqualTo("Monitor");
    }

    @Test
    void buscarPorId_noExiste_lanzaEntityNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listarPorCategoria_retornaLista() {
        Producto p1 = new Producto(1L, "Laptop", 1500.0, "ELECTRONICA");
        when(repo.findByCategoria("ELECTRONICA")).thenReturn(List.of(p1));

        List<Producto> lista = service.listarPorCategoria("ELECTRONICA");
        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNombre()).isEqualTo("Laptop");
    }

    @Test
    void actualizarPrecio_conValorValido_actualizaYGuarda() {
        Producto p = new Producto();
        p.setId(1L);
        p.setPrecio(100.0);

        when(repo.findById(1L)).thenReturn(Optional.of(p));
        when(repo.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto actualizado = service.actualizarPrecio(1L, 150.0);
        assertThat(actualizado.getPrecio()).isEqualTo(150.0);
        verify(repo).save(p);
    }

    @Test
    void actualizarPrecio_conPrecioInvalido_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarPrecio(1L, -20.0));
        verify(repo, never()).save(any());
    }
}
