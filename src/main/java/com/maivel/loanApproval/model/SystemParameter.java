package com.maivel.loanApproval.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "system_parameter")
public class SystemParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parameterId;

    @Column(name = "parameter_key", nullable = false)
    private String parameterKey;

    @Column(name = "parameter_value", nullable = false)
    private String parameterValue;

    public static SystemParameter create(String parameterKey, String parameterValue) {
        SystemParameter systemParameter = new SystemParameter();
        systemParameter.parameterKey = parameterKey;
        systemParameter.parameterValue = parameterValue;
        return systemParameter;
    }
}
