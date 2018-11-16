package com.marcouberti.caregivers.ui.calendar;

import androidx.appcompat.widget.Toolbar;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.db.entity.pojo.SlotCaregiver;
import com.marcouberti.caregivers.util.ColorUtils;
import com.marcouberti.caregivers.util.Constants;
import com.marcouberti.caregivers.util.DateUtils;
import com.marcouberti.caregivers.worker.AutoFillWorker;
import com.marcouberti.caregivers.worker.FetchAllCaregiversWorker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private CalendarViewModel mViewModel;
    private CalendarRouter router = new CalendarRouter();

    private Toolbar toolbar;
    private FloatingActionButton fabAutoFill;
    private SlotListAdapter mAdapter;

    private List<CalendarViewModel.TimeSlot> slots = new ArrayList<>();

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_fragment, container, false);

        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_prev_arrow);
        toolbar.setNavigationOnClickListener(view -> {
            if(getActivity()!=null && isAdded()) getActivity().finish();
        });
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_prev_day) {
                mViewModel.onTapPreviousDay();
            }else if (menuItem.getItemId() == R.id.action_next_day) {
                mViewModel.onTapNextDay();
            }
            return true;
        });
        toolbar.inflateMenu(R.menu.menu_calendar);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.slot_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SlotListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setItemViewCacheSize(24);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        toolbar.setOnClickListener(v -> displayDatePicker());

        fabAutoFill = rootView.findViewById(R.id.fab_auto_fill);
        fabAutoFill.setOnClickListener(v -> runAutoFillWorker());
        return rootView;
    }

    private void runAutoFillWorker() {
        Data params = new Data.Builder().putLong("DATE_TS",
                mViewModel.getDate().getValue().getTime()).build();

        final String JOB_GROUP_NAME = "AUTO_FILL_WORK";

        OneTimeWorkRequest fetchAllCaregiversWork =
                new OneTimeWorkRequest.Builder(FetchAllCaregiversWorker.class)
                        .build();

        OneTimeWorkRequest autoFillWork =
                new OneTimeWorkRequest.Builder(AutoFillWorker.class)
                        .setInputData(params)
                        .build();

        final WorkManager workManager = WorkManager.getInstance();

        // only one at the time, sequentially to avoid db conflicts
        WorkContinuation work = workManager
                .beginUniqueWork(JOB_GROUP_NAME, ExistingWorkPolicy.REPLACE, fetchAllCaregiversWork)
                .then(autoFillWork);
        work.enqueue();
    }

    private void displayDatePicker() {
        DialogFragment newFragment = SelectDateFragment.getInstance(mViewModel.getDate().getValue(),
                (view, year, month, dayOfMonth) -> {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mViewModel.onDateChange(calendar.getTime());
        });
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        mViewModel.getSlots().observe(this, slots -> {
            // update daily calendar UI
            if(slots == null) return;
            this.slots = slots;
            this.mAdapter.computeDiffs();
        });

        mViewModel.getDate().observe(this, date -> {
            // update daily calendar UI
            if(date == null) return;
            toolbar.setTitle(DateUtils.prettyDate(getContext(), date, DateFormat.MEDIUM));
        });
    }

    public class SlotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        LayoutInflater layoutInflater;
        AsyncLayoutInflater asyncLayoutInflater;

        final DiffUtil.ItemCallback<CalendarViewModel.TimeSlot> DIFF_CALLBACK
                = new DiffUtil.ItemCallback<CalendarViewModel.TimeSlot>() {
            @Override
            public boolean areItemsTheSame(
                    @NonNull CalendarViewModel.TimeSlot oldItem, @NonNull CalendarViewModel.TimeSlot newItem) {
                return oldItem.hour == newItem.hour;
            }
            @Override
            public boolean areContentsTheSame(
                    @NonNull CalendarViewModel.TimeSlot oldItem, @NonNull CalendarViewModel.TimeSlot newItem) {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldItem.equals(newItem);
            }
        };

        private final AsyncListDiffer<CalendarViewModel.TimeSlot> mDiffer = new AsyncListDiffer(this, DIFF_CALLBACK);

        void computeDiffs() {
            mDiffer.submitList(slots);
        }


        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return mDiffer.getCurrentList().size();
        }

        private SlotCaregiver getSlotForHourAndRoom(int hour, int room) {
            if(mDiffer != null && mDiffer.getCurrentList().size() > 0) {
                CalendarViewModel.TimeSlot timeSlot = mDiffer.getCurrentList().get(hour);
                SlotCaregiver sc = timeSlot.roomSlots.get(room+"");
                if(sc != null) return sc;
            }
            return null;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView hour;
            public ViewGroup roomContainer;

            public ItemViewHolder(ViewGroup v) {
                super(v);
                v.setOnClickListener(this);
                roomContainer = v.findViewById(R.id.container);
                roomContainer.setOnClickListener(this);
                hour = v.findViewById(R.id.hour);
            }

            @Override
            public void onClick(View v) {
                // room tap
                if(v instanceof RoomView) {
                    int hour = ((RoomView) v).getHour();
                    String room = ((RoomView) v).getRoom();

                    if(hour != RecyclerView.NO_POSITION && slots != null && hour < slots.size()) {
                        router.navigateToSlot(getContext(), mViewModel.getDate().getValue(), hour, room);
                    }
                }
                // row tap
                else {
                    int hour = getAdapterPosition();

                    if(slots.get(hour).roomSlots.size() == Constants.ROOM_COUNT) {
                        Toast.makeText(getContext(), R.string.no_room_available, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    router.navigateToSlot(getContext(), mViewModel.getDate().getValue(), hour, null);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

            if(layoutInflater == null) layoutInflater = LayoutInflater.from(parent.getContext());
            View v = layoutInflater.inflate(R.layout.calendar_list_item, parent, false);
            CalendarFragment.SlotListAdapter.ItemViewHolder vh = new CalendarFragment.SlotListAdapter.ItemViewHolder((ViewGroup)v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((ItemViewHolder) holder).hour.setText(DateUtils.formatLocalTime(getContext(), position));

            if(asyncLayoutInflater == null) asyncLayoutInflater = new AsyncLayoutInflater(getContext());
            boolean alreadyInflated = ((ItemViewHolder) holder).roomContainer.getChildCount() > 0;

            if(!alreadyInflated) {
                for (int i = 0; i < Constants.ROOM_COUNT; i++) {
                    final int room = i + 1;
                    asyncLayoutInflater.inflate(R.layout.room_slot_item, ((ItemViewHolder) holder).roomContainer,
                            (view, resid, parent) -> {
                                view.setOnClickListener(((ItemViewHolder) holder));
                                ((ItemViewHolder) holder).roomContainer.addView(view);
                                SlotCaregiver sc = getSlotForHourAndRoom(position, room);
                                bindView((RoomView)view, sc, position, room);
                            });
                }
            }else {
                for (int i = 0; i < Constants.ROOM_COUNT; i++) {
                    final int room = i + 1;
                    SlotCaregiver sc = getSlotForHourAndRoom(position, room);
                    bindView((RoomView) ((ItemViewHolder) holder).roomContainer.getChildAt(i), sc, position, room);
                }
            }
        }

        private void bindView(RoomView roomView, SlotCaregiver sc, int hour, int room) {
            if(roomView != null) {
                if (sc != null) {
                    roomView.setVisibility(View.VISIBLE);
                    roomView.setHour(hour);
                    roomView.setRoom(room + "");

                    roomView.setCardBackgroundColor(ColorUtils.getRoomColor(roomView.getRoom()));

                    ((TextView) roomView.findViewById(R.id.name)).setText(String.format("%s %s.", sc.caregiverName, sc.caregiverSurname.substring(0, 1)));
                    ((TextView) roomView.findViewById(R.id.room)).setText(String.format("#%s", sc.roomId));
                    Glide.with(getContext())
                            .load(sc.caregiverThumbnail)
                            .apply(RequestOptions.circleCropTransform())
                            .into((ImageView) (roomView.findViewById(R.id.photo)));
                } else {
                    roomView.setVisibility(View.GONE);
                    roomView.setHour(hour);
                    roomView.setRoom(room + "");
                }
            }
        }
    }
}
