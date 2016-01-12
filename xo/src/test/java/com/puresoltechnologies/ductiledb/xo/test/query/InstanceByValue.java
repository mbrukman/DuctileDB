package com.puresoltechnologies.ductiledb.xo.test.query;

import com.puresoltechnologies.ductiledb.xo.api.annotation.Query;

@Query(value = "g.V().has('_xo_discriminator_A').has('value', {value})", name = "a")
public interface InstanceByValue {

    A getA();

}
