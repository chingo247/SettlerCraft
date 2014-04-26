
import com.settlercraft.core.util.Database.ShopDBUtil;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chingo
 */
public class SCShop extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable(); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void registerListeners() {
        
    }
    
    private void initDB() {
        ShopDBUtil.addAnnotatedClasses(
                // Classes here
        );
    }
    
}
