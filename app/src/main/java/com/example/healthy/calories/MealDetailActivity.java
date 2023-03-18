package com.example.healthy.calories;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.R;
import com.example.healthy.adapter.AdapterAddFood;
import com.example.healthy.base.BaseActivity;
import com.example.healthy.databinding.ActivityMealDetailBinding;
import com.example.healthy.model.Food;
import com.example.healthy.model.Meal;
import com.example.healthy.model.Protein;
import com.example.healthy.network.ApiUtils;
import com.example.healthy.network.response.CaloriesResponse;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.untils.Constants;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import cpf.defnotification.DefNotification;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailActivity extends BaseActivity {
    ActivityMealDetailBinding binding;
    List<Food> list;
    AdapterAddFood adapterAddFood;
    private String kcalNeed;
    DbHelper dbHelper;
    private String type;
    private float protein = 0;
    private float fat = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meal_detail);
        list = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        dbHelper = new DbHelper(this);
        adapterAddFood = new AdapterAddFood(list);
        binding.rc.setAdapter(adapterAddFood);
        kcalNeed = getIntent().getStringExtra("Kcal");
        type = getIntent().getStringExtra("Title");
        count = Float.parseFloat(dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).energy);
        binding.tvKalo.setText(count + "kcal");
        if (type.equals(Constants.TYPE_MORNING)) {
            binding.tvTitile.setText("Bữa sáng");
        } else if (type.equals(Constants.TYPE_LUNCH)) {
            binding.tvTitile.setText("Bữa trưa");
        } else if (type.equals(Constants.TYPE_DINNER)) {
            binding.tvTitile.setText("Bữa tối");
        }
        if (dbHelper.getProtein() == null ||
                !dbHelper.getProtein().date.equals(dtf.format(now))
        ) {
            dbHelper.addProtein(new Protein("0", "0", "0", dtf.format(now)));
        }
        binding.tvSuggest.setText(" Bạn nên nạp " + kcalNeed + " kcal/ngày");
        binding.imgAdd.setOnClickListener(view -> {
            showDialog();
        });
        if (dbHelper.getMeal() != null || dbHelper.getMeal().size() > 0) {
            for (int i = 0; i < dbHelper.getMeal().size(); i++) {
                if (dbHelper.getMeal().get(i).type.equals(type)) {
                    list.add(new Food(dbHelper.getMeal().get(i).name, Float.parseFloat(dbHelper.getMeal().get(i).kcal)));
                }
            }
        }
        adapterAddFood.updateAdapter(list);

    }

    private void showDialog() {
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.addfooddialog, null);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        dialogBuilder.setView(view);
        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_login);
        alertDialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        RelativeLayout rlCancel = alertDialog.findViewById(R.id.rl_cancel);
        RelativeLayout rlok = alertDialog.findViewById(R.id.rlChange);
        EditText edt = alertDialog.findViewById(R.id.edtConten);
        rlCancel.setOnClickListener(view1 -> {
            alertDialog.dismiss();
        });
        rlok.setOnClickListener(view1 -> {
            TranslateAPI translateAPI = new TranslateAPI(
                    Language.VIETNAMESE,   //Source Language
                    Language.ENGLISH,         //Target Language
                    edt.getText().toString());
            translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                @Override
                public void onSuccess(String translatedText) {
                    getCalo(translatedText, alertDialog, edt.getText().toString());
                }

                @Override
                public void onFailure(String ErrorText) {
                    Log.d("KMFG", "onFailure: " + ErrorText);
                }
            });

        });

    }

    float count = 0;

    public void getCalo(String name, AlertDialog alertDialog, String nameSub) {

        ApiUtils.getApiServiceCalories().getCalories(name).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CaloriesResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        showProgressDialog(true);
                    }

                    @Override
                    public void onSuccess(@NonNull CaloriesResponse caloriesResponse) {
                        showProgressDialog(false);
                        if (caloriesResponse.totalNutrients.fat != null) {
                            fat = Float.parseFloat(dbHelper.getProtein().fat) + caloriesResponse.totalNutrients.fat.quantity;
                            protein = Float.parseFloat(dbHelper.getProtein().protein) + caloriesResponse.totalNutrients.protein.quantity;
                            dbHelper.addMeal(new Meal(type, nameSub, caloriesResponse.calories + ""));
                            list.add(new Food(nameSub, caloriesResponse.calories));
                            adapterAddFood.updateAdapter(list);

                            for (int i = 0; i < list.size(); i++) {
                                count = count + list.get(i).getCalories();
                            }
                            dbHelper.updateHealthy(Constants.ENERGY, count + "", dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).id);
                            binding.tvKalo.setText(count + " kcal");
                            dbHelper.updateProtein(protein + "", "" + fat, "" + count, dbHelper.getProtein().id);

                        } else {
                            DefNotification defNotification = new DefNotification(MealDetailActivity.this)
                                    .setContentView(R.layout.customtoast)
                                    .setDuration(4000);

                            TextView title = defNotification.findViewById(R.id.tv_toast);
                            title.setText("Vui lòng nhập số lượng!!");
                            defNotification.show();
                        }

                        alertDialog.dismiss();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showProgressDialog(false);
                        alertDialog.dismiss();
                        Log.d("kmfg", e.getLocalizedMessage());

                    }
                });
    }
}