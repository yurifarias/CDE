package com.example.yuri.cde.Estacas;

import com.example.yuri.cde.auxiliares.MainAuxiliar;

import Jama.Matrix;

public class SimetriaPorDoisPlanos extends MainAuxiliar {

    private double fx;
    private double fy;
    private double fz;
    private double fa;
    private double fb;
    private double fc;

    private Matrix tensorTransformacao;

    private Matrix matrizComponentesEstacasTransformados;
    private Matrix matrizRigidezTransformada;
    private Matrix matrizEsforcosTransformados;
    private Matrix matrizMovElasticoTransformado;

    private double xoYCL;
    private double xoZBL;

    public SimetriaPorDoisPlanos(double diametro, double comprimento, double mElasticidade,
                                 double fx, double fy, double fz, double fa, double fb, double fc,
                                 int nEstacas, double cota, Estaca[] estaqueamento) {

        super(diametro, comprimento, mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento);

        this.fx = fx;
        this.fy = fy;
        this.fz = fz;
        this.fa = fa;
        this.fb = fb;
        this.fc = fc;

        acharNovasCoordenadas();
        tensorTransformacao = montarTensorTransformacao();

        matrizComponentesEstacasTransformados = calcularComponentesEstacasTransformados();
        matrizRigidezTransformada = calcularRigidezTransformada();
        matrizEsforcosTransformados = calcularEsforcosTransformados();
        matrizMovElasticoTransformado = calcularMovElasticoTransformado();
    }

    /* Método para calcular os esforços normais de reação nas estacas: [N] = [v']transposta * [p'] */
    public Matrix calcularEsforcosNormais() {

        calcularMovimentoElastico();

        return (getMatrizRigidezEstacas().times(matrizComponentesEstacasTransformados.transpose())).times(matrizMovElasticoTransformado);
    }

    public double[][] calcularMovimentoElastico() {

        double[][] matriz = new double[6][1];

        matriz[0][0] = (tensorTransformacao.transpose()).times(matrizMovElasticoTransformado).getArray()[0][0];
        matriz[1][0] = (tensorTransformacao.transpose()).times(matrizMovElasticoTransformado).getArray()[1][0];
        matriz[2][0] = (tensorTransformacao.transpose()).times(matrizMovElasticoTransformado).getArray()[2][0];
        matriz[3][0] = (tensorTransformacao.transpose()).times(matrizMovElasticoTransformado).getArray()[3][0];
        matriz[4][0] = (tensorTransformacao.transpose()).times(matrizMovElasticoTransformado).getArray()[4][0];
        matriz[5][0] = (tensorTransformacao.transpose()).times(matrizMovElasticoTransformado).getArray()[5][0];

        return matriz;
    }

    /* Método para achar as novas posições do eixo x, em relação ao antigo, após a translação dos antigos
     eixos coordenados como aritifício para solucionar casos de estaqueamento simétrico por um plano. */
    private void acharNovasCoordenadas() {

        double[][] mS = getMatrizRigidez().getArray();

        xoYCL = mS[1][5] / mS[1][1];
        xoZBL = -mS[2][4] / mS[2][2];
    }

    /* Método para criar a matriz de trnasformação [T], ou tensor de transformação [T], referente aos
    casos de estaquemaneto plano em XY ou XZ retornando uma matriz 6 x 6 por apresentar degeneração */
    private Matrix montarTensorTransformacao() {

        double[][] tensorT = new double[6][6];

        tensorT[0][0] = 1;
        tensorT[0][1] = 0;
        tensorT[0][2] = 0;
        tensorT[0][3] = 0;
        tensorT[0][4] = 0;
        tensorT[0][5] = 0;

        tensorT[1][0] = 0;
        tensorT[1][1] = 1;
        tensorT[1][2] = 0;
        tensorT[1][3] = 0;
        tensorT[1][4] = 0;
        tensorT[1][5] = 0;

        tensorT[2][0] = 0;
        tensorT[2][1] = 0;
        tensorT[2][2] = 1;
        tensorT[2][3] = 0;
        tensorT[2][4] = 0;
        tensorT[2][5] = 0;

        tensorT[3][0] = 0;
        tensorT[3][1] = 0;
        tensorT[3][2] = 0;
        tensorT[3][3] = 1;
        tensorT[3][4] = 0;
        tensorT[3][5] = 0;

        tensorT[4][0] = 0;
        tensorT[4][1] = 0;
        tensorT[4][2] = xoZBL;
        tensorT[4][3] = 0;
        tensorT[4][4] = 1;
        tensorT[4][5] = 0;

        tensorT[5][0] = 0;
        tensorT[5][1] = -xoYCL;
        tensorT[5][2] = 0;
        tensorT[5][3] = 0;
        tensorT[5][4] = 0;
        tensorT[5][5] = 1;

        return new Matrix(tensorT);
    }

