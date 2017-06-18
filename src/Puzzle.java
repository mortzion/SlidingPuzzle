
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matheus Prachedes Batista
 */
public class Puzzle {
    public int vazio;
    private int[][] puzzle;
    private int posBrancoI;
    private int posBrancoJ;
    
    public Puzzle(int tam){
        init(tam);
    }
    
    public void init(int tam){
        puzzle  = new int[tam][tam];
        for(int i=0;i<tam;i++){
            for(int j=0;j<tam;j++){
                puzzle[i][j] = i*tam+j;
            }
        }
        vazio = tam*tam - 1;
        posBrancoI = tam-1;
        posBrancoJ = posBrancoI;
    }
    
    public int rand(){
        int i=0;
        Random r = new Random();
        while(!completo()){
            rand(r);
            i++;
        }
        return i;
    }
    
    public void embaralhar(int numMov){
        Random r = new Random();
        for(int i=0;i<numMov;i++){
            rand(r);
        }
    }
    
    public boolean posValida(int i,int j){
        if(i<0 || i>=puzzle.length)return false;
        if(j<0 || j>=puzzle.length)return false;
        return true;
    }
    
    public void swap(int i,int j){
        puzzle[posBrancoI][posBrancoJ] = puzzle[i][j];
        puzzle[i][j] = vazio;
        posBrancoI = i;
        posBrancoJ = j;
    }
    
    public void rand(Random rand){
        int movimento = rand.nextInt(4);
        int[] movimentosI = new int[]{-1,1,0,0};
        int[] movimentosJ = new int[]{0,0,-1,1};
        if(posValida(posBrancoI + movimentosI[movimento], posBrancoJ+movimentosJ[movimento])){
            swap(posBrancoI + movimentosI[movimento], posBrancoJ+movimentosJ[movimento]);
        }
        else rand(rand);
    }
    
    public int heuristica1(){
        HashNode estado = new HashNode(this.puzzle,dist1());
        int[] movimentosI = new int[]{-1,1,0,0};
        int[] movimentosJ = new int[]{0,0,-1,1};
        int numMovimentos = -1;
        HashMap<HashNode,Integer> visitados = new HashMap<>();
        TreeMap<HashNode,Integer> listaParaVisitar = new TreeMap<>();
        listaParaVisitar.put(estado, 0);
        while(!listaParaVisitar.isEmpty()){
            estado = listaParaVisitar.firstKey();
            numMovimentos = listaParaVisitar.remove(estado);
            if(visitados.containsKey(estado))continue;
            if(completo())break;
            visitados.put(estado, numMovimentos);
            puzzle = estado.estado;
            atualizaPosBranco();
            for(int i=0;i<4;i++){
                if(posValida(posBrancoI + movimentosI[i],posBrancoJ + movimentosJ[i])){
                    swap(posBrancoI+movimentosI[i],posBrancoJ+movimentosJ[i]);
                    int distancia = dist1();
                    listaParaVisitar.put(new HashNode(this.puzzle,dist1()+numMovimentos+1),numMovimentos+1);
                    swap(posBrancoI-movimentosI[i],posBrancoJ-movimentosJ[i]);
                }
            }
        }
        if(completo())return numMovimentos;
        else return -1;
    }
    
