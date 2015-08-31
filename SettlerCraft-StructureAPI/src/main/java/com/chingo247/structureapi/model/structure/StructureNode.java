/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.model.interfaces.IBaseSettler;
import com.chingo247.structureapi.model.owner.StructureOwnerNode;
import com.chingo247.structureapi.model.owner.StructureOwnerType;
import com.chingo247.structureapi.model.owner.StructureOwnershipRelation;
import com.chingo247.structureapi.model.world.StructureWorldNode;
import com.chingo247.structureapi.util.RegionUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
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

    public static final Label LABEL = DynamicLabel.label("Structure");
    public static final String ID_PROPERTY = "structureId";
    public static final String NAME_PROPERTY = "name";
    public static final String DIRECTION_PROPERTY = "direction";
    public static final String CONSTRUCTION_STATUS_PROPERTY = "constructionStatus";
    public static final String MIN_X_PROPERTY = "minX", MIN_Y_PROPERTY = "minY", MIN_Z_PROPERTY = "minZ", MAX_X_PROPERTY = "maxX", MAX_Y_PROPERTY = "maxY", MAX_Z_PROPERTY = "maxZ";
    public static final String POS_X_PROPERTY = "x", POS_Y_PROPERTY = "y", POS_Z_PROPERTY = "z";
    public static final String CREATED_AT_PROPERTY = "createdAt", DELETED_AT_PROPERTY = "deletedAt", COMPLETED_AT_PROPERTY = "completedAt";
    public static final String PRICE_PROPERTY = "price";
    public static final String SIZE_PROPERTY = "size";
    public static final String AUTO_REMOVED_PROPERTY = "autoremoved";
    public static final String CHECKED_HOLOGRAM_PROPERTY = "checkedHologram";

    // RelationShips
    private final Node underlyingNode;

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
    public final StructureWorldNode getWorld() {
        Relationship rel = underlyingNode.getSingleRelationship(DynamicRelationshipType.withName(StructureRelations.RELATION_WITHIN), org.neo4j.graphdb.Direction.OUTGOING);
        Node node = rel.getOtherNode(underlyingNode);
        return new StructureWorldNode(node);
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
        Relationship rel = underlyingNode.getSingleRelationship(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.OUTGOING);
        if (rel != null) {
            Node parentNode = rel.getOtherNode(underlyingNode);
            return new StructureNode(parentNode);
        }
        return null;
    }

    public StructureOwnershipRelation findOwnership(UUID possibleOwner) {
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelations.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            StructureOwnerNode ownerNode = new StructureOwnerNode(rel.getOtherNode(underlyingNode));
            if (ownerNode.getUUID().equals(possibleOwner)) {
                return new StructureOwnershipRelation(this, ownerNode, rel);
            }
        }
        return null;
    }

    public boolean addOwner(IBaseSettler node, StructureOwnerType type) {
        if (isOwner(node.getUUID())) {
            return false;
        }
        Relationship relationship = underlyingNode.createRelationshipTo(node.getNode(), DynamicRelationshipType.withName(StructureRelations.RELATION_OWNED_BY));
        relationship.setProperty("Type", type.getTypeId());
        return true;
    }

    public boolean isOwner(UUID possibleOwner) {
        return findOwnership(possibleOwner) != null;
    }

    public boolean isOwner(UUID possibleOwner, StructureOwnerType type) {
        StructureOwnershipRelation ownership = findOwnership(possibleOwner);
        if (ownership == null) {
            return false;
        }
        return ownership.getOwnerType() == type;
    }

    public boolean removeOwner(UUID owner) {
        StructureOwnershipRelation ownership = findOwnership(owner);
        if (ownership != null) {
            ownership.getRelation().delete();
            return true;
        }
        return false;
    }

    public List<StructureOwnerNode> getOwners() {
        List<StructureOwnerNode> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelations.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            if (rel.hasProperty("Type")) {
                Integer typeProp = (Integer) rel.getProperty("Type");
                StructureOwnerType type = StructureOwnerType.match(typeProp);
                StructureOwnerNode ownerNode = new StructureOwnerNode(rel.getOtherNode(underlyingNode));
                owners.add(ownerNode);
            }
        }
        return owners;
    }

    public List<StructureOwnerNode> getOwners(StructureOwnerType ownerType) {
        if (ownerType == null) {
            return getOwners();
        }

        List<StructureOwnerNode> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelations.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            if (rel.hasProperty("Type")) {
                Integer typeId = (Integer) rel.getProperty("Type");
                StructureOwnerType type = StructureOwnerType.match(typeId);
                if (type == ownerType) {
                    StructureOwnerNode ownerNode = new StructureOwnerNode(rel.getOtherNode(underlyingNode));
                    owners.add(ownerNode);
                }
            }
        }
        return owners;
    }

    public List<StructureOwnershipRelation> getOwnerships() {
        List<StructureOwnershipRelation> owners = Lists.newArrayList();
        for (Relationship rel : underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelations.RELATION_OWNED_BY), org.neo4j.graphdb.Direction.OUTGOING)) {
            if (rel.hasProperty("Type")) {
                Integer typeId = (Integer) rel.getProperty("Type");
                StructureOwnerType type = StructureOwnerType.match(typeId);

                owners.add(new StructureOwnershipRelation(this, new StructureOwnerNode(rel.getOtherNode(underlyingNode)), rel));

            }
        }
        return owners;
    }

    public List<StructureNode> getSubstructures() {
        Iterable<Relationship> relationships = underlyingNode.getRelationships(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.INCOMING);
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
        for (Relationship s : underlyingNode.getRelationships(org.neo4j.graphdb.Direction.INCOMING, DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE))) {
            StructureNode sn = new StructureNode(s.getOtherNode(underlyingNode));
            if (sn.getStatus() != ConstructionStatus.REMOVED) {
                return true;
            }
        }
        return false;
    }

    public StructureNode getRoot() {
        TraversalDescription traversalDescription = underlyingNode.getGraphDatabase().traversalDescription();
        Iterator<Node> nodeIt = traversalDescription.relationships(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.OUTGOING)
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
        otherNode.getNode().createRelationshipTo(underlyingNode, DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE));
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
        Iterator<Node> nodeIt = traversalDescription.relationships(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.INCOMING)
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
        Iterator<Node> nodeIt = traversalDescription.relationships(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.OUTGOING)
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

    public List<StructureNode> getSubStructuresWithin(CuboidRegion region, int limit) {
        final CuboidRegion thisRegion = getCuboidRegion();

        TraversalDescription traversal = getGraph().traversalDescription();
        Iterator<Node> nodeIt = traversal.relationships(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), org.neo4j.graphdb.Direction.INCOMING)
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
        }).breadthFirst().traverse(underlyingNode).nodes().iterator();
        
        List<StructureNode> structureNodes = new ArrayList<>();
        if(limit > 0) {
            int count = 0;
            while(nodeIt.hasNext() && count < limit) {
                structureNodes.add(new StructureNode(nodeIt.next()));
                count++;
            }
        } else {
            while(nodeIt.hasNext()) {
                structureNodes.add(new StructureNode(nodeIt.next()));
            }
        }

