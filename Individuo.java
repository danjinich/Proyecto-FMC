package com.company;
import java.math.*;

/*
* Es la estructura base del programa
* No guarda una maquina completa por uso de memoria, pero guarda su representacion binaria
*   Mejoraria si trabajariamos con bits y no con los caracteres '1' y '0'
*/

public class Individuo implements Comparable<Individuo>{
    private double fitness;
    private String genoma="";
    private int geneLength;
    private int size, nCambios;
    private String cinta="";
    //Crea un individuo nuevo
    public Individuo(int L, String objetivo, String ceros, int Pos, int Max) {
        geneLength=L;
        //Construimos un genoma aleatorio
        for (int j=1;j<=L;j++){
            if (Math.random()<0.5)
                genoma=genoma+"0";
            else
                genoma=genoma+"1";
            //endIf
        }//endFor
        fitness=objetivo.length();
        //Calcular de una vez fitness ahorra codigo
        calcFitness(objetivo,ceros,Pos,Max);
    }
    //Crea un individuo con genoma dado
    public Individuo(String genoma, int L, String objetivo, String ceros, int Pos, int Max) {
        this.genoma=genoma;
        geneLength=L;
        fitness=objetivo.length();
        calcFitness(objetivo,ceros,Pos,Max);
    }
    //Calculamos fitness desde que se construye, ahorras tiempo pero gastas memoria
    private void calcFitness(String objetivo, String ceros, int Pos, int Max) {
        Maquina Var;
        try{
            Var=new Maquina(genoma);
        }catch(Exception e){
            fitness=objetivo.length()+1; //Mayor que el maximo valor posible, para que no lo tome
            size=64;//Mayor que el maximo valor posible
            System.out.println(genoma);
            return;
        }
        int cont=0;
        String str1=Var.trabajar(ceros,Pos,Max);//Resultado de la maquina
        int res=objetivo.length();//maxima cantidad de diferencias
        for(int i=0;i<str1.length()-objetivo.length(); i++) {
            for (int j = 0; j<objetivo.length(); j++)
                if(str1.charAt(j+i)!=objetivo.charAt(j)) cont++;//recorremos todo el objetivo y un substring del resultado
            if(cont<res) {
                res = cont;//Checamos si hay menos elementos diferentes
                cinta=str1.substring(i,i+objetivo.length());
            }
            cont=0;//reiniciamos el contador de diferencias
        }
        nCambios=res;//numero de errores
        size=Var.size();
        fitness=nCambios+((double)size)/64;//Fitness es un solo valor y es mas importante el numero de errores
    }
    public double getFitness(){ return fitness; }
    public String getGenoma() { return genoma; }
    public int getSize(){ return size;}
    public int getnCambios(){ return nCambios;}
    public boolean equals(Individuo ind){
        if(ind==null) return false;
        return ind.getGenoma().equals(genoma);
    }
    public boolean fitter(Individuo ind){
        return  ind.getFitness()>fitness;
    }
    public int compareTo(Individuo ind){
        if(ind.getFitness()>fitness) return -1;
        else if(ind.getGenoma()==genoma) return 0;
        return 1;
    }
    public void print(){
        System.out.println(fitness);
    }
    public String getCinta() { return cinta; }
}


