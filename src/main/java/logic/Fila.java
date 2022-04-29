package logic;

import java.util.LinkedList;
import java.util.Queue;

public class Fila {

    private Queue<Medicao> queue;
    private String name;

    public Fila(String name){
        this.name = name;
        queue = new LinkedList<>();
    }

    public void add(Medicao medicao){
        //ver tamanho
        queue.add(medicao);
        //verify
    }
    public Medicao removeFirst(){
        return queue.remove();
    }

    public int getSize(){
        return queue.size();
    }

    public String getName() {
        return name;
    }


}
