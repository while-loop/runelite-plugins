package com.ttl.ui;

import com.ttl.TimeToLevelPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static com.ttl.ui.LevelsListPanel.XP_COLUMN_WIDTH;

@Slf4j
public class LevelsListHeader extends JPanel {
    private static final ImageIcon ARROW_UP;
    private static final ImageIcon HIGHLIGHT_ARROW_DOWN;
    private static final ImageIcon HIGHLIGHT_ARROW_UP;

    private static final Color ARROW_COLOR = ColorScheme.LIGHT_GRAY_COLOR;
    private static final Color HIGHLIGHT_COLOR = ColorScheme.BRAND_ORANGE;

    static {
        final BufferedImage arrowDown = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/arrow_down.png");
        final BufferedImage arrowUp = ImageUtil.rotateImage(arrowDown, Math.PI);
        final BufferedImage arrowUpFaded = ImageUtil.luminanceOffset(arrowUp, -80);
        ARROW_UP = new ImageIcon(arrowUpFaded);

        final BufferedImage highlightArrowDown = ImageUtil.fillImage(arrowDown, HIGHLIGHT_COLOR);
        final BufferedImage highlightArrowUp = ImageUtil.fillImage(arrowUp, HIGHLIGHT_COLOR);
        HIGHLIGHT_ARROW_DOWN = new ImageIcon(highlightArrowDown);
        HIGHLIGHT_ARROW_UP = new ImageIcon(highlightArrowUp);
    }

    private final Map<SortOrder, JLabel> lables = new HashMap<>();
    private final Map<SortOrder, JLabel> arrows = new HashMap<>();

    private SortOrder sortOrder = SortOrder.TTL;
    private final Runnable onSort;

    public LevelsListHeader(LevelsListPanel list, Runnable onSort) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));
        this.onSort = onSort;

        JPanel leftSide = new JPanel(new BorderLayout());
        JPanel rightSide = new JPanel(new BorderLayout());
        leftSide.setOpaque(true);
        rightSide.setOpaque(true);

        leftSide.add(buildHeader("Skill", SortOrder.SKILL, LevelsListPanel.SKILL_COLUMN_WIDTH), BorderLayout.WEST);
        leftSide.add(buildHeader("Lvl", SortOrder.LEVEL, LevelsListPanel.LEVEL_COLUMN_WIDTH), BorderLayout.EAST);
        rightSide.add(buildHeader("XP Left", SortOrder.XP, XP_COLUMN_WIDTH), BorderLayout.WEST);
        rightSide.add(buildHeader("XP/Hr", SortOrder.RATE, LevelsListPanel.XP_RATE_COLUMN_WIDTH), BorderLayout.CENTER);
        rightSide.add(buildHeader("TTL", SortOrder.TTL, LevelsListPanel.TTL_COLUMN_WIDTH), BorderLayout.EAST);

        add(leftSide, BorderLayout.WEST);
        add(rightSide, BorderLayout.EAST);

        SwingUtilities.invokeLater(this::setHighlights);
    }

    private JPanel buildHeader(String name, SortOrder so, int x) {
        JPanel column = new JPanel(new BorderLayout());
        column.setBackground(ColorScheme.SCROLL_TRACK_COLOR);
        column.setPreferredSize(new Dimension(x, 18));
        column.setBorder(new EmptyBorder(0, 1, 0, 1));


        JLabel label = new JLabel(name);
        label.setFont(FontManager.getRunescapeSmallFont());

        JLabel arrowLabel = new JLabel();
        arrowLabel.setIcon(ARROW_UP);

        column.add(label, BorderLayout.WEST);
        column.add(arrowLabel, BorderLayout.EAST);

        column.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (so == sortOrder) {
                    sortOrder.toggleAsc();
                } else {
                    sortOrder = so;
                }
                setHighlights();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(HIGHLIGHT_COLOR);
                if (so != sortOrder) {
                    arrowLabel.setIcon(HIGHLIGHT_ARROW_UP);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (so != sortOrder) {
                    label.setForeground(ARROW_COLOR);
                    arrowLabel.setIcon(ARROW_UP);
                }
            }
        });
        lables.put(so, label);
        arrows.put(so, arrowLabel);
        return column;
    }

    private void setHighlights() {
        onSort.run();

        for (Map.Entry<SortOrder, JLabel> e : lables.entrySet()) {
            SortOrder so = e.getKey();
            if (so == sortOrder) {
                e.getValue().setForeground(HIGHLIGHT_COLOR);
                if (sortOrder.asc) {
                    arrows.get(so).setIcon(HIGHLIGHT_ARROW_UP);
                } else {
                    arrows.get(so).setIcon(HIGHLIGHT_ARROW_DOWN);
                }
            } else {
                e.getValue().setForeground(ARROW_COLOR);
                arrows.get(so).setIcon(ARROW_UP);
            }
        }
    }

    public SortOrder getSort() {
        return this.sortOrder;
    }
}
