package cpen221.mp2.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AMGraph<V extends Vertex, E extends Edge<V>> implements MGraph<V, E> {

    /**
     * Create an empty graph with an upper-bound on the number of vertices
     * @param maxVertices is greater than 1
     *
     */

    private int[][] AdjMat;
    Map<Integer, String> VertexMap;
    Map<Integer, Boolean> HasVertexId;
    Map<String, Boolean> HasVertexName;


    public AMGraph(int maxVertices) {
        // TODO: Implement this method
        AdjMat = new int[maxVertices + 1][maxVertices + 1];
        VertexMap = new HashMap<>();
        HasVertexId = new HashMap<>();
        HasVertexName = new HashMap<>();
    }

    @Override
    public boolean addVertex(V v) { //tested
        if(v.id() > AdjMat.length) return false;
        else if(HasVertexName.containsKey(v.name())) return false;
        else if(HasVertexId.containsKey(v.id())) return false;
        else {
            VertexMap.put(v.id(), v.name());
            HasVertexId.put(v.id(), true);
            HasVertexName.put(v.name(), true);
        }

        return true;
    }

    @Override
    public boolean vertex(V v) { //tested
        if(VertexMap.get(v.id()) == v.name()) return true;
        else return false;
    }

    @Override
    public boolean addEdge(E e) { //tested
        if(vertex(e.v1()) == false || vertex(e.v2()) == false) return false;
        else if(AdjMat[e.v1().id()][e.v2().id()] != 0) return false;
        else {
            AdjMat[e.v1().id()][e.v2().id()] = e.length();
            AdjMat[e.v2().id()][e.v1().id()] = e.length();
            return true;
        }
    }

    @Override
    public boolean edge(E e) { //tested
        if(vertex(e.v2()) == false || vertex(e.v1()) == false) return false;
        else if(AdjMat[e.v1().id()][e.v2().id()] == 0) return false;
        else return true;
    }

    @Override
    public boolean edge(V v1, V v2) { //tested
        if(vertex(v2) == false || vertex(v1) == false) return false;
        else if(AdjMat[v1.id()][v2.id()] == 0) return false;
        else return true;
    }

    @Override
    public int edgeLength(V v1, V v2) { //tested
        if(edge(v1, v2) == false) return 0;
        else return AdjMat[v1.id()][v2.id()];
    }

    @Override
    public int edgeLengthSum() { //tested

        int sum = 0;
        int n = AdjMat.length;
        for(int i = 0; i < n; i++){
            for(int j = 0; j <= i; j++){
                sum += AdjMat[i][j];
            }
        }

        return sum;
    }

    @Override
    public boolean remove(E e) { //tested

        if(edge(e) == false) return false;
        else {
            AdjMat[e.v1().id()][e.v2().id()] = 0;
            AdjMat[e.v2().id()][e.v1().id()] = 0;
            return true;
        }
    }

    @Override
    public boolean remove(V v) { //tested

        if(vertex(v) == false) return false;
        else {
            /**
             * if there are edges linked to the vertex, it can not be deleted
             */
            int n = AdjMat.length;
            for(int i = 0; i < n; i++){
                if(AdjMat[v.id()][i] != 0) return false;
            }

            VertexMap.remove(v.id());
            HasVertexId.remove(v.id());
            HasVertexName.remove(v.name());

            return true;
        }

    }

    @Override
    public Set<V> allVertices() { //tested
        Set<V> ret = new HashSet<>();
        for(Map.Entry<Integer, String> entry : VertexMap.entrySet()){
            ret.add((V) new Vertex(entry.getKey(), entry.getValue()));
        }

        return ret;
    }

    @Override
    public Set<E> allEdges(V v) { //tested
        int n = AdjMat.length;

        Set<E> ret = new HashSet<>();

        for(int i = 0; i < n; i++){
            if(AdjMat[v.id()][i] != 0){
                ret.add((E) new Edge<V>(v, (V) new Vertex(i, VertexMap.get(i)), AdjMat[v.id()][i]));
            }
        }

        return ret;
    }

    @Override
    public Set<E> allEdges() {
        int n = AdjMat.length;

        Set<E> ret = new HashSet<>();

        for(int i = 0; i < n; i++){
            for(int j = 0; j <= i; j++){
                if(AdjMat[i][j] != 0){
                    ret.add((E) new Edge<V>((V) new Vertex(i, VertexMap.get(i)), (V) new Vertex(j, VertexMap.get(j)), AdjMat[i][j]));
                }
            }
        }

        return ret;
    }

    @Override
    public Map<V, E> getNeighbours(V v) {
        Map<V, E> ret = new HashMap<>();
        int n = AdjMat.length;
        for(int i = 0; i < n; i++){
            if(AdjMat[v.id()][i] != 0){
                V to = (V) new Vertex(i, VertexMap.get(i));
                ret.put(to, (E) new Edge<V>(v, to, AdjMat[v.id()][i]));
            }
        }
        return ret;
    }
}
