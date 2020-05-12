package com.company;
import java.io.*;
import java.math.*;
import java.util.*;
import java.util.Random;
/*
 * Autor: Dan Jinich
 * Clase: Fundamentos Matematicos de la Computacion
 */

public class Main {
    static PrintStream so=System.out;
    static String Resp;
    static int E, C, Pos, L, Max, G, iTmp, size;
    /*	E 	    - Numero de estados
     *	C 	    - Numero de ceros en la cinta inicial
     *	Pos	    - Posicion inicial en la cinta
     *	L	    - Longitud del genoma
     *	Max	    - Maximo numero de iteraciones de la maquina
     *	G	    - Número de iteraciones/generaciones
     *	iTmp    - Temporal para actualización de parámetros
     *  size    - Tamaño de la poblacion
     */
    static int maxE=63, minE=0;         //Cotas de el numero de estados
    static int maxG=1000000000, minG=1; //Cotas de iteraciones
    static int maxM=100000,  minM=1;    //Cotas del numero de cambios de la maquina
    static int maxC=100000;             //Cota superior del tamaño de la cinta, la inferior es la longitud del objetivo
    static int minP=0;                  //Cota inferior de la posicion inicial en la cinta, la superior es el numero de ceros
    static int maxS=100000, minS=5;     //Cotas para el tamaño de la poblacion
    static Maquina Var=new Maquina();   // Arreglo (vector) de valores para c/u de las variables
    static BufferedReader Fbr,Kbr;
    static String ceros, objetivo;      // Cinta y resultado deseado
    static Poblacion poblacion;
    static Individuo mejor;
    static double mutationRate=0.06, crossRate=0.86;//valores obtenidos por cross validation
    //Crea el documento donde guardamos los parametros, en caso de que no exista
    public static void CreaParams() throws Exception {
        try {
            Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("AlgGen.dat"))));
        }catch (Exception e){
            PrintStream Fps=new PrintStream(new FileOutputStream(new File("AlgGen.dat")));
            Fps.println("63");	                    //1) Estados
            Fps.println("1000");	                //2) Numero de ceros en la cinta
            Fps.println("500");	                    //3) Inicio maquina
            Fps.println("1000");                    //4) Maximo numero de iteraciones de la maquina
            Fps.println("10000");	                //5) Iteraciones
            Fps.println("20");                      //7) Tamaño de la población
        }//endCatch
    }
    //Lee el documento de parametros
    public static void GetParams() throws Exception {
        Fbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File("AlgGen.dat"))));
        E=Integer.parseInt(Fbr.readLine());             //1) Estados
        C =Integer.parseInt(Fbr.readLine());            //2) Ceros
        Pos =Integer.parseInt(Fbr.readLine());          //3) Posicion
        Max =Integer.parseInt(Fbr.readLine());          //4) Maximo
        G =Integer.parseInt(Fbr.readLine());            //5) Iteraciones
        size=Integer.parseInt(Fbr.readLine());          //7) Tamaño de poblacion
    }
    //Escribe en el documento de parametros los parametros actuales
    public static void ActualizaParams() throws Exception {
        PrintStream Fps=new PrintStream(new FileOutputStream(new File("AlgGen.dat")));
        Fps.println(E);			        //1) Estados
        Fps.println(C);				    //2) Ceros
        Fps.println(Pos);				//3) Posicion
        Fps.println(Max);				//4) Maximo
        Fps.println(G);				    //5) Iteraciones
        Fps.println(size);              //7) Tamaño de poblacion
    }
    //Imprime los parametros
    public static void DispParams() throws Exception {
        so.println();
        so.println("\033[1;34m1)\033[0m Numero de estados:                   \033[0;35m"+E);
        so.println("\033[1;34m2)\033[0m Numero de ceros en la cinta:         \033[0;35m"+ C);
        so.println("\033[1;34m3)\033[0m Posicion inicial en la cinta:        \033[0;35m"+ Pos);
        so.println("\033[1;34m4)\033[0m Maximo numero de cambios de estado:  \033[0;35m"+ Max);
        so.println("\033[0;35m** Long. del genoma:                    "+ L);
        so.println("\033[1;34m5)\033[0m Numero de iteraciones:               \033[0;35m"+ G);
        so.println("\033[1;34m6)\033[0m Tamaño de la población:              \033[0;35m"+size);
        so.print("\033[0m");
    }
    //Verifica que los parametros se encuentren entre sus respectivas cotas
    public static boolean CheckParams(int Opcion) {
        switch(Opcion) {
            case 1:
                E=iTmp;
                if (E<minE|E>maxE) return false;
                break;
            case 2:
                C =iTmp;
                if (C<objetivo.length()|C>maxC) {
                    so.println("La opcion 2 debe ser mayor a la longitud del objetivo:\t"+objetivo.length());
                    return false;}
                if(Pos>=C) {so.println("La opcion 3 no puede ser mayor a la opcion 2"); return false;}
                break;
            case 3:
                Pos =iTmp;
                if (Pos<minP|Pos>=C) return false;
                break;
            case 4:
                Max =iTmp;
                if (Max<minM|Max>maxM) return false;
                break;
            case 5:
                G =iTmp;
                if (G<minG|G>maxG) return false;
                break;
            case 6:
                size=iTmp;
                if(size>maxS || size<minS) return false;
                break;
        }//endSwitch
        return true;
    }
    //Modifica los parametros
    public static void Modifica() throws Exception {
        Kbr = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            L=E*16;						// Long. del individuo
            objetivo="";
            ceros = new String(new char[C]).replace('\0', '0');//Construye un string de puros 0 de longitud C
            DispParams();
            so.print("\nModificar\033[1;34m (S/N)\033[0m? ");
            Resp=Kbr.readLine().toUpperCase();
            if (!Resp.equals("S")&!Resp.equals("N")) continue;
            if (Resp.equals("N")) return;
            if (Resp.equals("S")){
                int tE=E, tC=C, tPos=Pos, tMax=Max, tG=G, tS=size;
                while (true){
                    so.print("Opcion No:       ");
                    int Opcion;
                    try{
                        Opcion=Integer.parseInt(Kbr.readLine());
                    }//endTry
                    catch (Exception e){
                        continue;							//No tecleó un dígito
                    }//endCatch
                    if (Opcion<1|Opcion>6)					//No está en rango
                        continue;
                    //endIf
                    so.print("Nuevo valor:     ");
                    iTmp=Integer.parseInt(Kbr.readLine());	//Parámetrp de CheckParams
                    boolean OK=CheckParams(Opcion);
                    if (!OK){
                        E=tE; C=tC; Pos=tPos; Max=tMax; G=tG; size=tS;
                        so.println("\033[0;35mError en la opcion # "+Opcion+"\033[0m");
                        continue;
                    }//endIf
                    break;
                }//endWhile
            }//endIf
        }//endWhile
    }
    //Lee el archivo con la cadena objetivo
    public static void readBin() throws Exception {
        Kbr = new BufferedReader(new InputStreamReader(System.in));
        String dir;
        boolean ascii;
        while (true){
            so.print("Archivo objetivo:     ");
            dir=Kbr.readLine();
            if(dir=="") continue;
            while(true) {
                so.print("¿Leer el documento como binario\033[1;34m (B)\033[0m o caracteres \033[1;34m(C)\033[0m?     ");
                Resp = Kbr.readLine().toUpperCase();
                if (!Resp.equals("B") & !Resp.equals("C")) continue;
                else ascii = Resp.equals("B");
                break;
            }
            try {
                if (ascii) objetivo = readFileAsBin(dir);
                else objetivo = readFile(dir);
                objetivo = limpiaString(objetivo);
                if (!resValido(objetivo)) {
                    so.println("\033[0;35mCadena no valida\033[0m");
                    objetivo = "";
                    continue;
                }
                break;
            } catch (Exception e) {
                so.println("\033[0;31mDirección no valida\033[0m");
                continue;
            }
        }
    }
    //Recibe un genoma y le cambia un bit aleatorio
    public static String Muta(String G) {
        // BIT A MUTAR
        int nBit=-1; while (nBit<0|nBit>=L) nBit=(int)(Math.random()*L);
        String mBit="0";	// Por default
        // 1) SI EL BIT ESTÁ EN UN LUGAR INTERMEDIO
        String genoma;
        while (true){
            if (nBit!=0&nBit!=L-1){
                if (G.substring(nBit,nBit+1).equals("0")) mBit="1";
                genoma=G.substring(0,nBit)+(mBit)+(G.substring(nBit+1));
                break;
            }//endif
            // 2) SI EL BIT ES EL PRIMERO
            if (nBit==0){
                if (G.substring(0,1).equals("0")) mBit="1";
                genoma=mBit+(G.substring(1));
                break;
            }//endif
            // 3) SI EL BIT ES EL ÚLTIMO
//	if (nBit==L-1){
            if (G.substring(L-1).equals("0")) mBit="1";
            genoma=G.substring(0,L-1)+(mBit);
            break;
            //}endif
        }//endWhile
        return genoma;
    }
    //Recibe el nombre de un archivo y regresa su contenido
    public static String readFile(String name) throws Exception {
        //Recibe la dirreccion de un archivo de texto y regresa el contenido como String
        File file = new File(name);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        String str = new String(data, "UTF-8");
        return str;
    }
    //Recibe el nmbre de un archivo y regresa su contenido como String binario
    public static String readFileAsBin(String name) throws Exception{
        String str = readFile(name);
        String res="";
        for(char x: str.toCharArray())
            res+=Integer.toBinaryString(x);
        return res;
    }
    //Checa que la cadena objetivo este en el formato correcto
    public static boolean resValido(String s) {
        //Revisa que el objetivo sea valida
        if (!s.matches("[01]+")) return false;
        return s!="";
    }
    //Elimina los \n y espacios de un String
    public static String limpiaString(String s) {//Quita todos los espacios y \n de un string
        s = s.replaceAll("\\s+", "");
        s = s.replaceAll("\n", "");
        return s;
    }
    /*
     * Cross Generational elitist selection, Heterogeneous recombination and
     *  Cataclysmic mutation GA (CHC)
     */
    public static void algGeneticoCHC() throws Exception{
        Random rn = new Random();
        ceros = new String(new char[C]).replace('\0', '0');
        poblacion = new Poblacion(size,L,objetivo,ceros,Pos,Max);
        mejor = poblacion.getFittest();
        //so.println("Generation: " + generationCount + " Fittest: " + mejor.getFitness()+" Size: "+mejor.getSize());
        for(int i=0; i<G; i++){
            poblacion=poblacion.nuevoCHC();
            mejor=poblacion.getFittest();
            //so.println("Generation: " + generationCount + " Fittest: " + mejor.getFitness()+ " Size: "+ mejor.getSize());
        }
    }
    /*
     * Version que saque yo, es una mezcla entre varios
     */
    public static void algGeneticoN(double crossRate, double mutRate) throws Exception{
        ceros = new String(new char[C]).replace('\0', '0');
        poblacion = new Poblacion(size,L,objetivo,ceros,Pos,Max);
        mejor = poblacion.getFittest();
        //so.println("Generation: " + generationCount + " Fittest: " + mejor.getFitness()+" Size: "+mejor.getSize());
        for(int i=0; i<G; i++){
            poblacion = poblacion.nuevoN(crossRate, mutRate);
            poblacion.add(mejor);
            mejor = poblacion.getFittest();
            //so.println("Generation: " + generationCount + " Fittest: " + mejor.getFitness()+ " Size: "+ mejor.getSize());
        }
    }
    /*
     * Elitist Canonical Genetic Algorithm (TGA)
     */
    public static void algGeneticoTGA(double crossRate, double mutRate) throws Exception{
        ceros = new String(new char[C]).replace('\0', '0');
        poblacion = new Poblacion(size,L,objetivo,ceros,Pos,Max);
        mejor = poblacion.getFittest();
        //so.println("Generation: " + generationCount + " Fittest: " + mejor.getFitness()+" Size: "+mejor.getSize());
        for(int i=0; i<G; i++){
            poblacion = poblacion.nuevoTGA(crossRate, mutRate);
            poblacion.add(mejor);
            mejor = poblacion.getFittest();
            //so.println("Generation: " + generationCount + " Fittest: " + mejor.getFitness()+ " Size: "+ mejor.getSize());
        }
    }
    /*
     * Hill climber
     */
    public static void hillClimber(){
        Individuo aux = new Individuo(L,objetivo,ceros,Pos,Max);
        mejor=aux;
        ceros = new String(new char[C]).replace('\0', '0');
        for(int i=0; i<G; i++){
            String gen=Muta(aux.getGenoma());
            aux=new Individuo(gen, L,objetivo,ceros,Pos,Max);
            if(aux.fitter(mejor))
                mejor=aux;
        }
    }

    //Deja elegir al usuario que algoritmo usar
    public static void escogeAlgoritmo() throws Exception{
        int Opcion;
        while (true){
            so.println("\033[1;34m1)\033[0m Algoritmo Hill Climber");
            so.println("\033[1;34m2)\033[0m AG otro");
            so.println("\033[1;34m3)\033[0m TGA");
            so.println("\033[1;34m4)\033[0m CHC (significativamente mas lento que los demas)");
            so.println("\033[1;34m5)\033[0m Correr todos\n");
            so.print("Opcion No:       ");
            try{
                Opcion=Integer.parseInt(Kbr.readLine());
            }//endTry
            catch (Exception e){
                continue;							//No tecleó un dígito
            }//endCatch
            if (Opcion<1|Opcion>5)					//No está en rango
                continue;
            //endIf
            break;
        }//endWhile
        switch (Opcion){
            case 1:
                hillClimber();
                reporte();
                break;
            case 2:
                algGeneticoN(crossRate, mutationRate);
                reporte();
                break;
            case 3:
                algGeneticoTGA(crossRate, mutationRate);
                reporte();
                break;
            case 4:
                algGeneticoCHC();
                reporte();
                break;
            case 5:
                hillClimber();
                so.println("El resultado del HILL CLIMBER:");
                reporte();
                algGeneticoN(crossRate, mutationRate);
                so.println("El resultado del AG comun:");
                reporte();
                algGeneticoTGA(crossRate, mutationRate);
                so.println("El resultado del TGA:");
                reporte();
                algGeneticoCHC();
                so.println("El resultado del CHC:");
                reporte();
                break;
        }
        return;
    }
    //Imprime el resultado de un algoritmo genetico
    public static void reporte() throws Exception{
        so.println("La mejor maquina fue:");
        Var = new Maquina(mejor.getGenoma());
        Var.trabajar(ceros, Pos, Max);
        Var.aplanar();
        Var.print();
        so.println("Su forma binaria es:\t" + "\033[0;35m"+Var.formaBinaria()+"\033[0m");
        so.println("\nSu cantidad de errores fue:   \033[0;35m"+mejor.getnCambios()+"\033[0m");
        so.println("Su tamaño es:                 \033[0;35m"+mejor.getSize()+"\033[0m");
        so.println("\nLa cadena objetivo era:     \033[0;35m"+objetivo+"\033[0m");
        so.print("El resultado fue:           ");
        for(int i=0; i<objetivo.length(); i++){
            if(mejor.getCinta().charAt(i)!=objetivo.charAt(i)) so.print("\033[0;31m");
                else so.print("\033[0;35m");
            so.print(mejor.getCinta().charAt(i));
        }
        so.println("\033[0m");
    }

    public static void main(String[] args) throws Exception {
        BufferedReader Fbr, Kbr;
        Kbr = new BufferedReader(new InputStreamReader(System.in));
        CreaParams();                            //Crea archivo si no existe
        GetParams();                            //Lee parametros de archivo
        while (true) {
            Modifica();                                //Modifica valores desde el escritorio
            ActualizaParams();                        //Graba en archivo
            readBin();
            so.println("Cadena objetivo:       \033[0;35m"+objetivo+"\033[0m");
            escogeAlgoritmo();
            so.println("\nOtra funcion \033[1;34m(S/N)\033[0m?");
            Resp = Kbr.readLine().toUpperCase();
            if (!Resp.equals("S")) {
                so.println("\033[0;31m\n*** FIN DE ESCALADOR ***\n");
                return;
            }//endIf
        }//endLoop
    }
}
