/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jDatabase;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldDAO;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
import com.chingo247.settlercraft.core.persistence.dao.world.DefaultWorldFactory;
import com.chingo247.settlercraft.core.World;
import com.chingo247.structureapi.persistence.dao.structure.StructureDAO;
import com.chingo247.structureapi.persistence.dao.structure.StructureNode;
import com.chingo247.structureapi.persistence.dao.structure.StructureOwnerType;
import com.chingo247.structureapi.persistence.dao.structure.StructureWorldNode;
import com.chingo247.settlercraft.core.Direction;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureTests {

    private static StructureDAO structureDAO;
    private static WorldDAO worldDAO;
    private static SettlerDAO settlerDAO;
    private WorldNode worldNode;
    private SettlerNode settlerNode;
    private static GraphDatabaseService graph;
    private static boolean firstTime = true;
    private UUID settlerUUID;

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Setting up");
        Neo4jDatabase database = new Neo4jDatabase(new File("G:\\Neo4j\\SettlerCraft"), "testdb", 256);
        structureDAO = new StructureDAO(database.getGraph());
        worldDAO = new WorldDAO(database.getGraph());
        settlerDAO = new SettlerDAO(database.getGraph());
        graph = database.getGraph();
    }

    @AfterClass
    public static void tearDownClass() {
        graph.shutdown();
    }

    private static int count = 0;

    @Before
    public void setUp() {
        if (firstTime) {
            System.out.println("Resetting graph");
            try (Transaction tx = graph.beginTx()) {
                graph.execute("START n=node(*) OPTIONAL MATCH (n)-[r]-() delete n,r");
                tx.success();
            }
            firstTime = false;
        }
        count++;
        System.out.println("test: " + count);

        try (Transaction tx = graph.beginTx()) {
            UUID worldUUID = UUID.randomUUID();
            worldDAO.addWorld("testWorld", worldUUID);
            worldNode = worldDAO.find(worldUUID);

            settlerUUID = UUID.randomUUID();
            settlerDAO.addSettler("testPlayer", settlerUUID);
            settlerNode = settlerDAO.find(settlerUUID);
            tx.success();
        }

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddStructure1() {
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            structureDAO.addStructure("test", new CuboidRegion(Vector.ZERO, Vector.ONE), Direction.NORTH, 100d);
            tx.success();
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("testAddStructure1: " + time + " ms");
    }

    @Test
    public void testAddStructures100() {
        long start = System.currentTimeMillis();

        try (Transaction tx = graph.beginTx()) {
            for (int i = 0; i < 100; i++) {
                structureDAO.addStructure("test", new CuboidRegion(Vector.ZERO, Vector.ONE), Direction.NORTH, 1000d);
            }
            tx.success();
        }

        long time = System.currentTimeMillis() - start;
        System.out.println("testAddStructures1000: " + time + " ms");
    }

    @Test
    public void testAddStructures1000() {
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            for (int i = 0; i < 1000; i++) {
                structureDAO.addStructure("test", new CuboidRegion(Vector.ZERO, Vector.ONE), Direction.NORTH, 1000d);
            }
            tx.success();
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("testAddStructures10K: " + time + " ms");
    }

    @Test
    public void testCountStructures() {
        long toAdd = 5;
        try (Transaction tx = graph.beginTx()) {
            for (int i = 0; i < toAdd; i++) {
                StructureNode structureNode = structureDAO.addStructure("test", new CuboidRegion(Vector.ZERO, Vector.ONE), Direction.NORTH, 1000d);
                structureNode.addOwner(settlerNode, StructureOwnerType.MEMBER);
            }
            long owned = structureDAO.getStructureCountForSettler(settlerUUID);
            Assert.assertTrue(toAdd == owned);
            tx.success();
        }
    }
    
    @Test
    public void testHasStructuresWithin() {
        try (Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.addStructure("test", new CuboidRegion(Vector.ZERO, Vector.ONE), Direction.NORTH, 0);
            StructureWorldNode structureWorldNode = new StructureWorldNode(worldNode);
            structureWorldNode.addStructure(structureNode);
            World defaultWorld = DefaultWorldFactory.instance().createWorld(structureWorldNode);
            
            boolean hasWithin = structureDAO.hasStructuresWithin(defaultWorld, new CuboidRegion(Vector.ZERO, Vector.ONE));
            
            
            Assert.assertTrue(hasWithin);
            tx.success();
        }
    }
}
