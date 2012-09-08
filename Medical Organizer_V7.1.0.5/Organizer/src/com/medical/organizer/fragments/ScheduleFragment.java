package com.medical.organizer.fragments;

import com.medical.organizer.R;
import com.medical.organizer.utilities.Helper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScheduleFragment extends Fragment {
	Helper help = new Helper(getActivity());
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.schedule_fragment, container, false);

		return v;
	}
}
