package com.example.yuri.cde.fragments.entrada;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuri.cde.R;
import com.example.yuri.cde.activities.MainActivity;
import com.example.yuri.cde.auxiliares.MainAuxiliar;

public class CaracteristicasFragment extends Fragment {

    private View view;

    private EditText diametroEditText;
    private EditText comprimentoEditText;
    private Spinner fckSpinner;
    private Button confirmarCaracteristicasButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_caracteristicas, container, false);

        diametroEditText = view.findViewById(R.id.editText_diametro);
        comprimentoEditText = view.findViewById(R.id.editText_comprimento);

        fckSpinner = view.findViewById(R.id.spinner_fck);

        confirmarCaracteristicasButton = view.findViewById(R.id.button_confirmar_caracteristicas);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter<String> fckAdapter = new ArrayAdapter<>(view.getContext(),
                R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.item_fck));

        fckSpinner.setAdapter(fckAdapter);
        fckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                determinarFckConcreto(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                determinarFckConcreto(0);
            }
        });

        confirmarCaracteristicasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = getActivity().getLayoutInflater();
                View view1 = li.inflate(R.layout.dialog_erro, null);

                TextView tv_titulo = view1.findViewById(R.id.textView_titulo_erro);
                TextView tv_mensagem = view1.findViewById(R.id.textView_mensagem_erro);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view1);

                try {

                    MainActivity.diametro = Double.parseDouble(diametroEditText.getText().toString());
                    MainActivity.comprimento = Double.parseDouble(comprimentoEditText.getText().toString());

                    if (MainActivity.diametro == 0 || MainActivity.comprimento == 0 || MainActivity.mElasticidade == 0) {

                        final AlertDialog alerta;

                        tv_titulo.setText("Erro");
                        tv_mensagem.setText("Os valores não podem ser zero.");

                        alerta = builder.create();

                        alerta.show();

                        view1.findViewById(R.id.button_retornar_erro).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alerta.dismiss();
                            }
                        });

                    } else {

                        Toast.makeText(view.getContext(), "Dados salvos." , Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                    final AlertDialog alerta;

                    tv_titulo.setText("Erro");
                    tv_mensagem.setText("Preencha ambos os dados e selecione o fck.");

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

    private void determinarFckConcreto(int position) {

        switch (position) {

            case 0:

                MainActivity.mElasticidade = 0;
                break;

            case 1:

                // 20MPa
                MainActivity.mElasticidade = 21 * Math.pow(10, 9);
                break;

            case 2:

                // 25MPa
                MainActivity.mElasticidade = 24 * Math.pow(10, 9);
                break;

            case 3:

                // 30MPa
                MainActivity.mElasticidade = 27 * Math.pow(10, 9);
                break;

            case 4:

                // 35MPa
                MainActivity.mElasticidade = 29 * Math.pow(10, 9);
                break;

            case 5:

                // 40MPa
                MainActivity.mElasticidade = 32 * Math.pow(10, 9);
                break;

            case 6:

                // 45MPa
                MainActivity.mElasticidade = 34 * Math.pow(10, 9);
                break;

            case 7:

                //50MPa
                MainActivity.mElasticidade = 37 * Math.pow(10, 9);
                break;
        }
    }
}
