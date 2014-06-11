package com.sc.entity.schematic;

///*
// * Copyright (C) 2014 Chingo
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.sc.api.structure.entity.schematic;
//
//import java.io.Serializable;
//import java.util.Objects;
//import javax.persistence.Embeddable;
//import javax.validation.constraints.NotNull;
//import org.bukkit.Material;
//
///**
// * The material and the Integer data value will serve as unique idenitifier for
// * {@link  SchematicMaterialResource}
// *
// * @author Chingo
// */
//@Embeddable
//public class SchematicMaterialResourceId implements Serializable {
//
//    @NotNull
//    private Material material;
//    @NotNull
//    private Integer data;
//
//    /**
//     * JPA Constructor
//     */
//    protected SchematicMaterialResourceId() {
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param material The material
//     * @param data The data or Byte value
//     */
//    public SchematicMaterialResourceId(Material material, Byte data) {
//        this.material = material;
//        this.data = data.intValue();
//    }
//
//    /**
//     * Get the data
//     *
//     * @return The data value
//     */
//    public Integer getData() {
//        return data;
//    }
//
//    /**
//     * Get the material
//     *
//     * @return The material
//     */
//    public Material getMaterial() {
//        return material;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final SchematicMaterialResource other = (SchematicMaterialResource) obj;
//        if (this.material != other.getMaterial()) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 11 * hash + Objects.hashCode(this.material);
//        return hash;
//    }
//
//}
