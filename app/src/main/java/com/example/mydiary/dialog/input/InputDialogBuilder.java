package com.example.mydiary.dialog.input;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class InputDialogBuilder {
    static private final CallbackListener EMPTY_LISTENER = object -> {};
    static private final String DEFAULT_POSITIVE_TEXT = "OK";
    static private final String DEFAULT_NEGATIVE_TEXT = "Cancel";
    static private final String DEFAULT_NEUTRAL_TEXT = "Clear";

    private final Activity activity;
    private final InputDialog inputDialog;

    private CallbackListener positiveListener, negativeListener, neutralListener;
    private String title, positiveText, negativeText, neutralText;

    public InputDialogBuilder(Activity activity, InputDialog inputDialog){
        this.activity = activity;
        this.inputDialog = inputDialog;
        reset();
    }

    public InputDialogBuilder reset(){
        this.positiveListener = null;
        this.negativeListener = null;
        this.neutralListener = null;
        this.title = null;
        return this;
    }

    public InputDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public InputDialogBuilder setPositiveButton(@NonNull CallbackListener listener)  {
        return setPositiveButton(listener, DEFAULT_POSITIVE_TEXT);
    }

    public InputDialogBuilder setPositiveButton(@NonNull CallbackListener listener, @NonNull String text) {
        this.positiveText = text;
        this.positiveListener = listener;
        return this;
    }

    public InputDialogBuilder setNegativeButton(@Nullable CallbackListener listener)  {
        return setNegativeButton(listener, DEFAULT_NEGATIVE_TEXT);
    }

    public InputDialogBuilder setNegativeButton(@Nullable CallbackListener listener, @NonNull String text) {
        this.negativeText = text;
        if(listener != null){
            this.negativeListener = listener;
        }
        else{
            this.negativeListener = EMPTY_LISTENER;
        }
        return this;
    }

    public InputDialogBuilder seNeutralButton(@Nullable CallbackListener listener)  {
        return setNeutralButton(listener, DEFAULT_NEUTRAL_TEXT);
    }

    public InputDialogBuilder setNeutralButton(@Nullable CallbackListener listener, @NonNull String text) {
        this.neutralText = text;
        if(listener != null){
            this.neutralListener = listener;
        }
        else{
            this.neutralListener = EMPTY_LISTENER;
        }
        return this;
    }

    public AlertDialog create(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inputDialog.inflateView(inflater);

        final CallbackListener posL = positiveListener;
        final CallbackListener negL = negativeListener;
        final CallbackListener neuL = neutralListener;

        builder.setView(view);

        builder.setTitle(title);

        assert posL != null;
        builder.setPositiveButton(positiveText, (dialog, id) -> posL.callback(inputDialog.getData(view)));

        if(negL != null) {
            builder.setNegativeButton(negativeText, (dialog, id) -> negL.callback(inputDialog.getData(view)));
        }

        if(neuL != null) {
            builder.setNeutralButton(neutralText, (dialog, id) -> neuL.callback(inputDialog.getData(view)));
        }

        return builder.create();
    }

    public interface CallbackListener {
        void callback(@NonNull Object object);
    }
}
