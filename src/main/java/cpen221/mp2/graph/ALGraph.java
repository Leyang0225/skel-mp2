package cpen221.mp2.graph;

import javax.lang.model.element.NestingKind;
import java.util.*;


public class ALGraph<V extends Vertex, E extends Edge<V>> implements MGraph<V, E> {

    /*class E extends Edge<V> {

        public E(V v1, V v2, int length) {
            super(v1, v2, length);
        }

        public boolean equals(Object o) {
            if (o instanceof Edge<?>) {
                Edge<?> other = (Edge<?>) o;
                if (other.v1().equals(this.v1()) && other.v2().equals(this.v2())) {
                    return true;
                }
            }
            return false;
        }

    }*/

    class DiEdge{
        E e;
        V to;


        public DiEdge(E e, V to) {
            this.e = e;
            this.to = to;
        }

        public DiEdge(E e){
            this.e = e;
            this.to = e.v2();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DiEdge diEdge = (DiEdge) o;
            return Objects.equals(e, diEdge.e) && Objects.equals(to, diEdge.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(e, to);
        }
    }


    Map<Integer, String> VertexMap;
    Map<Integer, Boolean> HasVertexId;
    Map<String, Boolean> HasVertexName;

    Map<V, DiEdge> FirstEdge;
    Map<DiEdge, DiEdge> NextEdge;

    public ALGraph() {
        VertexMap = new HashMap<>();
        HasVertexId = new HashMap<>();
        HasVertexName = new HashMap<>();
        FirstEdge = new HashMap<>();
        NextEdge = new HashMap<>();
    }

    @Override
    public boolean addVertex(V v) { //tested
        if(HasVertexName.containsKey(v.name())) return false;
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

    private boolean addSingleEdge(E e){
        if(FirstEdge.containsKey(e.v1()) == false){
            //System.out.println("add1 " + e.v1().id() + " " +  e.v2().id() + " " +  e.length());
            FirstEdge.put(e.v1(), new DiEdge(e));
        }
        else {
            //System.out.println("add2 " + e.v1().id() + " " +  e.v2().id() + " " +  e.length());
            NextEdge.put(new DiEdge(e), FirstEdge.get(e.v1()));
            FirstEdge.put(e.v1(), new DiEdge(e));
        }

        return true;
    }

    @Override
    public boolean addEdge(E e) { //tested
        if(vertex(e.v1()) == false || vertex(e.v2()) == false) return false;
        else if(edge(e.v1(), e.v2()) == true) return false;
        else {
            E e1 = (E) new Edge(e.v2(), e.v1(), e.length());
            addSingleEdge(e);
            addSingleEdge(e1);
            return true;
        }
    }

    @Override
    public boolean edge(E e) { //tested
        if(vertex(e.v2()) == false || vertex(e.v1()) == false) return false;
        else {
            if(FirstEdge.containsKey(e.v1()) == false) return false;
            for(DiEdge x = FirstEdge.get(e.v1()); ; x = NextEdge.get(x)){
                //System.out.println(NextEdge.containsKey(x));
                //System.out.println(e.v1().id() + " " + e.v2().id() + " " + e.length());
                //System.out.println(x.v1().id() + " " + x.v2().id() + " " + x.length());
                if(e.equals(x.e)) return true;
                if(NextEdge.containsKey(x) == false) break;
            }
            return false;
        }
    }

    @Override
    public boolean edge(V v1, V v2) { //tested
        if(vertex(v2) == false || vertex(v1) == false) return false;
        else {
            if(FirstEdge.containsKey(v1) == false) return false;
            for(DiEdge x = FirstEdge.get(v1); ; x = NextEdge.get(x)){
                if(v2.equals(x.e.v2())) return true;
                if(NextEdge.containsKey(x) == false) break;
            }
            return false;
        }
    }

    @Override
    public int edgeLength(V v1, V v2) { //tested
        if(edge(v1, v2) == false) return 0;
        else {
            for(DiEdge x = FirstEdge.get(v1); ; x = NextEdge.get(x)){
                if(v2.equals(x.e.v2())) return x.e.length();
                if(NextEdge.containsKey(x) == false) break;
            }
            return 0;
        }
    }


    @Override
    public int edgeLengthSum() {
        int sum = 0;
        for(int key : VertexMap.keySet()){
            V v = (V) new Vertex(key, VertexMap.get(key));
            if(FirstEdge.containsKey(v) == false) continue;
            for(DiEdge e = FirstEdge.get(v); ; e = NextEdge.get(e)){
                if(e.e.v1().id() <= e.e.v2().id()) sum = sum + e.e.length();
                if(NextEdge.containsKey(e) == false) break;
            }
        }
        return sum;
    }

    private void removeSingleEdge(E e){
        V v = e.v1();

        if(FirstEdge.get(v).e.equals(e)){
            FirstEdge.remove(v);
            if(NextEdge.containsKey(new DiEdge(e))){
                FirstEdge.put(v, NextEdge.get(new DiEdge(e)));
            }
            return;
        }
        else for(DiEdge now = FirstEdge.get(v); ; now = NextEdge.get(now)){
            //System.out.println(now.e.v1().id() + " " + now.e.v2().id() + " " + now.e.length());
            if(NextEdge.get(now).e.equals(e)){
                NextEdge.remove(now);
                if(NextEdge.containsKey(new DiEdge(e))){
                    NextEdge.put(now, NextEdge.get(new DiEdge(e)));
                }
                return;
            }

        }
    }

    @Override
    public boolean remove(E e) {
        if(edge(e) == false) return false;
        else {
            E e1 = (E) new Edge(e.v2(), e.v1(), e.length());
            removeSingleEdge(e);
            removeSingleEdge(e1);
            return true;
        }
    }

    @Override
    public boolean remove(V v) {
        if(vertex(v) == false) return false;
        else if(FirstEdge.containsKey(v)) return false;
        else {
            VertexMap.remove(v.id());
            HasVertexId.remove(v.id());
            HasVertexName.remove(v.name());

            return true;
        }
    }

    @Override
    public Set<V> allVertices() {
        Set<V> ret = new HashSet<>();
        for(Map.Entry<Integer, String> entry : VertexMap.entrySet()){
            ret.add((V) new Vertex(entry.getKey(), entry.getValue()));
        }

        return ret;
    }

    @Override
    public Set<E> allEdges(V v) {
        Set<E> ret = new HashSet<>();

        if(FirstEdge.containsKey(v) != false){
            for(DiEdge e = FirstEdge.get(v); ; e = NextEdge.get(e)){
                ret.add(e.e);
                if(NextEdge.containsKey(e) == false) break;
            }
        }


        return ret;
    }

    @Override
    public Set<E> allEdges() {
        Set<V> Vertices = allVertices();
        Set<E> ret = new HashSet<>();

        for(V now : Vertices){
            if(FirstEdge.containsKey(now) == false) continue;
            for(DiEdge e = FirstEdge.get(now); ; e = NextEdge.get(e)){
                if(e.e.v1().id() <= e.e.v2().id()) ret.add(e.e);
                if(NextEdge.containsKey(e) == false) break;
            }
        }

        return ret;

    }

    @Override
    public Map<V, E> getNeighbours(V v) {
        Map<V, E> ret = new HashMap<>();
        if(FirstEdge.containsKey(v) != false){
            for(DiEdge e = FirstEdge.get(v); ; e = NextEdge.get(e)){
                ret.put(e.e.v2(), e.e);
                if(NextEdge.containsKey(e) == false) break;
            }
        }

        return ret;
    }
}
