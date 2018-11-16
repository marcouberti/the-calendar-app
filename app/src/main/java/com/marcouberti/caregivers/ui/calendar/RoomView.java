package com.marcouberti.caregivers.ui.calendar;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class RoomView extends CardView {

    private int hour;
    private String room;

    public RoomView(Context context) {
        super(context);
    }

    public RoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
