package com.company;
import java.math.*;
import java.util.*;

public class Poblacion {
    private Individuo[] individuos;
    private int minFitIndex = 0, min2FitIndex = 0, leastFitIndex = 0;
    private int L, Pos, Max;
    private String ceros, objetivo;
    private static Random random = new Random();

    /*
    * Metodos necesario para cualquiera de los tres metodos
    */
    //Constructor inicial
    public Poblacion(int size, int L, String objetivo, String ceros, int Pos, int Max) {
        this.L = L;
        this.Pos = Pos;
        this.Max = Max;
        this.ceros = ceros;
        this.objetivo = objetivo;
        individuos = new Individuo[size];
        for (int i = 0; i < individuos.length; i++) {
            individuos[i] = new Individuo(L, objetivo, ceros, Pos, Max);
        }
    }
    //Da el mejor
    public Individuo getFittest() throws Exception {
        double minFit = objetivo.length() + 1;
        minFitIndex = 0;
        double min2Fit = objetivo.length();
        min2FitIndex = 0;
        for (int i = 0; i < individuos.length; i++) {
            //System.out.println(i);
            if (minFit > individuos[i].getFitness()) {
                min2Fit = minFit;
                min2FitIndex = minFitIndex;
                minFit = individuos[i].getFitness();
                minFitIndex = i;
            } else if (min2Fit > individuos[i].getFitness() &&
                    (!individuos[i].equals(individuos[minFitIndex]) || i == 0)) {
                min2Fit = individuos[i].getFitness();
                min2FitIndex = i;
            }
        }
        return individuos[minFitIndex];
    }
    //Da el segundoMejor
    public Individuo getSecondFittest() {
        return individuos[min2FitIndex];
    }
    //Da el indice del peor
    private int getLeastFitIndex() {
        double maxFitVal = 0;
        leastFitIndex = 0;
        for (int i = 0; i < individuos.length; i++) {
            if (maxFitVal < individuos[i].getFitness()) {
                maxFitVal = individuos[i].getFitness();
                leastFitIndex = i;
            }
        }
        return leastFitIndex;
    }
    //Agrega un elemento quitando el peor
    public void add(Individuo ind) throws Exception {
        getFittest();
        int i = getLeastFitIndex();
        if (!ind.equals(individuos[minFitIndex])) individuos[i] = ind;
    }

