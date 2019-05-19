from rest_framework import permissions

class UserHaveCommercialRole(permissions.BasePermission):
    """
    Our custom commercial permission
    """

    def has_permission(self, request, view):
        if request.method in permissions.SAFE_METHODS:
            return True

        return request.user.userrole.role == 'comercial'

    def has_object_permission(self, request, view, obj):
        # Read permissions are allowed to any request,
        # so we'll always allow GET, HEAD or OPTIONS requests.
        if request.method in permissions.SAFE_METHODS:
            return True

        return request.user.userrole.role == 'comercial'