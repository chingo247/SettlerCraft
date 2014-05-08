package com.sc.api.structure.model.structure.progress;



import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.schematic.SchematicBlockReport;
import com.sc.api.structure.model.structure.schematic.SchematicMaterialLayer;
import com.sc.api.structure.model.structure.schematic.SchematicMaterialResource;
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
public class StructureProgress implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private int currentLayer = 0;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Structure structure;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<StructureProgressLayer> layerRequirements;
    
    /**
     * JPA Constructor.
     */
    protected StructureProgress(){}

    /**
     * Constructor.
     * @param blockReport
     * @param structure The structure
     */
    public StructureProgress(Structure structure, SchematicBlockReport blockReport) {
        this.layerRequirements = new ArrayList<>(blockReport.getHeight());
        this.structure = structure;
        this.setProgressLayers(blockReport.getLayerRequirements());
    }
    
    private  void setProgressLayers(List<SchematicMaterialLayer> layerRqs) {
        for(SchematicMaterialLayer lr : layerRqs) {
            StructureProgressLayer progressLayer = new StructureProgressLayer(this, lr.getLayer());
            for(SchematicMaterialResource materialResource : lr.getResources()) {
                progressLayer.addResource(new StructureProgressMaterialResource(
                        progressLayer, 
                        materialResource.getMaterial(), 
                        materialResource.getData(), 
                        materialResource.getAmount()));
            }
            layerRequirements.add(progressLayer);
        }
    }

    
    public boolean setNext() {
        if(layerRequirements.get(currentLayer).getResources().isEmpty() && currentLayer < layerRequirements.size()) {
            currentLayer++;
            return true;
        }
        return false;
    }
    
    public StructureProgressLayer getCurrentLayerRequirement() {
        return layerRequirements.get(currentLayer);
    }
    
    public StructureProgressLayer getLayerRequirement(int index) {
        return layerRequirements.get(index);
    }
    
    public int size() {
        return layerRequirements.size();
    }

    public Structure getStructure() {
        return structure;
    }
    
    public Long getId() {
        return id;
    }

}
