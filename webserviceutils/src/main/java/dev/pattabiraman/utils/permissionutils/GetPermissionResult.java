package dev.pattabiraman.utils.permissionutils;


/**
 *  The `GetPermissionResult` interface defines two methods: `resultPermissionSuccess()` and
 `resultPermissionRevoked()`. This interface is likely used as a callback mechanism to handle the
 result of a permission request.*/
public interface GetPermissionResult {
    void resultPermissionSuccess();
    void resultPermissionRevoked();
}
