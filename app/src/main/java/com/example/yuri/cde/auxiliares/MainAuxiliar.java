package com.example.yuri.cde.auxiliares;

import com.example.yuri.cde.Estacas.Estaca;
import com.example.yuri.cde.Estacas.EstacasVerticais;
import com.example.yuri.cde.Estacas.EstaqueamentoPlano;
import com.example.yuri.cde.Estacas.SimetriaPorDoisPlanos;
import com.example.yuri.cde.Estacas.SimetriaPorUmEixo;
import com.example.yuri.cde.Estacas.SimetriaPorUmPlano;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import Jama.Matrix;

public class MainAuxiliar {

    private double diametro;
    private double comprimento;
    private double mElasticidade;

    private double fx;
    private double fy;
    private double fz;
    private double fa;
    private double fb;
    private double fc;

    private int nEstacas;
    private double cota;
    private Estaca[] estaqueamento;

    private ArrayList<String> eQI = new ArrayList<>();
    private ArrayList<String> eQII = new ArrayList<>();
    private ArrayList<String> eQIII = new ArrayList<>();
    private ArrayList<String> eQIV = new ArrayList<>();
    private ArrayList<String> eEI = new ArrayList<>();
    private ArrayList<String> eEII = new ArrayList<>();
    private ArrayList<String> eEIII = new ArrayList<>();
    private ArrayList<String> eEIV = new ArrayList<>();
    private ArrayList<String> eZero = new ArrayList<>();
    private ArrayList<String> ePlanoXY = new ArrayList<>();
    private ArrayList<String> ePlanoXZ = new ArrayList<>();

    private String estacaSimetricaQI;
    private String estacaSimetricaQII;
    private String estacaSimetricaQIII;
    private String estacaSimetricaQIV;
    private String estacaSimetricaEI;
    private String estacaSimetricaEII;
    private String estacaSimetricaEIII;
    private String estacaSimetricaEIV;

    private double alturaTetraedro;

    private Matrix matrizRigidezEstacas;
    private Matrix matrizComponentesEstacas;
    private Matrix matrizRigidez;

    private char casoSimetria;

    private boolean testeValidade;
    private String titulo;
    private String mensagem;

    public static double[][] deslocamentoElastico;
    public static Matrix reacoesNormais;

    public MainAuxiliar(double diametro, double comprimento, double mElasticidade,
                        double fx, double fy, double fz, double fa, double fb, double fc,
                        int nEstacas, double cota, Estaca[] estaqueamento) {

        this.diametro = diametro;
        this.comprimento = comprimento;
        this.mElasticidade = mElasticidade;

        this.fx = fx;
        this.fy = fy;
        this.fz = fz;
        this.fa = fa;
        this.fb = fb;
        this.fc = fc;

        this.nEstacas = nEstacas;
        this.cota = cota;
        this.estaqueamento = estaqueamento;

        matrizRigidezEstacas = montarMatrizRigidezEstacas();
        matrizComponentesEstacas = montaMatrizComponentesEstacas();
        matrizRigidez = calcularMatrizRigidez();

        casoSimetria = definirCaso();

    }

