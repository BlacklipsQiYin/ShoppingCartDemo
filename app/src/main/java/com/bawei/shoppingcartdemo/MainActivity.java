package com.bawei.shoppingcartdemo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    private TextView bianJI;
    private TextView heJi;
    private TextView jieSuan;
    private TextView quanXuan;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bianJI = (TextView) findViewById(R.id.bianJI);
        heJi = (TextView) findViewById(R.id.shopping_HeJi);
        jieSuan = (TextView) findViewById(R.id.jieSuan);
        quanXuan = (TextView) findViewById(R.id.quanXuan);
        recyclerView = (RecyclerView) findViewById(R.id.shopping_RecyclerView);

        showData();


    }


    //存放购物车中所有的商品
    private List<ShopBean.OrderDataBean.CartlistBean> mAllOrderList = new ArrayList<>();

    private void showData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        ListViewAdapter adapter = new ListViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);


        try{
            InputStream inputStream =  getAssets().open("shop.json");
            String data =  StringUtils.convertStreamToString(inputStream);
            Gson gson = new Gson();
            ShopBean shopBean =  gson.fromJson(data, ShopBean.class);


            for(int i=0;i<shopBean.getOrderData().size();i++){
                int length = shopBean.getOrderData().get(i).getCartlist().size() ;
                for(int j=0;j<length;j++){
                    mAllOrderList.add(shopBean.getOrderData().get(i).getCartlist().get(j));
                }
            }
            setFirstState(mAllOrderList);

            adapter.setData(mAllOrderList);
        }catch (Exception e){
            e.printStackTrace();
        }


        //删除数据回调
        adapter.setOnDeleteClickListener(new ListViewAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position, int cartid) {

            }
        });



        //
        adapter.setOnRefershListener(new ListViewAdapter.OnRefershListener() {
            @Override
            public void onRefersh(boolean isSelect,List<ShopBean.OrderDataBean.CartlistBean> list) {

                //标记底部 全选按钮
                if(isSelect){
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_selected);
                    quanXuan.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                }else {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);
                    quanXuan.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                }

                //总价
                float mTotlaPrice = 0f;
                int mTotalNum = 0;
                for(int i=0;i<list.size();i++){
                    if(list.get(i).isSelect()){
                        mTotlaPrice += list.get(i).getPrice() * list.get(i).getCount() ;
                        mTotalNum += list.get(i).getCount();
                    }
                }
                System.out.println("mTotlaPrice = " + mTotlaPrice);

                heJi.setText("合计 : " + mTotlaPrice  );

                jieSuan.setText("共" + mTotalNum + "件商品");
            }
        });




    }

    /**
     * 标记第一条数据 isfirst 1 显示商户名称 2 隐藏
     * @param list
     */
    public static void setFirstState(List<ShopBean.OrderDataBean.CartlistBean> list){

        if(list.size() > 0){
            list.get(0).setIsFirst(1);
            for(int i=1;i<list.size();i++){

                if(list.get(i).getShopId() == list.get(i-1).getShopId()){
                    list.get(i).setIsFirst(2);
                }else {
                    list.get(i).setIsFirst(1);
                }
            }
        }

    }



}
