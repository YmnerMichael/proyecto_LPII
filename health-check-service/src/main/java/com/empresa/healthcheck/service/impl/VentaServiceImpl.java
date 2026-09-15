package com.empresa.healthcheck.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.empresa.healthcheck.dto.DetalleVentaRequestDTO;
import com.empresa.healthcheck.dto.DetalleVentaResponseDTO;
import com.empresa.healthcheck.dto.VentaRequestDTO;
import com.empresa.healthcheck.dto.VentaResponseDTO;
import com.empresa.healthcheck.entity.Cliente;
import com.empresa.healthcheck.entity.DetalleVenta;
import com.empresa.healthcheck.entity.Producto;
import com.empresa.healthcheck.entity.Venta;
import com.empresa.healthcheck.enums.EstadoVenta;
import com.empresa.healthcheck.exception.RecursosNoEncontradoException;
import com.empresa.healthcheck.exception.ReglaNegocioException;
import com.empresa.healthcheck.repository.ClienteRepository;
import com.empresa.healthcheck.repository.ProductoRepository;
import com.empresa.healthcheck.repository.VentaRepository;
import com.empresa.healthcheck.service.service.VentaService;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class VentaServiceImpl implements VentaService {

    private static final Set<String> CAMPOS_PERMITIDOS = Set.of("id", "fecha", "total", "estado");

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public VentaServiceImpl(
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository) {

        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public VentaResponseDTO registrar(VentaRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursosNoEncontradoException("Cliente no encontrado con id: " + request.getClienteId()));

        if (!Boolean.TRUE.equals(cliente.getEstado())) {
            throw new ReglaNegocioException("No se puede registrar una venta para un cliente inactivo");
        }
        Venta venta = new Venta();

        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVenta.REGISTRADA);

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVentaRequestDTO item : request.getDetalles()) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow(() ->
                    new RecursosNoEncontradoException("Producto no encontrado con id: " + item.getProductoId()));

            if (!Boolean.TRUE.equals(producto.getEstado())) {
                throw new ReglaNegocioException("El producto " + producto.getNombre() + " se encuentra inactivo");
            }

            if (producto.getStock() < item.getCantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para " + producto.getNombre() + ". Disponible: " + producto.getStock()
                        + ", solicitado: " + item.getCantidad());
            }

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));

            DetalleVenta detalle = new DetalleVenta();

            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            venta.agregarDetalle(detalle);

            total = total.add(subtotal);

            producto.setStock(producto.getStock() - item.getCantidad());
        }

        venta.setTotal(total);

        Venta guardada = ventaRepository.save(venta);

        return convertirResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO buscar(Long id) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradoException("Venta no encontrada con id: " + id));
        return convertirResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listar() {
        return ventaRepository.findAll().stream().map(this::convertirResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscar(Long clienteId, EstadoVenta estado, LocalDateTime desde, LocalDateTime hasta, String ordenarPor, String direccion) {
        long inicio = System.currentTimeMillis();
        log.info("Inicio búsqueda de ventas - clienteId={}, estado={}, desde={}, hasta={}, ordenarPor={}, direccion={}",
                clienteId, estado, desde, hasta, ordenarPor, direccion);

        // 1. Validación de rango de fechas
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        // 2. Validación de lista blanca para ordenamiento
        String campoOrden = (ordenarPor != null && !ordenarPor.isBlank()) ? ordenarPor : "fecha";
        if (!CAMPOS_PERMITIDOS.contains(campoOrden)) {
            throw new ReglaNegocioException("El campo de ordenamiento '" + campoOrden + "' no está permitido. Campos válidos: " + CAMPOS_PERMITIDOS);
        }

        // 3. Dirección de ordenamiento
        Sort.Direction dir = "ASC".equalsIgnoreCase(direccion)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(dir, campoOrden);

        // 4. Ejecutar consulta
        List<Venta> ventas = ventaRepository.buscar(clienteId, estado, desde, hasta, sort);
        List<VentaResponseDTO> respuesta = ventas.stream().map(this::convertirResponse).toList();

        long duracion = System.currentTimeMillis() - inicio;
        log.info("Fin búsqueda de ventas - {} registros encontrados. Duración: {} ms", respuesta.size(), duracion);

        return respuesta;
    }

    private VentaResponseDTO convertirResponse(Venta venta) {
        List<DetalleVentaResponseDTO> detalles =
                venta.getDetalles()
                        .stream()
                        .map(detalle ->
                                new DetalleVentaResponseDTO(
                                        detalle.getProducto().getId(),
                                        detalle.getProducto().getNombre(),
                                        detalle.getCantidad(),
                                        detalle.getPrecio(),
                                        detalle.getSubtotal()
                                )
                        ).toList();

        String clienteNombre = venta.getCliente().getNombres() + " " + venta.getCliente().getApellidos();

        return new VentaResponseDTO(
                venta.getId(),
                venta.getFecha(),
                venta.getCliente().getId(),
                clienteNombre,
                venta.getEstado().name(),
                venta.getTotal(),
                detalles
        );
    }
}