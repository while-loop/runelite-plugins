package com.ttl.ui;

import com.ttl.ImgUtils;
import com.ttl.RateTTL;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.QuantityFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.ttl.ui.LevelsListPanel.*;

@Slf4j
public class LevelsListCalcBox extends JPanel {
    private static final Color ODD_ROW = new Color(44, 44, 44);

    private final RateTTL calc;

    public LevelsListCalcBox(RateTTL calc, Runnable onClick, boolean odd) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(2, 0, 2, 0));
        setBackground(odd ? ODD_ROW : ColorScheme.DARK_GRAY_COLOR);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseExited(e);
                onClick.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(odd ? ODD_ROW : ColorScheme.DARK_GRAY_COLOR);
            }
        });
        this.calc = calc;

        JPanel leftSide = new JPanel(new BorderLayout());
        JPanel rightSide = new JPanel(new BorderLayout());
        leftSide.setOpaque(false);
        rightSide.setOpaque(false);

        leftSide.add(buildSkillImage(), BorderLayout.WEST);
        leftSide.add(buildLevelLabel(), BorderLayout.EAST);
        rightSide.add(buildXpLabel(), BorderLayout.WEST);
        rightSide.add(buildRateLabel(), BorderLayout.CENTER);
        rightSide.add(buildTtlLabel(), BorderLayout.EAST);

        setToolTipText(calc.toolTip());

        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenuItem view = new JMenuItem("View skill xp rates");
        view.addActionListener(e -> onClick.run());
        popupMenu.add(view);
        setComponentPopupMenu(popupMenu);


        add(leftSide, BorderLayout.WEST);
        add(rightSide, BorderLayout.EAST);
    }

    private JPanel buildTtlLabel() {
        JPanel column = new JPanel(new BorderLayout());
        JLabel label = new JLabel(calc.ttl());
        column.add(label, BorderLayout.EAST);
        column.setBorder(new EmptyBorder(0, 0, 0, 9));
        column.setOpaque(false);
        column.setPreferredSize(new Dimension(TTL_COLUMN_WIDTH, 0));
        return column;
    }

    private JPanel buildRateLabel() {
        JPanel column = new JPanel(new BorderLayout());
        JLabel label = new JLabel(QuantityFormatter.quantityToRSDecimalStack(calc.getXpRate()));
        column.add(label, BorderLayout.WEST);
        column.setOpaque(false);
        column.setPreferredSize(new Dimension(XP_RATE_COLUMN_WIDTH, 0));
        return column;
    }

    private JPanel buildXpLabel() {
        JPanel column = new JPanel(new BorderLayout());
        JLabel label = new JLabel(QuantityFormatter.quantityToRSDecimalStack(calc.getXpLeft()));
        column.add(label, BorderLayout.WEST);
        column.setOpaque(false);
        column.setPreferredSize(new Dimension(XP_COLUMN_WIDTH, 0));
        return column;
    }

    private JPanel buildLevelLabel() {
        JPanel column = new JPanel(new BorderLayout());

        JLabel label = new JLabel(String.valueOf(calc.getLevel()));
        column.add(label, BorderLayout.CENTER);
        column.setOpaque(false);
        column.setPreferredSize(new Dimension(LEVEL_COLUMN_WIDTH, 0));
        return column;
    }

    private JPanel buildSkillImage() {
        JPanel column = new JPanel(new BorderLayout());
        JLabel label = new JLabel(new ImageIcon(ImgUtils.getSkillImage(calc.getSkill(), true)));
        column.add(label, BorderLayout.CENTER);
        column.setPreferredSize(new Dimension(SKILL_COLUMN_WIDTH, SKILL_COLUMN_HEIGHT));
        column.setOpaque(false);
        return column;
    }
}
