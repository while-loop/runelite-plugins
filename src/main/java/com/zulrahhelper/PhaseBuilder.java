package com.zulrahhelper;


import com.zulrahhelper.options.Attack;
import com.zulrahhelper.options.OverheadProtection;

public class PhaseBuilder {
    private int number;
    private Phase.Rotation rotation;
    private Attack[] attacks = {Attack.NONE, Attack.NONE, Attack.NONE};
    private OverheadProtection[] prayers = {OverheadProtection.NONE, OverheadProtection.NONE};

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
        setPrayer(OverheadProtection.PROTECT_FROM_MISSILES);
        return this;
    }

    public PhaseBuilder setMagePray() {
        setPrayer(OverheadProtection.PROTECT_FROM_MAGIC);
        return this;
    }

    private void setPrayer(OverheadProtection protect) {
        if (prayers[0] == OverheadProtection.NONE) {
            prayers[0] = protect;
        } else {
            prayers[1] = protect;
        }
    }
}
