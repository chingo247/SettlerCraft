/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entity.living;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Villager.Profession;

/**
 *
 * @author Chingo
 */
public class Labourer {
    
    private final NPC npc;
    private Profession profession;
    
    public Labourer(NPC npc, Profession profession) {
        Preconditions.checkNotNull(profession);
        Preconditions.checkNotNull(npc);
        this.npc = npc;
    }

    public Profession getProfession() {
        return profession;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Labourer)) return false;
        Labourer l = (Labourer) obj;
        return l.getNpc().getId() == this.getNpc().getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.npc.getId() + this.getNpc().getName());
        return hash;
    }
    
    
}