    public void checarValidade() {

        switch (getCasoSimetria()) {

            // ESTACAS VERTICAIS
            case 'A':

                titulo = "INSTABILIDADE!";

                if (fy != 0 || fz != 0 || fa != 0) {

                    //Passar mensagem de que o estaqueamento não reage a esforços Fy, Fz ou Fa.
                    mensagem = "O estaqueamento não reage aos esforços Fy, Fz, Fa.Por favor, reconsidere estes esforços.";

                    testeValidade = false;

                } else if (nEstacas == 2) {

                    if (!testePlanoXY() && !testePlanoXZ()) {

                        //Passar mensagem de que duas estacas são instáveis caso não formem estaqueamento plano em XY ou XZ.
                        mensagem = "Estaqueamento com apenas duas estacas que não estão contidas no plano XY ou plano XZ é instável.";

                        testeValidade = false;

                    } else if (testePlanoXY() && fb != 0) {

                        //Passar mensagem de instabilidade por causa do esforço Fc.
                        mensagem = "Esforço Fb neste estaqueamento gera instabilidade.";

                        testeValidade = false;

                    } else if (testePlanoXZ() && fc != 0) {

                        //Passar mensagem de instabilidade por causa do esforço Fb.
                        mensagem = "Esforço Fc neste estaqueamento gera instabilidade.";

                        testeValidade = false;

                    } else {

                        //Realizar o cálculo.
                        testeValidade = true;
                    }

                } else if (testeEstacasVerticaisColineares() && !testePlanoXY() && !testePlanoXZ()) {

                    //Passar mensagem de que o estaqueamento é instável porque as estacas são colieares e não é estaqueamento plano.
                    mensagem = "Estacas verticais coplanares geram instabilidade.";

                    testeValidade = false;

                } else {

                    //Realizar cálculo.
                    testeValidade = true;
                }

                break;

            // ESTAQUEAMENTO PLANO EM XY
            case 'B':

                titulo = "INSTABILIDADE!";

                if (fz != 0 || fa != 0 || fb != 0) {

                    //Passar mensagem de que o estaqueamento não reage a esforços Fz, Fa ou Fb.
                    mensagem = "O estaqueamento não reage aos esforços Fz, Fa, Fb.\nPor favor, reconsidere estes esforços.";

                    testeValidade = false;

                } else if (nEstacas == 2) {

                    if (!testeSimetriaXZ3()) {

                        //Passar mensagem de que duas estacas não simétricas são instáveis.
                        mensagem = "Estaqueamento com duas estacas não simétricas é instável.";

                        testeValidade = false;

                    } else if (fy != 0 || fc != 0) {

                        //Passar mensagem de instabilidade por causa do esforço Fy ou Fc.
                        mensagem = "Esforço Fy ou Fc neste estaqueamento gera instabilidade.";

                        testeValidade = false;

                    } else {

                        //Realizar cálculo.
                        testeValidade = true;
                    }

                } else if (nEstacas == 3) {

                    if (testeSimetriaXZ3() && (fy != 0 || fc != 0)) {

                        //Passar mensagem de instabilidade por causa do esforço Fy ou Fc.
                        mensagem = "Esforço Fy ou Fc neste estaqueamento gera instabilidade.";

                        testeValidade = false;

                    } else

                        //Realizar cálculo.
                        testeValidade = true;

                } else {

                    //Realizar cálculo.
                    testeValidade = true;

                }

                break;

            // ESTAQUEAMENTO PLANO EM XZ
            case 'C':

                titulo = "INSTABILIDADE!";

                if (fy != 0 || fa != 0 || fc != 0) {

                    //Passar mensagem de que o estaqueamento não reage a esforços Fy, Fa ou Fc.
                    mensagem = "O estaqueamento não reage aos esforços Fy, Fa, Fc.\nPor favor, reconsidere estes esforços.";

                    testeValidade = false;

                } else if (nEstacas == 2) {

                    if (!testeSimetriaXY3()) {

                        //Passar mensagem de que duas estacas não simétricas são instáveis.
                        mensagem = "Estaqueamento com duas estacas não simétricas é instável.";

                        testeValidade = false;

                    } else if (fz != 0 || fb != 0) {

                        //Passar mensagem de instabilidade por causa do esforço Fz ou Fb.
                        mensagem = "Esforço Fz ou Fb neste estaqueamento gera instabilidade.";

                        testeValidade = false;

                    } else {

                        //Realizar cálculo.
                        testeValidade = true;
                    }

                } else if (nEstacas == 3) {

                    if (testeSimetriaXY3() && (fz != 0 || fb != 0)) {

                        //Passar mensagem de instabilidade por causa do esforço Fy ou Fb.
                        mensagem = "Esforço Fz ou Fb neste estaqueamento gera instabilidade.";

                        testeValidade = false;

                    } else

                        //Realizar cálculo.
                        testeValidade = true;

                } else {

                    //Realizar cálculo.
                    testeValidade = true;

                }

                break;

            // SIMETRIA POR XY
            case 'D':

                titulo = "INSTABILIDADE!";

                if (fz != 0 || fa != 0 || fb != 0) {

                    //Passar mensagem de que o estaqueamento não reage a esforços Fz, Fa ou Fb.
                    mensagem = "Os esforços Fz, Fa, Fb geram instabilidade.\nPor favor, reconsidere estes esforços.";

                    testeValidade = false;

                } else if(nEstacas == 3) {

                    if (testeTetraedroEquilateroSimetria()) {

                        if (fc != 0) {

                            mensagem = "Esforço Fc causa instabilidade neste estaqueamento";

                            testeValidade = false;

                        } else {

                            //Realizar cálculo

                            testeValidade = true;
                        }

                    } else if (testeTrianguloEquilateroSimetria()) {

                        if (fy != 0 || fc != 0) {

                            mensagem = "Esforço Fy ou Fc causa instabilidade neste estaqueamento";

                            testeValidade = false;

                        } else {

                            //Realizar cálculo

                            testeValidade = true;
                        }
                    }

                } else if (nEstacas == 4) {

                    if (testeDoisParesSimetricos() && (fy != 0 || fc != 0)) {

                        mensagem = "Esforço Fy ou Fc causa instabilidade neste estaqueamento";

                        testeValidade = false;

                    } else {

                        //Realizar cálculo

                        testeValidade = true;
                    }

                } else {

                    //Realizar cálculo

                    testeValidade = true;
                }

                break;

            // SIMETRIA POR XZ
            case 'E':

                titulo = "INSTABILIDADE!";

                if (fy != 0 || fa != 0 || fc != 0) {

                    //Passar mensagem de que o estaqueamento não reage a esforços Fy, Fa ou Fb.
                    mensagem = "Os esforços Fy, Fa, Fc geram instabilidade.\nPor favor, reconsidere estes esforços.";

                    testeValidade = false;

                } else if(nEstacas == 3) {

                    if (testeTetraedroEquilateroSimetria()) {

                        if (fb != 0) {

                            mensagem = "Esforço Fb causa instabilidade neste estaqueamento";

                            testeValidade = false;

                        } else {

                            //Realizar cálculo

                            testeValidade = true;
                        }

                    } else if (testeTrianguloEquilateroSimetria()) {

                        if (fz != 0 || fb != 0) {

                            mensagem = "Esforço Fz ou Fb causa instabilidade neste estaqueamento";

                            testeValidade = false;

                        } else {

                            //Realizar cálculo

                            testeValidade = true;
                        }
                    }

                } else if (nEstacas == 4) {

                    if (testeDoisParesSimetricos() && (fz != 0 || fb != 0)) {

                        mensagem = "Esforço Fz ou Fb causa instabilidade neste estaqueamento";

                        testeValidade = false;

                    } else {

                        //Realizar cálculo

                        testeValidade = true;
                    }

                } else {

                    //Realizar cálculo

                    testeValidade = true;
                }

                break;

            // SIMETRIA POR DOIS PLANOS
            case 'F':

                titulo = "INSTABILIDADE!";

                if (nEstacas < 6) {

                    if (fy != 0 || fz != 0 || fb != 0 || fc != 0) {

                        mensagem = "Esforço Fy, Fz, Fb ou Fc causa instabilidade neste estaqueamento";

                        testeValidade = false;

                    } else {

                        //Realizar cálculo

                        testeValidade = true;
                    }

                } else {

                    if ((eEI.size() + eEII.size() + eZero.size()) == (nEstacas - 4)) {

                        if (fz != 0 || fb != 0) {

                            mensagem = "Esforço Fz ou Fb causa instabilidade neste estaqueamento";

                            testeValidade = false;

                        } else {

                            //Realizar cálculo

                            testeValidade = true;
                        }

                    } else if ((eEIII.size() + eEIV.size() + eZero.size()) == (nEstacas - 4)) {

                        if (fy != 0 || fc != 0) {

                            mensagem = "Esforço Fy ou Fc causa instabilidade neste estaqueamento";

                            testeValidade = false;

                        } else {

                            //Realizar cálculo

                            testeValidade = true;
                        }

                    } else {

                        //Realizar cálculo

                        testeValidade = true;

                    }
                }

                break;

            // SIMETRIA POR UM EIXO
            case 'G':

                titulo = "INSTABILIDADE!";

                if (fy != 0 || fz != 0 || fb != 0 || fc != 0) {

                    //Passar mensagem de que o estaqueamento não reage a esforços Fy, Fz, Fb ou Fc.
                    mensagem = "O estaqueamento não reage aos esforços Fy, Fz, Fb, Fc.\nPor favor, reconsidere estes esforços.";

                    testeValidade = false;

                } else if (nEstacas == 2) {

                    mensagem = "Apenas duas estacas não garantem estabilidade para este estaqueamento.";

                    testeValidade = false;

                } else {

                    testeValidade = true;
                }

                break;
        }
    }

