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
//import org.bukkit.Material;
//
///**
// * Structure resource contains a material and a amount to describe the amount that is needed of this
// * material.
// *
// * @author Chingo
// */
//public class SchematicMaterialResource implements Serializable {
//
//    private SchematicMaterialResourceId materialResourceId;
//
//    /**
//     * The amount of this resource
//     */
//    protected int amount;
//
//    /**
//     * JPA Constructor.
//     */
//    protected SchematicMaterialResource() {
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param material The material
//     * @param data The data
//     * @param amount The amount that is needed of this resource
//     */
//    public SchematicMaterialResource(Material material, byte data, int amount) {
//        this.materialResourceId = new SchematicMaterialResourceId(material, data);
//        this.amount = amount;
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param material The material
//     * @param data The data
//     * @param amount The amount that is needed of this resource
//     */
//    public SchematicMaterialResource(Material material, Integer data, int amount) {
//        this.materialResourceId = new SchematicMaterialResourceId(material, data.byteValue());
//        this.amount = amount;
//    }
//
//    public SchematicMaterialResourceId getMaterialResourceId() {
//        return materialResourceId;
//    }
//
//    /**
//     * Gets the data value
//     *
//     * @return
//     */
//    public Integer getData() {
//        return materialResourceId.getData();
//    }
//
//    /**
//     * Sets the amount
//     *
//     * @param value The value to set the that is needed for this resource
//     */
//    public void setAmount(int value) {
//        this.amount = value;
//    }
//
//    /**
//     * Gets the amount
//     *
//     * @return The amount
//     */
//    public int getAmount() {
//        return amount;
//    }
//
//    /**
//     * Gets the material
//     *
//     * @return The material
//     */
//    public Material getMaterial() {
//        return materialResourceId.getMaterial();
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 83 * hash + Objects.hashCode(this.materialResourceId);
//        return hash;
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
//        if (!Objects.equals(this.materialResourceId, other.materialResourceId)) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "[Material: " + materialResourceId.getMaterial() + "] : " + amount;
//    }
//
//}
