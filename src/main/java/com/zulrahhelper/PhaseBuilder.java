package com.zulrahhelper;


import com.zulrahhelper.options.Attack;
import com.zulrahhelper.options.OverheadProtection;

import static com.zulrahhelper.options.OverheadProtection.PROTECT_FROM_MAGIC;
import static com.zulrahhelper.options.OverheadProtection.PROTECT_FROM_MISSILES;

public class PhaseBuilder {
    private int number;
    private Phase.Rotation rotation;
    private Attack[] attacks = new Attack[3];
    private OverheadProtection[] prayers = new OverheadProtection[2];

    public Phase build() {
        return new Phase(rotation, number, prayers, attacks);
    }

    public PhaseBuilder setRotation(Phase.Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    public PhaseBuilder setNumber(int number) {
        this.number = number;
        return this;
    }

    public PhaseBuilder addAttack(Attack attack) {
        switch (attack.getSource()) {
            case NORMAL:
                this.attacks[0] = attack;
                break;
            case VENOM:
                this.attacks[1] = attack;
                break;
            case SNAKELING:
                this.attacks[2] = attack;
                break;
        }
        return this;
    }

    public PhaseBuilder setRangedPray() {
        setPrayer(PROTECT_FROM_MISSILES);
        return this;
    }

    public PhaseBuilder setMagePray() {
        setPrayer(PROTECT_FROM_MAGIC);
        return this;
    }

    private void setPrayer(OverheadProtection protect) {
        if (prayers[0] == null) {
            prayers[0] = protect;
        } else  {
            prayers[1] = protect;
        }
    }
}
