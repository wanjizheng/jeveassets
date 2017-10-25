/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.shared.WalletTransaction;
import com.beimin.eveapi.parser.character.CharWalletTransactionsParser;
import com.beimin.eveapi.parser.corporation.CorpWalletTransactionsParser;
import com.beimin.eveapi.response.shared.WalletTransactionsResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiAccessMask;
import net.nikr.eve.jeveasset.data.api.accounts.EveApiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyTransaction;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class TransactionsGetter extends AbstractApiGetter<WalletTransactionsResponse> {

	private static final int ROW_COUNT = 1000;

	private final boolean saveHistory;

	public TransactionsGetter(UpdateTask updateTask, EveApiOwner owner, boolean saveHistory) {
		super(updateTask, owner, false, owner.getTransactionsNextUpdate(), TaskType.TRANSACTIONS);
		this.saveHistory = saveHistory;
	}

	@Override
	protected void get(String updaterStatus) throws ApiException {
		Set<Integer> accountKeys = new HashSet<>();
		if (owner.isCorporation()) {
			for (int i = 1000; i <= 1006; i++) { //For each wallet division
				accountKeys.add(i);
			}
		} else {
			accountKeys.add(1000);
		}
		//for each account key
		Map<Integer, List<WalletTransaction>> updateList = updateList(accountKeys, NO_RETRIES, new ListHandler<Integer, List<WalletTransaction>>() {
			@Override
			public List<WalletTransaction> get(String listUpdaterStatus, Integer t) throws ApiException {
				return updateIDs(new HashSet<Long>(), NO_RETRIES, new IDsHandler<WalletTransaction>() {
					@Override
					protected List<WalletTransaction> get(String idUpdaterStatus, Long fromID) throws ApiException {
						if (fromID == null) {
							fromID = 0L;
						}
						if (owner.isCorporation()) {
							WalletTransactionsResponse response = new CorpWalletTransactionsParser()
									.getResponse(EveApiOwner.getApiAuthorization(owner), t, fromID, ROW_COUNT);
							if (!handle(response, listUpdaterStatus + " " + idUpdaterStatus)) {
								return null;
							}
							return response.getAll();
						} else {
							WalletTransactionsResponse response = new CharWalletTransactionsParser()
									.getTransactionsResponse(EveApiOwner.getApiAuthorization(owner), fromID, ROW_COUNT);
							if (!handle(response, listUpdaterStatus + " " + idUpdaterStatus)) {
								return null;
							}
							return response.getAll();
						}
					}

					@Override
					protected Long getID(WalletTransaction response) {
						return response.getTransactionID();
					}

				});
			}
		});
		Set<MyTransaction> transactions = new HashSet<MyTransaction>();
		for (Map.Entry<Integer, List<WalletTransaction>> entry : updateList.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			transactions.addAll(EveApiConverter.toTransactions(entry.getValue(), owner, entry.getKey(), saveHistory));
			
		}
		owner.setTransactions(transactions);
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setTransactionsNextUpdate(date);
	}

	@Override
	protected long requestMask() {
		if (owner.isCorporation()) {
			return EveApiAccessMask.TRANSACTIONS_CORP.getAccessMask();
		} else {
			return EveApiAccessMask.TRANSACTIONS_CHAR.getAccessMask();
		}
	}
}
