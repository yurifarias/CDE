package com.example.yuri.cde.Estacas;

import com.example.yuri.cde.auxiliares.MainAuxiliar;

import Jama.Matrix;

public class EstaqueamentoPlano extends MainAuxiliar {

    private double fx;
    private double fy;
    private double fz;
    private double fb;
    private double fc;

    private int nEstacas;

    private Matrix matrizComponentesEstacasReduzida;
    private Matrix matrizRigidezReduzida;
    private Matrix matrizEsforcosExternos;

    private Matrix tensorTransformacao;

    private Matrix matrizComponentesEstacasRedTransformados;
    private Matrix matrizRigidezRedTransformada;
    private Matrix matrizEsforcosRedTransformados;
    private Matrix matrizMovElasticoRedTransformado;

    private double fi;
    private double xo;
    private double yo;
    private double zo;

    public EstaqueamentoPlano(double diametro, double comprimento, double mElasticidade,
                              double fx, double fy, double fz, double fa, double fb, double fc,
                              int nEstacas, double cota, Estaca[] estaqueamento, char caso) {

        super(diametro, comprimento, mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento);

        this.fx = fx;
        this.fy = fy;
        this.fz = fz;
        this.fb = fb;
        this.fc = fc;

        this.nEstacas = nEstacas;

        matrizComponentesEstacasReduzida = reduzirMatrizCompontesEstacas(caso);
        matrizRigidezReduzida = reduzirMatrizRigidez(caso);
        matrizEsforcosExternos = montarMatrizEsforcosReduzidos(caso);

        acharNovasCoordenadas(caso);
        tensorTransformacao = montarTensorTransformacao(caso);

        matrizComponentesEstacasRedTransformados = calcularComponentesEstacasTransformados();
        matrizRigidezRedTransformada = calcularRigidezTransformada();
        matrizEsforcosRedTransformados = calcularEsforcosTransformados();
        matrizMovElasticoRedTransformado = calcularMovElasticoTransformado();
    }


    /* Método para calcular os esforços normais de reação nas estacas: [N] = [v']transposta * [p'] */
    public Matrix calcularEsforcosNormais(char caso) {

        calcularMovElastico(caso);

        return (getMatrizRigidezEstacas().times(matrizComponentesEstacasRedTransformados.transpose())).times(matrizMovElasticoRedTransformado);
    }

    public double[][] calcularMovElastico(char caso) {

        double[][] matriz = new double[6][1];

        switch (caso) {
            case 'B':

                matriz[0][0] = (tensorTransformacao.transpose().times(matrizMovElasticoRedTransformado)).getArray()[0][0];
                matriz[1][0] = (tensorTransformacao.transpose().times(matrizMovElasticoRedTransformado)).getArray()[1][0];
                matriz[2][0] = 0;
                matriz[3][0] = 0;
                matriz[4][0] = 0;
                matriz[5][0] = (tensorTransformacao.transpose().times(matrizMovElasticoRedTransformado)).getArray()[2][0];

                break;

            case 'C':

                matriz[0][0] = (tensorTransformacao.transpose().times(matrizMovElasticoRedTransformado)).getArray()[0][0];
                matriz[1][0] = 0;
                matriz[2][0] = (tensorTransformacao.transpose().times(matrizMovElasticoRedTransformado)).getArray()[1][0];;
                matriz[3][0] = 0;
                matriz[4][0] = (tensorTransformacao.transpose().times(matrizMovElasticoRedTransformado)).getArray()[2][0];
                matriz[5][0] = 0;

                break;
        }

        return matriz;
    }

    /* Método chamado para reduzir a matriz das componentes das estacas */
    private Matrix reduzirMatrizCompontesEstacas(char caso) {

        double[][] matriz = new double[3][nEstacas];
        double[][] componentes = getMatrizComponentesEstacas().getArray();

        switch (caso) {

            case 'B':

                for (int i = 0; i < nEstacas; i++) {

                    matriz[0][i] = componentes[0][i];
                    matriz[1][i] = componentes[1][i];
                    matriz[2][i] = componentes[5][i];

                } break;

            case 'C':

                for (int i = 0; i < nEstacas; i++) {

                    matriz[0][i] = componentes[0][i];
                    matriz[1][i] = componentes[2][i];
                    matriz[2][i] = componentes[4][i];

                } break;
        }

        return new Matrix(matriz);
    }

