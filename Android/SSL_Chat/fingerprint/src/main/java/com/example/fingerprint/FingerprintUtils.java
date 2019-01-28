package com.example.fingerprint;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.widget.Toast;

public class FingerprintUtils {
    private FingerprintUtils() {
    }

    public enum mSensorState {
        NOT_SUPPORTED,
        NOT_BLOCKED,
        NO_FINGERPRINTS,
        READY
    }

    public static boolean checkFingerprintCompatibility(@NonNull Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static mSensorState checkSensorState(@NonNull Context context) {
        if (checkFingerprintCompatibility(context)) {

            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(context, "mSensorState.NOT_BLOCKED", Toast.LENGTH_LONG).show();
                return mSensorState.NOT_BLOCKED;
            }

            if (!FingerprintManagerCompat.from(context).hasEnrolledFingerprints()) {
                Toast.makeText(context, "mSensorState.NO_FINGERPRINTS", Toast.LENGTH_LONG).show();
                return mSensorState.NO_FINGERPRINTS;
            }

            Toast.makeText(context, "mSensorState.READY", Toast.LENGTH_LONG).show();
            return mSensorState.READY;

        } else {
            Toast.makeText(context, "mSensorState.NOT_SUPPORTED", Toast.LENGTH_LONG).show();
            return mSensorState.NOT_SUPPORTED;
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isSensorStateAt(@NonNull mSensorState state, @NonNull Context context) {
        return checkSensorState(context) == state;
    }
}
