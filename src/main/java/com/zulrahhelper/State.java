package com.zulrahhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State {
    private int selectedPhase = 1;
    private int genPhase = 1;
    private Phase.Rotation rotation = Phase.Rotation.START;


    public int getSelectedPhase() {
        return selectedPhase;
    }

    public int getGenPhase() {
        return genPhase;
    }

    public Phase.Rotation getRotation() {
        return rotation;
    }

    /**
     * Set phase number until a choice is needed
     *
     * @param phase
     */
    public void setPhase(Phase phase) {
        this.setRotation(phase.getRotation());
        this.selectedPhase = phase.getNumber();
        if (phase.getRotation() == Phase.Rotation.MAGMA && phase.getNumber() == 2) {
            this.selectedPhase = phase.getNumber() + 1;
        }
        this.genPhase = buildTree().size();
    }

    public Phase getPhase() {
        return new Phase(rotation, selectedPhase);
    }

    public void setRotation(Phase.Rotation rotation) {
        this.rotation = rotation;
    }

    public void reset() {
        this.genPhase = 1;
        this.selectedPhase = 1;
        this.rotation = Phase.Rotation.START;
    }

    @Override
    public String toString() {
        return "State{" +
                "genPhase=" + genPhase +
                ", selectedPhase=" + selectedPhase +
                ", rotation=" + rotation +
                '}';
    }

    public List<List<Phase>> buildTree() {
        List<List<Phase>> tree = new ArrayList<>();
        switch (rotation) {
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

        for (List<Phase> phases : tree) {
            if (phases.size() <= 0) {
                break;
            }

            for (Phase p : phases) {
                p.setStates(this);
            }

            if (phases.get(0).getNumber() > getSelectedPhase() + 1) {
                if (phases.size() > 1) {
                    tree = tree.subList(0, tree.indexOf(phases) + 1);
                    break;
                }
            }
        }

        return tree;
    }

    List<List<Phase>> START = Arrays.asList(
            Arrays.asList(new Phase(Phase.Rotation.START, 1)),
            Arrays.asList(
                    new Phase(Phase.Rotation.MAGMA, 2),
                    new Phase(Phase.Rotation.NORMAL, 2),
                    new Phase(Phase.Rotation.TANZ, 2)
            )
    );
    List<List<Phase>> MAGMA = Arrays.asList(
            Arrays.asList(new Phase(Phase.Rotation.MAGMA, 1)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA, 2)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA, 3)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 4), new Phase(Phase.Rotation.MAGMA_B, 4))
    );
    List<List<Phase>> MAGMA_A = Arrays.asList(
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 1)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 2)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 3)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 4)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 5)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 6)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 7)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 8)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 9)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_A, 10))
    );
    List<List<Phase>> MAGMA_B = Arrays.asList(
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 1)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 2)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 3)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 4)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 5)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 6)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 7)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 8)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 9)),
            Arrays.asList(new Phase(Phase.Rotation.MAGMA_B, 10))
    );
    List<List<Phase>> NORMAL = Arrays.asList(
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 1)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 2)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 3)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 4)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 5)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 6)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 7)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 8)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 9)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 10)),
            Arrays.asList(new Phase(Phase.Rotation.NORMAL, 11))
    );
    List<List<Phase>> TANZ = Arrays.asList(
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 1)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 2)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 3)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 4)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 5)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 6)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 7)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 8)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 9)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 10)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 11)),
            Arrays.asList(new Phase(Phase.Rotation.TANZ, 12))
    );
}