package com.ttl.ui;

import com.ttl.ImgUtils;
import com.ttl.TimeToLevelPlugin;
import net.runelite.api.Skill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static net.runelite.api.Skill.*;


public class RatesConfigPanel extends JPanel {
    private static final ImageIcon BACK_ICON;
    private static final ImageIcon BACK_ICON_HOVER;

    static {
        final BufferedImage backIcon = ImageUtil.getResourceStreamFromClass(TimeToLevelPlugin.class, "/back_icon.png");
        BACK_ICON = new ImageIcon(backIcon);
        BACK_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, 0.53f));
    }

    private final JPanel skillsPanel = new JPanel(new GridLayout(8, 3, 8, 16));
    private final TimeToLevelPlugin plugin;

    public RatesConfigPanel(TimeToLevelPlugin plugin, Runnable back, Consumer<Skill> showSkillRates) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));
        this.plugin = plugin;

        JPanel northPanel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton(BACK_ICON);
        backBtn.setRolloverIcon(BACK_ICON_HOVER);
        SwingUtil.removeButtonDecorations(backBtn);
        backBtn.setToolTipText("Back");
        backBtn.addActionListener(l -> back.run());
        northPanel.add(backBtn, BorderLayout.WEST);

        JPanel wrapper = new JPanel(new BorderLayout());
        skillsPanel.setBorder(new EmptyBorder(16, 8, 16, 8));
        skillsPanel.setBackground(ColorScheme.MEDIUM_GRAY_COLOR.darker());
        wrapper.add(skillsPanel, BorderLayout.NORTH);

        JButton resetBtn = new JButton("Reset all rates");
        resetBtn.addActionListener(l -> {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                    "Are you sure you reset ALL custom XP rates?", "Reset Confirmation",
                    JOptionPane.YES_NO_OPTION)) {
                plugin.resetRates();
            }
        });

        JPanel buttons = new JPanel(new BorderLayout(4, 4));
        JPanel importExport = new JPanel(new GridLayout(1, 2, 8, 8));
        JButton imBtn = new JButton("Import rates");
        JButton exBtn = new JButton("Export rates");
        imBtn.addActionListener(l -> importRates());
        exBtn.addActionListener(l -> exportRates());
        exBtn.setFocusPainted(false);
        imBtn.setFocusPainted(false);
        resetBtn.setFocusPainted(false);

        buttons.add(resetBtn, BorderLayout.SOUTH);
        importExport.add(imBtn);
        importExport.add(exBtn);

        buttons.add(importExport, BorderLayout.NORTH);
        buttons.add(resetBtn, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        addSkills(showSkillRates);
    }

    private void importRates() {
        try {
            final String rates = JOptionPane.showInputDialog(this,
                    "Enter rates data",
                    "Import New Rates",
                    JOptionPane.PLAIN_MESSAGE);

            // cancel button was clicked
            if (rates == null) {
                return;
            }
            boolean success = plugin.setRatesConfig(rates);
            if (!success) {
                throw new Exception("failed to import rates");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Skill rates were successfully imported.",
                        "Import Succeeded",
                        JOptionPane.PLAIN_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid rates data.",
                    "Import Rates Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportRates() {
        final StringSelection contents = new StringSelection(plugin.getRatesConfig());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
        JOptionPane.showMessageDialog(this,
                "Skill rates was copied to clipboard.",
                "Export Succeeded",
                JOptionPane.PLAIN_MESSAGE);
    }

    private static final Skill[] SKILLS = {
            ATTACK, HITPOINTS, MINING,
            STRENGTH, AGILITY, SMITHING,
            DEFENCE, HERBLORE, FISHING,
            RANGED, THIEVING, COOKING,
            PRAYER, CRAFTING, FIREMAKING,
            MAGIC, FLETCHING, WOODCUTTING,
            RUNECRAFT, SLAYER, FARMING,
            CONSTRUCTION, HUNTER
    };

    private void addSkills(Consumer<Skill> showSkillRates) {
        skillsPanel.removeAll();

        for (Skill skill : SKILLS) {
            JLabel img = new JLabel(new ImageIcon(ImgUtils.getSkillImage(skill, false)));
            img.setToolTipText(skill.getName());
            img.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showSkillRates.accept(skill);
                }
            });
            skillsPanel.add(img);
        }
    }
}
