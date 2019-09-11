package scraping.secop.SecopVO;

import java.util.List;

public class DatosTablaVO {

    private String enlace;
    private String descripcion;
    private String valorEstimado;
    private List<String> listaCodigosUBSPC;
    private String presentacionOferta;
    private String nombreEntidad;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

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

    public String getPresentacionOferta() {
        return presentacionOferta;
    }

    public void setPresentacionOferta(String presentacionOferta) {
        this.presentacionOferta = presentacionOferta;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public void setNombreEntidad(String nombreEntidad) {
        this.nombreEntidad = nombreEntidad;
    }
}
