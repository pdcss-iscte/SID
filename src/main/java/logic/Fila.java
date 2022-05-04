package logic;


import com.mongodb.util.JSON;
import connectors.SQLConLocal;
import org.json.JSONObject;

import java.sql.*;

public class Fila {

    private Medicao[] lista;

    private int i;
    private String name;

    public Fila(String name){
        this.name = name;
        lista = new Medicao[3];
        i= 0;
    }

    public void add(Medicao medicao) throws SQLException {
        //ver tamanho
        if(i>2)
            isError(medicao);
        //verify
        if(i >= 3)
            dealWithRemove(removeFirst());

        lista[i] = medicao;
        i++;

    }

    public int suddenChanges(Medicao medicaoAtual, Medicao m1, Medicao m2) {
        int changes = 0;
        if(Math.abs((medicaoAtual.getLeitura() - m1.getLeitura())) > 10) changes++;
        if(Math.abs((medicaoAtual.getLeitura() - m2.getLeitura())) > 10) changes++;
        return changes;
    }

    public int suddenChanges(Medicao medicaoAtual, Medicao m1) {
        if(Math.abs((medicaoAtual.getLeitura() - m1.getLeitura())) > 10) return 1;
        return 0;
    }

    public int previousErrors(Medicao m1, Medicao m2) {
        int errors = 0;
        if(m1.isError()) errors++;
        if(m2.isError()) errors++;
        return errors;
    }

    public void isError(Medicao medicao){
        System.out.println("Zona medicao:     "+ medicao.getSensor().getId());
        System.out.println("Nome Fila:     "+ getName());
        System.out.println("VALORES Fila:     "+ " posicao 0 " + lista[0].getLeitura() + " posicao 1 " + lista[1].getLeitura() + " posicao 2 " + lista[2].getLeitura());


        //cenario 1 e 2
        if(suddenChanges(medicao,lista[2],lista[1])== 2 && previousErrors(lista[2],lista[1]) < 2){
            medicao.setError(true);
        }//cenario 2
        else if((suddenChanges(medicao,lista[2]) == 1 && lista[1].isError() ) || (suddenChanges(medicao,lista[1])== 1 && lista[2].isError())){
            medicao.setError(true);
        }//cenario 3
        else if(suddenChanges(medicao,lista[2],lista[1]) == 2 && previousErrors(lista[2],lista[1]) == 2 && suddenChanges(medicao,lista[0]) == 1 && !lista[0].isError()){
            medicao.setError(true);
        }//cenario 4
        else if(suddenChanges(medicao,lista[2],lista[1]) == 0 && previousErrors(lista[2],lista[1]) == 2){
            lista[2].setError(false);
            lista[1].setError(false);
            //FAZER A QUERY PARA APAGAR AS DUAS ULTIMAS ENTRADAS DA TABELA AVARIASENS0R
        }
    }
    public void dealWithRemove(Medicao medicao) throws SQLException {
        SQLConLocal connection = IniReader.getSQLConLocal();
        if(medicao.isError()){
            connection.insertIntoAvaria(medicao);
        }else{

                connection.insertIntoDB(medicao);

        }
            connection.closeConnection();
    }

    public Medicao removeFirst(){
        //insert into avariasensor
        Medicao temp = lista[0];
        lista[0] = lista[1];
        lista[1] = lista[2];
        i = 2;

        return temp;
    }


    public int getSize(){
        return i;
    }

    public String getName() {
        return name;
    }


}
