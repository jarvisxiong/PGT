package com.pgt.pawn.bean;

import com.pgt.common.bean.Media;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlwang on 1/16/16.
 */
public class PawnTicket implements Serializable, PawnTicketState {

	private int mPawnTicketId;
	private int mPawnshopId;
	private String mNumber;
	private String mComments;
	private int mStatus = INITIAL;
	private Date mCreationDate;
	private Date mUpdateDate;
	private Media pawnTicketPhoto;

	private Pawnshop mPawnshop;

	public int getPawnTicketId() {
		return mPawnTicketId;
	}

	public void setPawnTicketId(final int pPawnTicketId) {
		mPawnTicketId = pPawnTicketId;
	}

	public int getPawnshopId() {
		return mPawnshopId;
	}

	public void setPawnshopId(final int pPawnshopId) {
		mPawnshopId = pPawnshopId;
	}

	public String getNumber() {
		return mNumber;
	}

	public void setNumber(final String pNumber) {
		mNumber = pNumber;
	}

	public Date getCreationDate() {
		return mCreationDate;
	}

	public void setCreationDate(final Date pCreationDate) {
		mCreationDate = pCreationDate;
	}

	public Date getUpdateDate() {
		return mUpdateDate;
	}

	public void setUpdateDate(final Date pUpdateDate) {
		mUpdateDate = pUpdateDate;
	}

	public String getComments() {
		return mComments;
	}

	public void setComments(final String pComments) {
		mComments = pComments;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(final int pStatus) {
		mStatus = pStatus;
	}

	public Pawnshop getPawnshop() {
		return mPawnshop;
	}

	public void setPawnshop(final Pawnshop pPawnshop) {
		mPawnshop = pPawnshop;
	}

	public Media getPawnTicketPhoto() {
		return pawnTicketPhoto;
	}

	public void setPawnTicketPhoto(Media pawnTicketPhoto) {
		this.pawnTicketPhoto = pawnTicketPhoto;
	}
}
