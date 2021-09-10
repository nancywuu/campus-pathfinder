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

package pathfinder.scriptTestRunner;

import graph.DirectedGraph;
import pathfinder.datastructures.Path;
import pathfinder.dijkstraPath;

import java.io.*;
import java.util.*;

/**
 * This class implements a test driver that uses a script file format
 * to test an implementation of Dijkstra's algorithm on a graph.
 */
public class PathfinderTestDriver {

    private final Map<String, DirectedGraph<String, Double>> graphs = new HashMap<>();
    private final PrintWriter output;
    private final BufferedReader input;

    // Leave this constructor public
    public PathfinderTestDriver(Reader r, Writer w) {
        // TODO: Implement this, reading commands from `r` and writing output to `w`.
        input = new BufferedReader(r);
        output = new PrintWriter(w);
        // See GraphTestDriver as an example.
    }

    // Leave this method public
    public void runTests() throws IOException {
        // TODO: Implement this.
        String inputLine;
        while((inputLine = input.readLine()) != null) {
            if((inputLine.trim().length() == 0) ||
                    (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if(st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<>();
                    while(st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    private void executeCommand(String command, List<String> arguments) {
        try {
            switch(command) {
                case "FindPath":
                    findPath(arguments);
                    break;
                case "CreateGraph":
                    createGraph(arguments);
                    break;
                case "AddNode":
                    addNode(arguments);
                    break;
                case "AddEdge":
                    addEdge(arguments);
                    break;
                case "ListNodes":
                    listNodes(arguments);
                    break;
                case "ListChildren":
                    listChildren(arguments);
                    break;
                default:
                    output.println("Unrecognized command: " + command);
                    break;
            }
        } catch(Exception e) {
            String formattedCommand = command;
            formattedCommand += arguments.stream().reduce("", (a, b) -> a + " " + b);
            output.println("Exception while running command: " + formattedCommand);
            e.printStackTrace(output);
        }
    }

    private void createGraph(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }

    private void createGraph(String graphName) {
        graphs.put(graphName, new DirectedGraph<String, Double>());
        output.println("created graph " + graphName);
    }

    private void findPath(List<String> arguments) {
        if(arguments.size() != 3){
            throw new CommandException("Bad arguments to findPath: " + arguments);
        }

        String graphName = arguments.get(0);
        String start = arguments.get(1);
        String dest = arguments.get(2);
        findPath(graphName, start, dest);
    }

    private void findPath(String graphName, String start, String dest){
        DirectedGraph<String, Double> temp = graphs.get(graphName);
        if(!temp.containsNode(start) && !temp.containsNode(dest)){
            output.println("unknown: " + start);
            output.println("unknown: " + dest);
        } else if(!temp.containsNode(start)){
            output.println("unknown: " + start);
        } else if (!temp.containsNode(dest)){
            output.println("unknown: " + dest);
        } else {
            output.println("path from " + start + " to " + dest + ":");
            Path<String> path = dijkstraPath.findShortestPath(temp, start, dest);
            Iterator<Path<String>.Segment> iter = path.iterator();
            if(path == null){
                output.println("no path found");
            } else {
                Double totalCost = 0.0;
                while(iter.hasNext()){
                    Path.Segment seg = iter.next();
                    totalCost += seg.getCost();
                    String formatVal = String.format(" with weight %.3f", seg.getCost());
                    output.println(seg.getStart() + " to " + seg.getEnd() + formatVal);
                }
                output.println(String.format("total cost: %.3f", totalCost));
            }
        }
    }

    private void addNode(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to AddNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }

    private void addNode(String graphName, String nodeName) {
        DirectedGraph<String, Double> tempGraph = graphs.get(graphName);
        tempGraph.addNode(nodeName);
        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if(arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        String edgeLabel = arguments.get(3);

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         String edgeLabel) {
        DirectedGraph<String, Double> tempGraph = graphs.get(graphName);
        Double edgeVal = Double.parseDouble(edgeLabel);
        tempGraph.addEdge(new DirectedGraph.Edge<String, Double>(edgeVal, parentName, childName));
        String formatVal = String.format("added edge %.3f", edgeVal);
        output.println(formatVal + " from " + parentName + " to " + childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        DirectedGraph<String, Double> tempGraph = graphs.get(graphName);
        String result = graphName + " contains:";
        Set<String> temp = tempGraph.getAllNodes();
        Set<String> nodes = new TreeSet<String>();

        for(String n : temp){
            nodes.add(n);
        }

        for (String node : nodes)
            result += " " + node;

        output.println(result);
    }

    private void listChildren(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to ListChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {
        DirectedGraph<String, Double> tempGraph = graphs.get(graphName);
        String result = "the children of " + parentName + " in " + graphName + " are:";

        Set<DirectedGraph.Edge<String, Double>> edges = new TreeSet<>(new Comparator<DirectedGraph.Edge<String, Double>>() {
            public int compare(DirectedGraph.Edge<String, Double> e1, DirectedGraph.Edge<String, Double> e2) {
                String l1 = e1.getChild();
                String l2 = e2.getChild();
                if(!l1.equals(l2)){
                    return l1.compareTo(l2);
                } else {
                    return e1.getLabel().compareTo(e2.getLabel());
                }
            }
        });
        for (DirectedGraph.Edge<String, Double> e : tempGraph.getEdges(parentName)){
            edges.add(e);
        }

        for(DirectedGraph.Edge<String, Double> e: edges){
            String formatVal = String.format("%.3f", e.getLabel());
            result += " " + e.getChild() + "(" + formatVal + ")";
        }
        output.println(result);
    }

    /**
     * This exception results when the input file cannot be parsed properly
     **/
    static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}
