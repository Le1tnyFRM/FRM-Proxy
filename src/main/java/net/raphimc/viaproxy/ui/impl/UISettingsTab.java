/*
 * This file is part of ViaProxy - https://github.com/RaphiMC/ViaProxy
 * Copyright (C) 2021-2026 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.raphimc.viaproxy.ui.impl;

import net.lenni0451.commons.swing.GBC;
import net.raphimc.viaproxy.ui.UITab;
import net.raphimc.viaproxy.ui.ViaProxyWindow;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

import static net.raphimc.viaproxy.ui.ViaProxyWindow.BORDER_PADDING;

public class UISettingsTab extends UITab {

    public UISettingsTab(final ViaProxyWindow frame) {
        super(frame, "ui_settings");
    }

    @Override
    protected void init(JPanel contentPane) {
        JPanel body = new JPanel();
        body.setLayout(new GridBagLayout());

        int gridy = 0;
        // Theme Selection
        {
            JLabel themeLabel = new JLabel("Theme");
            GBC.create(body).grid(0, gridy++).insets(BORDER_PADDING, BORDER_PADDING, 0, BORDER_PADDING).anchor(GBC.NORTHWEST).add(themeLabel);

            String[] themes = {"Dark", "Light", "Ocean"};
            JComboBox<String> themeBox = new JComboBox<>(themes);
            themeBox.addActionListener(e -> {
                String selected = (String) themeBox.getSelectedItem();
                try {
                    // Reset custom defaults from UIManager completely first
                    UIManager.put("Panel.background", null);
                    UIManager.put("TabbedPane.contentAreaColor", null);
                    UIManager.put("Button.background", null);
                    UIManager.put("TextField.background", null);

                    if ("Dark".equals(selected)) {
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                    } else if ("Light".equals(selected)) {
                        UIManager.setLookAndFeel(new FlatLightLaf());
                    } else if ("Ocean".equals(selected)) {
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                        UIManager.put("Panel.background", new Color(0, 105, 148));
                        UIManager.put("TabbedPane.contentAreaColor", new Color(0, 80, 120));
                        UIManager.put("Button.background", new Color(0, 130, 180));
                        UIManager.put("TextField.background", new Color(0, 70, 100));
                    }
                    
                    // Force complete Look and Feel hierarchy updates correctly
                    SwingUtilities.updateComponentTreeUI(this.viaProxyWindow);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            GBC.create(body).grid(0, gridy++).weightx(1).insets(0, BORDER_PADDING, 0, BORDER_PADDING).fill(GBC.HORIZONTAL).add(themeBox);
        }

        // Additional setting: ToolTip Delay
        {
            JLabel tooltipLabel = new JLabel("ToolTip Delay (ms)");
            GBC.create(body).grid(0, gridy++).insets(BORDER_PADDING, BORDER_PADDING, 0, BORDER_PADDING).anchor(GBC.NORTHWEST).add(tooltipLabel);
            
            JSlider delaySlider = new JSlider(0, 2000, ToolTipManager.sharedInstance().getInitialDelay());
            delaySlider.addChangeListener(e -> ToolTipManager.sharedInstance().setInitialDelay(delaySlider.getValue()));
            GBC.create(body).grid(0, gridy++).weightx(1).insets(0, BORDER_PADDING, 0, BORDER_PADDING).fill(GBC.HORIZONTAL).add(delaySlider);
        }

        contentPane.setLayout(new BorderLayout());
        contentPane.add(body, BorderLayout.NORTH);
    }
}
