package com.chingo247.blocklogger.orientdb;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import java.io.IOException;

/**
 *
 * @author Chingo
 */
public class OrientDBServer {

    private OServer server;
    private final String password = "admin";
    private final String username = "admin";
    private final OServerAdmin admin;
    private static OrientDBServer instance;

    private OrientDBServer() throws Exception {
        
        server = OServerMain.create();
        
        // TODO move this to XML... someday... when OrientDB API is more clear... maybe...
        server.startup(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<orient-server>"
                + "<network>"
                + "<protocols>"
                + "<protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>"
//                + "<protocol name=\"http\" implementation=\"com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb\"/>"
                + "</protocols>"
                + "<listeners>"
                + "<listener ip-address=\"0.0.0.0\" port-range=\"2424-2430\" protocol=\"binary\"/>"
//                + "<listener ip-address=\"0.0.0.0\" port-range=\"2480-2490\" protocol=\"http\"/>"
                + "</listeners>"
                + "</network>"
                + "<properties>"
                + "<entry name=\"server.database.path\" value=\"plugins/SettlerCraft/BlockLog\"/>"
//                + "<entry name=\"orientdb.config.file\" value=\"C:/work/dev/orientechnologies/orientdb/releases/1.0rc1-SNAPSHOT/config/orientdb-server-config.xml\"/>"
                + "<entry name=\"server.cache.staticResources\" value=\"false\"/>"
                + "<entry name=\"log.console.level\" value=\"info\"/>"
                + "<entry name=\"log.file.level\" value=\"fine\"/>"
                //The following is required to eliminate an error or warning "Error on resolving property: ORIENTDB_HOME"
                + "<entry name=\"plugin.dynamic\" value=\"false\"/>"
                + "</properties>"
                + "</orient-server>");
        server.addUser(username, password, "*");
//        server.setServerRootDirectory("plugins/SettlerCraft/BlockLogger");
        server.activate();
        
        admin = new OServerAdmin("remote:localhost").connect(username, password);
    }
    
    public boolean hasDatabase(String name) throws IOException {
        return admin.listDatabases().containsKey(name);
    }
    
    public final void createDatabase(String name, String type, boolean memory) throws IOException {
        admin.createDatabase(name, type, memory ? "memory" : "plocal");
    }
    
    
    public static OrientDBServer getInstance() throws Exception {
        if(instance == null) {
            instance = new OrientDBServer();
        }
        return instance;
    }
    
    
            
            

   

}