    /* Método chamado para reduzir a matriz de rigidez do estaqueamento */
    private Matrix reduzirMatrizRigidez(char caso) {

        double[][] matrizReduzida = new double[3][3];

        switch (caso) {

            case 'B':

                matrizReduzida[0][0] = getMatrizRigidez().getArray()[0][0];
                matrizReduzida[0][1] = getMatrizRigidez().getArray()[0][1];
                matrizReduzida[0][2] = getMatrizRigidez().getArray()[0][5];

                matrizReduzida[1][0] = getMatrizRigidez().getArray()[1][0];
                matrizReduzida[1][1] = getMatrizRigidez().getArray()[1][1];
                matrizReduzida[1][2] = getMatrizRigidez().getArray()[1][5];

                matrizReduzida[2][0] = getMatrizRigidez().getArray()[5][0];
                matrizReduzida[2][1] = getMatrizRigidez().getArray()[5][1];
                matrizReduzida[2][2] = getMatrizRigidez().getArray()[5][5];

                break;

            case 'C':

                matrizReduzida[0][0] = getMatrizRigidez().getArray()[0][0];
                matrizReduzida[0][1] = getMatrizRigidez().getArray()[0][2];
                matrizReduzida[0][2] = getMatrizRigidez().getArray()[0][4];

                matrizReduzida[1][0] = getMatrizRigidez().getArray()[2][0];
                matrizReduzida[1][1] = getMatrizRigidez().getArray()[2][2];
                matrizReduzida[1][2] = getMatrizRigidez().getArray()[2][4];

                matrizReduzida[2][0] = getMatrizRigidez().getArray()[4][0];
                matrizReduzida[2][1] = getMatrizRigidez().getArray()[4][2];
                matrizReduzida[2][2] = getMatrizRigidez().getArray()[4][4];

                break;
        }

        return new Matrix(matrizReduzida);
    }

    /* Método chamado para reduzir a matriz de esforços externos */
    private Matrix montarMatrizEsforcosReduzidos(char caso) {

        double[][] matriz = new double[3][1];

        switch (caso) {

            case 'B':

                matriz[0][0] = fx;
                matriz[1][0] = fy;
                matriz[2][0] = fc;

                break;

            case 'C':

                matriz[0][0] = fx;
                matriz[1][0] = fz;
                matriz[2][0] = fb;

                break;
        }

        return new Matrix(matriz);
    }

    /* Método para achar as coordenadas (xo, yo) ou (xo, zo) dos novos eixos coordenados x', y' ou x', z'
    após a translação dos eixos originais x e y ou x e z, e o ângulo de rotação fi após a rotação em torno do
    eixo original z ou eixo original y, como aritifício para solucionar os casos de estaqueamentos planos. */
    private void acharNovasCoordenadas(char caso) {

        double[][] mS = matrizRigidezReduzida.getArray();

        if (mS[0][1] == 0) {

            fi = 0;

        } else {

            fi = (Math.atan((2 * mS[0][1]) / (mS[0][0] - mS[1][1]))) / 2;
        }

        switch (caso) {

            case 'B':

                xo = (mS[0][0] * mS[1][2] - mS[0][1] * mS[0][2]) / (mS[0][0] * mS[1][1] - Math.pow(mS[0][1], 2));
                yo = (mS[0][1] * mS[1][2] - mS[1][1] * mS[0][2]) / (mS[0][0] * mS[1][1] - Math.pow(mS[0][1], 2));

                break;

            case 'C':

                xo = (mS[0][1] * mS[0][2] - mS[0][0] * mS[1][2]) / (mS[0][0] * mS[1][1] - Math.pow(mS[0][1], 2));
                zo = (mS[1][1] * mS[0][2] - mS[0][1] * mS[1][2]) / (mS[0][0] * mS[1][1] - Math.pow(mS[0][1], 2));

                break;
        }
    }

