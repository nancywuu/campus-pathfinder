package pathfinder;
import graph.DirectedGraph;
import pathfinder.datastructures.Path;
import java.util.*;
import graph.DirectedGraph.*;

public class dijkstraPath<N> {

    /**
     * Finds the shortest path using Dijkstra's algorithm between two nodes
     *
     * @param <N> generic type
     * @param graph graph containing the nodes
     * @param first starting node of path to be found
     * @param last ending node of path to be found
     * @return shortest path between starting node and ending node
     */

    public static <N>Path<N> findShortestPath(DirectedGraph<N, Double> graph, N first, N last){
        PriorityQueue<Path<N>> active = new PriorityQueue<>(new Comparator<Path<N>>() {
            @Override
            public int compare(Path<N> x, Path<N> y) {
                if(x.getCost() > y.getCost()){
                    return 1;
                } else if (x.getCost() < y.getCost()){
                    return -1;
                }
                return 0;
            }
        });

        Set<N> finished = new HashSet<>();
        N start = first;
        N end = last;

        active.add(new Path<N>(start));
        while(!active.isEmpty()){
            Path<N> minPath = active.remove();
            N minDest = minPath.getEnd();
            if(minDest.equals(end)){
                return minPath;
            }
            if(finished.contains(minDest)){
               continue;
            }
            for(Edge<N, Double> e : graph.getEdges(minDest)){
                if(!finished.contains(e.getChild())){
                    Path<N> newPath = minPath.extend(e.getChild(), e.getLabel());
                    active.add(newPath);
                }
            }
            finished.add(minDest);
        }
        return null;
    }

}
