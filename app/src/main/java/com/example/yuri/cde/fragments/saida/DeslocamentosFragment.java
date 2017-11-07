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

public class DeslocamentosFragment extends Fragment {

    private View view;

    private ListView deslocamentosList;

    private double[][] deslocamentoElastico;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_deslocamentos, container, false);

        deslocamentosList = view.findViewById(R.id.listView_deslocamentos);

        deslocamentoElastico = MainAuxiliar.deslocamentoElastico;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String[] dElasticos = new String[6];

        dElasticos[0] = "Vx: " + arredondarEmQuatroCasas(deslocamentoElastico[0][0] * 1000) + " mm.";
        dElasticos[1] = "Vy: " + arredondarEmQuatroCasas(deslocamentoElastico[1][0] * 1000) + " mm.";
        dElasticos[2] = "Vz: " + arredondarEmQuatroCasas(deslocamentoElastico[2][0] * 1000) + " mm.";
        dElasticos[3] = "Va: " + arredondarEmQuatroCasas(deslocamentoElastico[3][0]) + " rad.";
        dElasticos[4] = "Vb: " + arredondarEmQuatroCasas(deslocamentoElastico[4][0]) + " rad.";
        dElasticos[5] = "Vc: " + arredondarEmQuatroCasas(deslocamentoElastico[5][0]) + " rad.";

        ArrayAdapter<String> adapterDeslocamento = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dElasticos);
        deslocamentosList.setAdapter(adapterDeslocamento);
    }

    private double arredondarEmQuatroCasas(double valor) {

        BigDecimal bd = new BigDecimal(valor).setScale(4, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
