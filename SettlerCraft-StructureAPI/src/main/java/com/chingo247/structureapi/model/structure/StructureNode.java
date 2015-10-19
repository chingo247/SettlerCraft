/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.persistence.neo4j.NodeHelper;
import com.chingo247.structureapi.model.RelTypes;
import com.chingo247.structureapi.model.owner.OwnerDomain;
import com.chingo247.structureapi.model.world.StructureWorld;
import com.chingo247.structureapi.util.RegionUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * Structure Node is a representation of a structure as node. All operations on this object will require an active transaction
 *
 * @author Chingo
 */
public class StructureNode extends AStructure {

    public static final String LABEL = "STRUCTURE";
    public static final String ID_PROPERTY = "structureId";
    public static final String NAME_PROPERTY = "name";
    public static final String DIRECTION_PROPERTY = "direction";
    public static final String CONSTRUCTION_STATUS_PROPERTY = "constructionStatus";
    public static final String MIN_X_PROPERTY = "minX", MIN_Y_PROPERTY = "minY", MIN_Z_PROPERTY = "minZ", MAX_X_PROPERTY = "maxX", MAX_Y_PROPERTY = "maxY", MAX_Z_PROPERTY = "maxZ";
    public static final String POS_X_PROPERTY = "x", POS_Y_PROPERTY = "y", POS_Z_PROPERTY = "z";
    public static final String CENTER_X_PROPERTY = "centerX", CENTER_Y_PROPERTY = "centerY", CENTER_Z_PROPERTY = "centerZ";
    public static final String CREATED_AT_PROPERTY = "createdAt", DELETED_AT_PROPERTY = "deletedAt", COMPLETED_AT_PROPERTY = "completedAt";
    public static final String PRICE_PROPERTY = "price";
    public static final String SIZE_PROPERTY = "size";
    public static final String AUTO_REMOVED_PROPERTY = "autoremoved";
    public static final String CHECKED_HOLOGRAM_PROPERTY = "checkedHologram";
    
    public static Label label() {
        return DynamicLabel.label(LABEL);
    }

    // RelationShips
    private final Node underlyingNode;

    private OwnerDomain ownerDomain;
    
    // Second level cache
    private Long id;

    /**
     * The node that provides all the information of the structure
     *
     * @param underlyingNode The node
     */
    public StructureNode(Node underlyingNode) {
        super(underlyingNode);
        this.underlyingNode = underlyingNode;
        this.ownerDomain = new OwnerDomain(underlyingNode);
    }

    public OwnerDomain getOwnerDomain() {
        return ownerDomain;
    }
    
    public Structure getUnmodifiable() {
        return new Structure(underlyingNode);
    }
    
    /**
     * Sets the center x
     * @param x The center x to set
     */
    public void setCenterX(int x) {
        underlyingNode.setProperty(CENTER_X_PROPERTY, x);
    }
    
    /**
     * Serts the center y
     * @param y The center y to set
     */
    public void setCenterY(int y) {
        underlyingNode.setProperty(CENTER_Y_PROPERTY, y);
    }
    
    /**
     * Sets the center z
     * @param z The center z to set
     */
    public void setCenterZ(int z) {
        underlyingNode.setProperty(CENTER_Z_PROPERTY, z);
    }
    
    /**
     * Gets the center x
     * @return The center x
     */
    public int getCenterX() {
        return NodeHelper.getInt(underlyingNode, CENTER_X_PROPERTY, 0);
    }
    
    /**
     * Gets the center y
     * @return The center y
     */
    public int getCenterY() {
        return NodeHelper.getInt(underlyingNode, CENTER_Y_PROPERTY, 0);
    }
    
    /**
     * Gets the center z
     * @return The center z
     */
    public int getCenterZ() {
        return NodeHelper.getInt(underlyingNode, CENTER_Z_PROPERTY, 0);
    }

    @Override
    public final Long getId() {
        if (id != null) {
            return id;
        }
        Object o = underlyingNode.getProperty(ID_PROPERTY);

        id = (Long) o;
        return id;
    }

    public GraphDatabaseService getGraph() {
        return getNode().getGraphDatabase();
    }

    public void setCompletedAt(Long date) {
        if (date != null) {
            underlyingNode.setProperty(COMPLETED_AT_PROPERTY, date);
        } else if (underlyingNode.hasProperty(COMPLETED_AT_PROPERTY)) {
            underlyingNode.removeProperty(COMPLETED_AT_PROPERTY);
        }
    }

    @Override
    public Vector getOrigin() {
        return new Vector(getX(), getY(), getZ());
    }

    public void setAutoremoved(boolean removed) {
        underlyingNode.setProperty(AUTO_REMOVED_PROPERTY, removed);
    }

    public boolean isAutoremoved() {
        if (underlyingNode.hasProperty(AUTO_REMOVED_PROPERTY)) {
            return (Boolean) underlyingNode.getProperty(AUTO_REMOVED_PROPERTY);
        }
        return false;
    }

