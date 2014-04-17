package com.settlercraft.core.model.entity.structure;

import com.settlercraft.core.model.plan.requirement.material.MaterialResource;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import org.hibernate.annotations.CollectionOfElements;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureProgress implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private int layer;
    
    private String plan;
    
    @OneToOne(cascade = CascadeType.ALL)
    private Structure structure;
    
    @CollectionOfElements(fetch = FetchType.EAGER)
    private List<MaterialResource> resources;
    
    @Version
    private Timestamp lastModified;

    /**
     * JPA Constructor.
     */
    protected StructureProgress(){}

    /**
     * Constructor.
     * @param structure The structure
     */
    public StructureProgress(Structure structure) {
        this.structure = structure;
        this.plan = structure.getPlan().getConfig().getName();
        this.resources = structure.getPlan().getRequirement().getMaterialRequirement().getLayer(0).getResources();
        this.layer = 0;
    }
    
    public int getMaxHeight() {
        return structure.getPlan().getSchematic().getHeight();
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setResources(List<MaterialResource> resources) {
        this.resources = resources;
    }

    public int getLayer() {
        return layer;
    }
    
    public List<MaterialResource> getResources() {
        return resources;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }
    
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resources: \n");
        for(MaterialResource r : resources) {
            sb.append(r).append("\n");
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
    
}
