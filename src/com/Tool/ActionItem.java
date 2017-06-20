package com.Tool;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * ���������������ڲ���������Ʊ����ͼ�꣩
 */
public class ActionItem {
	// ����ͼƬ����
	public int mDrawable;
	// �����ı�����
	public CharSequence mTitle;
	private int Participantsid;

	public ActionItem(int drawable, CharSequence title,int Participantsid) {
		this.mDrawable = drawable;
		this.mTitle = title;
		this.Participantsid=Participantsid;
	}

	public ActionItem(Context context, int titleId, int drawableId,int Participantsid) {
		this.mTitle = context.getResources().getText(titleId);
		this.mDrawable = drawableId;
		this.Participantsid=Participantsid;
	}

	public ActionItem(Context context, CharSequence title, int drawableId,int Participantsid) {
		this.mTitle = title;
		this.mDrawable = drawableId;
		this.Participantsid=Participantsid;
	}

	public ActionItem(Context context, CharSequence title) {
		this.mTitle = title;
		//this.mDrawable = null;
	}

	public int getParticipantsid() {
		return Participantsid;
	}

	public void setParticipantsid(int participantsid) {
		Participantsid = participantsid;
	}
	
}
