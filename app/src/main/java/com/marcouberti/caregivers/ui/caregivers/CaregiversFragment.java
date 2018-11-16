package com.marcouberti.caregivers.ui.caregivers;

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
import com.marcouberti.caregivers.db.entity.CaregiverEntity;
import com.marcouberti.caregivers.model.Caregiver;
import com.marcouberti.caregivers.ui.caregiver.CaregiverActivity;
import com.marcouberti.caregivers.ui.caregiver.CaregiverViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaregiversFragment extends Fragment {

    private CaregiversViewModel mViewModel;
    private CaregiversRouter router = new CaregiversRouter();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<CaregiverEntity> caregivers = new ArrayList<>();

    public static CaregiversFragment newInstance() {
        return new CaregiversFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.caregivers_fragment, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_prev_arrow);
        toolbar.setNavigationOnClickListener(view -> {
            if(getActivity()!=null && isAdded()) getActivity().finish();
        });

        mRecyclerView = rootView.findViewById(R.id.caregiver_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CaregiversListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        CaregiversViewModel.Filter filter = new CaregiversViewModel.Filter();
        if(args != null){
            filter.date = new Date(args.getLong("DATE_TS"));
            filter.hour = args.getInt("HOUR");
        }
        CaregiversViewModel.Factory factory = new CaregiversViewModel.Factory(
                getActivity().getApplication(), filter);

        mViewModel = ViewModelProviders.of(this, factory)
                .get(CaregiversViewModel.class);

        mViewModel.getCaregivers().observe(this, caregivers -> {
            // update UI
            this.caregivers = caregivers;
            if(caregivers != null) this.mAdapter.notifyItemRangeChanged(0, caregivers.size());
        });

        mViewModel.getToastEvent().observe(this, message -> 
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());

        mViewModel.setIsPicking(filter.date != null);
    }

    public class CaregiversListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public TextView name;
            public TextView surname;
            public ImageView photo;

            public ItemViewHolder(ViewGroup v) {
                super(v);
                v.setOnClickListener(this);
                name = v.findViewById(R.id.name);
                surname = v.findViewById(R.id.surname);
                photo = v.findViewById(R.id.photo);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && caregivers != null && position < caregivers.size()) {

                    if(mViewModel.getIsPicking().getValue()) {
                        Caregiver c = caregivers.get(position);
                        router.navigateBackWithSelection(getActivity(), c);
                    }else {
                        Caregiver c = caregivers.get(position);
                        router.navigateToDetail(getActivity(), c);
                    }
                }
            }
        }

        private void onScrollingToBottom() {
            mViewModel.loadMoreCaregivers();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.caregivers_list_item, parent, false);

            CaregiversListAdapter.ItemViewHolder vh = new CaregiversListAdapter.ItemViewHolder((ViewGroup)v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Caregiver caregiver = caregivers.get(position);
            CaregiversListAdapter.ItemViewHolder vh = ((CaregiversListAdapter.ItemViewHolder) holder);
            vh.name.setText(caregiver.getName());
            vh.surname.setText(caregiver.getSurname());
            Glide.with(getContext())
                    .load(caregiver.getPhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(vh.photo);

            if(position >= caregivers.size() - 3) onScrollingToBottom();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            if(caregivers != null) return caregivers.size();
            else return 0;
        }
    }
}
