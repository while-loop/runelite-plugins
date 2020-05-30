package com.ttl;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateMethod {
    int level;
    int rate;
    String method;

    public RateMethod(RateMethod ri) {
        level = ri.level;
        rate = ri.rate;
        method = ri.method;
    }
}
