package ru.ssau.tk._repfor2lab_._OOP_.entities;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "simple_functions")
public class SimpleFunctions {
    @Id
    @Column(name = "local_name", length = 128, nullable = false)
    private String localName;
    @OneToMany(mappedBy = "simpleFunctions")
    private List<MathFunctions> mathFunctions;

    public SimpleFunctions(){}
    public SimpleFunctions(String localName){
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
