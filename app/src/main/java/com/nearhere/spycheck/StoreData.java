package com.nearhere.spycheck;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Created by lokeshmutyala on 15-09-2017.
 */
@Entity
public interface StoreData {
    @Key
    @Generated
    public int getId();

    public String getStoreId();

    public String getTime();

    public Double getLatitude();

    public Double getLongitude();

    public Float getAccuracy();

    public String getStoreName();

    public String getStoreType();

    public int getPrice();

    public boolean getRA_ITC();

    public boolean getRA_VST();

    public boolean getRA_Marlboro();

    public boolean getShelf_ITC();

    public boolean getShelf_VST();

    public boolean getShelf_Marlboro();

    public boolean getPSU_ITC();

    public boolean getPSU_VST();

    public boolean getPSU_Marlboro();

    public boolean getGlow_ITC();

    public boolean getGlow_VST();

    public boolean getGlow_Marlboro();

    public boolean getNonlit_ITC();

    public boolean getNonlit_VST();

    public boolean getNonlit_Marlboro();

    public boolean getSyncflag();

    public String getVideoDuration();

    public boolean getIsSellCigarette();

    public boolean getIsSellBristole();

    public String getCamDuration();

    public boolean getIsSellCharms();

    public int getPriceCharms();

    public boolean getDalla_ITC();

    public boolean getDalla_VST();

    public boolean getDalla_Marlboro();

    public boolean getTv_ITC();

    public boolean getTv_VST();

    public boolean getTv_Marlboro();

    public String getLandMark();

    public String getStoreCat();



}
