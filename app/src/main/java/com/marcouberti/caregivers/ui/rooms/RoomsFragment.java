package com.marcouberti.caregivers.ui.rooms;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.marcouberti.caregivers.R;
import com.marcouberti.caregivers.model.Caregiver;
import com.marcouberti.caregivers.ui.caregivers.CaregiversFragment;
import com.marcouberti.caregivers.ui.caregivers.CaregiversViewModel;
import com.marcouberti.caregivers.util.ColorUtils;
import com.marcouberti.caregivers.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomsFragment extends Fragment {

    private RoomsViewModel mViewModel;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<String> busyRooms = new ArrayList<>();

    public static RoomsFragment newInstance() {
        return new RoomsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.rooms_fragment, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_prev_arrow);
        toolbar.setNavigationOnClickListener(view -> {
            if(getActivity()!=null && isAdded()) getActivity().finish();
        });

        mRecyclerView = rootView.findViewById(R.id.room_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RoomsFragment.RoomListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        RoomsViewModel.Filter filter = new RoomsViewModel.Filter();
        if(args != null){
            filter.date = new Date(args.getLong("DATE_TS"));
            filter.hour = args.getInt("HOUR");
        }
        RoomsViewModel.Factory factory = new RoomsViewModel.Factory(
                getActivity().getApplication(), filter);

        mViewModel = ViewModelProviders.of(this, factory)
                .get(RoomsViewModel.class);

        mViewModel.getBusyRooms().observe(this, busy -> {
            this.busyRooms = busy;
            mAdapter.notifyDataSetChanged();
        });
    }

    public class RoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public TextView room;
            public TextView status;
            public ImageView color;

            public ItemViewHolder(ViewGroup v) {
                super(v);
                v.setOnClickListener(this);
                room = v.findViewById(R.id.room_name);
                status = v.findViewById(R.id.room_state);
                color = v.findViewById(R.id.room_color);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();

                if(busyRooms != null && busyRooms.contains(""+(position+1))) {
                    Toast.makeText(getContext(), R.string.room_is_busy, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(position != RecyclerView.NO_POSITION) {
                    String roomId = (position+1)+"";
                    if(getActivity() != null && isAdded()) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", roomId);
                        getActivity().setResult(Activity.RESULT_OK, returnIntent);
                        getActivity().finish();
                    }
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_list_item, parent, false);

            RoomListAdapter.ItemViewHolder vh = new RoomListAdapter.ItemViewHolder((ViewGroup)v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            String roomId = ""+(position+1);
            RoomListAdapter.ItemViewHolder vh = ((RoomListAdapter.ItemViewHolder) holder);
            vh.room.setText(getContext().getString(R.string.room)+": "+ roomId);
            if(busyRooms != null) {
                vh.status.setText(busyRooms.contains(roomId) ? getContext().getString(R.string.busy) : getContext().getString(R.string.available));
            }
            vh.color.setColorFilter(ColorUtils.getRoomColor(roomId));
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return Constants.ROOM_COUNT;
        }
    }
}
