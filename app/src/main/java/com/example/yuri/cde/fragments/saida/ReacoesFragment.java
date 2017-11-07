package com.example.yuri.cde.fragments.saida;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yuri.cde.R;
import com.example.yuri.cde.auxiliares.MainAuxiliar;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReacoesFragment extends Fragment {

    private View view;
    private ListView reacoesList;

    private int nEstacas;

    private Jama.Matrix reacoesMatrix;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reacoes, container, false);

        reacoesList = view.findViewById(R.id.listView_reacoes);

        nEstacas = MainAuxiliar.reacoesNormais.getArray().length;

        reacoesMatrix = MainAuxiliar.reacoesNormais;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String[] reacoesNormais = new String[nEstacas];

        double[][] normais = reacoesMatrix.getArray();

        for (int i = 0; i < nEstacas; i++) {
            reacoesNormais[i] = "Estaca " + (1 + i) + ": " + arredondarEmUmaCasa(normais[i][0]) + " N";
        }

        ArrayAdapter<String> adapterReacoes = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, reacoesNormais);
        reacoesList.setAdapter(adapterReacoes);
    }

    private double arredondarEmUmaCasa(double valor) {

        BigDecimal bd = new BigDecimal(valor).setScale(1, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
