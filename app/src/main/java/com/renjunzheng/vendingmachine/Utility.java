package com.renjunzheng.vendingmachine;

/**
 * Created by Renjun Zheng on 2016/1/30.
 */
public class Utility {
    /**
     * Helper method to provide the art resource id
     * @param merchandiseName from server response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForMerchandise(String merchandiseName) {

        if(merchandiseName.equals("hershey"))
            return R.drawable.hershey;
        else if(merchandiseName.equals("kitkat"))
            return R.drawable.kitkat;
        else if(merchandiseName.equals("reeses"))
            return R.drawable.reeses;
        else if(merchandiseName.equals("snickers"))
            return R.drawable.snickers;
        else
            return 0;
    }
}
