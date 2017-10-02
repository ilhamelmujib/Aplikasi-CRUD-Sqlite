package com.example.pepey.crudapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pepey.crudapp.utils.DividerItemDecoration;
import com.example.pepey.crudapp.adapter.User;
import com.example.pepey.crudapp.adapter.UserCustomAdapter;
import com.example.pepey.crudapp.database.DataHelper;
import com.example.pepey.crudapp.utils.SwipeUtilDelete;
import com.example.pepey.crudapp.utils.SwipeUtilEdit;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    protected MainActivity.LayoutManagerType mCurrentLayoutManagerType;
    private List<User> userList;
    private DataHelper dataHelper;
    private RecyclerView recyclerView;
    private FloatingActionButton fabTambah;
    private SQLiteDatabase tampil, tulis;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog alertDialog;
    private DisplayMetrics metricsCreate;
    private Dialog dialogCreate;
    protected UserCustomAdapter userAdapter;
    protected Cursor cursor;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataHelper  =  new DataHelper(this);
        tampil      = dataHelper.getReadableDatabase();
        tulis       = dataHelper.getWritableDatabase();

        recyclerView                = (RecyclerView) findViewById(R.id.recyclerView);
        userList                    = new ArrayList<>();
        userAdapter                 = new UserCustomAdapter(userList);

        mLayoutManager              = new LinearLayoutManager(this);
        mCurrentLayoutManagerType   = MainActivity.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (MainActivity.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(userAdapter);

        fabTambah                   = (FloatingActionButton) findViewById(R.id.fabTambah);
        fabTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTambah();
            }
        });

        dataUser();
        setSwipeForRecyclerView();
    }

    public void setRecyclerViewLayoutManager(MainActivity.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(this);
        mCurrentLayoutManagerType = MainActivity.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setSwipeForRecyclerView() {

        SwipeUtilDelete swipeHelper = new SwipeUtilDelete(0, ItemTouchHelper.LEFT, this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                UserCustomAdapter adapter = (UserCustomAdapter) recyclerView.getAdapter();

                dialogHapus(userList.get(position).getNis());

                userList.clear();
                dataUser();
                userAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        swipeHelper.setLeftcolorCode(ContextCompat.getColor(this, R.color.colorRed));

        SwipeUtilEdit swipeHelpers = new SwipeUtilEdit(0, ItemTouchHelper.RIGHT, this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                UserCustomAdapter adapter = (UserCustomAdapter) recyclerView.getAdapter();

                dialogEdit(userList.get(position).getNis(), userList.get(position).getNama());

                userList.clear();
                dataUser();
                userAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper mItemTouchHelpers = new ItemTouchHelper(swipeHelpers);
        mItemTouchHelpers.attachToRecyclerView(recyclerView);
        swipeHelpers.setLeftcolorCode(ContextCompat.getColor(this, R.color.colorGreen));

    }

    public void dataUser(){
        cursor     = tampil.rawQuery("SELECT * FROM tb_user", null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            User user = new User(cursor.getString(0),cursor.getString(1));
            userList.add(user);
        }
    }

    public void dialogTambah(){
        dialogCreate  = new Dialog(MainActivity.this);
        dialogCreate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCreate.setContentView(R.layout.dialog_tambah);
        dialogCreate.setCanceledOnTouchOutside(false);
            metricsCreate = getResources().getDisplayMetrics();
            int width     = metricsCreate.widthPixels;
        dialogCreate.getWindow().setLayout((3 * width / 3), LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText nis   = dialogCreate.findViewById(R.id.etNis);
        final EditText nama  = dialogCreate.findViewById(R.id.etNama);
        Button simpan  = dialogCreate.findViewById(R.id.btnSimpan);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    tulis.execSQL("INSERT INTO tb_user (nama,nis) VALUES ('"+nama.getText()+"', '"+nis.getText()+"')");
                    Toast.makeText(MainActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                    userList.clear();
                    dataUser();
                    userAdapter.notifyDataSetChanged();

                    dialogCreate.dismiss();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogCreate.show();

    }


    public void dialogEdit(final String nis, final String nama){
        dialogCreate  = new Dialog(MainActivity.this);
        dialogCreate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCreate.setContentView(R.layout.dialog_edit);
        dialogCreate.setCanceledOnTouchOutside(false);
        metricsCreate = getResources().getDisplayMetrics();
        int width     = metricsCreate.widthPixels;
        dialogCreate.getWindow().setLayout((3 * width / 3), LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText nisEdit   = dialogCreate.findViewById(R.id.etNisEdit);
        final EditText namaEdit  = dialogCreate.findViewById(R.id.etNamaEdit);
        Button simpan  = dialogCreate.findViewById(R.id.btnSimpanEdit);

        nisEdit.setText(nis);
        namaEdit.setText(nama);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    tulis.execSQL("UPDATE tb_user SET nama = '"+namaEdit.getText()+"', nis = '"+nisEdit.getText()+"' " +
                            "WHERE nis = '"+nis+"'");
                    Toast.makeText(MainActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                    userList.clear();
                    dataUser();
                    userAdapter.notifyDataSetChanged();

                    dialogCreate.dismiss();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogCreate.show();

    }

    public void dialogHapus(final String nis){
        alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder
                .setTitle("Peringatan!")
                .setMessage("Apakah Anda akan menghapus nya?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            tulis.execSQL("DELETE FROM tb_user WHERE nis ='"+nis+"'");
                            Toast.makeText(MainActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                            userList.clear();
                            dataUser();
                            userAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userList.clear();
                        dataUser();
                        userAdapter.notifyDataSetChanged();
                    }
                });
        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

}