    public int heuristica2(){
        HashNode estado = new HashNode(this.puzzle,dist1());
        int[] movimentosI = new int[]{-1,1,0,0};
        int[] movimentosJ = new int[]{0,0,-1,1};
        int numMovimentos = -1,menorDistancia;
        HashMap<HashNode,Integer> visitados = new HashMap<>();
        TreeMap<HashNode,Integer> listaParaVisitar = new TreeMap<>();
        
        listaParaVisitar.put(estado, 0);
        while(!listaParaVisitar.isEmpty()){
            estado = listaParaVisitar.firstKey();
            numMovimentos = listaParaVisitar.remove(estado);
            if(visitados.containsKey(estado))continue;
            if(completo())break;
            visitados.put(estado, numMovimentos);
            puzzle = estado.estado;
            atualizaPosBranco();
            
            for(int i=0;i<4;i++){
                if(posValida(posBrancoI + movimentosI[i],posBrancoJ + movimentosJ[i])){
                    swap(posBrancoI + movimentosI[i],posBrancoJ + movimentosJ[i]);
                    menorDistancia = Integer.MAX_VALUE;
                    for(int j=0;j<4;j++){
                        if(posValida(posBrancoI + movimentosI[j],posBrancoJ + movimentosJ[j])){
                            swap(posBrancoI + movimentosI[j],posBrancoJ + movimentosJ[j]);
                            int distanciaAtual = dist1();
                            if(distanciaAtual<menorDistancia)menorDistancia = distanciaAtual;
                            swap(posBrancoI - movimentosI[j],posBrancoJ - movimentosJ[j]);
                        }
                    }
                    if(completo())menorDistancia = 0;
                    listaParaVisitar.put(new HashNode(puzzle,menorDistancia+numMovimentos+1), numMovimentos+1);
                    swap(posBrancoI - movimentosI[i],posBrancoJ - movimentosJ[i]);
                }
            }
        }
        if(completo())return numMovimentos;
        else return -1;
    }
    
    //Distancia de Manhathan
    public int dist1(){
        int soma=0;
        int peça;
        for(int i=0;i<puzzle.length;i++){
            for(int j=0;j<puzzle.length;j++){
                peça = puzzle[i][j];
                int posJ = peça%puzzle.length;
                int posI = peça/ puzzle.length;
                soma+= Math.abs(i-posI) + Math.abs(j-posJ);
            }   
        }
        return soma;
    }
    
    public boolean completo(){
        for(int i=0;i<puzzle.length;i++){
            for(int j=0;j<puzzle.length;j++){
                if(puzzle[i][j]!=i*puzzle.length + j)return false;
            }
        }
        return true;
    }
    
    public int[][] getPuzzle(){
        int[][] ret = new int[puzzle.length][puzzle.length];
        for(int i=0;i<puzzle.length;i++){
            for(int j=0;j<puzzle.length;j++){
                ret[i][j] = puzzle[i][j];
            }
        }
        
        return ret;
    }
  
    public String toString(){
        String s = "";
        for(int i=0;i<puzzle.length;i++){
            for(int j=0;j<puzzle.length;j++){
                if(puzzle[i][j] == vazio)s+="  ";
                else s+=puzzle[i][j] + " ";
            }
            s+="\n";
        }
        return s;
    }
    
    public static void main(String[] args) {
        Puzzle p = new Puzzle(3);
        int it = 10000;
        int resultado = 0;
        for(int i=0;i<it;i++){
            p.embaralhar(50);
            resultado+=(p.heuristica2());
        }
        System.out.println(resultado/(float)it);
    }

    private void atualizaPosBranco() {
        for(int i=0;i<puzzle.length;i++){
            for(int j=0;j<puzzle.length;j++){
                if(puzzle[i][j] == vazio){
                    posBrancoI = i;
                    posBrancoJ = j;
                    return;
                }
            }
        }
    }
    
    private class HashNode implements Comparable<HashNode>{
        private int[][] estado;
        private int distancia;
        
        public HashNode(int[][] estado, int distancia){
            this.estado = new int[estado.length][estado.length];
            for(int i=0;i<estado.length;i++){
                for(int j=0;j<estado.length;j++){
                    this.estado[i][j] = estado[i][j];
                }
            }
            this.distancia = distancia;
        }

        @Override
        public int compareTo(HashNode o) {
            if(distancia<o.distancia)return -1;
            else if(distancia>o.distancia)return 1;
            else if(equals(o))return 0;
            return -1;
        }   

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof HashNode){
                HashNode o = (HashNode)obj;
                for(int i=0;i<estado.length;i++){
                    for(int j=0;j<estado.length;j++){
                        if(estado[i][j] != o.estado[i][j])return false;
                    }
                }
                return true;
            }
            else return false;
        }
        
        @Override
        public int hashCode() {
            return Arrays.deepHashCode(estado);
        }
        
    }
}
