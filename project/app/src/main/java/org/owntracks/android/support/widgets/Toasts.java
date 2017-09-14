package org.owntracks.android.support.widgets;

import android.widget.Toast;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.services.ServiceMessage;

public class Toasts {
    public static void showCurrentLocationNotAvailable(){
    }


    public static void showLocationPermissionNotAvailable(){
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.locationPermissionNotAvailable), Toast.LENGTH_SHORT).show();
    }

    public static void showUnableToCopyCertificateToast() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.unableToCopyCertificate), Toast.LENGTH_SHORT).show();

    }

    public static void showCopyCertificateSuccessToast() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.successCopyCertificate), Toast.LENGTH_SHORT).show();

    }

    public static void showEndTimeBeforeStart() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.endBeforeStart), Toast.LENGTH_SHORT).show();
    }

    public static void showStartTimeBeforeStart() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.startBeforeStart), Toast.LENGTH_SHORT).show();
    }

    public static void showEndTimeAfterEnd() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.endAfterEnd), Toast.LENGTH_SHORT).show();
    }

    public static void showStartTimeAfterEnd() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.startAfterEnd), Toast.LENGTH_SHORT).show();
    }

    private static Toast stateChangeToast;
    public static void showEndpointStateChange(ServiceMessage.EndpointState state) {
        if(stateChangeToast != null)
            stateChangeToast.cancel();

        String label = state.getLabel(App.getContext());
        if (label != null && label != "") {
            stateChangeToast = Toast.makeText(App.getContext(), label, Toast.LENGTH_SHORT);
            stateChangeToast.show();
        }
    }

    public static void showWaypointRemovedToast() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.waypointRemoved), Toast.LENGTH_SHORT).show();
    }

    public static void showContactLocationNotAvailable() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.contactLocationUnknown), Toast.LENGTH_SHORT).show();
    }

    public static void showMessageSentAndRemaining(int nr) {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.messageSent) + " " + Integer.toString(nr), Toast.LENGTH_SHORT).show();
    }

    public static void showMessageQueued(int nr) {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.messageQueued) + ": " + Integer.toString(nr), Toast.LENGTH_SHORT).show();
    }

    public static void showEndpointNotConfigured() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.connectivityDisconnectedConfigIncomplete), Toast.LENGTH_SHORT).show();
    }

    public static void showTodayAlreadyAdded() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.todayAlreadyAdded), Toast.LENGTH_SHORT).show();
    }

    public static void showResumeDay() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.resumeDay), Toast.LENGTH_SHORT).show();
    }

    public static void showStartDay() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.startDay), Toast.LENGTH_SHORT).show();
    }

    public static void showInterventionsSent() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.interventionsSent), Toast.LENGTH_SHORT).show();
    }
}
