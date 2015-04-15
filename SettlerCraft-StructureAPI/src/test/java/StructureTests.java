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
import com.chingo247.settlercraft.core.persistence.repository.world.WorldNode;
import com.chingo247.settlercraft.core.persistence.repository.world.WorldRepository;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.persistence.repository.StructureData;
import com.chingo247.structureapi.persistence.repository.StructureRepository;
import com.chingo247.structureapi.world.Direction;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
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

    private static StructureRepository structureRepository;
    private static WorldRepository worldRepository;
    private static WorldNode worldNode;
    private static GraphDatabaseService graph;

    public StructureTests() {
        
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Setting up");
        Neo4jDatabase database = new Neo4jDatabase(new File("G:\\Neo4j\\SettlerCraft"), "testdb");
        structureRepository = new StructureRepository(database.getGraph());
        worldRepository = new WorldRepository(database.getGraph());
        UUID worldUUID = UUID.randomUUID();
        worldRepository.addWorld("testWorld", worldUUID);
        worldNode = worldRepository.findWorldNodeById(worldUUID);
        graph = database.getGraph();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testAddStructure1() {
        long start = System.currentTimeMillis();
        structureRepository.addStructure(worldNode, "test-", new CuboidDimension(Vector.ZERO, Vector.ONE), Direction.NORTH);
        long time = System.currentTimeMillis() - start;
        System.out.println("testAddStructure1: " + time + " ms");
    }

    @Test
    public void testAddStructures1000() {
        long start = System.currentTimeMillis();
        addStructures(1000);
        long time = System.currentTimeMillis() - start;
        System.out.println("testAddStructures1000: " + time + " ms");
    }
    
    @Test
    public void testAddStructures10K() {
        long start = System.currentTimeMillis();
        addStructures(10_000);
        long time = System.currentTimeMillis() - start;
        System.out.println("testAddStructures10K: " + time + " ms");
    }

    private void addStructures(int amount) {
        List<StructureData> data = new ArrayList<>(amount);
        System.out.println("Generating: " + amount);
        try (Transaction tx = graph.beginTx()) {
            for (int i = 0; i < amount; i++) {
                data.add(new StructureData("test", worldNode.getUUID(), worldNode.getName(), Direction.NORTH, new CuboidDimension(Vector.ZERO, Vector.ONE)));
            }
            tx.success();
        }
        
        System.out.println("Adding structures...");
        long start = System.currentTimeMillis();
        structureRepository.bulkCreateStructures(worldNode, data);
        System.out.println("Done in: " + (System.currentTimeMillis() - start) + " ms");
    }
}