    /* Método para criar a matriz de trnasformação [T], ou tensor de transformação [T], referente aos
    casos de estaquemaneto plano em XY ou XZ */
    private Matrix montarTensorTransformacao(char caso) {

        double[][] tensorT = new double[3][3];

        switch (caso) {

            case 'B':

                tensorT[0][0] = Math.cos(fi);
                tensorT[0][1] = Math.sin(fi);
                tensorT[0][2] = 0;

                tensorT[1][0] = -Math.sin(fi);
                tensorT[1][1] = Math.cos(fi);
                tensorT[1][2] = 0;

                tensorT[2][0] = yo;;
                tensorT[2][1] = -xo;
                tensorT[2][2] = 1;

                break;

            case 'C':

                tensorT[0][0] = Math.cos(fi);
                tensorT[0][1] = Math.sin(fi);
                tensorT[0][2] = 0;

                tensorT[1][0] = -Math.sin(fi);
                tensorT[1][1] = Math.cos(fi);
                tensorT[1][2] = 0;

                tensorT[2][0] = -zo;;
                tensorT[2][1] = xo;
                tensorT[2][2] = 1;

                break;
        }

        return new Matrix(tensorT);
    }

    /* Método para calcular a matriz de componentes de estaca transformados [p'], aplicando o tensor de
    transformação [T], retornando uma matriz 3 x n: [p'] = [T] * [p],
    onde n é número de estacas */
    private Matrix calcularComponentesEstacasTransformados() {

        return tensorTransformacao.times(matrizComponentesEstacasReduzida);
    }

    /* Método para calcular a matriz de rigidez transformada [S'], aplicando o tensor de transformação [T],
    retornando uma matriz 3 x 3, por apresentar degeneração: [S'] = [T] * [S] * [T]transposta */
    private Matrix calcularRigidezTransformada() {

        return tensorTransformacao.times(matrizRigidezReduzida.times(tensorTransformacao.transpose()));
    }

    /* Método para calcular a matriz de esforços transformados [F'], aplicando o tensor de transformação [T],
    retornando uma matraiz 3 x 1, por apresentar degeneração: [F'] = [T] * [F] */
    private Matrix calcularEsforcosTransformados() {

        return tensorTransformacao.times(matrizEsforcosExternos);
    }

    /* Método para calcular a matriz do movimento elástico transformado do bloco [v'], após
    a transformação da matriz de rigidez [S'] e da matriz do esforços externos [F'],
    retornando uma matriz 3 x 1, por apresentar degeneração: [v'] = [S' ^ -1] * [F'] */
    private Matrix calcularMovElasticoTransformado() {

        double[][] matriz = new double[3][1];

        if (matrizRigidezRedTransformada.getArray()[0][0] == 0) {

            matriz[0][0] = 0;

        } else {

            matriz[0][0] = matrizEsforcosRedTransformados.getArray()[0][0] / matrizRigidezRedTransformada.getArray()[0][0];
        }

        if (matrizRigidezRedTransformada.getArray()[1][1] == 0) {

            matriz[1][0] = 0;

        } else {

            matriz[1][0] = matrizEsforcosRedTransformados.getArray()[1][0] / matrizRigidezRedTransformada.getArray()[1][1];
        }

        if (matrizRigidezRedTransformada.getArray()[2][2] == 0) {

            matriz[2][0] = 0;

        } else {

            matriz[2][0] = matrizEsforcosRedTransformados.getArray()[2][0] / matrizRigidezRedTransformada.getArray()[2][2];
        }

        return new Matrix(matriz);
    }

    public Matrix getMatrizComponentesEstacasReduzida() {
        return matrizComponentesEstacasReduzida;
    }

    public Matrix getMatrizRigidezReduzida() {
        return matrizRigidezReduzida;
    }

    public Matrix getMatrizEsforcosExternos() {
        return matrizEsforcosExternos;
    }

    public Matrix getTensorTransformacao() {
        return tensorTransformacao;
    }

    public Matrix getMatrizComponentesEstacasRedTransformados() {
        return matrizComponentesEstacasRedTransformados;
    }

    public Matrix getMatrizRigidezRedTransformada() {
        return matrizRigidezRedTransformada;
    }

    public Matrix getMatrizEsforcosRedTransformados() {
        return matrizEsforcosRedTransformados;
    }

    public Matrix getMatrizMovElasticoRedTransformado() {
        return matrizMovElasticoRedTransformado;
    }
}
