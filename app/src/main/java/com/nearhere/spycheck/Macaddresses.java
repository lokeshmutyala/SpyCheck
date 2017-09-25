package com.nearhere.spycheck;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Created by lokeshmutyala on 19-09-2017.
 */
@Entity
public interface Macaddresses {
    @Key
    @Generated
    public int getId();

    public String getMacAddress();

}
