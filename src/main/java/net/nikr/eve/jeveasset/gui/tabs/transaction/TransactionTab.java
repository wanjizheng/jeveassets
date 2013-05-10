/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.transaction;

import ca.odell.glazedlists.*;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.WalletTransaction;
import net.nikr.eve.jeveasset.gui.frame.StatusPanel;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTab;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.LogicType;
import net.nikr.eve.jeveasset.gui.shared.filter.FilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.*;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.i18n.TabsTransaction;


public class TransactionTab extends JMainTab implements ListEventListener<WalletTransaction>, TableMenu<WalletTransaction> {

	private JAutoColumnTable jTable;
	private JLabel jSellOrdersTotal;
	private JLabel jBuyOrdersTotal;

	//Table
	private TransactionsFilterControl filterControl;
	private EnumTableFormatAdaptor<TransactionTableFormat, WalletTransaction> tableFormat;
	private DefaultEventTableModel<WalletTransaction> tableModel;
	private FilterList<WalletTransaction> filterList;
	private EventList<WalletTransaction> eventList;
	private DefaultEventSelectionModel<WalletTransaction> selectionModel;

	public static final String NAME = "transaction"; //Not to be changed!

	public TransactionTab(final Program program) {
		super(program, TabsTransaction.get().title(), Images.TOOL_WALLET.getIcon(), true);

		//Table Format
		tableFormat = new EnumTableFormatAdaptor<TransactionTableFormat, WalletTransaction>(TransactionTableFormat.class);
		//Backend
		eventList = program.getWalletTransactionsEventList();
		//Filter
		filterList = new FilterList<WalletTransaction>(eventList);
		filterList.addListEventListener(this);
		//Sorting (per column)
		SortedList<WalletTransaction> sortedList = new SortedList<WalletTransaction>(filterList);
		//Table Model
		tableModel = EventModels.createTableModel(sortedList, tableFormat);
		//Table
		jTable = new JAutoColumnTable(program, tableModel);
		jTable.setCellSelectionEnabled(true);
		//Sorting
		TableComparatorChooser.install(jTable, sortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, tableFormat);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(sortedList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable, NAME);
		//Scroll Panels
		JScrollPane jTableScroll = new JScrollPane(jTable);
		//Table Filter
		Map<String, List<Filter>> defaultFilters = new HashMap<String, List<Filter>>();
		List<Filter> filter;
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, TransactionTableFormat.TYPE, CompareType.EQUALS,  TabsTransaction.get().buy()));
		defaultFilters.put(TabsTransaction.get().buy(), filter);
		filter = new ArrayList<Filter>();
		filter.add(new Filter(LogicType.AND, TransactionTableFormat.TYPE, CompareType.EQUALS,  TabsTransaction.get().sell()));
		defaultFilters.put(TabsTransaction.get().sell(), filter);
		filterControl = new TransactionsFilterControl(
				program.getMainWindow().getFrame(),
				tableFormat,
				eventList,
				filterList,
				program.getSettings().getTableFilters(NAME),
				defaultFilters
				);

		//Menu
		installMenu(program, this, jTable, WalletTransaction.class);

		jSellOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().totalSell(), Images.ORDERS_SELL.getIcon());
		this.addStatusbarLabel(jSellOrdersTotal);

		jBuyOrdersTotal = StatusPanel.createLabel(TabsTransaction.get().totalBuy(), Images.ORDERS_BUY.getIcon());
		this.addStatusbarLabel(jBuyOrdersTotal);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(filterControl.getPanel())
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public JMenu getFilterMenu() {
		return filterControl.getMenu(jTable, selectionModel.getSelected());
	}

	@Override
	public JMenu getColumnMenu() {
		return tableFormat.getMenu(program, tableModel, jTable);
	}

	@Override
	public MenuData<WalletTransaction> getMenuData() {
		return new MenuData<WalletTransaction>(selectionModel.getSelected(), program.getSettings());
	}

	@Override
	public void addInfoMenu(JComponent jComponent) {
		JMenuInfo.wallet(jComponent, selectionModel.getSelected());
	}

	@Override
	public void addToolMenu(JComponent jComponent) { }

	@Override
	public void updateData() { }

	@Override
	public void listChanged(ListEvent<WalletTransaction> listChanges) {
		double sellOrdersTotal = 0;
		double buyOrdersTotal = 0;
		for (WalletTransaction transaction : filterList) {
			if (transaction.getTransactionType().equals("sell")) { //Sell
				sellOrdersTotal += transaction.getPrice() * transaction.getQuantity();
			} else { //Buy
				buyOrdersTotal += transaction.getPrice() * transaction.getQuantity();
			}
		}
		jSellOrdersTotal.setText(Formater.iskFormat(sellOrdersTotal));
		jBuyOrdersTotal.setText(Formater.iskFormat(buyOrdersTotal));
	}

	public static class TransactionsFilterControl extends FilterControl<WalletTransaction> {

		private EnumTableFormatAdaptor<TransactionTableFormat, WalletTransaction> tableFormat;

		public TransactionsFilterControl(final JFrame jFrame, final EnumTableFormatAdaptor<TransactionTableFormat, WalletTransaction> tableFormat, final EventList<WalletTransaction> eventList, final FilterList<WalletTransaction> filterList, final Map<String, List<Filter>> filters, final Map<String, List<Filter>> defaultFilters) {
			super(jFrame, NAME, eventList, filterList, filters, defaultFilters);
			this.tableFormat = tableFormat;
		}

		@Override
		protected Object getColumnValue(final WalletTransaction item, final String column) {
			TransactionTableFormat format = TransactionTableFormat.valueOf(column);
			return format.getColumnValue(item);
		}

		@Override
		protected boolean isNumericColumn(final Enum<?> column) {
			TransactionTableFormat format = (TransactionTableFormat) column;
			if (Number.class.isAssignableFrom(format.getType())) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected boolean isDateColumn(final Enum<?> column) {
			TransactionTableFormat format = (TransactionTableFormat) column;
			if (format.getType().getName().equals(Date.class.getName())) {
				return true;
			} else {
				return false;
			}
		}


		@Override
		public Enum[] getColumns() {
			return TransactionTableFormat.values();
		}

		@Override
		protected Enum<?> valueOf(final String column) {
			return TransactionTableFormat.valueOf(column);
		}

		@Override
		protected List<EnumTableColumn<WalletTransaction>> getEnumColumns() {
			return columnsAsList(TransactionTableFormat.values());
		}

		@Override
		protected List<EnumTableColumn<WalletTransaction>> getEnumShownColumns() {
			return new ArrayList<EnumTableColumn<WalletTransaction>>(tableFormat.getShownColumns());
		}
	}
}