package dev.pattabiraman.utils.callback;

import androidx.annotation.Nullable;

public interface HandleBackgroundThread {
    public void handleDoInBackground();

    public void handlePostExecute();

    public void handleException(@Nullable String exceptionMessage);
}