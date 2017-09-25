package com.nearhere.spycheck;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Created by lokeshmutyala on 22-09-2017.
 */
@Entity
public interface GpsCoordinated {
    @Key
    @Generated
    public int getId();

    public Double getLatitude();

    public Double getLongitude();

    public float getAccuracy();

    public String getAgentId();

    public String getcaptureTime();
}
