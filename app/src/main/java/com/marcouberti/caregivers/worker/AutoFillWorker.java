package com.marcouberti.caregivers.worker;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.marcouberti.caregivers.CaregiversApplication;
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.db.entity.SlotEntity;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;
import com.marcouberti.caregivers.model.Slot;
import com.marcouberti.caregivers.repository.CalendarRepository;
import com.marcouberti.caregivers.repository.CaregiversRepository;
import com.marcouberti.caregivers.util.Constants;
import com.marcouberti.caregivers.util.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AutoFillWorker extends Worker {

    private CalendarRepository calendarRepository;
    private CaregiversRepository caregiversRepository;

    public AutoFillWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        if(getApplicationContext() instanceof CaregiversApplication) {
            calendarRepository = ((CaregiversApplication)getApplicationContext()).getCalendarRepository();
            caregiversRepository = ((CaregiversApplication)getApplicationContext()).getCaregiverRepository();
        }else return Result.FAILURE;

        long ts = getInputData().getLong("DATE_TS", 0);
        if(ts == 0) return Result.FAILURE;

        Date date = new Date(ts);

        // 1 - load all vacant time slots
        // 2 - for each slot assign a suitable caregiver with the following logic
        //      3 - load all caregivers NOT busy in this time slot
        //      4 - removing the caregivers with already 6 weekly working hour
        //      5 - if no one -> skip to next slot
        //      6 - if only one -> pick it and go to next slot
        //      7 - choose from the remaining list with this logic
        //          8 - removing the caregivers with already 5 weekly working hour
        //          9 - if only one remaining  -> pick it and go to next slot
        //          10 - if no one proceed with the list of 5hours/week
        //          11 - if multiple ones:
        //              12 - filter caregivers working the same day
        //              13 - if one RETURN
        //              14 - if multiple RETURN the nearest room/slot one.
        //              15 - if multiple
        //                  16 - RETURN the one with less work in previous 4 weeks

        List<SlotEntity> busySlots = calendarRepository.getSlotsSync(date);
        List<Slot> vacantSlots = new ArrayList<>();
        for(int i = Constants.START_WORKING_HOUR; i<Constants.END_WORKING_HOUR; i++) {
            for(int r = 1; r<=Constants.ROOM_COUNT; r++) {
                SlotEntity se = new SlotEntity();
                se.setDate(date);
                se.setHour(i);
                se.setRoomId(r+"");
                se.setPatientName("Patient name");
                vacantSlots.add(se);
            }
        }

        // get only vacant slots removing the busy ones
        vacantSlots = Stream.of(vacantSlots).filter(slot -> !containsSlot(busySlots,slot)).
                collect(Collectors.toList());

        for(Slot slot: vacantSlots) {
            // load all caregivers NOT busy in this time slot
            List<CaregiverEntity> availableCaregivers = caregiversRepository.
                    loadAllCaregiversAvailableForDateAndHourSync(date, slot.getHour());

            // no result -> skip to next slot
            if(availableCaregivers == null || availableCaregivers.size() == 0) {
                continue;
            }

            // extract the non-busy caregivers in the current week (no more than 6 slot busy)
            List<CaregiverEntity> weeklyFreeCaregivers = caregiversRepository.
                    filterWithMaxSlotsInDateRangeSync(
                            DateUtils.getWeekStartDate(date),
                            DateUtils.getWeekEndDate(date),
                            Constants.MAX_OVERTIME_SLOTS,
                            availableCaregivers);

            // no result -> skip to next slot
            if(weeklyFreeCaregivers == null || weeklyFreeCaregivers.size() == 0) {
                continue;
            }
            // if only one ->pick it
            else if(weeklyFreeCaregivers.size() == 1) {
                saveSlot(slot, weeklyFreeCaregivers.get(0));
                continue;
            }

            // find in the weekly available if there is one or more with no overtime
            // extract the non-busy caregivers in the current week (no more than 6 slot busy)
            List<CaregiverEntity> noOvertimeCaregivers = caregiversRepository.
                    filterWithMaxSlotsInDateRangeSync(
                            DateUtils.getWeekStartDate(date),
                            DateUtils.getWeekEndDate(date),
                            Constants.NO_OVERTIME_SLOTS,
                            weeklyFreeCaregivers);

            // if only one ->pick it
            if(noOvertimeCaregivers != null && noOvertimeCaregivers.size() == 1) {
                saveSlot(slot, noOvertimeCaregivers.get(0));
                continue;
            }

            List<CaregiverEntity> remainingCaregivers = new ArrayList<>();
            // if no one with no-overtime, use the overtime list to proceed
            if(noOvertimeCaregivers == null || noOvertimeCaregivers.size() == 0) {
                remainingCaregivers = weeklyFreeCaregivers;
            }
            // if multiple ones with no-overtime still available
            else if(noOvertimeCaregivers.size() > 1) {
                remainingCaregivers =  noOvertimeCaregivers;
            }

            // get the ones working today (actually not today, but the date of the slot)
            List<CaregiverEntity> workingToday = caregiversRepository.
                    filterWorkingInDateSync(date, remainingCaregivers);

            // if one working today -> pick it
            if(workingToday != null && workingToday.size() == 1) {
                saveSlot(slot, workingToday.get(0));
                continue;
            }
            // if multiple one working today get the one working in the nearest room/time slot
            else if(workingToday != null && workingToday.size()> 1) {
                int roomInteger = Integer.parseInt(slot.getRoomId());
                int hour = slot.getHour();
                List<SlotEntity> dailySlot = calendarRepository.getSlotsSync(date);
                List<SlotEntity> dailySlotFiltered = Stream.of(dailySlot).filter(s ->
                        caregiverInSlots(workingToday,s)).collect(Collectors.toList());

                List<SlotEntity> minDistanceSlots = computeMinDistanceSlots(dailySlotFiltered, hour, roomInteger);

                // if only one pick it
                if(minDistanceSlots.size() == 1) {
                    final String caregiverId = minDistanceSlots.get(0).getCaregiverId();
                    CaregiverEntity c = Stream.of(workingToday).filter(car -> car.getId().equalsIgnoreCase(caregiverId)).single();
                    saveSlot(slot, c);
                    continue;
                }

                // if multiple one
                remainingCaregivers = Stream.of(minDistanceSlots).map(se -> getCaregiverFromSlot(workingToday, se)).collect(Collectors.toList());
            }

            // get the one with less working hour in the prev 4 weeks
            List<CaregiverEntity> lessWorkingIn4Weeks = caregiversRepository.filterLessWorkingInRangeSync(
                    DateUtils.getRelativeDate(date, Calendar.MONTH, -1), date, remainingCaregivers);

            if(lessWorkingIn4Weeks != null || lessWorkingIn4Weeks.size() > 0) {
                saveSlot(slot, lessWorkingIn4Weeks.get(0));
            }
        }

        return Result.SUCCESS;
    }

    @VisibleForTesting
    static List<SlotEntity> computeMinDistanceSlots(List<SlotEntity> slots, int hour, int room) {
        SlotEntity min = Stream.of(slots).min((o1, o2) -> {
            int score1 = Math.abs(o1.getHour() - hour) + Math.abs(Integer.parseInt(o1.getRoomId()) - room);
            int score2 = Math.abs(o2.getHour() - hour) + Math.abs(Integer.parseInt(o2.getRoomId()) - room);
            return Integer.compare(score1, score2);
        }).get();

        slots = Stream.of(slots).sorted((o1, o2) -> {
            int score1 = Math.abs(o1.getHour() - hour) + Math.abs(Integer.parseInt(o1.getRoomId()) - room);
            int score2 = Math.abs(o2.getHour() - hour) + Math.abs(Integer.parseInt(o2.getRoomId()) - room);
            return Integer.compare(score1, score2);
        }).collect(Collectors.toList());

        int minScore = Math.abs(min.getHour() - hour) + Math.abs(Integer.parseInt(min.getRoomId()) - room);

        List<SlotEntity> minDistanceSlots = new ArrayList<>();
        for (SlotEntity se :
                slots) {
            int score = Math.abs(se.getHour() - hour) + Math.abs(Integer.parseInt(se.getRoomId()) - room);
            if(score == minScore) minDistanceSlots.add(se);
        }
        return minDistanceSlots;
    }

    private void saveSlot(Slot slot, CaregiverEntity caregiver) {
        SlotCaregiver sc = new SlotCaregiver();
            sc.patientName = slot.getPatientName();
            sc.roomId = slot.getRoomId();
            sc.hour = slot.getHour();
            sc.date = slot.getDate();
            sc.caregiverId = caregiver.getId();
        calendarRepository.add(sc);
    }

    // region utility methods
    private CaregiverEntity getCaregiverFromSlot(List<CaregiverEntity> caregivers, SlotEntity se) {
        for (CaregiverEntity c : caregivers) {
            if(c.getId().equalsIgnoreCase(se.getCaregiverId())) return c;
        }
        return null;
    }

    private boolean caregiverInSlots(List<CaregiverEntity> caregivers, SlotEntity s) {
        for(CaregiverEntity c: caregivers) {
            if(c.getId().equalsIgnoreCase(s.getCaregiverId())) return true;
        }
        return false;
    }

    private boolean containsSlot(List<SlotEntity> busySlot, Slot slot) {
        for (Slot bs : busySlot) {
            if(DateUtils.isSameDay(bs.getDate(), slot.getDate()) &&
                    bs.getHour().intValue() == slot.getHour().intValue() &&
                    bs.getRoomId().equalsIgnoreCase(slot.getRoomId())) return true;
        }
        return false;
    }
    // endregion
}
