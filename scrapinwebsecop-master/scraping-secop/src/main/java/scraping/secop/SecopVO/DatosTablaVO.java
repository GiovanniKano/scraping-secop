package scraping.secop.SecopVO;

import java.util.ArrayList;
import java.util.List;

public class DatosTablaVO {

    private String enlace;
    private String descripcion;
    private String valorEstimado;
    private List<String> listaCodigosUBSPC = new ArrayList<>();
    private String fechaPresentacion;
    private String nombreEntidad;

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripción) {
        this.descripcion = descripción;
    }

    public String getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(String valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public List<String> getListaCodigosUBSPC() {
        return listaCodigosUBSPC;
    }

    public void setListaCodigosUBSPC(List<String> listaCodigosUBSPC) {
        this.listaCodigosUBSPC = listaCodigosUBSPC;
    }

    public String getFechaPresentacion() {
        return fechaPresentacion;
    }

    public void setFechaPresentacion(String fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public void setNombreEntidad(String nombreEntidad) {
        this.nombreEntidad = nombreEntidad;
    }
}
