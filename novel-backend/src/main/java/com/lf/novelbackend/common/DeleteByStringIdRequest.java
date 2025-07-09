package com.lf.novelbackend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteByStringIdRequest implements Serializable {

    /**
     * id
     */
    private String id;

    private static final long serialVersionUID = 1L;
}

