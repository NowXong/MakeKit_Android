package com.example.makekit.makekit_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makekit.R;
import com.example.makekit.makekit_adapter.CartAdapter;
import com.example.makekit.makekit_asynctask.CartNetworkTask;
import com.example.makekit.makekit_bean.Cart;
import com.example.makekit.makekit_sharVar.SharVar;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements OnChangedPrice{

    TextView orderTotalNext, productTotal, productDeliveryTotalPrice, allProductTotalPrice;
    CheckBox selectAll, itemSelect;
    String macIP, productNo, productQuantity, totalPrice, cartNo, urlAddrBase, urlAddr;
    DecimalFormat myFormatter;
    ArrayList<Cart> carts;
    ArrayList<String> productNums;
    Button btnDelete;
    ImageView btnHome;

    CartAdapter cartAdapter;

    private RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;

    final static String TAG = "CartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cart);

        orderTotalNext = findViewById(R.id.tv_total_payment_cart);
        btnDelete = findViewById(R.id.btn_cart_delete);
        productTotal = findViewById(R.id.productTotalPrice_cart);
        productDeliveryTotalPrice = findViewById(R.id.productDeliveryTotalPrice_cart);
        allProductTotalPrice = findViewById(R.id.allProductTotalPrice_cart);
        recyclerView = findViewById(R.id.recyclerViewCartList);
        selectAll = findViewById(R.id.cb_cart_selectall);
        btnHome = findViewById(R.id.img_home_cart);

        Intent intent = getIntent();
        //macIP = intent.getStringExtra("macIP");

        /////////////////////////////////////////
        // 1/17 kyeongmi productNo ????????????
        /////////////////////////////////////////
        //productNo = intent.getStringExtra("productNo");

        cartNo = intent.getStringExtra("cartNo");
        //productQuantity = intent.getStringExtra("productQuantity");
        //totalPrice = intent.getStringExtra("totalPrice");

        urlAddrBase = SharVar.urlAddrBase;
//        urlAddrBase = "http://" + macIP + ":8080/makeKit/";
        urlAddr = urlAddrBase + "jsp/select_usercart_all.jsp?cartno=" + cartNo;
        Log.v(TAG, "??????" + urlAddr);
        connectSelectData(urlAddr);
//        Log.v(TAG, "?????????" + carts.get(0).getTotalPrice());

        Log.v(TAG, "????????? : " + cartAdapter.checkBoxCheckedReturn());

        if(carts.size() == 0){
            orderTotalNext.setClickable(false);
        }

        orderTotalNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for(int i=0; i<cartAdapter.checkBoxCheckedReturn().size(); i++){
//                    String no = cartAdapter.checkBoxCheckedReturn().get(i).getProductNo();
//                    Log.v(TAG, "?????? : " + no);
////                    productNums.add(no);
//                }
                if(carts.size() != 0) {
                    Log.v(TAG, "???????????? : " + cartAdapter.checkBoxCheckedReturn().size());
                    Log.v(TAG, "?????? ????????? : " + carts.size());
                    if (cartAdapter.checkBoxCheckedReturn().size() == 0) {
                        Toast.makeText(CartActivity.this, "???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    } else {
                       // Toast.makeText(CartActivity.this, "?????? ??????????????????.", Toast.LENGTH_SHORT).show();

                    Intent intent1 = new Intent(CartActivity.this, OrderActivity.class);
                    intent1.putExtra("macIP", macIP);
                    intent1.putExtra("cartNo", cartNo);
                    intent1.putExtra("productno", cartAdapter.checkBoxCheckedReturn());
                    startActivity(intent1);
                    }
                } else {
                    Toast.makeText(CartActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    orderTotalNext.setClickable(false);
                    orderTotalNext.setBackgroundColor(getResources().getColor(R.color.gray));
                    orderTotalNext.setTextColor(getResources().getColor(R.color.black));

                }
            }
        });

        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (selectAll.isChecked()){
                    cartAdapter.checkBoxOperation(true);

                }else {
                    cartAdapter.checkBoxOperation(false);
                }

            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carts.size() != 0) {
                    cartAdapter.connectDeleteData();
                    connectSelectData(urlAddr);
                    productTotal.setText("0???");
                    productDeliveryTotalPrice.setText("0???");
                    allProductTotalPrice.setText("0???");

                    orderTotalNext.setText("????????????");
                    orderTotalNext.setBackgroundColor(getResources().getColor(R.color.gray));
                    orderTotalNext.setTextColor(getResources().getColor(R.color.black));
                    selectAll.setChecked(false);
                } else {
                    btnDelete.setClickable(false);
                }


