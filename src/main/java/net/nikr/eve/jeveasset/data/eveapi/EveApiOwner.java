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

package net.nikr.eve.jeveasset.data.eveapi;

import com.beimin.eveapi.parser.ApiAuthorization;
import java.util.Date;
import net.nikr.eve.jeveasset.data.api.AbstractOwner;
import net.nikr.eve.jeveasset.data.api.ApiType;
import net.nikr.eve.jeveasset.data.api.OwnerType;


public class EveApiOwner extends AbstractOwner implements OwnerType {
	private final EveApiAccount parentAccount;

	public EveApiOwner(EveApiAccount parentAccount) {
		this.parentAccount = parentAccount;
	}

	public EveApiOwner(final EveApiAccount parentAccount, final EveApiOwner owner) {
		super(owner);
		this.parentAccount = parentAccount;
	}

	public EveApiOwner(final EveApiAccount parentAccount, final String ownerName, final long ownerID) {
		super(ownerName, ownerID);
		this.parentAccount = parentAccount;
	}

	public EveApiAccount getParentAccount() {
		return parentAccount;
	}

	@Override
	public boolean isCorporation() {
		return getParentAccount().isCorporation();
	}

	@Override
	public boolean isAssetList() {
		return getParentAccount().isAssetList();
	}

	@Override
	public boolean isAccountBalance() {
		return getParentAccount().isAccountBalance();
	}

	@Override
	public boolean isIndustryJobs() {
		return getParentAccount().isIndustryJobs();
	}

	@Override
	public boolean isMarketOrders() {
		return getParentAccount().isMarketOrders();
	}

	@Override
	public boolean isTransactions() {
		return getParentAccount().isTransactions();
	}

	@Override
	public boolean isJournal() {
		return getParentAccount().isJournal();
	}

	@Override
	public boolean isContracts() {
		return getParentAccount().isContracts();
	}

	@Override
	public boolean isLocations() {
		return getParentAccount().isLocations();
	}

	@Override
	public Date getExpire() {
		return getParentAccount().getExpires();
	}

	@Override
	public String getComparator() {
		return "eveonline" + getParentAccount().getKeyID();
	}

	@Override
	public boolean isInvalid() {
		return getParentAccount().isInvalid();
	}

	@Override
	public String getAccountName() {
		if (getParentAccount().getName().isEmpty()) {
			return String.valueOf(getParentAccount().getKeyID());
		} else {
			return getParentAccount().getName();
		}
	}

	@Override
	public void setResetAccountName() {
		getParentAccount().setName(String.valueOf(getParentAccount().getKeyID()));
	}

	@Override
	public void setAccountName(String name) {
		getParentAccount().setName(name);
	}

	@Override
	public ApiType getAccountAPI() {
		return ApiType.EVE_ONLINE;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EveApiOwner other = (EveApiOwner) obj;
		if (this.getOwnerID() != other.getOwnerID()) {
			return false;
		}
		if (this.parentAccount != other.parentAccount && (this.parentAccount == null || !this.parentAccount.equals(other.parentAccount))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + (int) (this.getOwnerID() ^ (this.getOwnerID() >>> 32));
		hash = 89 * hash + (this.parentAccount != null ? this.parentAccount.hashCode() : 0);
		return hash;
	}

	public static ApiAuthorization getApiAuthorization(final EveApiAccount account) {
		return new ApiAuthorization(account.getKeyID(), account.getVCode());
	}
	public static ApiAuthorization getApiAuthorization(final EveApiOwner owner) {
		return getApiAuthorization(owner.getParentAccount(), owner.getOwnerID());
	}
	private static ApiAuthorization getApiAuthorization(final EveApiAccount account, final long ownerID) {
		return new ApiAuthorization(account.getKeyID(), ownerID, account.getVCode());
	}
}
