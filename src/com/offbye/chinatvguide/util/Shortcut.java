package com.offbye.chinatvguide.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.offbye.chinatvguide.R;

public class Shortcut {
	private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

	/**
	 * add a shortcut to home screen.
	 *
	 * @param context
	 * @param mainActivity
	 *            start with ., .Home for example.
	 */
	public static void addShortCut(Context context, String mainActivity) {
		Intent shortcutIntent = new Intent(ACTION_INSTALL_SHORTCUT);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context
				.getString(R.string.app_name));

		Intent intent = new Intent();
		intent.setComponent(new ComponentName(context.getPackageName(),
				mainActivity));

		// for when uninstall delete the shortcut.
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		intent.addFlags(0x10200000);

		// not duplicate create shortcut.
		shortcutIntent.putExtra("duplicate", false);

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context,
						R.drawable.icon));
		context.sendBroadcast(shortcutIntent);
	}

	public static void exit(Context context) {
		if (10 < Integer.valueOf(Build.VERSION.SDK)) {
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			manager.restartPackage(context.getPackageName());
		} else {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			am.killBackgroundProcesses(context.getPackageName());
		}
	}

}
