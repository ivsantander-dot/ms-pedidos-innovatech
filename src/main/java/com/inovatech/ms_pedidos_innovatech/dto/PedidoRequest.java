package com.inovatech.ms_pedidos_innovatech.dto;

public class PedidoRequest {

    private String clienteId;
    private String producto;
    private Double precio;
    private String estado;
    private String nombreDestinatario;
    private String direccionDestino;
    private String ciudadDestino;
    private String regionDestino;
    private String telefonoContacto;

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNombreDestinatario() { return nombreDestinatario; }
    public void setNombreDestinatario(String nombreDestinatario) { this.nombreDestinatario = nombreDestinatario; }
    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }
    public String getCiudadDestino() { return ciudadDestino; }
    public void setCiudadDestino(String ciudadDestino) { this.ciudadDestino = ciudadDestino; }
    public String getRegionDestino() { return regionDestino; }
    public void setRegionDestino(String regionDestino) { this.regionDestino = regionDestino; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
}
