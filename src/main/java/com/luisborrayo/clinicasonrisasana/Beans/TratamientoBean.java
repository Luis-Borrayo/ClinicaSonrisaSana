//import jakarta.annotation.PostConstruct;
//import jakarta.enterprise.context.SessionScoped;
//import jakarta.inject.Named;
//import java.io.Serializable;
//import java.util.List;
//
//@Named
//@SessionScoped
//public class TratamientoBean implements Serializable {
//
//    private Tratamiento tratamiento;
//    private List<Tratamiento> lista;
//
//    private TratamientoRepository repo = new TratamientoRepository();
//
//    @PostConstruct
//    public void init() {
//        tratamiento = new Tratamiento();
//        lista = repo.findAll();
//    }
//
//    public void guardar() {
//        if (tratamiento.getId() == null) {
//            repo.save(tratamiento);
//        } else {
//            repo.update(tratamiento);
//        }
//        tratamiento = new Tratamiento();
//    }
//
//    public void editar(Tratamiento t) {
//        this.tratamiento = t;
//    }
//
//    public void eliminar(Long id) {
//        repo.delete(id);
//    }
//
//    public Tratamiento getTratamiento() {
//        return tratamiento;
//    }
//
//    public void setTratamiento(Tratamiento tratamiento) {
//        this.tratamiento = tratamiento;
//    }
//
//    public List<Tratamiento> getLista() {
//        return lista;
//    }
//}