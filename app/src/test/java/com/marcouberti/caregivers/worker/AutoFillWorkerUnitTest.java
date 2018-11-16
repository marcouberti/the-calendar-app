package com.marcouberti.caregivers.worker;

import com.marcouberti.caregivers.db.entity.SlotEntity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AutoFillWorkerUnitTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testComputeMinDistanceSlotsSingleResult() {

        // DISTANCE = abs(hour1 - hour2) + abs(room1 - room2)

        final int HOUR = 9;
        final int ROOM = 1;

        List<SlotEntity> slots = new ArrayList<>();
        slots.add(generateSlot(10, 10)); // distance = 1 + 9 = 10
        slots.add(generateSlot(10, 5)); // distance = 1 + 4 = 5
        slots.add(generateSlot(10, 2)); // distance = 1 + 1 = 2 <--- best choice
        slots.add(generateSlot(10, 3)); // distance = 1 + 2 = 3
        List<SlotEntity> result = AutoFillWorker.computeMinDistanceSlots(slots, HOUR, ROOM);

        assertEquals(1, result.size()); // no fair result, only one best match
        assertEquals("2", result.get(0).getRoomId());
    }

    @Test
    public void testComputeMinDistanceSlotsMultipleResults() {

        // DISTANCE = abs(hour1 - hour2) + abs(room1 - room2)

        final int HOUR = 9;
        final int ROOM = 1;

        List<SlotEntity> slots = new ArrayList<>();
        slots.add(generateSlot(10, 10)); // distance = 1 + 9 = 10
        slots.add(generateSlot(10, 5)); // distance = 1 + 4 = 5
        slots.add(generateSlot(10, 2)); // distance = 1 + 1 = 2 <--- best choice
        slots.add(generateSlot(11, 1)); // distance = 2 + 0 = 2 <--- best choice too
        List<SlotEntity> result = AutoFillWorker.computeMinDistanceSlots(slots, HOUR, ROOM);

        assertEquals(2, result.size()); // no fair result, only one best match
        assertNotEquals("10", result.get(0).getRoomId());
        assertNotEquals("10", result.get(1).getRoomId());
        assertNotEquals("5", result.get(0).getRoomId());
        assertNotEquals("5", result.get(1).getRoomId());
    }

    private SlotEntity generateSlot(int hour, int room) {
        SlotEntity slot = new SlotEntity();
            slot.setHour(hour);
            slot.setRoomId(room+"");
        return slot;
    }

}