    /*
     * Metodos estaticos auxiliare
     */
    private static Individuo randomSelect(HashSet<Individuo> conjunto) {
        int size = conjunto.size();
        //System.out.println(size);
        int item = random.nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (Individuo ind : conjunto) {
            if (i == item) return ind;
            i++;
        }
        return randomSelect(conjunto);
    }
    static private String replace(String str, int index, char replace) {
        if (str == null) {
            return str;
        } else if (index < 0 || index >= str.length()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }
    private static String mutate(String gen, double mutateRate){//Usado por TGA y N
        for(int i=0; i<gen.length(); i++)
            if(Math.random()<mutateRate)
                if(gen.charAt(i)=='0')  gen=replace(gen,i,'1');
                else                    gen=replace(gen,i,'0');
        return gen;
    }

    /*
     * Version que saque yo, es una mezcla entre varios
     */
    private Individuo crossover(Individuo ind1, Individuo ind2, double crossRate, double mutateRate){//Usado por N
        String genInd1=ind1.getGenoma();
        String genInd2=ind2.getGenoma();
        String genRes="";
        for(int i=0; i<L; i++)
            if (Math.random()<crossRate)    genRes+=genInd1.charAt(i);
            else                            genRes+=genInd2.charAt(i);
        genRes=mutate(genRes, mutateRate);
        return new Individuo(genRes, L, objetivo, ceros, Pos, Max);
    }
    private Poblacion(Individuo ind1, Individuo ind2, int size, int L, String objetivo, String ceros, int Pos, int Max, double crossRate, double mutateRate, int indRand){//Usado por N
        this.L = L;this.Pos=Pos;this.Max=Max;
        this.ceros=ceros;this.objetivo=objetivo;
        individuos =new Individuo[size];
        for (int i = 0; i < size-indRand; i++)
            individuos[i]=crossover(ind1,ind2, crossRate, mutateRate);
        for(int i=size-indRand; i<size; i++)
            individuos[i]=new Individuo(L, objetivo, ceros, Pos,Max);
    }
    public Poblacion nuevoN(double crossRate, double mutateRate) throws Exception{
        return new Poblacion(getFittest(), getSecondFittest(), individuos.length, L, objetivo, ceros, Pos, Max, crossRate, mutateRate, individuos.length / 5);
    }

    /*
     * Elitist Canonical Genetic Algorithm (TGA)
     */
    private Individuo[] crossover(double[] PS, double crossRate, double mutateRate){//Usado por TGA
        Individuo[] ind = new Individuo[individuos.length];
        for(int i=0; i<individuos.length; i+=2){
            Individuo ind1=select(PS), ind2=select(PS);
            String gen1=ind1.getGenoma(), gen2=ind2.getGenoma();
            if (Math.random()<crossRate){
                int locus = random.nextInt(L);
                String aux=gen1.substring(0,locus)+gen2.substring(locus,L);
                aux=mutate(aux, mutateRate);
                ind[i]=new Individuo(aux,L, objetivo,ceros,Pos,Max);
                if(i+1<individuos.length) {
                    aux=gen2.substring(0,locus)+gen1.substring(locus,L);
                    aux=mutate(aux, mutateRate);
                    ind[i + 1] = new Individuo(aux, L, objetivo, ceros, Pos, Max);
                }
            }else {
                ind[i]=individuos[i];
                ind[i+1]=individuos[i+1];
            }
        }
        return ind;
    }
    private Individuo select(double[] PS){//Usado por TGA
        double sel=Math.random();
        for(int i=0; i<individuos.length; i++){
            sel-=PS[i];
            if(sel<=0)
                return individuos[i];
        }
        return individuos[0];
    }
    private Poblacion(Individuo[] individuos, int L, String objetivo, String ceros, int Pos, int Max) {//Usado por TGA
        this.L = L;this.Pos=Pos;this.Max=Max;
        this.ceros=ceros;this.objetivo=objetivo;
        this.individuos =individuos;
    }
    public Poblacion nuevoTGA(double crossRate, double mutateRate) {
        double F=0;
        for(Individuo ind : individuos)
            F+=ind.getFitness();
        double[] PS = new double[individuos.length];
        for(int i=0; i<individuos.length; i++)
            PS[i]=individuos[i].getFitness()/F;
        return new Poblacion(crossover(PS,crossRate,mutateRate), L, objetivo, ceros, Pos, Max);
    }

    /*
     * Cross Generational elitist selection, Heterogeneous recombination and
     *  Cataclysmic mutation GA (CHC)
     */
    public HashSet<Individuo> toSet() {
        return new HashSet<Individuo>(Arrays.asList(individuos));
    }
    private Poblacion(HashSet<Individuo> conjunto, int size, int L, String objetivo, String ceros, int Pos, int Max) {//Usado por CHC
        this.L = L;
        this.Pos = Pos;
        this.Max = Max;
        this.ceros = ceros;
        this.objetivo = objetivo;
        individuos = new Individuo[size];
        int i = 0;
        TreeSet<Individuo> con = new TreeSet<Individuo>(conjunto);
        //System.out.print("En este conjunto hay: \t"+con.size());
        for (Individuo ind : con) {
            //ind.print();
            individuos[i] = ind;
            i++;
            if (i >= size) break;
        }
    }
    public Poblacion nuevoCHC() {
        HashSet<Individuo> conjunto = toSet();
        this.L = L;
        this.Pos = Pos;
        this.Max = Max;
        this.ceros = ceros;
        this.objetivo = objetivo;
        double threshold = ((double) L) / 4;
        Individuo ind1, ind2;
        String gen1, gen2;
        boolean[] diffxy = new boolean[L];
        for (int i = 0; i < L / 2; i++) {
            ind1 = randomSelect(conjunto);
            ind2 = randomSelect(conjunto);
            gen1 = ind1.getGenoma();
            gen2 = ind2.getGenoma();
            double hamming = 0;
            for (int j = 0; j < L; j++) {
                if (gen1.charAt(j) == gen2.charAt(j)) {
                    diffxy[j] = true;
                    hamming++;
                } else
                    diffxy[j] = false;
            }
            if (((double)hamming) / 2 >= threshold) {
                double mutated = 0;
                while (mutated < hamming / 2) {
                    int j = random.nextInt(L);
                    if (diffxy[j]) {
                        char temp = gen1.charAt(j);
                        gen1 = replace(gen1, j, gen2.charAt(j));
                        gen2 = replace(gen2, j, temp);
                        diffxy[j] = false;
                    }
                    mutated++;
                }
                conjunto.add(new Individuo(gen1, L, objetivo, ceros, Pos, Max));
                conjunto.add(new Individuo(gen2, L, objetivo, ceros, Pos, Max));
            }
            conjunto.remove(ind1);
            conjunto.remove(ind2);
        }
        conjunto.addAll(toSet());
        //System.out.println("conjunto.addAll(toSet())");
        return new Poblacion(conjunto, individuos.length, L, objetivo, ceros, Pos, Max);
    }
}
