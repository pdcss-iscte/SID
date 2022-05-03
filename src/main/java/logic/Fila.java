package logic;


import connectors.SQLConLocal;

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

    public void add(Medicao medicao){
        //ver tamanho
        if(i>2)
            isError(medicao);
        //verify
        if(i >= 3)
            dealWithRemove(removeFirst());

        lista[i] = medicao;
        System.out.println(medicao.getLeitura()+ "    i = " + i);
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
        System.out.println("i = "+ i);
        //cenario 1
        if(suddenChanges(medicao,lista[2],lista[1])== 2 && previousErrors(lista[2],lista[1]) == 0){
            medicao.setError(true);
        }//cenario 2
        else if(suddenChanges(medicao,lista[2]) == 1 & !lista[2].isError() && suddenChanges(medicao,lista[1])== 1 & lista[1].isError()){
            medicao.setError(true);
        }//cenario 3
        else if(suddenChanges(medicao,lista[2],lista[1]) == 2 && previousErrors(lista[2],lista[1]) == 2 && suddenChanges(medicao,lista[0]) == 1 && !lista[0].isError()){
            medicao.setError(true);
        }//cenario 4
        else if(suddenChanges(medicao,lista[2],lista[1]) == 0 && previousErrors(lista[2],lista[1]) == 2){
            lista[2].setError(false);
            lista[1].setError(false);
        }
    }
    public void dealWithRemove(Medicao medicao){
        if(medicao.isError()){
            SQLConLocal connection = IniReader.getSQLConLocal();
            getId(medicao,connection);
            insertIntoDB(medicao,connection);
        }
    }

    public Medicao removeFirst(){
        //insert into avariasensor
        Medicao temp = lista[0];
        lista[0] = lista[1];
        lista[1] = lista[2];
        i = 2;

        return temp;
    }

    private void insertIntoDB(Medicao medicao, SQLConLocal connection) {
        Connection con = connection.getConnection();
        PreparedStatement statement = null;
        try {
            String insertMedicao = "insert into estufa.avariasensor (Zona, IDSensor, Hora, Leitura, IDMedicao) values (?,?,?,?,?);";
            statement = con.prepareStatement(insertMedicao);
            statement.setInt(1,medicao.getZone().getId());
            statement.setString(2,medicao.getSensor().getId());
            statement.setTimestamp(3,medicao.getTimestamp());
            statement.setDouble(4,medicao.getLeitura());
            statement.setInt(5,medicao.getId());
            statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void getId(Medicao medicao, SQLConLocal conLocal){
        Connection con = conLocal.getConnection();
        String queryGetID = "select IDMedicao from estufa.medicao where Zona = "+medicao.getZone().getId()+" and Leitura = "+medicao.getLeitura()+" and Idsensor = '"+medicao.getSensor().getId()+"' and Hora = '"+medicao.getTimestamp()+"';";
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(queryGetID);
            resultSet.next();
            int id = resultSet.getInt(1);
            medicao.setId(id);



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getSize(){
        return i;
    }

    public String getName() {
        return name;
    }


}
