package com.example.yuri.cde.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.yuri.cde.Estacas.Estaca;
import com.example.yuri.cde.auxiliares.MainAuxiliar;
import com.example.yuri.cde.adapters.MainFragmentPageAdapter;
import com.example.yuri.cde.R;
import com.example.yuri.cde.fragments.entrada.CaracteristicasFragment;
import com.example.yuri.cde.fragments.entrada.EsforcosFragment;
import com.example.yuri.cde.fragments.entrada.GeometriaFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public static double diametro;
    public static double comprimento;
    public static double mElasticidade;

    public static double fx;
    public static double fy;
    public static double fz;
    public static double fa;
    public static double fb;
    public static double fc;

    public static View[] itemEstacaViews;
    public static Switch[] inclinacaoEstacas;

    public static int nEstacas;
    public static double cota;
    public static Estaca[] estaqueamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = findViewById(R.id.tab_main);
        mViewPager = findViewById(R.id.main_view_pager);

        mViewPager.setAdapter(new MainFragmentPageAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.main_tab)));
        mViewPager.setOffscreenPageLimit(3);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_calcular:

                calcular();

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    private void calcular() {

        View view1 = getLayoutInflater().inflate(R.layout.dialog_erro, null);

        TextView tv_titulo = view1.findViewById(R.id.textView_titulo_erro);
        TextView tv_mensagem = view1.findViewById(R.id.textView_mensagem_erro);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view1);

        try {

            MainAuxiliar auxiliar = new MainAuxiliar(diametro, comprimento, mElasticidade,
                    fx, fy, fz, fa, fb, fc, nEstacas, cota, estaqueamento);

            auxiliar.checarValidade();

            if (auxiliar.isTesteValidade()) {

                auxiliar.calcularCaso();

                Intent intentCalcularEstaqueamento = new Intent(this, ResultadosActivity.class);
                startActivity(intentCalcularEstaqueamento);

            } else {

                final AlertDialog alerta;

                tv_titulo.setText(auxiliar.getTitulo());
                tv_mensagem.setText(auxiliar.getMensagem());

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
            tv_mensagem.setText("Reveja dados de entrada.");

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
}