/*
 * Copyright 2009-2015 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.gui.tabs.stockpile;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsStockpile;


public class StockpileImportDialog extends JDialogCentered {

	private enum StockpileImportAction {
		OK,
		CANCEL
	}

	public enum ImportReturn {
		RENAME,
		RENAME_ALL,
		MERGE,
		MERGE_ALL,
		OVERWRITE,
		OVERWRITE_ALL,
		SKIP,
		SKIP_ALL,
	}
	
	private final JRadioButton jRename;
	private final JRadioButton jMerge;
	private final JRadioButton jOverwrite;
	private final JRadioButton jSkip;
	private final JLabel jName;
	private final JLabel jRenameLabel;
	private final JLabel jMergeLabel;
	private final JLabel jOverwriteLabel;
	private final JLabel jSkipLabel;
	private final JCheckBox jAll;
	private final JButton jOK;
	private final JButton jCancel;
	private final ListenerClass listenerClass;

	private ImportReturn importReturn;
	
	public StockpileImportDialog(Program program) {
		super(program, "Import XML");
		
		listenerClass = new ListenerClass();

		jName = new JLabel();
		jName.setFont(new Font(jName.getFont().getName(), Font.BOLD, jName.getFont().getSize() + 1));
		jRenameLabel = new JLabel(TabsStockpile.get().importXmlRenameHelp());
		jRename = new JRadioButton(TabsStockpile.get().importXmlRename());
		jMergeLabel = new JLabel(TabsStockpile.get().importXmlMergeHelp());
		jMerge = new JRadioButton(TabsStockpile.get().importXmlMerge());
		jOverwriteLabel = new JLabel(TabsStockpile.get().importXmlOverwriteHelp());
		jOverwrite = new JRadioButton(TabsStockpile.get().importXmlOverwrite());
		jSkipLabel = new JLabel(TabsStockpile.get().importXmlSkipHelp());
		jSkip = new JRadioButton(TabsStockpile.get().importXmlSkip());

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(jRename);
		buttonGroup.add(jMerge);
		buttonGroup.add(jOverwrite);
		buttonGroup.add(jSkip);

		jAll = new JCheckBox();

		jOK = new JButton(TabsStockpile.get().ok());
		jOK.setActionCommand(StockpileImportAction.OK.name());
		jOK.addActionListener(listenerClass);

		jCancel = new JButton(TabsStockpile.get().cancel());
		jCancel.setActionCommand(StockpileImportAction.CANCEL.name());
		jCancel.addActionListener(listenerClass);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(jName)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRename)
						.addComponent(jMerge)
						.addComponent(jOverwrite)
						.addComponent(jSkip)
					)
					.addGap(20)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jRenameLabel)
						.addComponent(jMergeLabel)
						.addComponent(jOverwriteLabel)
						.addComponent(jSkipLabel)
					)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jAll)
					.addGap(0, 0, Integer.MAX_VALUE)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jRename, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jRenameLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jMerge, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMergeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jOverwrite, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jOverwriteLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(jSkip, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jSkipLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(20)
				.addComponent(jAll, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGap(20)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public void resetToDefault() {
		jRename.setSelected(true);
		jAll.setSelected(false);
	}

	public ImportReturn show(String name, int count) {
		jName.setText(name);
		jAll.setText(TabsStockpile.get().importXmlAll(count));
		jAll.setEnabled(count > 1);
		setVisible(true);
		return importReturn;
	}
	
	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() { }

	@Override
	protected void save() {
		selected(jRename, ImportReturn.RENAME, ImportReturn.RENAME_ALL);
		selected(jMerge, ImportReturn.MERGE, ImportReturn.MERGE_ALL);
		selected(jOverwrite, ImportReturn.OVERWRITE, ImportReturn.OVERWRITE_ALL);
		selected(jSkip, ImportReturn.SKIP, ImportReturn.SKIP_ALL);
		setVisible(false);
	}

	private void selected(JRadioButton jRadioButton, ImportReturn single, ImportReturn all) {
		if (jRadioButton.isSelected()) {
			if (jAll.isSelected()) {
				importReturn = all;
			} else {
				importReturn = single;
			}
		}
	}

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (StockpileImportAction.OK.name().equals(e.getActionCommand())) {
				save();
			}
			if (StockpileImportAction.CANCEL.name().equals(e.getActionCommand())) {
				importReturn = ImportReturn.SKIP_ALL;
				setVisible(false);
			}
		}
		
	}
	
	
}