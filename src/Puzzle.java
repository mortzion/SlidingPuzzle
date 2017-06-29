
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
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
    private Random r = new Random();

    public Puzzle(int tam) {
        init(tam);
    }

    public void init(int tam) {
        puzzle = new int[tam][tam];
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                puzzle[i][j] = i * tam + j;
            }
        }
        // puzzle = new int[][]{{1,5,4},{0,2,3},{8,7,6}};
        vazio = tam * tam - 1;
        posBrancoI = tam - 1;
        posBrancoJ = posBrancoI;
        //posBrancoI = 2;
        //posBrancoJ = 0;
    }

    public Queue<Integer> rand() {
        Queue<Integer> sequenciaMovimentos = new LinkedList<>();
        while (!completo()) {
            sequenciaMovimentos.add(rand(r));
        }
        return sequenciaMovimentos;
    }

    public int[][] getPuzzle() {
        return puzzle;
    }

    public void embaralhar(int numMov) {
        for (int i = 0; i < numMov; i++) {
            rand(r);
        }
    }

    public boolean posValida(int i, int j) {
        if (i < 0 || i >= puzzle.length) {
            return false;
        }
        if (j < 0 || j >= puzzle.length) {
            return false;
        }
        return true;
    }

    public int getPosBrancoI() {
        return posBrancoI;
    }

    public void setPosBrancoI(int posBrancoI) {
        this.posBrancoI = posBrancoI;
    }

    public int getPosBrancoJ() {
        return posBrancoJ;
    }

    public void setPosBrancoJ(int posBrancoJ) {
        this.posBrancoJ = posBrancoJ;
    }

    public void swap(int i, int j) {
        puzzle[posBrancoI][posBrancoJ] = puzzle[i][j];
        puzzle[i][j] = vazio;
        posBrancoI = i;
        posBrancoJ = j;
    }

    public int rand(Random rand) {
        int movimento = rand.nextInt(4);
        int[] movimentosI = new int[]{-1, 1, 0, 0};
        int[] movimentosJ = new int[]{0, 0, -1, 1};
        if (posValida(posBrancoI + movimentosI[movimento], posBrancoJ + movimentosJ[movimento])) {
            swap(posBrancoI + movimentosI[movimento], posBrancoJ + movimentosJ[movimento]);
            return movimento;
        } else {
            return rand(rand);
        }
    }

    public HashNode heuristica1() {
        HashNode estado = new HashNode(this.puzzle, dist1(), 0, null);
        int[] movimentosI = new int[]{-1, 1, 0, 0};
        int[] movimentosJ = new int[]{0, 0, -1, 1};
        int numMovimentos = -1;
        HashMap<HashNode, Integer> visitados = new HashMap<>();
        PriorityQueue<HashNode> listaParaVisitar = new PriorityQueue<>(14 * 4);
        listaParaVisitar.add(estado);
        while (!listaParaVisitar.isEmpty()) {
            estado = listaParaVisitar.remove();
            numMovimentos = estado.numMovimentos;
            if (visitados.containsKey(estado)) {
                continue;
            }
            visitados.put(estado, numMovimentos);
            puzzle = estado.estado;
            atualizaPosBranco();
            if (completo()) {
                break;
            }
            //Testa os 4 movimentos
            for (int i = 0; i < 4; i++) {
                //Se o movimento for valido, insere na lista para visitar
                if (posValida(posBrancoI + movimentosI[i], posBrancoJ + movimentosJ[i])) {
                    swap(posBrancoI + movimentosI[i], posBrancoJ + movimentosJ[i]);
                    int distancia = dist1();
                    listaParaVisitar.add(new HashNode(clonePuzzle(), dist1() + numMovimentos + 1, numMovimentos + 1, estado));
                    swap(posBrancoI - movimentosI[i], posBrancoJ - movimentosJ[i]);
                }
            }
        }
        if (completo()) {
            return estado;
        } else {
            return null;
        }
    }

    public HashNode heuristica2() {
        HashNode estado = new HashNode(this.puzzle, dist1(), 0, null);
        int[] movimentosI = new int[]{-1, 1, 0, 0};
        int[] movimentosJ = new int[]{0, 0, -1, 1};
        int numMovimentos = -1, menorDistancia;
        HashMap<HashNode, Integer> visitados = new HashMap<>();
        PriorityQueue<HashNode> listaParaVisitar = new PriorityQueue<>(14 * 4);
        listaParaVisitar.add(estado);
        while (!listaParaVisitar.isEmpty()) {
            estado = listaParaVisitar.remove();
            numMovimentos = estado.numMovimentos;
            if (visitados.containsKey(estado)) {
                continue;
            }
            visitados.put(estado, numMovimentos);
            puzzle = estado.estado;
            atualizaPosBranco();
            if (completo()) {
                break;
            }
            for (int i = 0; i < 4; i++) {
                if (posValida(posBrancoI + movimentosI[i], posBrancoJ + movimentosJ[i])) {
                    swap(posBrancoI + movimentosI[i], posBrancoJ + movimentosJ[i]);
                    menorDistancia = nivelHeuristica(1);
                    if (completo()) {
                        menorDistancia = 0;
                    }
                    listaParaVisitar.add(new HashNode(clonePuzzle(), dist1() + numMovimentos + 1, numMovimentos + 1, estado));
                    swap(posBrancoI - movimentosI[i], posBrancoJ - movimentosJ[i]);
                }
            }
        }
        if (completo()) {
            return estado;
        } else {
            return null;
        }
    }

    public int nivelHeuristica(int altura) {
        if(altura == 0)return dist1();
        int[] movimentosI = new int[]{-1, 1, 0, 0};
        int[] movimentosJ = new int[]{0, 0, -1, 1};
        int menorDistancia = Integer.MAX_VALUE;
        int distanciaAtual;
        for (int j = 0; j < 4; j++) {
            if (posValida(posBrancoI + movimentosI[j], posBrancoJ + movimentosJ[j])) {
                swap(posBrancoI + movimentosI[j], posBrancoJ + movimentosJ[j]);
                if(altura==1)distanciaAtual = dist1();
                else distanciaAtual = nivelHeuristica(altura-1);
                if (distanciaAtual < menorDistancia) {
                    menorDistancia = distanciaAtual;
                }
                swap(posBrancoI - movimentosI[j], posBrancoJ - movimentosJ[j]);
                if(completo())return 0;
            }
        }
        return menorDistancia;
    }

    //Distancia de Manhathan
    public int dist1() {
        int soma = 0;
        int peça;
        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle.length; j++) {
                peça = puzzle[i][j];
                int posJ = peça % puzzle.length;
                int posI = peça / puzzle.length;
                soma += Math.abs(i - posI) + Math.abs(j - posJ);
            }
        }
        return soma;
    }

    public boolean completo() {
        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle.length; j++) {
                if (puzzle[i][j] != i * puzzle.length + j) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] clonePuzzle() {
        int[][] ret = new int[puzzle.length][puzzle.length];
        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle.length; j++) {
                ret[i][j] = puzzle[i][j];
            }
        }

        return ret;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle.length; j++) {
                if (puzzle[i][j] == vazio) {
                    s += "  ";
                } else {
                    s += puzzle[i][j] + " ";
                }
            }
            s += "\n";
        }
        return s;
    }

    public void setPuzzle(int[][] puzzle) {
        this.puzzle = puzzle;
    }

    public void atualizaPosBranco() {
        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle.length; j++) {
                if (puzzle[i][j] == vazio) {
                    posBrancoI = i;
                    posBrancoJ = j;
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        Puzzle p = new Puzzle(3);
        long cont = 0;
        for (int i = 0; i < 1000; i++) {
            p.embaralhar(100);
            cont += p.heuristica3().numMovimentos;
        }
        System.out.println(cont / 1000f);
    }

    public HashNode heuristica3() {
        HashNode estado = new HashNode(this.puzzle, dist1(), 0, null);
        int[] movimentosI = new int[]{-1, 1, 0, 0};
        int[] movimentosJ = new int[]{0, 0, -1, 1};
        int numMovimentos = -1, menorDistancia;
        HashMap<HashNode, Integer> visitados = new HashMap<>();
        PriorityQueue<HashNode> listaParaVisitar = new PriorityQueue<>(14 * 4);
        listaParaVisitar.add(estado);
        while (!listaParaVisitar.isEmpty()) {
            estado = listaParaVisitar.remove();
            numMovimentos = estado.numMovimentos;
            if (visitados.containsKey(estado)) {
                continue;
            }
            visitados.put(estado, numMovimentos);
            puzzle = estado.estado;
            atualizaPosBranco();
            if (completo()) {
                break;
            }
            for (int i = 0; i < 4; i++) {
                if (posValida(posBrancoI + movimentosI[i], posBrancoJ + movimentosJ[i])) {
                    swap(posBrancoI + movimentosI[i], posBrancoJ + movimentosJ[i]);
                    menorDistancia = nivelHeuristica(2);
                    if (completo()) {
                        menorDistancia = 0;
                    }
                    listaParaVisitar.add(new HashNode(clonePuzzle(), dist1() + numMovimentos + 1, numMovimentos + 1, estado));
                    swap(posBrancoI - movimentosI[i], posBrancoJ - movimentosJ[i]);
                }
            }
        }
        if (completo()) {
            return estado;
        } else {
            return null;
        }
    }

    public class HashNode implements Comparable<HashNode> {

        private int[][] estado;
        private int distancia;
        private int numMovimentos;
        private HashNode pai;

        public HashNode(int[][] estado, int distancia, int numMovimentos, HashNode pai) {
            this.estado = new int[estado.length][estado.length];
            for (int i = 0; i < estado.length; i++) {
                for (int j = 0; j < estado.length; j++) {
                    this.estado[i][j] = estado[i][j];
                }
            }
            this.distancia = distancia;
            this.numMovimentos = numMovimentos;
            this.pai = pai;
        }

        @Override
        public int compareTo(HashNode o) {
            int diferença = Integer.compare(distancia, o.distancia);
            if (diferença == 0) {
                diferença = Integer.compare(numMovimentos, numMovimentos);
            }
            if (diferença != 0) {
                return diferença;
            }
            if (equals(o)) {
                return 0;
            }
            return -1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HashNode) {
                HashNode o = (HashNode) obj;
                for (int i = 0; i < estado.length; i++) {
                    for (int j = 0; j < estado.length; j++) {
                        if (estado[i][j] != o.estado[i][j]) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(estado);
        }

        public int[][] getEstado() {
            return estado;
        }

        public void setEstado(int[][] estado) {
            this.estado = estado;
        }

        public int getDistancia() {
            return distancia;
        }

        public void setDistancia(int distancia) {
            this.distancia = distancia;
        }

        public int getNumMovimentos() {
            return numMovimentos;
        }

        public void setNumMovimentos(int numMovimentos) {
            this.numMovimentos = numMovimentos;
        }

        public HashNode getPai() {
            return pai;
        }

        public void setPai(HashNode pai) {
            this.pai = pai;
        }

    }
}
