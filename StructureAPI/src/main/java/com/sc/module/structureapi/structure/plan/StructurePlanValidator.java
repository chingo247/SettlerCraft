/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.plan;

/**
 *
 * @author Chingo
 */
public class StructurePlanValidator {
    
//    private  StructurePlanValidator() {}
//
//    public static void validate(File xml) throws StructureDataException {
//
//        SAXReader reader = new SAXReader();
//        try {
//            Document d = reader.read(xml);
//
//            // Validate StructureAPI Nodes
//            performStructureAPIChecks(xml, d);
//
//            // Validate WorldGuard Nodes
//            performWorldGuardChecks(xml, d);
//
//            // Validate StructureDisplays
////            performStructureDisplayChecks(xml, d);
//
//            // Validate Holograms
//            performHologramsCheck(xml, d);
//
//        } catch (DocumentException ex) {
//            Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
//
//    private static void performHologramsCheck(File xml, Document d) throws StructureDataException {
//        List<Node> nodes = d.selectNodes(Nodes.HOLOGRAM_NODE);
//
//        if (nodes != null && !nodes.isEmpty()) {
//            int count = 0;
//            for (Node n : nodes) {
//                Node xNode = n.selectSingleNode("x");
//                Node yNode = n.selectSingleNode("y");
//                Node zNode = n.selectSingleNode("z");
//
//                if (xNode == null || yNode == null || zNode == null) {
//                    throw new StructureDataException("Missing values(x,y,z) for 'Hologram #" + count + "' in " + xml.getAbsolutePath());
//                }
//
//                try {
//                    Integer.parseInt(xNode.getText());
//                } catch (NumberFormatException nfe) {
//                    throw new StructureDataException("Invalid x value should 'Hologram #" + count + "' in " + xml.getAbsolutePath() + ", value should be a number");
//                }
//
//                try {
//                    Integer.parseInt(yNode.getText());
//                } catch (NumberFormatException nfe) {
//                    throw new StructureDataException("Invalid y value for 'Hologram #" + count + "' in " + xml.getAbsolutePath() + ", value should be a number");
//                }
//
//                try {
//                    Integer.parseInt(zNode.getText());
//                } catch (NumberFormatException nfe) {
//                    throw new StructureDataException("Invalid z value for 'Hologram #" + count + "' in " + xml.getAbsolutePath() + ", value should be a number");
//                }
//
//                Node linesNode = n.selectSingleNode("Lines");
//                if (linesNode == null) {
//                    throw new StructureDataException("Missing 'Lines' node for 'Hologram # " + count + "' in " + xml.getAbsolutePath());
//                }
//
//                List<Node> lineNodes = n.selectNodes("Lines/Line");
//                if (lineNodes == null || lineNodes.isEmpty()) {
//                    throw new StructureDataException("Missing 'Line' nodes for 'Hologram # " + count + "' in " + xml.getAbsolutePath());
//                }
//
//                count++;
//            }
//        }
//    }
//
//
//
//    private static void performStructureAPIChecks(File xml, Document d) throws StructureDataException {
//        if (isStructurePlan(d)) {
//
//            if (!hasSchematic(xml)) {
//                throw new StructureDataException("schematic was found for " + xml.getAbsolutePath());
//            }
//
//            Node nameNode = d.selectSingleNode(Nodes.NAME_NODE);
//            if (nameNode == null) {
//                throw new StructureDataException("Missing 'Name' node for " + xml.getAbsolutePath());
//            }
//        }
//    }
//
//    private static boolean isStructurePlan(Document d) {
//        Element root = d.getRootElement();
//
//        return root.getName().equals(STRUCTUREPLAN_NODE);
//    }
//
//    private static File getSchematic(File xml) {
//        SAXReader reader = new SAXReader();
//        Document d;
//        File file;
//        try {
//            d = reader.read(xml);
//
//            Node pathNode = d.selectSingleNode(Nodes.STRUCTURE_SCHEMATIC_NODE);
//            if (pathNode == null) {
//                return null;
//            }
//
//            String path = pathNode.getText();
//
//            if (path == null || path.trim().isEmpty()) {
//                return null;
//            }
//
//            file = new File(xml.getParent(), path);
//
//            if (!file.exists()) {
//                return null;
//            }
//
//            if (!FilenameUtils.isExtension(file.getName(), "schematic")) {
//                return null;
//            }
//
//        } catch (DocumentException ex) {
//            Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//
//        return file;
//    }
//
//    private static boolean hasSchematic(File xml) {
//        return getSchematic(xml) != null;
//    }
//
//    private static void performWorldGuardChecks(File xml, Document d) throws StructureDataException {
//        if (d.selectSingleNode(Nodes.WORLDGUARD_FLAGS_NODE) != null) {
//            List<Node> nodes = d.selectNodes(Nodes.WORLDGUARD_FLAG_NODE);
//
//            for (Node n : nodes) {
//                if (n.selectSingleNode("Name") == null) {
//                    throw new StructureDataException("Missing name for flag in " + xml.getAbsolutePath());
//                }
//                if (n.selectSingleNode("Value") == null) {
//                    throw new StructureDataException("Missing value for flag in " + xml.getAbsolutePath());
//                }
//
//                Flag f = DefaultFlag.fuzzyMatchFlag(n.selectSingleNode("Name").getText());
//                if (f == null) {
//                    throw new StructureDataException("Flag '" + n.selectSingleNode("Name").getText() + "' not recognized");
//                }
//
//                try {
//                    Object v = f.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), n.selectSingleNode("Value").getText());
//                    System.out.println("Flag: " + f.getName() + " Value: " + v);
//                } catch (InvalidFlagFormat ex) {
//                    Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }

}
