package com.reicast.emulator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ConfigureFragment extends Fragment {

	Activity parentActivity;
	TextView mainFrames;
	OnClickListener mCallback;
	
	boolean dynarecopt = true;
	boolean unstableopt = false;
	int dcregion = 3;
	boolean limitfps = true;
	boolean mipmaps = true;
	boolean widescreen = false;
	int frameskip = 0;
	boolean pvrrender = false;

	private SharedPreferences mPrefs;
	private File sdcard = Environment.getExternalStorageDirectory();
	private String home_directory = sdcard + "/dc";

	// Container Activity must implement this interface
	public interface OnClickListener {
		public void onMainBrowseSelected(String path_entry, boolean games);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.configure_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// setContentView(R.layout.activity_main);

		parentActivity = getActivity();

		mPrefs = PreferenceManager.getDefaultSharedPreferences(parentActivity);
		home_directory = mPrefs.getString("home_directory", home_directory);

		try {
			File config = new File(home_directory, "emu.cfg");
			if (config.exists()) {
				Scanner scanner = new Scanner(config);
				String currentLine;
				while (scanner.hasNextLine()) {
					currentLine = scanner.nextLine();
					
					// Check if the existing emu.cfg has the setting and get current value
					
					/*if (StringUtils.containsIgnoreCase(currentLine, "Dynarec.Enabled")) {
						dynarecopt = Boolean.valueOf(currentLine.replace(
								"Dynarec.Enabled=", ""));
					}*/
					if (StringUtils.containsIgnoreCase(currentLine, "Dynarec.unstable-opt")) {
						unstableopt = Boolean.valueOf(currentLine.replace(
								"Dynarec.unstable-opt=", ""));
					}
					/*if (StringUtils.containsIgnoreCase(currentLine, "Dreamcast.Region")) {
						dcregion = Integer.valueOf(currentLine.replace(
								"Dreamcast.Region=", ""));
					}*/
					if (StringUtils.containsIgnoreCase(currentLine, "aica.LimitFPS")) {
						limitfps = Boolean.valueOf(currentLine.replace(
								"aica.LimitFPS=", ""));
					}
					if (StringUtils.containsIgnoreCase(currentLine, "rend.UseMipmaps")) {
						mipmaps = Boolean.valueOf(currentLine.replace(
								"rend.UseMipmaps=", ""));
					}
					if (StringUtils.containsIgnoreCase(currentLine,
							"rend.WideScreen")) {
						widescreen = Boolean.valueOf(currentLine.replace(
								"rend.WideScreen=", ""));
					}
					if (StringUtils.containsIgnoreCase(currentLine, "ta.skip")) {
						frameskip = Integer.valueOf(currentLine.replace(
								"ta.skip=", ""));
					}
					/*if (StringUtils.containsIgnoreCase(currentLine, "pvr.rend")) {
						pvrrender = Boolean.valueOf(currentLine.replace(
								"pvr.rend=", ""));
					}*/
					
				}
				scanner.close();
			}
		} catch (Exception e) {
			Log.d("reicast", "Exception: " + e);
		}
		
		// Generate the menu options and fill in existing settings
		
		/*OnCheckedChangeListener dynarec_options = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPrefs.edit().putBoolean("dynarec_opt", isChecked).commit();
				dynarecopt = isChecked;
				if (!executeAppendConfig("Dynarec.Enabled",
						String.valueOf(isChecked ? 1 : 0))) {
					executeWriteConfig();
				}
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			Switch dynarec_opt = (Switch) getView().findViewById(
					R.id.dynarec_option);
			boolean dynarec = mPrefs.getBoolean("dynarec_opt", dynarecopt);
			if (dynarec) {
				dynarec_opt.setChecked(true);
			} else {
				dynarec_opt.setChecked(false);
			}
			dynarec_opt.setOnCheckedChangeListener(dynarec_options);
		} else {
			CheckBox dynarec_opt = (CheckBox) getView().findViewById(
					R.id.dynarec_option);
			boolean dynarec = mPrefs.getBoolean("dynarec_opt", dynarecopt);
			if (dynarec) {
				dynarec_opt.setChecked(true);
			} else {
				dynarec_opt.setChecked(false);
			}
			dynarec_opt.setOnCheckedChangeListener(dynarec_options);
		}*/
		
		OnCheckedChangeListener unstable_option = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPrefs.edit().putBoolean("unstable_opt", isChecked).commit();
				unstableopt = isChecked;
				if (!executeAppendConfig("Dynarec.unstable-opt",
						String.valueOf(isChecked ? 1 : 0))) {
					executeWriteConfig();
				}
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			Switch unstable_opt = (Switch) getView().findViewById(
					R.id.unstable_option);
			boolean unstable = mPrefs.getBoolean("unstable_opt", unstableopt);
			if (unstable) {
				unstable_opt.setChecked(true);
			} else {
				unstable_opt.setChecked(false);
			}
			unstable_opt.setOnCheckedChangeListener(unstable_option);
		} else {
			CheckBox unstable_opt = (CheckBox) getView().findViewById(
					R.id.unstable_option);
			boolean unstable = mPrefs.getBoolean("unstable_opt", unstableopt);
			if (unstable) {
				unstable_opt.setChecked(true);
			} else {
				unstable_opt.setChecked(false);
			}
			unstable_opt.setOnCheckedChangeListener(unstable_option);
		}
		
		/*String[] regions = parentActivity.getResources().getStringArray(R.array.region);

		Spinner region_spnr = (Spinner) getView().findViewById(
				R.id.region_spinner);
		ArrayAdapter<String> localeAdapter = new ArrayAdapter<String>(
				parentActivity, android.R.layout.simple_spinner_item, regions);
		localeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		region_spnr.setAdapter(localeAdapter);
		
		int dc_region = mPrefs.getInt("dc_region", dcregion);
		region_spnr.setSelection(dc_region, true);

		region_spnr.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				mPrefs.edit().putInt("dc_region", pos).commit();
				dcregion = pos;
				if (!executeAppendConfig("Dreamcast.Region",
						String.valueOf(pos))) {
					executeWriteConfig();
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				mPrefs.edit().putInt("dc_region", 3).commit();
			}

		});*/
		
		OnCheckedChangeListener limitfps_option = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPrefs.edit().putBoolean("limit_fps", isChecked).commit();
				limitfps = isChecked;
				if (!executeAppendConfig("aica.LimitFPS",
						String.valueOf(isChecked ? 1 : 0))) {
					executeWriteConfig();
				}
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			Switch limit_fps = (Switch) getView().findViewById(
					R.id.limitfps_option);
			boolean limited = mPrefs.getBoolean("limit_fps", limitfps);
			if (limited) {
				limit_fps.setChecked(true);
			} else {
				limit_fps.setChecked(false);
			}
			limit_fps.setOnCheckedChangeListener(limitfps_option);
		} else {
			CheckBox limit_fps = (CheckBox) getView().findViewById(
					R.id.limitfps_option);
			boolean limited = mPrefs.getBoolean("limit_fps", limitfps);
			if (limited) {
				limit_fps.setChecked(true);
			} else {
				limit_fps.setChecked(false);
			}
			limit_fps.setOnCheckedChangeListener(limitfps_option);
		}
		
		OnCheckedChangeListener mipmaps_option = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPrefs.edit().putBoolean("use_mipmaps", isChecked).commit();
				mipmaps = isChecked;
				if (!executeAppendConfig("rend.UseMipmaps",
						String.valueOf(isChecked ? 1 : 0))) {
					executeWriteConfig();
				}
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			Switch mipmap_opt = (Switch) getView().findViewById(
					R.id.mipmaps_option);
			boolean mipmapped = mPrefs.getBoolean("use_mipmaps", mipmaps);
			if (mipmapped) {
				mipmap_opt.setChecked(true);
			} else {
				mipmap_opt.setChecked(false);
			}
			mipmap_opt.setOnCheckedChangeListener(mipmaps_option);
		} else {
			CheckBox mipmap_opt = (CheckBox) getView().findViewById(
					R.id.mipmaps_option);
			boolean mipmapped = mPrefs.getBoolean("use_mipmaps", mipmaps);
			if (mipmapped) {
				mipmap_opt.setChecked(true);
			} else {
				mipmap_opt.setChecked(false);
			}
			mipmap_opt.setOnCheckedChangeListener(mipmaps_option);
		}

		OnCheckedChangeListener full_screen = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPrefs.edit().putBoolean("stretch_view", isChecked).commit();
				widescreen = isChecked;
				if (!executeAppendConfig("rend.WideScreen",
						String.valueOf(isChecked ? 1 : 0))) {
					executeWriteConfig();
				}
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			Switch stretch_view = (Switch) getView().findViewById(
					R.id.stretch_option);
			boolean stretched = mPrefs.getBoolean("stretch_view", widescreen);
			if (stretched) {
				stretch_view.setChecked(true);
			} else {
				stretch_view.setChecked(false);
			}
			stretch_view.setOnCheckedChangeListener(full_screen);
		} else {
			CheckBox stretch_view = (CheckBox) getView().findViewById(
					R.id.stretch_option);
			boolean stretched = mPrefs.getBoolean("stretch_view", widescreen);
			if (stretched) {
				stretch_view.setChecked(true);
			} else {
				stretch_view.setChecked(false);
			}
			stretch_view.setOnCheckedChangeListener(full_screen);
		}
		
		mainFrames = (TextView) getView().findViewById(R.id.current_frames);
		mainFrames.setText(String.valueOf(frameskip));

		SeekBar frameSeek = (SeekBar) getView()
				.findViewById(R.id.frame_seekbar);

		int userFrames = mPrefs.getInt("frame_skip", frameskip);
		frameSeek.setProgress(userFrames);

		frameSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progressChanged = progress;
				mainFrames.setText(String.valueOf(progress));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				mPrefs.edit().putInt("frame_skip", progressChanged).commit();
				frameskip = progressChanged;
				if (!executeAppendConfig("ta.skip",
						String.valueOf(progressChanged))) {
					executeWriteConfig();
				}
			}
		});

		/*OnCheckedChangeListener pvr_rendering = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mPrefs.edit().putBoolean("pvr_render", isChecked).commit();
				pvrrender = isChecked;
				if (!executeAppendConfig("pvr.rend",
						String.valueOf(isChecked ? 1 : 0))) {
					executeWriteConfig();
				}
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			Switch pvr_render = (Switch) getView().findViewById(
					R.id.render_option);
			boolean rendered = mPrefs.getBoolean("pvr_render", pvrrender);
			if (rendered) {
				pvr_render.setChecked(true);
			} else {
				pvr_render.setChecked(false);
			}
			pvr_render.setOnCheckedChangeListener(pvr_rendering);
		} else {
			CheckBox pvr_render = (CheckBox) getView().findViewById(
					R.id.render_option);
			boolean rendered = mPrefs.getBoolean("pvr_render", pvrrender);
			if (rendered) {
				pvr_render.setChecked(true);
			} else {
				pvr_render.setChecked(false);
			}
			pvr_render.setOnCheckedChangeListener(pvr_rendering);
		}*/
	}

	private boolean executeAppendConfig(String identifier, String value) {
		File config = new File(home_directory, "emu.cfg");
		if (config.exists()) {
			try {
				
				// Read existing emu.cfg and substitute new setting value
				
				StringBuilder rebuildFile = new StringBuilder();
				Scanner scanner = new Scanner(config);
				String currentLine;
				while (scanner.hasNextLine()) {
					currentLine = scanner.nextLine();
					if (StringUtils.containsIgnoreCase(currentLine, identifier)) {
						rebuildFile.append(identifier + "=" + value + "\n");
					} else {
						rebuildFile.append(currentLine + "\n");
					}
				}
				scanner.close();
				config.delete();
				FileOutputStream fos = new FileOutputStream(config);
				fos.write(rebuildFile.toString().getBytes());
				fos.close();
				return true;
			} catch (Exception e) {
				Log.d("reicast", "Exception: " + e);
			}
		}
		return false;
	}

	private void executeWriteConfig() {
		try {
			File config = new File(home_directory, "emu.cfg");
			if (config.exists()) {
				config.delete();
			}
			
			// Write new emu.cfg using current display values
			
			StringBuilder rebuildFile = new StringBuilder();
			rebuildFile.append("[config]" + "\n");
			rebuildFile.append("Dynarec.Enabled=" + String.valueOf(dynarecopt ? 1 : 0) + "\n");
			rebuildFile.append("Dynarec.idleskip=1" + "\n");
			rebuildFile.append("Dynarec.unstable-opt=" + String.valueOf(unstableopt ? 1 : 0) + "\n");
			rebuildFile.append("Dreamcast.Cable=3" + "\n");
			rebuildFile.append("Dreamcast.RTC=2018927206" + "\n");
			rebuildFile.append("Dreamcast.Region=" + String.valueOf(dcregion) + "\n");
			rebuildFile.append("Dreamcast.Broadcast=4" + "\n");
			rebuildFile.append("aica.LimitFPS=" + String.valueOf(limitfps ? 1 : 0) + "\n");
			rebuildFile.append("aica.NoBatch=0" + "\n");
			rebuildFile.append("rend.UseMipmaps=" + String.valueOf(mipmaps ? 1 : 0) + "\n");
			rebuildFile.append("rend.WideScreen="
					+ String.valueOf(widescreen ? 1 : 0) + "\n");
			rebuildFile.append("pvr.Subdivide=0" + "\n");
			rebuildFile.append("ta.skip=" + String.valueOf(frameskip) + "\n");
			rebuildFile.append("pvr.rend=" + String.valueOf(pvrrender ? 1 : 0) + "\n");
			rebuildFile.append("image=null" + "\n");
			FileOutputStream fos = new FileOutputStream(config);
			fos.write(rebuildFile.toString().getBytes());
			fos.close();
		} catch (Exception e) {
			Log.d("reicast", "Exception: " + e);
		}
	}
}
