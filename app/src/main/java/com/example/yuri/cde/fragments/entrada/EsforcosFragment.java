package com.example.yuri.cde.fragments.entrada;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuri.cde.R;
import com.example.yuri.cde.activities.MainActivity;
import com.example.yuri.cde.adapters.ImagensEsforcosViewPager;

public class EsforcosFragment extends Fragment {

    private View view;

    private ViewPager imagensViewPager;

    private EditText fxEditText;
    private EditText fyEditText;
    private EditText fzEditText;
    private EditText faEditText;
    private EditText fbEditText;
    private EditText fcEditText;

    private Button confirmarEsforcosButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_esforcos, container, false);

        fxEditText = view.findViewById(R.id.editText_fx);
        fyEditText = view.findViewById(R.id.editText_fy);
        fzEditText = view.findViewById(R.id.editText_fz);
        faEditText = view.findViewById(R.id.editText_mx);
        fbEditText = view.findViewById(R.id.editText_my);
        fcEditText = view.findViewById(R.id.editText_mz);

        confirmarEsforcosButton = view.findViewById(R.id.button_confirmar_esforcos);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ImagensEsforcosViewPager viewPager = new ImagensEsforcosViewPager(getContext());

        imagensViewPager = view.findViewById(R.id.viewPager_esforcos_imagens);

        imagensViewPager.setAdapter(viewPager);

        confirmarEsforcosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = getActivity().getLayoutInflater();
                View view1 = li.inflate(R.layout.dialog_erro, null);

                TextView tv_titulo = view1.findViewById(R.id.textView_titulo_erro);
                TextView tv_mensagem = view1.findViewById(R.id.textView_mensagem_erro);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view1);

                try {

                    MainActivity.fx = 1000 * Double.parseDouble(fxEditText.getText().toString());

                    if (MainActivity.fx == 0) {

                        throw null;

                    } else {

                        try {

                            MainActivity.fy = 1000 * Double.parseDouble(fyEditText.getText().toString());

                        } catch (Exception e) {

                            MainActivity.fy = 0;
                        }

                        try {

                            MainActivity.fz = 1000 * Double.parseDouble(fzEditText.getText().toString());

                        } catch (Exception e) {

                            MainActivity.fz = 0;
                        }

                        try {

                            MainActivity.fa = 1000 * Double.parseDouble(faEditText.getText().toString());

                        } catch (Exception e) {

                            MainActivity.fa = 0;
                        }

                        try {

                            MainActivity.fb = 1000 * Double.parseDouble(fbEditText.getText().toString());

                        } catch (Exception e) {

                            MainActivity.fb = 0;
                        }

                        try {

                            MainActivity.fc = 1000 * Double.parseDouble(fcEditText.getText().toString());

                        } catch (Exception e) {

                            MainActivity.fc = 0;
                        }

                        Toast.makeText(view.getContext(), "Esforços salvos.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                    final AlertDialog alerta;

                    tv_titulo.setText("Erro");
                    tv_mensagem.setText("Insira pelo menos um valor diferente de zero para Fx.");

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
}