    public void setDeletedAt(Long date) {
        if (date != null) {
            underlyingNode.setProperty(DELETED_AT_PROPERTY, date);
        } else if (underlyingNode.hasProperty(DELETED_AT_PROPERTY)) {
            underlyingNode.removeProperty(DELETED_AT_PROPERTY);
        }
    }

    public void setCreatedAt(Long date) {
        if (date != null) {
            underlyingNode.setProperty(CREATED_AT_PROPERTY, date);
        } else if (underlyingNode.hasProperty(CREATED_AT_PROPERTY)) {
            underlyingNode.removeProperty(CREATED_AT_PROPERTY);
        }
    }

    @Override
    public Node getNode() {
        return underlyingNode;
    }

    protected int getX() {
        Object o = underlyingNode.getProperty(POS_X_PROPERTY);
        return (Integer) o;
    }

    protected int getY() {
        Object o = underlyingNode.getProperty(POS_Y_PROPERTY);
        return (Integer) o;
    }

    protected int getZ() {
        Object o = underlyingNode.getProperty(POS_Z_PROPERTY);
        return (Integer) o;
    }

    @Override
    public double getPrice() {
        if (!underlyingNode.hasProperty(PRICE_PROPERTY)) {
            return 0;
        }
        Object o = underlyingNode.getProperty(PRICE_PROPERTY);
        return (Double) o;
    }

    public void setPrice(double price) {
        underlyingNode.setProperty(PRICE_PROPERTY, price);
    }

    public int getSize() {
        Object o = underlyingNode.getProperty(SIZE_PROPERTY);
        return o != null ? (int) o : null;
    }

    protected void setSize(int size) {
        underlyingNode.setProperty(SIZE_PROPERTY, size);
    }

    @Override
    public String getName() {
        return (String) underlyingNode.getProperty(NAME_PROPERTY);
    }

    public void setName(String name) {
        underlyingNode.setProperty(NAME_PROPERTY, name);
    }

    public Date getCreatedAt() {
        if (!underlyingNode.hasProperty(CREATED_AT_PROPERTY)) {
            return null;
        }
        Object o = underlyingNode.getProperty(CREATED_AT_PROPERTY);
        return o != null ? new Date((Long) o) : null;
    }

    public Date getDeletedAt() {
        if (!underlyingNode.hasProperty(DELETED_AT_PROPERTY)) {
            return null;
        }
        Object o = underlyingNode.getProperty(DELETED_AT_PROPERTY);
        return o != null ? new Date((Long) o) : null;
    }

    public Date getCompletedAt() {
        if (!underlyingNode.hasProperty(COMPLETED_AT_PROPERTY)) {
            return null;
        }
        Object o = underlyingNode.getProperty(COMPLETED_AT_PROPERTY);
        return new Date((Long) o);
    }

    @Override
    public ConstructionStatus getStatus() {
        Object o = underlyingNode.getProperty(CONSTRUCTION_STATUS_PROPERTY);
        return o != null ? ConstructionStatus.match((int) o) : null;
    }

    public void setStatus(ConstructionStatus status) {
        if (getStatus() != status) {
            underlyingNode.setProperty(CONSTRUCTION_STATUS_PROPERTY, status.getStatusId());
            if (status == ConstructionStatus.COMPLETED) {
                underlyingNode.setProperty(COMPLETED_AT_PROPERTY, System.currentTimeMillis());
            } else if (status == ConstructionStatus.REMOVED) {
                underlyingNode.setProperty(DELETED_AT_PROPERTY, System.currentTimeMillis());
            }
        }
    }

    @Override
    public final StructureWorld getWorld() {
        for(Relationship rel : underlyingNode.getRelationships(RelTypes.WITHIN, org.neo4j.graphdb.Direction.OUTGOING)) {
            if(rel.getOtherNode(underlyingNode).hasLabel(WorldNode.label())) {
                Node node = rel.getOtherNode(underlyingNode);
                return new StructureWorld(node);
            }
        }
        return null;
    }

    @Override
    public final Direction getDirection() {
        return Direction.match((int) underlyingNode.getProperty(DIRECTION_PROPERTY));
    }

