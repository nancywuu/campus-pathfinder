/*
 * Copyright (C) 2021 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2021 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package pathfinder;

import graph.DirectedGraph;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import pathfinder.parser.CampusBuilding;
import pathfinder.parser.CampusPath;
import pathfinder.parser.CampusPathsParser;

import java.util.*;

import java.util.Map;

/**
 * This represents a campus map with paths between different locations
 * such as buildings and key points along pathways
 */
public class CampusMap implements ModelAPI {

    // AF(this) =
    //      a map representing connections between points on campus
    //      pathMap maps coordinate points to each other
    //      shortToLong keeps track of corresponding short names and long names
    //      xCoord keeps track of x-coordinates of points on the map
    //      yCoord keeps track of y-coordinates of points on the map

    // Rep Invariant:
    //      pathMap != null and does not contain null elements
    //      shortToLong != null and does not contain null elements
    //      xCoord != null and does not contain null elements
    //      yCoord != null and does not contain null elements

    DirectedGraph<Point, Double> pathMap;
    Map<String, String> shortToLong;
    Map<String, Double> xCoord;
    Map<String, Double> yCoord;

    public CampusMap(){
        List<CampusPath> pathList = CampusPathsParser.parseCampusPaths("campus_paths.csv");
        List<CampusBuilding> buildingList = CampusPathsParser.parseCampusBuildings("campus_buildings.csv");
        shortToLong = new HashMap<>();
        xCoord = new HashMap<>();
        yCoord = new HashMap<>();
        pathMap = new DirectedGraph<>();

        for(CampusPath p : pathList){
            Point parent = new Point(p.getX1(), p.getY1());
            Point child = new Point(p.getX2(), p.getY2());
            pathMap.addNode(parent);
            pathMap.addNode(child);
            pathMap.addEdge(new DirectedGraph.Edge<>(p.getDistance(), parent, child));
        }

        for(CampusBuilding b : buildingList){
            shortToLong.put(b.getShortName(), b.getLongName());
            xCoord.put(b.getShortName(), b.getX());
            yCoord.put(b.getShortName(), b.getY());
        }
        checkRep();
    }

    @Override
    public boolean shortNameExists(String shortName) {
        checkRep();
        return shortToLong.containsKey(shortName);
    }

    @Override
    public String longNameForShort(String shortName) {
        checkRep();
        return shortToLong.get(shortName);
    }

    @Override
    public Map<String, String> buildingNames() {
        checkRep();
        return shortToLong;
    }

    @Override
    public Path<Point> findShortestPath(String startShortName, String endShortName) {
        checkRep();
        Point start = new Point(xCoord.get(startShortName), yCoord.get(startShortName));
        Point end = new Point(xCoord.get(endShortName), yCoord.get(endShortName));
        if(!pathMap.containsNode(start) && !pathMap.containsNode(end)){
            throw new IllegalArgumentException("end and start unknown or invalid");
        } else if(!pathMap.containsNode(start)){
            throw new IllegalArgumentException("start unknown or invalid");
        } else if (!pathMap.containsNode(end)){
            throw new IllegalArgumentException("end unknown or invalid");
        }

        Path<Point> pathResult = dijkstraPath.findShortestPath(pathMap, start, end);

        checkRep();
        return pathResult;
    }

    /**
     * Throws an exception if the representation invariant is violated
     */
    private void checkRep(){
        assert(pathMap != null):"assert pathMap is not null";
        assert(shortToLong != null):"assert shortToLong is not null";
        assert(xCoord != null):"assert xCoord is not null";
        assert(yCoord != null):"assert yCoord is not null";

        for (Point p : pathMap.getAllNodes()) {
            assert(p != null):"assert pathMap points are not null";
            for (DirectedGraph.Edge<Point, Double> e: pathMap.getEdges(p)) {
                assert (e.getLabel() != null):"assert edges are not null";
            }
        }

        // checks shortToLong
        for (String sho : shortToLong.keySet()) {
            assert (sho != null);
            assert (shortToLong.get(sho) != null);
        }

        //checks xCoord
        for (String sho : xCoord.keySet()) {
            assert (sho != null);
            assert (xCoord.get(sho) != null);
        }

        // checks yCoord
        for (String sho : yCoord.keySet()) {
            assert (sho != null);
            assert (yCoord.get(sho) != null);
        }
    }

}
