package com.company;

public class EstadoTM {

    private int estado0, estado1, mov0, mov1, esc0, esc1;

    public EstadoTM(String s, int max) throws Exception{
        /*Es el constructor de cada estado, recibe:
        *   s: un String de 16 caracteres con "1" y "0".
        *   max: el numero de estados en la maquina.*/
        esc0=Integer.parseInt(s.substring(0,1));
        mov0=Integer.parseInt(s.substring(1,2));
        estado0=Integer.parseInt(s.substring(2,8), 2);
        if (estado0!=63 && estado0>=max) {
            throw new Exception();
        }
        esc1=Integer.parseInt(s.substring(8,9));
        mov1=Integer.parseInt(s.substring(9,10));
        estado1=Integer.parseInt(s.substring(10,16), 2);
        if (estado1!=63 && estado1>=max) {
            throw new Exception();
        }
    }
    public String formaBinaria(){
        String res="";
        res+=Integer.toString(esc0);
        res+=Integer.toString(mov0);
        res+=toBin(estado0);
        res+=Integer.toString(esc1);
        res+=Integer.toString(mov1);
        res+=toBin(estado1);

        return res;
    }
    private static String toBin(int n){
        String res="";
        for(int i=0; i<6;i++){
            res=Integer.toString(n%2)+res;
            n=n/2;
        }
        return res;
    }
    public void print(){//imprime cada estado
        System.out.print("\033[0;35m"+escribir('0')+"\033[0m\t|\t");
        if(mov0==0)
            System.out.print("\033[0;35mR\033[0m\t|\t");
        else
            System.out.print("\033[0;35mL\033[0m\t|\t");
        if(estado0==63)
            System.out.print("\033[0;35mH\033[0m\t||\t");//checa si es halt
        else
            System.out.print("\033[0;35m"+estado0+"\033[0m\t||\t");
        System.out.print("\033[0;35m"+escribir('1')+"\033[0m\t|\t");
        if(mov1==0)
            System.out.print("\033[0;35mR\033[0m\t|\t");
        else
            System.out.print("\033[0;35mL\033[0m\t|\t");
        if(estado1==63)//checa si es halt
            System.out.println("\033[0;35mH\033[0m\t|");
        else
            System.out.println("\033[0;35m"+estado1+"\033[0m\t|");
    }

    public int proxEstado(char x){//Checa cual es el proximo estado dependiendo si x es '1' o '0'
        if (x=='1')
            return estado1;
        else
            return estado0;
    }
    public void setEstado0(int estado0){
        this.estado0=estado0;
    }
    public void setEstado1(int estado1){
        this.estado1=estado1;
    }
    public int izqDer(char x){//Checa cual es el proximo movimiento dependiendo si x es '1' o '0'
        if(x=='1'){
            return mov1;
        }else{
            return mov0;
        }
    }
    public char escribir(char x){//Checa que se debe escribir si x es '1' o '0'
        if (x=='1')
            return (char) ('0'+esc1);
        else
            return (char) ('0'+esc0);
    }
}