//                cartAdapter.checkBoxCheckedReturn();
//                Log.v(TAG, "?????? : " + cartAdapter.checkBoxCheckedReturn().get(0).getProductNo());
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });

    }

    // select cart
    private void connectSelectData(String urlAddr) {
        try {
            CartNetworkTask cartNetworkTask = new CartNetworkTask(CartActivity.this, urlAddr, "select");

            Object object = cartNetworkTask.execute().get();
            carts = (ArrayList<Cart>) object;

            cartAdapter = new CartAdapter(CartActivity.this, R.layout.custom_cart_layout, carts, urlAddrBase, macIP, this);
            recyclerView.setAdapter(cartAdapter);
            recyclerView.setHasFixedSize(true); // ?????????????????? ???????????? ??????
            layoutManager = new LinearLayoutManager(CartActivity.this);
            recyclerView.setLayoutManager(layoutManager);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        selectAll.setChecked(false);
        String productprice ="";
        String productcount ="";
        String deliveryprice ="";
        int price = 0;
        super.onResume();
        Log.v(TAG, "onResume cart");
        connectSelectData(urlAddr);

        for (int i=0; i<carts.size(); i++){
            productprice = carts.get(i).getProductPrice();
            productcount = carts.get(i).getCartQuantity();
            price += Integer.parseInt(productprice) * Integer.parseInt(productcount);
            Log.v(TAG, "price : " + String.valueOf(price));

        }
        myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(price);
        String formattedStringPrice1 = myFormatter.format(carts.size() * 2500);
        String formattedStringPrice2 = myFormatter.format(carts.size() * 2500 + price);

        productTotal.setText("0???");
        productDeliveryTotalPrice.setText("0???");
        allProductTotalPrice.setText("0???");

        orderTotalNext.setText("????????????");
        orderTotalNext.setBackgroundColor(getResources().getColor(R.color.gray));
        orderTotalNext.setTextColor(getResources().getColor(R.color.black));



//        if(carts.size() > cartAdapter.checkBoxCheckedReturn().size()){
//            selectAll.setChecked(false);
//        }

//        Intent intent = getIntent();
//
//        String pricecheck = intent.getStringExtra("price");
//        Log.v(TAG, "price2 : " + pricecheck);

    }

    @Override
    public void changedPrice(int productTotalPrice, int deliveryPrice, int totalPrice) {
        if(productTotalPrice == 0) {
            productTotal.setText("0???");
            productDeliveryTotalPrice.setText("0???");
            allProductTotalPrice.setText("0???");
            orderTotalNext.setText("????????????");
            orderTotalNext.setBackgroundColor(getResources().getColor(R.color.gray));
            orderTotalNext.setTextColor(getResources().getColor(R.color.black));
        } else {
            myFormatter = new DecimalFormat("###,###");
            String formattedStringPrice = myFormatter.format(productTotalPrice);
            String formattedStringPrice1 = myFormatter.format(deliveryPrice);
            String formattedStringPrice2 = myFormatter.format(totalPrice);
            Log.v(TAG, "?????? ???????????? ????????? ?????????!!!");
            productTotal.setText(formattedStringPrice + "???");
            productDeliveryTotalPrice.setText(formattedStringPrice1 + "???");
            allProductTotalPrice.setText(formattedStringPrice2 + "???");
            orderTotalNext.setText("??? " + formattedStringPrice2 + "??? ????????????");
            orderTotalNext.setBackgroundColor(getResources().getColor(R.color.brown));
            orderTotalNext.setTextColor(getResources().getColor(R.color.white));
        }
    }
}