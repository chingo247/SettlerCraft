package com.settlercraft.core.model.entity.structure;

import com.settlercraft.core.model.entity.SettlerCraftEntity;
import com.settlercraft.core.model.plan.requirement.material.MaterialResource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureProgress extends SettlerCraftEntity implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private int layer;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Structure structure;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MaterialResource> resources;
    
    /**
     * JPA Constructor.
     */
    protected StructureProgress(){}

    /**
     * Constructor.
     * @param structure The structure
     */
    public StructureProgress(Structure structure) {
        this.resources = new ArrayList<>();
        this.structure = structure;
        this.layer = 0;
        this.resources = structure.getPlan().getRequirement().getMaterialRequirement().getLayer(layer).getResources(this);
    }

    public Structure getStructure() {
        return structure;
    }
    
    public void addAll(List<MaterialResource> resources){
        this.resources.addAll(resources);
    }
    
    public void setResources(List<MaterialResource> resources) {
        this.resources = resources;
    }
    
    public int getMaxHeight() {
        return structure.getPlan().getStructureSchematic().getHeight();
    }

    public void setLayer(int layer) {
        this.layer = layer;
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
    

    
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(MaterialResource r : resources) {
            sb.append(r).append("\n");
        }
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
    
}
