package com.zulrahhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zulrahhelper.options.Attack.*;

public class State {
    private Phase selectedPhase = START_PHASE;

    public int getNumber() {
        return selectedPhase.getNumber();
    }

    public Phase.Rotation getRotation() {
        return selectedPhase.getRotation();
    }

    /**
     * Set phase number until a choice is needed
     *
     * @param phase
     */
    public void setPhase(Phase phase) {
        this.selectedPhase = phase.copy();
        if (phase.getRotation() == Phase.Rotation.MAGMA && phase.getNumber() == 2) {
            // auto skip to the next magna phase since no input is required.
            this.selectedPhase = MAGMA.get(2).get(0);
        }
    }

    public Phase getPhase() {
        return this.selectedPhase;
    }

    public void reset() {
        this.selectedPhase = START_PHASE;
    }

    @Override
    public String toString() {
        return "State{" +
                ", selectedPhase=" + selectedPhase.toString() +
                '}';
    }

    public List<List<Phase>> buildTree() {
        List<List<Phase>> tree = getRotationTree();
        for (List<Phase> phases : tree) {
            if (phases.size() <= 0) {
                break;
            }

            for (Phase p : phases) {
                p.setStates(this);
            }

            if (phases.get(0).getNumber() > getNumber() + 1) {
                if (phases.size() > 1) {
                    tree = tree.subList(0, tree.indexOf(phases) + 1);
                    break;
                }
            }
        }

        return tree;
    }

    public List<List<Phase>> getRotationTree() {
        List<List<Phase>> tree = new ArrayList<>();
        switch (selectedPhase.getRotation()) {
            case START:
                tree = START;
                break;
            case NORMAL:
                tree = NORMAL;
                break;
            case MAGMA:
                tree = MAGMA;
                break;
            case MAGMA_A:
                tree = MAGMA_A;
                break;
            case MAGMA_B:
                tree = MAGMA_B;
                break;
            case TANZ:
                tree = TANZ;
        }
        return tree;
    }

    // -dark-pray-attack.png

    public static final Phase START_PHASE = Phase.builder(Phase.Rotation.START, 1).addAttack(NORMAL_5).addAttack(VENOM_4).build();
    List<List<Phase>> START = Arrays.asList(
            Collections.singletonList(START_PHASE),
            Arrays.asList(
                    Phase.builder(Phase.Rotation.MAGMA, 2).addAttack(NORMAL_2).build(),
                    Phase.builder(Phase.Rotation.NORMAL, 2).addAttack(NORMAL_5).addAttack(SNAKELING_3).setRangedPray().build(),
                    Phase.builder(Phase.Rotation.TANZ, 2).addAttack(NORMAL_6).setMagePray().build()
            )
    );
    List<List<Phase>> MAGMA = Arrays.asList(
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA, 1).addAttack(NORMAL_5).addAttack(VENOM_4).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA, 2).addAttack(NORMAL_2).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA, 3).addAttack(NORMAL_4).setMagePray().build()),
            Arrays.asList(
                    Phase.builder(Phase.Rotation.MAGMA_A, 4).addAttack(NORMAL_5).addAttack(VENOM_2).addAttack(SNAKELING_4).setRangedPray().build(),
                    Phase.builder(Phase.Rotation.MAGMA_B, 4).addAttack(VENOM_4).addAttack(SNAKELING_4).build()
            )
    );
    List<List<Phase>> MAGMA_A = Arrays.asList(
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 1).addAttack(NORMAL_5).addAttack(VENOM_4).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 2).addAttack(NORMAL_2).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 3).addAttack(NORMAL_4).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 4).addAttack(NORMAL_5).addAttack(VENOM_2).addAttack(SNAKELING_4).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 5).addAttack(NORMAL_2).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 6).addAttack(NORMAL_2).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 7).addAttack(VENOM_3).addAttack(SNAKELING_4).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 8).addAttack(NORMAL_5).addAttack(VENOM_2).addAttack(SNAKELING_3).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 9).addAttack(NORMAL_10).addAttack(VENOM_4).setRangedPray().setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_A, 10).build())
    );
    List<List<Phase>> MAGMA_B = Arrays.asList(
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 1).addAttack(NORMAL_5).addAttack(VENOM_4).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 2).addAttack(NORMAL_2).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 3).addAttack(NORMAL_4).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 4).addAttack(VENOM_3).addAttack(SNAKELING_4).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 5).addAttack(NORMAL_5).addAttack(VENOM_2).addAttack(SNAKELING_4).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 6).addAttack(NORMAL_2).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 7).addAttack(NORMAL_5).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 8).addAttack(NORMAL_5).addAttack(VENOM_2).addAttack(SNAKELING_3).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 9).addAttack(NORMAL_10).addAttack(VENOM_4).setRangedPray().setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.MAGMA_B, 10).addAttack(NORMAL_2).build())
    );
    List<List<Phase>> NORMAL = Arrays.asList(
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 1).addAttack(NORMAL_5).addAttack(VENOM_4).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 2).addAttack(NORMAL_5).addAttack(SNAKELING_3).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 3).addAttack(NORMAL_2).addAttack(VENOM_3).addAttack(SNAKELING_3).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 4).addAttack(NORMAL_5).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 5).addAttack(NORMAL_5).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 6).addAttack(NORMAL_5).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 7).addAttack(VENOM_3).addAttack(SNAKELING_3).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 8).addAttack(NORMAL_5).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 9).addAttack(NORMAL_5).addAttack(VENOM_2).addAttack(SNAKELING_3).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 10).addAttack(NORMAL_10).setMagePray().setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.NORMAL, 11).addAttack(SNAKELING_4).build())
    );
    List<List<Phase>> TANZ = Arrays.asList(
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 1).addAttack(NORMAL_5).addAttack(VENOM_4).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 2).addAttack(NORMAL_6).addAttack(SNAKELING_4).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 3).addAttack(NORMAL_4).addAttack(VENOM_2).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 4).addAttack(NORMAL_4).addAttack(SNAKELING_4).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 5).addAttack(NORMAL_2).addAttack(VENOM_2).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 6).addAttack(NORMAL_4).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 7).addAttack(VENOM_3).addAttack(SNAKELING_6).build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 8).addAttack(NORMAL_5).addAttack(VENOM_4).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 9).addAttack(NORMAL_5).setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 10).addAttack(NORMAL_4).addAttack(VENOM_3).setMagePray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 11).addAttack(NORMAL_8).setMagePray().setRangedPray().build()),
            Collections.singletonList(Phase.builder(Phase.Rotation.TANZ, 12).addAttack(SNAKELING_4).build())
    );
}
