package com.ttl;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.Skill;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class Rates {
    private String skill;
    private ArrayList<RateMethod> methods;

    public Rates(Rates r) {
        skill = r.skill;
        methods = new ArrayList<>(r.methods.size());
        r.methods.forEach(ri -> methods.add(new RateMethod(ri)));
    }

    public Skill getRSSkill(){
        return Skill.valueOf(skill.toUpperCase());
    }
}
