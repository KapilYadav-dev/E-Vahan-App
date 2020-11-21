package in.kay.evahaan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    CircularProgressButton btn;
    EditText carNum;
    TextView tvDaysLeftInsurance, tvNumber, tvpetrol, tvOwnerName, tvModel, tvClass, tvRegDate, tvExpDate, tvChasis, tvEngine, tvRegAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initz();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(carNum.getText().toString()) && carNum.getText().toString().length() > 7) {
                    btn.startAnimation();
                    if (Paper.book("LocalDb").read(carNum.getText().toString().trim())==null)
                    {
                        Toast.makeText(MainActivity.this, "From Server", Toast.LENGTH_SHORT).show();
                        DoWork(carNum.getText().toString().trim());
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "From LocalDB", Toast.LENGTH_SHORT).show();
                        LoadDataFromLocalDB();
                    }


                } else {
                    carNum.setError("Please enter a valid Car-Number");
                    carNum.requestFocus();
                }
            }
        });
    }

    private void LoadDataFromLocalDB() {
        List<PaperDbModel> list= Paper.book("LocalDb").read(carNum.getText().toString().trim());
        String Owner_Name = list.get(0).getOwnerName();
        String Registration_Number =list.get(0).getRegisterationNumber();
        String Model =list.get(0).getModel();
        String Class = list.get(0).getClass_();
        String Fuel_Type =list.get(0).getFuelType();
        String Reg_Date =list.get(0).getRegDate();
        String Chassis_Number =list.get(0).getChassisNumber();
        String Engine_Number =list.get(0).getEngineNumber();
        String Fitness_Upto =list.get(0).getFitnessUpto();
        String Insurance_expiry = list.get(0).getInsuranceExpiry();
        String Registering_Authority = list.get(0).getRegisteringAuthority();
        ShowUI();
        DoneUIWork(Owner_Name, Registration_Number, Model, Class, Fuel_Type, Reg_Date, Chassis_Number, Engine_Number, Fitness_Upto, Insurance_expiry, Registering_Authority);
    }

    private void SaveToHistory() {
        List<String> list = Paper.book("History").getAllKeys();
        Integer size = list.size();
        Paper.book("History").write(Integer.toString(size), carNum.getText().toString().trim());
        for (int i = 0; i < list.size(); i++) {

        }
    }

    private void Initz() {
        DateTimeUtils.setTimeZone("UTC");
        btn = (CircularProgressButton) findViewById(R.id.btn_id);
        carNum = findViewById(R.id.et_num);
        tvDaysLeftInsurance = findViewById(R.id.tv_days_left_insurance);
        tvNumber = findViewById(R.id.tv_number);
        tvpetrol = findViewById(R.id.tvpetrol);
        tvRegAuth = findViewById(R.id.tvRegAuth);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvModel = findViewById(R.id.tvModel);
        tvClass = findViewById(R.id.tvClass);
        tvRegDate = findViewById(R.id.tvRegDate);
        tvChasis = findViewById(R.id.tvChasis);
        tvExpDate = findViewById(R.id.tvExpDate);
        tvEngine = findViewById(R.id.tvEngine);
    }

    private void DoWork(String string) {
        Call<ResponseBody> call = RetrofitClient.getInstance().getApi().search(string);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        SaveToHistory();
                        btn.revertAnimation();
                        String str = response.body().string();
                        JSONObject object = new JSONObject(str);
                        String Owner_Name = object.getString("Owner Name");
                        String Registration_Number = object.getString("Registeration Number");
                        String Model = object.getString("Model");
                        String Class = object.getString("Class");
                        String Fuel_Type = object.getString("Fuel Type");
                        String Reg_Date = object.getString("Reg Date");
                        String Chassis_Number = object.getString("Chassis Number");
                        String Engine_Number = object.getString("Engine Number");
                        String Fitness_Upto = object.getString("Fitness Upto");
                        String Insurance_expiry = object.getString("Insurance expiry");
                        String Registering_Authority = object.getString("Registering Authority");
                        ShowUI();
                        SaveUserDataLocally(Owner_Name, Registration_Number, Model, Class, Fuel_Type, Reg_Date, Chassis_Number, Engine_Number, Fitness_Upto, Insurance_expiry, Registering_Authority);
                        DoneUIWork(Owner_Name, Registration_Number, Model, Class, Fuel_Type, Reg_Date, Chassis_Number, Engine_Number, Fitness_Upto, Insurance_expiry, Registering_Authority);
                    } else {
                        btn.stopAnimation();
                        btn.revertAnimation();
                        btn.recoverInitialState();
                        carNum.setError("No Result Found...");
                        carNum.requestFocus();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    btn.revertAnimation();
                    btn.setText("Error " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void SaveUserDataLocally(String owner_name, String registration_number, String model, String aClass, String fuel_type, String reg_date, String chassis_number, String engine_number, String fitness_upto, String insurance_expiry, String registering_authority) {
        List<PaperDbModel> list = new ArrayList<>();
        PaperDbModel paperDbModel = new PaperDbModel();
        paperDbModel.setOwnerName(owner_name);
        paperDbModel.setRegisterationNumber(registration_number);
        paperDbModel.setModel(model);
        paperDbModel.setClass_(aClass);
        paperDbModel.setFuelType(fuel_type);
        paperDbModel.setRegDate(reg_date);
        paperDbModel.setChassisNumber(chassis_number);
        paperDbModel.setEngineNumber(engine_number);
        paperDbModel.setFitnessUpto(fitness_upto);
        paperDbModel.setInsuranceExpiry(insurance_expiry);
        paperDbModel.setRegisteringAuthority(registering_authority);
        list.add(paperDbModel);
        Paper.book("LocalDb").write(carNum.getText().toString().trim(), list);
    }

    private void DoneUIWork(String owner_Name, String registration_Number, String model, String aClass, String fuel_Type, String reg_Date, String chassis_Number, String engine_Number, String fitness_Upto, String insurance_expiry, String registering_Authority) {
        tvRegAuth.setText(registering_Authority);
        tvOwnerName.setText(owner_Name);
        tvModel.setText(model);
        tvClass.setText(aClass);
        tvRegDate.setText(reg_Date);
        tvChasis.setText(chassis_Number);
        tvExpDate.setText(fitness_Upto);
        tvEngine.setText(engine_Number);
        tvInsuranceFN(insurance_expiry);
        tvCarRegFN(registration_Number);
        try {
            PetrolPriceFN(registering_Authority);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    private void PetrolPriceFN(String string) throws IOException, JSONException {
        String result = string.substring(string.lastIndexOf(',') + 1).trim();
        Call<List<ResponseModel>> call = RetrofitPetrolClient.getInstance().getApi().petrol(result);
        call.enqueue(new Callback<List<ResponseModel>>() {
            @Override
            public void onResponse(Call<List<ResponseModel>> call, Response<List<ResponseModel>> response) {
                if (response.isSuccessful()) {
                    List<ResponseModel> list = response.body();
                    String price = list.get(0).getProducts().get(0).getProductPrice();
                    tvpetrol.setText("₹" + price + " / L");
                } else {
                    tvpetrol.setText("N/A");
                }
            }

            @Override
            public void onFailure(Call<List<ResponseModel>> call, Throwable t) {

            }
        });


    }

    private void tvCarRegFN(String registration_number) {
        int length = registration_number.length();
        StringBuilder sb = new StringBuilder(registration_number)
                .insert(length - 4, "-")
                .insert(2, "-");
        tvNumber.setText(sb.toString());
    }

    private void tvInsuranceFN(String insurance_expiry) {
        Date date = new Date();
        String ExpMonth = insurance_expiry.replaceAll("[-0-9\\s]", "");
        Integer strExpMonth = null;
        try {
            strExpMonth = GetDate(ExpMonth);
        } catch (ParseException e) {
            Toast.makeText(this, "Error  is " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        String strExpYear = insurance_expiry.substring(insurance_expiry.lastIndexOf('-') + 1).trim();
        String strExpDay = insurance_expiry.substring(0, 2);
        String strExpDate = strExpYear + "-" + strExpMonth + "-" + strExpDay;
        String date2 = strExpDate + " 00:00:00";
        int diff = DateTimeUtils.getDateDiff(date2, date, DateTimeUnits.DAYS);
        if (diff > 0)
            tvDaysLeftInsurance.setText(diff + " days left");
        else {
            tvDaysLeftInsurance.setText("Expired");
        }
    }

    private int GetDate(String insurance_expiry) throws ParseException {
        Date date = new SimpleDateFormat("MMM").parse(insurance_expiry);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    private void ShowUI() {
        findViewById(R.id.rl).setVisibility(View.VISIBLE);
        findViewById(R.id.awesomeTextInputLayout).setVisibility(View.GONE);
        findViewById(R.id.btn_id).setVisibility(View.GONE);
        findViewById(R.id.iv).setVisibility(View.GONE);
    }

    public void ResetView(View view) {
        btn.stopAnimation();
        btn.revertAnimation();
        findViewById(R.id.awesomeTextInputLayout).clearFocus();
        findViewById(R.id.rl).setVisibility(View.GONE);
        findViewById(R.id.awesomeTextInputLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_id).setVisibility(View.VISIBLE);
        findViewById(R.id.iv).setVisibility(View.VISIBLE);
    }

    public void ShowHistory(View view) {

    }
}