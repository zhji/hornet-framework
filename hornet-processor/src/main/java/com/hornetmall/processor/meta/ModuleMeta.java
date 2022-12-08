package com.hornetmall.processor.meta;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ModuleMeta {
    private String name;
}