    @Override
    public CuboidRegion getCuboidRegion() {
        int minX = (int) underlyingNode.getProperty(MIN_X_PROPERTY);
        int minY = (int) underlyingNode.getProperty(MIN_Y_PROPERTY);
        int minZ = (int) underlyingNode.getProperty(MIN_Z_PROPERTY);
        int maxX = (int) underlyingNode.getProperty(MAX_X_PROPERTY);
        int maxY = (int) underlyingNode.getProperty(MAX_Y_PROPERTY);
        int maxZ = (int) underlyingNode.getProperty(MAX_Z_PROPERTY);
        return new CuboidRegion(new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
    }

    public StructureNode getParent() {
        // (this)-[:substructure of]-(parent)
        Relationship rel = underlyingNode.getSingleRelationship(RelTypes.SUBSTRUCTURE_OF, org.neo4j.graphdb.Direction.OUTGOING);
        if (rel != null) {
            Node parentNode = rel.getOtherNode(underlyingNode);
            return new StructureNode(parentNode);
        }
        return null;
    }

    public List<StructureNode> getSubstructures() {
        Iterable<Relationship> relationships = underlyingNode.getRelationships(RelTypes.SUBSTRUCTURE_OF, org.neo4j.graphdb.Direction.INCOMING);
        List<StructureNode> substructures = Lists.newArrayList();
        for (Relationship rel : relationships) {
            StructureNode substructure = new StructureNode(rel.getOtherNode(underlyingNode));
            if (substructure.getStatus() != ConstructionStatus.REMOVED) {
                substructures.add(new StructureNode(rel.getOtherNode(underlyingNode)));
            }
        }
        return substructures;
    }

    public boolean hasSubstructures() {
        for (Relationship s : underlyingNode.getRelationships(org.neo4j.graphdb.Direction.INCOMING, RelTypes.SUBSTRUCTURE_OF)) {
            StructureNode sn = new StructureNode(s.getOtherNode(underlyingNode));
            if (sn.getStatus() != ConstructionStatus.REMOVED) {
                return true;
            }
        }
        return false;
    }

    public StructureNode getRoot() {
        TraversalDescription traversalDescription = underlyingNode.getGraphDatabase().traversalDescription();
        Iterator<Node> nodeIt = traversalDescription.relationships(RelTypes.SUBSTRUCTURE_OF, org.neo4j.graphdb.Direction.OUTGOING)
                .depthFirst()
                .traverse(underlyingNode)
                .nodes()
                .iterator();

        while (nodeIt.hasNext()) {
            Node n = nodeIt.next();
            if (!nodeIt.hasNext()) {
                return new StructureNode(n);
            }

        }
        return this;
    }

    public void addSubstructure(StructureNode otherNode) {
        Preconditions.checkArgument(!otherNode.isAncestorOf(this), "Can't add an ancestor as Substructure");
        Preconditions.checkArgument(!otherNode.isSubstructureOf(this), otherNode.getId() + " is already a substructure of " + getId() + "!");
        otherNode.getNode().createRelationshipTo(underlyingNode, RelTypes.SUBSTRUCTURE_OF);
    }

    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructureNode other = (StructureNode) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

    public boolean hasParent() {
        return getParent() != null;
    }

    public boolean isSubstructureOf(StructureNode structure) {
        TraversalDescription traversalDescription = underlyingNode.getGraphDatabase().traversalDescription();
        Iterator<Node> nodeIt = traversalDescription.relationships(RelTypes.SUBSTRUCTURE_OF, org.neo4j.graphdb.Direction.INCOMING)
                .breadthFirst()
                .traverse(underlyingNode)
                .nodes()
                .iterator();

        while (nodeIt.hasNext()) {
            Node n = nodeIt.next();
            if (n.getId() == structure.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAncestorOf(StructureNode structure) {
        TraversalDescription traversalDescription = underlyingNode.getGraphDatabase().traversalDescription();
        Iterator<Node> nodeIt = traversalDescription.relationships(RelTypes.SUBSTRUCTURE_OF, org.neo4j.graphdb.Direction.OUTGOING)
                .depthFirst()
                .traverse(underlyingNode)
                .nodes()
                .iterator();

        while (nodeIt.hasNext()) {
            Node n = nodeIt.next();
            if (n.getId() == structure.getId()) {
                return true;
            }
        }
        return false;
    }

    public Iterable<StructureNode> getSubStructuresWithin(CuboidRegion region) {
        final CuboidRegion thisRegion = getCuboidRegion();
        TraversalDescription traversal = getGraph().traversalDescription();
        Iterable<Node> structure = traversal.relationships(RelTypes.SUBSTRUCTURE_OF, org.neo4j.graphdb.Direction.INCOMING)
            .evaluator(Evaluators.excludeStartPosition()).evaluator(new Evaluator() {

            @Override
            public Evaluation evaluate(Path path) {
                
                
                StructureNode endStructure = new StructureNode(path.endNode());
                if (endStructure.getStatus() == ConstructionStatus.REMOVED) {
                    return Evaluation.EXCLUDE_AND_PRUNE;
                } else if(RegionUtil.overlaps(endStructure.getCuboidRegion(), thisRegion)) {
                    return Evaluation.INCLUDE_AND_CONTINUE;
                } else {
                    return Evaluation.EXCLUDE_AND_PRUNE;
                }
            }
        }).breadthFirst().traverse(underlyingNode).nodes();
        
        return NodeHelper.makeIterable(structure, StructureNode.class);
    }

    public boolean hasSubstructuresWithin(CuboidRegion region) {
        return getSubStructuresWithin(region).iterator().hasNext();
    }
    
    

}