    public void calcularCaso() {

        if (casoSimetria == 'A') {

            EstacasVerticais estacasVerticais = new EstacasVerticais(diametro, comprimento,
                    mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento);

            reacoesNormais = estacasVerticais.calcularEsforcosNormais();

            deslocamentoElastico = estacasVerticais.calcularMovElastico();

        } else if (casoSimetria == 'B' || casoSimetria == 'C') {

            EstaqueamentoPlano estaqueamentoPlano = new EstaqueamentoPlano(diametro, comprimento,
                    mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas,cota, estaqueamento, casoSimetria);

            reacoesNormais = estaqueamentoPlano.calcularEsforcosNormais(casoSimetria);

            deslocamentoElastico = estaqueamentoPlano.calcularMovElastico(casoSimetria);

        } else if (casoSimetria == 'D' || casoSimetria == 'E') {

            SimetriaPorUmPlano simetriaPorUmPlano = new SimetriaPorUmPlano(diametro, comprimento,
                    mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento, casoSimetria);

            reacoesNormais = simetriaPorUmPlano.calcularEsforcosNormais(casoSimetria);

            deslocamentoElastico = simetriaPorUmPlano.calcularMovimentoElastico(casoSimetria);

        } else if (casoSimetria == 'F') {

            SimetriaPorDoisPlanos simetriaPorDoisPlanos = new SimetriaPorDoisPlanos(diametro, comprimento,
                    mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento);

            reacoesNormais = simetriaPorDoisPlanos.calcularEsforcosNormais();

            deslocamentoElastico = simetriaPorDoisPlanos.calcularMovimentoElastico();

        } else if (casoSimetria == 'G') {

            SimetriaPorUmEixo simetriaPorUmEixo = new SimetriaPorUmEixo(diametro, comprimento,
                    mElasticidade, fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento);

            reacoesNormais = simetriaPorUmEixo.calcularEsforcosNormais();

        } else {

            try {

                double[][] matriz = {{fx}, {fy}, {fz}, {fa}, {fb}, {fc}};

                deslocamentoElastico = matrizRigidez.solve(new Matrix(matriz)).getArray();

                reacoesNormais = (matrizComponentesEstacas.transpose()).times(matrizRigidez.solve(new Matrix(matriz)));

            } catch (Exception e) {

//                Toast.makeText(activity,"Não é possível achar reações.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Matrix montarMatrizRigidezEstacas() {

        double[][] matriz = new double[nEstacas][nEstacas];

        double rigidezEstaca = (mElasticidade * Math.PI * Math.pow(diametro, 2)) / (4 * comprimento);

        for (int i = 0; i < nEstacas; i++) {

            for (int j = 0; j < nEstacas; j++) {

                if (i == j) {

                    matriz[i][j] = rigidezEstaca;

                } else {

                    matriz[i][j] = 0;
                }
            }
        }

        return new Matrix(matriz);
    }

    // Método para calcular as componentes vetoriais de cada estaca i, retornando
    // a matriz P (6 x n) com as respectivas componentes ordenadas pelo índice i, sendo:
    // i o índice da estaca;
    // n o número de estacas.
    private Matrix montaMatrizComponentesEstacas() {

        double[][] matrizP = new double[6][nEstacas];

        for (int i = 0; i < nEstacas; i++) {
            Estaca e = estaqueamento[i];

            double xi = e.getPosX();
            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            double pxi = cosAi(ai);
            double pyi = senAi(ai)*cosWi(wi);
            double pzi = senAi(ai)*senWi(wi);
            double pai = yi * pzi - zi * pyi;
            double pbi = zi * pxi - xi * pzi;
            double pci = xi * pyi - yi * pxi;

            matrizP[0][i] = pxi;
            matrizP[1][i] = pyi;
            matrizP[2][i] = pzi;
            matrizP[3][i] = pai;
            matrizP[4][i] = pbi;
            matrizP[5][i] = pci;
        }

        return new Matrix(matrizP);
    }

    // Método para a montagem da matriz de rigidez geral S pela multiplicação
    // da matriz P por sua transposta, retornando a matriz S (6 x 6).
    private Matrix calcularMatrizRigidez() {

        return (matrizComponentesEstacas.times(matrizRigidezEstacas)).times(matrizComponentesEstacas.transpose());
    }

    private char definirCaso() {

        testeQuadrantes();

        double somaYi = 0;
        double somaZi = 0;
        double somaCosAi = 0;
        double somaSenAiCosWi = 0;
        double somaSenAiSenWi = 0;

        for (int i = 0; i < nEstacas; i++) {
            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            somaYi += yi;
            somaZi += zi;
            somaCosAi += cosAi(ai);
            somaSenAiCosWi += senAicosWi(ai, wi);
            somaSenAiSenWi += senAisenWi(ai, wi);

        }

        /* Todas estacas paralelas, ou seja, o ângulo de cravação
         de todas as estacas é de 90 graus com o plano do solo */
        if (somaCosAi == nEstacas) {

            return 'A';

        } /* Estaqueamento plano em XY */
        else if (testePlanoXY()) {

            return 'B';

        } /* Estaqueamento plano em XZ */
        else if (testePlanoXZ()) {

            return 'C';

        } /* Plano XY de simetria */
        else if (testeSimetriaXY1() && testeSimetriaXY2() && testeSimetriaXY3() && testeSimetriaXY4() &&
                (!testeSimetriaXZ1() || !testeSimetriaXZ2() || !testeSimetriaXZ3() || !testeSimetriaXZ4())) {

            return 'D';

        } /* Plano XZ de simetria */
        else if (testeSimetriaXZ1() && testeSimetriaXZ2() && testeSimetriaXZ3() && testeSimetriaXZ4() &&
                (!testeSimetriaXY1() || !testeSimetriaXY2() || !testeSimetriaXY3() || !testeSimetriaXY4())) {

            return 'E';

        }  /* Simetria por dois planos (XY e XZ) */
        else if (testeSimetriaXY1() && testeSimetriaXY2() && testeSimetriaXY3() && testeSimetriaXY4() &&
                testeSimetriaXZ1() && testeSimetriaXZ2() && testeSimetriaXZ3() && testeSimetriaXZ4()) {

            return 'F';

        } /* Simetria pelo eixo x */
        else if ((!testeSimetriaXY1() || !testeSimetriaXY2() || !testeSimetriaXY3()) && testeSimetriaXY4() &&
                (!testeSimetriaXZ1() || !testeSimetriaXZ2() || !testeSimetriaXZ3()) && testeSimetriaXZ4() &&
                somaYi == 0 && somaZi == 0 && somaSenAiCosWi == 0 && somaSenAiSenWi == 0) {

            return 'G';

        } /* Caso geral */
        else {

            return 'Z';
        }
    }

    private boolean testeEstacasVerticaisColineares() {

        double[] yi = new double[nEstacas];
        double[] zi = new double[nEstacas];
        double[] inclinacao = new double[nEstacas - 1];
        ArrayList<String> inclinacoes = new ArrayList<>();

        int i = 0;

        while (i < nEstacas) {

            Estaca e = estaqueamento[i];

            yi[i] = e.getPosY();
            zi[i] = e.getPosZ();

            i++;
        }

        int j = 0;

        while (j < inclinacao.length) {

            if (yi[0] - yi[j+1] == 0) {

                inclinacao[j] = Math.PI / 2;

            } else {

                inclinacao[j] = Math.atan((zi[0] - zi[j+1]) / (yi[0] - yi[j+1]));
            }


            if (inclinacao[j] == inclinacao[0]) {

                inclinacoes.add("Sim");

            } else {

                inclinacoes.add("Nao");
            }

            j++;
        }

        return !inclinacoes.contains("Nao");
    }

    private boolean testeTetraedroEquilateroSimetria() {

        boolean trianguloEquilatero = testeTrianguloEquilateroSimetria();

        return  ((alturaTetraedro == cota) && trianguloEquilatero);
    }

    private boolean testeTrianguloEquilateroSimetria() {

        double[] yi = new double[nEstacas];
        double[] zi = new double[nEstacas];
        double[] ai = new double[nEstacas];
        double[] wi = new double[nEstacas];
        double[] li = new double[nEstacas];

        double somaYi = 0;
        double somaZi = 0;
        double somaCosWi = 0;
        double somaSenWi = 0;

        boolean teste = false;

        int i = 0;

        while (i < nEstacas) {

            Estaca e = estaqueamento[i];

            yi[i] = e.getPosY();
            zi[i] = e.getPosZ();
            ai[i] = e.getAngCrav();
            wi[i] = e.getAngProj();

            somaYi =+ yi[i];
            somaZi =+ zi[i];
            somaCosWi =+ cosWi(wi[i]);
            somaSenWi =+ senWi(wi[i]);

            i++;
        }

        li[0] = Math.sqrt(Math.pow((yi[0] - yi[1]), 2) + Math.pow((zi[0] - zi[1]), 2));
        li[1] = Math.sqrt(Math.pow((yi[1] - yi[2]), 2) + Math.pow((zi[1] - zi[2]), 2));
        li[2] = Math.sqrt(Math.pow((yi[2] - yi[0]), 2) + Math.pow((zi[2] - zi[0]), 2));

        alturaTetraedro = Math.sqrt(2 / 3) * li[0];

        switch (casoSimetria) {

            case 'D':

                teste = (li[0] == li[1] && li[0] == li[2] && ai[0] == ai[1] && ai[0] == ai[2] && somaYi == 0 && somaZi == 0 && somaCosWi != 0);

                break;

            case 'E':

                teste = (li[0] == li[1] && li[0] == li[2] && ai[0] == ai[1] && ai[0] == ai[2] && somaYi == 0 && somaZi == 0 && somaSenWi != 0);

                break;
        }

        return teste;
    }

    private boolean testeDoisParesSimetricos() {

        double[] yi = new double[nEstacas];
        double[] zi = new double[nEstacas];
        double[] ai = new double[nEstacas];
        double[] wi = new double[nEstacas];

        double somaYi = 0;
        double somaZi = 0;
        double somaCosWi = 0;
        double somaSenWi = 0;

        boolean teste = false;

        int i = 0;

        while (i < nEstacas) {

            Estaca e = estaqueamento[i];

            yi[i] = e.getPosY();
            zi[i] = e.getPosZ();
            ai[i] = e.getAngCrav();
            wi[i] = e.getAngProj();

            somaYi =+ yi[i];
            somaZi =+ zi[i];
            somaCosWi =+ cosWi(wi[i]);
            somaSenWi =+ senWi(wi[i]);

            i++;
        }

        switch (casoSimetria) {

            case 'D':

                teste = (ai[0] == ai[1] && ai[0] == ai[2] && ai[0] == ai[3] && somaYi == 0 && somaCosWi == 0);

                break;

            case 'E':

                teste = (ai[0] == ai[1] && ai[0] == ai[2] && ai[0] == ai[3] && somaZi == 0 && somaSenWi == 0);

                break;
        }

        return teste;
    }

    private void testeQuadrantes() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            if (yi > 0 && zi > 0) {

				/* A estaca i esta no 1o Quadrante (QI) */
                eQI.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi < 0 && zi > 0) {

				/* A estaca i esta no 2o Quadrante (QII) */
                eQII.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi < 0 && zi < 0) {

				/* A estaca i esta no 3o Quadrante (QIII) */
                eQIII.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi > 0 && zi < 0) {

				/* A estaca i esta no 4o Quadrante (QIV) */
                eQIV.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi > 0 && zi == 0) {

				/* A estaca i esta no segmento positivo do eixo Y (EI) */
                eEI.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi < 0 && zi == 0) {

				/* A estaca i esta no segmento negativo do eixo Y (EII) */
                eEII.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi == 0 && zi > 0) {

				/* A estaca i esta no segmento positivo do eixo Z (EIII) */
                eEIII.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi == 0 && zi < 0) {

				/* A estaca i esta no segmento negativo do eixo Z (EIV) */
                eEIV.add(yi + ", " + zi + ", " + ai + ", " + wi);

            } else if (yi == 0 && zi == 0) {

				/* A estaca i esta na origem do sistema de eixos coordenados */
                eZero.add(yi + ", " + zi + ", " + ai + ", " + wi);
            }
        }
    }

