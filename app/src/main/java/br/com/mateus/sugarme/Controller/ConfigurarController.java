package br.com.mateus.sugarme.Controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/***
 * Classe tipo Controller, com métodos estáticos para a ConfigurarActivity.
 */
public class ConfigurarController {
    private static DatabaseReference databaseReference;


    /***
     * Método responsável por alterar as configurações do paciente.
     * @param userId
     * @param aceitaChat
     * @param compartilharDiario
     * @param hipoglicemia
     * @param hiperglicemia
     */
    public static void alterarCofigurarPaciente (String userId, String aceitaChat, String compartilharDiario, String hipoglicemia, String hiperglicemia){

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("aceitaChat").setValue(aceitaChat);
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("compartilharDiario").setValue(compartilharDiario);
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("hipoglicemia").setValue(hipoglicemia);
        databaseReference.child("users").child("pacientes").child(userId).child("configurar").child("hiperglicemia").setValue(hiperglicemia);

    }

    /***
     * Método responsável por alterar as configurações do médico.
     * @param userId
     * @param aceitaChat
     */
    public static void alterarCofigurarMedico (String userId, String aceitaChat){

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child("medicos").child(userId).child("configurar").child("aceitaChat").setValue(aceitaChat);
    }

}
