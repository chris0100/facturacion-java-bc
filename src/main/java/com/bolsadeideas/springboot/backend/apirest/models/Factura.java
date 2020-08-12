package com.bolsadeideas.springboot.backend.apirest.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "facturas")
public class Factura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    private String observacion;


    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value={"listaFacturas","hibernateLazyInitializer", "handler"}, allowSetters = true )
    private Cliente cliente;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "factura_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<ItemFactura> listaItemFactura;


    public Factura() {
        this.listaItemFactura = new ArrayList<>();
    }

    //antes de guardar
    @PrePersist
    public void prePersist(){
        this.createAt = new Date();
    }


    //GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


    public List<ItemFactura> getListaItemFactura() {
        return listaItemFactura;
    }

    public void setListaItemFactura(List<ItemFactura> listaItemFactura) {
        this.listaItemFactura = listaItemFactura;
    }

    public Double getTotal(){
        Double total = 0.00;
        for (ItemFactura item : listaItemFactura){
            total += item.getImporte();
        }
        return total;
    }

    private static final long serialVersionUID = 1L;

}
