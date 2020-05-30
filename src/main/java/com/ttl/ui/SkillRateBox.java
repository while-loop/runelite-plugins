package com.ttl.ui;

import com.ttl.TimeToLevelPlugin;
import com.ttl.RateMethod;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

@Slf4j
public class SkillRateBox extends JPanel {
    private static final ImageIcon DELETE_ICON;
    private static final ImageIcon DELETE_ICON_HOVER;

    static {
        final BufferedImage deleteIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/delete_icon.png");
        DELETE_ICON = new ImageIcon(deleteIcon);
        DELETE_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(deleteIcon, 0.53f));
    }

    public SkillRateBox(RateMethod rates, Runnable onDelete) {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setLayout(new BorderLayout(0, 2));
        setBorder(new EmptyBorder(4, 4, 4, 4));


        JPanel top = new JPanel(new BorderLayout());

        JPanel lvlCnts = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));

        NumberFormatter lvlFmt = new NumberFormatter(NumberFormat.getIntegerInstance());
        lvlFmt.setValueClass(Integer.class);
        lvlFmt.setAllowsInvalid(false);
        lvlFmt.setMinimum(1);
        lvlFmt.setCommitsOnValidEdit(true);
        lvlFmt.setMaximum(99);
        JFormattedTextField lvlTxt = new JFormattedTextField(lvlFmt);
        lvlTxt.setColumns(2);
        lvlTxt.setValue(rates.getLevel());
        lvlCnts.add(new JLabel("Lvl: "));
        lvlCnts.add(lvlTxt);

        JPanel xpCnts = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        NumberFormatter xpFmt = new NumberFormatter(NumberFormat.getIntegerInstance());
        xpFmt.setValueClass(Integer.class);
        xpFmt.setAllowsInvalid(false);
        xpFmt.setCommitsOnValidEdit(true);
        xpFmt.setMinimum(0);
        xpFmt.setMaximum(Integer.MAX_VALUE);
        JFormattedTextField xpRateTxt = new JFormattedTextField(xpFmt);
        xpRateTxt.setValue(rates.getRate());
        xpRateTxt.setColumns(7);
        xpCnts.add(xpRateTxt);
        xpCnts.add(new JLabel("xp/hr"));

        JButton deleteBtn = new JButton(DELETE_ICON);
        deleteBtn.setRolloverIcon(DELETE_ICON_HOVER);
        SwingUtil.removeButtonDecorations(deleteBtn);
        deleteBtn.addActionListener(e -> onDelete.run());
        deleteBtn.setToolTipText("Delete rate");
        deleteBtn.setPreferredSize(new Dimension(16, 0));

        top.add(lvlCnts, BorderLayout.WEST);
        top.add(xpCnts, BorderLayout.CENTER);
        top.add(deleteBtn, BorderLayout.EAST);

        JTextField methodTxt = new JTextField(rates.getMethod());
        if (rates.getMethod() == null) {
            methodTxt.setText("method description");
        }

        add(top, BorderLayout.NORTH);
        add(methodTxt, BorderLayout.SOUTH);


        lvlTxt.addFocusListener(new FocusAdapter() {
            @SneakyThrows
            @Override
            public void focusLost(FocusEvent e) {
                rates.setLevel((Integer) lvlTxt.getValue());
            }
        });

        xpRateTxt.addFocusListener(new FocusAdapter() {
            @SneakyThrows
            @Override
            public void focusLost(FocusEvent e) {
                rates.setRate((Integer) xpRateTxt.getValue());
            }
        });

        methodTxt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                rates.setMethod(methodTxt.getText());
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PluginPanel.WIDTH, super.getPreferredSize().height);
    }
}
