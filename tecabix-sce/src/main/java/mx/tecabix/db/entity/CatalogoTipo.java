package mx.tecabix.db.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity()
@Table(name = "catalogo_tipo")
public class CatalogoTipo implements Serializable{

	private static final long serialVersionUID = -4174323806062618433L;
	@Id
    @Column(name = "id_catalogo_tipo", unique = true, nullable = false)
	@SequenceGenerator(name = "catalogo_tipo_id_catalogo_tipo_gen", sequenceName = "tecabix_spv.catalogo_tipo_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalogo_tipo_id_catalogo_tipo_gen")
    private Integer id;
	@Column(name = "nombre")
	private String nombre;
	@Column(name = "descripcion")
	private String descripcion;
	@OneToMany(fetch = FetchType.LAZY, mappedBy="catalogoTipo", cascade=CascadeType.REMOVE)
	private List<Catalogo> catalogos;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public List<Catalogo> getCatalogos() {
		return catalogos;
	}
	public void setCatalogos(List<Catalogo> catalogos) {
		this.catalogos = catalogos;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CatalogoTipo other = (CatalogoTipo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