//        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(getWorld().getName());
//        List<StructureNode> structures = new ArrayList<>();
//
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("worldId", w.getUUID().toString());
//
//        String query
//                = "MATCH (world:" + WorldNode.LABEL.name() + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
//                + " WITH world "
//                + " MATCH (world)<-[:" + StructureRelations.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
//                + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " IS NULL"
//                + " AND NOT s." + StructureNode.ID_PROPERTY + " = " + getId()
//                + " AND NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + StructureStatus.REMOVED.getStatusId()
//                + " AND s." + StructureNode.SIZE_PROPERTY + " <= " + getCuboidRegion().getArea()
//                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + region.getMinimumPoint().getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + region.getMaximumPoint().getBlockX()
//                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + region.getMinimumPoint().getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + region.getMaximumPoint().getBlockY()
//                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + region.getMinimumPoint().getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + region.getMaximumPoint().getBlockZ()
//                + " RETURN s"
//                + " LIMIT " + limit;
//        Result result = getGraph().execute(query, params);
//        while (result.hasNext()) {
//            Map<String, Object> map = result.next();
//            for (Object o : map.values()) {
//                structures.add(new StructureNode((Node) o));
//            }
//        }
        return structureNodes;
    }

    public boolean hasSubstructuresWithin(CuboidRegion region) {
        return !getSubStructuresWithin(region, 1).isEmpty();
    }

}
