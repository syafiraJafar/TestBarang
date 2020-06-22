package com.example.testbarang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

//import android.support.annotation.Nullable;
//import android.support.design.widget.Snackbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TambahData extends AppCompatActivity {
    private DatabaseReference database;

    //variable fields EditText dan Button
    private Button btSubmit;
    private EditText etKode;
    private EditText etNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);

        etKode = (EditText) findViewById(R.id.editNo);
        etNama = (EditText) findViewById(R.id.editNama);
        btSubmit = (Button) findViewById(R.id.btnOk);

        //mengambil referensi ke firebase Database
        database = FirebaseDatabase.getInstance().getReference();

        final Barang barang = (Barang) getIntent().getSerializableExtra("data");

        if (barang != null){
            etNama.setText(barang.getNama());
            etKode.setText(barang.getKode());
            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barang.setNama(etNama.getText().toString());
                    barang.setKode(etKode.getText().toString());

                    UpdateBarang(barang);
                }
            });
        }else {
            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(etKode.getText().toString().isEmpty()) && !(etNama.getText().toString().isEmpty()))
                        submitBrg(new Barang(etKode.getText().toString(), etNama.getText().toString()));
                    else
                        Toast.makeText(getApplicationContext(), "Data tidak boleh kosong",Toast.LENGTH_LONG).show();

                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etNama.getWindowToken(),0);

                }
            });
        }
    }

    private boolean isEmpty(String s){
        //cek apakah ada fields yang kosing ,sebelum disumbmit
        return TextUtils.isEmpty(s);
    }

    private void UpdateBarang(Barang barang){
        /**
         * Baris kode yang digunakan untuk mengupdate data barang
         * yang sudah dimasukkan di Firebase Realtime Database
         */
        database.child("Barang").child(barang.getKey()).setValue(barang).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                /**
                 * Baris kode yang akan dipanggil apabila proses update barang sukses
                 */
                Snackbar.make(findViewById(R.id.btnOk), "Data berhasil diubah", Snackbar.LENGTH_LONG).setAction("Oke", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
            }
        });
    }

    public void submitBrg(Barang brg){
        /*Berikut ini adalah kode yang digunkan untuk mengirim data ke firebase
        * Realtime Database dan juga kita set onSuccessListener yang berisi
        * kode yang aka dijalankan ketika data berhasi; di tambahkan*/
        database.child("Barang").push().setValue(brg).addOnSuccessListener(this,
                new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                etKode.setText("");
                etNama.setText("");
                Toast.makeText(getApplicationContext(),"Data behasil ditambahkan", Toast.LENGTH_LONG).show();
            }
        });
    }
    public static Intent getActIntent(Activity activity){
        //kode untuk pengambilan Intent
        return new Intent(activity, TambahData.class);
    }
}
