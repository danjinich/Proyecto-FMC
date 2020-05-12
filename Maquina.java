package com.company;
import java.io.* ;
import java.util.*;


public class Maquina {
    private EstadoTM[] estados;
    private TreeSet<Integer> pasados = new TreeSet<Integer>();//Checa por que estados se paso

    public Maquina() { } //constuctor vacio

    public Maquina(String s) throws Exception{
        /*Constructor de una maquina, recibe:
        *   s: la maquina en como String de "1" o "0", asume que esta bien
        *       pero revisa que no se llame a un estado que no existe*/
        int numEstados = s.length() / 16;

        estados = new EstadoTM[numEstados];
        for (int i = 0; i < numEstados; i++) {
            //System.out.println(s.substring(i * 16, i * 16 + 16));
            estados[i] = new EstadoTM(s.substring(i * 16, i * 16 + 16), numEstados);
        }
    }

    public void print() {
        System.out.println("Hay " +"\033[0;35m"+ size() + "\033[0m" + " estados en la Maquina de Turing");
        System.out.print("\t\033[0;35mEA\033[0m\t|\t\033[0;35mO");
        System.out.print("\033[0m\t|\t\033[0;35mM\033[0m\t|\t");
        System.out.print("\033[0;35mSE\033[0m\t||\t\033[0;35mO");
        System.out.print("\033[0m\t|\t\033[0;35mM\033[0m\t|\t");
        System.out.println("\033[0;35mSE\033[0m\t|");
        System.out.println(" ----------------------------------------------------------");
        if(pasados.isEmpty()){//verificamos si ha sido utilizada la maquina
            for(int i=0; i<estados.length; i++){
                System.out.print("\t\033[0;35m" + i + " \033[0m\t|\t");
                estados[i].print();
            }
            return;
        }
        for(int i : pasados){//solo se imprimen los estados por los que se paso
            System.out.print("\t" + i + " \t|\t");
            estados[i].print();
        }
    }
    public void aplanar(){
        //Combierte a una maquina en su expresion minima, dependiendo de los estados que son utilizados
        EstadoTM[] aux=new EstadoTM[pasados.size()];    //Va a ser el proximo estados
        int[] foo=new int[64];                          //Arreglo donde esta el nuevo nombre de cada estado
        int i=0;                                        //Contador
        for(int j : pasados){
            aux[i]=estados[j];
            foo[j]=i;
            i++;
        }
        int prox;
        for(int j=0; j<pasados.size(); j++){
            prox=aux[j].proxEstado('0');
            if(prox!=0)
                if(foo[prox]==0)    aux[j].setEstado0(63);
                else                aux[j].setEstado0(foo[prox]);
            prox=aux[j].proxEstado('1');
            if(prox!=0)
                if(foo[prox]==0)    aux[j].setEstado1(63);
                else                aux[j].setEstado1(foo[prox]);

        }
        estados=aux;
        pasados.clear();
    }
    public String formaBinaria(){
        String res="";
        for(EstadoTM est : estados) res+=est.formaBinaria();
        return res;
    }

    public String trabajar(String cin, int pos, int max) {
        /*Metodo en el que la Maquina funciona, recibe:
        *   cin: es la cinta, es un String de "1" y "0"
        *   pos: la posicion inicial en la cinta
        *   max: numero maximo de cambios
        */
        StringBuilder cinta = new StringBuilder(cin);//es mas facil manejar que un String
        int est = 0;//estado en el que estamos
        for (int i = 0; i < max; i++) {
            if (pos < 0 || pos >= cinta.length() || est == 63)//Checa si se salio de la cinta o si esta en Halt
                break;
            pasados.add(est);//Se anota que se paso por ese estado
            char x = cinta.charAt(pos);//caracter en la posicion actual
            cinta.setCharAt(pos, estados[est].escribir(x));//Escribe el caracter nuevo
            if (estados[est].izqDer(x) == 0)//Cambia de posicion
                pos++;
            else
                pos--;
            est = estados[est].proxEstado(x);//Cambia de estado
        }
        return cinta.toString();//Regresa la cinta sobre la que se trabajo
    }
    public boolean isEmpty(){
        return estados==null;
    }

    public int size() {
        if(pasados.isEmpty()) return estados.length;
        return pasados.size();
    }
}