    /* Método para calcular a matriz de componentes de estaca transformados [p'], aplicando o tensor de
    transformação [T], retornando uma matriz 6 x n: [p'] = [T] * [p], por apresentar degeneração,
    onde n é número de estacas */
    private Matrix calcularComponentesEstacasTransformados() {

        return tensorTransformacao.times(getMatrizComponentesEstacas());
    }

    /* Método para calcular a matriz de rigidez transformada [S'], aplicando o tensor de transformação [T],
    retornando uma matriz 6 x 6, por apresentar degeneração: [S'] = [T] * [S] * [T]transposta */
    private Matrix calcularRigidezTransformada() {

        return tensorTransformacao.times(getMatrizRigidez().times(tensorTransformacao.transpose()));
    }

    /* Método para calcular a matriz de esforços transformados [F'], aplicando o tensor de transformação [T],
    retornando uma matraiz 6 x 1, por apresentar degeneração: [F'] = [T] * [F] */
    private Matrix calcularEsforcosTransformados() {

        double[][] esforcos = {{fx}, {fy}, {fz}, {fa}, {fb}, {fc}};

        return tensorTransformacao.times(new Matrix(esforcos));
    }

    /* Método para calcular a matriz do movimento elástico transformado do bloco [v'], após
    a transformação da matriz de rigidez [S'] e da matriz do esforços externos [F'],
    retornando uma matriz 6 x 1, por apresentar degeneração: [v'] = [S' ^ -1] * [F'] */
    private Matrix calcularMovElasticoTransformado() {

        double[][] vL = new double[6][1];

        if (matrizRigidezTransformada.getArray()[0][0] == 0) {

            vL[0][0] = 0;
        } else {

            vL[0][0] = matrizEsforcosTransformados.getArray()[0][0] / matrizRigidezTransformada.getArray()[0][0];
        }

        if (matrizRigidezTransformada.getArray()[1][1] == 0) {

            vL[1][0] = 0;
        } else {

            vL[1][0] = matrizEsforcosTransformados.getArray()[1][0] / matrizRigidezTransformada.getArray()[1][1];
        }

        if (matrizRigidezTransformada.getArray()[2][2] == 0) {

            vL[2][0] = 0;
        } else {

            vL[2][0] = matrizEsforcosTransformados.getArray()[2][0] / matrizRigidezTransformada.getArray()[2][2];
        }

        if (matrizRigidezTransformada.getArray()[3][3] == 0) {

            vL[3][0] = 0;
        } else {

            vL[3][0] = matrizEsforcosTransformados.getArray()[3][0] / matrizRigidezTransformada.getArray()[3][3];
        }

        if (matrizRigidezTransformada.getArray()[4][4] == 0) {

            vL[4][0] = 0;
        } else {

            vL[4][0] = matrizEsforcosTransformados.getArray()[4][0] / matrizRigidezTransformada.getArray()[4][4];
        }

        if (matrizRigidezTransformada.getArray()[5][5] == 0) {

            vL[5][0] = 0;
        } else {

            vL[5][0] = matrizEsforcosTransformados.getArray()[5][0] / matrizRigidezTransformada.getArray()[5][5];
        }

        return new Matrix(vL);
    }
}