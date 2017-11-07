package com.example.yuri.cde.fragments.entrada;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuri.cde.Estacas.Estaca;
import com.example.yuri.cde.R;
import com.example.yuri.cde.activities.MainActivity;
import com.example.yuri.cde.adapters.ImagensGeometriaViewPager;

import java.util.ArrayList;

public class GeometriaFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private View view;

    private ViewPager imagensViewPager;

    private EditText nEstacasEditText;
    private EditText cotaEditText;

    private Button prosseguirButton;
    private Button confirmarGeometriaButton;

    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_geometria, container, false);

        nEstacasEditText = view.findViewById(R.id.editText_quantidade_estacas);
        cotaEditText = view.findViewById(R.id.editText_cota);
        prosseguirButton = view.findViewById(R.id.button_prosseguir);

        linearLayout = view.findViewById(R.id.formulario_estacas);

        confirmarGeometriaButton = view.findViewById(R.id.button_confirmar_geometria);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        imagensViewPager = view.findViewById(R.id.viewPager_geometria_imagens);

        ImagensGeometriaViewPager viewPager = new ImagensGeometriaViewPager(getContext());

        imagensViewPager.setAdapter(viewPager);

        prosseguirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = getActivity().getLayoutInflater();
                View view1 = li.inflate(R.layout.dialog_erro, null);

                TextView tv_titulo = view1.findViewById(R.id.textView_titulo_erro);
                TextView tv_mensagem = view1.findViewById(R.id.textView_mensagem_erro);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view1);

                try {

                    MainActivity.nEstacas = Integer.parseInt(nEstacasEditText.getText().toString());
                    MainActivity.cota = Double.parseDouble(cotaEditText.getText().toString());

                    if (MainActivity.nEstacas > 1 && MainActivity.cota != 0) {

                        inserirFormularioEstacas();

                    } else {

                        final AlertDialog alerta;

                        tv_titulo.setText("Erro");
                        tv_mensagem.setText("Insira duas estacas ou mais.");

                        alerta = builder.create();

                        alerta.show();

                        view1.findViewById(R.id.button_retornar_erro).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alerta.dismiss();
                            }
                        });
                    }

                } catch (Exception e) {

                    final AlertDialog alerta;

                    tv_titulo.setText("Erro");
                    tv_mensagem.setText("Por favor, insira ambos os dados.");

                    alerta = builder.create();

                    alerta.show();

                    view1.findViewById(R.id.button_retornar_erro).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alerta.dismiss();
                        }
                    });
                }
            }
        });

        confirmarGeometriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.estaqueamento = new Estaca[MainActivity.nEstacas];

                try {

                    if (MainActivity.nEstacas > 0) {

                        for (int i = 0; i < MainActivity.nEstacas; i++) {

                            MainActivity.estaqueamento[i] = new Estaca(
                                    MainActivity.cota,
                                    Double.parseDouble(((EditText) MainActivity.itemEstacaViews[i].findViewById(R.id.editText_posY)).getText().toString()),
                                    Double.parseDouble(((EditText) MainActivity.itemEstacaViews[i].findViewById(R.id.editText_posZ)).getText().toString()),
                                    Double.parseDouble(((EditText) MainActivity.itemEstacaViews[i].findViewById(R.id.editText_ang_crav)).getText().toString()),
                                    Double.parseDouble(((EditText) MainActivity.itemEstacaViews[i].findViewById(R.id.editText_ang_proj)).getText().toString())
                            );
                        }

                        Toast.makeText(getContext(), "Estacas salvas.", Toast.LENGTH_LONG).show();

                    } else {

                        LayoutInflater li = getActivity().getLayoutInflater();
                        View view1 = li.inflate(R.layout.dialog_erro, null);

                        TextView tv_titulo = view1.findViewById(R.id.textView_titulo_erro);
                        TextView tv_mensagem = view1.findViewById(R.id.textView_mensagem_erro);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(view1);

                        final AlertDialog alerta;

                        tv_titulo.setText("Erro");
                        tv_mensagem.setText("Insira os dados acima.");

                        alerta = builder.create();

                        alerta.show();

                        view1.findViewById(R.id.button_retornar_erro).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alerta.dismiss();
                            }
                        });
                    }

                } catch (Exception e) {

                    LayoutInflater li = getActivity().getLayoutInflater();
                    View view1 = li.inflate(R.layout.dialog_erro, null);

                    TextView tv_titulo = view1.findViewById(R.id.textView_titulo_erro);
                    TextView tv_mensagem = view1.findViewById(R.id.textView_mensagem_erro);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(view1);

                    final AlertDialog alerta;

                    tv_titulo.setText("Erro");
                    tv_mensagem.setText("Insira todos os valores.");

                    alerta = builder.create();

                    alerta.show();

                    view1.findViewById(R.id.button_retornar_erro).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alerta.dismiss();
                        }
                    });
            }
            }
        });
    }

    private void inserirFormularioEstacas() {

        linearLayout.removeAllViews();

        MainActivity.itemEstacaViews = new View[MainActivity.nEstacas];

        MainActivity.inclinacaoEstacas = new Switch[MainActivity.nEstacas];

        for (int i = 0; i < MainActivity.nEstacas; i++) {

            MainActivity.itemEstacaViews[i] = getActivity().getLayoutInflater().inflate(R.layout.item_estaca, null, false);

            ((TextView) MainActivity.itemEstacaViews[i].findViewById(R.id.textView_indice)).setText("Estaca " + String.valueOf(i + 1));

            MainActivity.inclinacaoEstacas[i] = MainActivity.itemEstacaViews[i].findViewById(R.id.switch_inclinacao);

            MainActivity.inclinacaoEstacas[i].setOnCheckedChangeListener(this);

            ((EditText) MainActivity.itemEstacaViews[i].findViewById(R.id.editText_ang_crav)).setText("0");
            ((EditText) MainActivity.itemEstacaViews[i].findViewById(R.id.editText_ang_proj)).setText("0");

            linearLayout.addView(MainActivity.itemEstacaViews[i]);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        final LinearLayout linearLayout = (LinearLayout) buttonView.getParent();

        linearLayout.getChildAt(3);

        if (isChecked) {

            ((EditText) linearLayout.findViewById(R.id.editText_ang_crav)).setText("");
            ((EditText) linearLayout.findViewById(R.id.editText_ang_proj)).setText("");

            linearLayout.getChildAt(3).setVisibility(View.VISIBLE);

        } else {

            linearLayout.getChildAt(3).setVisibility(View.GONE);

            ((EditText) linearLayout.findViewById(R.id.editText_ang_crav)).setText("0.0");
            ((EditText) linearLayout.findViewById(R.id.editText_ang_proj)).setText("0.0");
        }
    }
}