    /* Método para testar se todas as estacas estão contidas no plano XY. */
    private boolean testePlanoXY() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if ((eEI.contains(estacai) || eEII.contains(estacai) || eZero.contains(estacai) && wi == 0)
                    || (eEI.contains(estacai) || eEII.contains(estacai) || eZero.contains(estacai) && wi == 180)) {

                ePlanoXY.add("true");

            } else {

                ePlanoXY.add("false");
            }
        }

        return !ePlanoXY.contains("false");
    }

    /* Método para testar se todas as estacas estão contidas no plano XZ. */
    private boolean testePlanoXZ() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if ((eEIII.contains(estacai) || eEIV.contains(estacai) || eZero.contains(estacai) && ai == 0)
                    || (eEIII.contains(estacai) || eEIV.contains(estacai) || eZero.contains(estacai) && wi == 90)
                    || (eEIII.contains(estacai) || eEIV.contains(estacai) || eZero.contains(estacai) && wi == 270)) {

                ePlanoXZ.add(estacai);

            } else {

                ePlanoXZ.add("false");
            }
        }

        return !ePlanoXZ.contains("false");
    }

    private boolean testeSimetriaXY1() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (eQI.contains(estacai)) {
                estacaSimetricaQIV = yi + ", " + (-zi) + ", " + ai + ", " + (360 - wi);

            } else if (eQIV.contains(estacai)) {
                estacaSimetricaQI = yi + ", " + (-zi) + ", " + ai + ", " + (360 - wi);

            }
        }

        if (eQI.size() == eQIV.size() && eQI.contains(estacaSimetricaQI) && eQIV.contains(estacaSimetricaQIV)) {

            return true;

        } else {

            return false;

        }
    }

    private boolean testeSimetriaXY2() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (eQII.contains(estacai)) {
                estacaSimetricaQIII = yi + ", " + (-zi) + ", " + ai + ", " + (360 - wi);

            } else if (eQIII.contains(estacai)) {
                estacaSimetricaQII = yi + ", " + (-zi) + ", " + ai + ", " + (360 - wi);

            }
        }

        if (eQII.size() == eQIII.size() && eQII.contains(estacaSimetricaQII) && eQIII.contains(estacaSimetricaQIII)) {
            return true;

        } else {
            return false;

        }
    }

    private boolean testeSimetriaXY3() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (eEIII.contains(estacai)) {
                estacaSimetricaEIV = yi + ", " + (-zi) + ", " + ai + ", " + (360 - wi);

            } else if (eEIV.contains(estacai)) {
                estacaSimetricaEIII = yi + ", " + (-zi) + ", " + ai + ", " + (360 - wi);

            } else if (eEI.contains(estacai) && (ai == 0 || wi == 0)) {
                estacaSimetricaEI = estacai;

            } else if (eEII.contains(estacai) && (ai == 0 || wi == 180)) {
                estacaSimetricaEII = estacai;

            }
        }

        if (eEIII.size() == eEIV.size() && eEIII.contains(estacaSimetricaEIII) && eEIV.contains(estacaSimetricaEIV)) {
            return true;

        } else if ((eEI.contains(estacaSimetricaEI) && !eEI.contains(null)) || (eEI.contains(null) && !eEI.contains(estacaSimetricaEII))
                || (eEI.contains(estacaSimetricaEI) && !eEI.contains(estacaSimetricaEII))) {
            return true;

        } else {
            return false;

        }
    }

    private boolean testeSimetriaXY4() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (!eZero.contains(estacai)) {
                return true;

            } else if (eZero.contains(estacai) && ai == 0) {
                return true;

            } else if (eZero.contains(estacai) && ai != 0 && (wi == 0 || wi == 180)) {
                return true;

            }

        } return false;
    }

    private boolean testeSimetriaXZ1() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (eQI.contains(estacai)) {
                estacaSimetricaQII = (-yi) + ", " + zi + ", " + ai + ", " + (180 - wi);

            } else if (eQII.contains(estacai)) {
                estacaSimetricaQI = (-yi) + ", " + zi + ", " + ai + ", " + (180 - wi);

            }
        }

        if (eQI.size() == eQII.size() && eQI.contains(estacaSimetricaQI) && eQII.contains(estacaSimetricaQII)) {

            return true;

        } else {

            return false;

        }
    }

    private boolean testeSimetriaXZ2() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (eQIII.contains(estacai)) {
                estacaSimetricaQIV = (-yi) + ", " + zi + ", " + ai + ", " + (540 - wi);

            } else if (eQIV.contains(estacai)) {
                estacaSimetricaQIII = (-yi) + ", " + zi + ", " + ai + ", " + (540 - wi);

            }
        }

        if (eQIII.size() == eQIV.size() && eQIII.contains(estacaSimetricaQIII) && eQIV.contains(estacaSimetricaQIV)) {
            return true;

        } else {
            return false;

        }
    }

    private boolean testeSimetriaXZ3() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (eEI.contains(estacai)) {
                estacaSimetricaEII = (-yi) + ", " + zi + ", " + ai + ", " + (180 - wi);

            } else if (eEII.contains(estacai)) {
                estacaSimetricaEI = (-yi) + ", " + zi + ", " + ai + ", " + (180 - wi);

            } else if (eEIII.contains(estacai) && (ai == 0 || wi == 90)) {
                estacaSimetricaEIII = estacai;

            } else if (eEIV.contains(estacai) && (ai == 0 || wi == 270)) {
                estacaSimetricaEIV = estacai;

            }
        }

        if (eEI.size() == eEII.size() && eEI.contains(estacaSimetricaEI) && eEII.contains(estacaSimetricaEII)) {
            return true;

        } else if ((eEIII.contains(estacaSimetricaEIII) && !eEIV.contains(null)) || (eEIII.contains(null) && !eEIV.contains(estacaSimetricaEIV))
                || (eEIII.contains(estacaSimetricaEIII) && !eEIV.contains(estacaSimetricaEIV))) {
            return true;

        } else {
            return false;

        }
    }

    private boolean testeSimetriaXZ4() {

        for (int i = 0; i < nEstacas; i++) {

            Estaca e = estaqueamento[i];

            double yi = e.getPosY();
            double zi = e.getPosZ();
            double ai = e.getAngCrav();
            double wi = e.getAngProj();

            String estacai = yi + ", " + zi + ", " + ai + ", " + wi;

            if (!eZero.contains(estacai)) {
                return true;

            } else if (eZero.contains(estacai) && ai == 0) {
                return true;

            } else if (eZero.contains(estacai) && ai != 0 && (wi == 90 || wi == 270)) {
                return true;

            }

        } return false;
    }

    private double cosAi(double ai) {

        double cosAi = Math.cos(Math.toRadians(ai));

        if (cosAi == 0) {

            return cosAi;
        } else {

            BigDecimal bd = new BigDecimal(cosAi).setScale(6, RoundingMode.HALF_EVEN);
            return bd.doubleValue();
        }
    }

    private double senAi(double ai) {

        double senAi = Math.sin(Math.toRadians(ai));

        if (senAi == 0) {

            return senAi;
        } else {

            BigDecimal bd = new BigDecimal(senAi).setScale(6, RoundingMode.HALF_EVEN);
            return bd.doubleValue();
        }
    }

    private double cosWi(double wi) {

        double cosWi;

        if (90 < wi && wi <= 180) {
            cosWi = -Math.cos(Math.toRadians(180 - wi));

        } else if (180 < wi && wi <= 270) {
            cosWi = -Math.cos(Math.toRadians(wi - 180));

        } else if (270 < wi && wi <= 360) {
            cosWi = Math.cos(Math.toRadians(360 - wi));

        } else {
            cosWi = Math.cos(Math.toRadians(wi));

        }

        if (cosWi == 0) {

            return cosWi;
        } else {

            BigDecimal bd = new BigDecimal(cosWi).setScale(6, RoundingMode.HALF_EVEN);
            return bd.doubleValue();
        }
    }

    private double senWi(double wi) {

        double senWi;

        if (90 < wi && wi <= 180) {
            senWi = Math.sin(Math.toRadians(180 - wi));

        } else if (180 < wi && wi <= 270) {
            senWi = -Math.sin(Math.toRadians(wi - 180));

        } else if (270 < wi && wi <= 360) {
            senWi = -Math.sin(Math.toRadians(360 - wi));

        } else {
            senWi = Math.sin(Math.toRadians(wi));

        }

        if (senWi == 0) {

            return senWi;
        } else {

            BigDecimal bd = new BigDecimal(senWi).setScale(6, RoundingMode.HALF_EVEN);
            return bd.doubleValue();
        }
    }

    private double senAicosWi (double ai, double wi) {

        double senAicosWi = senAi(ai)*cosWi(wi);

        if (senAicosWi == 0) {

            return senAicosWi;
        } else {

            BigDecimal bd = new BigDecimal(senAicosWi).setScale(6, RoundingMode.HALF_EVEN);
            return bd.doubleValue();
        }
    }

    private double senAisenWi (double ai, double wi) {

        double senAisenWi = senAi(ai)*senWi(wi);

        if (senAisenWi == 0) {

            return senAisenWi;
        } else {

            BigDecimal bd = new BigDecimal(senAisenWi).setScale(6, RoundingMode.HALF_EVEN);
            return bd.doubleValue();
        }
    }

    public Matrix getMatrizRigidezEstacas() {
        return matrizRigidezEstacas;
    }

    public Matrix getMatrizComponentesEstacas() {
        return matrizComponentesEstacas;
    }

    public Matrix getMatrizRigidez() {
        return matrizRigidez;
    }

    public char getCasoSimetria() {
        return casoSimetria;
    }

    public boolean isTesteValidade() {
        return testeValidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }
}