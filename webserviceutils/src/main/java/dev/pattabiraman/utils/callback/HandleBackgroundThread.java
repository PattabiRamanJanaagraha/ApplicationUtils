package dev.pattabiraman.utils.callback;

import androidx.annotation.Nullable;

/**
 *  The `HandleBackgroundThread` interface defines three methods: `handleDoInBackground()`,
 `handlePostExecute()`, and `handleException()`.*/
public interface HandleBackgroundThread {
    public void handleDoInBackground();

    public void handlePostExecute();

    public void handleException(@Nullable String exceptionMessage);
}