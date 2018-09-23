package br.com.mateus.sugarme.Presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.com.mateus.sugarme.R;

public class MainActivity extends AppCompatActivity {
    MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainController = new MainController();

       if(mainController.verificaLogin()){
           mainController.verificaTipoUsuario(MainActivity.this);
       }
       else{
           mainController.fazerLogin(this);
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mainController.onLoginResult(this, requestCode);
    }
}
