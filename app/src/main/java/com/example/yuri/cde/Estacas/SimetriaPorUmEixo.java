package com.example.yuri.cde.Estacas;

import com.example.yuri.cde.auxiliares.MainAuxiliar;

import Jama.Matrix;

public class SimetriaPorUmEixo extends MainAuxiliar {

    private double fx;
    private double fa;

    private int nEstacas;

    private Matrix matrizComponentesEstacasSubdividida;
    private Matrix matrizRigidezSubdividida;
    private Matrix matrizEsforcosExternosSubdividida;

    public SimetriaPorUmEixo(double diametro, double comprimento, double mElasticidade,
                             double fx, double fy, double fz, double fa, double fb, double fc,
                             int nEstacas, double cota, Estaca[] estaqueamento) {

        super(diametro, comprimento, mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento);

        this.fx = fx;
        this.fa = fa;

        this.nEstacas = nEstacas;

        matrizComponentesEstacasSubdividida = subdividirMatrizCompontesEstacas();
        matrizRigidezSubdividida = subdividirMatrizRigidez();
        matrizEsforcosExternosSubdividida = subdividirMatrizEsforcos();
    }

    /* Método para calcular as matrizes do movimento elástico transformado do bloco [v'], após a transformação
    da matriz de rigidez [S''] e da matriz do esforços externos [F''],
    retornando três matrizes 2 x 1: [v''] = [S'' ^ -1] * [F''] */
    public double[][] calcularMovElastico() {

        double[][] matriz = new double[6][1];
        double[][] matriz1 = {{0}, {0}};

        Matrix vxa = new Matrix(matriz1);

        try {

            vxa = matrizRigidezSubdividida.solve(matrizEsforcosExternosSubdividida);

        } catch (Exception e) {

            // mandar mensagem sobre hiperestaticidade
        }

        matriz[0][0] = vxa.getArray()[0][0];
        matriz[1][0] = 0;
        matriz[2][0] = 0;
        matriz[3][0] = vxa.getArray()[1][0];
        matriz[4][0] = 0;
        matriz[5][0] = 0;

        return matriz;
    }

    /* Método para calcular os esforços normais de reação nas estacas: [N] = [v'']transposta * [p''] */
    public Matrix calcularEsforcosNormais() {

        return (getMatrizRigidezEstacas().times(matrizComponentesEstacasSubdividida.transpose())).times(new Matrix(calcularMovElastico()));
    }

    /* Método chamado para subdividir a matriz das componentes das estacas */
    private Matrix subdividirMatrizCompontesEstacas() {

        double[][] matriz1 = new double[2][nEstacas];
        double[][] componentes = getMatrizComponentesEstacas().getArray();

        for (int i = 0; i < nEstacas; i++) {

            // Matriz de componentes de estacas XA
            matriz1[0][i] = componentes[0][i];
            matriz1[1][i] = componentes[3][i];
        }

        // [p]xa
        return new Matrix(matriz1);
    }

    /* Método chamado para subdividir a matriz de rigidez do estaqueamento */
    private Matrix subdividirMatrizRigidez() {

        double[][] matriz1 = new double[2][2];

        // Matriz de rigidez XA
        matriz1[0][0] = getMatrizRigidez().getArray()[0][0];
        matriz1[0][1] = getMatrizRigidez().getArray()[0][3];

        matriz1[1][0] = getMatrizRigidez().getArray()[3][0];
        matriz1[1][1] = getMatrizRigidez().getArray()[3][3];

        // [S]xa
        return  new Matrix(matriz1);
    }

    /* Método chamado para reduzir a matriz de esforços externos */
    private Matrix subdividirMatrizEsforcos() {

        double[][] matriz1 = new double[2][1];

        // Matriz dos esforços XA
        matriz1[0][0] = fx;
        matriz1[1][0] = fa;

        // [F]xa
        return new Matrix(matriz1);
    }
}