package com.dying.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author daylight
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String email;
    private String code;

}
