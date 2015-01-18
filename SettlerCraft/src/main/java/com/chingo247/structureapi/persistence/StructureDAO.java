package com.chingo247.structureapi.persistence;

//
///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.settlercraft.structureapi.persistence.hibernate;
//
//import com.chingo247.settlercraft.structureapi.structure.QStructure;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
//import com.mysema.query.jpa.JPQLQuery;
//import com.mysema.query.jpa.hibernate.HibernateQuery;
//import java.util.List;
//import org.hibernate.Session;
//
///**
// * Structure Data Access Object for performing actions on the database
// * @author Chingo
// */
//public class StructureDAO extends AbstractDAOImpl<Structure>  {
//
//
//    public Structure getStructure(String world, int x, int y, int z) {
//        QStructure qStructure = QStructure.structure;
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//
//        Structure structure = query.from(qStructure)
//                .where(qStructure.location().world.eq(world)
//                        .and(qStructure.dimension().minX.loe(x))
//                        .and(qStructure.dimension().maxX.goe(x))
//                        .and(qStructure.dimension().minZ.loe(z))
//                        .and(qStructure.dimension().maxZ.goe(z))
//                        .and(qStructure.dimension().minY.loe(y))
//                        .and(qStructure.dimension().maxY.goe(y))
//                        .and(qStructure.state.ne(Structure.State.REMOVED))
//                ).singleResult(qStructure);
//        session.close();
//        return structure;
//    }
//
//    public boolean overlaps(String world, CuboidDimension dimension) {
//        QStructure qStructure = QStructure.structure;
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        boolean result = query.from(qStructure)
//                .where(qStructure.location().world.eq(world)
//                        .and(qStructure.dimension().maxX.goe(dimension.getMinX()).and(qStructure.dimension().minX.loe(dimension.getMaxX())))
//                        .and(qStructure.dimension().maxY.goe(dimension.getMinY()).and(qStructure.dimension().minY.loe(dimension.getMaxY())))
//                        .and(qStructure.dimension().maxZ.goe(dimension.getMinZ()).and(qStructure.dimension().minZ.loe(dimension.getMaxZ())))
//                        .and(qStructure.state.ne(Structure.State.REMOVED))
//                ).exists();
//        session.close();
//        return result;
//    }
//
//    public List<Structure> getStructuresWithin(String world, CuboidDimension dimension) {
//        QStructure qStructure = QStructure.structure;
//        Session session = HibernateUtil.getSession();
//        JPQLQuery query = new HibernateQuery(session);
//        List<Structure> result = query.from(qStructure)
//                .where(qStructure.location().world.eq(world)
//                        .and(qStructure.dimension().maxX.goe(dimension.getMinX()).and(qStructure.dimension().minX.loe(dimension.getMaxX())))
//                        .and(qStructure.dimension().maxY.goe(dimension.getMinY()).and(qStructure.dimension().minY.loe(dimension.getMaxY())))
//                        .and(qStructure.dimension().maxZ.goe(dimension.getMinZ()).and(qStructure.dimension().minZ.loe(dimension.getMaxZ())))
//                        .and(qStructure.state.ne(Structure.State.REMOVED))
//                ).list(qStructure);
//        session.close();
//        return result;
//    } 
//
//}
