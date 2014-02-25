package com.reicast.emulator;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.reicast.emulator.config.ConfigureFragment;
import com.reicast.emulator.config.InputFragment;
import com.reicast.emulator.config.OptionsFragment;
import com.reicast.emulator.debug.GenerateLogs;
import com.reicast.emulator.emu.JNIdc;

public class MainActivity extends SlidingFragmentActivity implements
		FileBrowser.OnItemSelectedListener, OptionsFragment.OnClickListener {

	private SharedPreferences mPrefs;
	private static File sdcard = Environment.getExternalStorageDirectory();
	public static String home_directory = sdcard + "/dc";

	private TextView menuHeading;
	
	private SlidingMenu sm;
	
	private UncaughtExceptionHandler mUEHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainuilayout_fragment);
        setBehindContentView(R.layout.drawer_menu);

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		mUEHandler = new Thread.UncaughtExceptionHandler() {
	        public void uncaughtException(Thread t, Throwable error) {
			if (error != null) {
				StringBuilder output = new StringBuilder();
				output.append("Thread:\n");
				for (StackTraceElement trace : t.getStackTrace()) {
					output.append(trace.toString() + " ");
				}
				output.append("\n\nError:\n");
				for (StackTraceElement trace : error.getStackTrace()) {
					output.append(trace.toString() + " ");
				}
				String log = output.toString();
				mPrefs.edit().putString("prior_error", log).commit();
				error.printStackTrace();
				MainActivity.this.finish();
			}
	        }
		};
		Thread.setDefaultUncaughtExceptionHandler(mUEHandler);

		home_directory = mPrefs.getString("home_directory", home_directory);

		String prior_error = mPrefs.getString("prior_error", null);
		if (prior_error != null && !prior_error.equals(null)) {
			initiateReport(prior_error, savedInstanceState);
			mPrefs.edit().remove("prior_error").commit();
		} else {
			loadInterface(savedInstanceState);
		}
	}

	/**
	 * Load the GUI interface for display to the user
	 * 
	 * @param bundle
	 *            The savedInstanceState passed from onCreate
	 */
	private void loadInterface(Bundle savedInstanceState) {
		if (!getFilesDir().exists()) {
			getFilesDir().mkdir();
		}
		JNIdc.config(home_directory);

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
				if (savedInstanceState != null) {
					return;
				}
			}

			// Create a new Fragment to be placed in the activity layout
			FileBrowser firstFragment = new FileBrowser();
			Bundle args = new Bundle();
			args.putBoolean("ImgBrowse", true);
			args.putString("browse_entry", null);
			// specify a path for selecting folder options
			args.putBoolean("games_entry", false);
			// specify if the desired path is for games or data
			firstFragment.setArguments(args);
			// In case this activity was started with special instructions from
			// an
			// Intent, pass the Intent's extras to the fragment as arguments
			// firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_container, firstFragment,
					"MAIN_BROWSER").commit();
		}

		menuHeading = (TextView) findViewById(R.id.menu_heading);

		sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setOnOpenListener(new OnOpenListener() {
			public void onOpen() {
				findViewById(R.id.browser_menu).setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						FileBrowser browseFrag = (FileBrowser) getSupportFragmentManager()
								.findFragmentByTag("MAIN_BROWSER");
						if (browseFrag != null) {
							if (browseFrag.isVisible()) {
								return;
							}
						}
						browseFrag = new FileBrowser();
						Bundle args = new Bundle();
						args.putBoolean("ImgBrowse", true);
						args.putString("browse_entry", null);
						// specify a path for selecting folder options
						args.putBoolean("games_entry", false);
						// specify if the desired path is for games or data
						browseFrag.setArguments(args);
						getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, browseFrag,
								"MAIN_BROWSER").addToBackStack(null)
								.commit();
						setTitle(getString(R.string.browser));
						sm.toggle(true);
					}

				});
				findViewById(R.id.settings_menu).setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						ConfigureFragment configFrag = (ConfigureFragment) getSupportFragmentManager()
								.findFragmentByTag("CONFIG_FRAG");
						if (configFrag != null) {
							if (configFrag.isVisible()) {
								return;
							}
						}
						configFrag = new ConfigureFragment();
						getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, configFrag,
								"CONFIG_FRAG").addToBackStack(null)
								.commit();
						setTitle(getString(R.string.settings));
						sm.toggle(true);
					}

				});

				findViewById(R.id.paths_menu).setOnClickListener(
						new OnClickListener() {
							public void onClick(View view) {
								OptionsFragment optionsFrag = (OptionsFragment) getSupportFragmentManager()
										.findFragmentByTag("OPTIONS_FRAG");
								if (optionsFrag != null) {
									if (optionsFrag.isVisible()) {
										return;
									}
								}
								optionsFrag = new OptionsFragment();
								getSupportFragmentManager()
								.beginTransaction()
								.replace(R.id.fragment_container,
										optionsFrag, "OPTIONS_FRAG")
										.addToBackStack(null).commit();
								setTitle(getString(R.string.paths));
								sm.toggle(true);
							}

						});

				findViewById(R.id.input_menu).setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						InputFragment inputFrag = (InputFragment) getSupportFragmentManager()
								.findFragmentByTag("INPUT_FRAG");
						if (inputFrag != null) {
							if (inputFrag.isVisible()) {
								return;
							}
						}
						inputFrag = new InputFragment();
						getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, inputFrag,
								"INPUT_FRAG").addToBackStack(null).commit();
						setTitle(getString(R.string.input));
						sm.toggle(true);
					}

				});

				findViewById(R.id.about_menu).setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						AboutFragment aboutFrag = (AboutFragment) getSupportFragmentManager()
								.findFragmentByTag("ABOUT_FRAG");
						if (aboutFrag != null) {
							if (aboutFrag.isVisible()) {
								return;
							}
						}
						aboutFrag = new AboutFragment();
						getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, aboutFrag,
								"ABOUT_FRAG").addToBackStack(null).commit();
						setTitle(getString(R.string.about));
						sm.toggle(true);
					}

				});

				findViewById(R.id.rateme_menu).setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
							// vib.vibrate(50);
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse("market://details?id="
											+ getPackageName())));
							//setTitle(getString(R.string.rateme));
							sm.toggle(true);
							return true;
						} else
							return false;
					}
				});
			}
		});
		findViewById(R.id.header_list).setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
					sm.toggle(true);
					return true;
				} else
					return false;
			}
		});
	}

	/**
	 * Display a dialog to notify the user of prior crash
	 * 
	 * @param string
	 *            A generalized summary of the crash cause
	 * @param bundle
	 *            The savedInstanceState passed from onCreate
	 */
	private void initiateReport(final String error, final Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(getString(R.string.report_issue));
		builder.setMessage(error);
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						loadInterface(savedInstanceState);
						dialog.dismiss();
					}
				});
		builder.setPositiveButton("Report",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						GenerateLogs mGenerateLogs = new GenerateLogs(MainActivity.this);
						mGenerateLogs.setUnhandled(error);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mGenerateLogs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
									home_directory);
						} else {
							mGenerateLogs.execute(home_directory);
						}
						loadInterface(savedInstanceState);
						dialog.dismiss();
					}
				});
		builder.create();
		builder.show();
	}

	public static boolean isBiosExisting() {
		File bios = new File(home_directory, "data/dc_boot.bin");
		return bios.exists();
	}

	public static boolean isFlashExisting() {
		File flash = new File(home_directory, "data/dc_flash.bin");
		return flash.exists();
	}

	public void onGameSelected(Uri uri) {
		String msg = null;
		if (!isBiosExisting())
			msg = getString(R.string.missing_bios, home_directory);
		else if (!isFlashExisting())
			msg = getString(R.string.missing_flash, home_directory);

		if (msg != null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set title
			alertDialogBuilder.setTitle("You have to provide the BIOS");

			// set dialog message
			alertDialogBuilder
					.setMessage(msg)
					.setCancelable(false)
					.setPositiveButton("Dismiss",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									// MainActivity.this.finish();
								}
							})
					.setNegativeButton("Options",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									FileBrowser firstFragment = new FileBrowser();
									Bundle args = new Bundle();
									// args.putBoolean("ImgBrowse", false);
									// specify ImgBrowse option. true = images,
									// false = folders only
									args.putString("browse_entry",
											sdcard.toString());
									// specify a path for selecting folder
									// options
									args.putBoolean("games_entry", false);
									// selecting a BIOS folder, so this is not
									// games

									firstFragment.setArguments(args);
									// In case this activity was started with
									// special instructions from
									// an Intent, pass the Intent's extras to
									// the fragment as arguments
									// firstFragment.setArguments(getIntent().getExtras());

									// Add the fragment to the
									// 'fragment_container' FrameLayout
									getSupportFragmentManager()
											.beginTransaction()
											.replace(R.id.fragment_container,
													firstFragment,
													"MAIN_BROWSER")
											.addToBackStack(null).commit();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		} else {
			Intent inte = new Intent(Intent.ACTION_VIEW, uri, getBaseContext(),
					GL2JNIActivity.class);
			startActivity(inte);
		}
	}

	public void onFolderSelected(Uri uri) {
		FileBrowser browserFrag = (FileBrowser) getSupportFragmentManager()
				.findFragmentByTag("MAIN_BROWSER");
		if (browserFrag != null) {
			if (browserFrag.isVisible()) {

				Log.d("reicast", "Main folder: " + uri.toString());
				// return;
			}
		}

		OptionsFragment optsFrag = new OptionsFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, optsFrag, "OPTIONS_FRAG")
				.commit();
		return;
	}

	public void onMainBrowseSelected(String path_entry, boolean games) {
		FileBrowser firstFragment = new FileBrowser();
		Bundle args = new Bundle();
		args.putBoolean("ImgBrowse", false);
		// specify ImgBrowse option. true = images, false = folders only
		args.putString("browse_entry", path_entry);
		// specify a path for selecting folder options
		args.putBoolean("games_entry", games);
		// specify if the desired path is for games or data

		firstFragment.setArguments(args);
		// In case this activity was started with special instructions from
		// an Intent, pass the Intent's extras to the fragment as arguments
		// firstFragment.setArguments(getIntent().getExtras());

		// Add the fragment to the 'fragment_container' FrameLayout
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_container, firstFragment, "MAIN_BROWSER")
				.addToBackStack(null).commit();
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		menuHeading.setText(title);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Fragment fragment = (FileBrowser) getSupportFragmentManager()
					.findFragmentByTag("MAIN_BROWSER");
			if (fragment != null && fragment.isVisible()) {
				boolean readyToQuit = true;
				if (fragment.getArguments() != null) {
					readyToQuit = fragment.getArguments().getBoolean(
							"ImgBrowse", true);
				}
				if (readyToQuit) {
					MainActivity.this.finish();
				} else {
					launchMainFragment(fragment);
				}
				return true;
			} else {
				launchMainFragment(fragment);
				return true;
			}

		}

		return super.onKeyDown(keyCode, event);
	}
	
	private void launchMainFragment(Fragment fragment) {
		fragment = new FileBrowser();
		Bundle args = new Bundle();
		args.putBoolean("ImgBrowse", true);
		args.putString("browse_entry", null);
		args.putBoolean("games_entry", false);
		fragment.setArguments(args);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.fragment_container, fragment,
				"MAIN_BROWSER").commit();
		setTitle(getString(R.string.browser));
	}

	@Override
	protected void onPause() {
		super.onPause();
		InputFragment fragment = (InputFragment) getSupportFragmentManager()
				.findFragmentByTag("INPUT_FRAG");
		if (fragment != null && fragment.isVisible()) {
			if (fragment.moga != null) {
				fragment.moga.onPause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		InputFragment fragment = (InputFragment) getSupportFragmentManager()
				.findFragmentByTag("INPUT_FRAG");
		if (fragment != null && fragment.isVisible()) {
			if (fragment.moga != null) {
				fragment.moga.onDestroy();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		InputFragment fragment = (InputFragment) getSupportFragmentManager()
				.findFragmentByTag("INPUT_FRAG");
		if (fragment != null && fragment.isVisible()) {
			if (fragment.moga != null) {
				fragment.moga.onResume();
			}
		}
	}
}
